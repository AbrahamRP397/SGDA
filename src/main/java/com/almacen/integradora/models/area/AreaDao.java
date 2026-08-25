package com.almacen.integradora.models.area;

import com.almacen.integradora.templates.Dao;
import com.almacen.integradora.utils.SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos del catálogo de áreas, incluida su baja lógica.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class AreaDao implements Dao<Area, Integer> {

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param area valor de area requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean create(Area area) {
        String sql = """
                INSERT INTO areas (shortName, name, description, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, area.getShortName());
            statement.setString(2, area.getName());
            setNullableText(statement, 3, area.getDescription());
            statement.setInt(4, area.getStatus() == null ? 1 : area.getStatus());

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Error al registrar el área de destino.", exception);
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
    public List<Area> getAll() {
        String sql = """
                SELECT id_area, shortName, name, description, status
                FROM areas
                ORDER BY status DESC, UPPER(name), UPPER(shortName)
                """;

        List<Area> areas = new ArrayList<>();

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                areas.add(mapArea(resultSet));
            }

            return areas;
        } catch (SQLException exception) {
            throw new RuntimeException("Error al consultar las áreas de destino.", exception);
        }
    }

    /**
     * Obtiene los registros activos disponibles para la operación.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<Area> getActiveAreas() {
        String sql = """
                SELECT id_area, shortName, name, description, status
                FROM areas
                WHERE status = 1
                ORDER BY UPPER(name), UPPER(shortName)
                """;

        List<Area> areas = new ArrayList<>();

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                areas.add(mapArea(resultSet));
            }

            return areas;
        } catch (SQLException exception) {
            throw new RuntimeException("Error al consultar las áreas activas.", exception);
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
    public Area getById(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }

        String sql = """
                SELECT id_area, shortName, name, description, status
                FROM areas
                WHERE id_area = ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapArea(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al consultar el área de destino.", exception);
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
    public Area findAnyByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String sql = """
                SELECT id_area, shortName, name, description, status
                FROM areas
                WHERE UPPER(TRIM(name)) = UPPER(TRIM(?))
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapArea(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al consultar el nombre del área.", exception);
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
    public Area findAnyByShortName(String shortName) {
        if (shortName == null || shortName.isBlank()) {
            return null;
        }

        String sql = """
                SELECT id_area, shortName, name, description, status
                FROM areas
                WHERE UPPER(TRIM(shortName)) = UPPER(TRIM(?))
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, shortName.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapArea(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al consultar la abreviatura del área.", exception);
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
    public Area findAnyByNameExceptId(String name, long excludedId) {
        if (name == null || name.isBlank() || excludedId <= 0) {
            return null;
        }

        String sql = """
                SELECT id_area, shortName, name, description, status
                FROM areas
                WHERE UPPER(TRIM(name)) = UPPER(TRIM(?))
                  AND id_area <> ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name.trim());
            statement.setLong(2, excludedId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapArea(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al validar el nombre del área.", exception);
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
    public Area findAnyByShortNameExceptId(String shortName, long excludedId) {
        if (shortName == null || shortName.isBlank() || excludedId <= 0) {
            return null;
        }

        String sql = """
                SELECT id_area, shortName, name, description, status
                FROM areas
                WHERE UPPER(TRIM(shortName)) = UPPER(TRIM(?))
                  AND id_area <> ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, shortName.trim());
            statement.setLong(2, excludedId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapArea(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Error al validar la abreviatura del área.", exception);
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param area valor de area requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean update(Area area) {
        String sql = """
                UPDATE areas
                SET shortName = ?, name = ?, description = ?
                WHERE id_area = ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, area.getShortName());
            statement.setString(2, area.getName());
            setNullableText(statement, 3, area.getDescription());
            statement.setLong(4, area.getIdArea());

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Error al actualizar el área de destino.", exception);
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idArea identificador del registro relacionado con la operación
     * @param status estado que se utilizará en la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean changeStatus(long idArea, int status) {
        if (idArea <= 0 || (status != 0 && status != 1)) {
            return false;
        }

        String sql = """
                UPDATE areas
                SET status = ?
                WHERE id_area = ?
                """;

        try (Connection connection = SQLConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, status);
            statement.setLong(2, idArea);

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Error al cambiar el estado del área.", exception);
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
    private Area mapArea(ResultSet resultSet) throws SQLException {
        Area area = new Area();
        area.setIdArea(resultSet.getLong("id_area"));
        area.setShortName(resultSet.getString("shortName"));
        area.setName(resultSet.getString("name"));
        area.setDescription(resultSet.getString("description"));
        area.setStatus(resultSet.getInt("status"));
        return area;
    }

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
    private void setNullableText(PreparedStatement statement, int index, String value)
            throws SQLException {

        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
            return;
        }

        statement.setString(index, value.trim());
    }
}
