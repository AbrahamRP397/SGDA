package com.almacen.integradora.controllers;

import com.almacen.integradora.models.dashboard.ChartMovement;
import com.almacen.integradora.models.dashboard.DashboardDao;
import com.almacen.integradora.models.dashboard.DashboardMovement;
import com.almacen.integradora.models.dashboard.DashboardProduct;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
/**
 * Define DashboardServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name = "DashboardServlet",
        urlPatterns = {
                "/dashboard",
                "/dashboard/data",
                "/dashboard/chart"
        }
)
/** Controlador del tablero, sus indicadores y series para gráficas.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class DashboardServlet extends HttpServlet {

    private DashboardDao dashboardDao;
    private Gson gson;

    private static final Set<String> VALID_PERIODS = Set.of(
            "daily",
            "weekly",
            "monthly",
            "annual"
    );

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init() {
        dashboardDao = new DashboardDao();

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd'T'HH:mm:ss"
                );

        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>)
                                (value, type, context) ->
                                        value == null
                                                ? null
                                                : new JsonPrimitive(
                                                dateFormatter.format(value)
                                        )
                )
                .create();
    }

    /* ==========================================================
       GET
       ========================================================== */

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
    ) throws ServletException, IOException {

        disableCache(response);

        switch (request.getServletPath()) {

            case "/dashboard" ->
                    showDashboard(
                            request,
                            response
                    );

            case "/dashboard/data" ->
                    getDashboardData(
                            response
                    );

            case "/dashboard/chart" ->
                    getDashboardChart(
                            request,
                            response
                    );

            default ->
                    sendJson(
                            response,
                            HttpServletResponse.SC_NOT_FOUND,
                            false,
                            "error",
                            "La ruta solicitada no existe.",
                            null
                    );
        }
    }

    /* ==========================================================
       MOSTRAR VISTA
       ========================================================== */

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
    private void showDashboard(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String selectedPeriod =
                normalizePeriod(
                        request.getParameter("period")
                );

        List<DashboardMovement> recentMovements =
                new ArrayList<>();

        List<DashboardProduct> mostMovedProducts =
                new ArrayList<>();

        List<DashboardProduct> leastMovedProducts =
                new ArrayList<>();

        List<DashboardProduct> productsWithMostStock =
                new ArrayList<>();

        List<ChartMovement> chartMovements =
                new ArrayList<>();

        try {
            recentMovements =
                    dashboardDao.getRecentMovements();

            mostMovedProducts =
                    dashboardDao.getMostMovedProducts();

            leastMovedProducts =
                    dashboardDao.getLeastMovedProducts();

            productsWithMostStock =
                    dashboardDao.getProductsWithMostStock();

            chartMovements =
                    dashboardDao.getMovementsByPeriod(
                            selectedPeriod
                    );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al cargar la información del dashboard.",
                    exception
            );

            request.setAttribute(
                    "dashboardError",
                    "No fue posible cargar toda la información del dashboard."
            );
        }

        request.setAttribute(
                "recentMovements",
                recentMovements
        );

        request.setAttribute(
                "mostMovedProducts",
                mostMovedProducts
        );

        request.setAttribute(
                "leastMovedProducts",
                leastMovedProducts
        );

        request.setAttribute(
                "productsWithMostStock",
                productsWithMostStock
        );

        request.setAttribute(
                "chartMovements",
                chartMovements
        );

        request.setAttribute(
                "selectedPeriod",
                selectedPeriod
        );

        request.getRequestDispatcher(
                "/views/dashboard.jsp"
        ).forward(request, response);
    }

    /* ==========================================================
       DATOS GENERALES DEL DASHBOARD
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void getDashboardData(
            HttpServletResponse response
    ) throws IOException {

        try {
            List<DashboardMovement> recentMovements =
                    dashboardDao.getRecentMovements();

            List<DashboardProduct> mostMovedProducts =
                    dashboardDao.getMostMovedProducts();

            List<DashboardProduct> leastMovedProducts =
                    dashboardDao.getLeastMovedProducts();

            List<DashboardProduct> productsWithMostStock =
                    dashboardDao.getProductsWithMostStock();

            Map<String, Object> data =
                    new LinkedHashMap<>();

            data.put(
                    "recentMovements",
                    recentMovements
            );

            data.put(
                    "mostMovedProducts",
                    mostMovedProducts
            );

            data.put(
                    "leastMovedProducts",
                    leastMovedProducts
            );

            data.put(
                    "productsWithMostStock",
                    productsWithMostStock
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    data
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al consultar la información del dashboard.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cargar la información del dashboard.",
                    null
            );
        }
    }

    /* ==========================================================
       DATOS DE LA GRÁFICA
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void getDashboardChart(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String period =
                normalizePeriod(
                        request.getParameter("period")
                );

        try {
            List<ChartMovement> chartMovements =
                    dashboardDao.getMovementsByPeriod(
                            period
                    );

            Map<String, Object> data =
                    new LinkedHashMap<>();

            data.put(
                    "period",
                    period
            );

            data.put(
                    "movements",
                    chartMovements
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    data
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al consultar la gráfica del dashboard.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cargar la gráfica del dashboard.",
                    null
            );
        }
    }

    /* ==========================================================
       PERIODO
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param period valor de period requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizePeriod(
            String period
    ) {
        if (period == null || period.isBlank()) {
            return "monthly";
        }

        String normalizedPeriod =
                period.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return VALID_PERIODS.contains(
                normalizedPeriod
        )
                ? normalizedPeriod
                : "monthly";
    }

    /* ==========================================================
       RESPUESTA JSON
       ========================================================== */

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
    ) throws IOException {

        response.setStatus(statusCode);

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "success",
                success
        );

        result.put(
                "type",
                normalizeResponseType(
                        type,
                        success
                )
        );

        result.put(
                "message",
                message == null
                        ? ""
                        : message.trim()
        );

        if (data != null) {
            result.put(
                    "data",
                    data
            );
        }

        response.getWriter().write(
                gson.toJson(result)
        );
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
    private String normalizeResponseType(
            String type,
            boolean success
    ) {
        if (type == null || type.isBlank()) {
            return success
                    ? "success"
                    : "error";
        }

        String normalizedType =
                type.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return switch (normalizedType) {

            case "success",
                 "error",
                 "warning",
                 "info" ->
                    normalizedType;

            default ->
                    success
                            ? "success"
                            : "error";
        };
    }

    /* ==========================================================
       CACHE
       ========================================================== */

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
    ) {
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
