package com.almacen.integradora.controllers.user;

import com.almacen.integradora.services.TemporaryAccessService;
import com.almacen.integradora.models.user.User;
import com.almacen.integradora.models.user.UserDao;
import com.almacen.integradora.utils.EmailSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * Define UserServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(name="UserServlet",urlPatterns={
        "/users",
        "/users/list",
        "/user/save",
        "/user/update",
        "/user/change-status",
        "/user/reset-access"
})
/** Controlador HTTP del módulo de usuarios, estados y accesos temporales.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class UserServlet extends HttpServlet{
    private UserDao userDao;
    private TemporaryAccessService temporaryAccessService;
    private Gson gson;

    private static final Pattern NAME_PATTERN=Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s'-]{2,50}$");
    private static final Pattern EMAIL_PATTERN=Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final Pattern PHONE_PATTERN=Pattern.compile("^\\d{10}$");

    private static final String ROLE_ADMIN="Administrador";
    private static final String ROLE_WAREHOUSE="Almacenista";

    private static final int TEMPORARY_PASSWORD_HOURS=24;
    private static final int TEMPORARY_PASSWORD_LENGTH=12;

    private static final String UPPERCASE="ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE="abcdefghijkmnopqrstuvwxyz";
    private static final String NUMBERS="23456789";
    private static final String SYMBOLS="@#$%";
    private static final String ALL_CHARACTERS=UPPERCASE+LOWERCASE+NUMBERS+SYMBOLS;

    private static final SecureRandom SECURE_RANDOM=new SecureRandom();

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init(){
        userDao=new UserDao();
        temporaryAccessService = new TemporaryAccessService();
        gson=new GsonBuilder().serializeNulls().create();
    }

    /**
     * Atiende solicitudes HTTP GET y prepara la respuesta correspondiente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws ServletException si no puede completarse la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        disableCache(response);

        switch(request.getServletPath()){
            case "/users"->showUsers(request,response);
            case "/users/list"->listUsers(request,response);
            default->sendJson(response,HttpServletResponse.SC_NOT_FOUND,false,"error","La ruta solicitada no existe.",null);
        }
    }

    /**
     * Atiende solicitudes HTTP POST y coordina la operación solicitada.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException{
        disableCache(response);
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        switch(request.getServletPath()){
            case "/user/save"->saveUser(request,response);
            case "/user/update"->updateUser(request,response);
            case "/user/change-status"->changeUserStatus(request,response);
            case "/user/reset-access"->resetUserAccess(request,response);
            default->sendJson(response,HttpServletResponse.SC_NOT_FOUND,false,"error","La ruta solicitada no existe.",null);
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws ServletException si no puede completarse la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void showUsers(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        try{
            User sessionUser=getSessionUser(request);

            if(!hasValidSessionUser(sessionUser)){
                response.sendRedirect(request.getContextPath()+"/login");
                return;
            }

            List<User> users=getUsersExceptCurrent(sessionUser);
            request.setAttribute("users",users);
            request.getRequestDispatcher("/views/user/users.jsp").forward(request,response);
        }catch(RuntimeException exception){
            getServletContext().log("Error al consultar los usuarios.",exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"No fue posible consultar los usuarios.");
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void listUsers(HttpServletRequest request,HttpServletResponse response)throws IOException{
        try{
            User sessionUser=getSessionUser(request);

            if(!hasValidSessionUser(sessionUser)){
                sendJson(response,HttpServletResponse.SC_UNAUTHORIZED,false,"error","La sesión del usuario no es válida.",null);
                return;
            }

            sendJson(response,HttpServletResponse.SC_OK,true,"success","",getUsersExceptCurrent(sessionUser));
        }catch(RuntimeException exception){
            getServletContext().log("Error al consultar los usuarios.",exception);
            sendJson(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false,"error","No fue posible consultar los usuarios.",null);
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param sessionUser valor de sessionUser requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private List<User> getUsersExceptCurrent(User sessionUser){
        return userDao.getAll().stream()
                .filter(user->user!=null
                        &&user.getId()!=null
                        &&!user.getId().equals(sessionUser.getId()))
                .toList();
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void saveUser(HttpServletRequest request,HttpServletResponse response)throws IOException{
        User user=readUser(request);

        try{
            String validationMessage=validateUser(user);

            if(validationMessage!=null){
                sendJson(response,HttpServletResponse.SC_BAD_REQUEST,false,"warning",validationMessage,null);
                return;
            }

            if(userDao.findAnyByEmail(user.getEmail())!=null){
                sendJson(response,HttpServletResponse.SC_CONFLICT,false,"warning","Ya existe un usuario registrado con ese correo electrónico.",null);
                return;
            }

            String temporaryPassword=generateTemporaryPassword();
            Timestamp expiration=createTemporaryPasswordExpiration();

            user.setPassword(temporaryPassword);
            user.setStatus(1);
            user.setMustChangePassword(1);
            user.setTemporaryPasswordExpiration(expiration);

            if(!userDao.create(user)){
                sendJson(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false,"error","No fue posible registrar el usuario.",null);
                return;
            }

            try{
                sendTemporaryAccessEmail(user,temporaryPassword,false);
            }catch(RuntimeException emailException){
                getServletContext().log("El usuario fue registrado, pero no fue posible enviar el correo.",emailException);

                User createdUser=userDao.findAnyByEmail(user.getEmail());

                if(createdUser!=null&&createdUser.getId()!=null){
                    try{
                        userDao.delete(createdUser.getId().intValue());
                    }catch(RuntimeException deleteException){
                        getServletContext().log("No fue posible revertir el registro del usuario.",deleteException);
                    }
                }

                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible enviar las credenciales al correo del usuario. El registro fue cancelado.",
                        null
                );
                return;
            }

            Map<String,Object> data=new LinkedHashMap<>();
            data.put("email",user.getEmail());
            data.put("fullName",buildFullName(user.getName(),user.getSurname(),user.getLastname()));
            data.put("expiresInHours",TEMPORARY_PASSWORD_HOURS);

            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    true,
                    "success",
                    "El usuario se registró correctamente. Las credenciales fueron enviadas a "+user.getEmail()+".",
                    data
            );
        }catch(RuntimeException exception){
            getServletContext().log("Error inesperado al registrar el usuario.",exception);
            sendJson(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false,"error","No fue posible registrar el usuario.",null);
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void updateUser(HttpServletRequest request,HttpServletResponse response)throws IOException{
        String idValue=normalizeText(request.getParameter("id"));
        User submittedUser=readUser(request);

        try{
            Integer id=parsePositiveInteger(idValue);

            if(id==null){
                sendJson(response,HttpServletResponse.SC_BAD_REQUEST,false,"warning","El identificador del usuario no es válido.",null);
                return;
            }

            User sessionUser=getSessionUser(request);

            if(!hasValidSessionUser(sessionUser)){
                sendJson(response,HttpServletResponse.SC_UNAUTHORIZED,false,"error","La sesión del usuario no es válida.",null);
                return;
            }

            if(isSameUser(sessionUser,id)){
                sendJson(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        false,
                        "warning",
                        "No puedes modificar tu propia cuenta desde la gestión de usuarios. Utiliza la sección de perfil.",
                        null
                );
                return;
            }

            User currentUser=userDao.getById(id);

            if(currentUser==null){
                sendJson(response,HttpServletResponse.SC_NOT_FOUND,false,"error","El usuario solicitado no existe.",null);
                return;
            }

            String validationMessage=validateUser(submittedUser);

            if(validationMessage!=null){
                sendJson(response,HttpServletResponse.SC_BAD_REQUEST,false,"warning",validationMessage,null);
                return;
            }

            User userWithEmail=userDao.findAnyByEmail(submittedUser.getEmail());

            if(userWithEmail!=null&&!userWithEmail.getId().equals(currentUser.getId())){
                sendJson(response,HttpServletResponse.SC_CONFLICT,false,"warning","Ya existe otro usuario registrado con ese correo electrónico.",null);
                return;
            }

            currentUser.setName(submittedUser.getName());
            currentUser.setSurname(submittedUser.getSurname());
            currentUser.setLastname(submittedUser.getLastname());
            currentUser.setPhone(submittedUser.getPhone());
            currentUser.setEmail(submittedUser.getEmail());
            currentUser.setRole(submittedUser.getRole());

            if(!userDao.update(currentUser)){
                sendJson(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false,"error","No fue posible actualizar el usuario.",null);
                return;
            }

            sendJson(response,HttpServletResponse.SC_OK,true,"success","El usuario se actualizó correctamente.",null);
        }catch(RuntimeException exception){
            getServletContext().log("Error inesperado al actualizar el usuario.",exception);
            sendJson(response,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,false,"error","No fue posible actualizar el usuario.",null);
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void changeUserStatus(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{

        String idValue=
                normalizeText(
                        request.getParameter("id")
                );

        String statusValue=
                normalizeText(
                        request.getParameter("status")
                );

        try{
            Integer id=
                    parsePositiveInteger(idValue);

            Integer status=
                    parseStatus(statusValue);

            if(id==null||status==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "El identificador o el estado enviado no es válido.",
                        null
                );
                return;
            }

            User sessionUser=
                    getSessionUser(request);

            if(!hasValidSessionUser(sessionUser)){
                sendJson(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        false,
                        "error",
                        "La sesión del usuario no es válida.",
                        null
                );
                return;
            }

            /*
             * El usuario actual nunca puede cambiar su propio estado
             * desde Administración.
             */
            if(isSameUser(sessionUser,id)){
                sendJson(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        false,
                        "warning",
                        status==0
                                ?"No puedes desactivar tu propia cuenta."
                                :"No puedes cambiar el estado de tu propia cuenta desde este módulo.",
                        null
                );
                return;
            }

            User user=
                    userDao.getById(id);

            if(user==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El usuario solicitado no existe.",
                        null
                );
                return;
            }

            if(Integer.valueOf(status)
                    .equals(user.getStatus())){

                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        true,
                        "info",
                        status==1
                                ?"El usuario ya se encuentra activo."
                                :"El usuario ya se encuentra inactivo.",
                        null
                );
                return;
            }

            /*
             * ======================================================
             * DESACTIVACIÓN
             * ======================================================
             *
             * Antes de desactivar invalidamos todos los enlaces de
             * recuperación pendientes.
             *
             * De esta manera esos tokens tampoco podrían volver a ser
             * válidos posteriormente si el usuario fuera reactivado.
             */

            if(!userDao.updateStatusAndInvalidateRecoveryTokens(
                    id,
                    status
            )){
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible cambiar el estado del usuario.",
                        null
                );
                return;
            }

            /*
             * ACTIVACIÓN:
             *
             * No generamos contraseña nueva.
             * No restauramos tokens anteriores.
             * No modificamos el historial.
             *
             * Simplemente habilitamos nuevamente la cuenta.
             */
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    status==1
                            ?"success"
                            :"warning",
                    status==1
                            ?"El usuario fue activado correctamente."
                            :"El usuario fue desactivado correctamente. Sus enlaces de recuperación pendientes fueron invalidados.",
                    null
            );

        }catch(RuntimeException exception){

            getServletContext().log(
                    "Error al cambiar el estado del usuario.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cambiar el estado del usuario.",
                    null
            );
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void resetUserAccess(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String idValue =
                normalizeText(
                        request.getParameter(
                                "id"
                        )
                );

        try {
            Integer id =
                    parsePositiveInteger(
                            idValue
                    );

            if (id == null) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "El identificador del usuario no es válido.",
                        null
                );

                return;
            }

            User sessionUser =
                    getSessionUser(
                            request
                    );

            if (!hasValidSessionUser(
                    sessionUser
            )) {

                sendJson(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        false,
                        "error",
                        "La sesión del usuario no es válida.",
                        null
                );

                return;
            }

            /*
             * El administrador no puede restablecer desde este módulo
             * su propio acceso.
             */
            if (isSameUser(
                    sessionUser,
                    id
            )) {

                sendJson(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        false,
                        "warning",
                        "No puedes restablecer tu propio acceso desde la gestión de usuarios.",
                        null
                );

                return;
            }

            User user =
                    userDao.getById(
                            id
                    );

            if (user == null) {

                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El usuario solicitado no existe.",
                        null
                );

                return;
            }

            if (!Integer.valueOf(1)
                    .equals(
                            user.getStatus()
                    )) {

                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Activa al usuario antes de restablecer su acceso.",
                        null
                );

                return;
            }

            String temporaryPassword =
                    generateTemporaryPassword();

            Timestamp expiration =
                    createTemporaryPasswordExpiration();

            /*
             * ======================================================
             * OPERACIÓN TRANSACCIONAL
             * ======================================================
             *
             * TemporaryAccessService:
             *
             * - vuelve a validar el usuario;
             * - bloquea USERS;
             * - cambia la contraseña;
             * - intenta enviar el correo;
             * - COMMIT únicamente si el correo fue enviado;
             * - ROLLBACK si el correo falla.
             */
            TemporaryAccessService.Result result =
                    temporaryAccessService.resetAccess(
                            user.getId(),
                            temporaryPassword,
                            expiration,
                            () ->
                                    sendTemporaryAccessEmail(
                                            user,
                                            temporaryPassword,
                                            true
                                    )
                    );

            switch (result) {

                case SUCCESS -> {

                    Map<String, Object> data =
                            new LinkedHashMap<>();

                    data.put(
                            "email",
                            user.getEmail()
                    );

                    data.put(
                            "fullName",
                            buildFullName(
                                    user.getName(),
                                    user.getSurname(),
                                    user.getLastname()
                            )
                    );

                    data.put(
                            "expiresInHours",
                            TEMPORARY_PASSWORD_HOURS
                    );

                    sendJson(
                            response,
                            HttpServletResponse.SC_OK,
                            true,
                            "success",
                            "El acceso se restableció correctamente. La nueva contraseña temporal fue enviada a "
                                    + user.getEmail()
                                    + ".",
                            data
                    );
                }

                case USER_NOT_FOUND ->

                        sendJson(
                                response,
                                HttpServletResponse.SC_NOT_FOUND,
                                false,
                                "error",
                                "El usuario solicitado ya no existe.",
                                null
                        );

                case USER_INACTIVE ->

                        sendJson(
                                response,
                                HttpServletResponse.SC_CONFLICT,
                                false,
                                "warning",
                                "El usuario fue desactivado antes de completar el restablecimiento.",
                                null
                        );

                case EMAIL_FAILED -> {

                    /*
                     * MUY IMPORTANTE:
                     *
                     * La transacción ya hizo ROLLBACK.
                     *
                     * La contraseña que tenía el usuario antes de esta
                     * operación sigue siendo válida.
                     */
                    getServletContext().log(
                            "No fue posible enviar el correo de restablecimiento de acceso al usuario "
                                    + user.getId()
                                    + ". La modificación de contraseña fue revertida."
                    );

                    sendJson(
                            response,
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            false,
                            "error",
                            "No fue posible enviar la nueva contraseña al correo del usuario. El restablecimiento fue cancelado y su acceso anterior permanece sin cambios.",
                            null
                    );
                }

                case INVALID_DATA,
                     UPDATE_FAILED ->

                        sendJson(
                                response,
                                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                                false,
                                "error",
                                "No fue posible restablecer el acceso del usuario.",
                                null
                        );
            }

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al restablecer el acceso del usuario.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible restablecer el acceso del usuario.",
                    null
            );
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param user valor de user requerido por la operación
     * @param temporaryPassword contraseña que se procesará de forma segura
     * @param resetAccess valor de resetAccess requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void sendTemporaryAccessEmail(User user,String temporaryPassword,boolean resetAccess){
        String fullName=buildFullName(user.getName(),user.getSurname(),user.getLastname());
        String title=resetAccess?"Acceso restablecido":"Bienvenido al Sistema Gestor de Almacén";

        String introduction=resetAccess
                ?"Tu acceso al Sistema Gestor de Almacén fue restablecido. Utiliza estas credenciales temporales para ingresar."
                :"Se creó una cuenta para ti en el Sistema Gestor de Almacén. Utiliza estas credenciales temporales para ingresar.";

        String body="""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width,initial-scale=1">
                </head>
                <body style="margin:0;padding:0;background:#f4f6f8;font-family:Arial,Helvetica,sans-serif;color:#2d3748;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="padding:30px 15px;background:#f4f6f8;">
                        <tr>
                            <td align="center">
                                <table width="100%%" cellpadding="0" cellspacing="0" style="max-width:600px;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,.08);">
                                    <tr>
                                        <td style="padding:28px 30px;background:#6390ff;color:#ffffff;text-align:center;">
                                            <h1 style="margin:0;font-size:23px;">Sistema Gestor de Almacén</h1>
                                            <p style="margin:8px 0 0;opacity:.9;">%s</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:30px;">
                                            <p style="font-size:16px;margin-top:0;">Hola <strong>%s</strong>,</p>
                                            <p style="font-size:15px;line-height:1.6;">%s</p>
                                            <table width="100%%" cellpadding="0" cellspacing="0" style="margin:24px 0;background:#f7f8fa;border-radius:12px;padding:18px;">
                                                <tr>
                                                    <td style="padding:8px 12px;color:#718096;">Correo</td>
                                                    <td style="padding:8px 12px;font-weight:bold;">%s</td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:8px 12px;color:#718096;">Contraseña temporal</td>
                                                    <td style="padding:8px 12px;font-weight:bold;font-family:monospace;font-size:17px;color:#2d3748;">%s</td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:8px 12px;color:#718096;">Rol</td>
                                                    <td style="padding:8px 12px;font-weight:bold;">%s</td>
                                                </tr>
                                            </table>
                                            <div style="padding:15px 18px;background:#fff8e5;border-radius:10px;color:#805b10;font-size:14px;line-height:1.6;">
                                                <strong>Importante:</strong>
                                                esta contraseña es temporal y vencerá en %d horas.
                                                Al iniciar sesión deberás establecer una nueva contraseña.
                                            </div>
                                            <p style="margin-top:24px;font-size:14px;line-height:1.6;color:#718096;">
                                                Por seguridad, no compartas estas credenciales con otras personas.
                                                Si no reconoces esta operación, comunícate con el administrador del sistema.
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:18px;text-align:center;background:#f7f8fa;color:#8a94a6;font-size:12px;">
                                            Sistema Gestor de Almacén
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                escapeHtml(title),
                escapeHtml(fullName),
                escapeHtml(introduction),
                escapeHtml(user.getEmail()),
                escapeHtml(temporaryPassword),
                escapeHtml(user.getRole()),
                TEMPORARY_PASSWORD_HOURS
        );

        String subject=resetAccess
                ?"Restablecimiento de acceso - Sistema Gestor de Almacén"
                :"Credenciales de acceso - Sistema Gestor de Almacén";

        EmailSender.sendMail(user.getEmail(),subject,body);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private User readUser(HttpServletRequest request){
        User user=new User();
        user.setName(normalizeText(request.getParameter("name")));
        user.setSurname(normalizeText(request.getParameter("surname")));
        user.setLastname(normalizeText(request.getParameter("lastname")));
        user.setPhone(normalizeDigits(request.getParameter("phone")));
        user.setEmail(normalizeEmail(request.getParameter("email")));
        user.setRole(normalizeRole(request.getParameter("role")));
        return user;
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String validateUser(User user){
        if(user==null)return "No se recibieron los datos del usuario.";

        if(hasEmptyRequiredFields(
                user.getName(),
                user.getSurname(),
                user.getLastname(),
                user.getPhone(),
                user.getEmail(),
                user.getRole()
        )){
            return "Completa todos los campos obligatorios.";
        }

        if(!isValidName(user.getName()))return "El nombre no tiene un formato válido.";
        if(!isValidName(user.getSurname()))return "El apellido paterno no tiene un formato válido.";
        if(!isValidName(user.getLastname()))return "El apellido materno no tiene un formato válido.";
        if(!isValidPhone(user.getPhone()))return "El teléfono debe contener exactamente 10 dígitos.";
        if(!isValidEmail(user.getEmail()))return "El correo electrónico no tiene un formato válido.";
        if(!isValidRole(user.getRole()))return "El rol seleccionado no es válido.";

        return null;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param values valor de values requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean hasEmptyRequiredFields(String...values){
        if(values==null)return true;

        for(String value:values){
            if(value==null||value.isBlank())return true;
        }

        return false;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidName(String value){
        return value!=null&&NAME_PATTERN.matcher(value).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param phone valor de phone requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidPhone(String phone){
        return phone!=null&&PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param email dirección de correo asociada a la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidEmail(String email){
        return email!=null&&email.length()<=100&&EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param role valor de role requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidRole(String role){
        return ROLE_ADMIN.equals(role)||ROLE_WAREHOUSE.equals(role);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private Integer parsePositiveInteger(String value){
        if(value==null||value.isBlank())return null;

        try{
            int number=Integer.parseInt(value.trim());
            return number>0?number:null;
        }catch(NumberFormatException exception){
            return null;
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private Integer parseStatus(String value){
        if("0".equals(value))return 0;
        if("1".equals(value))return 1;
        return null;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private User getSessionUser(HttpServletRequest request){
        HttpSession session=request.getSession(false);

        if(session==null)return null;

        Object value=session.getAttribute("usuario");
        return value instanceof User user?user:null;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean hasValidSessionUser(User user){
        return user!=null&&user.getId()!=null&&user.getId()>0;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param sessionUser valor de sessionUser requerido por la operación
     * @param requestedUserId valor de requestedUserId requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isSameUser(User sessionUser,Integer requestedUserId){
        return hasValidSessionUser(sessionUser)
                &&requestedUserId!=null
                &&sessionUser.getId().longValue()==requestedUserId.longValue();
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String generateTemporaryPassword(){
        char[] password=new char[TEMPORARY_PASSWORD_LENGTH];

        password[0]=randomCharacter(UPPERCASE);
        password[1]=randomCharacter(LOWERCASE);
        password[2]=randomCharacter(NUMBERS);
        password[3]=randomCharacter(SYMBOLS);

        for(int index=4;index<password.length;index++){
            password[index]=randomCharacter(ALL_CHARACTERS);
        }

        shuffle(password);
        return new String(password);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param characters valor de characters requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private char randomCharacter(String characters){
        int index=SECURE_RANDOM.nextInt(characters.length());
        return characters.charAt(index);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param characters valor de characters requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void shuffle(char[] characters){
        for(int index=characters.length-1;index>0;index--){
            int randomIndex=SECURE_RANDOM.nextInt(index+1);
            char temporary=characters[index];
            characters[index]=characters[randomIndex];
            characters[randomIndex]=temporary;
        }
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private Timestamp createTemporaryPasswordExpiration(){
        return Timestamp.from(
                Instant.now().plus(
                        Duration.ofHours(TEMPORARY_PASSWORD_HOURS)
                )
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String escapeHtml(String value){
        if(value==null)return "";

        return value
                .replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizeText(String value){
        if(value==null)return "";
        return value.trim().replaceAll("\\s+"," ");
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizeDigits(String value){
        if(value==null)return "";
        return value.replaceAll("\\D","");
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizeEmail(String value){
        return normalizeText(value).toLowerCase(Locale.ROOT);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizeRole(String value){
        String normalized=normalizeText(value);

        if(ROLE_ADMIN.equalsIgnoreCase(normalized))return ROLE_ADMIN;
        if(ROLE_WAREHOUSE.equalsIgnoreCase(normalized))return ROLE_WAREHOUSE;

        return normalized;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param name valor de name requerido por la operación
     * @param surname valor de surname requerido por la operación
     * @param lastname valor de lastname requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildFullName(String name,String surname,String lastname){
        return String.join(
                " ",
                normalizeText(name),
                normalizeText(surname),
                normalizeText(lastname)
        ).trim();
    }

    /**
     * Construye o envía la respuesta requerida por el cliente HTTP.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param statusCode estado que se utilizará en la operación
     * @param success valor de success requerido por la operación
     * @param type valor de type requerido por la operación
     * @param message valor de message requerido por la operación
     * @param data valor de data requerido por la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void sendJson(
            HttpServletResponse response,
            int statusCode,
            boolean success,
            String type,
            String message,
            Object data
    )throws IOException{
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String,Object> result=new LinkedHashMap<>();
        result.put("success",success);
        result.put("type",normalizeResponseType(type,success));
        result.put("message",message==null?"":message.trim());

        if(data!=null)result.put("data",data);

        response.getWriter().write(gson.toJson(result));
    }

    /**
     * Construye o envía la respuesta requerida por el cliente HTTP.
     *
     * @param type valor de type requerido por la operación
     * @param success valor de success requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizeResponseType(String type,boolean success){
        if(type==null||type.isBlank())return success?"success":"error";

        String normalizedType=type.trim().toLowerCase(Locale.ROOT);

        return switch(normalizedType){
            case "success","error","warning","info"->normalizedType;
            default->success?"success":"error";
        };
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void disableCache(HttpServletResponse response){
        response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0);
    }
}
