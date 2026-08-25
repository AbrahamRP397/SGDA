package com.almacen.integradora.controllers.product;

import com.almacen.integradora.models.metric.Metric;
import com.almacen.integradora.models.metric.MetricDao;
import com.almacen.integradora.models.product.Product;
import com.almacen.integradora.models.product.ProductDao;
import com.almacen.integradora.models.product.ProductProvider;
import com.almacen.integradora.models.provider.Provider;
import com.almacen.integradora.models.provider.ProviderDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
/**
 * Define ProductServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(name="ProductServlet",urlPatterns={
        "/products",
        "/products/list",
        "/products/by-provider",
        "/product/save",
        "/product/update",
        "/product/change-status"
})
/** Controlador HTTP del catálogo de productos y proveedores asociados.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class ProductServlet extends HttpServlet{
    private ProductDao productDao;
    private MetricDao metricDao;
    private ProviderDao providerDao;
    private Gson gson;

    private static final Pattern CODE_PATTERN=
            Pattern.compile("^[A-Z0-9][A-Z0-9._-]{1,49}$");

    private static final Pattern NAME_PATTERN=
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s.,()/'&+\\-]{2,150}$");

    private static final BigDecimal MAX_PURCHASE_PRICE=
            new BigDecimal("9999999999.99");

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init(){
        productDao=new ProductDao();
        metricDao=new MetricDao();
        providerDao=new ProviderDao();
        gson=new GsonBuilder().serializeNulls().create();
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

        switch(request.getServletPath()){
            case "/products"->showProducts(request,response);
            case "/products/list"->listProducts(response);
            case "/products/by-provider"->listProductsByProvider(request,response);
            default->sendJson(
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
    )throws IOException{
        disableCache(response);
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        switch(request.getServletPath()){
            case "/product/save"->saveProduct(request,response);
            case "/product/update"->updateProduct(request,response);
            case "/product/change-status"->changeProductStatus(request,response);
            default->sendJson(
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
    private void showProducts(
            HttpServletRequest request,
            HttpServletResponse response
    )throws ServletException,IOException{
        try{
            List<Product> products=productDao.getAll();
            List<Metric> metrics=metricDao.getActiveMetrics();
            List<Provider> providers=providerDao.getActiveProviders();

            request.setAttribute("products",products);
            request.setAttribute("metrics",metrics);
            request.setAttribute("providers",providers);

            request.getRequestDispatcher("/views/product/products.jsp")
                    .forward(request,response);

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al consultar la información de productos.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar los productos."
            );
        }
    }

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
    private void listProducts(
            HttpServletResponse response
    )throws IOException{
        try{
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    productDao.getAll()
            );

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al consultar los productos.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar los productos.",
                    null
            );
        }
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
    private void listProductsByProvider(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        Long idProvider=parsePositiveLong(
                normalizeText(
                        request.getParameter("idProvider")
                )
        );

        if(idProvider==null){
            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    "El proveedor seleccionado no es válido.",
                    null
            );
            return;
        }

        try{
            Provider provider=providerDao.getById(
                    idProvider.intValue()
            );

            if(!isProviderActive(provider)){
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "warning",
                        "El proveedor no existe o se encuentra inactivo.",
                        null
                );
                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    productDao.getActiveProductsByProvider(idProvider)
            );

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al consultar los productos del proveedor.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar los productos del proveedor.",
                    null
            );
        }
    }

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
    private void saveProduct(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        String code=normalizeCode(
                request.getParameter("code")
        );

        String name=normalizeText(
                request.getParameter("name")
        );

        String metricValue=normalizeText(
                request.getParameter("idMetric")
        );

        String description=normalizeDescription(
                request.getParameter("description")
        );

        try{
            Long idMetric=parsePositiveLong(metricValue);

            if(code.isBlank()
                    ||name.isBlank()
                    ||idMetric==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Completa la clave, el nombre y la unidad de medida.",
                        null
                );
                return;
            }

            if(!isValidCode(code)
                    ||!isValidName(name)
                    ||!isValidDescription(description)){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Verifica los datos ingresados para el producto.",
                        null
                );
                return;
            }

            Metric metric=metricDao.getById(
                    idMetric.intValue()
            );

            if(metric==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "La unidad de medida seleccionada no existe.",
                        null
                );
                return;
            }

            if(!isMetricActive(metric)){
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "La unidad de medida seleccionada se encuentra inactiva. Selecciona una unidad activa.",
                        null
                );
                return;
            }

            Product existingProduct=
                    productDao.findAnyByCode(code);

            if(existingProduct!=null){
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe un producto registrado con esa clave.",
                        null
                );
                return;
            }

            List<ProductProvider> providers=
                    parseProductProviders(request);

            if(providers.isEmpty()){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Asocia al menos un proveedor con el producto.",
                        null
                );
                return;
            }

            validateProviders(providers);

            Product product=new Product();
            product.setCode(code);
            product.setName(name);
            product.setIdMetric(idMetric);
            product.setDescription(description);
            product.setStatus(1);
            product.setProviders(providers);

            if(!productDao.create(product)){
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible registrar el producto.",
                        null
                );
                return;
            }

            Map<String,Object> data=
                    new LinkedHashMap<>();

            data.put(
                    "idProduct",
                    product.getIdProduct()
            );

            data.put(
                    "providerCount",
                    product.getProviderCount()
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    true,
                    "success",
                    "El producto se registró correctamente.",
                    data
            );

        }catch(ValidationException exception){
            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    exception.getMessage(),
                    null
            );

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error inesperado al registrar el producto.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible registrar el producto.",
                    null
            );
        }
    }

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
    private void updateProduct(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        String idValue=normalizeText(
                request.getParameter("id")
        );

        String code=normalizeCode(
                request.getParameter("code")
        );

        String name=normalizeText(
                request.getParameter("name")
        );

        String metricValue=normalizeText(
                request.getParameter("idMetric")
        );

        String description=normalizeDescription(
                request.getParameter("description")
        );

        try{
            Long idProduct=parsePositiveLong(idValue);
            Long idMetric=parsePositiveLong(metricValue);

            if(idProduct==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "El identificador del producto no es válido.",
                        null
                );
                return;
            }

            Product currentProduct=
                    productDao.getById(
                            idProduct.intValue()
                    );

            if(currentProduct==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El producto solicitado no existe.",
                        null
                );
                return;
            }

            if(code.isBlank()
                    ||name.isBlank()
                    ||idMetric==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Completa la clave, el nombre y la unidad de medida.",
                        null
                );
                return;
            }

            if(!isValidCode(code)
                    ||!isValidName(name)
                    ||!isValidDescription(description)){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Verifica los datos ingresados para el producto.",
                        null
                );
                return;
            }

            Metric metric=metricDao.getById(
                    idMetric.intValue()
            );

            if(metric==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "La unidad de medida seleccionada no existe.",
                        null
                );
                return;
            }

            if(!isMetricActive(metric)){
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "La unidad de medida seleccionada se encuentra inactiva. Selecciona una unidad activa.",
                        null
                );
                return;
            }

            Product duplicatedProduct=
                    productDao.findAnyByCodeExceptId(
                            code,
                            idProduct
                    );

            if(duplicatedProduct!=null){
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe otro producto registrado con esa clave.",
                        null
                );
                return;
            }

            List<ProductProvider> providers=
                    parseProductProviders(request);

            if(providers.isEmpty()){
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "Asocia al menos un proveedor con el producto.",
                        null
                );
                return;
            }

            validateProviders(providers);

            Product product=new Product();
            product.setIdProduct(idProduct);
            product.setCode(code);
            product.setName(name);
            product.setIdMetric(idMetric);
            product.setDescription(description);
            product.setStatus(currentProduct.getStatus());
            product.setProviders(providers);

            if(!productDao.update(product)){
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible actualizar el producto.",
                        null
                );
                return;
            }

            Map<String,Object> data=
                    new LinkedHashMap<>();

            data.put(
                    "idProduct",
                    idProduct
            );

            data.put(
                    "providerCount",
                    product.getProviderCount()
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "El producto se actualizó correctamente.",
                    data
            );

        }catch(ValidationException exception){
            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    exception.getMessage(),
                    null
            );

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error inesperado al actualizar el producto.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible actualizar el producto.",
                    null
            );
        }
    }

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
    private void changeProductStatus(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        String idValue=normalizeText(
                request.getParameter("id")
        );

        String statusValue=normalizeText(
                request.getParameter("status")
        );

        try{
            Long idProduct=parsePositiveLong(idValue);
            Integer status=parseStatus(statusValue);

            if(idProduct==null||status==null){
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

            Product product=productDao.getById(
                    idProduct.intValue()
            );

            if(product==null){
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El producto solicitado no existe.",
                        null
                );
                return;
            }

            if(product.getStatus()!=null
                    &&product.getStatus().equals(status)){
                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        true,
                        "info",
                        status==1
                                ?"El producto ya se encuentra activo."
                                :"El producto ya se encuentra inactivo.",
                        null
                );
                return;
            }

            /*
             * Desactivar siempre está permitido.
             *
             * Al activar volvemos a validar todas las dependencias
             * necesarias para que el producto pueda utilizarse.
             */
            if(status==1){
                String dependencyError=
                        validateActivationDependencies(
                                product
                        );

                if(dependencyError!=null){
                    sendJson(
                            response,
                            HttpServletResponse.SC_CONFLICT,
                            false,
                            "warning",
                            dependencyError,
                            null
                    );
                    return;
                }
            }

            if(!productDao.changeStatus(
                    idProduct,
                    status
            )){
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible cambiar el estado del producto.",
                        null
                );
                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    status==1
                            ?"success"
                            :"warning",
                    status==1
                            ?"El producto fue activado correctamente."
                            :"El producto fue desactivado correctamente.",
                    null
            );

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al cambiar el estado del producto.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cambiar el estado del producto.",
                    null
            );
        }
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param product valor de product requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String validateActivationDependencies(
            Product product
    ){
        if(product==null){
            return "No fue posible validar las dependencias del producto.";
        }

        Long idMetric=
                product.getIdMetric();

        if(idMetric==null
                ||idMetric<=0
                ||idMetric>Integer.MAX_VALUE){
            return "El producto no tiene una unidad de medida válida asociada.";
        }

        Metric metric=
                metricDao.getById(
                        idMetric.intValue()
                );

        if(metric==null){
            return "La unidad de medida asociada al producto ya no existe.";
        }

        if(!isMetricActive(metric)){
            return "No puedes activar el producto porque su unidad de medida se encuentra inactiva.";
        }

        List<ProductProvider> relations=
                product.getProviders();

        if(relations==null
                ||relations.isEmpty()){
            return "No puedes activar el producto porque no tiene proveedores asociados.";
        }

        boolean hasActiveProvider=false;

        for(ProductProvider relation:relations){
            if(relation==null
                    ||relation.getIdProvider()==null
                    ||relation.getIdProvider()<=0
                    ||relation.getIdProvider()>Integer.MAX_VALUE
                    ||!Integer.valueOf(1).equals(relation.getStatus())){
                continue;
            }

            Provider provider=
                    providerDao.getById(
                            relation.getIdProvider()
                                    .intValue()
                    );

            if(isProviderActive(provider)){
                hasActiveProvider=true;
                break;
            }
        }

        if(!hasActiveProvider){
            return "No puedes activar el producto porque no tiene ningún proveedor activo asociado.";
        }

        return null;
    }

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
    private List<ProductProvider> parseProductProviders(
            HttpServletRequest request
    )throws ValidationException{
        String[] providerIds=getParameterValues(
                request,
                "providerId[]",
                "providerId"
        );

        String[] purchasePrices=getParameterValues(
                request,
                "purchasePrice[]",
                "purchasePrice"
        );

        List<ProductProvider> providers=
                new ArrayList<>();

        if(providerIds==null
                ||providerIds.length==0){
            return providers;
        }

        if(purchasePrices==null
                ||providerIds.length
                !=purchasePrices.length){
            throw new ValidationException(
                    "La información de los proveedores está incompleta."
            );
        }

        Set<Long> uniqueProviderIds=
                new HashSet<>();

        for(int index=0;
            index<providerIds.length;
            index++){

            String providerValue=
                    normalizeText(
                            providerIds[index]
                    );

            String priceValue=
                    normalizeDecimal(
                            purchasePrices[index]
                    );

            if(providerValue.isBlank()
                    &&priceValue.isBlank()){
                continue;
            }

            Long idProvider=
                    parsePositiveLong(
                            providerValue
                    );

            if(idProvider==null){
                throw new ValidationException(
                        "Selecciona un proveedor válido en todas las filas."
                );
            }

            if(idProvider>Integer.MAX_VALUE){
                throw new ValidationException(
                        "Uno de los proveedores seleccionados no es válido."
                );
            }

            if(!uniqueProviderIds.add(
                    idProvider
            )){
                throw new ValidationException(
                        "No puedes asociar dos veces el mismo proveedor."
                );
            }

            BigDecimal purchasePrice=
                    parsePurchasePrice(
                            priceValue
                    );

            ProductProvider relation=
                    new ProductProvider();

            relation.setIdProvider(
                    idProvider
            );

            relation.setPurchasePrice(
                    purchasePrice
            );

            relation.setStatus(1);

            providers.add(relation);
        }

        return providers;
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
    ){
        String[] values=
                request.getParameterValues(
                        primaryName
                );

        if(values==null){
            values=
                    request.getParameterValues(
                            alternativeName
                    );
        }

        return values;
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param relations valor de relations requerido por la operación
     * @throws ValidationException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void validateProviders(
            List<ProductProvider> relations
    )throws ValidationException{
        for(ProductProvider relation:relations){
            if(relation==null
                    ||relation.getIdProvider()==null
                    ||relation.getIdProvider()<=0
                    ||relation.getIdProvider()>Integer.MAX_VALUE){
                throw new ValidationException(
                        "Se recibió un proveedor no válido."
                );
            }

            Provider provider=
                    providerDao.getById(
                            relation.getIdProvider()
                                    .intValue()
                    );

            if(provider==null){
                throw new ValidationException(
                        "Uno de los proveedores seleccionados no existe."
                );
            }

            if(!isProviderActive(provider)){
                throw new ValidationException(
                        "El proveedor "
                                +provider.getName()
                                +" se encuentra inactivo."
                );
            }
        }
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param metric valor de metric requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isMetricActive(
            Metric metric
    ){
        return metric!=null
                &&Integer.valueOf(1)
                .equals(metric.getStatus());
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param provider valor de provider requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isProviderActive(
            Provider provider
    ){
        return provider!=null
                &&Integer.valueOf(1)
                .equals(provider.getStatus());
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
    private BigDecimal parsePurchasePrice(
            String value
    )throws ValidationException{
        if(value==null
                ||value.isBlank()){
            throw new ValidationException(
                    "Captura el precio de compra de todos los proveedores."
            );
        }

        try{
            BigDecimal price=
                    new BigDecimal(value)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            if(price.compareTo(
                    BigDecimal.ZERO
            )<0){
                throw new ValidationException(
                        "El precio de compra no puede ser negativo."
                );
            }

            if(price.compareTo(
                    MAX_PURCHASE_PRICE
            )>0){
                throw new ValidationException(
                        "El precio de compra supera el límite permitido."
                );
            }

            return price;

        }catch(NumberFormatException exception){
            throw new ValidationException(
                    "Captura precios de compra válidos."
            );
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
    )throws IOException{
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        Map<String,Object> result=
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
                message==null
                        ?""
                        :message.trim()
        );

        if(data!=null){
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
    ){
        if(type==null
                ||type.isBlank()){
            return success
                    ?"success"
                    :"error";
        }

        String normalizedType=
                type.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return switch(normalizedType){
            case "success",
                 "error",
                 "warning",
                 "info"->
                    normalizedType;

            default->
                    success
                            ?"success"
                            :"error";
        };
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param code valor de code requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidCode(
            String code
    ){
        return code!=null
                &&CODE_PATTERN.matcher(
                code
        ).matches();
    }

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
    ){
        return name!=null
                &&NAME_PATTERN.matcher(
                name
        ).matches();
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
    private boolean isValidDescription(
            String description
    ){
        return description!=null
                &&description.length()<=500;
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
    ){
        if(value==null
                ||value.isBlank()){
            return null;
        }

        try{
            long number=
                    Long.parseLong(value);

            return number>0
                    ?number
                    :null;

        }catch(NumberFormatException exception){
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
    ){
        if("0".equals(value)){
            return 0;
        }

        if("1".equals(value)){
            return 1;
        }

        return null;
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
    ){
        if(value==null){
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
    private String normalizeCode(
            String value
    ){
        return normalizeText(value)
                .toUpperCase(
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
    private String normalizeDecimal(
            String value
    ){
        if(value==null){
            return "";
        }

        return value
                .trim()
                .replace(",",".");
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
    private String normalizeDescription(
            String value
    ){
        if(value==null){
            return "";
        }

        return value
                .trim()
                .replaceAll(
                        "[\\t\\x0B\\f\\r ]+",
                        " "
                )
                .replaceAll(
                        "\\n{3,}",
                        "\n\n"
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

    private static class ValidationException
            extends Exception{

        public ValidationException(
                String message
        ){
            super(message);
        }
    }
}
