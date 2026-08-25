/**
 * ==========================================================
 * COMPONENTE: TOAST DE NOTIFICACIONES
 * ==========================================================
 *
 * El toast únicamente se encarga de mostrar mensajes.
 *
 * No conoce productos, usuarios, áreas ni proveedores.
 * No lee parámetros de la URL.
 * No contiene mensajes del servidor.
 *
 * Ejemplos:
 *
 * AppToast.success("Producto registrado correctamente.");
 * AppToast.error("No fue posible registrar el producto.");
 * AppToast.warning("Completa los campos obligatorios.");
 * AppToast.info("La sesión está por expirar.");
 *
 * También puede utilizarse:
 *
 * AppToast.show("Mensaje", "success");
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */

(function () {
    "use strict";

    class ToastManager {

        /**
         * @param {Object} config
         * @param {string} config.elementId
         * @param {number} config.delay
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        constructor(config = {}) {

            this.elementId =
                config.elementId || "toastNotification";

            this.delay =
                Number(config.delay) > 0
                    ? Number(config.delay)
                    : 3500;

            this.toastElement = null;
            this.toastInstance = null;
            this.initialized = false;
        }

        /**
         * Inicializa el toast de Bootstrap.
         *
         * Se ejecuta automáticamente cuando sea necesario.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        init() {

            if (this.initialized) {
                return true;
            }

            this.toastElement =
                document.getElementById(this.elementId);

            if (!this.toastElement) {

                console.warn(
                    `No se encontró el toast con id "${this.elementId}".`
                );

                return false;
            }

            if (
                typeof bootstrap === "undefined" ||
                typeof bootstrap.Toast === "undefined"
            ) {

                console.error(
                    "Bootstrap Toast no está disponible. " +
                    "Carga bootstrap.bundle.min.js antes de toast.js."
                );

                return false;
            }

            this.toastInstance =
                bootstrap.Toast.getOrCreateInstance(
                    this.toastElement,
                    {
                        delay: this.delay,
                        autohide: true
                    }
                );

            this.initialized = true;

            return true;
        }

        /**
         * Muestra una notificación.
         *
         * @param {string} message
         * @param {"success"|"error"|"warning"|"info"} type
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        show(message, type = "success") {

            if (!this.init()) {
                return false;
            }

            const toastTypes = {

                success: {
                    color: "#57d38c",
                    icon: "bi-check-circle-fill"
                },

                error: {
                    color: "#ff6666",
                    icon: "bi-exclamation-circle-fill"
                },

                warning: {
                    color: "#ffc857",
                    icon: "bi-exclamation-triangle-fill"
                },

                info: {
                    color: "#6390ff",
                    icon: "bi-info-circle-fill"
                }

            };

            const normalizedType =
                typeof type === "string"
                    ? type.toLowerCase().trim()
                    : "success";

            const selectedType =
                toastTypes[normalizedType] ||
                toastTypes.info;

            const icon =
                this.toastElement.querySelector("#toastIcon");

            const messageElement =
                this.toastElement.querySelector("#toastMessage");

            if (icon) {

                icon.className =
                    `bi ${selectedType.icon}`;

                icon.style.color =
                    selectedType.color;
            }

            if (messageElement) {

                messageElement.textContent =
                    this.normalizeMessage(message);
            }

            this.toastElement.dataset.type =
                normalizedType;

            this.toastInstance.show();

            return true;
        }

        /**
         * Muestra mensaje de éxito.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        success(message) {
            return this.show(message, "success");
        }

        /**
         * Muestra mensaje de error.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        error(message) {
            return this.show(message, "error");
        }

        /**
         * Muestra mensaje de advertencia.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        warning(message) {
            return this.show(message, "warning");
        }

        /**
         * Muestra mensaje informativo.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        info(message) {
            return this.show(message, "info");
        }

        /**
         * Muestra automáticamente una respuesta JSON del servidor.
         *
         * Formato esperado:
         *
         * {
         *     success: true,
         *     type: "success",
         *     message: "Operación realizada correctamente."
         * }
         *
         * @param {Object} response
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        fromResponse(response) {

            if (!response || typeof response !== "object") {

                return this.error(
                    "El servidor devolvió una respuesta no válida."
                );
            }

            const type =
                response.type ||
                (
                    response.success === false
                        ? "error"
                        : "success"
                );

            const message =
                response.message ||
                (
                    response.success === false
                        ? "No fue posible completar la operación."
                        : "Operación realizada correctamente."
                );

            return this.show(message, type);
        }

        /**
         * Oculta el toast.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        hide() {

            if (!this.init()) {
                return false;
            }

            this.toastInstance.hide();

            return true;
        }

        /**
         * Cambia el tiempo de duración.
         *
         * @param {number} delay
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        setDelay(delay) {

            const normalizedDelay =
                Number(delay);

            if (
                !Number.isFinite(normalizedDelay) ||
                normalizedDelay <= 0
            ) {

                console.warn(
                    "El tiempo del toast debe ser mayor que cero."
                );

                return false;
            }

            this.delay = normalizedDelay;

            if (this.toastInstance) {

                this.toastInstance.dispose();
                this.toastInstance = null;
                this.initialized = false;

                this.init();
            }

            return true;
        }

        /**
         * Libera la instancia de Bootstrap.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        dispose() {

            if (this.toastInstance) {
                this.toastInstance.dispose();
            }

            this.toastInstance = null;
            this.toastElement = null;
            this.initialized = false;
        }

        /**
         * Garantiza un mensaje válido.
         *
         * @param {*} message
         * @returns {string}
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        normalizeMessage(message) {

            if (message === null || message === undefined) {
                return "Operación realizada.";
            }

            const normalizedMessage =
                String(message).trim();

            return normalizedMessage ||
                "Operación realizada.";
        }
    }

    /**
     * ==========================================================
     * INSTANCIA GLOBAL
     * ==========================================================
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */

    window.ToastManager =
        ToastManager;

    window.AppToast =
        new ToastManager({
            elementId: "toastNotification",
            delay: 3500
        });

})();
