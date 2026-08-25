package com.almacen.integradora.controllers.provider;

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
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * Define ProviderServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name = "ProviderServlet",
        urlPatterns = {
                "/providers",
                "/providers/list",
                "/provider/save",
                "/provider/update",
                "/provider/change-status"
        }
)
/** Controlador HTTP del catálogo de proveedores y sus cambios de estado.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class ProviderServlet extends HttpServlet {

    private ProviderDao providerDao;
    private Gson gson;

    private static final Pattern NAME_PATTERN =
            Pattern.compile(
                    "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s.,()/'&+\\-]{2,150}$"
            );

    private static final Pattern RFC_PATTERN =
            Pattern.compile(
                    "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(
                    "^\\d{10}$"
            );

    private static final Pattern POST_CODE_PATTERN =
            Pattern.compile(
                    "^\\d{5}$"
            );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
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
        providerDao =
                new ProviderDao();

        gson =
                new GsonBuilder()
                        .serializeNulls()
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

            case "/providers" ->
                    showProviders(
                            request,
                            response
                    );

            case "/providers/list" ->
                    listProviders(
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

        request.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        switch (request.getServletPath()) {

            case "/provider/save" ->
                    saveProvider(
                            request,
                            response
                    );

            case "/provider/update" ->
                    updateProvider(
                            request,
                            response
                    );

            case "/provider/change-status" ->
                    changeProviderStatus(
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
    private void showProviders(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            List<Provider> providers =
                    providerDao.getAll();

            request.setAttribute(
                    "providers",
                    providers
            );

            request.getRequestDispatcher(
                    "/views/provider/providers.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar los proveedores.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar los proveedores."
            );
        }
    }

    /* ==========================================================
       LISTAR JSON
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
    private void listProviders(
            HttpServletResponse response
    ) throws IOException {

        try {
            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "",
                    providerDao.getAll()
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al consultar los proveedores.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible consultar los proveedores.",
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
    private void saveProvider(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Provider provider =
                readProvider(
                        request
                );

        try {
            String validationMessage =
                    validateProvider(
                            provider
                    );

            if (validationMessage != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        validationMessage,
                        null
                );

                return;
            }

            /*
             * La búsqueda incluye proveedores activos e inactivos.
             *
             * No queremos duplicar un proveedor únicamente porque
             * el registro existente está desactivado.
             */
            Provider existingByRfc =
                    providerDao.findAnyByRfc(
                            provider.getRfc()
                    );

            if (existingByRfc != null) {
                boolean active =
                        Integer.valueOf(1)
                                .equals(
                                        existingByRfc.getStatus()
                                );

                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        active
                                ? "Ya existe un proveedor registrado con ese RFC."
                                : "Ya existe un proveedor inactivo con ese RFC. Reactívalo en lugar de registrar uno nuevo.",
                        null
                );

                return;
            }

            if (!provider.getEmail().isBlank()) {
                Provider existingByEmail =
                        providerDao.findAnyByEmail(
                                provider.getEmail()
                        );

                if (existingByEmail != null) {
                    boolean active =
                            Integer.valueOf(1)
                                    .equals(
                                            existingByEmail.getStatus()
                                    );

                    sendJson(
                            response,
                            HttpServletResponse.SC_CONFLICT,
                            false,
                            "warning",
                            active
                                    ? "Ya existe un proveedor registrado con ese correo."
                                    : "Ya existe un proveedor inactivo con ese correo. Reactívalo en lugar de registrar uno nuevo.",
                            null
                    );

                    return;
                }
            }

            provider.setStatus(
                    1
            );

            if (!providerDao.create(provider)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible registrar el proveedor.",
                        null
                );

                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_CREATED,
                    true,
                    "success",
                    "El proveedor se registró correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error inesperado al registrar el proveedor.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible registrar el proveedor.",
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
    private void updateProvider(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String idValue =
                normalizeText(
                        request.getParameter(
                                "id"
                        )
                );

        Provider provider =
                readProvider(
                        request
                );

        try {
            Long idProvider =
                    parsePositiveLong(
                            idValue
                    );

            if (idProvider == null
                    || idProvider > Integer.MAX_VALUE) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "El identificador del proveedor no es válido.",
                        null
                );

                return;
            }

            Provider currentProvider =
                    providerDao.getById(
                            idProvider.intValue()
                    );

            if (currentProvider == null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El proveedor solicitado no existe.",
                        null
                );

                return;
            }

            provider.setIdProvider(
                    idProvider
            );

            String validationMessage =
                    validateProvider(
                            provider
                    );

            if (validationMessage != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        validationMessage,
                        null
                );

                return;
            }

            Provider existingByRfc =
                    providerDao.findAnyByRfcExceptId(
                            provider.getRfc(),
                            idProvider
                    );

            if (existingByRfc != null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe otro proveedor registrado con ese RFC.",
                        null
                );

                return;
            }

            if (!provider.getEmail().isBlank()) {
                Provider existingByEmail =
                        providerDao.findAnyByEmailExceptId(
                                provider.getEmail(),
                                idProvider
                        );

                if (existingByEmail != null) {
                    sendJson(
                            response,
                            HttpServletResponse.SC_CONFLICT,
                            false,
                            "warning",
                            "Ya existe otro proveedor registrado con ese correo.",
                            null
                    );

                    return;
                }
            }

            /*
             * update() no toca status.
             *
             * Si editamos un proveedor inactivo seguirá inactivo.
             */
            if (!providerDao.update(provider)) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible actualizar el proveedor.",
                        null
                );

                return;
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "El proveedor se actualizó correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error inesperado al actualizar el proveedor.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible actualizar el proveedor.",
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
    private void changeProviderStatus(
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
            Long idProvider =
                    parsePositiveLong(
                            idValue
                    );

            Integer status =
                    parseStatus(
                            statusValue
                    );

            if (idProvider == null
                    || idProvider > Integer.MAX_VALUE
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

            Provider provider =
                    providerDao.getById(
                            idProvider.intValue()
                    );

            if (provider == null) {
                sendJson(
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        false,
                        "error",
                        "El proveedor solicitado no existe.",
                        null
                );

                return;
            }

            if (Integer.valueOf(status)
                    .equals(
                            provider.getStatus()
                    )) {

                sendJson(
                        response,
                        HttpServletResponse.SC_OK,
                        true,
                        "info",
                        status == 1
                                ? "El proveedor ya se encuentra activo."
                                : "El proveedor ya se encuentra inactivo.",
                        null
                );

                return;
            }

            /*
             * ======================================================
             * DESACTIVACIÓN
             * ======================================================
             *
             * Permitimos desactivar siempre que ningún producto
             * ACTIVO quede sin un proveedor ACTIVO alternativo.
             *
             * Un producto inactivo no bloquea.
             *
             * Una relación product_providers con status = 0
             * tampoco cuenta como proveedor disponible.
             */
            if (status == 0) {
                int affectedProducts =
                        providerDao
                                .countActiveProductsThatWouldLoseProvider(
                                        idProvider
                                );

                if (affectedProducts > 0) {
                    String message =
                            affectedProducts == 1
                                    ? "No puedes desactivar este proveedor porque dejaría 1 producto activo sin ningún proveedor activo asociado. Asocia otro proveedor activo o desactiva el producto primero."
                                    : "No puedes desactivar este proveedor porque dejaría "
                                    + affectedProducts
                                    + " productos activos sin ningún proveedor activo asociado. Asocia otros proveedores activos o desactiva esos productos primero.";

                    Map<String, Object> data =
                            new LinkedHashMap<>();

                    data.put(
                            "idProvider",
                            idProvider
                    );

                    data.put(
                            "providerName",
                            provider.getName()
                    );

                    data.put(
                            "affectedProductCount",
                            affectedProducts
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
             * ======================================================
             * ACTIVACIÓN
             * ======================================================
             *
             * Un proveedor no tiene dependencias superiores.
             *
             * Al reactivarlo:
             * - vuelve a aparecer en formularios;
             * - relaciones product_providers que ya tengan status=1
             *   vuelven a ser operativas;
             * - no se modifica historial ni stock.
             */
            if (!providerDao.changeStatus(
                    idProvider,
                    status
            )) {
                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible cambiar el estado del proveedor.",
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
                            ? "El proveedor fue activado correctamente."
                            : "El proveedor fue desactivado correctamente.",
                    null
            );

        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Error al cambiar el estado del proveedor.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cambiar el estado del proveedor.",
                    null
            );
        }
    }

    /* ==========================================================
       LEER DATOS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private Provider readProvider(
            HttpServletRequest request
    ) {
        Provider provider =
                new Provider();

        provider.setName(
                normalizeText(
                        request.getParameter(
                                "name"
                        )
                )
        );

        provider.setRfc(
                normalizeUpperText(
                        request.getParameter(
                                "rfc"
                        )
                )
        );

        provider.setPhone(
                normalizeDigits(
                        request.getParameter(
                                "phone"
                        )
                )
        );

        provider.setEmail(
                normalizeEmail(
                        request.getParameter(
                                "email"
                        )
                )
        );

        provider.setContactName(
                normalizeText(
                        request.getParameter(
                                "contactName"
                        )
                )
        );

        provider.setAddress(
                normalizeText(
                        request.getParameter(
                                "address"
                        )
                )
        );

        provider.setPostCode(
                normalizeDigits(
                        request.getParameter(
                                "postCode"
                        )
                )
        );

        provider.setSocialCase(
                normalizeText(
                        request.getParameter(
                                "socialCase"
                        )
                )
        );

        provider.setContactPhone(
                normalizeDigits(
                        request.getParameter(
                                "contactPhone"
                        )
                )
        );

        provider.setContactEmail(
                normalizeEmail(
                        request.getParameter(
                                "contactEmail"
                        )
                )
        );

        return provider;
    }

    /* ==========================================================
       VALIDACIONES
       ========================================================== */

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param provider valor de provider requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String validateProvider(
            Provider provider
    ) {
        if (provider == null) {
            return "La información del proveedor no es válida.";
        }

        if (provider.getName().isBlank()
                || provider.getRfc().isBlank()
                || provider.getSocialCase().isBlank()) {

            return "Completa el nombre comercial, la razón social y el RFC.";
        }

        if (!isValidName(
                provider.getName()
        )) {
            return "El nombre comercial no tiene un formato válido.";
        }

        if (!isValidName(
                provider.getSocialCase()
        )) {
            return "La razón social no tiene un formato válido.";
        }

        if (!isValidRfc(
                provider.getRfc()
        )) {
            return "El RFC no tiene un formato válido.";
        }

        if (!provider.getPhone().isBlank()
                && !isValidPhone(
                provider.getPhone()
        )) {
            return "El teléfono del proveedor debe contener 10 dígitos.";
        }

        if (!provider.getContactPhone().isBlank()
                && !isValidPhone(
                provider.getContactPhone()
        )) {
            return "El teléfono del contacto debe contener 10 dígitos.";
        }

        if (!provider.getEmail().isBlank()
                && !isValidEmail(
                provider.getEmail()
        )) {
            return "El correo del proveedor no tiene un formato válido.";
        }

        if (!provider.getContactEmail().isBlank()
                && !isValidEmail(
                provider.getContactEmail()
        )) {
            return "El correo del contacto no tiene un formato válido.";
        }

        if (!provider.getPostCode().isBlank()
                && !isValidPostCode(
                provider.getPostCode()
        )) {
            return "El código postal debe contener 5 dígitos.";
        }

        if (!provider.getContactName().isBlank()
                && !isValidName(
                provider.getContactName()
        )) {
            return "El nombre del contacto no tiene un formato válido.";
        }

        if (provider.getAddress() != null
                && provider.getAddress().length() > 300) {

            return "La dirección no puede superar los 300 caracteres.";
        }

        return null;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidName(
            String value
    ) {
        return value != null
                && NAME_PATTERN.matcher(
                value
        ).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param rfc valor de rfc requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidRfc(
            String rfc
    ) {
        return rfc != null
                && RFC_PATTERN.matcher(
                rfc
        ).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param phone valor de phone requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidPhone(
            String phone
    ) {
        return phone != null
                && PHONE_PATTERN.matcher(
                phone
        ).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param email dirección de correo asociada a la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidEmail(
            String email
    ) {
        return email != null
                && EMAIL_PATTERN.matcher(
                email
        ).matches();
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param postCode valor de postCode requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidPostCode(
            String postCode
    ) {
        return postCode != null
                && POST_CODE_PATTERN.matcher(
                postCode
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
    private String normalizeUpperText(
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
    private String normalizeEmail(
            String value
    ) {
        return normalizeText(
                value
        ).toLowerCase(
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
    private String normalizeDigits(
            String value
    ) {
        if (value == null) {
            return "";
        }

        return value.replaceAll(
                "\\D",
                ""
        );
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
                gson.toJson(
                        result
                )
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
