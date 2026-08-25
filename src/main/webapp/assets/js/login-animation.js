/** Coordina la transición visual posterior a una autenticación exitosa.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
document.addEventListener(
    "DOMContentLoaded",
    function () {

        "use strict";

        if (!window.loginExitoso) {
            return;
        }

        const logo =
            document.getElementById("logoSistema");

        if (!logo) {
            return;
        }


        /*
         * ======================================================
         * OBTENER TEMA ACTUAL
         * ======================================================
         */

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getCurrentTheme() {

            const htmlTheme =
                document.documentElement.getAttribute(
                    "data-theme"
                );

            if (
                htmlTheme === "dark"
                || htmlTheme === "light"
            ) {
                return htmlTheme;
            }

            const savedTheme =
                localStorage.getItem("theme");

            if (
                savedTheme === "dark"
                || savedTheme === "light"
            ) {
                return savedTheme;
            }

            return window.matchMedia(
                "(prefers-color-scheme: dark)"
            ).matches
                ? "dark"
                : "light";
        }


        /*
         * ======================================================
         * OBTENER CONTEXT PATH
         * ======================================================
         */

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getContextPath() {

            if (
                typeof window.contextPath === "string"
            ) {
                return window.contextPath;
            }

            const pathParts =
                window.location.pathname
                    .split("/")
                    .filter(Boolean);

            return pathParts.length > 1
                ? "/" + pathParts[0]
                : "";
        }


        /*
         * ======================================================
         * INICIAR ANIMACIÓN DE CIERRE
         * ======================================================
         */

        logo.classList.remove(
            "logo-abrir"
        );

        logo.classList.add(
            "logo-cerrar"
        );


        /*
         * ======================================================
         * CAMBIAR AL LOGO ABIERTO
         * ======================================================
         */

        setTimeout(function () {

            const theme =
                getCurrentTheme();

            const logoFile =
                theme === "dark"
                    ? "logoSGDA-dark.svg"
                    : "logoSGDA.svg";

            logo.src =
                getContextPath()
                + "/assets/img/"
                + logoFile;

            logo.classList.remove(
                "logo-cerrar"
            );

            /*
             * Reinicia correctamente la animación.
             */
            logo.classList.remove(
                "logo-abrir"
            );

            void logo.offsetWidth;

            logo.classList.add(
                "logo-abrir"
            );

        }, 450);


        /*
         * ======================================================
         * REDIRECCIONAR AL DASHBOARD
         * ======================================================
         */

        setTimeout(function () {

            const redirectUrl =
                window.redirectUrl
                || (
                    getContextPath()
                    + "/dashboard"
                );

            window.location.replace(
                redirectUrl
            );

        }, 1700);

    }
);
