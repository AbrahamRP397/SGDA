package com.almacen.integradora.controllers.exit;

import com.almacen.integradora.models.area.Area;
import com.almacen.integradora.models.area.AreaDao;
import com.almacen.integradora.models.exit.Exit;
import com.almacen.integradora.models.exit.ExitDao;
import com.almacen.integradora.models.exit.ExitProduct;
import com.almacen.integradora.models.product.Product;
import com.almacen.integradora.models.product.ProductDao;
import com.almacen.integradora.models.stock.Stock;
import com.almacen.integradora.models.stock.StockDao;
import com.almacen.integradora.models.user.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
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
 * Define ExitServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(name = "ExitServlet", urlPatterns = {
        "/exits",
        "/exits/list",
        "/exits/products",
        "/exit/save"
})
/** Controlador HTTP para registrar y consultar salidas de inventario.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class ExitServlet extends HttpServlet {

    private ExitDao exitDao;
    private AreaDao areaDao;
    private ProductDao productDao;
    private StockDao stockDao;
    private Gson gson;

    private static final int MAX_QUANTITY = 999_999_999;

    private static final Pattern DOCUMENT_PATTERN =
            Pattern.compile(
                    "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9._/#\\-\\s]{1,50}$"
            );

    private static final Pattern BUYER_PATTERN =
            Pattern.compile(
                    "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s.'\\-]{2,150}$"
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

        exitDao = new ExitDao();
        areaDao = new AreaDao();
        productDao = new ProductDao();
        stockDao = new StockDao();

        DateTimeFormatter formatter =
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
                                                formatter.format(value)
                                        )
                )
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

            case "/exits" ->
                    showExits(
                            request,
                            response
                    );

            case "/exits/list" ->
                    listExits(
                            response
                    );

            case "/exits/products" ->
                    listAvailableProducts(
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

            case "/exit/save" ->
                    saveExit(
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
    private void showExits(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {

            request.setAttribute(
                    "exits",
                    exitDao.getAll()
            );

            request.setAttribute(
                    "areas",
                    areaDao.getActiveAreas()
            );

            request.getRequestDispatcher(
                    "/views/exit/exits.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al consultar la información de salidas.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar las salidas."
            );
        }
    }

    /* ==========================================================
       LISTAR SALIDAS
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
    private void listExits(
            HttpServletResponse response
    ) throws IOException {

        try {

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    exitDao.getAll()
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al consultar las salidas.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar las salidas.",
                    null
            );
        }
    }

    /* ==========================================================
       PRODUCTOS CON EXISTENCIA DISPONIBLE PARA SALIDA

       IMPORTANTE:

       Un producto puede aparecer aunque esté inactivo.

       Producto activo:
       - Permite entradas.
       - Permite salidas.

       Producto inactivo:
       - NO permite entradas.
       - SÍ permite salidas mientras tenga existencia.

       Esto permite desalojar físicamente stock existente sin
       volver a habilitar el producto para reabastecimiento.
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
    private void listAvailableProducts(
            HttpServletResponse response
    ) throws IOException {

        try {
            List<Product> products =
                    productDao.getAll();

            List<Stock> stocks =
                    stockDao.getAllStock();

            Map<Long, Long> stockByProduct =
                    new LinkedHashMap<>();

            for (Stock stock : stocks) {
                if (stock == null
                        || stock.getIdProduct() == null
                        || stock.getIdProduct() <= 0
                        || stock.getQuantity() == null
                        || stock.getQuantity() <= 0) {

                    continue;
                }

                stockByProduct.merge(
                        stock.getIdProduct(),
                        stock.getQuantity().longValue(),
                        Long::sum
                );
            }

            List<Map<String, Object>> availableProducts =
                    new ArrayList<>();

            for (Product product : products) {
                if (product == null
                        || product.getIdProduct() == null
                        || product.getIdProduct() <= 0) {

                    continue;
                }

                long quantity =
                        stockByProduct.getOrDefault(
                                product.getIdProduct(),
                                0L
                        );

                /*
                 * Salidas se basan en existencia física.
                 *
                 * El estado del producto NO bloquea una salida.
                 */
                if (quantity <= 0) {
                    continue;
                }

                boolean active =
                        Integer.valueOf(1)
                                .equals(
                                        product.getStatus()
                                );

                Map<String, Object> item =
                        new LinkedHashMap<>();

                item.put(
                        "idProduct",
                        product.getIdProduct()
                );

                item.put(
                        "code",
                        product.getCode()
                );

                item.put(
                        "name",
                        product.getName()
                );

                item.put(
                        "metricName",
                        product.getMetricName()
                );

                item.put(
                        "metricShortName",
                        product.getMetricShortName()
                );

                item.put(
                        "availableQuantity",
                        quantity
                );

                item.put(
                        "status",
                        active ? 1 : 0
                );

                item.put(
                        "active",
                        active
                );

                item.put(
                        "statusLabel",
                        active
                                ? "Activo"
                                : "Inactivo - solo salida"
                );

                availableProducts.add(
                        item
                );
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    availableProducts
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar los productos disponibles para salida.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar los productos disponibles.",
                    null
            );
        }
    }

    /* ==========================================================
       REGISTRAR SALIDA
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
    private void saveExit(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        User sessionUser =
                getSessionUser(
                        request
                );

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

        String invoiceNumber =
                normalizeDocumentNumber(
                        request.getParameter(
                                "invoiceNumber"
                        )
                );

        String buyerName =
                normalizePersonName(
                        request.getParameter(
                                "buyerName"
                        )
                );

        Long idArea =
                parsePositiveLong(
                        normalizeText(
                                request.getParameter(
                                        "idArea"
                                )
                        )
                );

        try {

            validateHeader(
                    invoiceNumber,
                    buyerName,
                    idArea
            );

            Area area =
                    areaDao.getById(
                            idArea.intValue()
                    );

            if (area == null
                    || area.getStatus() == null
                    || area.getStatus() != 1) {

                throw new ValidationException(
                        "El área seleccionada no existe o se encuentra inactiva."
                );
            }

            List<ExitProduct> products =
                    parseExitProducts(
                            request
                    );

            if (products.isEmpty()) {

                throw new ValidationException(
                        "Agrega al menos un producto a la salida."
                );
            }

            Exit exit =
                    new Exit();

            exit.setInvoiceNumber(
                    invoiceNumber
            );

            exit.setBuyerName(
                    buyerName
            );

            exit.setIdArea(
                    idArea
            );

            exit.setIdUser(
                    sessionUser.getId()
            );

            exit.setProducts(
                    products
            );

            if (!exitDao.create(exit)) {

                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible registrar la salida.",
                        null
                );

                return;
            }

            Map<String, Object> data =
                    new LinkedHashMap<>();

            data.put(
                    "idExit",
                    exit.getIdExit()
            );

            data.put(
                    "folioNumber",
                    exit.getFolioNumber()
            );

            data.put(
                    "totalAllPrices",
                    exit.getTotalAllPrices()
            );

            data.put(
                    "productCount",
                    exit.getProductCount()
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    true,
                    "success",
                    "La salida "
                            + exit.getFolioNumber()
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
                    "Error inesperado al registrar la salida.",
                    exception
            );

            Throwable rootCause =
                    getRootCause(
                            exception
                    );

            String technicalMessage =
                    rootCause != null
                            && rootCause.getMessage() != null
                            ? rootCause.getMessage()
                            : "";

            String userMessage =
                    extractSafeBusinessMessage(
                            technicalMessage
                    );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    userMessage.startsWith(
                            "Stock insuficiente"
                    )
                            ? "warning"
                            : "error",
                    userMessage,
                    null
            );
        }
    }

    /* ==========================================================
       VALIDAR ENCABEZADO
       ========================================================== */

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param invoiceNumber valor de invoiceNumber requerido por la operación
     * @param buyerName valor de buyerName requerido por la operación
     * @param idArea identificador del registro relacionado con la operación
     * @throws ValidationException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void validateHeader(
            String invoiceNumber,
            String buyerName,
            Long idArea
    ) throws ValidationException {

        if (invoiceNumber.isBlank()) {

            throw new ValidationException(
                    "Captura el número de vale, factura o documento de salida."
            );
        }

        if (!DOCUMENT_PATTERN.matcher(
                invoiceNumber
        ).matches()) {

            throw new ValidationException(
                    "El número de documento contiene caracteres no permitidos."
            );
        }

        if (buyerName.isBlank()) {

            throw new ValidationException(
                    "Captura el nombre de la persona que recibe los productos."
            );
        }

        if (!BUYER_PATTERN.matcher(
                buyerName
        ).matches()) {

            throw new ValidationException(
                    "El nombre de la persona que recibe no es válido."
            );
        }

        if (idArea == null) {

            throw new ValidationException(
                    "Selecciona un área de destino válida."
            );
        }
    }

    /* ==========================================================
       PRODUCTOS SOLICITADOS

       IMPORTANTE:
       aquí NO exigimos que product.status sea 1.

       Para Salidas únicamente necesitamos:
       - que exista;
       - que tenga stock;
       - que alcance la existencia.

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
    private List<ExitProduct> parseExitProducts(
            HttpServletRequest request
    ) throws ValidationException {

        String[] productValues =
                getParameterValues(
                        request,
                        "idProduct[]",
                        "idProduct"
                );

        String[] quantityValues =
                getParameterValues(
                        request,
                        "quantity[]",
                        "quantity"
                );

        List<ExitProduct> products =
                new ArrayList<>();

        if (productValues == null
                || productValues.length == 0) {

            return products;
        }

        if (quantityValues == null
                || productValues.length
                != quantityValues.length) {

            throw new ValidationException(
                    "La información de los productos está incompleta."
            );
        }

        Set<Long> uniqueProductIds =
                new HashSet<>();

        for (
                int index = 0;
                index < productValues.length;
                index++
        ) {

            String productValue =
                    normalizeText(
                            productValues[index]
                    );

            String quantityValue =
                    normalizeText(
                            quantityValues[index]
                    );

            if (productValue.isBlank()
                    && quantityValue.isBlank()) {

                continue;
            }

            Long idProduct =
                    parsePositiveLong(
                            productValue
                    );

            if (idProduct == null
                    || idProduct > Integer.MAX_VALUE) {

                throw new ValidationException(
                        "Selecciona un producto válido en todas las filas."
                );
            }

            if (!uniqueProductIds.add(
                    idProduct
            )) {

                throw new ValidationException(
                        "No puedes agregar dos veces el mismo producto."
                );
            }

            Integer quantity =
                    parsePositiveInteger(
                            quantityValue
                    );

            if (quantity == null) {

                throw new ValidationException(
                        "Captura cantidades enteras mayores que cero."
                );
            }

            /*
             * Solamente comprobamos existencia.
             *
             * Ya NO rechazamos product.status = 0.
             */
            Product product =
                    productDao.getById(
                            idProduct.intValue()
                    );

            if (product == null) {

                throw new ValidationException(
                        "Uno de los productos seleccionados ya no existe."
                );
            }

            int availableQuantity =
                    stockDao.getTotalQuantityByProduct(
                            idProduct
                    );

            if (availableQuantity <= 0) {

                throw new ValidationException(
                        "El producto "
                                + safeProductName(product)
                                + " ya no tiene existencia disponible."
                );
            }

            if (availableQuantity < quantity) {
                throw new ValidationException(
                        "Stock insuficiente para "
                                + product.getName()
                                + ". Disponible: "
                                + availableQuantity
                                + ", solicitado: "
                                + quantity
                                + "."
                );
            }

            ExitProduct exitProduct =
                    new ExitProduct();

            exitProduct.setIdProduct(
                    idProduct
            );

            exitProduct.setProductCode(
                    product.getCode()
            );

            exitProduct.setProductName(
                    product.getName()
            );

            exitProduct.setIdMetric(
                    product.getIdMetric()
            );

            exitProduct.setMetricName(
                    product.getMetricName()
            );

            exitProduct.setMetricShortName(
                    product.getMetricShortName()
            );

            exitProduct.setQuantity(
                    quantity
            );

            products.add(
                    exitProduct
            );
        }

        return products;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param product valor de product requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String safeProductName(
            Product product
    ) {

        if (product == null
                || product.getName() == null
                || product.getName().isBlank()) {

            return "seleccionado";
        }

        return product.getName().trim();
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
                request.getParameterValues(
                        primaryName
                );

        return values != null
                ? values
                : request.getParameterValues(
                alternativeName
        );
    }

    /* ==========================================================
       SESIÓN
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
                session.getAttribute(
                        "usuario"
                );

        return value instanceof User user
                ? user
                : null;
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

        String normalized =
                type.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return switch (normalized) {

            case "success",
                 "error",
                 "warning",
                 "info" ->
                    normalized;

            default ->
                    success
                            ? "success"
                            : "error";
        };
    }

    /* ==========================================================
       ERRORES
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param throwable valor de throwable requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private Throwable getRootCause(
            Throwable throwable
    ) {

        if (throwable == null) {
            return null;
        }

        Throwable current =
                throwable;

        while (current.getCause() != null
                && current.getCause() != current) {

            current =
                    current.getCause();
        }

        return current;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param technicalMessage valor de technicalMessage requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String extractSafeBusinessMessage(
            String technicalMessage
    ) {

        if (technicalMessage == null
                || technicalMessage.isBlank()) {

            return "No fue posible registrar la salida.";
        }

        if (technicalMessage.startsWith(
                "Stock insuficiente"
        )) {

            return technicalMessage;
        }

        if (technicalMessage.contains(
                "área seleccionada"
        )) {

            return technicalMessage;
        }

        if (technicalMessage.contains(
                "producto"
        )
                && technicalMessage.contains(
                "no existe"
        )) {

            return technicalMessage;
        }

        if (technicalMessage.contains(
                "existencia"
        )) {

            return technicalMessage;
        }

        return "No fue posible registrar la salida.";
    }

    /* ==========================================================
       CONVERSIONES
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
    private Integer parsePositiveInteger(
            String value
    ) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        try {

            int number =
                    Integer.parseInt(
                            value
                    );

            return number > 0
                    && number <= MAX_QUANTITY
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
    private String normalizeText(
            String value
    ) {

        return value == null
                ? ""
                : value.trim()
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
    private String normalizeDocumentNumber(
            String value
    ) {

        return normalizeText(
                value
        ).toUpperCase(
                Locale.ROOT
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
    private String normalizePersonName(
            String value
    ) {

        return normalizeText(
                value
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

    private static class ValidationException
            extends Exception {

        public ValidationException(
                String message
        ) {

            super(
                    message
            );
        }
    }
}
