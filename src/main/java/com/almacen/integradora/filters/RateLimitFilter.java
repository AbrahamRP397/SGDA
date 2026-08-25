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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Define RateLimitFilter y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebFilter("/*")
/** Filtro que limita solicitudes repetidas en rutas sensibles para reducir abuso.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class RateLimitFilter implements Filter {

    private Gson gson;

    private static final String LOGIN_PATH = "/login";
    private static final String RECOVERY_PATH = "/verify-email";

    /*
     * ==========================================================
     * LÍMITES
     * ==========================================================
     *
     * LOGIN:
     * 20 peticiones válidas por IP cada 5 minutos.
     *
     * RECUPERACIÓN:
     * 5 peticiones válidas por IP cada 15 minutos.
     *
     * LoginServlet mantiene además su propio límite de intentos
     * fallidos por sesión. Este filtro es una segunda barrera
     * basada en dirección IP.
     */
    private static final int LOGIN_MAX_REQUESTS = 20;
    private static final long LOGIN_WINDOW_MILLIS =
            5 * 60 * 1000L;

    private static final int RECOVERY_MAX_REQUESTS = 5;
    private static final long RECOVERY_WINDOW_MILLIS =
            15 * 60 * 1000L;

    /*
     * Limpieza de buckets antiguos.
     */
    private static final long CLEANUP_INTERVAL_MILLIS =
            5 * 60 * 1000L;

    private static final long ENTRY_MAX_IDLE_MILLIS =
            30 * 60 * 1000L;

    /*
     * ==========================================================
     * CSRF
     * ==========================================================
     *
     * Debe coincidir con CsrfFilter.
     *
     * El sistema actual utiliza double-submit cookie:
     *
     * COOKIE:
     * XSRF-TOKEN
     *
     * HEADER:
     * X-CSRF-Token
     *
     * o parámetro:
     * csrfToken
     *
     * Ya NO utilizamos HttpSession para almacenar el token.
     */
    private static final String CSRF_COOKIE_NAME =
            "XSRF-TOKEN";

    private static final String CSRF_HEADER_NAME =
            "X-CSRF-Token";

    private static final String CSRF_PARAMETER_NAME =
            "csrfToken";

    private static final ConcurrentHashMap<String, RequestBucket>
            BUCKETS = new ConcurrentHashMap<>();

    private static final AtomicLong LAST_CLEANUP =
            new AtomicLong(
                    System.currentTimeMillis()
            );

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

        String path =
                getRequestPath(request);

        /*
         * Solo limitamos los endpoints definidos.
         *
         * Cualquier otra petición continúa normalmente.
         */
        if (!shouldLimit(
                request,
                path
        )) {

            chain.doFilter(
                    request,
                    response
            );

            return;
        }

        /*
         * ======================================================
         * VALIDAR CSRF ANTES DE CONTABILIZAR
         * ======================================================
         *
         * Solo contabilizamos solicitudes que conozcan el token
         * CSRF legítimo de la aplicación.
         *
         * Esto evita que un sitio externo pueda consumir
         * deliberadamente el límite de solicitudes de una IP
         * enviando POST sin conocer el token.
         *
         * CsrfFilter seguirá siendo el responsable de rechazar
         * formalmente cualquier solicitud cuyo CSRF sea inválido.
         */
        if (!hasValidCsrfToken(request)) {

            chain.doFilter(
                    request,
                    response
            );

            return;
        }

        cleanupIfNecessary();

        RateLimitPolicy policy =
                getPolicy(path);

        if (policy == null) {

            chain.doFilter(
                    request,
                    response
            );

            return;
        }

        String clientIp =
                getClientIp(request);

        String key =
                path
                        + "|"
                        + clientIp;

        long now =
                System.currentTimeMillis();

        RequestBucket bucket =
                BUCKETS.computeIfAbsent(
                        key,
                        ignored ->
                                new RequestBucket()
                );

        RateLimitResult result =
                bucket.register(
                        now,
                        policy.maxRequests(),
                        policy.windowMillis()
                );

        /*
         * ======================================================
         * LÍMITE EXCEDIDO
         * ======================================================
         */
        if (!result.allowed()) {

            handleRateLimitExceeded(
                    request,
                    response,
                    path,
                    result.retryAfterSeconds()
            );

            return;
        }

        /*
         * Cabeceras informativas.
         */
        response.setHeader(
                "X-RateLimit-Limit",
                String.valueOf(
                        policy.maxRequests()
                )
        );

        response.setHeader(
                "X-RateLimit-Remaining",
                String.valueOf(
                        result.remaining()
                )
        );

        chain.doFilter(
                request,
                response
        );
    }

    /*
     * ==========================================================
     * ENDPOINTS PROTEGIDOS
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param path valor de path requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean shouldLimit(
            HttpServletRequest request,
            String path
    ) {

        if (request == null
                || !"POST".equalsIgnoreCase(
                request.getMethod()
        )) {

            return false;
        }

        return LOGIN_PATH.equals(path)
                || RECOVERY_PATH.equals(path);
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param path valor de path requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private RateLimitPolicy getPolicy(
            String path
    ) {

        if (LOGIN_PATH.equals(path)) {

            return new RateLimitPolicy(
                    LOGIN_MAX_REQUESTS,
                    LOGIN_WINDOW_MILLIS
            );
        }

        if (RECOVERY_PATH.equals(path)) {

            return new RateLimitPolicy(
                    RECOVERY_MAX_REQUESTS,
                    RECOVERY_WINDOW_MILLIS
            );
        }

        return null;
    }

    /*
     * ==========================================================
     * RUTA
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
    private String getRequestPath(
            HttpServletRequest request
    ) {

        if (request == null) {
            return "";
        }

        String contextPath =
                request.getContextPath();

        String uri =
                request.getRequestURI();

        if (uri == null
                || uri.isBlank()) {

            return "";
        }

        if (contextPath == null
                || contextPath.isBlank()) {

            return uri;
        }

        if (uri.startsWith(contextPath)) {

            return uri.substring(
                    contextPath.length()
            );
        }

        return uri;
    }

    /*
     * ==========================================================
     * VALIDACIÓN CSRF
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
    private boolean hasValidCsrfToken(
            HttpServletRequest request
    ) {

        if (request == null) {
            return false;
        }

        String cookieToken =
                getCsrfCookieToken(request);

        if (!isValidCsrfTokenFormat(
                cookieToken
        )) {

            return false;
        }

        String receivedToken =
                getReceivedCsrfToken(request);

        if (!isValidCsrfTokenFormat(
                receivedToken
        )) {

            return false;
        }

        return constantTimeEquals(
                cookieToken,
                receivedToken
        );
    }

    /*
     * Buscar XSRF-TOKEN entre las cookies.
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
    private String getCsrfCookieToken(
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

            if (!CSRF_COOKIE_NAME.equals(
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
     * Primero intentamos obtener el token del header utilizado
     * por api.js.
     *
     * Como respaldo aceptamos el parámetro csrfToken para
     * formularios HTML tradicionales.
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
    private String getReceivedCsrfToken(
            HttpServletRequest request
    ) {

        String token =
                request.getHeader(
                        CSRF_HEADER_NAME
                );

        if (token == null
                || token.isBlank()) {

            token =
                    request.getParameter(
                            CSRF_PARAMETER_NAME
                    );
        }

        return token == null
                ? ""
                : token.trim();
    }

    /*
     * CsrfFilter genera 32 bytes y los codifica mediante
     * Base64 URL-safe sin padding.
     *
     * El resultado habitual tiene 43 caracteres.
     *
     * Conservamos el mismo rango que CsrfFilter para evitar
     * divergencias entre ambos filtros.
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
    private boolean isValidCsrfTokenFormat(
            String token
    ) {

        if (token == null
                || token.isBlank()) {

            return false;
        }

        if (token.length() < 40
                || token.length() > 64) {

            return false;
        }

        for (
                int index = 0;
                index < token.length();
                index++
        ) {

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
     * Comparación resistente a diferencias de tiempo.
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
     * IP
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
    private String getClientIp(
            HttpServletRequest request
    ) {

        /*
         * Por seguridad utilizamos RemoteAddr.
         *
         * No confiamos directamente en X-Forwarded-For porque
         * podría ser falsificado si Tomcat está expuesto
         * directamente.
         *
         * Si después el sistema se publica detrás de un proxy
         * inverso confiable, se podrá configurar soporte para
         * encabezados del proxy.
         */
        String remoteAddress =
                request.getRemoteAddr();

        if (remoteAddress == null
                || remoteAddress.isBlank()) {

            return "unknown";
        }

        return remoteAddress.trim();
    }

    /*
     * ==========================================================
     * RESPUESTA 429
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param path valor de path requerido por la operación
     * @param retryAfterSeconds valor de retryAfterSeconds requerido por la operación
     * @throws IOException si no puede completarse la operación
     * @throws ServletException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void handleRateLimitExceeded(
            HttpServletRequest request,
            HttpServletResponse response,
            String path,
            long retryAfterSeconds
    ) throws IOException, ServletException {

        disableCache(response);

        long safeRetryAfter =
                Math.max(
                        1,
                        retryAfterSeconds
                );

        response.setStatus(
                429
        );

        response.setHeader(
                "Retry-After",
                String.valueOf(
                        safeRetryAfter
                )
        );

        /*
         * AJAX / API
         */
        if (isAjaxRequest(request)) {

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
                    "warning"
            );

            result.put(
                    "message",
                    buildRateLimitMessage(
                            path,
                            safeRetryAfter
                    )
            );

            result.put(
                    "retryAfterSeconds",
                    safeRetryAfter
            );

            response.getWriter().write(
                    gson.toJson(result)
            );

            return;
        }

        /*
         * Formularios tradicionales.
         */
        request.setAttribute(
                "error",
                buildRateLimitMessage(
                        path,
                        safeRetryAfter
                )
        );

        if (LOGIN_PATH.equals(path)) {

            request.setAttribute(
                    "emailIngresado",
                    normalizeText(
                            request.getParameter(
                                    "email"
                            )
                    )
            );

            request.getRequestDispatcher(
                    "/login.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }

        if (RECOVERY_PATH.equals(path)) {

            request.getRequestDispatcher(
                    "/verify-email.jsp"
            ).forward(
                    request,
                    response
            );

            return;
        }

        response.sendError(
                429,
                "Se realizaron demasiadas solicitudes."
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param path valor de path requerido por la operación
     * @param retryAfterSeconds valor de retryAfterSeconds requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildRateLimitMessage(
            String path,
            long retryAfterSeconds
    ) {

        long minutes =
                Math.max(
                        1,
                        (long) Math.ceil(
                                retryAfterSeconds
                                        / 60.0
                        )
                );

        if (LOGIN_PATH.equals(path)) {

            return "Se realizaron demasiados intentos de inicio de sesión desde esta conexión. Intenta nuevamente en aproximadamente "
                    + minutes
                    + (
                    minutes == 1
                            ? " minuto."
                            : " minutos."
            );
        }

        return "Se realizaron demasiadas solicitudes de recuperación desde esta conexión. Intenta nuevamente en aproximadamente "
                + minutes
                + (
                minutes == 1
                        ? " minuto."
                        : " minutos."
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
     * LIMPIEZA DE BUCKETS
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void cleanupIfNecessary() {

        long now =
                System.currentTimeMillis();

        long previous =
                LAST_CLEANUP.get();

        if (now - previous
                < CLEANUP_INTERVAL_MILLIS) {

            return;
        }

        if (!LAST_CLEANUP.compareAndSet(
                previous,
                now
        )) {

            return;
        }

        BUCKETS.entrySet()
                .removeIf(
                        entry ->
                                entry.getValue()
                                        .isExpired(
                                                now,
                                                ENTRY_MAX_IDLE_MILLIS
                                        )
                );
    }

    /*
     * ==========================================================
     * TEXTO
     * ==========================================================
     */

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
                : value.trim();
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

    /*
     * ==========================================================
     * DESTROY
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void destroy() {

        BUCKETS.clear();

        gson = null;
    }

    /*
     * ==========================================================
     * POLÍTICA
     * ==========================================================
     */

    private record RateLimitPolicy(
            int maxRequests,
            long windowMillis
    ) {
    }

    private record RateLimitResult(
            boolean allowed,
            int remaining,
            long retryAfterSeconds
    ) {
    }

    /*
     * ==========================================================
     * BUCKET
     * ==========================================================
     */

    private static final class RequestBucket {

        private final Deque<Long> timestamps =
                new ArrayDeque<>();

        private long lastAccess =
                System.currentTimeMillis();

        /**
         * Registra la información recibida y confirma el resultado de la operación.
         *
         * @param now valor de now requerido por la operación
         * @param maxRequests valor de maxRequests requerido por la operación
         * @param windowMillis valor de windowMillis requerido por la operación
         * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        private synchronized RateLimitResult register(
                long now,
                int maxRequests,
                long windowMillis
        ) {

            lastAccess =
                    now;

            long minimumTimestamp =
                    now
                            - windowMillis;

            /*
             * Eliminar solicitudes que ya salieron de la ventana.
             */
            while (
                    !timestamps.isEmpty()
                            && timestamps.peekFirst()
                            <= minimumTimestamp
            ) {

                timestamps.removeFirst();
            }

            /*
             * Ya alcanzó el máximo.
             */
            if (timestamps.size()
                    >= maxRequests) {

                Long oldest =
                        timestamps.peekFirst();

                long retryAfterMillis =
                        oldest == null
                                ? windowMillis
                                : (
                                oldest
                                        + windowMillis
                                        - now
                        );

                long retryAfterSeconds =
                        Math.max(
                                1,
                                (long) Math.ceil(
                                        retryAfterMillis
                                                / 1000.0
                                )
                        );

                return new RateLimitResult(
                        false,
                        0,
                        retryAfterSeconds
                );
            }

            /*
             * Registrar solicitud actual.
             */
            timestamps.addLast(
                    now
            );

            int remaining =
                    Math.max(
                            0,
                            maxRequests
                                    - timestamps.size()
                    );

            return new RateLimitResult(
                    true,
                    remaining,
                    0
            );
        }

        /**
         * Evalúa la condición indicada para el estado actual.
         *
         * @param now valor de now requerido por la operación
         * @param maxIdleMillis valor de maxIdleMillis requerido por la operación
         * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        private synchronized boolean isExpired(
                long now,
                long maxIdleMillis
        ) {

            return now
                    - lastAccess
                    > maxIdleMillis;
        }
    }
}
