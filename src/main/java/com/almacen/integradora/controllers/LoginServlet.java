package com.almacen.integradora.controllers;

import com.almacen.integradora.models.user.User;
import com.almacen.integradora.models.user.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;
/**
 * Define LoginServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(name="LoginServlet",value="/login")
/** Controlador del inicio de sesión y de las redirecciones asociadas al estado de la cuenta.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class LoginServlet extends HttpServlet{
    private UserDao userDao;

    private static final int SESSION_TIMEOUT_SECONDS=30*60;
    private static final int MAX_LOGIN_ATTEMPTS=5;
    private static final long LOGIN_BLOCK_MILLIS=5*60*1000L;

    private static final String ATTEMPTS_ATTRIBUTE="loginAttempts";
    private static final String BLOCK_UNTIL_ATTRIBUTE="loginBlockedUntil";

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

        HttpSession session=request.getSession(false);

        if(session!=null){
            User user=getSessionUser(session);

            if(user!=null){
                if(user.requiresPasswordChange()){
                    session.setAttribute(
                            "passwordChangeRequired",
                            true
                    );

                    response.sendRedirect(
                            request.getContextPath()
                                    +"/force-password-change"
                    );
                    return;
                }

                response.sendRedirect(
                        request.getContextPath()
                                +"/dashboard"
                );
                return;
            }
        }

        request.getRequestDispatcher(
                "/login.jsp"
        ).forward(request,response);
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
        request.setCharacterEncoding("UTF-8");

        HttpSession session=
                request.getSession(true);

        if(isLoginBlocked(session)){
            long remainingSeconds=
                    getRemainingBlockSeconds(
                            session
                    );

            request.setAttribute(
                    "error",
                    "Has realizado demasiados intentos. Intenta nuevamente en "
                            +remainingSeconds
                            +" segundos."
            );

            request.getRequestDispatcher(
                    "/login.jsp"
            ).forward(request,response);

            return;
        }

        String email=
                normalizeEmail(
                        request.getParameter(
                                "email"
                        )
                );

        String password=
                request.getParameter(
                        "password"
                );

        if(email.isBlank()
                ||password==null
                ||password.isBlank()){

            request.setAttribute(
                    "error",
                    "Completa el correo electrónico y la contraseña."
            );

            request.setAttribute(
                    "emailIngresado",
                    email
            );

            request.getRequestDispatcher(
                    "/login.jsp"
            ).forward(request,response);

            return;
        }

        User user;

        try{
            user=userDao.login(
                    email,
                    password
            );

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al intentar iniciar sesión.",
                    exception
            );

            request.setAttribute(
                    "error",
                    "No fue posible iniciar sesión en este momento. Intenta nuevamente."
            );

            request.setAttribute(
                    "emailIngresado",
                    email
            );

            request.getRequestDispatcher(
                    "/login.jsp"
            ).forward(request,response);

            return;
        }

        if(user==null){
            registerFailedAttempt(
                    session
            );

            request.setAttribute(
                    "error",
                    isLoginBlocked(session)
                            ?"Has realizado demasiados intentos. Intenta nuevamente en unos minutos."
                            :"Los datos ingresados son incorrectos."
            );

            request.setAttribute(
                    "emailIngresado",
                    email
            );

            request.getRequestDispatcher(
                    "/login.jsp"
            ).forward(request,response);

            return;
        }

        /*
         * Limpiamos los intentos fallidos porque las credenciales
         * ya fueron validadas correctamente.
         */
        clearLoginAttempts(
                session
        );

        /*
         * Validar contraseña temporal antes de establecer
         * definitivamente la sesión autenticada.
         */
        if(user.requiresPasswordChange()){
            boolean temporaryPasswordExpired;

            try{
                temporaryPasswordExpired=
                        userDao.isTemporaryPasswordExpired(
                                user
                        );

            }catch(RuntimeException exception){
                getServletContext().log(
                        "Error al validar la vigencia de la contraseña temporal.",
                        exception
                );

                request.setAttribute(
                        "error",
                        "No fue posible validar el acceso temporal. Intenta nuevamente."
                );

                request.setAttribute(
                        "emailIngresado",
                        email
                );

                request.getRequestDispatcher(
                        "/login.jsp"
                ).forward(request,response);

                return;
            }

            if(temporaryPasswordExpired){
                request.setAttribute(
                        "error",
                        "La contraseña temporal ha expirado. Solicita al administrador un nuevo acceso."
                );

                request.setAttribute(
                        "emailIngresado",
                        email
                );

                request.getRequestDispatcher(
                        "/login.jsp"
                ).forward(request,response);

                return;
            }
        }

        /*
         * ======================================================
         * SEGURIDAD DE SESIÓN
         * ======================================================
         *
         * No invalidamos la sesión y creamos otra.
         *
         * changeSessionId() cambia únicamente el identificador
         * JSESSIONID manteniendo los atributos y la asociación
         * correcta con el navegador.
         *
         * Esto protege contra ataques de session fixation
         * sin provocar pérdida de sesión.
         */
        try{
            request.changeSessionId();
        }catch(IllegalStateException ignored){
            /*
             * Si por alguna razón el contenedor no puede cambiar
             * el ID, conservamos la sesión actual.
             */
        }

        /*
         * Guardar usuario autenticado.
         */
        session.setAttribute(
                "usuario",
                user
        );

        session.setAttribute(
                "rol",
                user.getRole()
        );

        session.setMaxInactiveInterval(
                SESSION_TIMEOUT_SECONDS
        );

        if(user.requiresPasswordChange()){
            session.setAttribute(
                    "passwordChangeRequired",
                    true
            );

            response.sendRedirect(
                    request.getContextPath()
                            +"/force-password-change"
            );

            return;
        }

        session.removeAttribute(
                "passwordChangeRequired"
        );

        /*
         * Mantengo tu animación actual de inicio de sesión.
         *
         * login.jsp recibe loginExitoso=true y después
         * login-animation.js realiza la navegación.
         */
        request.setAttribute(
                "loginExitoso",
                true
        );

        request.setAttribute(
                "redirectUrl",
                request.getContextPath()
                        +"/dashboard"
        );

        request.getRequestDispatcher(
                "/login.jsp"
        ).forward(request,response);
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param session valor de session requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private User getSessionUser(
            HttpSession session
    ){
        if(session==null){
            return null;
        }

        Object value=
                session.getAttribute(
                        "usuario"
                );

        return value instanceof User user
                ?user
                :null;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param session valor de session requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isLoginBlocked(
            HttpSession session
    ){
        if(session==null){
            return false;
        }

        Object value=
                session.getAttribute(
                        BLOCK_UNTIL_ATTRIBUTE
                );

        if(!(value instanceof Long blockedUntil)){
            return false;
        }

        long now=
                System.currentTimeMillis();

        if(blockedUntil<=now){
            clearLoginAttempts(
                    session
            );
            return false;
        }

        return true;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param session valor de session requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private long getRemainingBlockSeconds(
            HttpSession session
    ){
        if(session==null){
            return 0;
        }

        Object value=
                session.getAttribute(
                        BLOCK_UNTIL_ATTRIBUTE
                );

        if(!(value instanceof Long blockedUntil)){
            return 0;
        }

        long remaining=
                blockedUntil
                        -System.currentTimeMillis();

        if(remaining<=0){
            return 0;
        }

        return Math.max(
                1,
                (long)Math.ceil(
                        remaining/1000.0
                )
        );
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param session valor de session requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void registerFailedAttempt(
            HttpSession session
    ){
        if(session==null){
            return;
        }

        int attempts=
                getLoginAttempts(
                        session
                )+1;

        session.setAttribute(
                ATTEMPTS_ATTRIBUTE,
                attempts
        );

        if(attempts>=MAX_LOGIN_ATTEMPTS){
            session.setAttribute(
                    BLOCK_UNTIL_ATTRIBUTE,
                    System.currentTimeMillis()
                            +LOGIN_BLOCK_MILLIS
            );
        }
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param session valor de session requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private int getLoginAttempts(
            HttpSession session
    ){
        if(session==null){
            return 0;
        }

        Object value=
                session.getAttribute(
                        ATTEMPTS_ATTRIBUTE
                );

        return value instanceof Integer attempts
                ?attempts
                :0;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param session valor de session requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void clearLoginAttempts(
            HttpSession session
    ){
        if(session==null){
            return;
        }

        session.removeAttribute(
                ATTEMPTS_ATTRIBUTE
        );

        session.removeAttribute(
                BLOCK_UNTIL_ATTRIBUTE
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
    private String normalizeEmail(
            String value
    ){
        if(value==null){
            return "";
        }

        return value.trim()
                .toLowerCase(
                        Locale.ROOT
                );
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
