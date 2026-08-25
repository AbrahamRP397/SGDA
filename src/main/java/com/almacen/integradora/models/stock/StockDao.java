package com.almacen.integradora.models.stock;

import com.almacen.integradora.utils.SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Consultas y ajustes de existencias por producto y proveedor.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class StockDao {

    private static final String BASE_SELECT = """
        SELECT
            s.id_stock,
            s.id_product_provider,
            s.quantity,

            pp.id_product,
            pp.id_provider,
            pp.purchase_price,
            pp.status AS relation_status,

            p.code AS product_code,
            p.name AS product_name,
            p.id_metric,
            p.status AS product_status,

            m.name AS metric_name,
            m.shortName AS metric_short_name,
            m.status AS metric_status,

            pr.name AS provider_name,
            pr.rfc AS provider_rfc,
            pr.status AS provider_status

        FROM stock s
        INNER JOIN product_providers pp
            ON s.id_product_provider = pp.id_product_provider
        INNER JOIN products p
            ON pp.id_product = p.id_product
        INNER JOIN metrics m
            ON p.id_metric = m.id_metric
        INNER JOIN providers pr
            ON pp.id_provider = pr.id_provider
        """;

    /* ==========================================================
       CREAR STOCK
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param stock valor de stock requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean create(Stock stock) {
        if (stock == null
                || stock.getIdProductProvider() == null
                || stock.getIdProductProvider() <= 0) {
            return false;
        }

        try (Connection connection = SQLConnector.getConnection()) {
            return create(connection, stock);
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al registrar el stock.",
                    exception
            );
        }
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param connection conexión JDBC activa
     * @param stock valor de stock requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean create(
            Connection connection,
            Stock stock
    ) throws SQLException {

        if (connection == null
                || stock == null
                || stock.getIdProductProvider() == null
                || stock.getIdProductProvider() <= 0) {
            return false;
        }

        String sql = """
                INSERT INTO stock (
                    id_product_provider,
                    quantity
                )
                VALUES (?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    stock.getIdProductProvider()
            );

            statement.setInt(
                    2,
                    stock.getQuantity() == null
                            ? 0
                            : stock.getQuantity()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /* ==========================================================
       CONSULTAR TODO EL STOCK
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
    public List<Stock> getAllStock() {
        String sql = BASE_SELECT + """
                ORDER BY
                    UPPER(p.name),
                    UPPER(pr.name)
                """;

        List<Stock> stockList = new ArrayList<>();

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                stockList.add(mapStock(resultSet));
            }

            return stockList;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el stock.",
                    exception
            );
        }
    }

    /* ==========================================================
       BUSCAR STOCK CON FILTROS
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param code valor de code requerido por la operación
     * @param name valor de name requerido por la operación
     * @param idMetric identificador del registro relacionado con la operación
     * @param providerName valor de providerName requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Stock> searchStock(
            String code,
            String name,
            Long idMetric,
            String providerName
    ) {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        List<Object> parameters = new ArrayList<>();

        sql.append(" WHERE 1 = 1 ");

        if (code != null && !code.isBlank()) {
            sql.append("""
                    AND UPPER(p.code) LIKE UPPER(?)
                    """);

            parameters.add(
                    "%" + code.trim() + "%"
            );
        }

        if (name != null && !name.isBlank()) {
            sql.append("""
                    AND UPPER(p.name) LIKE UPPER(?)
                    """);

            parameters.add(
                    "%" + name.trim() + "%"
            );
        }

        if (idMetric != null && idMetric > 0) {
            sql.append("""
                    AND p.id_metric = ?
                    """);

            parameters.add(idMetric);
        }

        if (providerName != null
                && !providerName.isBlank()) {

            sql.append("""
                    AND UPPER(pr.name) LIKE UPPER(?)
                    """);

            parameters.add(
                    "%" + providerName.trim() + "%"
            );
        }

        sql.append("""
                ORDER BY
                    UPPER(p.name),
                    UPPER(pr.name)
                """);

        List<Stock> stockList = new ArrayList<>();

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {

            setParameters(statement, parameters);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    stockList.add(
                            mapStock(resultSet)
                    );
                }
            }

            return stockList;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al filtrar el stock.",
                    exception
            );
        }
    }

    /* ==========================================================
       CONSULTAR POR ID
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idStock identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Stock getById(Long idStock) {
        if (idStock == null || idStock <= 0) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE s.id_stock = ?
                """;

        try (Connection connection =
                     SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idStock);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapStock(resultSet);
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el stock.",
                    exception
            );
        }
    }

    /* ==========================================================
       CONSULTAR POR RELACIÓN PRODUCTO-PROVEEDOR
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idProductProvider identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Stock getByProductProvider(
            Long idProductProvider
    ) {
        if (idProductProvider == null
                || idProductProvider <= 0) {
            return null;
        }

        try (Connection connection =
                     SQLConnector.getConnection()) {

            return getByProductProvider(
                    connection,
                    idProductProvider,
                    false
            );

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el stock del producto y proveedor.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idProductProvider identificador del registro relacionado con la operación
     * @param lockForUpdate valor de lockForUpdate requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Stock getByProductProvider(
            Connection connection,
            Long idProductProvider,
            boolean lockForUpdate
    ) throws SQLException {

        if (connection == null
                || idProductProvider == null
                || idProductProvider <= 0) {
            return null;
        }

        String sql = """
                SELECT
                    id_stock,
                    id_product_provider,
                    quantity
                FROM stock
                WHERE id_product_provider = ?
                """ + (lockForUpdate
                ? " FOR UPDATE"
                : "");

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    idProductProvider
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                Stock stock = new Stock();

                stock.setIdStock(
                        resultSet.getLong("id_stock")
                );

                stock.setIdProductProvider(
                        resultSet.getLong(
                                "id_product_provider"
                        )
                );

                stock.setQuantity(
                        resultSet.getInt("quantity")
                );

                return stock;
            }
        }
    }

    /* ==========================================================
       AUMENTAR STOCK

       Se utilizará en Entradas.
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idProductProvider identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean increaseStock(
            Connection connection,
            Long idProductProvider,
            int quantity
    ) throws SQLException {

        if (connection == null
                || idProductProvider == null
                || idProductProvider <= 0
                || quantity <= 0) {
            return false;
        }

        String updateSql = """
                UPDATE stock
                SET quantity = quantity + ?
                WHERE id_product_provider = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(updateSql)) {

            statement.setInt(1, quantity);
            statement.setLong(
                    2,
                    idProductProvider
            );

            int updatedRows =
                    statement.executeUpdate();

            if (updatedRows > 0) {
                return true;
            }
        }

        String insertSql = """
                INSERT INTO stock (
                    id_product_provider,
                    quantity
                )
                VALUES (?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(insertSql)) {

            statement.setLong(
                    1,
                    idProductProvider
            );

            statement.setInt(
                    2,
                    quantity
            );

            return statement.executeUpdate() > 0;
        }
    }

    /* ==========================================================
       DISMINUIR STOCK

       Se utilizará en Salidas.
       La fila debe bloquearse antes con FOR UPDATE.
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idStock identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean decreaseStock(
            Connection connection,
            Long idStock,
            int quantity
    ) throws SQLException {

        if (connection == null
                || idStock == null
                || idStock <= 0
                || quantity <= 0) {
            return false;
        }

        String sql = """
                UPDATE stock
                SET quantity = quantity - ?
                WHERE id_stock = ?
                  AND quantity >= ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setLong(2, idStock);
            statement.setInt(3, quantity);

            return statement.executeUpdate() > 0;
        }
    }

    /* ==========================================================
       ESTABLECER CANTIDAD
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idStock identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean updateQuantity(
            Connection connection,
            Long idStock,
            int quantity
    ) throws SQLException {

        if (connection == null
                || idStock == null
                || idStock <= 0
                || quantity < 0) {
            return false;
        }

        String sql = """
                UPDATE stock
                SET quantity = ?
                WHERE id_stock = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setLong(2, idStock);

            return statement.executeUpdate() > 0;
        }
    }

    /* ==========================================================
       STOCK DE UN PRODUCTO

       Devuelve una fila por proveedor.
       Después servirá para FIFO.
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idProduct identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Stock> getStockByProduct(
            Long idProduct
    ) {
        if (idProduct == null || idProduct <= 0) {
            return new ArrayList<>();
        }

        String sql = BASE_SELECT + """
                WHERE pp.id_product = ?
                ORDER BY
                    s.id_stock,
                    UPPER(pr.name)
                """;

        List<Stock> stockList = new ArrayList<>();

        try (Connection connection =
                     SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idProduct);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    stockList.add(
                            mapStock(resultSet)
                    );
                }
            }

            return stockList;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el stock del producto.",
                    exception
            );
        }
    }

    /* ==========================================================
       STOCK DISPONIBLE PARA FIFO

       Por ahora se ordena por id_stock.
       Cuando construyamos Entradas lo cambiaremos para usar
       la fecha real del lote o de la entrada.
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
    public List<Stock> getAvailableStockForProduct(
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
            s.id_stock,
            s.id_product_provider,
            s.quantity,

            pp.id_product,
            pp.id_provider,
            pp.purchase_price,
            pp.status AS relation_status,

            p.code AS product_code,
            p.name AS product_name,
            p.id_metric,
            p.status AS product_status,

            m.name AS metric_name,
            m.shortName AS metric_short_name,
            m.status AS metric_status,

            pr.name AS provider_name,
            pr.rfc AS provider_rfc,
            pr.status AS provider_status

        FROM stock s
        INNER JOIN product_providers pp
            ON s.id_product_provider = pp.id_product_provider
        INNER JOIN products p
            ON pp.id_product = p.id_product
        INNER JOIN metrics m
            ON p.id_metric = m.id_metric
        INNER JOIN providers pr
            ON pp.id_provider = pr.id_provider
        WHERE pp.id_product = ?
          AND s.quantity > 0
        ORDER BY s.id_stock
        FOR UPDATE OF s.quantity
        """;

        List<Stock> stockList =
                new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idProduct);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    stockList.add(
                            mapStock(resultSet)
                    );
                }
            }
        }

        return stockList;
    }

    /* ==========================================================
       TOTAL DISPONIBLE DE UN PRODUCTO
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param idProduct identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public int getTotalQuantityByProduct(
            Long idProduct
    ) {
        if (idProduct == null || idProduct <= 0) {
            return 0;
        }

        String sql = """
                SELECT NVL(SUM(s.quantity), 0) AS total_quantity
                FROM stock s
                INNER JOIN product_providers pp
                    ON s.id_product_provider =
                       pp.id_product_provider
                WHERE pp.id_product = ?
                """;

        try (Connection connection =
                     SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idProduct);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(
                            "total_quantity"
                    );
                }
            }

            return 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar la existencia total del producto.",
                    exception
            );
        }
    }

    /* ==========================================================
       ELIMINAR STOCK

       Normalmente no se utilizará porque se conserva el historial.
       ========================================================== */

    /**
     * Ejecuta la eliminación definida por el componente, física o lógica según su contrato.
     *
     * @param idStock identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean delete(Long idStock) {
        if (idStock == null || idStock <= 0) {
            return false;
        }

        String sql = """
                DELETE FROM stock
                WHERE id_stock = ?
                """;

        try (Connection connection =
                     SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idStock);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al eliminar el stock.",
                    exception
            );
        }
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
    private Stock mapStock(
            ResultSet resultSet
    ) throws SQLException {

        Stock stock = new Stock();

        stock.setIdStock(
                resultSet.getLong(
                        "id_stock"
                )
        );

        stock.setIdProductProvider(
                resultSet.getLong(
                        "id_product_provider"
                )
        );

        stock.setQuantity(
                resultSet.getInt(
                        "quantity"
                )
        );

        stock.setIdProduct(
                resultSet.getLong(
                        "id_product"
                )
        );

        stock.setProductCode(
                resultSet.getString(
                        "product_code"
                )
        );

        stock.setProductName(
                resultSet.getString(
                        "product_name"
                )
        );

        stock.setProductStatus(
                resultSet.getInt(
                        "product_status"
                )
        );

        stock.setIdMetric(
                resultSet.getLong(
                        "id_metric"
                )
        );

        stock.setMetricName(
                resultSet.getString(
                        "metric_name"
                )
        );

        stock.setMetricShortName(
                resultSet.getString(
                        "metric_short_name"
                )
        );

        stock.setMetricStatus(
                resultSet.getInt(
                        "metric_status"
                )
        );

        stock.setIdProvider(
                resultSet.getLong(
                        "id_provider"
                )
        );

        stock.setProviderName(
                resultSet.getString(
                        "provider_name"
                )
        );

        stock.setProviderRfc(
                resultSet.getString(
                        "provider_rfc"
                )
        );

        stock.setProviderStatus(
                resultSet.getInt(
                        "provider_status"
                )
        );

        stock.setRelationStatus(
                resultSet.getInt(
                        "relation_status"
                )
        );

        stock.setPurchasePrice(
                resultSet.getBigDecimal(
                        "purchase_price"
                )
        );

        return stock;
    }

    /* ==========================================================
       PARÁMETROS DINÁMICOS
       ========================================================== */

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param statement sentencia preparada que recibirá el valor
     * @param parameters valor de parameters requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void setParameters(
            PreparedStatement statement,
            List<Object> parameters
    ) throws SQLException {

        for (int index = 0;
             index < parameters.size();
             index++) {

            Object value = parameters.get(index);

            if (value instanceof Long longValue) {
                statement.setLong(
                        index + 1,
                        longValue
                );
            } else {
                statement.setString(
                        index + 1,
                        String.valueOf(value)
                );
            }
        }
    }
}
