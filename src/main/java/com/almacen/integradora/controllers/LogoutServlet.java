package com.almacen.integradora.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
/**
 * Define LogoutServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name="LogoutServlet",
        value="/logout"
)
/** Controlador que invalida la sesión y finaliza el acceso autenticado.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class LogoutServlet extends HttpServlet{

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
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        disableCache(response);

        HttpSession session=
                request.getSession(false);

        if(session!=null){
            try{
                session.invalidate();
            }catch(IllegalStateException ignored){
            }
        }

        response.sendRedirect(
                request.getContextPath()+"/login"
        );
    }

    /**
     * Atiende solicitudes HTTP GET y prepara la respuesta correspondiente.
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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        /*
         * Cerrar sesión modifica estado.
         *
         * Por ello ya no permitimos GET.
         */
        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Utiliza POST para cerrar sesión."
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
