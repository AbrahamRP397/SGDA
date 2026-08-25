package com.almacen.integradora.models.metric;

import com.almacen.integradora.templates.Dao;
import com.almacen.integradora.utils.SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de unidades de medida y sus dependencias activas.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class MetricDao implements Dao<Metric, Integer> {

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param metric valor de metric requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean create(Metric metric) {
        if (metric == null) {
            return false;
        }

        String sql = """
                INSERT INTO metrics (
                    name,
                    shortName,
                    status
                )
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    metric.getName()
            );

            statement.setString(
                    2,
                    metric.getShortName()
            );

            statement.setInt(
                    3,
                    metric.getStatus() == null
                            ? 1
                            : metric.getStatus()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al registrar la unidad de medida.",
                    exception
            );
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
    @Override
    public List<Metric> getAll() {
        String sql = """
                SELECT
                    id_metric,
                    name,
                    shortName,
                    status
                FROM metrics
                ORDER BY
                    status DESC,
                    UPPER(name),
                    UPPER(shortName)
                """;

        List<Metric> metrics =
                new ArrayList<>();

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                metrics.add(
                        mapMetric(resultSet)
                );
            }

            return metrics;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar las unidades de medida.",
                    exception
            );
        }
    }

    /*
     * ==========================================================
     * MÉTRICAS ACTIVAS
     *
     * Se utiliza en formularios operativos, por ejemplo:
     *
     * - Registrar producto.
     * - Editar producto.
     *
     * Una métrica inactiva nunca debe aparecer como opción
     * para una nueva asociación.
     * ==========================================================
     */

    /**
     * Obtiene los registros activos disponibles para la operación.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Metric> getActiveMetrics() {
        String sql = """
                SELECT
                    id_metric,
                    name,
                    shortName,
                    status
                FROM metrics
                WHERE status = 1
                ORDER BY
                    UPPER(name),
                    UPPER(shortName)
                """;

        List<Metric> metrics =
                new ArrayList<>();

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                metrics.add(
                        mapMetric(resultSet)
                );
            }

            return metrics;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar las unidades de medida activas.",
                    exception
            );
        }
    }

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
    public Metric getById(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }

        String sql = """
                SELECT
                    id_metric,
                    name,
                    shortName,
                    status
                FROM metrics
                WHERE id_metric = ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    id
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapMetric(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar la unidad de medida.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param name valor de name requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Metric findAnyByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String sql = """
                SELECT
                    id_metric,
                    name,
                    shortName,
                    status
                FROM metrics
                WHERE UPPER(TRIM(name)) = UPPER(TRIM(?))
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    name.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapMetric(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar el nombre de la unidad de medida.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param shortName valor de shortName requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Metric findAnyByShortName(String shortName) {
        if (shortName == null
                || shortName.isBlank()) {
            return null;
        }

        String sql = """
                SELECT
                    id_metric,
                    name,
                    shortName,
                    status
                FROM metrics
                WHERE UPPER(TRIM(shortName)) = UPPER(TRIM(?))
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    shortName.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapMetric(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar la abreviatura de la unidad de medida.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param name valor de name requerido por la operación
     * @param excludedId valor de excludedId requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Metric findAnyByNameExceptId(
            String name,
            long excludedId
    ) {
        if (name == null
                || name.isBlank()
                || excludedId <= 0) {
            return null;
        }

        String sql = """
                SELECT
                    id_metric,
                    name,
                    shortName,
                    status
                FROM metrics
                WHERE UPPER(TRIM(name)) = UPPER(TRIM(?))
                  AND id_metric <> ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    name.trim()
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
                        ? mapMetric(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al validar el nombre de la unidad de medida.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param shortName valor de shortName requerido por la operación
     * @param excludedId valor de excludedId requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Metric findAnyByShortNameExceptId(
            String shortName,
            long excludedId
    ) {
        if (shortName == null
                || shortName.isBlank()
                || excludedId <= 0) {
            return null;
        }

        String sql = """
                SELECT
                    id_metric,
                    name,
                    shortName,
                    status
                FROM metrics
                WHERE UPPER(TRIM(shortName)) = UPPER(TRIM(?))
                  AND id_metric <> ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    shortName.trim()
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
                        ? mapMetric(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al validar la abreviatura de la unidad de medida.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param metric valor de metric requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean update(Metric metric) {
        if (metric == null
                || metric.getIdMetric() == null
                || metric.getIdMetric() <= 0) {
            return false;
        }

        String sql = """
                UPDATE metrics
                SET
                    name = ?,
                    shortName = ?
                WHERE id_metric = ?
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    metric.getName()
            );

            statement.setString(
                    2,
                    metric.getShortName()
            );

            statement.setLong(
                    3,
                    metric.getIdMetric()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al actualizar la unidad de medida.",
                    exception
            );
        }
    }

    /*
     * ==========================================================
     * PRODUCTOS ACTIVOS QUE UTILIZAN UNA MÉTRICA
     *
     * Esta validación es importante para mantener coherencia:
     *
     * una métrica inactiva no debe quedar asociada a productos
     * que continúen activos.
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param idMetric identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public int countActiveProductsUsingMetric(
            long idMetric
    ) {
        if (idMetric <= 0) {
            return 0;
        }

        String sql = """
                SELECT COUNT(*) AS total
                FROM products
                WHERE id_metric = ?
                  AND status = 1
                """;

        try (
                Connection connection = SQLConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(
                    1,
                    idMetric
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
                    "Error al consultar los productos activos asociados a la unidad de medida.",
                    exception
            );
        }
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param idMetric identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean hasActiveProducts(
            long idMetric
    ) {
        return countActiveProductsUsingMetric(
                idMetric
        ) > 0;
    }

    /*
     * ==========================================================
     * CAMBIAR ESTADO
     * ==========================================================
     */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idMetric identificador del registro relacionado con la operación
     * @param status estado que se utilizará en la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean changeStatus(
            long idMetric,
            int status
    ) {
        if (idMetric <= 0
                || (status != 0
                && status != 1)) {
            return false;
        }

        String sql = """
                UPDATE metrics
                SET status = ?
                WHERE id_metric = ?
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
                    idMetric
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al cambiar el estado de la unidad de medida.",
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

        /*
         * delete representa una baja lógica.
         *
         * No eliminamos físicamente la métrica porque puede formar
         * parte del historial de productos y movimientos.
         */
        return changeStatus(
                id.longValue(),
                0
        );
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
    private Metric mapMetric(
            ResultSet resultSet
    ) throws SQLException {
        Metric metric =
                new Metric();

        metric.setIdMetric(
                resultSet.getLong(
                        "id_metric"
                )
        );

        metric.setName(
                resultSet.getString(
                        "name"
                )
        );

        metric.setShortName(
                resultSet.getString(
                        "shortName"
                )
        );

        metric.setStatus(
                resultSet.getInt(
                        "status"
                )
        );

        return metric;
    }
}
