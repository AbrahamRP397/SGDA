package com.almacen.integradora.models.provider;

import com.almacen.integradora.templates.Dao;
import com.almacen.integradora.utils.SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Persistencia de proveedores y sus relaciones activas con productos.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ProviderDao implements Dao<Provider, Integer> {

    private static final String BASE_SELECT = """
            SELECT
                id_provider,
                name,
                rfc,
                phone,
                email,
                contactName,
                address,
                postCode,
                socialCase,
                contactPhone,
                contactEmail,
                status
            FROM providers
            """;

    /* ==========================================================
       REGISTRAR
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param provider valor de provider requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean create(Provider provider) {
        if (provider == null) {
            return false;
        }

        String sql = """
                INSERT INTO providers (
                    name,
                    rfc,
                    phone,
                    email,
                    contactName,
                    address,
                    postCode,
                    socialCase,
                    contactPhone,
                    contactEmail,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, provider.getName());
            statement.setString(2, provider.getRfc());
            setNullableText(statement, 3, provider.getPhone());
            setNullableText(statement, 4, provider.getEmail());
            setNullableText(statement, 5, provider.getContactName());
            setNullableText(statement, 6, provider.getAddress());
            setNullableText(statement, 7, provider.getPostCode());
            setNullableText(statement, 8, provider.getSocialCase());
            setNullableText(statement, 9, provider.getContactPhone());
            setNullableText(statement, 10, provider.getContactEmail());

            statement.setInt(
                    11,
                    provider.getStatus() == null
                            ? 1
                            : provider.getStatus()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al registrar el proveedor.",
                    exception
            );
        }
    }

    /* ==========================================================
       CONSULTAR TODOS

       Aquí sí mostramos activos e inactivos porque es una pantalla
       administrativa.
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
    public List<Provider> getAll() {
        String sql = BASE_SELECT + """
                ORDER BY
                    status DESC,
                    UPPER(name),
                    UPPER(rfc)
                """;

        List<Provider> providers = new ArrayList<>();

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                providers.add(
                        mapProvider(resultSet)
                );
            }

            return providers;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los proveedores.",
                    exception
            );
        }
    }

    /* ==========================================================
       PROVEEDORES ACTIVOS

       Se utiliza para nuevas operaciones:
       - Registrar/editar productos.
       - Registrar entradas.

       Un proveedor inactivo no debe aparecer en estos selectores.
       ========================================================== */

    /**
     * Obtiene los registros activos disponibles para la operación.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Provider> getActiveProviders() {
        String sql = BASE_SELECT + """
                WHERE status = 1
                ORDER BY
                    UPPER(name),
                    UPPER(rfc)
                """;

        List<Provider> providers = new ArrayList<>();

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                providers.add(
                        mapProvider(resultSet)
                );
            }

            return providers;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los proveedores activos.",
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
     * @param id identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public Provider getById(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE id_provider = ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapProvider(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el proveedor.",
                    exception
            );
        }
    }

    /* ==========================================================
       BUSCAR POR RFC
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param rfc valor de rfc requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Provider findAnyByRfc(String rfc) {
        if (rfc == null || rfc.isBlank()) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE UPPER(TRIM(rfc)) = UPPER(TRIM(?))
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    rfc.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapProvider(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el RFC del proveedor.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param rfc valor de rfc requerido por la operación
     * @param excludedId valor de excludedId requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Provider findAnyByRfcExceptId(
            String rfc,
            long excludedId
    ) {
        if (rfc == null
                || rfc.isBlank()
                || excludedId <= 0) {

            return null;
        }

        String sql = BASE_SELECT + """
                WHERE UPPER(TRIM(rfc)) = UPPER(TRIM(?))
                  AND id_provider <> ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    rfc.trim()
            );

            statement.setLong(
                    2,
                    excludedId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapProvider(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al validar el RFC del proveedor.",
                    exception
            );
        }
    }

    /* ==========================================================
       BUSCAR POR CORREO
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param email dirección de correo asociada a la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Provider findAnyByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = BASE_SELECT + """
                WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    email.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapProvider(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el correo del proveedor.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param email dirección de correo asociada a la operación
     * @param excludedId valor de excludedId requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Provider findAnyByEmailExceptId(
            String email,
            long excludedId
    ) {
        if (email == null
                || email.isBlank()
                || excludedId <= 0) {

            return null;
        }

        String sql = BASE_SELECT + """
                WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))
                  AND id_provider <> ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    email.trim()
            );

            statement.setLong(
                    2,
                    excludedId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapProvider(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al validar el correo del proveedor.",
                    exception
            );
        }
    }

    /* ==========================================================
       ACTUALIZAR

       No modifica status.
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param provider valor de provider requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean update(Provider provider) {
        if (provider == null
                || provider.getIdProvider() == null
                || provider.getIdProvider() <= 0) {

            return false;
        }

        String sql = """
                UPDATE providers
                SET
                    name = ?,
                    rfc = ?,
                    phone = ?,
                    email = ?,
                    contactName = ?,
                    address = ?,
                    postCode = ?,
                    socialCase = ?,
                    contactPhone = ?,
                    contactEmail = ?
                WHERE id_provider = ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, provider.getName());
            statement.setString(2, provider.getRfc());
            setNullableText(statement, 3, provider.getPhone());
            setNullableText(statement, 4, provider.getEmail());
            setNullableText(statement, 5, provider.getContactName());
            setNullableText(statement, 6, provider.getAddress());
            setNullableText(statement, 7, provider.getPostCode());
            setNullableText(statement, 8, provider.getSocialCase());
            setNullableText(statement, 9, provider.getContactPhone());
            setNullableText(statement, 10, provider.getContactEmail());

            statement.setLong(
                    11,
                    provider.getIdProvider()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al actualizar el proveedor.",
                    exception
            );
        }
    }

    /* ==========================================================
       PRODUCTOS ACTIVOS QUE QUEDARÍAN SIN PROVEEDOR

       Cuenta únicamente los productos que:

       1. Están activos.
       2. Tienen una relación ACTIVA con este proveedor.
       3. No tienen ninguna otra relación activa con otro
          proveedor que también esté activo.

       Productos inactivos NO bloquean la desactivación.
       Relaciones inactivas tampoco cuentan.
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param idProvider identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public int countActiveProductsThatWouldLoseProvider(
            long idProvider
    ) {
        if (idProvider <= 0) {
            return 0;
        }

        String sql = """
                SELECT COUNT(*) AS total
                FROM products p
                WHERE p.status = 1

                  AND EXISTS (
                      SELECT 1
                      FROM product_providers current_relation
                      WHERE current_relation.id_product = p.id_product
                        AND current_relation.id_provider = ?
                        AND current_relation.status = 1
                  )

                  AND NOT EXISTS (
                      SELECT 1
                      FROM product_providers other_relation
                      INNER JOIN providers other_provider
                          ON other_provider.id_provider =
                             other_relation.id_provider
                      WHERE other_relation.id_product = p.id_product
                        AND other_relation.id_provider <> ?
                        AND other_relation.status = 1
                        AND other_provider.status = 1
                  )
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(
                    1,
                    idProvider
            );

            statement.setLong(
                    2,
                    idProvider
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (!resultSet.next()) {
                    return 0;
                }

                return resultSet.getInt(
                        "total"
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al validar los productos dependientes del proveedor.",
                    exception
            );
        }
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param idProvider identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean wouldLeaveActiveProductsWithoutProvider(
            long idProvider
    ) {
        return countActiveProductsThatWouldLoseProvider(
                idProvider
        ) > 0;
    }

    /* ==========================================================
       CAMBIAR ESTADO

       Solo cambia providers.status.

       NO modificamos:
       - product_providers
       - stock
       - entries
       - entry_products

       Esto mantiene historial y existencias.
       ========================================================== */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idProvider identificador del registro relacionado con la operación
     * @param status estado que se utilizará en la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean changeStatus(
            long idProvider,
            int status
    ) {
        if (idProvider <= 0
                || (status != 0 && status != 1)) {

            return false;
        }

        String sql = """
                UPDATE providers
                SET status = ?
                WHERE id_provider = ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    status
            );

            statement.setLong(
                    2,
                    idProvider
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al cambiar el estado del proveedor.",
                    exception
            );
        }
    }

    /* ==========================================================
       BAJA LÓGICA
       ========================================================== */

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

        return changeStatus(
                id.longValue(),
                0
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
    private Provider mapProvider(
            ResultSet resultSet
    ) throws SQLException {

        Provider provider =
                new Provider();

        provider.setIdProvider(
                resultSet.getLong(
                        "id_provider"
                )
        );

        provider.setName(
                resultSet.getString(
                        "name"
                )
        );

        provider.setRfc(
                resultSet.getString(
                        "rfc"
                )
        );

        provider.setPhone(
                resultSet.getString(
                        "phone"
                )
        );

        provider.setEmail(
                resultSet.getString(
                        "email"
                )
        );

        provider.setContactName(
                resultSet.getString(
                        "contactName"
                )
        );

        provider.setAddress(
                resultSet.getString(
                        "address"
                )
        );

        provider.setPostCode(
                resultSet.getString(
                        "postCode"
                )
        );

        provider.setSocialCase(
                resultSet.getString(
                        "socialCase"
                )
        );

        provider.setContactPhone(
                resultSet.getString(
                        "contactPhone"
                )
        );

        provider.setContactEmail(
                resultSet.getString(
                        "contactEmail"
                )
        );

        provider.setStatus(
                resultSet.getInt(
                        "status"
                )
        );

        return provider;
    }

    /* ==========================================================
       NULLABLE TEXT
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

        if (value == null
                || value.isBlank()) {

            statement.setNull(
                    index,
                    Types.VARCHAR
            );

            return;
        }

        statement.setString(
                index,
                value.trim()
        );
    }
}
