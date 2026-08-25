package com.almacen.integradora.controllers.entry;

import com.almacen.integradora.models.entry.Entry;
import com.almacen.integradora.models.entry.EntryDao;
import com.almacen.integradora.models.entry.EntryProduct;
import com.almacen.integradora.models.provider.Provider;
import com.almacen.integradora.models.provider.ProviderDao;
import com.almacen.integradora.models.user.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
/**
 * Define EntryServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(name = "EntryServlet", urlPatterns = {
        "/entries",
        "/entries/list",
        "/entry/save"
})
/** Controlador HTTP para registrar y consultar entradas de inventario.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class EntryServlet extends HttpServlet {

    private EntryDao entryDao;
    private ProviderDao providerDao;
    private Gson gson;

    private static final Pattern INVOICE_PATTERN =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9._/#\\-\\s]{1,50}$");

    private static final BigDecimal MAX_UNIT_PRICE =
            new BigDecimal("9999999999.99");

    private static final int MAX_QUANTITY = 999_999_999;

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init() {
        entryDao = new EntryDao();
        providerDao = new ProviderDao();

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(
                        LocalDateTime.class,
                        (com.google.gson.JsonSerializer<LocalDateTime>)
                                (value, type, context) ->
                                        value == null
                                                ? null
                                                : new com.google.gson.JsonPrimitive(
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
            case "/entries" ->
                    showEntries(request, response);

            case "/entries/list" ->
                    listEntries(response);

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
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        disableCache(response);
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        switch (request.getServletPath()) {
            case "/entry/save" ->
                    saveEntry(request, response);

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
    private void showEntries(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<Entry> entries = entryDao.getAll();
            List<Provider> providers =
                    providerDao.getActiveProviders();

            request.setAttribute("entries", entries);
            request.setAttribute("providers", providers);

            request.getRequestDispatcher(
                    "/views/entry/entries.jsp"
            ).forward(request, response);

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar la información de entradas.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar las entradas."
            );
        }
    }

    /* ==========================================================
       LISTAR ENTRADAS EN JSON
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
    private void listEntries(
            HttpServletResponse response
    ) throws IOException {

        try {
            List<Entry> entries = entryDao.getAll();

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    entries
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar las entradas.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar las entradas.",
                    null
            );
        }
    }

    /* ==========================================================
       REGISTRAR ENTRADA
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
    private void saveEntry(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        User sessionUser = getSessionUser(request);

        if (sessionUser == null
                || sessionUser.getId() == null
                || sessionUser.getId() <= 0) {

            sendJson(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    false,
                    "error",
                    "La sesión no es válida. Inicia sesión nuevamente.",
                    null
            );
            return;
        }

        String invoiceNumber = normalizeInvoiceNumber(
                request.getParameter("invoiceNumber")
        );

        Long idProvider = parsePositiveLong(
                normalizeText(
                        request.getParameter("idProvider")
                )
        );

        try {
            if (invoiceNumber.isBlank()) {
                throw new ValidationException(
                        "Captura el número de factura o remisión."
                );
            }

            if (!INVOICE_PATTERN.matcher(invoiceNumber).matches()) {
                throw new ValidationException(
                        "El número de factura contiene caracteres no permitidos."
                );
            }

            if (idProvider == null) {
                throw new ValidationException(
                        "Selecciona un proveedor válido."
                );
            }

            Provider provider = providerDao.getById(
                    idProvider.intValue()
            );

            if (provider == null
                    || provider.getStatus() == null
                    || provider.getStatus() != 1) {

                throw new ValidationException(
                        "El proveedor seleccionado no existe o está inactivo."
                );
            }

            List<EntryProduct> products =
                    parseEntryProducts(request);

            if (products.isEmpty()) {
                throw new ValidationException(
                        "Agrega al menos un producto a la entrada."
                );
            }

            Entry entry = new Entry();
            entry.setInvoiceNumber(invoiceNumber);
            entry.setIdUser(sessionUser.getId());
            entry.setIdProvider(idProvider);
            entry.setProducts(products);

            if (!entryDao.create(entry)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible registrar la entrada.",
                        null
                );
                return;
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("idEntry", entry.getIdEntry());
            data.put("folioNumber", entry.getFolioNumber());
            data.put("totalAllPrices", entry.getTotalAllPrices());
            data.put("productCount", entry.getProductCount());

            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    true,
                    "success",
                    "La entrada " + entry.getFolioNumber()
                            + " se registró correctamente.",
                    data
            );

        } catch (ValidationException exception) {
            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    exception.getMessage(),
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error inesperado al registrar la entrada.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible registrar la entrada.",
                    null
            );
        }
    }

    /* ==========================================================
       LEER PRODUCTOS DEL FORMULARIO

       Espera:
       idProductProvider[]
       quantity[]
       unitPrice[]
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @return resultado producido por la operación
     * @throws ValidationException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private List<EntryProduct> parseEntryProducts(
            HttpServletRequest request
    ) throws ValidationException {

        String[] relationValues = getParameterValues(
                request,
                "idProductProvider[]",
                "idProductProvider"
        );

        String[] quantityValues = getParameterValues(
                request,
                "quantity[]",
                "quantity"
        );

        String[] priceValues = getParameterValues(
                request,
                "unitPrice[]",
                "unitPrice"
        );

        List<EntryProduct> products = new ArrayList<>();

        if (relationValues == null
                || relationValues.length == 0) {
            return products;
        }

        if (quantityValues == null
                || priceValues == null
                || relationValues.length != quantityValues.length
                || relationValues.length != priceValues.length) {

            throw new ValidationException(
                    "La información de los productos está incompleta."
            );
        }

        Set<Long> uniqueRelations = new HashSet<>();

        for (int index = 0;
             index < relationValues.length;
             index++) {

            String relationValue =
                    normalizeText(relationValues[index]);

            String quantityValue =
                    normalizeText(quantityValues[index]);

            String priceValue =
                    normalizeDecimal(priceValues[index]);

            if (relationValue.isBlank()
                    && quantityValue.isBlank()
                    && priceValue.isBlank()) {
                continue;
            }

            Long idProductProvider =
                    parsePositiveLong(relationValue);

            if (idProductProvider == null) {
                throw new ValidationException(
                        "Selecciona un producto válido en todas las filas."
                );
            }

            if (!uniqueRelations.add(idProductProvider)) {
                throw new ValidationException(
                        "No puedes agregar dos veces el mismo producto."
                );
            }

            Integer quantity =
                    parsePositiveInteger(quantityValue);

            if (quantity == null) {
                throw new ValidationException(
                        "Captura cantidades enteras mayores que cero."
                );
            }

            BigDecimal unitPrice =
                    parseUnitPrice(priceValue);

            EntryProduct product = new EntryProduct();
            product.setIdProductProvider(idProductProvider);
            product.setQuantity(quantity);
            product.setRemainingQuantity(quantity);
            product.setUnitPrice(unitPrice);
            product.setTotalPrice(
                    unitPrice.multiply(
                            BigDecimal.valueOf(quantity)
                    ).setScale(2, RoundingMode.HALF_UP)
            );

            products.add(product);
        }

        return products;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param primaryName valor de primaryName requerido por la operación
     * @param alternativeName valor de alternativeName requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String[] getParameterValues(
            HttpServletRequest request,
            String primaryName,
            String alternativeName
    ) {
        String[] values =
                request.getParameterValues(primaryName);

        if (values == null) {
            values = request.getParameterValues(
                    alternativeName
            );
        }

        return values;
    }

    /* ==========================================================
       USUARIO DE SESIÓN
       ========================================================== */

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
    private User getSessionUser(
            HttpServletRequest request
    ) {
        HttpSession session =
                request.getSession(false);

        if (session == null) {
            return null;
        }

        Object value =
                session.getAttribute("usuario");

        if (value instanceof User user) {
            return user;
        }

        return null;
    }

    /* ==========================================================
       VALIDACIÓN NUMÉRICA
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
    private Long parsePositiveLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            long number = Long.parseLong(value);

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
    private Integer parsePositiveInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            int number = Integer.parseInt(value);

            return number > 0 && number <= MAX_QUANTITY
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
     * @throws ValidationException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private BigDecimal parseUnitPrice(
            String value
    ) throws ValidationException {

        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    "Captura el precio unitario de todos los productos."
            );
        }

        try {
            BigDecimal price = new BigDecimal(value)
                    .setScale(2, RoundingMode.HALF_UP);

            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException(
                        "El precio unitario no puede ser negativo."
                );
            }

            if (price.compareTo(MAX_UNIT_PRICE) > 0) {
                throw new ValidationException(
                        "Uno de los precios supera el límite permitido."
                );
            }

            return price;

        } catch (NumberFormatException exception) {
            throw new ValidationException(
                    "Captura precios unitarios válidos."
            );
        }
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

        return value
                .trim()
                .replaceAll("\\s+", " ");
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
    private String normalizeInvoiceNumber(String value) {
        return normalizeText(value)
                .toUpperCase(Locale.ROOT);
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
    private String normalizeDecimal(String value) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replace(",", ".");
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
        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("success", success);
        result.put(
                "type",
                normalizeResponseType(type, success)
        );
        result.put(
                "message",
                message == null ? "" : message.trim()
        );

        if (data != null) {
            result.put("data", data);
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
            return success ? "success" : "error";
        }

        String normalizedType =
                type.trim().toLowerCase(Locale.ROOT);

        return switch (normalizedType) {
            case "success", "error", "warning", "info" ->
                    normalizedType;

            default ->
                    success ? "success" : "error";
        };
    }

    /* ==========================================================
       ERRORES
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
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private static class ValidationException
            extends Exception {

        public ValidationException(String message) {
            super(message);
        }
    }
}
