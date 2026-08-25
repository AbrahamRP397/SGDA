package com.almacen.integradora.models.passwordreset;

import com.almacen.integradora.utils.SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Administra tokens de recuperación, vigencia e invalidación.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class PasswordResetTokenDao{

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param idUser identificador del registro relacionado con la operación
     * @param token token utilizado para validar la solicitud
     * @param expiration valor de expiration requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean create(
            long idUser,
            String token,
            LocalDateTime expiration
    ){
        if(idUser<=0
                ||token==null
                ||token.isBlank()
                ||expiration==null){
            return false;
        }

        String sql="""
                INSERT INTO PASSWORD_RESET_TOKENS(
                    ID_USER,
                    TOKEN,
                    EXPIRATION,
                    USED
                )
                VALUES(?,?,?,0)
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setLong(
                    1,
                    idUser
            );

            statement.setString(
                    2,
                    token.trim()
            );

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(expiration)
            );

            return statement.executeUpdate()==1;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al crear el token de recuperación.",
                    exception
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param token token utilizado para validar la solicitud
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PasswordResetToken findValidToken(
            String token
    ){
        if(token==null||token.isBlank()){
            return null;
        }

        String sql="""
                SELECT
                    prt.ID_TOKEN,
                    prt.ID_USER,
                    prt.TOKEN,
                    prt.EXPIRATION,
                    prt.USED,
                    prt.CREATED_AT
                FROM PASSWORD_RESET_TOKENS prt
                INNER JOIN USERS u
                    ON prt.ID_USER=u.ID_USER
                WHERE prt.TOKEN=?
                  AND prt.USED=0
                  AND prt.EXPIRATION>CURRENT_TIMESTAMP
                  AND u.STATUS=1
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(
                    1,
                    token.trim()
            );

            try(ResultSet resultSet=
                        statement.executeQuery()){

                if(!resultSet.next()){
                    return null;
                }

                return mapToken(resultSet);
            }

        }catch(SQLException exception){
            throw databaseException(
                    "Error al consultar el token de recuperación.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idToken identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean markAsUsed(
            long idToken
    ){
        if(idToken<=0){
            return false;
        }

        String sql="""
                UPDATE PASSWORD_RESET_TOKENS
                SET USED=1
                WHERE ID_TOKEN=?
                  AND USED=0
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setLong(
                    1,
                    idToken
            );

            return statement.executeUpdate()==1;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al marcar el token como utilizado.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param token token utilizado para validar la solicitud
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean invalidateToken(
            String token
    ){
        if(token==null
                ||token.isBlank()){
            return false;
        }

        String sql="""
            UPDATE PASSWORD_RESET_TOKENS
            SET USED=1
            WHERE TOKEN=?
              AND USED=0
            """;

        try(
                Connection connection=
                        SQLConnector.getConnection();

                PreparedStatement statement=
                        connection.prepareStatement(sql)
        ){
            statement.setString(
                    1,
                    token.trim()
            );

            /*
             * Si ya estaba invalidado, cero filas no representa
             * un problema para este flujo.
             */
            statement.executeUpdate();

            return true;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al invalidar el token de recuperación.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idUser identificador del registro relacionado con la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean invalidateUserTokens(
            long idUser
    ){
        if(idUser<=0){
            return false;
        }

        String sql="""
                UPDATE PASSWORD_RESET_TOKENS
                SET USED=1
                WHERE ID_USER=?
                  AND USED=0
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setLong(
                    1,
                    idUser
            );

            /*
             * Cero filas NO es un error.
             *
             * Puede significar simplemente que el usuario no tenía
             * ningún token pendiente.
             */
            statement.executeUpdate();

            return true;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al invalidar los tokens de recuperación del usuario.",
                    exception
            );
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
    private PasswordResetToken mapToken(
            ResultSet resultSet
    )throws SQLException{
        PasswordResetToken token=
                new PasswordResetToken();

        token.setIdToken(
                resultSet.getLong(
                        "ID_TOKEN"
                )
        );

        token.setIdUser(
                resultSet.getLong(
                        "ID_USER"
                )
        );

        token.setToken(
                resultSet.getString(
                        "TOKEN"
                )
        );

        Timestamp expiration=
                resultSet.getTimestamp(
                        "EXPIRATION"
                );

        if(expiration!=null){
            token.setExpiration(
                    expiration.toLocalDateTime()
            );
        }

        token.setUsed(
                resultSet.getInt(
                        "USED"
                )==1
        );

        Timestamp createdAt=
                resultSet.getTimestamp(
                        "CREATED_AT"
                );

        if(createdAt!=null){
            token.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return token;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param message valor de message requerido por la operación
     * @param exception valor de exception requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private RuntimeException databaseException(
            String message,
            SQLException exception
    ){
        return new RuntimeException(
                message,
                exception
        );
    }
}
