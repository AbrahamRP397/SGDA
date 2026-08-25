/**
 * Sincroniza el logotipo principal con el tema visual y el estado del menú.
 * El módulo se encapsula para no publicar funciones auxiliares en {@code window}.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function () {

    "use strict";

    /**
     * Resuelve el tema efectivo respetando el atributo HTML, la preferencia
     * guardada y, como último recurso, la configuración del sistema operativo.
     *
     * @returns {"dark"|"light"} tema que debe utilizar la interfaz
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getCurrentTheme() {

        const htmlTheme =
            document.documentElement.getAttribute("data-theme");

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


    /**
     * Obtiene la ruta base de despliegue para construir direcciones portables.
     *
     * @returns {string} context path, por ejemplo {@code /integradora}
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

        // En /integradora/login, el primer segmento corresponde al contexto.
        return pathParts.length > 1
            ? "/" + pathParts[0]
            : "";
    }


    /**
     * Selecciona el recurso SVG apropiado según el tema y conserva la variante
     * abierta o cerrada que representa el estado actual del menú lateral.
     *
     * @returns {void}
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function updateLogo() {

        const logo =
            document.getElementById("logoSistema");

        if (!logo) {
            return;
        }

        const theme =
            getCurrentTheme();

        const currentSource =
            logo.getAttribute("src") || "";

        const isClosed =
            currentSource.includes("logoSGDAClosed");

        const baseName =
            isClosed
                ? "logoSGDAClosed"
                : "logoSGDA";

        const suffix =
            theme === "dark"
                ? "-dark"
                : "";

        const newSource =
            getContextPath()
            + "/assets/img/"
            + baseName
            + suffix
            + ".svg";

        if (
            logo.getAttribute("src")
            !== newSource
        ) {
            logo.src = newSource;
        }
    }


    /**
     * Realiza la sincronización inicial y registra observadores para cambios
     * emitidos por el selector de tema o aplicados al atributo {@code data-theme}.
     *
     * @returns {void}
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function init() {

        updateLogo();

        document.addEventListener(
            "themeChanged",
            updateLogo
        );

        const observer =
            new MutationObserver(function (mutations) {

                mutations.forEach(function (mutation) {

                    if (
                        mutation.attributeName
                        === "data-theme"
                    ) {
                        updateLogo();
                    }

                });

            });

        observer.observe(
            document.documentElement,
            {
                attributes: true,
                attributeFilter: ["data-theme"]
            }
        );
    }


    if (
        document.readyState === "loading"
    ) {

        document.addEventListener(
            "DOMContentLoaded",
            init
        );

    } else {

        init();
    }

})();
