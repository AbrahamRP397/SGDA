/**
 * Módulo de perfil de usuario.
 * Valida la edición de datos propios y el cambio de contraseña antes de enviarlos.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
document.addEventListener(
    "DOMContentLoaded",
    function () {

        "use strict";

        const contextPath =
            document.body.dataset.contextPath || "";

        const formEditProfile =
            document.getElementById(
                "formEditProfile"
            );

        const formChangePassword =
            document.getElementById(
                "formChangePassword"
            );

        const btnSaveProfile =
            document.getElementById(
                "btnSaveProfile"
            );

        const btnConfirmPassword =
            document.getElementById(
                "btnConfirmPassword"
            );

        const modalEditElement =
            document.getElementById(
                "modalEditProfile"
            );

        const modalPasswordElement =
            document.getElementById(
                "modalChangePassword"
            );

        const phoneInput =
            document.getElementById(
                "profileEditPhone"
            );

        if (!window.bootstrap) {

            console.error(
                "Bootstrap no está disponible."
            );

            return;
        }

        if (!window.Api) {

            console.error(
                "api.js no está disponible."
            );

            return;
        }

        const modalEdit =
            modalEditElement
                ? bootstrap.Modal.getOrCreateInstance(
                    modalEditElement
                )
                : null;

        const modalPassword =
            modalPasswordElement
                ? bootstrap.Modal.getOrCreateInstance(
                    modalPasswordElement
                )
                : null;

        /* ======================================================
           TELÉFONO
           ====================================================== */

        phoneInput?.addEventListener(
            "input",
            function () {

                phoneInput.value =
                    phoneInput.value
                        .replace(/\D/g, "")
                        .slice(0, 10);
            }
        );

        /* ======================================================
           AL ABRIR EDITAR PERFIL
           ====================================================== */

        modalEditElement?.addEventListener(
            "shown.bs.modal",
            function () {

                clearValidation(
                    formEditProfile
                );

                document.getElementById(
                    "profileName"
                )?.focus();
            }
        );

        /* ======================================================
           AL ABRIR CONTRASEÑA
           ====================================================== */

        modalPasswordElement?.addEventListener(
            "show.bs.modal",
            function () {

                if (formChangePassword) {

                    formChangePassword.reset();

                    clearValidation(
                        formChangePassword
                    );
                }
            }
        );

        modalPasswordElement?.addEventListener(
            "shown.bs.modal",
            function () {

                document.getElementById(
                    "currentPassword"
                )?.focus();
            }
        );

        /* ======================================================
           EDITAR PERFIL
           ====================================================== */

        formEditProfile?.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();

                event.stopPropagation();

                if (
                    formEditProfile.dataset
                        .submitting === "true"
                ) {
                    return;
                }

                clearValidation(
                    formEditProfile
                );

                if (!formEditProfile.checkValidity()) {

                    formEditProfile.classList.add(
                        "was-validated"
                    );

                    formEditProfile.querySelector(
                        ":invalid"
                    )?.focus();

                    return;
                }

                formEditProfile.dataset.submitting =
                    "true";

                setButtonLoading(
                    btnSaveProfile,
                    true,
                    "Guardando..."
                );

                try {

                    const result =
                        await Api.submitForm(
                            formEditProfile
                        );

                    showResult(result);

                    if (!result?.success) {
                        return;
                    }

                    /*
                     * Actualizamos primero lo visible.
                     */
                    updateProfileView(
                        result.data
                    );

                    modalEdit?.hide();

                    /*
                     * Recargamos para sincronizar también
                     * sidebar y cualquier dato de sesión.
                     */
                    window.setTimeout(
                        function () {

                            window.location.href =
                                contextPath + "/perfil";
                        },
                        650
                    );

                } catch (error) {

                    handleError(error);

                } finally {

                    delete formEditProfile.dataset
                        .submitting;

                    setButtonLoading(
                        btnSaveProfile,
                        false
                    );
                }
            }
        );

        /* ======================================================
           CAMBIAR CONTRASEÑA
           ====================================================== */

        formChangePassword?.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();

                event.stopPropagation();

                if (
                    formChangePassword.dataset
                        .submitting === "true"
                ) {
                    return;
                }

                clearValidation(
                    formChangePassword
                );

                if (!formChangePassword.checkValidity()) {

                    formChangePassword.classList.add(
                        "was-validated"
                    );

                    formChangePassword.querySelector(
                        ":invalid"
                    )?.focus();

                    return;
                }

                const currentPassword =
                    document.getElementById(
                        "currentPassword"
                    )?.value || "";

                const newPassword =
                    document.getElementById(
                        "newPassword"
                    )?.value || "";

                const confirmation =
                    document.getElementById(
                        "confirmation"
                    )?.value || "";

                if (
                    newPassword !== confirmation
                ) {

                    showMessage(
                        "La nueva contraseña y su confirmación no coinciden.",
                        "warning"
                    );

                    document.getElementById(
                        "confirmation"
                    )?.focus();

                    return;
                }

                if (
                    currentPassword === newPassword
                ) {

                    showMessage(
                        "La nueva contraseña debe ser diferente de la contraseña actual.",
                        "warning"
                    );

                    document.getElementById(
                        "newPassword"
                    )?.focus();

                    return;
                }

                formChangePassword.dataset.submitting =
                    "true";

                setButtonLoading(
                    btnConfirmPassword,
                    true,
                    "Actualizando..."
                );

                try {

                    const result =
                        await Api.submitForm(
                            formChangePassword
                        );

                    showResult(result);

                    if (!result?.success) {
                        return;
                    }

                    formChangePassword.reset();

                    clearValidation(
                        formChangePassword
                    );

                    modalPassword?.hide();

                } catch (error) {

                    handleError(error);

                } finally {

                    delete formChangePassword.dataset
                        .submitting;

                    setButtonLoading(
                        btnConfirmPassword,
                        false
                    );
                }
            }
        );

        /* ======================================================
           ACTUALIZAR DATOS VISIBLES
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} data datos que serán procesados por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateProfileView(data) {

            if (!data) {
                return;
            }

            setText(
                "profileFullName",
                data.fullName
            );

            setText(
                "profileEmail",
                data.email
            );

            setText(
                "profilePhone",
                data.phone
            );

            setText(
                "profileRole",
                data.role
            );

            setValue(
                "profileName",
                data.name
            );

            setValue(
                "profileSurname",
                data.surname
            );

            setValue(
                "profileLastname",
                data.lastname
            );

            setValue(
                "profileEditPhone",
                data.phone
            );

            setValue(
                "profileEditEmail",
                data.email
            );
        }

        /* ======================================================
           MENSAJES
           ====================================================== */

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} result valor de result requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function showResult(result) {

            if (
                window.AppToast &&
                typeof AppToast.fromResponse
                === "function"
            ) {

                AppToast.fromResponse(
                    result
                );

                return;
            }

            showMessage(
                result?.message ||
                "Operación realizada.",
                result?.type ||
                (
                    result?.success
                        ? "success"
                        : "error"
                )
            );
        }

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} message valor de message requerido por la función
         * @param {*} type valor de type requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function showMessage(
            message,
            type
        ) {

            if (
                window.AppToast &&
                typeof AppToast.show === "function"
            ) {

                AppToast.show(
                    message,
                    type
                );

                return;
            }

            alert(message);
        }

        /**
         * Procesa el evento de interfaz asociado a esta función.
         *
         * @param {*} error valor de error requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function handleError(error) {

            console.error(error);

            if (
                Number(error?.status) === 401
            ) {

                window.location.href =
                    contextPath + "/login";

                return;
            }

            if (
                Number(error?.status) === 403
            ) {

                showMessage(
                    "La solicitud fue rechazada. Recarga la página e inténtalo nuevamente.",
                    "error"
                );

                return;
            }

            showMessage(
                error?.message ||
                "No fue posible completar la operación.",
                "error"
            );
        }

        /* ======================================================
           ESTADO DE BOTONES
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} button valor de button requerido por la función
         * @param {*} loading valor de loading requerido por la función
         * @param {*} text valor de text requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setButtonLoading(
            button,
            loading,
            text
        ) {

            if (!button) {
                return;
            }

            if (
                !button.dataset.originalHtml
            ) {

                button.dataset.originalHtml =
                    button.innerHTML;
            }

            button.disabled =
                Boolean(loading);

            if (loading) {

                button.innerHTML =
                    `
                    <span class="spinner-border spinner-border-sm"
                          aria-hidden="true"></span>
                    <span>${escapeHtml(
                        text || "Procesando..."
                    )}</span>
                    `;

                return;
            }

            button.innerHTML =
                button.dataset.originalHtml;
        }

        /* ======================================================
           VALIDACIÓN
           ====================================================== */

        /**
         * Retira o limpia la información indicada de la interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function clearValidation(form) {

            if (!form) {
                return;
            }

            form.classList.remove(
                "was-validated"
            );

            form.querySelectorAll(
                ".is-valid, .is-invalid"
            ).forEach(
                function (element) {

                    element.classList.remove(
                        "is-valid",
                        "is-invalid"
                    );
                }
            );
        }

        /* ======================================================
           DOM
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} id identificador del registro o componente
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setText(
            id,
            value
        ) {

            const element =
                document.getElementById(id);

            if (!element) {
                return;
            }

            element.textContent =
                normalizeText(value) || "-";
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} id identificador del registro o componente
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setValue(
            id,
            value
        ) {

            const element =
                document.getElementById(id);

            if (!element) {
                return;
            }

            element.value =
                normalizeText(value);
        }

        /**
         * Ejecuta la operación normalizeText del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeText(value) {

            return String(
                value ?? ""
            )
                .trim()
                .replace(/\s+/g, " ");
        }

        /**
         * Ejecuta la operación escapeHtml del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function escapeHtml(value) {

            return String(
                value ?? ""
            )
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }
    }
);
