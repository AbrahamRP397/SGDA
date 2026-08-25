package com.almacen.integradora.controllers.user;

import com.almacen.integradora.models.user.User;
import com.almacen.integradora.models.user.UserDao;
import com.almacen.integradora.utils.PasswordPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * Define ProfileServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name = "ProfileServlet",
        urlPatterns = {
                "/perfil",
                "/perfil/update",
                "/perfil/change-password"
        }
)
/** Controlador HTTP para consultar y modificar la cuenta autenticada.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class ProfileServlet extends HttpServlet {

    private UserDao userDao;
    private Gson gson;

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s'-]{2,50}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{10}$");

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init() {
        userDao = new UserDao();

        gson = new GsonBuilder()
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

        if ("/perfil".equals(request.getServletPath())) {
            showProfile(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
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

            case "/perfil/update" ->
                    updateProfile(request, response);

            case "/perfil/change-password" ->
                    changePassword(request, response);

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
       MOSTRAR PERFIL
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
    private void showProfile(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        User sessionUser = getSessionUser(request);

        if (!hasValidSessionUser(sessionUser)) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {

            User currentUser =
                    userDao.getById(
                            sessionUser.getId().intValue()
                    );

            if (currentUser == null
                    || !Integer.valueOf(1)
                    .equals(currentUser.getStatus())) {

                invalidateSession(request);

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );

                return;
            }

            synchronizeSession(
                    request,
                    currentUser
            );

            request.setAttribute(
                    "profileUser",
                    currentUser
            );

            request.getRequestDispatcher(
                    "/views/user/profile.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al cargar el perfil.",
                    exception
            );

            request.setAttribute(
                    "profileError",
                    "No fue posible cargar la información de tu perfil."
            );

            request.getRequestDispatcher(
                    "/views/user/profile.jsp"
            ).forward(
                    request,
                    response
            );
        }
    }

    /* ==========================================================
       ACTUALIZAR INFORMACIÓN
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
    private void updateProfile(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        User sessionUser = getSessionUser(request);

        if (!hasValidSessionUser(sessionUser)) {

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

        String name =
                normalizeText(
                        request.getParameter("name")
                );

        String surname =
                normalizeText(
                        request.getParameter("surname")
                );

        String lastname =
                normalizeText(
                        request.getParameter("lastname")
                );

        String phone =
                normalizeDigits(
                        request.getParameter("phone")
                );

        String email =
                normalizeEmail(
                        request.getParameter("email")
                );

        String validationMessage =
                validateProfile(
                        name,
                        surname,
                        lastname,
                        phone,
                        email
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

        try {

            User currentUser =
                    userDao.getById(
                            sessionUser.getId().intValue()
                    );

            if (currentUser == null
                    || !Integer.valueOf(1)
                    .equals(currentUser.getStatus())) {

                sendJson(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        false,
                        "error",
                        "Tu cuenta ya no se encuentra disponible.",
                        null
                );

                return;
            }

            User existingEmail =
                    userDao.findAnyByEmail(email);

            if (existingEmail != null
                    && existingEmail.getId() != null
                    && !existingEmail.getId()
                    .equals(currentUser.getId())) {

                sendJson(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        false,
                        "warning",
                        "Ya existe otro usuario registrado con ese correo electrónico.",
                        null
                );

                return;
            }

            /*
             * Solamente permitimos cambiar información personal.
             * El rol y el estado permanecen intactos.
             */
            currentUser.setName(name);
            currentUser.setSurname(surname);
            currentUser.setLastname(lastname);
            currentUser.setPhone(phone);
            currentUser.setEmail(email);

            if (!userDao.update(currentUser)) {

                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible actualizar tu perfil.",
                        null
                );

                return;
            }

            User updatedUser =
                    userDao.getById(
                            currentUser.getId().intValue()
                    );

            if (updatedUser == null) {

                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "Los datos fueron actualizados, pero no fue posible recargar el perfil.",
                        null
                );

                return;
            }

            synchronizeSession(
                    request,
                    updatedUser
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "Tu información se actualizó correctamente.",
                    buildProfileData(updatedUser)
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al actualizar el perfil.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible actualizar tu perfil.",
                    null
            );
        }
    }

    /* ==========================================================
       CAMBIAR CONTRASEÑA
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
    private void changePassword(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        User sessionUser = getSessionUser(request);

        if (!hasValidSessionUser(sessionUser)) {

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

        String currentPassword =
                request.getParameter(
                        "currentPassword"
                );

        String newPassword =
                request.getParameter(
                        "newPassword"
                );

        String confirmation =
                request.getParameter(
                        "confirmation"
                );

        if (currentPassword == null
                || currentPassword.isBlank()) {

            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    "Ingresa tu contraseña actual.",
                    null
            );

            return;
        }

        if (!PasswordPolicy.isValid(newPassword)) {

            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    PasswordPolicy.getValidationMessage(),
                    null
            );

            return;
        }

        if (confirmation == null
                || !newPassword.equals(confirmation)) {

            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    "La nueva contraseña y su confirmación no coinciden.",
                    null
            );

            return;
        }

        if (currentPassword.equals(newPassword)) {

            sendJson(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    false,
                    "warning",
                    "La nueva contraseña debe ser diferente de la contraseña actual.",
                    null
            );

            return;
        }

        try {

            User currentUser =
                    userDao.getById(
                            sessionUser.getId().intValue()
                    );

            if (currentUser == null
                    || !Integer.valueOf(1)
                    .equals(currentUser.getStatus())) {

                sendJson(
                        response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        false,
                        "error",
                        "Tu cuenta ya no se encuentra disponible.",
                        null
                );

                return;
            }

            /*
             * Verificamos la contraseña actual usando exactamente
             * el método de login del DAO, que ya utiliza BCrypt.
             */
            User authenticatedUser =
                    userDao.login(
                            currentUser.getEmail(),
                            currentPassword
                    );

            if (authenticatedUser == null) {

                sendJson(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        false,
                        "warning",
                        "La contraseña actual no es correcta.",
                        null
                );

                return;
            }

            if (!userDao.updatePasswordById(
                    currentUser.getId(),
                    newPassword
            )) {

                sendJson(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        false,
                        "error",
                        "No fue posible cambiar la contraseña.",
                        null
                );

                return;
            }

            User updatedUser =
                    userDao.getById(
                            currentUser.getId().intValue()
                    );

            if (updatedUser != null) {
                synchronizeSession(
                        request,
                        updatedUser
                );
            }

            HttpSession session =
                    request.getSession(false);

            if (session != null) {

                session.removeAttribute(
                        "passwordChangeRequired"
                );
            }

            sendJson(
                    response,
                    HttpServletResponse.SC_OK,
                    true,
                    "success",
                    "Tu contraseña se cambió correctamente.",
                    null
            );

        } catch (RuntimeException exception) {

            getServletContext().log(
                    "Error al cambiar la contraseña.",
                    exception
            );

            sendJson(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    false,
                    "error",
                    "No fue posible cambiar la contraseña.",
                    null
            );
        }
    }

    /* ==========================================================
       VALIDACIONES
       ========================================================== */

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param name valor de name requerido por la operación
     * @param surname valor de surname requerido por la operación
     * @param lastname valor de lastname requerido por la operación
     * @param phone valor de phone requerido por la operación
     * @param email dirección de correo asociada a la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String validateProfile(
            String name,
            String surname,
            String lastname,
            String phone,
            String email
    ) {

        if (name.isBlank()
                || surname.isBlank()
                || lastname.isBlank()
                || phone.isBlank()
                || email.isBlank()) {

            return "Completa todos los campos obligatorios.";
        }

        if (!NAME_PATTERN.matcher(name).matches()) {
            return "El nombre no tiene un formato válido.";
        }

        if (!NAME_PATTERN.matcher(surname).matches()) {
            return "El apellido paterno no tiene un formato válido.";
        }

        if (!NAME_PATTERN.matcher(lastname).matches()) {
            return "El apellido materno no tiene un formato válido.";
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return "El teléfono debe contener exactamente 10 dígitos.";
        }

        if (email.length() > 100
                || !EMAIL_PATTERN.matcher(email).matches()) {

            return "El correo electrónico no tiene un formato válido.";
        }

        return null;
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

        if (value instanceof User user) {
            return user;
        }

        return null;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private boolean hasValidSessionUser(
            User user
    ) {

        return user != null
                && user.getId() != null
                && user.getId() > 0;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param user valor de user requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void synchronizeSession(
            HttpServletRequest request,
            User user
    ) {

        if (user == null) {
            return;
        }

        /*
         * Nunca dejamos el hash de contraseña
         * almacenado dentro de la sesión.
         */
        user.setPassword(null);

        HttpSession session =
                request.getSession(true);

        session.setAttribute(
                "usuario",
                user
        );

        session.setAttribute(
                "rol",
                user.getRole()
        );

        session.setAttribute(
                "nombreCompleto",
                buildFullName(user)
        );
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param request solicitud HTTP recibida por el servlet
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void invalidateSession(
            HttpServletRequest request
    ) {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            return;
        }

        try {
            session.invalidate();
        } catch (IllegalStateException ignored) {
        }
    }

    /* ==========================================================
       DATOS PARA JAVASCRIPT
       ========================================================== */

    private Map<String, Object> buildProfileData(
            User user
    ) {

        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put(
                "id",
                user.getId()
        );

        data.put(
                "name",
                user.getName()
        );

        data.put(
                "surname",
                user.getSurname()
        );

        data.put(
                "lastname",
                user.getLastname()
        );

        data.put(
                "fullName",
                buildFullName(user)
        );

        data.put(
                "phone",
                user.getPhone()
        );

        data.put(
                "email",
                user.getEmail()
        );

        data.put(
                "role",
                user.getRole()
        );

        data.put(
                "status",
                user.getStatus()
        );

        return data;
    }

    /* ==========================================================
       JSON
       ========================================================== */

    /**
     * Construye o envía la respuesta requerida por el cliente HTTP.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param status estado que se utilizará en la operación
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
            int status,
            boolean success,
            String type,
            String message,
            Object data
    ) throws IOException {

        response.setStatus(status);

        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("success", success);
        result.put("type", type);
        result.put("message", message);
        result.put("data", data);

        response.getWriter().write(
                gson.toJson(result)
        );
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

        return value.trim()
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

        return normalizeText(value)
                .toLowerCase(
                        Locale.ROOT
                );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param user valor de user requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildFullName(
            User user
    ) {

        if (user == null) {
            return "";
        }

        return (
                normalizeText(user.getName())
                        + " "
                        + normalizeText(user.getSurname())
                        + " "
                        + normalizeText(user.getLastname())
        ).trim()
                .replaceAll(
                        "\\s+",
                        " "
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
