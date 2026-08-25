package com.almacen.integradora.filters;

import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
/**
 * Define CsrfFilter y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebFilter("/*")
/** Filtro que genera y valida tokens CSRF en operaciones que modifican estado.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class CsrfFilter implements Filter {

    private Gson gson;

    private static final String REQUEST_ATTRIBUTE = "csrfToken";
    private static final String COOKIE_NAME = "XSRF-TOKEN";
    private static final String HEADER_NAME = "X-CSRF-Token";
    private static final String PARAMETER_NAME = "csrfToken";

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

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
    public void init(FilterConfig filterConfig) {
        gson = new Gson();
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
    ) throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;

        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        String contextPath =
                request.getContextPath();

        String uri =
                request.getRequestURI();

        String path =
                uri != null
                        && contextPath != null
                        && uri.startsWith(contextPath)
                        ? uri.substring(contextPath.length())
                        : uri;

        if (isStaticResource(path)) {
            chain.doFilter(
                    request,
                    response
            );
            return;
        }

        /*
         * ==========================================================
         * TOKEN CSRF
         * ==========================================================
         *
         * El token ya no depende de HttpSession.
         *
         * Esto evita que un cambio de JSESSIONID invalide
         * formularios públicos como:
         *
         * - login
         * - recuperación de contraseña
         * - restablecimiento de contraseña
         *
         * El servidor compara:
         *
         * COOKIE XSRF-TOKEN
         *        VS
         * HEADER X-CSRF-Token / parámetro csrfToken
         */
        String cookieToken =
                getCookieToken(request);

        /*
         * En peticiones seguras podemos crear el token si todavía
         * no existe.
         */
        if (!isUnsafeMethod(request.getMethod())) {

            if (!isValidTokenFormat(cookieToken)) {
                cookieToken =
                        generateToken();

                writeCookie(
                        request,
                        response,
                        cookieToken
                );
            }

            request.setAttribute(
                    REQUEST_ATTRIBUTE,
                    cookieToken
            );

            chain.doFilter(
                    request,
                    response
            );

            return;
        }

        /*
         * ==========================================================
         * PETICIONES POST / PUT / PATCH / DELETE
         * ==========================================================
         */

        if (!isValidTokenFormat(cookieToken)) {
            handleInvalidToken(
                    request,
                    response
            );
            return;
        }

        String receivedToken =
                getReceivedToken(request);

        if (!isValidTokenFormat(receivedToken)
                || !constantTimeEquals(
                cookieToken,
                receivedToken
        )) {

            handleInvalidToken(
                    request,
                    response
            );

            return;
        }

        /*
         * Dejamos disponible el token también como atributo
         * para cualquier JSP al que pueda llegar la petición.
         */
        request.setAttribute(
                REQUEST_ATTRIBUTE,
                cookieToken
        );

        chain.doFilter(
                request,
                response
        );
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
    private boolean isStaticResource(
            String path
    ) {
        return path != null
                && path.startsWith(
                "/assets/"
        );
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param method valor de method requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isUnsafeMethod(
            String method
    ) {
        if (method == null) {
            return false;
        }

        return switch (
                method.toUpperCase(
                        Locale.ROOT
                )
                ) {
            case "POST",
                 "PUT",
                 "PATCH",
                 "DELETE" -> true;

            default -> false;
        };
    }

    /*
     * ==========================================================
     * OBTENER TOKEN RECIBIDO
     * ==========================================================
     */

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
    private String getReceivedToken(
            HttpServletRequest request
    ) {

        String received =
                request.getHeader(
                        HEADER_NAME
                );

        if (received == null
                || received.isBlank()) {

            received =
                    request.getParameter(
                            PARAMETER_NAME
                    );
        }

        return received == null
                ? ""
                : received.trim();
    }

    /*
     * ==========================================================
     * LEER COOKIE
     * ==========================================================
     */

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
    private String getCookieToken(
            HttpServletRequest request
    ) {

        Cookie[] cookies =
                request.getCookies();

        if (cookies == null
                || cookies.length == 0) {

            return "";
        }

        for (Cookie cookie : cookies) {

            if (cookie == null) {
                continue;
            }

            if (!COOKIE_NAME.equals(
                    cookie.getName()
            )) {
                continue;
            }

            String value =
                    cookie.getValue();

            if (value == null
                    || value.isBlank()) {

                continue;
            }

            return value.trim();
        }

        return "";
    }

    /*
     * ==========================================================
     * CREAR TOKEN
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String generateToken() {

        byte[] bytes =
                new byte[32];

        SECURE_RANDOM.nextBytes(
                bytes
        );

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        bytes
                );
    }

    /*
     * ==========================================================
     * VALIDAR FORMATO
     * ==========================================================
     */

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param token token utilizado para validar la solicitud
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean isValidTokenFormat(
            String token
    ) {

        if (token == null
                || token.isBlank()) {

            return false;
        }

        /*
         * Un token generado con 32 bytes en Base64 URL-safe
         * sin padding tiene 43 caracteres.
         *
         * Permitimos un rango pequeño para mantener compatibilidad
         * sin aceptar valores arbitrariamente grandes.
         */
        if (token.length() < 40
                || token.length() > 64) {

            return false;
        }

        for (int index = 0;
             index < token.length();
             index++) {

            char character =
                    token.charAt(index);

            boolean valid =
                    character >= 'A'
                            && character <= 'Z'
                            || character >= 'a'
                            && character <= 'z'
                            || character >= '0'
                            && character <= '9'
                            || character == '-'
                            || character == '_';

            if (!valid) {
                return false;
            }
        }

        return true;
    }

    /*
     * ==========================================================
     * COMPARACIÓN CONSTANTE
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param expected valor de expected requerido por la operación
     * @param received valor de received requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean constantTimeEquals(
            String expected,
            String received
    ) {

        if (expected == null
                || received == null) {

            return false;
        }

        byte[] expectedBytes =
                expected.getBytes(
                        StandardCharsets.UTF_8
                );

        byte[] receivedBytes =
                received.getBytes(
                        StandardCharsets.UTF_8
                );

        return MessageDigest.isEqual(
                expectedBytes,
                receivedBytes
        );
    }

    /*
     * ==========================================================
     * COOKIE
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param token token utilizado para validar la solicitud
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void writeCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String token
    ) {

        String path =
                request.getContextPath();

        if (path == null
                || path.isBlank()) {

            path = "/";
        }

        StringBuilder cookie =
                new StringBuilder();

        cookie.append(
                        COOKIE_NAME
                )
                .append("=")
                .append(token)
                .append("; Path=")
                .append(path)
                .append("; SameSite=Strict");

        /*
         * No usamos HttpOnly porque api.js necesita leer
         * XSRF-TOKEN para enviarlo mediante X-CSRF-Token.
         */
        if (request.isSecure()) {
            cookie.append(
                    "; Secure"
            );
        }

        response.addHeader(
                "Set-Cookie",
                cookie.toString()
        );
    }

    /*
     * ==========================================================
     * TOKEN INVÁLIDO
     * ==========================================================
     */

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
    private void handleInvalidToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        disableCache(
                response
        );

        if (isAjaxRequest(request)) {

            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
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
                    false
            );

            result.put(
                    "type",
                    "error"
            );

            result.put(
                    "message",
                    "La solicitud de seguridad no es válida. Recarga la página e intenta nuevamente."
            );

            response.getWriter().write(
                    gson.toJson(
                            result
                    )
            );

            return;
        }

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "La solicitud de seguridad no es válida. Regresa a la página anterior, recárgala e intenta nuevamente."
        );
    }

    /*
     * ==========================================================
     * AJAX
     * ==========================================================
     */

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
    private boolean isAjaxRequest(
            HttpServletRequest request
    ) {

        String requestedWith =
                request.getHeader(
                        "X-Requested-With"
                );

        if ("XMLHttpRequest".equalsIgnoreCase(
                requestedWith
        )) {
            return true;
        }

        String accept =
                request.getHeader(
                        "Accept"
                );

        return accept != null
                && accept.toLowerCase(
                Locale.ROOT
        ).contains(
                "application/json"
        );
    }

    /*
     * ==========================================================
     * CACHE
     * ==========================================================
     */

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

    /**
     * Ejecuta la operación específica de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void destroy() {
        gson = null;
    }
}
