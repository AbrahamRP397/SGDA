package com.almacen.integradora.services;

import com.almacen.integradora.utils.SQLConnector;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/** Coordina la generación y vigencia de credenciales de acceso temporal.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class TemporaryAccessService {

    /*
     * ==========================================================
     * RESTABLECER ACCESO TEMPORAL
     * ==========================================================
     *
     * Flujo:
     *
     * 1. Abrir transacción.
     * 2. Bloquear USERS.
     * 3. Comprobar que el usuario sigue activo.
     * 4. Cambiar temporalmente la contraseña.
     * 5. Ejecutar el envío del correo.
     * 6. Si el correo falla -> ROLLBACK.
     * 7. Si el correo funciona -> COMMIT.
     *
     * De esta manera un fallo de SMTP no deja al usuario con una
     * contraseña desconocida.
     */
    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param idUser identificador del registro relacionado con la operación
     * @param temporaryPassword contraseña que se procesará de forma segura
     * @param expiration valor de expiration requerido por la operación
     * @param mailAction valor de mailAction requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    public Result resetAccess(
            long idUser,
            String temporaryPassword,
            Timestamp expiration,
            MailAction mailAction
    ) {

        if (idUser <= 0
                || temporaryPassword == null
                || temporaryPassword.isBlank()
                || expiration == null
                || mailAction == null) {

            return Result.INVALID_DATA;
        }

        Connection connection = null;

        try {
            connection =
                    SQLConnector.getConnection();

            disableParallelDml(
                    connection
            );

            connection.setAutoCommit(
                    false
            );

            /*
             * ======================================================
             * BLOQUEAR USUARIO
             * ======================================================
             *
             * Conservamos el mismo orden de bloqueo utilizado por
             * los demás procesos de seguridad:
             *
             * USERS
             *   ↓
             * otras tablas relacionadas
             */
            UserState userState =
                    lockUser(
                            connection,
                            idUser
                    );

            if (userState == null) {

                rollbackQuietly(
                        connection
                );

                return Result.USER_NOT_FOUND;
            }

            if (userState.status() != 1) {

                rollbackQuietly(
                        connection
                );

                return Result.USER_INACTIVE;
            }

            String passwordHash =
                    BCrypt.hashpw(
                            temporaryPassword,
                            BCrypt.gensalt()
                    );

            if (!updateTemporaryPassword(
                    connection,
                    idUser,
                    passwordHash,
                    expiration
            )) {

                rollbackQuietly(
                        connection
                );

                return Result.UPDATE_FAILED;
            }

            /*
             * ======================================================
             * INVALIDAR TOKENS DE RECUPERACIÓN
             * ======================================================
             *
             * Al generar un nuevo acceso temporal, ningún enlace de
             * recuperación creado anteriormente debe seguir siendo
             * válido.
             *
             * El orden de bloqueo se mantiene:
             *
             * USERS
             *   ↓
             * PASSWORD_RESET_TOKENS
             *
             * Cero filas modificadas es completamente válido porque
             * el usuario puede no tener recuperaciones pendientes.
             */
            invalidateRecoveryTokens(
                    connection,
                    idUser
            );

            /*
             * ======================================================
             * ENVIAR CORREO ANTES DEL COMMIT
             * ======================================================
             *
             * Si el correo falla, hacemos ROLLBACK de TODA la
             * operación:
             *
             * - se restaura la contraseña anterior;
             * - se restaura el estado anterior de los tokens.
             *
             * Por tanto el restablecimiento se considera totalmente
             * cancelado.
             */
            try {
                mailAction.send();

            } catch (RuntimeException exception) {

                rollbackQuietly(
                        connection
                );

                return Result.EMAIL_FAILED;
            }

            connection.commit();

            return Result.SUCCESS;

        } catch (SQLException exception) {

            rollbackQuietly(
                    connection
            );

            throw new RuntimeException(
                    "Error al restablecer el acceso temporal de forma transaccional.",
                    exception
            );

        } catch (RuntimeException exception) {

            rollbackQuietly(
                    connection
            );

            throw exception;

        } finally {

            closeConnection(
                    connection
            );
        }
    }

    /*
     * ==========================================================
     * BLOQUEAR USUARIO
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param idUser identificador del registro relacionado con la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private UserState lockUser(
            Connection connection,
            long idUser
    ) throws SQLException {

        String sql = """
                SELECT ID_USER,
                       STATUS
                FROM USERS
                WHERE ID_USER = ?
                FOR UPDATE
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    idUser
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return null;
                }

                return new UserState(
                        resultSet.getLong(
                                "ID_USER"
                        ),
                        resultSet.getInt(
                                "STATUS"
                        )
                );
            }
        }
    }

    /*
     * ==========================================================
     * ACTUALIZAR CONTRASEÑA TEMPORAL
     * ==========================================================
     */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idUser identificador del registro relacionado con la operación
     * @param passwordHash contraseña que se procesará de forma segura
     * @param expiration valor de expiration requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean updateTemporaryPassword(
            Connection connection,
            long idUser,
            String passwordHash,
            Timestamp expiration
    ) throws SQLException {

        String sql = """
                UPDATE USERS
                SET PASSWORD = ?,
                    MUST_CHANGE_PASSWORD = 1,
                    TEMPORARY_PASSWORD_EXPIRATION = ?
                WHERE ID_USER = ?
                  AND STATUS = 1
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    passwordHash
            );

            statement.setTimestamp(
                    2,
                    expiration
            );

            statement.setLong(
                    3,
                    idUser
            );

            return statement.executeUpdate()
                    == 1;
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idUser identificador del registro relacionado con la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void invalidateRecoveryTokens(
            Connection connection,
            long idUser
    ) throws SQLException {

        if (connection == null
                || idUser <= 0) {

            throw new SQLException(
                    "No fue posible invalidar los tokens de recuperación."
            );
        }

        String sql = """
            UPDATE PASSWORD_RESET_TOKENS
            SET USED = 1
            WHERE ID_USER = ?
              AND USED = 0
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    idUser
            );

            /*
             * No comprobamos el número de filas.
             *
             * Cero filas significa simplemente que el usuario
             * no tenía enlaces de recuperación pendientes.
             */
            statement.executeUpdate();
        }
    }

    /*
     * ==========================================================
     * ORACLE
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void disableParallelDml(
            Connection connection
    ) throws SQLException {

        if (connection == null) {

            throw new SQLException(
                    "La conexión para restablecer el acceso no es válida."
            );
        }

        try (
                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(
                    "ALTER SESSION DISABLE PARALLEL DML"
            );
        }
    }

    /*
     * ==========================================================
     * TRANSACCIÓN
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void rollbackQuietly(
            Connection connection
    ) {

        if (connection == null) {
            return;
        }

        try {

            if (!connection.getAutoCommit()) {
                connection.rollback();
            }

        } catch (SQLException ignored) {
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void closeConnection(
            Connection connection
    ) {

        if (connection == null) {
            return;
        }

        try {

            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(
                        true
                );
            }

        } catch (SQLException ignored) {
        }

        try {
            connection.close();

        } catch (SQLException ignored) {
        }
    }

    /*
     * ==========================================================
     * CALLBACK DE CORREO
     * ==========================================================
     */

    @FunctionalInterface
    public interface MailAction {

        void send();
    }

    /*
     * ==========================================================
     * DATOS INTERNOS
     * ==========================================================
     */

    private record UserState(
            long idUser,
            int status
    ) {
    }

    /*
     * ==========================================================
     * RESULTADO
     * ==========================================================
     */

    public enum Result {
        SUCCESS,
        INVALID_DATA,
        USER_NOT_FOUND,
        USER_INACTIVE,
        UPDATE_FAILED,
        EMAIL_FAILED
    }
}
