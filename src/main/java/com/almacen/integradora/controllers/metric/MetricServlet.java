package com.almacen.integradora.controllers.metric;

import com.almacen.integradora.models.metric.Metric;
import com.almacen.integradora.models.metric.MetricDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * Define MetricServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name = "MetricServlet",
        urlPatterns = {
                "/metrics",
                "/metrics/list",
                "/metric/save",
                "/metric/update",
                "/metric/change-status"
        }
)
/** Controlador HTTP del catálogo de unidades de medida.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class MetricServlet extends HttpServlet {

    private MetricDao metricDao;
    private Gson gson;

    private static final Pattern NAME_PATTERN =
            Pattern.compile(
                    "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s.,()/'&+\\-]{2,100}$"
            );

    private static final Pattern SHORT_NAME_PATTERN =
            Pattern.compile(
                    "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9.%/\\-]{1,10}$"
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
        metricDao =
                new MetricDao();

        gson =
                new GsonBuilder()
                        .serializeNulls()
                        .create();
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
    ) throws ServletException, IOException {

        disableCache(response);

        switch (request.getServletPath()) {

            case "/metrics" ->
                    showMetrics(
                            request,
                            response
                    );

            case "/metrics/list" ->
                    listMetrics(
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
    ) throws IOException {

        disableCache(response);

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        switch (request.getServletPath()) {

            case "/metric/save" ->
                    saveMetric(
                            request,
                            response
                    );

            case "/metric/update" ->
                    updateMetric(
                            request,
                            response
                    );

            case "/metric/change-status" ->
                    changeMetricStatus(
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
    private void showMetrics(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<Metric> metrics =
                    metricDao.getAll();

            request.setAttribute(
                    "metrics",
                    metrics
            );

            request.getRequestDispatcher(
                    "/views/metric/metrics.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar las unidades de medida.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar las unidades de medida."
            );
        }
    }

    /* ==========================================================
       LISTA JSON
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void listMetrics(
            HttpServletResponse response
    ) throws IOException {

        try {
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    metricDao.getAll()
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar las unidades de medida.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar las unidades de medida.",
                    null
            );
        }
    }

    /* ==========================================================
       REGISTRAR
       ========================================================== */

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
    private void saveMetric(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String name =
                normalizeText(
                        request.getParameter(
                                "name"
                        )
                );

        String shortName =
                normalizeShortName(
                        request.getParameter(
                                "shortName"
                        )
                );

        try {
            if (name.isBlank()
                    || shortName.isBlank()) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Completa el nombre y la abreviatura de la unidad.",
                        null
                );

                return;
            }

            if (!isValidName(name)
                    || !isValidShortName(shortName)) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Verifica el nombre y la abreviatura de la unidad de medida.",
                        null
                );

                return;
            }

            /*
             * Buscamos incluso métricas inactivas.
             *
             * No queremos permitir duplicados simplemente porque
             * el registro anterior esté desactivado.
             */
            Metric existingByName =
                    metricDao.findAnyByName(
                            name
                    );

            if (existingByName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        Integer.valueOf(1)
                                .equals(existingByName.getStatus())
                                ? "Ya existe una unidad de medida con ese nombre."
                                : "Ya existe una unidad de medida inactiva con ese nombre. Reactívala en lugar de registrar otra.",
                        null
                );

                return;
            }

            Metric existingByShortName =
                    metricDao.findAnyByShortName(
                            shortName
                    );

            if (existingByShortName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        Integer.valueOf(1)
                                .equals(existingByShortName.getStatus())
                                ? "Ya existe una unidad de medida con esa abreviatura."
                                : "Ya existe una unidad de medida inactiva con esa abreviatura. Reactívala en lugar de registrar otra.",
                        null
                );

                return;
            }

            Metric metric =
                    new Metric();

            metric.setName(
                    name
            );

            metric.setShortName(
                    shortName
            );

            metric.setStatus(
                    1
            );

            if (!metricDao.create(metric)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible registrar la unidad de medida.",
                        null
                );

                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    true,
                    "success",
                    "La unidad de medida se registró correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error inesperado al registrar la unidad de medida.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible registrar la unidad de medida.",
                    null
            );
        }
    }

    /* ==========================================================
       ACTUALIZAR
       ========================================================== */

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
    private void updateMetric(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String idValue =
                normalizeText(
                        request.getParameter(
                                "id"
                        )
                );

        String name =
                normalizeText(
                        request.getParameter(
                                "name"
                        )
                );

        String shortName =
                normalizeShortName(
                        request.getParameter(
                                "shortName"
                        )
                );

        try {
            Long idMetric =
                    parsePositiveLong(
                            idValue
                    );

            if (idMetric == null
                    || idMetric > Integer.MAX_VALUE) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "El identificador de la unidad de medida no es válido.",
                        null
                );

                return;
            }

            Metric currentMetric =
                    metricDao.getById(
                            idMetric.intValue()
                    );

            if (currentMetric == null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "La unidad de medida solicitada no existe.",
                        null
                );

                return;
            }

            if (name.isBlank()
                    || shortName.isBlank()) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Completa el nombre y la abreviatura de la unidad.",
                        null
                );

                return;
            }

            if (!isValidName(name)
                    || !isValidShortName(shortName)) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Verifica el nombre y la abreviatura de la unidad de medida.",
                        null
                );

                return;
            }

            Metric existingByName =
                    metricDao.findAnyByNameExceptId(
                            name,
                            idMetric
                    );

            if (existingByName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe otra unidad de medida con ese nombre.",
                        null
                );

                return;
            }

            Metric existingByShortName =
                    metricDao.findAnyByShortNameExceptId(
                            shortName,
                            idMetric
                    );

            if (existingByShortName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe otra unidad de medida con esa abreviatura.",
                        null
                );

                return;
            }

            Metric metric =
                    new Metric();

            metric.setIdMetric(
                    idMetric
            );

            metric.setName(
                    name
            );

            metric.setShortName(
                    shortName
            );

            /*
             * update() no modifica status.
             *
             * Si la métrica estaba inactiva permanece inactiva.
             */
            if (!metricDao.update(metric)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible actualizar la unidad de medida.",
                        null
                );

                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "La unidad de medida se actualizó correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error inesperado al actualizar la unidad de medida.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible actualizar la unidad de medida.",
                    null
            );
        }
    }

    /* ==========================================================
       CAMBIAR ESTADO
       ========================================================== */

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
    private void changeMetricStatus(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String idValue =
                normalizeText(
                        request.getParameter(
                                "id"
                        )
                );

        String statusValue =
                normalizeText(
                        request.getParameter(
                                "status"
                        )
                );

        try {
            Long idMetric =
                    parsePositiveLong(
                            idValue
                    );

            Integer status =
                    parseStatus(
                            statusValue
                    );

            if (idMetric == null
                    || idMetric > Integer.MAX_VALUE
                    || status == null) {

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

            Metric metric =
                    metricDao.getById(
                            idMetric.intValue()
                    );

            if (metric == null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "La unidad de medida solicitada no existe.",
                        null
                );

                return;
            }

            if (Integer.valueOf(status)
                    .equals(metric.getStatus())) {

                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        true,
                        "info",
                        status == 1
                                ? "La unidad de medida ya se encuentra activa."
                                : "La unidad de medida ya se encuentra inactiva.",
                        null
                );

                return;
            }

            /*
             * ======================================================
             * REGLA DE DESACTIVACIÓN
             * ======================================================
             *
             * No permitimos:
             *
             * MÉTRICA INACTIVA
             *        ↓
             * PRODUCTO ACTIVO
             *
             * Primero deben cambiarse de métrica o desactivarse
             * esos productos.
             */
            if (status == 0) {
                int activeProducts =
                        metricDao.countActiveProductsUsingMetric(
                                idMetric
                        );

                if (activeProducts > 0) {
                    String message =
                            activeProducts == 1
                                    ? "No puedes desactivar esta unidad de medida porque está asociada a 1 producto activo. Cambia la unidad del producto o desactívalo primero."
                                    : "No puedes desactivar esta unidad de medida porque está asociada a "
                                    + activeProducts
                                    + " productos activos. Cambia la unidad de esos productos o desactívalos primero.";

                    Map<String, Object> data =
                            new LinkedHashMap<>();

                    data.put(
                            "activeProductCount",
                            activeProducts
                    );

                    data.put(
                            "idMetric",
                            idMetric
                    );

                    data.put(
                            "metricName",
                            metric.getName()
                    );

                    sendJson(
                            response,
                            HttpServletResponse.SC_CONFLICT,
                            false,
                            "warning",
                            message,
                            data
                    );

                    return;
                }
            }

            /*
             * La activación de una métrica no tiene dependencias
             * adicionales: al activarla vuelve a estar disponible
             * para nuevas asociaciones.
             */
            if (!metricDao.changeStatus(
                    idMetric,
                    status
            )) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible cambiar el estado de la unidad de medida.",
                        null
                );

                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    status == 1
                            ? "success"
                            : "warning",
                    status == 1
                            ? "La unidad de medida fue activada correctamente."
                            : "La unidad de medida fue desactivada correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al cambiar el estado de la unidad de medida.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cambiar el estado de la unidad de medida.",
                    null
            );
        }
    }

    /* ==========================================================
       JSON
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

        response.setStatus(
                statusCode
        );

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
        if (type == null
                || type.isBlank()) {

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
       VALIDACIONES
       ========================================================== */

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param name valor de name requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidName(
            String name
    ) {
        return name != null
                && NAME_PATTERN.matcher(
                name
        ).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param shortName valor de shortName requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidShortName(
            String shortName
    ) {
        return shortName != null
                && SHORT_NAME_PATTERN.matcher(
                shortName
        ).matches();
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
    private Long parsePositiveLong(
            String value
    ) {
        if (value == null
                || value.isBlank()) {

            return null;
        }

        try {
            long number =
                    Long.parseLong(
                            value
                    );

            return number > 0
                    ? number
                    : null;

        } catch (NumberFormatException exception) {
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
    private Integer parseStatus(
            String value
    ) {
        if ("0".equals(value)) {
            return 0;
        }

        if ("1".equals(value)) {
            return 1;
        }

        return null;
    }

    /* ==========================================================
       NORMALIZACIÓN
       ========================================================== */

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
    private String normalizeText(
            String value
    ) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
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
    private String normalizeShortName(
            String value
    ) {
        return normalizeText(
                value
        ).toUpperCase(
                Locale.ROOT
        );
    }

    /* ==========================================================
       HTTP
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
