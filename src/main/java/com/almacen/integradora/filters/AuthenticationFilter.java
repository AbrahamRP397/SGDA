package com.almacen.integradora.filters;

import com.almacen.integradora.models.user.User;
import com.almacen.integradora.models.user.UserDao;
import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
/**
 * Define AuthenticationFilter y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebFilter("/*")
/** Filtro de autenticación y autorización para recursos protegidos por sesión.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class AuthenticationFilter implements Filter{
    private UserDao userDao;
    private Gson gson;

    private static final String ROLE_ADMIN="Administrador";

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
     *
     * @param filterConfig valor de filterConfig requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init(FilterConfig filterConfig){
        userDao=new UserDao();
        gson=new Gson();
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param servletRequest valor de servletRequest requerido por la operación
     * @param servletResponse valor de servletResponse requerido por la operación
     * @param chain valor de chain requerido por la operación
     * @throws IOException si no puede completarse la operación
     * @throws ServletException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    )throws IOException,ServletException{
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;

        String contextPath=request.getContextPath();
        String uri=request.getRequestURI();
        String path=uri.substring(contextPath.length());

        if(isPublicResource(path)){
            chain.doFilter(request,response);
            return;
        }

        HttpSession session=request.getSession(false);

        if(session==null){
            handleUnauthenticated(request,response,contextPath);
            return;
        }

        Object sessionUserValue=session.getAttribute("usuario");

        if(!(sessionUserValue instanceof User sessionUser)
                ||sessionUser.getId()==null
                ||sessionUser.getId()<=0
                ||sessionUser.getId()>Integer.MAX_VALUE){
            invalidateSession(session);
            handleUnauthenticated(request,response,contextPath);
            return;
        }

        User currentUser;

        try{
            currentUser=userDao.getById(
                    sessionUser.getId().intValue()
            );
        }catch(RuntimeException exception){
            request.getServletContext().log(
                    "No fue posible validar la sesión contra la base de datos.",
                    exception
            );
            handleInternalError(request,response);
            return;
        }

        if(currentUser==null||!Integer.valueOf(1).equals(currentUser.getStatus())){
            invalidateSession(session);
            handleUnauthenticated(request,response,contextPath);
            return;
        }

        currentUser.setPassword(null);
        session.setAttribute("usuario",currentUser);
        session.setAttribute("rol",currentUser.getRole());

        if(currentUser.requiresPasswordChange()){
            session.setAttribute(
                    "passwordChangeRequired",
                    true
            );

            if(!isAllowedDuringPasswordChange(path)){
                if(isAjaxRequest(request)){
                    sendJson(
                            response,
                            HttpServletResponse.SC_FORBIDDEN,
                            false,
                            "warning",
                            "Debes cambiar tu contraseña antes de continuar."
                    );
                }else{
                    response.sendRedirect(
                            contextPath+"/force-password-change"
                    );
                }
                return;
            }
        }else{
            session.removeAttribute(
                    "passwordChangeRequired"
            );

            if(path.equals("/force-password-change")){
                response.sendRedirect(
                        contextPath+"/dashboard"
                );
                return;
            }
        }

        if(isUserModule(path)
                &&!ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole())){
            handleForbidden(request,response);
            return;
        }

        chain.doFilter(request,response);
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param path valor de path requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isPublicResource(String path){
        if(path==null||path.isBlank()){
            return false;
        }

        return path.equals("/")
                ||path.equals("/index.jsp")
                ||path.equals("/login")
                ||path.equals("/login.jsp")
                ||path.equals("/verify-email")
                ||path.equals("/verify-email.jsp")
                ||path.equals("/reset-password")
                ||path.equals("/reset-password.jsp")
                ||path.equals("/password-reset-success")
                ||path.equals("/password-reset-success.jsp")
                ||path.startsWith("/assets/");
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param path valor de path requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isAllowedDuringPasswordChange(String path){
        return path.equals("/force-password-change")
                ||path.equals("/logout")
                ||path.startsWith("/assets/");
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param path valor de path requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isUserModule(String path){
        return path.equals("/users")
                ||path.equals("/users/list")
                ||path.startsWith("/user/");
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isAjaxRequest(HttpServletRequest request){
        String requestedWith=request.getHeader("X-Requested-With");

        if("XMLHttpRequest".equalsIgnoreCase(requestedWith)){
            return true;
        }

        String accept=request.getHeader("Accept");

        return accept!=null
                &&accept.toLowerCase(Locale.ROOT)
                .contains("application/json");
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param contextPath valor de contextPath requerido por la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void handleUnauthenticated(
            HttpServletRequest request,
            HttpServletResponse response,
            String contextPath
    )throws IOException{
        disableCache(response);

        if(isAjaxRequest(request)){
            sendJson(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    false,
                    "error",
                    "Tu sesión terminó o tu cuenta ya no está disponible. Inicia sesión nuevamente."
            );
            return;
        }

        response.sendRedirect(contextPath+"/login");
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
    private void handleForbidden(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        disableCache(response);

        if(isAjaxRequest(request)){
            sendJson(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    false,
                    "warning",
                    "No tienes permisos para realizar esta operación."
            );
            return;
        }

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "No tienes permisos para acceder a este recurso."
        );
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
    private void handleInternalError(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        disableCache(response);

        if(isAjaxRequest(request)){
            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible validar tu sesión. Intenta nuevamente."
            );
            return;
        }

        response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "No fue posible validar la sesión."
        );
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param session valor de session requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void invalidateSession(HttpSession session){
        if(session==null){
            return;
        }

        try{
            session.invalidate();
        }catch(IllegalStateException ignored){
        }
    }

    /**
     * Construye o envía la respuesta requerida por el cliente HTTP.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param statusCode estado que se utilizará en la operación
     * @param success valor de success requerido por la operación
     * @param type valor de type requerido por la operación
     * @param message valor de message requerido por la operación
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
            String message
    )throws IOException{
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String,Object> result=new LinkedHashMap<>();
        result.put("success",success);
        result.put("type",type);
        result.put("message",message);

        response.getWriter().write(
                gson.toJson(result)
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
    private void disableCache(HttpServletResponse response){
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0);
    }

    /**
     * Ejecuta la operación específica de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void destroy(){
        userDao=null;
        gson=null;
    }
}
