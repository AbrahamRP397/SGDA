package com.almacen.integradora.models.product;

import com.almacen.integradora.templates.Dao;
import com.almacen.integradora.utils.SQLConnector;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Persistencia de productos y proveedores asociados.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ProductDao implements Dao<Product, Integer> {

    private static final String BASE_SELECT = """
            SELECT
                p.id_product,
                p.code,
                p.name,
                p.id_metric,
                p.description,
                p.status,
                m.name AS metric_name,
                m.shortName AS metric_short_name
            FROM products p
            INNER JOIN metrics m
                ON p.id_metric = m.id_metric
            """;

    private static final String PROVIDER_SELECT = """
        SELECT
            pp.id_product_provider,
            pp.id_product,
            pp.id_provider,
            pp.purchase_price,
            pp.status,
            pr.status AS provider_status,
            pr.name AS provider_name,
            pr.rfc AS provider_rfc
        FROM product_providers pp
        INNER JOIN providers pr
            ON pp.id_provider = pr.id_provider
        """;

    /* ==========================================================
       REGISTRAR PRODUCTO CON PROVEEDORES
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param product valor de product requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean create(Product product) {
        if (product == null) {
            return false;
        }

        String sqlProduct = """
                INSERT INTO products (
                    code,
                    name,
                    id_metric,
                    description,
                    status
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        Connection connection = null;

        try {
            connection = SQLConnector.getConnection();
            disableParallelDml(connection);
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(
                    sqlProduct,
                    new String[]{"ID_PRODUCT"}
            )) {
                statement.setString(1, product.getCode());
                statement.setString(2, product.getName());
                statement.setLong(3, product.getIdMetric());
                setNullableText(statement, 4, product.getDescription());
                statement.setInt(
                        5,
                        product.getStatus() == null
                                ? 1
                                : product.getStatus()
                );

                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new SQLException(
                                "Oracle insertó el producto pero no devolvió ID_PRODUCT."
                        );
                    }

                    long idProduct = generatedKeys.getLong(1);

                    if (idProduct <= 0) {
                        throw new SQLException(
                                "Oracle devolvió un ID_PRODUCT no válido: " + idProduct
                        );
                    }

                    product.setIdProduct(idProduct);
                }
            }

            synchronizeProviders(
                    connection,
                    product.getIdProduct(),
                    product.getProviders()
            );

            connection.commit();
            return true;

        } catch (SQLException exception) {
            rollbackQuietly(connection);

            throw new RuntimeException(
                    "Error al registrar el producto con sus proveedores.",
                    exception
            );

        } finally {
            closeConnection(connection);
        }
    }

    /* ==========================================================
       CONSULTAR TODOS
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
    @Override
    public List<Product> getAll() {
        String sql = BASE_SELECT + """
                ORDER BY
                    p.status DESC,
                    UPPER(p.name),
                    UPPER(p.code)
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<Product> products = new ArrayList<>();
            Map<Long, Product> productsById = new LinkedHashMap<>();

            while (resultSet.next()) {
                Product product = mapProduct(resultSet);

                products.add(product);
                productsById.put(product.getIdProduct(), product);
            }

            loadProvidersForProducts(connection, productsById);

            return products;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los productos.",
                    exception
            );
        }
    }

    /* ==========================================================
       CONSULTAR PRODUCTO POR ID
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param id identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public Product getById(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE p.id_product = ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Product product = mapProduct(resultSet);

                product.setProviders(
                        getProvidersByProduct(
                                connection,
                                product.getIdProduct(),
                                false
                        )
                );

                return product;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el producto.",
                    exception
            );
        }
    }

    /* ==========================================================
       CONSULTAR POR CÓDIGO
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param code valor de code requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Product findAnyByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE UPPER(TRIM(p.code)) = UPPER(TRIM(?))
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Product product = mapProduct(resultSet);

                product.setProviders(
                        getProvidersByProduct(
                                connection,
                                product.getIdProduct(),
                                false
                        )
                );

                return product;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el código del producto.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param code valor de code requerido por la operación
     * @param excludedId valor de excludedId requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Product findAnyByCodeExceptId(String code, long excludedId) {
        if (code == null || code.isBlank() || excludedId <= 0) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE UPPER(TRIM(p.code)) = UPPER(TRIM(?))
                  AND p.id_product <> ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, code.trim());
            statement.setLong(2, excludedId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return mapProduct(resultSet);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al validar el código del producto.",
                    exception
            );
        }
    }

    /* ==========================================================
       ACTUALIZAR PRODUCTO Y PROVEEDORES
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param product valor de product requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean update(Product product) {
        if (product == null
                || product.getIdProduct() == null
                || product.getIdProduct() <= 0) {
            return false;
        }

        String sqlProduct = """
                UPDATE products
                SET
                    code = ?,
                    name = ?,
                    id_metric = ?,
                    description = ?
                WHERE id_product = ?
                """;

        Connection connection = null;

        try {
            connection = SQLConnector.getConnection();
            disableParallelDml(connection);
            connection.setAutoCommit(false);

            try (PreparedStatement statement =
                         connection.prepareStatement(sqlProduct)) {

                statement.setString(1, product.getCode());
                statement.setString(2, product.getName());
                statement.setLong(3, product.getIdMetric());
                setNullableText(statement, 4, product.getDescription());
                statement.setLong(5, product.getIdProduct());

                if (statement.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            synchronizeProviders(
                    connection,
                    product.getIdProduct(),
                    product.getProviders()
            );

            connection.commit();
            return true;

        } catch (SQLException exception) {
            rollbackQuietly(connection);

            throw new RuntimeException(
                    "Error al actualizar el producto con sus proveedores.",
                    exception
            );

        } finally {
            closeConnection(connection);
        }
    }

    /* ==========================================================
       SINCRONIZAR PROVEEDORES

       1. Inactiva relaciones anteriores.
       2. Inserta relaciones nuevas.
       3. Reactiva y actualiza las existentes.
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param idProduct identificador del registro relacionado con la operación
     * @param providers valor de providers requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void synchronizeProviders(
            Connection connection,
            Long idProduct,
            List<ProductProvider> providers
    ) throws SQLException {

        if (connection == null) {
            throw new SQLException(
                    "No existe una conexión válida para sincronizar los proveedores."
            );
        }

        if (idProduct == null || idProduct <= 0) {
            throw new SQLException(
                    "El identificador del producto no es válido."
            );
        }

        /*
         * ==========================================================
         * REGLA DE SINCRONIZACIÓN
         * ==========================================================
         *
         * Solo desactivamos relaciones pertenecientes a proveedores
         * GLOBALMENTE ACTIVOS.
         *
         * ¿Por qué?
         *
         * Los proveedores globalmente inactivos no aparecen en el
         * formulario de edición del producto.
         *
         * Por lo tanto, su ausencia en la petición NO significa que
         * el usuario quiera eliminar la relación.
         *
         * Si desactiváramos todas las relaciones:
         *
         * proveedor.status = 0
         * product_provider.status = 1
         *
         * al editar el producto pasaría accidentalmente a:
         *
         * proveedor.status = 0
         * product_provider.status = 0
         *
         * y al reactivar después el proveedor, la relación ya no
         * volvería a ser operativa.
         *
         * Conservando relation.status = 1, la relación vuelve a estar
         * disponible automáticamente cuando el proveedor se reactive.
         */
        String deactivateSql = """
            UPDATE product_providers
            SET status = 0
            WHERE id_product = ?
              AND id_provider IN (
                  SELECT id_provider
                  FROM providers
                  WHERE status = 1
              )
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                deactivateSql
                        )
        ) {
            statement.setLong(
                    1,
                    idProduct
            );

            statement.executeUpdate();
        }

        /*
         * Si no se enviaron proveedores no hay nada que reactivar.
         *
         * En condiciones normales ProductServlet ya impide guardar
         * un producto sin al menos un proveedor activo.
         */
        if (providers == null || providers.isEmpty()) {
            return;
        }

        validateDuplicatedProviders(
                providers
        );

        /*
         * ==========================================================
         * REACTIVAR RELACIÓN EXISTENTE
         * ==========================================================
         *
         * Si la relación ya existe:
         *
         * - actualizamos precio;
         * - status = 1.
         *
         * Esto permite volver a asociar un proveedor que el usuario
         * había quitado anteriormente.
         */
        String updateSql = """
            UPDATE product_providers
            SET purchase_price = ?,
                status = 1
            WHERE id_product = ?
              AND id_provider = ?
            """;

        /*
         * ==========================================================
         * INSERTAR RELACIÓN NUEVA
         * ==========================================================
         */
        String insertSql = """
            INSERT INTO product_providers (
                id_product,
                id_provider,
                purchase_price,
                status
            )
            VALUES (?, ?, ?, 1)
            """;

        try (
                PreparedStatement updateStatement =
                        connection.prepareStatement(
                                updateSql
                        );

                PreparedStatement insertStatement =
                        connection.prepareStatement(
                                insertSql
                        )
        ) {
            for (ProductProvider provider : providers) {

                /*
                 * Vuelve a comprobar en BD que el proveedor exista,
                 * esté activo y tenga un precio válido.
                 *
                 * No confiamos solamente en lo enviado por el frontend.
                 */
                validateProvider(
                        connection,
                        provider
                );

                updateStatement.setBigDecimal(
                        1,
                        provider.getPurchasePrice()
                );

                updateStatement.setLong(
                        2,
                        idProduct
                );

                updateStatement.setLong(
                        3,
                        provider.getIdProvider()
                );

                int updatedRows =
                        updateStatement.executeUpdate();

                /*
                 * La relación ya existía.
                 */
                if (updatedRows > 0) {
                    continue;
                }

                /*
                 * La relación nunca había existido.
                 */
                insertStatement.setLong(
                        1,
                        idProduct
                );

                insertStatement.setLong(
                        2,
                        provider.getIdProvider()
                );

                insertStatement.setBigDecimal(
                        3,
                        provider.getPurchasePrice()
                );

                if (insertStatement.executeUpdate() != 1) {
                    throw new SQLException(
                            "No fue posible asociar uno de los proveedores al producto."
                    );
                }
            }
        }
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param connection conexión JDBC activa
     * @param provider valor de provider requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void validateProvider(
            Connection connection,
            ProductProvider provider
    ) throws SQLException {

        if (connection == null) {
            throw new SQLException(
                    "No existe una conexión válida para comprobar el proveedor."
            );
        }

        if (provider == null
                || provider.getIdProvider() == null
                || provider.getIdProvider() <= 0) {

            throw new SQLException(
                    "Se recibió un proveedor no válido."
            );
        }

        BigDecimal price =
                provider.getPurchasePrice();

        if (price == null
                || price.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new SQLException(
                    "El precio de compra no es válido."
            );
        }

        String sql = """
            SELECT status
            FROM providers
            WHERE id_provider = ?
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(
                    1,
                    provider.getIdProvider()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (!resultSet.next()) {
                    throw new SQLException(
                            "Uno de los proveedores seleccionados no existe."
                    );
                }

                if (resultSet.getInt(
                        "status"
                ) != 1) {

                    throw new SQLException(
                            "No se puede asociar un proveedor inactivo al producto."
                    );
                }
            }
        }
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param providers valor de providers requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void validateDuplicatedProviders(
            List<ProductProvider> providers
    ) throws SQLException {

        List<Long> providerIds = new ArrayList<>();

        for (ProductProvider provider : providers) {
            if (provider == null
                    || provider.getIdProvider() == null) {
                continue;
            }

            if (providerIds.contains(provider.getIdProvider())) {
                throw new SQLException(
                        "No puedes asociar dos veces el mismo proveedor."
                );
            }

            providerIds.add(provider.getIdProvider());
        }
    }

    /* ==========================================================
       PROVEEDORES DE UN PRODUCTO
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
    public List<ProductProvider> getProvidersByProduct(Long idProduct) {
        if (idProduct == null || idProduct <= 0) {
            return new ArrayList<>();
        }

        try (Connection connection = SQLConnector.getConnection()) {
            return getProvidersByProduct(
                    connection,
                    idProduct,
                    false
            );

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los proveedores del producto.",
                    exception
            );
        }
    }

    /**
     * Obtiene los registros activos disponibles para la operación.
     *
     * @param idProduct identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<ProductProvider> getActiveProvidersByProduct(
            Long idProduct
    ) {
        if (idProduct == null || idProduct <= 0) {
            return new ArrayList<>();
        }

        try (Connection connection = SQLConnector.getConnection()) {
            return getProvidersByProduct(
                    connection,
                    idProduct,
                    true
            );

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los proveedores activos del producto.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idProduct identificador del registro relacionado con la operación
     * @param onlyActive valor de onlyActive requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ProductProvider> getProvidersByProduct(
            Connection connection,
            Long idProduct,
            boolean onlyActive
    ) throws SQLException {

        String sql = PROVIDER_SELECT + """
                WHERE pp.id_product = ?
                """ + (onlyActive
                ? " AND pp.status = 1 AND pr.status = 1 "
                : "") + """
                ORDER BY
                    pp.status DESC,
                    UPPER(pr.name)
                """;

        List<ProductProvider> providers = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idProduct);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    providers.add(
                            mapProductProvider(resultSet)
                    );
                }
            }
        }

        return providers;
    }

    /* ==========================================================
       PRODUCTOS ACTIVOS POR PROVEEDOR

       Este método se utilizará después en Entradas.
       ========================================================== */

    /**
     * Obtiene los registros activos disponibles para la operación.
     *
     * @param idProvider identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Product> getActiveProductsByProvider(
            Long idProvider
    ) {
        if (idProvider == null || idProvider <= 0) {
            return new ArrayList<>();
        }

        String sql = """
                SELECT
                    p.id_product,
                    p.code,
                    p.name,
                    p.id_metric,
                    p.description,
                    p.status,
                    m.name AS metric_name,
                    m.shortName AS metric_short_name,
                    pp.id_product_provider,
                    pp.id_provider,
                    pp.purchase_price,
                    pp.status AS relation_status,
                    pr.name AS provider_name,
                    pr.rfc AS provider_rfc
                FROM product_providers pp
                INNER JOIN products p
                    ON pp.id_product = p.id_product
                INNER JOIN metrics m
                    ON p.id_metric = m.id_metric
                INNER JOIN providers pr
                    ON pp.id_provider = pr.id_provider
                WHERE pp.id_provider = ?
                  AND pp.status = 1
                  AND p.status = 1
                  AND m.status = 1
                  AND pr.status = 1
                ORDER BY UPPER(p.name), UPPER(p.code)
                """;

        List<Product> products = new ArrayList<>();

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, idProvider);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = mapProduct(resultSet);

                    ProductProvider relation =
                            new ProductProvider();

                    relation.setIdProductProvider(
                            resultSet.getLong(
                                    "id_product_provider"
                            )
                    );

                    relation.setIdProduct(
                            product.getIdProduct()
                    );

                    relation.setIdProvider(
                            resultSet.getLong("id_provider")
                    );

                    relation.setPurchasePrice(
                            resultSet.getBigDecimal(
                                    "purchase_price"
                            )
                    );

                    relation.setStatus(
                            resultSet.getInt("relation_status")
                    );

                    relation.setProviderName(
                            resultSet.getString(
                                    "provider_name"
                            )
                    );

                    relation.setProviderRfc(
                            resultSet.getString(
                                    "provider_rfc"
                            )
                    );

                    product.addProvider(relation);
                    products.add(product);
                }
            }

            return products;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los productos del proveedor.",
                    exception
            );
        }
    }

    /* ==========================================================
       CARGAR PROVEEDORES DE VARIOS PRODUCTOS
       ========================================================== */

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
    private void loadProvidersForProducts(
            Connection connection,
            Map<Long, Product> productsById
    ) throws SQLException {

        if (productsById == null || productsById.isEmpty()) {
            return;
        }

        String sql = PROVIDER_SELECT + """
                ORDER BY
                    pp.id_product,
                    pp.status DESC,
                    UPPER(pr.name)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long idProduct =
                        resultSet.getLong("id_product");

                Product product =
                        productsById.get(idProduct);

                if (product != null) {
                    product.addProvider(
                            mapProductProvider(resultSet)
                    );
                }
            }
        }
    }

    /* ==========================================================
       CAMBIAR ESTADO DEL PRODUCTO
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idProduct identificador del registro relacionado con la operación
     * @param status estado que se utilizará en la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean changeStatus(long idProduct, int status) {
        if (idProduct <= 0 || (status != 0 && status != 1)) {
            return false;
        }

        String sql = """
                UPDATE products
                SET status = ?
                WHERE id_product = ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, status);
            statement.setLong(2, idProduct);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al cambiar el estado del producto.",
                    exception
            );
        }
    }

    /**
     * Ejecuta la eliminación definida por el componente, física o lógica según su contrato.
     *
     * @param id identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean delete(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }

        return changeStatus(id.longValue(), 0);
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
    private Product mapProduct(ResultSet resultSet)
            throws SQLException {

        Product product = new Product();

        product.setIdProduct(
                resultSet.getLong("id_product")
        );

        product.setCode(
                resultSet.getString("code")
        );

        product.setName(
                resultSet.getString("name")
        );

        product.setIdMetric(
                resultSet.getLong("id_metric")
        );

        product.setMetricName(
                resultSet.getString("metric_name")
        );

        product.setMetricShortName(
                resultSet.getString("metric_short_name")
        );

        product.setDescription(
                resultSet.getString("description")
        );

        product.setStatus(
                resultSet.getInt("status")
        );

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
    private ProductProvider mapProductProvider(
            ResultSet resultSet
    ) throws SQLException {

        ProductProvider provider =
                new ProductProvider();

        provider.setIdProductProvider(
                resultSet.getLong(
                        "id_product_provider"
                )
        );

        provider.setIdProduct(
                resultSet.getLong(
                        "id_product"
                )
        );

        provider.setIdProvider(
                resultSet.getLong(
                        "id_provider"
                )
        );

        provider.setPurchasePrice(
                resultSet.getBigDecimal(
                        "purchase_price"
                )
        );

        provider.setStatus(
                resultSet.getInt(
                        "status"
                )
        );

        provider.setProviderStatus(
                resultSet.getInt(
                        "provider_status"
                )
        );

        provider.setProviderName(
                resultSet.getString(
                        "provider_name"
                )
        );

        provider.setProviderRfc(
                resultSet.getString(
                        "provider_rfc"
                )
        );

        return provider;
    }

    /* ==========================================================
       AUXILIARES JDBC
       ========================================================== */

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param statement sentencia preparada que recibirá el valor
     * @param index posición del parámetro o elemento procesado
     * @param value valor de value requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void setNullableText(
            PreparedStatement statement,
            int index,
            String value
    ) throws SQLException {

        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
            return;
        }

        statement.setString(index, value.trim());
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
    private void disableParallelDml(Connection connection)
            throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    "ALTER SESSION DISABLE PARALLEL DML"
            );
        }
    }
}
