/**
 * ==========================================================
 * MÓDULO: DASHBOARD
 * ==========================================================
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function () {
    "use strict";

    if (window.dashboardModuleInitialized) {
        return;
    }

    window.dashboardModuleInitialized = true;

    document.addEventListener("DOMContentLoaded", function () {

        /* ======================================================
           CONFIGURACIÓN
           ====================================================== */

        const VALID_PERIODS = new Set([
            "daily",
            "weekly",
            "monthly",
            "annual"
        ]);

        const chartCanvas =
            document.getElementById("dashboardMovementsChart");

        const chartLoading =
            document.getElementById("dashboardChartLoading");

        const chartEmpty =
            document.getElementById("dashboardChartEmpty");

        const initialChartData =
            document.getElementById("dashboardInitialChartData");

        const recentMovementsContainer =
            document.getElementById("dashboardRecentMovements");

        const mostMovedContainer =
            document.getElementById("dashboardMostMovedProducts");

        const leastMovedContainer =
            document.getElementById("dashboardLeastMovedProducts");

        const mostStockContainer =
            document.getElementById("dashboardMostStockProducts");

        const btnRefreshDashboard =
            document.getElementById("btnRefreshDashboard");

        const periodButtons = Array.from(
            document.querySelectorAll("[data-dashboard-period]")
        );

        /* ======================================================
           REPORTES
           ====================================================== */

        const reportModalElement =
            document.getElementById("modalReports");

        const btnOpenReports =
            document.getElementById("btnOpenReports");

        const btnGenerateReport =
            document.getElementById("btnGenerateReport");

        const reportForm =
            document.getElementById("reportForm");

        const reportType =
            document.getElementById("reportType");

        const reportPeriod =
            document.getElementById("reportPeriod");

        const customDateRange =
            document.getElementById("customDateRange");

        const reportStartDate =
            document.getElementById("reportStartDate");

        const reportEndDate =
            document.getElementById("reportEndDate");

        const modalReports =
            reportModalElement
            && typeof bootstrap !== "undefined"
            && bootstrap.Modal
                ? bootstrap.Modal.getOrCreateInstance(
                    reportModalElement
                )
                : null;

        /* ======================================================
           ESTADO
           ====================================================== */

        let selectedPeriod = normalizePeriod(
            document.body.dataset.selectedPeriod
        );

        let movementsChart = null;
        let dashboardRequestId = 0;
        let chartRequestId = 0;

        if (!window.Api) {
            console.error("api.js no está disponible.");
            return;
        }

        if (!chartCanvas || typeof Chart === "undefined") {
            console.error(
                "Chart.js o el canvas del Dashboard no están disponibles."
            );
            return;
        }

        initializeDashboard();

        /* ======================================================
           INICIALIZACIÓN
           ====================================================== */

        /**
         * Inicializa los eventos y el estado del módulo.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function initializeDashboard() {
            setActivePeriodButton(selectedPeriod);
            formatInitialDates();

            const movements =
                readInitialChartData();

            createOrUpdateChart(movements);

            await Promise.allSettled([
                loadDashboardData(),
                loadDashboardChart(selectedPeriod)
            ]);
        }

        /* ======================================================
           PERIODOS
           ====================================================== */

        periodButtons.forEach(function (button) {
            button.addEventListener("click", async function () {
                if (button.disabled) {
                    return;
                }

                const period = normalizePeriod(
                    button.dataset.dashboardPeriod
                );

                if (period === selectedPeriod) {
                    return;
                }

                selectedPeriod = period;
                setActivePeriodButton(period);

                try {
                    await loadDashboardChart(period);
                } catch (error) {
                    handleRequestError(error);
                }
            });
        });

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} period valor de period requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setActivePeriodButton(period) {
            selectedPeriod = normalizePeriod(period);

            periodButtons.forEach(function (button) {
                const buttonPeriod = normalizePeriod(
                    button.dataset.dashboardPeriod
                );

                const active =
                    buttonPeriod === selectedPeriod;

                button.classList.toggle(
                    "is-active",
                    active
                );

                button.setAttribute(
                    "aria-pressed",
                    String(active)
                );
            });

            document.body.dataset.selectedPeriod =
                selectedPeriod;
        }

        /* ======================================================
           ACTUALIZAR DASHBOARD
           ====================================================== */

        btnRefreshDashboard?.addEventListener(
            "click",
            async function () {
                if (btnRefreshDashboard.disabled) {
                    return;
                }

                setRefreshLoading(true);

                try {
                    const results =
                        await Promise.allSettled([
                            loadDashboardData(),
                            loadDashboardChart(selectedPeriod)
                        ]);

                    const failedResult =
                        results.find(
                            result =>
                                result.status === "rejected"
                        );

                    if (failedResult) {
                        throw failedResult.reason;
                    }

                    showSuccess(
                        "El Dashboard se actualizó correctamente."
                    );

                } catch (error) {
                    handleRequestError(error);

                } finally {
                    setRefreshLoading(false);
                }
            }
        );

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} loading valor de loading requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setRefreshLoading(loading) {
            if (!btnRefreshDashboard) {
                return;
            }

            btnRefreshDashboard.disabled = loading;

            const icon =
                btnRefreshDashboard.querySelector("i");

            const text =
                btnRefreshDashboard.querySelector("span");

            if (icon) {
                icon.className = loading
                    ? "bi bi-arrow-clockwise dashboard-spin"
                    : "bi bi-arrow-clockwise";
            }

            if (text) {
                text.textContent = loading
                    ? "Actualizando..."
                    : "Actualizar";
            }
        }

        /* ======================================================
           DATOS GENERALES
           ====================================================== */

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadDashboardData() {
            dashboardRequestId++;

            const currentRequestId =
                dashboardRequestId;

            const result =
                await Api.get("/dashboard/data");

            if (
                currentRequestId
                !== dashboardRequestId
            ) {
                return;
            }

            if (!result.success) {
                throw createApiError(
                    result,
                    "No fue posible cargar la información del Dashboard."
                );
            }

            const data =
                result.data || {};

            renderRecentMovements(
                Array.isArray(data.recentMovements)
                    ? data.recentMovements
                    : []
            );

            renderProducts(
                mostMovedContainer,
                Array.isArray(data.mostMovedProducts)
                    ? data.mostMovedProducts
                    : [],
                {
                    type: "movement",
                    emptyIcon: "bi-graph-up",
                    emptyMessage:
                        "Aún no hay productos con movimientos."
                }
            );

            renderProducts(
                leastMovedContainer,
                Array.isArray(data.leastMovedProducts)
                    ? data.leastMovedProducts
                    : [],
                {
                    type: "movement",
                    emptyIcon: "bi-graph-down",
                    emptyMessage:
                        "Aún no hay productos con movimientos."
                }
            );

            renderProducts(
                mostStockContainer,
                Array.isArray(data.productsWithMostStock)
                    ? data.productsWithMostStock
                    : [],
                {
                    type: "stock",
                    emptyIcon: "bi-box-seam",
                    emptyMessage:
                        "No hay productos con existencia."
                }
            );
        }

        /* ======================================================
           GRÁFICA
           ====================================================== */

        /**
         * Carga la información requerida desde el servidor.
         *
         * @param {*} period valor de period requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadDashboardChart(period) {
            chartRequestId++;

            const currentRequestId =
                chartRequestId;

            const normalizedPeriod =
                normalizePeriod(period);

            setChartLoading(true);

            try {
                const result = await Api.get(
                    "/dashboard/chart",
                    {
                        period: normalizedPeriod
                    }
                );

                if (
                    currentRequestId
                    !== chartRequestId
                ) {
                    return;
                }

                if (!result.success) {
                    throw createApiError(
                        result,
                        "No fue posible cargar la gráfica del Dashboard."
                    );
                }

                selectedPeriod = normalizePeriod(
                    result.data?.period
                    || normalizedPeriod
                );

                setActivePeriodButton(
                    selectedPeriod
                );

                const movements =
                    Array.isArray(
                        result.data?.movements
                    )
                        ? result.data.movements
                        : [];

                createOrUpdateChart(movements);

            } catch (error) {
                if (
                    currentRequestId
                    !== chartRequestId
                ) {
                    return;
                }

                setChartEmpty(true);
                throw error;

            } finally {
                if (
                    currentRequestId
                    === chartRequestId
                ) {
                    setChartLoading(false);
                }
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} movements valor de movements requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createOrUpdateChart(movements) {
            const safeMovements =
                Array.isArray(movements)
                    ? movements
                    : [];

            const hasValues =
                safeMovements.some(function (movement) {
                    return normalizeNumber(
                            movement.entryQuantity
                        ) > 0
                        || normalizeNumber(
                            movement.exitQuantity
                        ) > 0;
                });

            setChartEmpty(
                safeMovements.length === 0
                || !hasValues
            );

            const labels =
                safeMovements.map(function (movement) {
                    return normalizeText(
                        movement.periodLabel
                    ) || normalizeText(
                        movement.periodKey
                    );
                });

            const entries =
                safeMovements.map(function (movement) {
                    return normalizeNumber(
                        movement.entryQuantity
                    );
                });

            const exits =
                safeMovements.map(function (movement) {
                    return normalizeNumber(
                        movement.exitQuantity
                    );
                });

            const chartData = {
                labels,
                datasets: [
                    {
                        label: "Entradas",
                        data: entries,
                        borderColor: "#55b8ff",
                        backgroundColor:
                            "rgba(85,184,255,.16)",
                        pointBackgroundColor: "#55b8ff",
                        pointBorderColor: "#55b8ff",
                        pointRadius: 4,
                        pointHoverRadius: 6,
                        borderWidth: 3,
                        tension: 0.35,
                        fill: true
                    },
                    {
                        label: "Salidas",
                        data: exits,
                        borderColor: "#ff6666",
                        backgroundColor:
                            "rgba(255,102,102,.12)",
                        pointBackgroundColor: "#ff6666",
                        pointBorderColor: "#ff6666",
                        pointRadius: 4,
                        pointHoverRadius: 6,
                        borderWidth: 3,
                        tension: 0.35,
                        fill: true
                    }
                ]
            };

            if (movementsChart) {
                movementsChart.data = chartData;

                movementsChart.options.scales.x
                    .ticks.maxRotation =
                    getLabelRotation();

                movementsChart.options.scales.x
                    .ticks.minRotation =
                    getLabelRotation();

                movementsChart.update();
                return;
            }

            movementsChart = new Chart(
                chartCanvas,
                {
                    type: "line",
                    data: chartData,
                    options: createChartOptions()
                }
            );
        }

        /**
         * Valida y envía la información capturada por el usuario.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createChartOptions() {
            const textColor =
                getCssVariable(
                    "--text-color",
                    "#2d3748"
                );

            const mutedColor =
                getCssVariable(
                    "--text-muted",
                    "#718096"
                );

            return {
                responsive: true,
                maintainAspectRatio: false,

                interaction: {
                    mode: "index",
                    intersect: false
                },

                animation: {
                    duration: 500
                },

                plugins: {
                    legend: {
                        position: "top",
                        align: "end",

                        labels: {
                            color: textColor,
                            usePointStyle: true,
                            pointStyle: "circle",
                            boxWidth: 9,
                            boxHeight: 9,
                            padding: 18,

                            font: {
                                size: 12,
                                weight: "600"
                            }
                        }
                    },

                    tooltip: {
                        padding: 12,
                        cornerRadius: 12,

                        callbacks: {
                            label: function (context) {
                                return `${
                                    context.dataset.label
                                }: ${
                                    formatNumber(
                                        context.parsed.y
                                    )
                                } unidades`;
                            }
                        }
                    }
                },

                scales: {
                    x: {
                        grid: {
                            display: false
                        },

                        border: {
                            display: false
                        },

                        ticks: {
                            color: mutedColor,
                            autoSkip: false,
                            maxRotation:
                                getLabelRotation(),
                            minRotation:
                                getLabelRotation(),

                            font: {
                                size: 11
                            }
                        }
                    },

                    y: {
                        beginAtZero: true,
                        suggestedMax: 5,

                        grid: {
                            color: getGridColor(),
                            drawTicks: false
                        },

                        border: {
                            display: false
                        },

                        ticks: {
                            color: mutedColor,
                            precision: 0,
                            padding: 10,

                            callback: function (value) {
                                return formatCompactNumber(
                                    value
                                );
                            }
                        },

                        title: {
                            display: true,
                            text: "Unidades movidas",
                            color: mutedColor,

                            font: {
                                size: 11,
                                weight: "600"
                            }
                        }
                    }
                }
            };
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getLabelRotation() {
            return selectedPeriod === "weekly"
            || selectedPeriod === "monthly"
                ? 25
                : 0;
        }

        /**
         * Ejecuta la operación readInitialChartData del módulo de interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function readInitialChartData() {
            if (!initialChartData) {
                return [];
            }

            return Array.from(
                initialChartData.querySelectorAll(
                    ".dashboard-chart-data"
                )
            ).map(function (element) {
                return {
                    periodKey:
                        normalizeText(
                            element.dataset.periodKey
                        ),

                    periodLabel:
                        normalizeText(
                            element.dataset.periodLabel
                        ),

                    entryQuantity:
                        normalizeNumber(
                            element.dataset.entryQuantity
                        ),

                    exitQuantity:
                        normalizeNumber(
                            element.dataset.exitQuantity
                        )
                };
            });
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} loading valor de loading requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setChartLoading(loading) {
            chartLoading?.classList.toggle(
                "is-visible",
                loading
            );

            periodButtons.forEach(function (button) {
                button.disabled = loading;
            });
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} empty valor de empty requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setChartEmpty(empty) {
            chartEmpty?.classList.toggle(
                "is-visible",
                empty
            );

            chartCanvas.style.visibility =
                empty ? "hidden" : "visible";
        }

        /* ======================================================
           MOVIMIENTOS RECIENTES
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} movements valor de movements requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderRecentMovements(movements) {
            if (!recentMovementsContainer) {
                return;
            }

            recentMovementsContainer.replaceChildren();

            if (movements.length === 0) {
                recentMovementsContainer.appendChild(
                    createEmptyState(
                        "bi-clock-history",
                        "No hay movimientos recientes."
                    )
                );

                return;
            }

            movements.forEach(function (movement) {
                recentMovementsContainer.appendChild(
                    createMovementItem(movement)
                );
            });
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} movement valor de movement requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createMovementItem(movement) {
            const isEntry =
                normalizeText(
                    movement.movementType
                ).toUpperCase() === "ENTRY";

            const article =
                document.createElement("article");

            article.className =
                "dashboard-movement-item";

            const iconContainer =
                document.createElement("div");

            iconContainer.className =
                `dashboard-movement-icon ${
                    isEntry
                        ? "is-entry"
                        : "is-exit"
                }`;

            const icon =
                document.createElement("i");

            icon.className = isEntry
                ? "bi bi-box-arrow-in-down"
                : "bi bi-box-arrow-up";

            iconContainer.appendChild(icon);

            const information =
                document.createElement("div");

            information.className =
                "dashboard-movement-information";

            const title =
                document.createElement("div");

            title.className =
                "dashboard-movement-title";

            const typeBadge =
                document.createElement("span");

            typeBadge.className =
                `dashboard-movement-type ${
                    isEntry
                        ? "is-entry"
                        : "is-exit"
                }`;

            typeBadge.textContent =
                isEntry ? "Entrada" : "Salida";

            const folio =
                document.createElement("span");

            folio.textContent =
                normalizeText(
                    movement.folioNumber
                ) || "Sin folio";

            title.append(
                typeBadge,
                folio
            );

            const destination =
                document.createElement("span");

            destination.className =
                "dashboard-movement-destination";

            destination.textContent =
                `${
                    isEntry
                        ? "Proveedor"
                        : "Área"
                }: ${
                    normalizeText(
                        movement.destinationName
                    ) || "No disponible"
                }`;

            const responsible =
                document.createElement("span");

            responsible.className =
                "dashboard-movement-responsible";

            responsible.textContent =
                `Registró: ${
                    normalizeText(
                        movement.responsibleName
                    ) || "Usuario no disponible"
                }`;

            information.append(
                title,
                destination,
                responsible
            );

            const values =
                document.createElement("div");

            values.className =
                "dashboard-movement-values";

            const quantity =
                document.createElement("strong");

            quantity.className =
                "dashboard-movement-quantity";

            quantity.textContent =
                `${formatNumber(
                    movement.totalQuantity
                )} unidades`;

            const date =
                document.createElement("span");

            date.className =
                "dashboard-movement-date";

            date.dataset.dashboardDate =
                normalizeText(
                    movement.changeDate
                );

            date.textContent =
                formatRelativeDate(
                    movement.changeDate
                );

            values.append(
                quantity,
                date
            );

            article.append(
                iconContainer,
                information,
                values
            );

            return article;
        }

        /* ======================================================
           PRODUCTOS
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} container valor de container requerido por la función
         * @param {*} products valor de products requerido por la función
         * @param {*} options valor de options requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderProducts(
            container,
            products,
            options
        ) {
            if (!container) {
                return;
            }

            container.replaceChildren();

            if (products.length === 0) {
                container.appendChild(
                    createEmptyState(
                        options.emptyIcon,
                        options.emptyMessage
                    )
                );

                return;
            }

            products.forEach(function (
                product,
                index
            ) {
                container.appendChild(
                    createProductItem(
                        product,
                        index + 1,
                        options.type
                    )
                );
            });
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} product valor de product requerido por la función
         * @param {*} position valor de position requerido por la función
         * @param {*} type valor de type requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createProductItem(
            product,
            position,
            type
        ) {
            const article =
                document.createElement("article");

            article.className =
                "dashboard-product-item";

            const positionElement =
                document.createElement("div");

            positionElement.className =
                "dashboard-product-position";

            positionElement.textContent =
                String(position);

            const information =
                document.createElement("div");

            information.className =
                "dashboard-product-information";

            const name =
                document.createElement("span");

            name.className =
                "dashboard-product-name";

            name.textContent =
                normalizeText(
                    product.productName
                ) || "Producto sin nombre";

            const code =
                document.createElement("span");

            code.className =
                "dashboard-product-code";

            code.textContent =
                normalizeText(
                    product.productCode
                ) || "Sin clave";

            information.appendChild(name);

            const meta =
                document.createElement("div");

            meta.className =
                "dashboard-product-meta";

            meta.appendChild(code);

            if (
                type === "stock"
                && Number(product.productStatus) !== 1
            ) {
                const status =
                    document.createElement("span");

                status.className =
                    "dashboard-product-status is-inactive";

                const icon =
                    document.createElement("i");

                icon.className =
                    "bi bi-exclamation-circle";

                const text =
                    document.createElement("span");

                text.textContent =
                    "Inactivo";

                status.append(
                    icon,
                    text
                );

                meta.appendChild(status);
            }

            information.appendChild(meta);

            if (type === "movement") {
                const breakdown =
                    document.createElement("span");

                breakdown.className =
                    "dashboard-product-breakdown";

                breakdown.textContent =
                    `Entradas: ${
                        formatNumber(
                            product.entryQuantity
                        )
                    } · Salidas: ${
                        formatNumber(
                            product.exitQuantity
                        )
                    }`;

                information.appendChild(
                    breakdown
                );
            }

            const value =
                document.createElement("div");

            value.className =
                "dashboard-product-value";

            const quantity =
                document.createElement("strong");

            quantity.className =
                "dashboard-product-quantity";

            quantity.textContent =
                formatNumber(
                    type === "stock"
                        ? product.stockQuantity
                        : product.totalMovement
                );

            const unit =
                document.createElement("span");

            unit.className =
                "dashboard-product-unit";

            unit.textContent =
                normalizeText(
                    product.metricShortName
                ) || "unidades";

            value.append(
                quantity,
                unit
            );

            article.append(
                positionElement,
                information,
                value
            );

            return article;
        }

        /* ======================================================
           REPORTES
           ====================================================== */

        btnOpenReports?.addEventListener(
            "click",
            function () {
                if (!modalReports) {
                    return;
                }

                if (
                    reportPeriod
                    && reportPeriod.value !== "custom"
                ) {
                    reportPeriod.value =
                        selectedPeriod;
                }

                updateCustomDateRange();
                clearDateValidation();

                btnOpenReports.classList.add(
                    "is-active"
                );

                modalReports.show();
            }
        );

        reportPeriod?.addEventListener(
            "change",
            function () {
                updateCustomDateRange();
                clearDateValidation();
            }
        );

        reportStartDate?.addEventListener(
            "input",
            clearDateValidation
        );

        reportStartDate?.addEventListener(
            "change",
            clearDateValidation
        );

        reportEndDate?.addEventListener(
            "input",
            clearDateValidation
        );

        reportEndDate?.addEventListener(
            "change",
            clearDateValidation
        );

        btnGenerateReport?.addEventListener(
            "click",
            generateReport
        );

        reportForm?.addEventListener(
            "submit",
            function (event) {
                event.preventDefault();
                generateReport();
            }
        );

        reportModalElement?.addEventListener(
            "hidden.bs.modal",
            function () {
                btnOpenReports?.classList.remove(
                    "is-active"
                );

                setReportButtonLoading(false);
                clearDateValidation();
            }
        );

        /**
         * Ejecuta la operación generateReport del módulo de interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function generateReport(){
            if(
                !btnGenerateReport
                ||btnGenerateReport.disabled
            ){
                return;
            }

            if(!validateReportDates()){
                return;
            }

            const selectedFormat=
                document.querySelector(
                    'input[name="reportFormat"]:checked'
                );

            const format=
                selectedFormat?.value||"pdf";

            const type=
                reportType?.value||"movements";

            const period=
                reportPeriod?.value||"monthly";

            const contextPath=
                window.dashboardConfig?.contextPath
                ||document.body.dataset.contextPath
                ||Api.getContextPath()
                ||"";

            const parameters=
                new URLSearchParams({
                    type,
                    period
                });

            if(period==="custom"){
                parameters.set(
                    "startDate",
                    reportStartDate.value
                );

                parameters.set(
                    "endDate",
                    reportEndDate.value
                );
            }

            const url=
                `${contextPath}/report/${encodeURIComponent(format)}?${parameters.toString()}`;

            setReportButtonLoading(true);

            try{
                const response=await fetch(
                    url,
                    {
                        method:"GET",
                        credentials:"same-origin",
                        cache:"no-store",
                        headers:{
                            "Accept":
                                format==="pdf"
                                    ?"application/pdf"
                                    :"application/xml",
                            "X-Requested-With":
                                "XMLHttpRequest"
                        }
                    }
                );

                if(response.status===401){
                    window.location.href=
                        `${contextPath}/login`;

                    return;
                }

                if(!response.ok){
                    let message=
                        "No fue posible generar el reporte.";

                    const contentType=
                        response.headers.get(
                            "content-type"
                        )||"";

                    if(
                        contentType
                            .toLowerCase()
                            .includes("application/json")
                    ){
                        try{
                            const result=
                                await response.json();

                            if(
                                result?.message
                                &&String(result.message).trim()
                            ){
                                message=
                                    String(
                                        result.message
                                    ).trim();
                            }
                        }catch(error){
                            console.error(
                                "No fue posible leer el error del reporte.",
                                error
                            );
                        }
                    }

                    throw new Error(message);
                }

                const blob=
                    await response.blob();

                if(!blob||blob.size===0){
                    throw new Error(
                        "El servidor generó un archivo vacío."
                    );
                }

                const fileName=
                    getReportFileName(
                        response,
                        format
                    );

                downloadReportBlob(
                    blob,
                    fileName
                );

                modalReports?.hide();

                showSuccess(
                    "El reporte se generó correctamente."
                );

            }catch(error){
                console.error(
                    "Error al generar el reporte:",
                    error
                );

                const message=
                    error?.message
                    ||"No fue posible generar el reporte.";

                if(
                    window.AppToast
                    &&typeof AppToast.error==="function"
                ){
                    AppToast.error(message);
                }else{
                    window.alert(message);
                }

            }finally{
                setReportButtonLoading(false);
            }
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         *
         * @param {*} response valor de response requerido por la función
         * @param {*} format valor de format requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getReportFileName(
            response,
            format
        ){
            const disposition=
                response.headers.get(
                    "Content-Disposition"
                )||"";

            const utf8Match=
                disposition.match(
                    /filename\*=UTF-8''([^;]+)/i
                );

            if(utf8Match?.[1]){
                try{
                    return decodeURIComponent(
                        utf8Match[1].trim()
                    );
                }catch(error){
                    console.warn(
                        "No fue posible decodificar el nombre del archivo.",
                        error
                    );
                }
            }

            const normalMatch=
                disposition.match(
                    /filename="?([^";]+)"?/i
                );

            if(normalMatch?.[1]){
                return normalMatch[1].trim();
            }

            return `reporte.${format}`;
        }

        /**
         * Ejecuta la operación downloadReportBlob del módulo de interfaz.
         *
         * @param {*} blob valor de blob requerido por la función
         * @param {*} fileName valor de fileName requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function downloadReportBlob(
            blob,
            fileName
        ){
            const objectUrl=
                URL.createObjectURL(blob);

            const link=
                document.createElement("a");

            link.href=objectUrl;
            link.download=
                fileName||"reporte";

            link.style.display=
                "none";

            document.body.appendChild(
                link
            );

            link.click();

            link.remove();

            window.setTimeout(
                function(){
                    URL.revokeObjectURL(
                        objectUrl
                    );
                },
                1000
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateCustomDateRange() {
            if (!customDateRange) {
                return;
            }

            const custom =
                reportPeriod?.value === "custom";

            customDateRange.hidden = !custom;

            if (reportStartDate) {
                reportStartDate.required =
                    custom;
            }

            if (reportEndDate) {
                reportEndDate.required =
                    custom;
            }

            if (!custom) {
                clearDateValidation();
            }
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function validateReportDates() {
            if (
                reportPeriod?.value !== "custom"
            ) {
                return true;
            }

            clearDateValidation();

            const startValue =
                reportStartDate?.value || "";

            const endValue =
                reportEndDate?.value || "";

            let valid = true;

            if (!startValue) {
                markDateInvalid(
                    reportStartDate,
                    "La fecha inicial es obligatoria."
                );

                valid = false;
            }

            if (!endValue) {
                markDateInvalid(
                    reportEndDate,
                    "La fecha final es obligatoria."
                );

                valid = false;
            }

            if (!valid) {
                return false;
            }

            const startDate =
                new Date(
                    `${startValue}T00:00:00`
                );

            const endDate =
                new Date(
                    `${endValue}T00:00:00`
                );

            const today =
                new Date();

            today.setHours(0, 0, 0, 0);

            if (
                Number.isNaN(
                    startDate.getTime()
                )
            ) {
                markDateInvalid(
                    reportStartDate,
                    "La fecha inicial no es válida."
                );

                return false;
            }

            if (
                Number.isNaN(
                    endDate.getTime()
                )
            ) {
                markDateInvalid(
                    reportEndDate,
                    "La fecha final no es válida."
                );

                return false;
            }

            if (endDate < startDate) {
                markDateInvalid(
                    reportEndDate,
                    "La fecha final no puede ser anterior a la inicial."
                );

                return false;
            }

            if (startDate > today) {
                markDateInvalid(
                    reportStartDate,
                    "No puedes seleccionar una fecha futura."
                );

                return false;
            }

            if (endDate > today) {
                markDateInvalid(
                    reportEndDate,
                    "No puedes seleccionar una fecha futura."
                );

                return false;
            }

            return true;
        }

        /**
         * Ejecuta la operación markDateInvalid del módulo de interfaz.
         *
         * @param {*} input valor de input requerido por la función
         * @param {*} message valor de message requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function markDateInvalid(
            input,
            message
        ) {
            if (!input) {
                return;
            }

            input.classList.remove(
                "is-valid"
            );

            input.classList.add(
                "is-invalid"
            );

            const feedback =
                input.closest(".form-field")
                    ?.querySelector(
                        ".invalid-feedback"
                    );

            if (feedback) {
                feedback.textContent =
                    message;
            }

            input.focus();
        }

        /**
         * Retira o limpia la información indicada de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function clearDateValidation() {
            [
                reportStartDate,
                reportEndDate
            ].forEach(function (input) {
                input?.classList.remove(
                    "is-invalid",
                    "is-valid"
                );
            });
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} loading valor de loading requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setReportButtonLoading(
            loading
        ) {
            if (!btnGenerateReport) {
                return;
            }

            btnGenerateReport.disabled =
                loading;

            if (loading) {
                if (
                    !btnGenerateReport
                        .dataset.originalHtml
                ) {
                    btnGenerateReport
                        .dataset.originalHtml =
                        btnGenerateReport.innerHTML;
                }

                btnGenerateReport.innerHTML = `
                    <span class="spinner-border spinner-border-sm me-2"
                          aria-hidden="true">
                    </span>
                    Generando...
                `;

                return;
            }

            btnGenerateReport.innerHTML =
                btnGenerateReport
                    .dataset.originalHtml
                || `
                    <i class="bi bi-download me-1"></i>
                    Generar
                `;
        }

        /* ======================================================
           FECHAS
           ====================================================== */

        /**
         * Convierte el valor al formato utilizado para su presentación.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatInitialDates() {
            document.querySelectorAll(
                "[data-dashboard-date]"
            ).forEach(function (element) {
                element.textContent =
                    formatRelativeDate(
                        element.dataset.dashboardDate
                    );
            });
        }

        /**
         * Convierte el valor al formato utilizado para su presentación.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatRelativeDate(value) {
            const date =
                parseDate(value);

            if (!date) {
                return "Fecha no disponible";
            }

            const now =
                new Date();

            const difference =
                now.getTime() - date.getTime();

            const future =
                difference < 0;

            const absoluteDifference =
                Math.abs(difference);

            const minutes =
                Math.floor(
                    absoluteDifference / 60_000
                );

            const hours =
                Math.floor(
                    absoluteDifference / 3_600_000
                );

            const days =
                Math.floor(
                    absoluteDifference / 86_400_000
                );

            if (minutes < 1) {
                return future
                    ? "En unos segundos"
                    : "Hace unos segundos";
            }

            if (minutes < 60) {
                return future
                    ? `En ${minutes} min`
                    : `Hace ${minutes} min`;
            }

            if (hours < 24) {
                return future
                    ? `En ${hours} h`
                    : `Hace ${hours} h`;
            }

            if (days === 1) {
                return future
                    ? "Mañana"
                    : "Ayer";
            }

            if (days < 7) {
                return future
                    ? `En ${days} días`
                    : `Hace ${days} días`;
            }

            return new Intl.DateTimeFormat(
                "es-MX",
                {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit"
                }
            ).format(date);
        }

        /**
         * Ejecuta la operación parseDate del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function parseDate(value) {
            if (!value) {
                return null;
            }

            const date =
                new Date(value);

            return Number.isNaN(
                date.getTime()
            )
                ? null
                : date;
        }

        /* ======================================================
           CAMBIO DE TEMA
           ====================================================== */

        document.addEventListener(
            "themeChanged",
            updateChartTheme
        );

        const themeObserver =
            new MutationObserver(
                function (mutations) {
                    const changed =
                        mutations.some(
                            function (mutation) {
                                return mutation.type
                                    === "attributes"
                                    && mutation.attributeName
                                    === "data-theme";
                            }
                        );

                    if (changed) {
                        updateChartTheme();
                    }
                }
            );

        themeObserver.observe(
            document.documentElement,
            {
                attributes: true,
                attributeFilter: [
                    "data-theme"
                ]
            }
        );

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateChartTheme() {
            if (!movementsChart) {
                return;
            }

            const textColor =
                getCssVariable(
                    "--text-color",
                    "#2d3748"
                );

            const mutedColor =
                getCssVariable(
                    "--text-muted",
                    "#718096"
                );

            movementsChart.options.plugins
                .legend.labels.color =
                textColor;

            movementsChart.options.scales.x
                .ticks.color =
                mutedColor;

            movementsChart.options.scales.y
                .ticks.color =
                mutedColor;

            movementsChart.options.scales.y
                .title.color =
                mutedColor;

            movementsChart.options.scales.y
                .grid.color =
                getGridColor();

            movementsChart.update();
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getGridColor() {
            const dark =
                document.documentElement
                    .getAttribute(
                        "data-theme"
                    ) === "dark";

            return dark
                ? "rgba(255,255,255,.08)"
                : "rgba(45,55,72,.08)";
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         *
         * @param {*} variableName valor de variableName requerido por la función
         * @param {*} fallback valor de fallback requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getCssVariable(
            variableName,
            fallback
        ) {
            const value =
                getComputedStyle(
                    document.documentElement
                )
                    .getPropertyValue(
                        variableName
                    )
                    .trim();

            return value || fallback;
        }

        /* ======================================================
           ESTADOS VACÍOS
           ====================================================== */

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} iconClass valor de iconClass requerido por la función
         * @param {*} message valor de message requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createEmptyState(
            iconClass,
            message
        ) {
            const container =
                document.createElement("div");

            container.className =
                "dashboard-empty-state";

            const icon =
                document.createElement("i");

            icon.className =
                `bi ${iconClass}`;

            const text =
                document.createElement("span");

            text.textContent =
                message;

            container.append(
                icon,
                text
            );

            return container;
        }

        /* ======================================================
           MENSAJES Y ERRORES
           ====================================================== */

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} message valor de message requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function showSuccess(message) {
            if (
                window.AppToast
                && typeof AppToast.success
                === "function"
            ) {
                AppToast.success(message);
                return;
            }

            if (
                window.AppToast
                && typeof AppToast.show
                === "function"
            ) {
                AppToast.show(
                    message,
                    "success"
                );

                return;
            }

            console.log(message);
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
        function handleRequestError(error) {
            console.error(error);

            if (Number(error?.status) === 401) {
                window.location.href =
                    `${Api.getContextPath()}/login`;

                return;
            }

            if (
                error?.data
                && typeof error.data
                === "object"
            ) {
                showResponseMessage(
                    error.data
                );

                return;
            }

            const message =
                error?.message
                || "No fue posible actualizar el Dashboard.";

            if (
                window.AppToast
                && typeof AppToast.error
                === "function"
            ) {
                AppToast.error(message);
                return;
            }

            window.alert(message);
        }

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
        function showResponseMessage(result) {
            if (
                window.AppToast
                && typeof AppToast.fromResponse
                === "function"
            ) {
                AppToast.fromResponse(result);
                return;
            }

            const message =
                result.message
                || "No fue posible completar la operación.";

            const type =
                result.type || "error";

            if (
                window.AppToast
                && typeof AppToast.show
                === "function"
            ) {
                AppToast.show(
                    message,
                    type
                );

                return;
            }

            window.alert(message);
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} result valor de result requerido por la función
         * @param {*} fallbackMessage valor de fallbackMessage requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createApiError(
            result,
            fallbackMessage
        ) {
            const message =
                result?.message
                || fallbackMessage;

            if (window.Api?.ApiError) {
                return new Api.ApiError(
                    message,
                    400,
                    result
                );
            }

            const error =
                new Error(message);

            error.status = 400;
            error.data = result;

            return error;
        }

        /* ======================================================
           AUXILIARES
           ====================================================== */

        /**
         * Ejecuta la operación normalizePeriod del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizePeriod(value) {
            const period =
                normalizeText(value)
                    .toLowerCase();

            return VALID_PERIODS.has(period)
                ? period
                : "monthly";
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
            return value == null
                ? ""
                : String(value).trim();
        }

        /**
         * Ejecuta la operación normalizeNumber del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeNumber(value) {
            const normalized =
                normalizeText(value)
                    .replace(",", ".");

            const number =
                Number(normalized);

            return Number.isFinite(number)
                ? number
                : 0;
        }

        /**
         * Convierte el valor al formato utilizado para su presentación.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatNumber(value) {
            return new Intl.NumberFormat(
                "es-MX",
                {
                    maximumFractionDigits: 0
                }
            ).format(
                normalizeNumber(value)
            );
        }

        /**
         * Convierte el valor al formato utilizado para su presentación.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatCompactNumber(value) {
            return new Intl.NumberFormat(
                "es-MX",
                {
                    notation: "compact",
                    maximumFractionDigits: 1
                }
            ).format(
                normalizeNumber(value)
            );
        }
    });
})();
