package com.almacen.integradora.controllers;

import com.almacen.integradora.models.passwordreset.PasswordResetToken;
import com.almacen.integradora.models.passwordreset.PasswordResetTokenDao;
import com.almacen.integradora.models.user.User;
import com.almacen.integradora.models.user.UserDao;
import com.almacen.integradora.services.PasswordResetService;
import com.almacen.integradora.utils.EmailSender;
import com.almacen.integradora.utils.PasswordPolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;
/**
 * Define RecoverPasswordServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name="RecoverPasswordServlet",
        urlPatterns={
                "/verify-email",
                "/reset-password",
                "/password-reset-success"
        }
)
/** Controlador del flujo público de recuperación de contraseña por token.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class RecoverPasswordServlet extends HttpServlet{
    private UserDao userDao;
    private PasswordResetTokenDao tokenDao;
    private PasswordResetService passwordResetService;

    private static final int RESET_TOKEN_MINUTES=15;
    private static final int RESET_TOKEN_BYTES=32;

    private static final String GENERIC_RECOVERY_MESSAGE=
            "Si el correo está registrado y la cuenta se encuentra activa, recibirás un enlace para restablecer tu contraseña.";

    private static final SecureRandom SECURE_RANDOM=
            new SecureRandom();

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
        tokenDao=new PasswordResetTokenDao();
        passwordResetService=new PasswordResetService();
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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        disableCache(response);

        switch(request.getServletPath()){
            case "/verify-email"->
                    showVerifyEmail(request,response);

            case "/reset-password"->
                    showResetPassword(request,response);

            case "/password-reset-success"->
                    showSuccess(request,response);

            default->
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND
                    );
        }
    }

    /**
     * Atiende solicitudes HTTP POST y coordina la operación solicitada.
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
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        disableCache(response);

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        switch(request.getServletPath()){
            case "/verify-email"->
                    processRecoveryRequest(
                            request,
                            response
                    );

            case "/reset-password"->
                    processPasswordReset(
                            request,
                            response
                    );

            default->
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND
                    );
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
    private void showVerifyEmail(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        request.getRequestDispatcher(
                "/verify-email.jsp"
        ).forward(request,response);
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
    private void showResetPassword(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        String token=normalizeToken(
                request.getParameter("token")
        );

        if(token.isBlank()){
            showRecoveryError(
                    request,
                    response,
                    "El enlace de recuperación no es válido."
            );
            return;
        }

        PasswordResetToken resetToken;

        try{
            resetToken=
                    tokenDao.findValidToken(token);

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al validar el token de recuperación.",
                    exception
            );

            showRecoveryError(
                    request,
                    response,
                    "No fue posible validar el enlace de recuperación."
            );
            return;
        }

        if(resetToken==null){
            showRecoveryError(
                    request,
                    response,
                    "El enlace de recuperación ya fue utilizado o ha expirado."
            );
            return;
        }

        request.setAttribute(
                "token",
                token
        );

        request.getRequestDispatcher(
                "/reset-password.jsp"
        ).forward(request,response);
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
    private void showSuccess(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        request.getRequestDispatcher(
                "/password-reset-success.jsp"
        ).forward(request,response);
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
    private void processRecoveryRequest(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{

        String email=
                normalizeEmail(
                        request.getParameter(
                                "email"
                        )
                );

        if(email.isBlank()
                ||!isReasonableEmail(email)){

            showGenericRecoveryMessage(
                    request,
                    response
            );

            return;
        }

        User user;

        try{
            user=
                    userDao.findByEmail(
                            email
                    );

        }catch(RuntimeException exception){

            getServletContext().log(
                    "Error al consultar el usuario para recuperación.",
                    exception
            );

            showRecoveryProcessingError(
                    request,
                    response
            );

            return;
        }

        /*
         * Nunca revelamos si el correo existe o si la
         * cuenta se encuentra inactiva.
         */
        if(user==null
                ||!Integer.valueOf(1)
                .equals(
                        user.getStatus()
                )){

            showGenericRecoveryMessage(
                    request,
                    response
            );

            return;
        }

        if(user.getId()==null
                ||user.getId()<=0){

            showRecoveryProcessingError(
                    request,
                    response
            );

            return;
        }

        /*
         * Declaramos el token fuera del try para poder invalidarlo
         * si cualquier operación posterior a su creación falla.
         */
        String token=null;
        boolean tokenCreated=false;

        try{

            token=
                    generateSecureToken();

            LocalDateTime expiration=
                    LocalDateTime.now()
                            .plusMinutes(
                                    RESET_TOKEN_MINUTES
                            );

            /*
             * ======================================================
             * CREAR TOKEN
             * ======================================================
             *
             * PasswordResetService:
             *
             * - bloquea USERS;
             * - comprueba que el usuario siga activo;
             * - invalida tokens anteriores;
             * - inserta el nuevo token;
             * - hace COMMIT.
             */
            if(!passwordResetService.createRecoveryToken(
                    user.getId(),
                    token,
                    expiration
            )){

                /*
                 * La cuenta pudo haber sido desactivada entre
                 * la consulta inicial y este punto.
                 *
                 * No revelamos esa información.
                 */
                showGenericRecoveryMessage(
                        request,
                        response
                );

                return;
            }

            /*
             * A partir de este punto sabemos que el token ya
             * existe y fue confirmado en la base de datos.
             */
            tokenCreated=true;

            /*
             * ======================================================
             * CONSTRUIR ENLACE
             * ======================================================
             *
             * Aquí puede fallar, por ejemplo, si APP_BASE_URL
             * no está configurada.
             */
            String resetLink=
                    buildResetLink(
                            request,
                            token
                    );

            /*
             * ======================================================
             * CONSTRUIR CORREO
             * ======================================================
             */
            String body=
                    buildRecoveryEmail(
                            user,
                            resetLink
                    );

            /*
             * ======================================================
             * ENVIAR CORREO
             * ======================================================
             */
            EmailSender.sendMail(
                    user.getEmail(),
                    "Restablecimiento de contraseña - Sistema Gestor de Almacén",
                    body
            );

            /*
             * El token ya fue entregado correctamente.
             *
             * Desde este momento NO debemos invalidarlo en este flujo.
             */
            tokenCreated=false;

            showGenericRecoveryMessage(
                    request,
                    response
            );

        }catch(RuntimeException exception){

            getServletContext().log(
                    "Error inesperado al generar o enviar la recuperación de contraseña.",
                    exception
            );

            /*
             * ======================================================
             * LIMPIEZA COMPENSATORIA
             * ======================================================
             *
             * Si el token ya fue guardado pero alguna operación
             * posterior falló, invalidamos EXACTAMENTE ese token.
             *
             * No invalidamos todos los tokens del usuario porque una
             * petición concurrente podría haber generado uno nuevo.
             */
            if(tokenCreated
                    &&token!=null
                    &&!token.isBlank()){

                try{
                    tokenDao.invalidateToken(
                            token
                    );

                }catch(RuntimeException invalidateException){

                    getServletContext().log(
                            "No fue posible invalidar el token generado después de un fallo en la recuperación.",
                            invalidateException
                    );
                }
            }

            showGenericRecoveryMessage(
                    request,
                    response
            );
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
    private void processPasswordReset(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        String token=normalizeToken(
                request.getParameter("token")
        );

        String password=
                request.getParameter("password");

        String confirmation=
                request.getParameter(
                        "confirmPassword"
                );

        if(token.isBlank()){
            showRecoveryError(
                    request,
                    response,
                    "El enlace de recuperación no es válido."
            );
            return;
        }

        if(password==null
                ||password.isBlank()){
            showResetError(
                    request,
                    response,
                    token,
                    "Ingresa una contraseña nueva."
            );
            return;
        }

        if(!PasswordPolicy.isValid(password)){
            showResetError(
                    request,
                    response,
                    token,
                    PasswordPolicy.getValidationMessage()
            );
            return;
        }

        if(confirmation==null
                ||confirmation.isBlank()){
            showResetError(
                    request,
                    response,
                    token,
                    "Confirma la contraseña nueva."
            );
            return;
        }

        if(!password.equals(confirmation)){
            showResetError(
                    request,
                    response,
                    token,
                    "Las contraseñas no coinciden."
            );
            return;
        }

        try{
            PasswordResetService.ResetResult result=
                    passwordResetService.resetPassword(
                            token,
                            password
                    );

            switch(result){
                case SUCCESS->{
                    response.sendRedirect(
                            request.getContextPath()
                                    +"/password-reset-success"
                    );
                }

                case INVALID_TOKEN->{
                    showRecoveryError(
                            request,
                            response,
                            "El enlace de recuperación ya fue utilizado o ha expirado."
                    );
                }

                case INVALID_PASSWORD->{
                    showResetError(
                            request,
                            response,
                            token,
                            PasswordPolicy.getValidationMessage()
                    );
                }

                case USER_NOT_FOUND->{
                    showRecoveryError(
                            request,
                            response,
                            "La cuenta asociada al enlace ya no se encuentra disponible."
                    );
                }

                case TOKEN_INVALIDATION_FAILED->{
                    showResetError(
                            request,
                            response,
                            token,
                            "No fue posible completar el restablecimiento. Intenta nuevamente."
                    );
                }
            }

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error transaccional al restablecer la contraseña.",
                    exception
            );

            showResetError(
                    request,
                    response,
                    token,
                    "No fue posible cambiar la contraseña. Intenta nuevamente."
            );
        }
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
    private String generateSecureToken(){
        byte[] bytes=
                new byte[RESET_TOKEN_BYTES];

        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param token token utilizado para validar la solicitud
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildResetLink(
            HttpServletRequest request,
            String token
    ){
        String configuredBaseUrl=
                normalizeBaseUrl(
                        System.getenv(
                                "APP_BASE_URL"
                        )
                );

        if(configuredBaseUrl.isBlank()){
            throw new IllegalStateException(
                    "APP_BASE_URL no se encuentra configurada."
            );
        }

        if(token==null||token.isBlank()){
            throw new IllegalArgumentException(
                    "El token de recuperación no es válido."
            );
        }

        return configuredBaseUrl
                +request.getContextPath()
                +"/reset-password?token="
                +URLEncoder.encode(
                token,
                StandardCharsets.UTF_8
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param user valor de user requerido por la operación
     * @param resetLink valor de resetLink requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildRecoveryEmail(
            User user,
            String resetLink
    ){
        String fullName=
                buildFullName(user);

        return """
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
                                            <p style="margin:8px 0 0;opacity:.9;">Restablecimiento de contraseña</p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:30px;">
                                            <p style="font-size:16px;margin-top:0;">
                                                Hola <strong>%s</strong>,
                                            </p>
                                            <p style="font-size:15px;line-height:1.6;">
                                                Recibimos una solicitud para restablecer la contraseña de tu cuenta.
                                            </p>
                                            <div style="text-align:center;margin:28px 0;">
                                                <a href="%s"
                                                   style="display:inline-block;padding:13px 22px;background:#6390ff;color:#ffffff;text-decoration:none;border-radius:10px;font-weight:bold;">
                                                    Restablecer contraseña
                                                </a>
                                            </div>
                                            <div style="padding:15px 18px;background:#fff8e5;border-radius:10px;color:#805b10;font-size:14px;line-height:1.6;">
                                                <strong>Importante:</strong>
                                                este enlace es de un solo uso y vencerá en %d minutos.
                                            </div>
                                            <p style="margin-top:24px;font-size:14px;line-height:1.6;color:#718096;">
                                                Si no solicitaste este cambio, puedes ignorar este correo. Tu contraseña actual seguirá funcionando.
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
                escapeHtml(fullName),
                escapeHtml(resetLink),
                RESET_TOKEN_MINUTES
        );
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
    private void showGenericRecoveryMessage(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        request.setAttribute(
                "mensaje",
                GENERIC_RECOVERY_MESSAGE
        );

        request.getRequestDispatcher(
                "/verify-email.jsp"
        ).forward(request,response);
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
    private void showRecoveryProcessingError(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        request.setAttribute(
                "error",
                "No fue posible procesar la solicitud en este momento."
        );

        request.getRequestDispatcher(
                "/verify-email.jsp"
        ).forward(request,response);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param message valor de message requerido por la operación
     * @throws ServletException si no puede completarse la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void showRecoveryError(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    )throws ServletException,IOException{
        request.setAttribute(
                "error",
                message
        );

        request.getRequestDispatcher(
                "/verify-email.jsp"
        ).forward(request,response);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param token token utilizado para validar la solicitud
     * @param message valor de message requerido por la operación
     * @throws ServletException si no puede completarse la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void showResetError(
            HttpServletRequest request,
            HttpServletResponse response,
            String token,
            String message
    )throws ServletException,IOException{
        request.setAttribute(
                "error",
                message
        );

        request.setAttribute(
                "token",
                token
        );

        request.getRequestDispatcher(
                "/reset-password.jsp"
        ).forward(request,response);
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
    private boolean isReasonableEmail(
            String email
    ){
        if(email==null
                ||email.isBlank()
                ||email.length()>100){
            return false;
        }

        int at=
                email.indexOf('@');

        return at>0
                &&at==email.lastIndexOf('@')
                &&at<email.length()-3
                &&email.indexOf('.',at)>at+1;
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
    private String normalizeEmail(
            String value
    ){
        if(value==null){
            return "";
        }

        return value.trim()
                .toLowerCase(Locale.ROOT);
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
    private String normalizeToken(
            String value
    ){
        return value==null
                ?""
                :value.trim();
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
    private String normalizeBaseUrl(
            String value
    ){
        if(value==null){
            return "";
        }

        String normalized=
                value.trim();

        while(normalized.endsWith("/")){
            normalized=
                    normalized.substring(
                            0,
                            normalized.length()-1
                    );
        }

        return normalized;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildFullName(
            User user
    ){
        if(user==null){
            return "usuario";
        }

        String fullName=
                String.join(
                                " ",
                                safeText(user.getName()),
                                safeText(user.getSurname()),
                                safeText(user.getLastname())
                        )
                        .trim()
                        .replaceAll(
                                "\\s+",
                                " "
                        );

        if(!fullName.isBlank()){
            return fullName;
        }

        return safeText(user.getEmail())
                .isBlank()
                ?"usuario"
                :safeText(user.getEmail());
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
    private String safeText(
            String value
    ){
        return value==null
                ?""
                :value.trim();
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
    private String escapeHtml(
            String value
    ){
        if(value==null){
            return "";
        }

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
     * @param response respuesta HTTP donde se escribirá el resultado
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void disableCache(
            HttpServletResponse response
    ){
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );
    }
}
