package com.almacen.integradora.models.exit;

import com.almacen.integradora.models.entry.EntryDao;
import com.almacen.integradora.models.entry.EntryProduct;
import com.almacen.integradora.models.stock.Stock;
import com.almacen.integradora.models.stock.StockDao;
import com.almacen.integradora.utils.SQLConnector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Registro transaccional de salidas y asignaciones por lote.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ExitDao {
    private final EntryDao entryDao = new EntryDao();
    private final StockDao stockDao = new StockDao();
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String BASE_SELECT = """
            SELECT
                x.id_exit,
                x.change_date,
                x.invoice_number,
                x.folio_number,
                x.id_user,
                x.id_area,
                x.buyer_name,
                x.total_all_prices,
                TRIM(u.name || ' ' || u.surname || ' ' || u.lastname) AS user_name,
                a.name AS area_name,
                a.shortName AS area_short_name
            FROM exits x
            INNER JOIN users u
                ON x.id_user = u.id_user
            INNER JOIN areas a
                ON x.id_area = a.id_area
            """;

    private static final String PRODUCT_SELECT = """
            SELECT
                xp.id_exit_product,
                xp.id_exit,
                xp.id_product,
                xp.quantity,
                xp.unit_price,
                xp.total_price,
                p.code AS product_code,
                p.name AS product_name,
                p.id_metric,
                m.name AS metric_name,
                m.shortName AS metric_short_name
            FROM exit_products xp
            INNER JOIN products p
                ON xp.id_product = p.id_product
            INNER JOIN metrics m
                ON p.id_metric = m.id_metric
            """;

    private static final String ALLOCATION_SELECT = """
            SELECT
                xa.id_exit_allocation,
                xa.id_exit_product,
                xa.id_entry_product,
                xa.quantity,
                xa.unit_cost,
                xa.total_cost,
                ep.id_entry,
                ep.id_product_provider,
                pp.id_provider,
                e.folio_number AS entry_folio,
                pr.name AS provider_name
            FROM exit_allocations xa
            INNER JOIN entry_products ep
                ON xa.id_entry_product = ep.id_entry_product
            INNER JOIN entries e
                ON ep.id_entry = e.id_entry
            INNER JOIN product_providers pp
                ON ep.id_product_provider = pp.id_product_provider
            INNER JOIN providers pr
                ON pp.id_provider = pr.id_provider
            """;

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param exit valor de exit requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean create(Exit exit) {
        if (!isValidExit(exit)) {
            return false;
        }

        Connection connection = null;

        try {
            connection = SQLConnector.getConnection();
            disableParallelDml(connection);
            connection.setAutoCommit(false);

            validateArea(connection, exit.getIdArea());
            validateProducts(connection, exit.getProducts());

            for (ExitProduct product : exit.getProducts()) {
                prepareFifoAllocations(connection, product);
            }

            exit.setFolioNumber(generateUniqueFolio(connection));
            exit.setTotalAllPrices(calculateExitTotal(exit.getProducts()));

            Long idExit = insertExit(connection, exit);

            if (idExit == null) {
                connection.rollback();
                return false;
            }

            exit.setIdExit(idExit);

            for (ExitProduct product : exit.getProducts()) {
                product.setIdExit(idExit);

                Long idExitProduct = insertExitProduct(connection, product);

                if (idExitProduct == null) {
                    connection.rollback();
                    return false;
                }

                product.setIdExitProduct(idExitProduct);

                for (ExitAllocation allocation : product.getAllocations()) {
                    allocation.setIdExitProduct(idExitProduct);

                    Long idAllocation = insertAllocation(connection, allocation);

                    if (idAllocation == null) {
                        connection.rollback();
                        return false;
                    }

                    allocation.setIdExitAllocation(idAllocation);

                    if (!entryDao.decreaseRemainingQuantity(
                            connection,
                            allocation.getIdEntryProduct(),
                            allocation.getQuantity()
                    )) {
                        throw new SQLException(
                                "No fue posible descontar la existencia del lote FIFO."
                        );
                    }

                    if (!stockDao.decreaseStock(
                            connection,
                            allocation.getIdStock(),
                            allocation.getQuantity()
                    )) {
                        throw new SQLException(
                                "No fue posible descontar la existencia general del producto."
                        );
                    }
                }
            }

            connection.commit();
            return true;

        } catch (SQLException exception) {
            rollbackQuietly(connection);

            throw new RuntimeException(
                    "Error al registrar la salida.",
                    exception
            );

        } finally {
            closeConnection(connection);
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param requestedProduct valor de requestedProduct requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void prepareFifoAllocations(
            Connection connection,
            ExitProduct requestedProduct
    ) throws SQLException {
        List<EntryProduct> lots = entryDao.getAvailableLotsForProduct(
                connection,
                requestedProduct.getIdProduct()
        );

        int requestedQuantity = requestedProduct.getQuantity();
        int totalAvailable = lots.stream()
                .map(EntryProduct::getRemainingQuantity)
                .filter(quantity -> quantity != null)
                .mapToInt(Integer::intValue)
                .sum();

        if (totalAvailable < requestedQuantity) {
            throw new SQLException(
                    "Stock insuficiente para el producto "
                            + getProductDisplayName(requestedProduct)
                            + ". Disponible: " + totalAvailable
                            + ", solicitado: " + requestedQuantity + "."
            );
        }

        int pendingQuantity = requestedQuantity;
        BigDecimal exactTotal = BigDecimal.ZERO;
        List<ExitAllocation> allocations = new ArrayList<>();

        for (EntryProduct lot : lots) {
            if (pendingQuantity <= 0) {
                break;
            }

            int availableInLot = lot.getRemainingQuantity() == null
                    ? 0
                    : lot.getRemainingQuantity();

            if (availableInLot <= 0) {
                continue;
            }

            int quantityTaken = Math.min(pendingQuantity, availableInLot);

            Long idStock = lot.getIdStock();

            if (idStock == null || idStock <= 0) {
                Stock stock = stockDao.getByProductProvider(
                        connection,
                        lot.getIdProductProvider(),
                        true
                );

                if (stock == null) {
                    throw new SQLException(
                            "No existe un registro de stock para uno de los lotes."
                    );
                }

                idStock = stock.getIdStock();
            }

            BigDecimal unitCost = lot.getUnitPrice()
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal allocationTotal = unitCost
                    .multiply(BigDecimal.valueOf(quantityTaken))
                    .setScale(2, RoundingMode.HALF_UP);

            ExitAllocation allocation = new ExitAllocation();
            allocation.setIdEntryProduct(lot.getIdEntryProduct());
            allocation.setIdEntry(lot.getIdEntry());
            allocation.setIdProductProvider(lot.getIdProductProvider());
            allocation.setIdProvider(lot.getIdProvider());
            allocation.setIdStock(idStock);
            allocation.setProviderName(lot.getProviderName());
            allocation.setQuantity(quantityTaken);
            allocation.setUnitCost(unitCost);
            allocation.setTotalCost(allocationTotal);

            allocations.add(allocation);
            exactTotal = exactTotal.add(allocationTotal);
            pendingQuantity -= quantityTaken;
        }

        if (pendingQuantity > 0) {
            throw new SQLException(
                    "No fue posible completar la distribución FIFO del producto "
                            + getProductDisplayName(requestedProduct) + "."
            );
        }

        BigDecimal averageUnitPrice = exactTotal
                .divide(
                        BigDecimal.valueOf(requestedQuantity),
                        4,
                        RoundingMode.HALF_UP
                );

        requestedProduct.setAllocations(allocations);
        requestedProduct.setUnitPrice(averageUnitPrice);
        requestedProduct.setTotalPrice(
                exactTotal.setScale(2, RoundingMode.HALF_UP)
        );
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param connection conexión JDBC activa
     * @param exit valor de exit requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private Long insertExit(
            Connection connection,
            Exit exit
    ) throws SQLException {
        String sql = """
                INSERT INTO exits (
                    change_date,
                    invoice_number,
                    folio_number,
                    id_user,
                    id_area,
                    buyer_name,
                    total_all_prices
                )
                VALUES (
                    CURRENT_TIMESTAMP,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                new String[]{"ID_EXIT"}
        )) {
            statement.setString(1, exit.getInvoiceNumber());
            statement.setString(2, exit.getFolioNumber());
            statement.setLong(3, exit.getIdUser());
            statement.setLong(4, exit.getIdArea());
            statement.setString(5, exit.getBuyerName());
            statement.setBigDecimal(6, exit.getTotalAllPrices());

            if (statement.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                return generatedKeys.next()
                        ? generatedKeys.getLong(1)
                        : null;
            }
        }
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param connection conexión JDBC activa
     * @param product valor de product requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private Long insertExitProduct(
            Connection connection,
            ExitProduct product
    ) throws SQLException {
        String sql = """
                INSERT INTO exit_products (
                    id_exit,
                    id_product,
                    quantity,
                    unit_price,
                    total_price
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                new String[]{"ID_EXIT_PRODUCT"}
        )) {
            statement.setLong(1, product.getIdExit());
            statement.setLong(2, product.getIdProduct());
            statement.setInt(3, product.getQuantity());
            statement.setBigDecimal(4, product.getUnitPrice());
            statement.setBigDecimal(5, product.getTotalPrice());

            if (statement.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                return generatedKeys.next()
                        ? generatedKeys.getLong(1)
                        : null;
            }
        }
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param connection conexión JDBC activa
     * @param allocation valor de allocation requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private Long insertAllocation(
            Connection connection,
            ExitAllocation allocation
    ) throws SQLException {
        String sql = """
                INSERT INTO exit_allocations (
                    id_exit_product,
                    id_entry_product,
                    quantity,
                    unit_cost
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                new String[]{"ID_EXIT_ALLOCATION"}
        )) {
            statement.setLong(1, allocation.getIdExitProduct());
            statement.setLong(2, allocation.getIdEntryProduct());
            statement.setInt(3, allocation.getQuantity());
            statement.setBigDecimal(4, allocation.getUnitCost());

            if (statement.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                return generatedKeys.next()
                        ? generatedKeys.getLong(1)
                        : null;
            }
        }
    }

    /**
     * Obtiene todos los registros disponibles para esta consulta.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Exit> getAll() {
        String sql = BASE_SELECT + """
                ORDER BY
                    x.change_date DESC,
                    x.id_exit DESC
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<Exit> exits = new ArrayList<>();
            Map<Long, Exit> exitsById = new LinkedHashMap<>();

            while (resultSet.next()) {
                Exit exit = mapExit(resultSet);
                exits.add(exit);
                exitsById.put(exit.getIdExit(), exit);
            }

            loadProductsForExits(connection, exitsById);
            return exits;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar las salidas.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idExit identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Exit getById(Long idExit) {
        if (idExit == null || idExit <= 0) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE x.id_exit = ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idExit);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Exit exit = mapExit(resultSet);
                exit.setProducts(getProductsByExit(connection, idExit));
                return exit;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar la salida.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idExit identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<ExitProduct> getProductsByExit(Long idExit) {
        if (idExit == null || idExit <= 0) {
            return new ArrayList<>();
        }

        try (Connection connection = SQLConnector.getConnection()) {
            return getProductsByExit(connection, idExit);

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los productos de la salida.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idExit identificador del registro relacionado con la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ExitProduct> getProductsByExit(
            Connection connection,
            Long idExit
    ) throws SQLException {
        String sql = PRODUCT_SELECT + """
                WHERE xp.id_exit = ?
                ORDER BY UPPER(p.name), xp.id_exit_product
                """;

        List<ExitProduct> products = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idExit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ExitProduct product = mapExitProduct(resultSet);
                    product.setAllocations(
                            getAllocationsByExitProduct(
                                    connection,
                                    product.getIdExitProduct()
                            )
                    );
                    products.add(product);
                }
            }
        }

        return products;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param exitsById valor de exitsById requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void loadProductsForExits(
            Connection connection,
            Map<Long, Exit> exitsById
    ) throws SQLException {
        if (exitsById == null || exitsById.isEmpty()) {
            return;
        }

        String sql = PRODUCT_SELECT + """
                ORDER BY xp.id_exit, UPPER(p.name), xp.id_exit_product
                """;

        Map<Long, ExitProduct> productsById = new LinkedHashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ExitProduct product = mapExitProduct(resultSet);
                Exit exit = exitsById.get(product.getIdExit());

                if (exit != null) {
                    exit.addProduct(product);
                    productsById.put(product.getIdExitProduct(), product);
                }
            }
        }

        loadAllocationsForProducts(connection, productsById);
    }

    /**
     * Obtiene todos los registros disponibles para esta consulta.
     *
     * @param connection conexión JDBC activa
     * @param idExitProduct identificador del registro relacionado con la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ExitAllocation> getAllocationsByExitProduct(
            Connection connection,
            Long idExitProduct
    ) throws SQLException {
        String sql = ALLOCATION_SELECT + """
                WHERE xa.id_exit_product = ?
                ORDER BY e.change_date, xa.id_exit_allocation
                """;

        List<ExitAllocation> allocations = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idExitProduct);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    allocations.add(mapAllocation(resultSet));
                }
            }
        }

        return allocations;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param productsById valor de productsById requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void loadAllocationsForProducts(
            Connection connection,
            Map<Long, ExitProduct> productsById
    ) throws SQLException {
        if (productsById == null || productsById.isEmpty()) {
            return;
        }

        String sql = ALLOCATION_SELECT + """
                ORDER BY xa.id_exit_product, e.change_date, xa.id_exit_allocation
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long idExitProduct = resultSet.getLong("id_exit_product");
                ExitProduct product = productsById.get(idExitProduct);

                if (product != null) {
                    product.addAllocation(mapAllocation(resultSet));
                }
            }
        }
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param connection conexión JDBC activa
     * @param idArea identificador del registro relacionado con la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void validateArea(
            Connection connection,
            Long idArea
    ) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total
                FROM areas
                WHERE id_area = ?
                  AND status = 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idArea);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()
                        || resultSet.getInt("total") == 0) {
                    throw new SQLException(
                            "El área seleccionada no existe o está inactiva."
                    );
                }
            }
        }
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param connection conexión JDBC activa
     * @param products valor de products requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void validateProducts(
            Connection connection,
            List<ExitProduct> products
    ) throws SQLException {

        String sql = """
            SELECT
                code,
                name
            FROM products
            WHERE id_product = ?
            """;

        Set<Long> uniqueProducts =
                new HashSet<>();

        for (ExitProduct product : products) {
            if (product == null
                    || product.getIdProduct() == null
                    || product.getIdProduct() <= 0) {

                throw new SQLException(
                        "Se recibió un producto no válido."
                );
            }

            if (!uniqueProducts.add(
                    product.getIdProduct()
            )) {
                throw new SQLException(
                        "No puedes agregar dos veces el mismo producto."
                );
            }

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {
                statement.setLong(
                        1,
                        product.getIdProduct()
                );

                try (
                        ResultSet resultSet =
                                statement.executeQuery()
                ) {
                    if (!resultSet.next()) {
                        throw new SQLException(
                                "Uno de los productos seleccionados no existe."
                        );
                    }

                    product.setProductCode(
                            resultSet.getString(
                                    "code"
                            )
                    );

                    product.setProductName(
                            resultSet.getString(
                                    "name"
                            )
                    );
                }
            }
        }
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param exit valor de exit requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private boolean isValidExit(Exit exit) {
        if (exit == null
                || exit.getIdUser() == null
                || exit.getIdUser() <= 0
                || exit.getIdArea() == null
                || exit.getIdArea() <= 0
                || exit.getInvoiceNumber() == null
                || exit.getInvoiceNumber().isBlank()
                || exit.getBuyerName() == null
                || exit.getBuyerName().isBlank()
                || exit.getProducts() == null
                || exit.getProducts().isEmpty()) {
            return false;
        }

        Set<Long> productIds = new HashSet<>();

        for (ExitProduct product : exit.getProducts()) {
            if (product == null
                    || product.getIdProduct() == null
                    || product.getIdProduct() <= 0
                    || product.getQuantity() == null
                    || product.getQuantity() <= 0
                    || !productIds.add(product.getIdProduct())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param products valor de products requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private BigDecimal calculateExitTotal(
            List<ExitProduct> products
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (ExitProduct product : products) {
            total = total.add(
                    product.getTotalPrice() == null
                            ? BigDecimal.ZERO
                            : product.getTotalPrice()
            );
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String generateUniqueFolio(
            Connection connection
    ) throws SQLException {
        for (int attempt = 0; attempt < 100; attempt++) {
            String folio = "S"
                    + Year.now().getValue()
                    + String.format("%04d", RANDOM.nextInt(10_000));

            if (!folioExists(connection, folio)) {
                return folio;
            }
        }

        throw new SQLException(
                "No fue posible generar un folio único para la salida."
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param folio valor de folio requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private boolean folioExists(
            Connection connection,
            String folio
    ) throws SQLException {
        String sql = """
                SELECT 1
                FROM exits
                WHERE folio_number = ?
                FETCH FIRST 1 ROWS ONLY
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, folio);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private Exit mapExit(ResultSet resultSet)
            throws SQLException {
        Exit exit = new Exit();

        exit.setIdExit(resultSet.getLong("id_exit"));

        Timestamp timestamp = resultSet.getTimestamp("change_date");

        exit.setChangeDate(
                timestamp == null
                        ? null
                        : timestamp.toLocalDateTime()
        );

        exit.setInvoiceNumber(resultSet.getString("invoice_number"));
        exit.setFolioNumber(resultSet.getString("folio_number"));
        exit.setIdUser(resultSet.getLong("id_user"));
        exit.setUserName(resultSet.getString("user_name"));
        exit.setIdArea(resultSet.getLong("id_area"));
        exit.setAreaName(resultSet.getString("area_name"));
        exit.setAreaShortName(resultSet.getString("area_short_name"));
        exit.setBuyerName(resultSet.getString("buyer_name"));
        exit.setTotalAllPrices(
                resultSet.getBigDecimal("total_all_prices")
        );

        return exit;
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private ExitProduct mapExitProduct(
            ResultSet resultSet
    ) throws SQLException {
        ExitProduct product = new ExitProduct();

        product.setIdExitProduct(
                resultSet.getLong("id_exit_product")
        );

        product.setIdExit(resultSet.getLong("id_exit"));
        product.setIdProduct(resultSet.getLong("id_product"));
        product.setProductCode(resultSet.getString("product_code"));
        product.setProductName(resultSet.getString("product_name"));
        product.setIdMetric(resultSet.getLong("id_metric"));
        product.setMetricName(resultSet.getString("metric_name"));
        product.setMetricShortName(
                resultSet.getString("metric_short_name")
        );
        product.setQuantity(resultSet.getInt("quantity"));
        product.setUnitPrice(resultSet.getBigDecimal("unit_price"));
        product.setTotalPrice(resultSet.getBigDecimal("total_price"));

        return product;
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private ExitAllocation mapAllocation(
            ResultSet resultSet
    ) throws SQLException {
        ExitAllocation allocation = new ExitAllocation();

        allocation.setIdExitAllocation(
                resultSet.getLong("id_exit_allocation")
        );
        allocation.setIdExitProduct(
                resultSet.getLong("id_exit_product")
        );
        allocation.setIdEntryProduct(
                resultSet.getLong("id_entry_product")
        );
        allocation.setIdEntry(resultSet.getLong("id_entry"));
        allocation.setIdProductProvider(
                resultSet.getLong("id_product_provider")
        );
        allocation.setIdProvider(resultSet.getLong("id_provider"));
        allocation.setEntryFolio(resultSet.getString("entry_folio"));
        allocation.setProviderName(resultSet.getString("provider_name"));
        allocation.setQuantity(resultSet.getInt("quantity"));
        allocation.setUnitCost(resultSet.getBigDecimal("unit_cost"));
        allocation.setTotalCost(resultSet.getBigDecimal("total_cost"));

        return allocation;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param product valor de product requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String getProductDisplayName(ExitProduct product) {
        if (product.getProductName() != null
                && !product.getProductName().isBlank()) {
            return product.getProductName();
        }

        return "con ID " + product.getIdProduct();
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void disableParallelDml(
            Connection connection
    ) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "ALTER SESSION DISABLE PARALLEL DML"
            );
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
        }

        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }
}
