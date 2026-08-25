/**
 * Controlador del menú lateral responsivo.
 * Gestiona apertura, cierre y persistencia visual sin conocer módulos de negocio.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function () {
    "use strict";

    let initialized = false;

    /**
     * Inicializa los eventos y el estado del módulo.
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function initSidebar() {

        if (initialized) {
            return;
        }

        const sidebar =
            document.getElementById("sidebar-menu");

        if (!sidebar) {
            return;
        }

        initialized = true;

        const overlay =
            document.getElementById("sidebarOverlay");

        const btnOpen =
            document.getElementById("btnOpenSidebar");

        const btnClose =
            document.getElementById("btnCloseSidebar");

        const details =
            Array.from(
                sidebar.querySelectorAll(
                    ".sidebar-details"
                )
            );

        const navigationLinks =
            Array.from(
                sidebar.querySelectorAll(
                    "[data-sidebar-path]"
                )
            );

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function isMobile() {
            return window.innerWidth <= 768;
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function isTablet() {
            return window.innerWidth >= 769
                && window.innerWidth <= 1100;
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function isDesktop() {
            return window.innerWidth > 1100;
        }

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openSidebar() {

            if (!isMobile()) {
                return;
            }

            sidebar.classList.add("show");

            if (overlay) {
                overlay.classList.add("show");
            }

            document.body.classList.add(
                "sidebar-open"
            );
        }

        /**
         * Oculta el componente visual y restablece su estado temporal.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function closeSidebar() {

            sidebar.classList.remove("show");

            if (overlay) {
                overlay.classList.remove("show");
            }

            document.body.classList.remove(
                "sidebar-open"
            );
        }

        /**
         * Oculta el componente visual y restablece su estado temporal.
         *
         * @param {*} currentDetail valor de currentDetail requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function closeOtherDetails(currentDetail) {

            details.forEach(function (detail) {

                if (detail !== currentDetail) {
                    detail.open = false;
                }

            });
        }

        /**
         * Ejecuta la operación synchronizeTabletState del módulo de interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function synchronizeTabletState() {

            if (!isTablet()) {

                sidebar.classList.remove(
                    "tablet-expanded"
                );

                return;
            }

            const hasOpenDetails =
                details.some(function (detail) {
                    return detail.open;
                });

            sidebar.classList.toggle(
                "tablet-expanded",
                hasOpenDetails
            );
        }

        details.forEach(function (detail) {

            detail.addEventListener(
                "toggle",
                function () {

                    if (detail.open) {
                        closeOtherDetails(detail);
                    }

                    synchronizeTabletState();
                }
            );

        });

        /**
         * Oculta el componente visual y restablece su estado temporal.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function closeTabletMenus() {

            if (!isTablet()) {
                return;
            }

            details.forEach(function (detail) {
                detail.open = false;
            });

            sidebar.classList.remove(
                "tablet-expanded"
            );
        }

        document.addEventListener(
            "click",
            function (event) {

                if (!isTablet()) {
                    return;
                }

                if (sidebar.contains(event.target)) {
                    return;
                }

                closeTabletMenus();
            }
        );

        /**
         * Ejecuta la operación normalizePath del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizePath(value) {

            if (!value) {
                return "/";
            }

            let result =
                String(value)
                    .trim()
                    .replace(/\/+/g, "/");

            if (
                result.length > 1
                && result.endsWith("/")
            ) {
                result =
                    result.substring(
                        0,
                        result.length - 1
                    );
            }

            return result;
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getContextPath() {

            const bodyContext =
                document.body?.dataset?.contextPath;

            if (
                typeof bodyContext === "string"
                && bodyContext
            ) {
                return normalizePath(
                    bodyContext
                );
            }

            return "";
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getCurrentPath() {

            let path =
                normalizePath(
                    window.location.pathname
                );

            const contextPath =
                getContextPath();

            if (
                contextPath
                && contextPath !== "/"
                && path.startsWith(contextPath)
            ) {

                path =
                    path.substring(
                        contextPath.length
                    ) || "/";
            }

            return normalizePath(path);
        }

        /**
         * Ejecuta la operación markActiveLink del módulo de interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function markActiveLink() {

            const currentPath =
                getCurrentPath();

            let bestMatch = null;
            let bestLength = -1;

            navigationLinks.forEach(
                function (link) {

                    link.classList.remove(
                        "active"
                    );

                    const configuredPath =
                        normalizePath(
                            link.dataset.sidebarPath
                        );

                    const exactMatch =
                        currentPath
                        === configuredPath;

                    const childMatch =
                        configuredPath !== "/"
                        && currentPath.startsWith(
                            configuredPath + "/"
                        );

                    if (
                        (exactMatch || childMatch)
                        && configuredPath.length
                        > bestLength
                    ) {

                        bestLength =
                            configuredPath.length;

                        bestMatch =
                            link;
                    }

                }
            );

            if (!bestMatch) {
                return;
            }

            bestMatch.classList.add(
                "active"
            );

            const parentDetails =
                bestMatch.closest(
                    ".sidebar-details"
                );

            if (parentDetails) {
                parentDetails.open = true;
            }

            synchronizeTabletState();
        }

        sidebar.addEventListener(
            "click",
            function (event) {

                const link =
                    event.target.closest(
                        "a[href]"
                    );

                if (
                    link
                    && isMobile()
                ) {
                    closeSidebar();
                }

            }
        );

        if (btnOpen) {
            btnOpen.addEventListener(
                "click",
                openSidebar
            );
        }

        if (btnClose) {
            btnClose.addEventListener(
                "click",
                closeSidebar
            );
        }

        if (overlay) {
            overlay.addEventListener(
                "click",
                closeSidebar
            );
        }

        document.addEventListener(
            "keydown",
            function (event) {

                if (event.key !== "Escape") {
                    return;
                }

                if (isMobile()) {
                    closeSidebar();
                    return;
                }

                if (isTablet()) {
                    closeTabletMenus();
                }

            }
        );

        let resizeTimer = null;

        window.addEventListener(
            "resize",
            function () {

                clearTimeout(
                    resizeTimer
                );

                resizeTimer =
                    setTimeout(
                        function () {

                            if (!isMobile()) {
                                closeSidebar();
                            }

                            if (isDesktop()) {

                                sidebar.classList.remove(
                                    "tablet-expanded"
                                );

                            } else {

                                synchronizeTabletState();
                            }

                        },
                        120
                    );
            }
        );

        markActiveLink();
        synchronizeTabletState();
    }

    if (
        document.readyState
        === "loading"
    ) {

        document.addEventListener(
            "DOMContentLoaded",
            initSidebar
        );

    } else {

        initSidebar();
    }

})();
