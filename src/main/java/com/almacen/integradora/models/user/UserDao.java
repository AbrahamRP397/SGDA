package com.almacen.integradora.models.user;

import com.almacen.integradora.templates.Dao;
import com.almacen.integradora.utils.SQLConnector;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Persistencia de usuarios, autenticación, estados y credenciales temporales.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class UserDao implements Dao<User,Integer>{

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean create(User user){
        if(user==null
                ||user.getPassword()==null
                ||user.getPassword().isBlank()){
            return false;
        }

        String sql="""
                INSERT INTO USERS(
                    name,
                    surname,
                    lastname,
                    phone,
                    email,
                    password,
                    role,
                    status,
                    must_change_password,
                    temporary_password_expiration
                )
                VALUES(?,?,?,?,?,?,?,?,?,?)
                """;

        String hash=BCrypt.hashpw(
                user.getPassword(),
                BCrypt.gensalt()
        );

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(1,user.getName());
            statement.setString(2,user.getSurname());
            statement.setString(3,user.getLastname());
            statement.setString(4,user.getPhone());
            statement.setString(5,user.getEmail());
            statement.setString(6,hash);
            statement.setString(7,user.getRole());
            statement.setInt(
                    8,
                    user.getStatus()==null?1:user.getStatus()
            );
            statement.setInt(
                    9,
                    user.getMustChangePassword()==null
                            ?0
                            :user.getMustChangePassword()
            );
            statement.setTimestamp(
                    10,
                    user.getTemporaryPasswordExpiration()
            );

            return statement.executeUpdate()==1;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al registrar el usuario.",
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
    public List<User> getAll(){
        String sql="""
                SELECT
                    id_user,
                    name,
                    surname,
                    lastname,
                    phone,
                    email,
                    role,
                    status,
                    must_change_password,
                    temporary_password_expiration
                FROM USERS
                ORDER BY
                    status DESC,
                    UPPER(name),
                    UPPER(surname),
                    UPPER(lastname),
                    id_user
                """;

        List<User> users=new ArrayList<>();

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql);
                ResultSet resultSet=statement.executeQuery()
        ){
            while(resultSet.next()){
                users.add(
                        mapUser(
                                resultSet,
                                false
                        )
                );
            }

            return users;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al consultar los usuarios.",
                    exception
            );
        }
    }

    /**
     * Consulta un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return usuario encontrado o {@code null} si el identificador es inválido
     *         o no existe un registro asociado
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public User getById(Integer id){
        if(id==null||id<=0){
            return null;
        }

        String sql="""
                SELECT
                    id_user,
                    name,
                    surname,
                    lastname,
                    phone,
                    email,
                    role,
                    status,
                    must_change_password,
                    temporary_password_expiration
                FROM USERS
                WHERE id_user=?
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setInt(1,id);

            try(ResultSet resultSet=statement.executeQuery()){
                return resultSet.next()
                        ?mapUser(resultSet,false)
                        :null;
            }

        }catch(SQLException exception){
            throw databaseException(
                    "Error al consultar el usuario.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public boolean update(
            User user
    ){
        if(user==null
                ||user.getId()==null
                ||user.getId()<=0
                ||user.getEmail()==null
                ||user.getEmail().isBlank()){
            return false;
        }

        Connection connection=null;

        try{
            connection=
                    SQLConnector.getConnection();

            disableParallelDml(
                    connection
            );

            connection.setAutoCommit(
                    false
            );

            /*
             * ======================================================
             * BLOQUEAR USUARIO Y OBTENER CORREO ACTUAL
             * ======================================================
             *
             * Primero bloqueamos USERS para mantener el orden global:
             *
             * USERS
             *   ↓
             * PASSWORD_RESET_TOKENS
             *
             * Además obtenemos el correo actualmente almacenado para
             * saber si realmente fue modificado.
             */
            String currentEmailSql="""
                SELECT email
                FROM USERS
                WHERE id_user=?
                FOR UPDATE
                """;

            String currentEmail;

            try(
                    PreparedStatement statement=
                            connection.prepareStatement(
                                    currentEmailSql
                            )
            ){
                statement.setLong(
                        1,
                        user.getId()
                );

                try(
                        ResultSet resultSet=
                                statement.executeQuery()
                ){
                    if(!resultSet.next()){
                        rollbackQuietly(
                                connection
                        );

                        return false;
                    }

                    currentEmail=
                            resultSet.getString(
                                    "email"
                            );
                }
            }

            String newEmail=
                    user.getEmail()
                            .trim();

            boolean emailChanged=
                    currentEmail==null
                            ||!currentEmail.trim()
                            .equalsIgnoreCase(
                                    newEmail
                            );

            /*
             * ======================================================
             * ACTUALIZAR USUARIO
             * ======================================================
             */
            String updateSql="""
                UPDATE USERS
                SET name=?,
                    surname=?,
                    lastname=?,
                    phone=?,
                    email=?,
                    role=?
                WHERE id_user=?
                """;

            try(
                    PreparedStatement statement=
                            connection.prepareStatement(
                                    updateSql
                            )
            ){
                statement.setString(
                        1,
                        user.getName()
                );

                statement.setString(
                        2,
                        user.getSurname()
                );

                statement.setString(
                        3,
                        user.getLastname()
                );

                statement.setString(
                        4,
                        user.getPhone()
                );

                statement.setString(
                        5,
                        newEmail
                );

                statement.setString(
                        6,
                        user.getRole()
                );

                statement.setLong(
                        7,
                        user.getId()
                );

                if(statement.executeUpdate()!=1){
                    rollbackQuietly(
                            connection
                    );

                    return false;
                }
            }

            /*
             * ======================================================
             * INVALIDAR RECUPERACIONES SI CAMBIÓ EL CORREO
             * ======================================================
             *
             * Los enlaces de recuperación anteriores pudieron haber
             * sido enviados al correo que acabamos de reemplazar.
             *
             * Por seguridad dejan de ser válidos inmediatamente.
             */
            if(emailChanged){

                String tokenSql="""
                    UPDATE PASSWORD_RESET_TOKENS
                    SET USED=1
                    WHERE ID_USER=?
                      AND USED=0
                    """;

                try(
                        PreparedStatement statement=
                                connection.prepareStatement(
                                        tokenSql
                                )
                ){
                    statement.setLong(
                            1,
                            user.getId()
                    );

                    /*
                     * Cero filas es válido:
                     * puede no haber enlaces pendientes.
                     */
                    statement.executeUpdate();
                }
            }

            connection.commit();

            return true;

        }catch(SQLException exception){

            rollbackQuietly(
                    connection
            );

            throw databaseException(
                    "Error al actualizar el usuario.",
                    exception
            );

        }catch(RuntimeException exception){

            rollbackQuietly(
                    connection
            );

            throw exception;

        }finally{

            closeConnection(
                    connection
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
    public boolean delete(Integer id){
        if(id==null||id<=0){
            return false;
        }

        String sql="""
                DELETE FROM USERS
                WHERE id_user=?
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setInt(1,id);

            return statement.executeUpdate()==1;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al eliminar el usuario.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idUser identificador del registro relacionado con la operación
     * @param status estado que se utilizará en la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean updateStatusAndInvalidateRecoveryTokens(
            int idUser,
            int status
    ){
        if(idUser<=0||(status!=0&&status!=1)){
            return false;
        }

        /*
         * Para activar no es necesario tocar tokens.
         *
         * Los tokens antiguos que fueron invalidados al desactivar
         * permanecen USED = 1 y nunca vuelven a ser utilizables.
         */
        if(status==1){
            return updateStatus(
                    idUser,
                    1
            );
        }

        Connection connection=null;

        try{
            connection=SQLConnector.getConnection();

            /*
             * Evita problemas ORA-12838 / ORA-12839 al modificar
             * USERS y PASSWORD_RESET_TOKENS dentro de la misma
             * transacción.
             */
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
             * El FOR UPDATE evita que el estado del mismo usuario sea
             * modificado simultáneamente mientras realizamos la baja.
             */
            String userSql="""
                SELECT status
                FROM USERS
                WHERE id_user=?
                FOR UPDATE
                """;

            Integer currentStatus=null;

            try(
                    PreparedStatement statement=
                            connection.prepareStatement(
                                    userSql
                            )
            ){
                statement.setInt(
                        1,
                        idUser
                );

                try(
                        ResultSet resultSet=
                                statement.executeQuery()
                ){
                    if(!resultSet.next()){
                        rollbackQuietly(
                                connection
                        );

                        return false;
                    }

                    currentStatus=
                            resultSet.getInt(
                                    "status"
                            );
                }
            }

            /*
             * Si por una condición de carrera otro proceso ya había
             * desactivado al usuario, consideramos la operación válida.
             *
             * Aun así invalidamos los tokens pendientes para mantener
             * la invariancia de seguridad.
             */
            String tokenSql="""
                UPDATE PASSWORD_RESET_TOKENS
                SET USED=1
                WHERE ID_USER=?
                  AND USED=0
                """;

            try(
                    PreparedStatement statement=
                            connection.prepareStatement(
                                    tokenSql
                            )
            ){
                statement.setInt(
                        1,
                        idUser
                );

                /*
                 * Cero filas actualizadas es válido:
                 * simplemente significa que no existían tokens
                 * pendientes.
                 */
                statement.executeUpdate();
            }

            if(currentStatus!=0){
                String statusSql="""
                    UPDATE USERS
                    SET status=0
                    WHERE id_user=?
                    """;

                try(
                        PreparedStatement statement=
                                connection.prepareStatement(
                                        statusSql
                                )
                ){
                    statement.setInt(
                            1,
                            idUser
                    );

                    if(statement.executeUpdate()!=1){
                        rollbackQuietly(
                                connection
                        );

                        return false;
                    }
                }
            }

            connection.commit();

            return true;

        }catch(SQLException exception){
            rollbackQuietly(
                    connection
            );

            throw databaseException(
                    "Error al cambiar el estado del usuario e invalidar sus tokens.",
                    exception
            );

        }finally{
            closeConnection(
                    connection
            );
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param email dirección de correo asociada a la operación
     * @param password contraseña que se procesará de forma segura
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public User login(
            String email,
            String password
    ){
        if(email==null
                ||email.isBlank()
                ||password==null
                ||password.isBlank()){
            return null;
        }

        String sql="""
                SELECT
                    id_user,
                    name,
                    surname,
                    lastname,
                    phone,
                    email,
                    password,
                    role,
                    status,
                    must_change_password,
                    temporary_password_expiration
                FROM USERS
                WHERE LOWER(email)=LOWER(?)
                  AND status=1
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(
                    1,
                    email.trim()
            );

            try(ResultSet resultSet=statement.executeQuery()){
                if(!resultSet.next()){
                    return null;
                }

                User user=mapUser(
                        resultSet,
                        true
                );

                String storedPassword=
                        user.getPassword();

                if(storedPassword==null
                        ||storedPassword.isBlank()){
                    return null;
                }

                final boolean validPassword;

                try{
                    validPassword=BCrypt.checkpw(
                            password,
                            storedPassword
                    );
                }catch(IllegalArgumentException exception){
                    /*
                     * Un hash BCrypt corrupto en la base de datos
                     * es un problema del servidor, no credenciales
                     * incorrectas del usuario.
                     */
                    throw new IllegalStateException(
                            "La contraseña almacenada del usuario no tiene un formato BCrypt válido.",
                            exception
                    );
                }

                if(!validPassword){
                    return null;
                }

                return user;
            }

        }catch(SQLException exception){
            throw databaseException(
                    "Error al iniciar sesión.",
                    exception
            );
        }
    }

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
    public User findByEmail(String email){
        if(email==null||email.isBlank()){
            return null;
        }

        String sql="""
                SELECT
                    id_user,
                    name,
                    surname,
                    lastname,
                    phone,
                    email,
                    role,
                    status,
                    must_change_password,
                    temporary_password_expiration
                FROM USERS
                WHERE LOWER(email)=LOWER(?)
                  AND status=1
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(
                    1,
                    email.trim()
            );

            try(ResultSet resultSet=statement.executeQuery()){
                return resultSet.next()
                        ?mapUser(resultSet,false)
                        :null;
            }

        }catch(SQLException exception){
            throw databaseException(
                    "Error al consultar el usuario por correo.",
                    exception
            );
        }
    }

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
    public User findAnyByEmail(String email){
        if(email==null||email.isBlank()){
            return null;
        }

        String sql="""
                SELECT
                    id_user,
                    name,
                    surname,
                    lastname,
                    phone,
                    email,
                    role,
                    status,
                    must_change_password,
                    temporary_password_expiration
                FROM USERS
                WHERE LOWER(email)=LOWER(?)
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(
                    1,
                    email.trim()
            );

            try(ResultSet resultSet=statement.executeQuery()){
                return resultSet.next()
                        ?mapUser(resultSet,false)
                        :null;
            }

        }catch(SQLException exception){
            throw databaseException(
                    "Error al consultar el usuario por correo.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param id identificador del registro relacionado con la operación
     * @param status estado que se utilizará en la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean updateStatus(
            int id,
            int status
    ){
        if(id<=0||(status!=0&&status!=1)){
            return false;
        }

        String sql="""
                UPDATE USERS
                SET status=?
                WHERE id_user=?
                """;

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setInt(1,status);
            statement.setInt(2,id);

            return statement.executeUpdate()==1;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al cambiar el estado del usuario.",
                    exception
            );
        }
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idUser identificador del registro relacionado con la operación
     * @param temporaryPassword contraseña que se procesará de forma segura
     * @param expiration valor de expiration requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean setTemporaryPassword(
            long idUser,
            String temporaryPassword,
            Timestamp expiration
    ){
        if(idUser<=0
                ||temporaryPassword==null
                ||temporaryPassword.isBlank()
                ||expiration==null){
            return false;
        }

        String sql="""
                UPDATE USERS
                SET password=?,
                    must_change_password=1,
                    temporary_password_expiration=?
                WHERE id_user=?
                  AND status=1
                """;

        String hash=BCrypt.hashpw(
                temporaryPassword,
                BCrypt.gensalt()
        );

        try(
                Connection connection=SQLConnector.getConnection();
                PreparedStatement statement=connection.prepareStatement(sql)
        ){
            statement.setString(
                    1,
                    hash
            );

            statement.setTimestamp(
                    2,
                    expiration
            );

            statement.setLong(
                    3,
                    idUser
            );

            return statement.executeUpdate()==1;

        }catch(SQLException exception){
            throw databaseException(
                    "Error al generar el acceso temporal del usuario.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param idUser identificador del registro relacionado con la operación
     * @param newPassword contraseña que se procesará de forma segura
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean updatePasswordById(
            long idUser,
            String newPassword
    ){
        if(idUser<=0
                ||newPassword==null
                ||newPassword.isBlank()){
            return false;
        }

        Connection connection=null;

        try{
            connection=
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
             * Conservamos el orden global:
             *
             * USERS
             *   ↓
             * PASSWORD_RESET_TOKENS
             */
            String lockSql="""
                SELECT status
                FROM USERS
                WHERE id_user=?
                FOR UPDATE
                """;

            try(
                    PreparedStatement statement=
                            connection.prepareStatement(
                                    lockSql
                            )
            ){
                statement.setLong(
                        1,
                        idUser
                );

                try(
                        ResultSet resultSet=
                                statement.executeQuery()
                ){
                    if(!resultSet.next()){
                        rollbackQuietly(
                                connection
                        );

                        return false;
                    }

                    if(resultSet.getInt(
                            "status"
                    )!=1){
                        rollbackQuietly(
                                connection
                        );

                        return false;
                    }
                }
            }

            String hash=
                    BCrypt.hashpw(
                            newPassword,
                            BCrypt.gensalt()
                    );

            /*
             * ======================================================
             * ACTUALIZAR CONTRASEÑA
             * ======================================================
             */
            String passwordSql="""
                UPDATE USERS
                SET password=?,
                    must_change_password=0,
                    temporary_password_expiration=NULL
                WHERE id_user=?
                  AND status=1
                """;

            try(
                    PreparedStatement statement=
                            connection.prepareStatement(
                                    passwordSql
                            )
            ){
                statement.setString(
                        1,
                        hash
                );

                statement.setLong(
                        2,
                        idUser
                );

                if(statement.executeUpdate()!=1){
                    rollbackQuietly(
                            connection
                    );

                    return false;
                }
            }

            /*
             * ======================================================
             * INVALIDAR RECUPERACIONES ANTERIORES
             * ======================================================
             *
             * Una vez cambiada la contraseña ningún enlace de
             * recuperación generado anteriormente debe continuar
             * siendo válido.
             *
             * Cero filas actualizadas es correcto:
             * el usuario puede no tener tokens pendientes.
             */
            String tokenSql="""
                UPDATE PASSWORD_RESET_TOKENS
                SET USED=1
                WHERE ID_USER=?
                  AND USED=0
                """;

            try(
                    PreparedStatement statement=
                            connection.prepareStatement(
                                    tokenSql
                            )
            ){
                statement.setLong(
                        1,
                        idUser
                );

                statement.executeUpdate();
            }

            connection.commit();

            return true;

        }catch(SQLException exception){
            rollbackQuietly(
                    connection
            );

            throw databaseException(
                    "Error al actualizar la contraseña del usuario.",
                    exception
            );

        }catch(RuntimeException exception){
            rollbackQuietly(
                    connection
            );

            throw exception;

        }finally{
            closeConnection(
                    connection
            );
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
    private void disableParallelDml(
            Connection connection
    )throws SQLException{
        if(connection==null){
            throw new SQLException(
                    "La conexión para desactivar Parallel DML no es válida."
            );
        }

        try(
                Statement statement=
                        connection.createStatement()
        ){
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
    ){
        if(connection==null){
            return;
        }

        try{
            if(!connection.getAutoCommit()){
                connection.rollback();
            }
        }catch(SQLException ignored){
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
    ){
        if(connection==null){
            return;
        }

        try{
            if(!connection.getAutoCommit()){
                connection.setAutoCommit(
                        true
                );
            }
        }catch(SQLException ignored){
        }

        try{
            connection.close();
        }catch(SQLException ignored){
        }
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean isTemporaryPasswordExpired(
            User user
    ){
        if(user==null
                ||!user.requiresPasswordChange()){
            return false;
        }

        Timestamp expiration=
                user.getTemporaryPasswordExpiration();

        return expiration==null
                ||expiration.before(
                new Timestamp(
                        System.currentTimeMillis()
                )
        );
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @param includePassword contraseña que se procesará de forma segura
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private User mapUser(
            ResultSet resultSet,
            boolean includePassword
    )throws SQLException{
        User user=new User();

        user.setId(
                resultSet.getLong("id_user")
        );

        user.setName(
                resultSet.getString("name")
        );

        user.setSurname(
                resultSet.getString("surname")
        );

        user.setLastname(
                resultSet.getString("lastname")
        );

        user.setPhone(
                resultSet.getString("phone")
        );

        user.setEmail(
                resultSet.getString("email")
        );

        if(includePassword){
            user.setPassword(
                    resultSet.getString("password")
            );
        }

        user.setRole(
                resultSet.getString("role")
        );

        user.setStatus(
                resultSet.getInt("status")
        );

        user.setMustChangePassword(
                resultSet.getInt(
                        "must_change_password"
                )
        );

        user.setTemporaryPasswordExpiration(
                resultSet.getTimestamp(
                        "temporary_password_expiration"
                )
        );

        return user;
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
