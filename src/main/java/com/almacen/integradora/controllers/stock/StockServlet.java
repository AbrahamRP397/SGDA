package com.almacen.integradora.controllers.stock;

import com.almacen.integradora.models.stock.Stock;
import com.almacen.integradora.models.stock.StockDao;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
/**
 * Define StockServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name = "StockServlet",
        urlPatterns = {
                "/stock",
                "/stock/list",
                "/stock/summary"
        }
)
/** Controlador HTTP de existencias, disponibilidad y resúmenes de inventario.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class StockServlet extends HttpServlet {

    private StockDao stockDao;
    private Gson gson;

    /*
     * Por ahora consideramos stock bajo cuando hay
     * entre 1 y 10 unidades.
     *
     * Después este límite puede guardarse por producto
     * en la base de datos.
     */
    private static final int LOW_STOCK_LIMIT = 10;

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init() {
        stockDao = new StockDao();
        gson = new Gson();
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

            case "/stock" ->
                    showStockView(request, response);

            case "/stock/list" ->
                    listStock(response);

            case "/stock/summary" ->
                    getStockSummary(response);

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
    private void showStockView(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<Stock> stockList =
                    prepareStockList(stockDao.getAllStock());

            Map<String, Object> summary =
                    calculateSummary(stockList);

            request.setAttribute(
                    "stockList",
                    stockList
            );

            request.setAttribute(
                    "stockSummary",
                    summary
            );

            request.setAttribute(
                    "lowStockLimit",
                    LOW_STOCK_LIMIT
            );

            request.getRequestDispatcher(
                    "/views/stock/stock.jsp"
            ).forward(request, response);

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al cargar la vista de existencias.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar las existencias."
            );
        }
    }

    /* ==========================================================
       LISTAR STOCK EN JSON
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
    private void listStock(
            HttpServletResponse response
    ) throws IOException {

        try {
            List<Stock> stockList =
                    prepareStockList(stockDao.getAllStock());

            List<Map<String, Object>> result =
                    new ArrayList<>();

            for (Stock stock : stockList) {
                result.add(
                        createStockResponse(stock)
                );
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    result
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al consultar las existencias.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar las existencias.",
                    null
            );
        }
    }

    /* ==========================================================
       RESUMEN EN JSON
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
    private void getStockSummary(
            HttpServletResponse response
    ) throws IOException {

        try {
            List<Stock> stockList =
                    prepareStockList(stockDao.getAllStock());

            Map<String, Object> summary =
                    calculateSummary(stockList);

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    summary
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al calcular el resumen de existencias.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible calcular el resumen de existencias.",
                    null
            );
        }
    }

    /* ==========================================================
       PREPARAR Y ORDENAR EXISTENCIAS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param originalList valor de originalList requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private List<Stock> prepareStockList(
            List<Stock> originalList
    ) {
        if (originalList == null) {
            return new ArrayList<>();
        }

        List<Stock> stockList =
                new ArrayList<>(originalList);

        /*
         * Primero agotados, después stock bajo y al final
         * existencias disponibles.
         *
         * Dentro de cada grupo se ordena por producto
         * y proveedor.
         */
        stockList.sort(
                Comparator
                        .comparingInt(
                                (Stock stock) ->
                                        getStockPriority(
                                                stock.getQuantity()
                                        )
                        )
                        .thenComparing(
                                (Stock stock) ->
                                        normalizeComparable(
                                                stock.getProductName()
                                        )
                        )
                        .thenComparing(
                                (Stock stock) ->
                                        normalizeComparable(
                                                stock.getProviderName()
                                        )
                        )
        );

        return stockList;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param quantity valor de quantity requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private int getStockPriority(
            Integer quantity
    ) {
        int safeQuantity =
                quantity == null ? 0 : quantity;

        if (safeQuantity <= 0) {
            return 0;
        }

        if (safeQuantity <= LOW_STOCK_LIMIT) {
            return 1;
        }

        return 2;
    }

    /* ==========================================================
       TARJETAS DE RESUMEN
       ========================================================== */

    private Map<String, Object> calculateSummary(
            List<Stock> stockList
    ) {
        Set<Long> uniqueProductIds =
                new HashSet<>();

        Map<Long, Long> totalByProduct =
                new LinkedHashMap<>();

        long totalUnits = 0L;

        /*
         * ==========================================================
         * VALOR DEL INVENTARIO
         * ==========================================================
         *
         * Utilizamos BigDecimal porque estamos trabajando con dinero.
         *
         * Evitamos:
         *
         * - overflow de long;
         * - conversiones manuales a centavos;
         * - pérdida de precisión por double.
         */
        BigDecimal totalInventoryValue =
                BigDecimal.ZERO;

        if (stockList != null) {

            for (Stock stock : stockList) {

                if (stock == null) {
                    continue;
                }

                Long idProduct =
                        stock.getIdProduct();

                int quantity =
                        stock.getQuantity() == null
                                ? 0
                                : stock.getQuantity();

                if (idProduct != null
                        && idProduct > 0) {

                    uniqueProductIds.add(
                            idProduct
                    );

                    totalByProduct.merge(
                            idProduct,
                            (long) quantity,
                            Long::sum
                    );
                }

                /*
                 * totalUnits sigue siendo long porque representa
                 * cantidades físicas, no dinero.
                 */
                totalUnits += quantity;

                /*
                 * ======================================================
                 * PRECIO × EXISTENCIA
                 * ======================================================
                 */
                if (stock.getPurchasePrice() != null
                        && quantity > 0) {

                    BigDecimal stockValue =
                            stock.getPurchasePrice()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    quantity
                                            )
                                    );

                    totalInventoryValue =
                            totalInventoryValue.add(
                                    stockValue
                            );
                }
            }
        }

        long lowStockProducts =
                totalByProduct.values()
                        .stream()
                        .filter(
                                quantity ->
                                        quantity > 0
                                                && quantity
                                                <= LOW_STOCK_LIMIT
                        )
                        .count();

        long outOfStockProducts =
                totalByProduct.values()
                        .stream()
                        .filter(
                                quantity ->
                                        quantity <= 0
                        )
                        .count();

        Map<String, Object> summary =
                new LinkedHashMap<>();

        summary.put(
                "totalProducts",
                uniqueProductIds.size()
        );

        summary.put(
                "totalUnits",
                totalUnits
        );

        summary.put(
                "lowStockProducts",
                lowStockProducts
        );

        summary.put(
                "outOfStockProducts",
                outOfStockProducts
        );

        summary.put(
                "totalRelations",
                stockList == null
                        ? 0
                        : stockList.size()
        );

        summary.put(
                "lowStockLimit",
                LOW_STOCK_LIMIT
        );

        /*
         * Gson serializa BigDecimal como número JSON.
         *
         * No hacemos:
         *
         * total / 100.0
         *
         * porque el valor ya está expresado directamente en pesos.
         */
        summary.put(
                "inventoryValue",
                totalInventoryValue
        );

        return summary;
    }

    /* ==========================================================
       CONSTRUIR RESPUESTA DE UNA FILA
       ========================================================== */

    private Map<String, Object> createStockResponse(
            Stock stock
    ) {
        Map<String, Object> item =
                new LinkedHashMap<>();

        int quantity =
                stock.getQuantity() == null
                        ? 0
                        : stock.getQuantity();

        String stockStatus =
                getStockStatus(quantity);

        int stockPercentage =
                calculateStockPercentage(quantity);

        item.put(
                "idStock",
                stock.getIdStock()
        );

        item.put(
                "idProductProvider",
                stock.getIdProductProvider()
        );

        item.put(
                "idProduct",
                stock.getIdProduct()
        );

        item.put(
                "productCode",
                stock.getProductCode()
        );

        item.put(
                "productName",
                stock.getProductName()
        );

        item.put(
                "idMetric",
                stock.getIdMetric()
        );

        item.put(
                "metricName",
                stock.getMetricName()
        );

        item.put(
                "metricShortName",
                stock.getMetricShortName()
        );

        item.put(
                "idProvider",
                stock.getIdProvider()
        );

        item.put(
                "providerName",
                stock.getProviderName()
        );

        item.put(
                "providerRfc",
                stock.getProviderRfc()
        );

        item.put(
                "purchasePrice",
                stock.getPurchasePrice()
        );

        item.put(
                "quantity",
                quantity
        );

        item.put(
                "stockStatus",
                stockStatus
        );

        item.put(
                "stockStatusLabel",
                getStockStatusLabel(stockStatus)
        );

        item.put(
                "stockPercentage",
                stockPercentage
        );

        item.put(
                "productStatus",
                stock.getProductStatus()
        );

        item.put(
                "metricStatus",
                stock.getMetricStatus()
        );

        item.put(
                "providerStatus",
                stock.getProviderStatus()
        );

        item.put(
                "relationStatus",
                stock.getRelationStatus()
        );

        item.put(
                "operationalForEntry",
                stock.isOperationalForEntry()
        );

        item.put(
                "availableForExit",
                stock.isAvailableForExit()
        );

        return item;
    }

    /* ==========================================================
       ESTADO DEL STOCK
       ========================================================== */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param quantity valor de quantity requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String getStockStatus(
            int quantity
    ) {
        if (quantity <= 0) {
            return "out";
        }

        if (quantity <= LOW_STOCK_LIMIT) {
            return "low";
        }

        return "available";
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param status estado que se utilizará en la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String getStockStatusLabel(
            String status
    ) {
        return switch (status) {
            case "out" ->
                    "Agotado";

            case "low" ->
                    "Stock bajo";

            default ->
                    "Disponible";
        };
    }

    /*
     * La barra visual usa el límite bajo como referencia.
     *
     * 0 unidades      = 0 %
     * 1 a 10          = 10 % a 50 %
     * 11 a 20         = 55 % a 100 %
     * más de 20       = 100 %
     */
    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param quantity valor de quantity requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private int calculateStockPercentage(
            int quantity
    ) {
        if (quantity <= 0) {
            return 0;
        }

        int referenceMaximum =
                LOW_STOCK_LIMIT * 2;

        int percentage =
                (int) Math.round(
                        quantity * 100.0
                                / referenceMaximum
                );

        return Math.min(
                Math.max(percentage, 5),
                100
        );
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
       AUXILIARES
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
    private String normalizeComparable(
            String value
    ) {
        return value == null
                ? ""
                : value.trim()
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
