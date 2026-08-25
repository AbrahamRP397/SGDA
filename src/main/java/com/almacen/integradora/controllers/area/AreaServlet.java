package com.almacen.integradora.controllers.area;

import com.almacen.integradora.models.area.Area;
import com.almacen.integradora.models.area.AreaDao;
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
 * Define AreaServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(name = "AreaServlet", urlPatterns = {
        "/areas",
        "/areas/list",
        "/area/save",
        "/area/update",
        "/area/change-status"
})
/** Controlador HTTP del catálogo de áreas de destino.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class AreaServlet extends HttpServlet {

    private AreaDao areaDao;
    private Gson gson;

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s.,()/'&+\\-]{2,100}$");

    private static final Pattern SHORT_NAME_PATTERN =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9._\\-]{1,20}$");

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init() {
        areaDao = new AreaDao();
        gson = new GsonBuilder().serializeNulls().create();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        disableCache(response);

        switch (request.getServletPath()) {
            case "/areas" -> showAreas(request, response);
            case "/areas/list" -> listAreas(response);
            default -> sendJson(
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
       POST
       ========================================================== */

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        disableCache(response);
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        switch (request.getServletPath()) {
            case "/area/save" -> saveArea(request, response);
            case "/area/update" -> updateArea(request, response);
            case "/area/change-status" -> changeAreaStatus(request, response);
            default -> sendJson(
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
    private void showAreas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Area> areas = areaDao.getAll();
            request.setAttribute("areas", areas);

            request.getRequestDispatcher("/views/area/areas.jsp")
                    .forward(request, response);

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar las áreas de destino.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar las áreas de destino."
            );
        }
    }

    /* ==========================================================
       LISTAR EN JSON
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
    private void listAreas(HttpServletResponse response) throws IOException {
        try {
            List<Area> areas = areaDao.getAll();

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    areas
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar las áreas de destino.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar las áreas de destino.",
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
    private void saveArea(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String shortName = normalizeShortName(request.getParameter("shortName"));
        String name = normalizeText(request.getParameter("name"));
        String description = normalizeDescription(request.getParameter("description"));

        try {
            if (shortName.isBlank() || name.isBlank()) {
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Completa el nombre y la abreviatura del área.",
                        null
                );
                return;
            }

            if (!isValidShortName(shortName)
                    || !isValidName(name)
                    || !isValidDescription(description)) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Verifica los datos ingresados para el área.",
                        null
                );
                return;
            }

            Area existingByName =
                    areaDao.findAnyByName(name);

            if (existingByName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        Integer.valueOf(1)
                                .equals(existingByName.getStatus())
                                ? "Ya existe un área con ese nombre."
                                : "Ya existe un área inactiva con ese nombre. Reactívala en lugar de registrar otra.",
                        null
                );

                return;
            }

            Area existingByShortName =
                    areaDao.findAnyByShortName(shortName);

            if (existingByShortName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        Integer.valueOf(1)
                                .equals(existingByShortName.getStatus())
                                ? "Ya existe un área con esa abreviatura."
                                : "Ya existe un área inactiva con esa abreviatura. Reactívala en lugar de registrar otra.",
                        null
                );

                return;
            }

            Area area = new Area();
            area.setShortName(shortName);
            area.setName(name);
            area.setDescription(description);
            area.setStatus(1);

            if (!areaDao.create(area)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible registrar el área de destino.",
                        null
                );
                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    true,
                    "success",
                    "El área de destino se registró correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error inesperado al registrar el área de destino.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible registrar el área de destino.",
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
    private void updateArea(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idValue = normalizeText(request.getParameter("id"));
        String shortName = normalizeShortName(request.getParameter("shortName"));
        String name = normalizeText(request.getParameter("name"));
        String description = normalizeDescription(request.getParameter("description"));

        try {
            Long idArea = parsePositiveLong(idValue);

            if (idArea == null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "El identificador del área no es válido.",
                        null
                );
                return;
            }

            Area currentArea = areaDao.getById(idArea.intValue());

            if (currentArea == null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El área solicitada no existe.",
                        null
                );
                return;
            }

            if (shortName.isBlank() || name.isBlank()) {
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Completa el nombre y la abreviatura del área.",
                        null
                );
                return;
            }

            if (!isValidShortName(shortName)
                    || !isValidName(name)
                    || !isValidDescription(description)) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Verifica los datos ingresados para el área.",
                        null
                );
                return;
            }

            Area existingByName =
                    areaDao.findAnyByNameExceptId(name, idArea);

            if (existingByName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe otra área con ese nombre.",
                        null
                );
                return;
            }

            Area existingByShortName =
                    areaDao.findAnyByShortNameExceptId(shortName, idArea);

            if (existingByShortName != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe otra área con esa abreviatura.",
                        null
                );
                return;
            }

            Area area = new Area();
            area.setIdArea(idArea);
            area.setShortName(shortName);
            area.setName(name);
            area.setDescription(description);

            if (!areaDao.update(area)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible actualizar el área de destino.",
                        null
                );
                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "El área de destino se actualizó correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error inesperado al actualizar el área de destino.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible actualizar el área de destino.",
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
    private void changeAreaStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String idValue = normalizeText(request.getParameter("id"));
        String statusValue = normalizeText(request.getParameter("status"));

        try {
            Long idArea = parsePositiveLong(idValue);
            Integer status = parseStatus(statusValue);

            if (idArea == null || status == null) {
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

            Area area = areaDao.getById(idArea.intValue());

            if (area == null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El área solicitada no existe.",
                        null
                );
                return;
            }

            if (area.getStatus() != null && area.getStatus().equals(status)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        true,
                        "info",
                        status == 1
                                ? "El área ya se encuentra activa."
                                : "El área ya se encuentra inactiva.",
                        null
                );
                return;
            }

            if (!areaDao.changeStatus(idArea, status)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible cambiar el estado del área.",
                        null
                );
                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    status == 1 ? "success" : "warning",
                    status == 1
                            ? "El área fue activada correctamente."
                            : "El área fue desactivada correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al cambiar el estado del área de destino.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cambiar el estado del área.",
                    null
            );
        }
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
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", success);
        result.put("type", normalizeResponseType(type, success));
        result.put("message", message == null ? "" : message.trim());

        if (data != null) {
            result.put("data", data);
        }

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
    private String normalizeResponseType(String type, boolean success) {
        if (type == null || type.isBlank()) {
            return success ? "success" : "error";
        }

        String normalizedType = type.trim().toLowerCase(Locale.ROOT);

        return switch (normalizedType) {
            case "success", "error", "warning", "info" -> normalizedType;
            default -> success ? "success" : "error";
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
    private boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
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
    private boolean isValidShortName(String shortName) {
        return shortName != null && SHORT_NAME_PATTERN.matcher(shortName).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param description valor de description requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidDescription(String description) {
        return description != null && description.length() <= 500;
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
    private Long parsePositiveLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            long number = Long.parseLong(value);
            return number > 0 ? number : null;
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
    private Integer parseStatus(String value) {
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
    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replaceAll("\\s+", " ");
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
    private String normalizeShortName(String value) {
        return normalizeText(value).toUpperCase(Locale.ROOT);
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
    private String normalizeDescription(String value) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceAll("[\\t\\x0B\\f\\r ]+", " ")
                .replaceAll("\\n{3,}", "\n\n");
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
    private void disableCache(HttpServletResponse response) {
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
