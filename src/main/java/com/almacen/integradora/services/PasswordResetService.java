package com.almacen.integradora.services;

import com.almacen.integradora.utils.PasswordPolicy;
import com.almacen.integradora.utils.SQLConnector;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Orquesta el flujo seguro de recuperación y restablecimiento de contraseñas.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class PasswordResetService {

    /*
     * ==========================================================
     * CREAR TOKEN DE RECUPERACIÓN
     * ==========================================================
     *
     * Toda la operación ocurre dentro de una sola transacción:
     *
     * 1. Bloquear usuario.
     * 2. Comprobar que sigue activo.
     * 3. Invalidar tokens anteriores.
     * 4. Crear nuevo token.
     * 5. COMMIT.
     *
     * UserDao utiliza igualmente SELECT ... FOR UPDATE cuando
     * desactiva un usuario.
     *
     * Por lo tanto recuperación y desactivación quedan
     * serializadas sobre la misma fila de USERS.
     */
    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param idUser identificador del registro relacionado con la operación
     * @param token token utilizado para validar la solicitud
     * @param expiration valor de expiration requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    public boolean createRecoveryToken(
            long idUser,
            String token,
            LocalDateTime expiration
    ) {

        if (idUser <= 0
                || token == null
                || token.isBlank()
                || expiration == null) {

            return false;
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
             * Bloqueamos la fila del usuario.
             *
             * Si una desactivación está ocurriendo simultáneamente,
             * esta operación esperará a que termine.
             */
            if (!lockActiveUser(
                    connection,
                    idUser
            )) {

                rollbackQuietly(
                        connection
                );

                return false;
            }

            /*
             * Todos los enlaces anteriores dejan de ser válidos.
             *
             * Cero filas modificadas es completamente válido.
             */
            invalidatePendingTokens(
                    connection,
                    idUser
            );

            if (!insertRecoveryToken(
                    connection,
                    idUser,
                    token.trim(),
                    expiration
            )) {

                rollbackQuietly(
                        connection
                );

                return false;
            }

            connection.commit();

            return true;

        } catch (SQLException exception) {

            rollbackQuietly(
                    connection
            );

            throw new RuntimeException(
                    "Error al crear el token de recuperación de forma transaccional.",
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
     * RESTABLECER CONTRASEÑA
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param token token utilizado para validar la solicitud
     * @param newPassword contraseña que se procesará de forma segura
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    public ResetResult resetPassword(
            String token,
            String newPassword
    ) {

        if (token == null || token.isBlank()) {
            return ResetResult.INVALID_TOKEN;
        }

        if (newPassword == null
                || newPassword.isBlank()
                || !PasswordPolicy.isValid(newPassword)) {

            return ResetResult.INVALID_PASSWORD;
        }

        String normalizedToken =
                token.trim();

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
             * ==========================================================
             * 1. LOCALIZAR USUARIO DEL TOKEN SIN BLOQUEAR
             * ==========================================================
             *
             * Esta primera consulta solamente nos permite conocer qué
             * usuario pertenece al token.
             *
             * NO utilizamos FOR UPDATE aquí porque la regla global de
             * bloqueo del sistema será siempre:
             *
             * USERS
             *   ↓
             * PASSWORD_RESET_TOKENS
             *
             * La validez definitiva del token se comprobará nuevamente
             * después de bloquear al usuario.
             */
            Long idUser =
                    findTokenUserId(
                            connection,
                            normalizedToken
                    );

            if (idUser == null
                    || idUser <= 0) {

                rollbackQuietly(
                        connection
                );

                return ResetResult.INVALID_TOKEN;
            }

            /*
             * ==========================================================
             * 2. BLOQUEAR USUARIO
             * ==========================================================
             *
             * ESTE ES SIEMPRE EL PRIMER BLOQUEO DE FILA.
             *
             * De esta forma utilizamos el mismo orden que:
             *
             * - createRecoveryToken();
             * - updateStatusAndInvalidateRecoveryTokens().
             *
             * Esto evita el ciclo:
             *
             * TOKEN -> USERS
             * USERS -> TOKEN
             *
             * que podía producir un deadlock.
             */
            if (!lockActiveUser(
                    connection,
                    idUser
            )) {

                rollbackQuietly(
                        connection
                );

                return ResetResult.USER_NOT_FOUND;
            }

            /*
             * ==========================================================
             * 3. BLOQUEAR Y REVALIDAR TOKEN
             * ==========================================================
             *
             * Ahora que USERS ya está bloqueado podemos bloquear el token.
             *
             * Es indispensable volver a validar:
             *
             * - que siga existiendo;
             * - que siga sin utilizarse;
             * - que no haya expirado;
             * - que siga perteneciendo al mismo usuario.
             *
             * Otra petición pudo modificar el token entre la consulta
             * inicial y este punto.
             */
            TokenData tokenData =
                    lockAndValidateToken(
                            connection,
                            normalizedToken
                    );

            if (tokenData == null
                    || tokenData.idUser() != idUser) {

                rollbackQuietly(
                        connection
                );

                return ResetResult.INVALID_TOKEN;
            }

            /*
             * ==========================================================
             * 4. CAMBIAR CONTRASEÑA
             * ==========================================================
             */
            String passwordHash =
                    BCrypt.hashpw(
                            newPassword,
                            BCrypt.gensalt()
                    );

            if (!updatePassword(
                    connection,
                    idUser,
                    passwordHash
            )) {

                rollbackQuietly(
                        connection
                );

                return ResetResult.USER_NOT_FOUND;
            }

            /*
             * ==========================================================
             * 5. INVALIDAR TODOS LOS ENLACES DEL USUARIO
             * ==========================================================
             *
             * Incluye el token que acaba de utilizarse y cualquier otro
             * enlace pendiente.
             */
            if (!invalidateAllUserTokens(
                    connection,
                    idUser
            )) {

                rollbackQuietly(
                        connection
                );

                return ResetResult.TOKEN_INVALIDATION_FAILED;
            }

            /*
             * ==========================================================
             * 6. COMMIT
             * ==========================================================
             */
            connection.commit();

            return ResetResult.SUCCESS;

        } catch (SQLException exception) {

            rollbackQuietly(
                    connection
            );

            throw new RuntimeException(
                    "Error al restablecer la contraseña de forma transaccional.",
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

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param token token utilizado para validar la solicitud
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private Long findTokenUserId(
            Connection connection,
            String token
    ) throws SQLException {

        if (connection == null
                || token == null
                || token.isBlank()) {

            return null;
        }

        /*
         * ==========================================================
         * CONSULTA PRELIMINAR SIN BLOQUEO
         * ==========================================================
         *
         * Su única finalidad es conocer ID_USER para poder respetar
         * el orden global de bloqueos:
         *
         * USERS -> PASSWORD_RESET_TOKENS
         *
         * No consideramos esta consulta como validación definitiva.
         * lockAndValidateToken() volverá a comprobar todo después de
         * obtener el bloqueo de USERS.
         */
        String sql = """
            SELECT ID_USER
            FROM PASSWORD_RESET_TOKENS
            WHERE TOKEN = ?
              AND USED = 0
              AND EXPIRATION > CURRENT_TIMESTAMP
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    token
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return null;
                }

                long idUser =
                        resultSet.getLong(
                                "ID_USER"
                        );

                if (resultSet.wasNull()
                        || idUser <= 0) {

                    return null;
                }

                return idUser;
            }
        }
    }

    /*
     * ==========================================================
     * BLOQUEAR USUARIO ACTIVO
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
    private boolean lockActiveUser(
            Connection connection,
            long idUser
    ) throws SQLException {

        if (connection == null
                || idUser <= 0) {

            return false;
        }

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
                    return false;
                }

                return resultSet.getInt(
                        "STATUS"
                ) == 1;
            }
        }
    }

    /*
     * ==========================================================
     * INVALIDAR TOKENS PENDIENTES
     * ==========================================================
     */

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
    private void invalidatePendingTokens(
            Connection connection,
            long idUser
    ) throws SQLException {

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
             * No exigimos filas modificadas.
             *
             * Un usuario puede no tener ningún token anterior.
             */
            statement.executeUpdate();
        }
    }

    /*
     * ==========================================================
     * INSERTAR TOKEN
     * ==========================================================
     */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param connection conexión JDBC activa
     * @param idUser identificador del registro relacionado con la operación
     * @param token token utilizado para validar la solicitud
     * @param expiration valor de expiration requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean insertRecoveryToken(
            Connection connection,
            long idUser,
            String token,
            LocalDateTime expiration
    ) throws SQLException {

        if (connection == null
                || idUser <= 0
                || token == null
                || token.isBlank()
                || expiration == null) {

            return false;
        }

        String sql = """
                INSERT INTO PASSWORD_RESET_TOKENS (
                    ID_USER,
                    TOKEN,
                    EXPIRATION,
                    USED
                )
                VALUES (?, ?, ?, 0)
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    idUser
            );

            statement.setString(
                    2,
                    token
            );

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(
                            expiration
                    )
            );

            return statement.executeUpdate()
                    == 1;
        }
    }

    /*
     * ==========================================================
     * BLOQUEAR Y VALIDAR TOKEN
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param connection conexión JDBC activa
     * @param token token utilizado para validar la solicitud
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private TokenData lockAndValidateToken(
            Connection connection,
            String token
    ) throws SQLException {

        String sql = """
                SELECT
                    ID_TOKEN,
                    ID_USER
                FROM PASSWORD_RESET_TOKENS
                WHERE TOKEN = ?
                  AND USED = 0
                  AND EXPIRATION > CURRENT_TIMESTAMP
                FOR UPDATE
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    token
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    return null;
                }

                long idToken =
                        resultSet.getLong(
                                "ID_TOKEN"
                        );

                long idUser =
                        resultSet.getLong(
                                "ID_USER"
                        );

                if (idToken <= 0
                        || idUser <= 0) {

                    return null;
                }

                return new TokenData(
                        idToken,
                        idUser
                );
            }
        }
    }

    /*
     * ==========================================================
     * ACTUALIZAR CONTRASEÑA
     * ==========================================================
     */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param connection conexión JDBC activa
     * @param idUser identificador del registro relacionado con la operación
     * @param passwordHash contraseña que se procesará de forma segura
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean updatePassword(
            Connection connection,
            long idUser,
            String passwordHash
    ) throws SQLException {

        if (idUser <= 0
                || passwordHash == null
                || passwordHash.isBlank()) {

            return false;
        }

        String sql = """
                UPDATE USERS
                SET PASSWORD = ?,
                    MUST_CHANGE_PASSWORD = 0,
                    TEMPORARY_PASSWORD_EXPIRATION = NULL
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

            statement.setLong(
                    2,
                    idUser
            );

            return statement.executeUpdate()
                    == 1;
        }
    }

    /*
     * ==========================================================
     * INVALIDAR TODOS LOS TOKENS
     * ==========================================================
     */

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
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
    private boolean invalidateAllUserTokens(
            Connection connection,
            long idUser
    ) throws SQLException {

        if (idUser <= 0) {
            return false;
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
             * Al menos el token que acabamos de bloquear debe
             * encontrarse todavía pendiente.
             */
            return statement.executeUpdate()
                    > 0;
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
                    "La conexión para desactivar Parallel DML no es válida."
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
     * DATOS INTERNOS
     * ==========================================================
     */

    private record TokenData(
            long idToken,
            long idUser
    ) {
    }

    public enum ResetResult {
        SUCCESS,
        INVALID_TOKEN,
        INVALID_PASSWORD,
        USER_NOT_FOUND,
        TOKEN_INVALIDATION_FAILED
    }
}
