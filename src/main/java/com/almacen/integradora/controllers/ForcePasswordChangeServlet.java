package com.almacen.integradora.controllers;

import com.almacen.integradora.models.user.User;
import com.almacen.integradora.models.user.UserDao;
import com.almacen.integradora.utils.PasswordPolicy;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
/**
 * Define ForcePasswordChangeServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name="ForcePasswordChangeServlet",
        value="/force-password-change"
)
/** Controlador que obliga a sustituir una contraseña temporal antes de continuar.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class ForcePasswordChangeServlet extends HttpServlet{
    private UserDao userDao;

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
        User user=getSessionUser(session);

        if(user==null){
            response.sendRedirect(
                    request.getContextPath()+"/login"
            );
            return;
        }

        if(!user.requiresPasswordChange()){
            response.sendRedirect(
                    request.getContextPath()+"/dashboard"
            );
            return;
        }

        if(userDao.isTemporaryPasswordExpired(user)){
            session.invalidate();

            request.setAttribute(
                    "error",
                    "La contraseña temporal ha expirado. Solicita al administrador un nuevo acceso."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request,response);
            return;
        }

        request.setAttribute("user",user);

        request.getRequestDispatcher(
                "/force-password-change.jsp"
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
        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        HttpSession session=request.getSession(false);
        User user=getSessionUser(session);

        if(user==null){
            response.sendRedirect(
                    request.getContextPath()+"/login"
            );
            return;
        }

        if(!user.requiresPasswordChange()){
            response.sendRedirect(
                    request.getContextPath()+"/dashboard"
            );
            return;
        }

        if(userDao.isTemporaryPasswordExpired(user)){
            session.invalidate();

            request.setAttribute(
                    "error",
                    "La contraseña temporal ha expirado. Solicita al administrador un nuevo acceso."
            );

            request.getRequestDispatcher("/login.jsp")
                    .forward(request,response);
            return;
        }

        String password=request.getParameter("password");
        String confirmation=request.getParameter("confirmation");

        if(password==null
                ||password.isBlank()
                ||confirmation==null
                ||confirmation.isBlank()){
            showError(
                    request,
                    response,
                    user,
                    "Completa la contraseña y su confirmación."
            );
            return;
        }

        if(!PasswordPolicy.isValid(password)){
            showError(
                    request,
                    response,
                    user,
                    PasswordPolicy.getValidationMessage()
            );
            return;
        }

        if(!password.equals(confirmation)){
            showError(
                    request,
                    response,
                    user,
                    "Las contraseñas no coinciden."
            );
            return;
        }

        try{
            if(!userDao.updatePasswordById(
                    user.getId(),
                    password
            )){
                showError(
                        request,
                        response,
                        user,
                        "No fue posible actualizar la contraseña."
                );
                return;
            }
        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al cambiar la contraseña temporal del usuario.",
                    exception
            );

            showError(
                    request,
                    response,
                    user,
                    "No fue posible actualizar la contraseña."
            );
            return;
        }

        user.setPassword(null);
        user.setMustChangePassword(0);
        user.setTemporaryPasswordExpiration(null);

        session.setAttribute("usuario",user);
        session.setAttribute("rol",user.getRole());
        session.removeAttribute("passwordChangeRequired");

        response.sendRedirect(
                request.getContextPath()+"/dashboard"
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param user valor de user requerido por la operación
     * @param message valor de message requerido por la operación
     * @throws ServletException si no puede completarse la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void showError(
            HttpServletRequest request,
            HttpServletResponse response,
            User user,
            String message
    )throws ServletException,IOException{
        request.setAttribute("error",message);
        request.setAttribute("user",user);

        request.getRequestDispatcher(
                "/force-password-change.jsp"
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
    private User getSessionUser(HttpSession session){
        if(session==null){
            return null;
        }

        Object value=session.getAttribute("usuario");

        return value instanceof User user
                ?user
                :null;
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
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0);
    }
}
