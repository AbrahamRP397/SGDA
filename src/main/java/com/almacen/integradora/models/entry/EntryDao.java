package com.almacen.integradora.models.entry;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Registro transaccional de entradas y partidas de inventario.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class EntryDao {

    private final StockDao stockDao = new StockDao();
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String BASE_SELECT = """
            SELECT
                e.id_entry,
                e.change_date,
                e.invoice_number,
                e.folio_number,
                e.id_user,
                e.id_provider,
                e.total_all_prices,
                TRIM(
                    u.name || ' ' ||
                    u.surname || ' ' ||
                    u.lastname
                ) AS user_name,
                pr.name AS provider_name,
                pr.rfc AS provider_rfc
            FROM entries e
            INNER JOIN users u
                ON e.id_user = u.id_user
            INNER JOIN providers pr
                ON e.id_provider = pr.id_provider
            """;

    private static final String DETAIL_SELECT = """
            SELECT
                ep.id_entry_product,
                ep.id_entry,
                ep.id_product_provider,
                s.id_stock,
                ep.quantity,
                ep.remaining_quantity,
                ep.unit_price,
                ep.total_price,
                pp.id_product,
                pp.id_provider,
                p.code AS product_code,
                p.name AS product_name,
                p.id_metric,
                m.name AS metric_name,
                m.shortName AS metric_short_name,
                pr.name AS provider_name
            FROM entry_products ep
            INNER JOIN product_providers pp
                ON ep.id_product_provider = pp.id_product_provider
            INNER JOIN stock s
                ON pp.id_product_provider = s.id_product_provider
            INNER JOIN products p
                ON pp.id_product = p.id_product
            INNER JOIN metrics m
                ON p.id_metric = m.id_metric
            INNER JOIN providers pr
                ON pp.id_provider = pr.id_provider
            """;

    /* ==========================================================
       REGISTRAR ENTRADA COMPLETA
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param entry valor de entry requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean create(Entry entry) {
        if (!isValidEntry(entry)) {
            return false;
        }

        Connection connection = null;

        try {
            connection = SQLConnector.getConnection();
            disableParallelDml(connection);
            connection.setAutoCommit(false);

            validateEntryRelations(connection, entry);

            String folio = generateUniqueFolio(connection);
            BigDecimal total = calculateEntryTotal(entry.getProducts());

            entry.setFolioNumber(folio);
            entry.setTotalAllPrices(total);

            Long idEntry = insertEntry(connection, entry);

            if (idEntry == null) {
                connection.rollback();
                return false;
            }

            entry.setIdEntry(idEntry);

            for (EntryProduct product : entry.getProducts()) {
                product.setIdEntry(idEntry);
                product.setRemainingQuantity(product.getQuantity());
                product.setTotalPrice(calculateDetailTotal(product));

                Long idEntryProduct = insertEntryProduct(
                        connection,
                        product
                );

                if (idEntryProduct == null) {
                    connection.rollback();
                    return false;
                }

                product.setIdEntryProduct(idEntryProduct);

                boolean stockUpdated = stockDao.increaseStock(
                        connection,
                        product.getIdProductProvider(),
                        product.getQuantity()
                );

                if (!stockUpdated) {
                    connection.rollback();
                    return false;
                }

                boolean priceUpdated = updateLastPurchasePrice(
                        connection,
                        product.getIdProductProvider(),
                        product.getUnitPrice()
                );

                if (!priceUpdated) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;

        } catch (SQLException exception) {
            rollbackQuietly(connection);

            throw new RuntimeException(
                    "Error al registrar la entrada.",
                    exception
            );

        } finally {
            closeConnection(connection);
        }
    }

    /* ==========================================================
       INSERTAR ENCABEZADO
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param connection conexión JDBC activa
     * @param entry valor de entry requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private Long insertEntry(
            Connection connection,
            Entry entry
    ) throws SQLException {

        String sql = """
                INSERT INTO entries (
                    change_date,
                    invoice_number,
                    folio_number,
                    id_user,
                    id_provider,
                    total_all_prices
                )
                VALUES (
                    CURRENT_TIMESTAMP,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             new String[]{"ID_ENTRY"}
                     )) {

            statement.setString(
                    1,
                    entry.getInvoiceNumber()
            );

            statement.setString(
                    2,
                    entry.getFolioNumber()
            );

            statement.setLong(
                    3,
                    entry.getIdUser()
            );

            statement.setLong(
                    4,
                    entry.getIdProvider()
            );

            statement.setBigDecimal(
                    5,
                    entry.getTotalAllPrices()
            );

            if (statement.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    return null;
                }

                return generatedKeys.getLong(1);
            }
        }
    }

    /* ==========================================================
       INSERTAR DETALLE Y LOTE FIFO
       ========================================================== */

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
    private Long insertEntryProduct(
            Connection connection,
            EntryProduct product
    ) throws SQLException {

        String sql = """
                INSERT INTO entry_products (
                    id_entry,
                    id_product_provider,
                    quantity,
                    remaining_quantity,
                    unit_price
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             new String[]{"ID_ENTRY_PRODUCT"}
                     )) {

            statement.setLong(
                    1,
                    product.getIdEntry()
            );

            statement.setLong(
                    2,
                    product.getIdProductProvider()
            );

            statement.setInt(
                    3,
                    product.getQuantity()
            );

            statement.setInt(
                    4,
                    product.getRemainingQuantity()
            );

            statement.setBigDecimal(
                    5,
                    product.getUnitPrice()
            );

            if (statement.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    return null;
                }

                return generatedKeys.getLong(1);
            }
        }
    }

    /* ==========================================================
       ACTUALIZAR ÚLTIMO PRECIO
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idProductProvider identificador del registro relacionado con la operación
     * @param purchasePrice valor de purchasePrice requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private boolean updateLastPurchasePrice(
            Connection connection,
            Long idProductProvider,
            BigDecimal purchasePrice
    ) throws SQLException {

        String sql = """
                UPDATE product_providers
                SET purchase_price = ?
                WHERE id_product_provider = ?
                  AND status = 1
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(
                    1,
                    purchasePrice
            );

            statement.setLong(
                    2,
                    idProductProvider
            );

            return statement.executeUpdate() > 0;
        }
    }

    /* ==========================================================
       VALIDAR RELACIONES

       Comprueba que todos los productos pertenezcan al proveedor
       seleccionado y que las relaciones estén activas.
       ========================================================== */

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param connection conexión JDBC activa
     * @param entry valor de entry requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void validateEntryRelations(
            Connection connection,
            Entry entry
    ) throws SQLException {

        if (connection == null) {
            throw new SQLException(
                    "No existe una conexión válida para registrar la entrada."
            );
        }

        if (entry == null
                || entry.getIdProvider() == null
                || entry.getIdProvider() <= 0) {

            throw new SQLException(
                    "El proveedor seleccionado no es válido."
            );
        }

        if (entry.getProducts() == null
                || entry.getProducts().isEmpty()) {

            throw new SQLException(
                    "La entrada no contiene productos."
            );
        }

        /*
         * ==========================================================
         * VALIDAR PROVEEDOR
         * ==========================================================
         *
         * Para una NUEVA entrada el proveedor debe:
         *
         * - existir;
         * - encontrarse activo.
         *
         * Un proveedor inactivo puede seguir apareciendo en historial
         * y conservar stock anterior, pero no puede abastecer una
         * entrada nueva.
         */
        String providerSql = """
            SELECT status
            FROM providers
            WHERE id_provider = ?
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(providerSql)
        ) {
            statement.setLong(
                    1,
                    entry.getIdProvider()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (!resultSet.next()) {
                    throw new SQLException(
                            "El proveedor seleccionado no existe."
                    );
                }

                if (resultSet.getInt("status") != 1) {
                    throw new SQLException(
                            "El proveedor seleccionado se encuentra inactivo."
                    );
                }
            }
        }

        /*
         * ==========================================================
         * VALIDAR RELACIÓN + PRODUCTO + MÉTRICA
         * ==========================================================
         *
         * Para registrar una entrada nueva deben estar operativos:
         *
         * - relación producto-proveedor;
         * - producto;
         * - unidad de medida;
         * - proveedor.
         *
         * Aunque el frontend ya filtra estos registros, esta validación
         * evita que una petición HTTP manipulada eluda las reglas.
         */
        String relationSql = """
            SELECT
                pp.id_product_provider,
                pp.id_provider,
                pp.status AS relation_status,

                p.id_product,
                p.code AS product_code,
                p.name AS product_name,
                p.status AS product_status,

                m.id_metric,
                m.name AS metric_name,
                m.shortName AS metric_short_name,
                m.status AS metric_status,

                pr.status AS provider_status

            FROM product_providers pp
            INNER JOIN products p
                ON pp.id_product = p.id_product
            INNER JOIN metrics m
                ON p.id_metric = m.id_metric
            INNER JOIN providers pr
                ON pp.id_provider = pr.id_provider
            WHERE pp.id_product_provider = ?
            """;

        for (EntryProduct product : entry.getProducts()) {

            if (product == null
                    || product.getIdProductProvider() == null
                    || product.getIdProductProvider() <= 0) {

                throw new SQLException(
                        "Se recibió una relación producto-proveedor no válida."
                );
            }

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(relationSql)
            ) {
                statement.setLong(
                        1,
                        product.getIdProductProvider()
                );

                try (
                        ResultSet resultSet =
                                statement.executeQuery()
                ) {
                    if (!resultSet.next()) {
                        throw new SQLException(
                                "Una de las relaciones producto-proveedor no existe."
                        );
                    }

                    long relationProvider =
                            resultSet.getLong(
                                    "id_provider"
                            );

                    int relationStatus =
                            resultSet.getInt(
                                    "relation_status"
                            );

                    int productStatus =
                            resultSet.getInt(
                                    "product_status"
                            );

                    int metricStatus =
                            resultSet.getInt(
                                    "metric_status"
                            );

                    int providerStatus =
                            resultSet.getInt(
                                    "provider_status"
                            );

                    String productName =
                            resultSet.getString(
                                    "product_name"
                            );

                    if (relationProvider
                            != entry.getIdProvider()) {

                        throw new SQLException(
                                "El producto "
                                        + safeDatabaseText(
                                        productName,
                                        "seleccionado"
                                )
                                        + " no pertenece al proveedor de la entrada."
                        );
                    }

                    /*
                     * La relación puede quedar inactiva en el futuro para
                     * conservar historial y stock, pero NO puede utilizarse
                     * para recibir mercancía nueva.
                     */
                    if (relationStatus != 1) {
                        throw new SQLException(
                                "La relación del producto "
                                        + safeDatabaseText(
                                        productName,
                                        "seleccionado"
                                )
                                        + " con el proveedor se encuentra inactiva."
                        );
                    }

                    /*
                     * Un producto inactivo puede sacar stock antiguo,
                     * pero no puede recibir nuevas entradas.
                     */
                    if (productStatus != 1) {
                        throw new SQLException(
                                "El producto "
                                        + safeDatabaseText(
                                        productName,
                                        "seleccionado"
                                )
                                        + " se encuentra inactivo y no puede recibir nuevas entradas."
                        );
                    }

                    /*
                     * Esta es la validación que faltaba.
                     *
                     * Si por cualquier inconsistencia existiera un producto
                     * activo asociado a una métrica inactiva, tampoco se
                     * permitirá registrar una nueva entrada.
                     */
                    if (metricStatus != 1) {
                        throw new SQLException(
                                "La unidad de medida del producto "
                                        + safeDatabaseText(
                                        productName,
                                        "seleccionado"
                                )
                                        + " se encuentra inactiva."
                        );
                    }

                    /*
                     * Ya validamos el proveedor del encabezado arriba,
                     * pero lo repetimos aquí como defensa adicional para
                     * la relación exacta seleccionada.
                     */
                    if (providerStatus != 1) {
                        throw new SQLException(
                                "El proveedor asociado al producto "
                                        + safeDatabaseText(
                                        productName,
                                        "seleccionado"
                                )
                                        + " se encuentra inactivo."
                        );
                    }
                }
            }
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @param fallback valor de fallback requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String safeDatabaseText(
            String value,
            String fallback
    ) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }

    /* ==========================================================
       CONSULTAR TODAS LAS ENTRADAS
       ========================================================== */

    /**
     * Obtiene todos los registros disponibles para esta consulta.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Entry> getAll() {
        String sql = BASE_SELECT + """
                ORDER BY
                    e.change_date DESC,
                    e.id_entry DESC
                """;

        try (Connection connection =
                     SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            List<Entry> entries = new ArrayList<>();
            Map<Long, Entry> entriesById =
                    new LinkedHashMap<>();

            while (resultSet.next()) {
                Entry entry = mapEntry(resultSet);

                entries.add(entry);
                entriesById.put(
                        entry.getIdEntry(),
                        entry
                );
            }

            loadDetailsForEntries(
                    connection,
                    entriesById
            );

            return entries;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar las entradas.",
                    exception
            );
        }
    }

    /* ==========================================================
       CONSULTAR ENTRADA POR ID
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idEntry identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Entry getById(Long idEntry) {
        if (idEntry == null || idEntry <= 0) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE e.id_entry = ?
                """;

        try (Connection connection =
                     SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idEntry);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                Entry entry = mapEntry(resultSet);

                entry.setProducts(
                        getProductsByEntry(
                                connection,
                                idEntry
                        )
                );

                return entry;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar la entrada.",
                    exception
            );
        }
    }

    /* ==========================================================
       CONSULTAR DETALLES DE UNA ENTRADA
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idEntry identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<EntryProduct> getProductsByEntry(
            Long idEntry
    ) {
        if (idEntry == null || idEntry <= 0) {
            return new ArrayList<>();
        }

        try (Connection connection =
                     SQLConnector.getConnection()) {

            return getProductsByEntry(
                    connection,
                    idEntry
            );

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los productos de la entrada.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idEntry identificador del registro relacionado con la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<EntryProduct> getProductsByEntry(
            Connection connection,
            Long idEntry
    ) throws SQLException {

        String sql = DETAIL_SELECT + """
                WHERE ep.id_entry = ?
                ORDER BY
                    UPPER(p.name),
                    ep.id_entry_product
                """;

        List<EntryProduct> products =
                new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idEntry);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(
                            mapEntryProduct(resultSet)
                    );
                }
            }
        }

        return products;
    }

    /* ==========================================================
       CARGAR DETALLES PARA VARIAS ENTRADAS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param entriesById valor de entriesById requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void loadDetailsForEntries(
            Connection connection,
            Map<Long, Entry> entriesById
    ) throws SQLException {

        if (entriesById == null
                || entriesById.isEmpty()) {
            return;
        }

        String sql = DETAIL_SELECT + """
                ORDER BY
                    ep.id_entry,
                    UPPER(p.name),
                    ep.id_entry_product
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                long idEntry =
                        resultSet.getLong("id_entry");

                Entry entry =
                        entriesById.get(idEntry);

                if (entry != null) {
                    entry.addProduct(
                            mapEntryProduct(resultSet)
                    );
                }
            }
        }
    }

    /* ==========================================================
       LOTES FIFO DISPONIBLES

       Se utilizará después para registrar salidas.
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idProduct identificador del registro relacionado con la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<EntryProduct> getAvailableLotsForProduct(
            Connection connection,
            Long idProduct
    ) throws SQLException {

        if (connection == null
                || idProduct == null
                || idProduct <= 0) {
            return new ArrayList<>();
        }

        String sql = """
            SELECT
                ep.id_entry_product,
                ep.id_entry,
                ep.id_product_provider,
                s.id_stock,
                ep.quantity,
                ep.remaining_quantity,
                ep.unit_price,
                ep.total_price,
                pp.id_product,
                pp.id_provider,
                p.code AS product_code,
                p.name AS product_name,
                p.id_metric,
                m.name AS metric_name,
                m.shortName AS metric_short_name,
                pr.name AS provider_name
            FROM entry_products ep
            INNER JOIN stock s
                ON ep.id_product_provider = s.id_product_provider
            INNER JOIN entries e
                ON ep.id_entry = e.id_entry
            INNER JOIN product_providers pp
                ON ep.id_product_provider =
                   pp.id_product_provider
            INNER JOIN products p
                ON pp.id_product = p.id_product
            INNER JOIN metrics m
                ON p.id_metric = m.id_metric
            INNER JOIN providers pr
                ON pp.id_provider = pr.id_provider
            WHERE pp.id_product = ?
              AND ep.remaining_quantity > 0
            ORDER BY
                e.change_date,
                ep.id_entry_product
            FOR UPDATE OF ep.remaining_quantity
            """;

        List<EntryProduct> lots =
                new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idProduct);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    lots.add(
                            mapEntryProduct(resultSet)
                    );
                }
            }
        }

        return lots;
    }

    /* ==========================================================
       ACTUALIZAR SALDO DE LOTE FIFO
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idEntryProduct identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean decreaseRemainingQuantity(
            Connection connection,
            Long idEntryProduct,
            int quantity
    ) throws SQLException {

        if (connection == null
                || idEntryProduct == null
                || idEntryProduct <= 0
                || quantity <= 0) {
            return false;
        }

        String sql = """
                UPDATE entry_products
                SET remaining_quantity =
                    remaining_quantity - ?
                WHERE id_entry_product = ?
                  AND remaining_quantity >= ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setLong(2, idEntryProduct);
            statement.setInt(3, quantity);

            return statement.executeUpdate() > 0;
        }
    }

    /* ==========================================================
       FOLIO
       Formato: E + año + cuatro dígitos.
       Ejemplo: E20261234
       ========================================================== */

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

        final int maximumAttempts = 100;

        for (int attempt = 0;
             attempt < maximumAttempts;
             attempt++) {

            int randomNumber =
                    RANDOM.nextInt(10_000);

            String folio = "E"
                    + Year.now().getValue()
                    + String.format(
                    "%04d",
                    randomNumber
            );

            if (!folioExists(connection, folio)) {
                return folio;
            }
        }

        throw new SQLException(
                "No fue posible generar un folio único para la entrada."
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
                FROM entries
                WHERE folio_number = ?
                FETCH FIRST 1 ROWS ONLY
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, folio);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next();
            }
        }
    }

    /* ==========================================================
       VALIDACIONES
       ========================================================== */

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param entry valor de entry requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private boolean isValidEntry(Entry entry) {
        if (entry == null
                || entry.getIdUser() == null
                || entry.getIdUser() <= 0
                || entry.getIdProvider() == null
                || entry.getIdProvider() <= 0
                || entry.getInvoiceNumber() == null
                || entry.getInvoiceNumber().isBlank()
                || entry.getProducts() == null
                || entry.getProducts().isEmpty()) {
            return false;
        }

        List<Long> relationIds =
                new ArrayList<>();

        for (EntryProduct product :
                entry.getProducts()) {

            if (product == null
                    || product.getIdProductProvider() == null
                    || product.getIdProductProvider() <= 0
                    || product.getQuantity() == null
                    || product.getQuantity() <= 0
                    || product.getUnitPrice() == null
                    || product.getUnitPrice()
                    .compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }

            if (relationIds.contains(
                    product.getIdProductProvider()
            )) {
                return false;
            }

            relationIds.add(
                    product.getIdProductProvider()
            );
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
    private BigDecimal calculateEntryTotal(
            List<EntryProduct> products
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (EntryProduct product : products) {
            total = total.add(
                    calculateDetailTotal(product)
            );
        }

        return total.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param product valor de product requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private BigDecimal calculateDetailTotal(
            EntryProduct product
    ) {
        return product.getUnitPrice()
                .multiply(
                        BigDecimal.valueOf(
                                product.getQuantity()
                        )
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    /* ==========================================================
       MAPEO
       ========================================================== */

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
    private Entry mapEntry(
            ResultSet resultSet
    ) throws SQLException {

        Entry entry = new Entry();

        entry.setIdEntry(
                resultSet.getLong("id_entry")
        );

        Timestamp timestamp =
                resultSet.getTimestamp("change_date");

        entry.setChangeDate(
                timestamp == null
                        ? null
                        : timestamp.toLocalDateTime()
        );

        entry.setInvoiceNumber(
                resultSet.getString("invoice_number")
        );

        entry.setFolioNumber(
                resultSet.getString("folio_number")
        );

        entry.setIdUser(
                resultSet.getLong("id_user")
        );

        entry.setUserName(
                resultSet.getString("user_name")
        );

        entry.setIdProvider(
                resultSet.getLong("id_provider")
        );

        entry.setProviderName(
                resultSet.getString("provider_name")
        );

        entry.setProviderRfc(
                resultSet.getString("provider_rfc")
        );

        entry.setTotalAllPrices(
                resultSet.getBigDecimal(
                        "total_all_prices"
                )
        );

        return entry;
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
    private EntryProduct mapEntryProduct(
            ResultSet resultSet
    ) throws SQLException {

        EntryProduct product =
                new EntryProduct();

        product.setIdEntryProduct(
                resultSet.getLong(
                        "id_entry_product"
                )
        );

        product.setIdEntry(
                resultSet.getLong("id_entry")
        );

        product.setIdProductProvider(
                resultSet.getLong(
                        "id_product_provider"
                )
        );

        product.setIdStock(
                resultSet.getLong("id_stock")
        );

        product.setIdProduct(
                resultSet.getLong("id_product")
        );

        product.setIdProvider(
                resultSet.getLong("id_provider")
        );

        product.setProductCode(
                resultSet.getString(
                        "product_code"
                )
        );

        product.setProductName(
                resultSet.getString(
                        "product_name"
                )
        );

        product.setIdMetric(
                resultSet.getLong("id_metric")
        );

        product.setMetricName(
                resultSet.getString(
                        "metric_name"
                )
        );

        product.setMetricShortName(
                resultSet.getString(
                        "metric_short_name"
                )
        );

        product.setProviderName(
                resultSet.getString(
                        "provider_name"
                )
        );

        product.setQuantity(
                resultSet.getInt("quantity")
        );

        product.setRemainingQuantity(
                resultSet.getInt(
                        "remaining_quantity"
                )
        );

        product.setUnitPrice(
                resultSet.getBigDecimal(
                        "unit_price"
                )
        );

        product.setTotalPrice(
                resultSet.getBigDecimal(
                        "total_price"
                )
        );

        return product;
    }

    /* ==========================================================
       CONFIGURACIÓN JDBC
       ========================================================== */

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

        try (Statement statement =
                     connection.createStatement()) {

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
    private void rollbackQuietly(
            Connection connection
    ) {
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
    private void closeConnection(
            Connection connection
    ) {
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
