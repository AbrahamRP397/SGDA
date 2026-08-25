/**
 * ==========================================================
 * MÓDULO: EXISTENCIAS
 * ==========================================================
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function () {
    "use strict";

    if (window.stockModuleInitialized) {
        return;
    }

    window.stockModuleInitialized = true;

    document.addEventListener("DOMContentLoaded", function () {
        const TABLE_ID = "stockTable";

        const table = document.getElementById(TABLE_ID);
        const tableBody = table?.querySelector("tbody");

        const btnRefreshStock =
            document.getElementById("btnRefreshStock");

        const stockSummaryProducts =
            document.getElementById("stockSummaryProducts");

        const stockSummaryUnits =
            document.getElementById("stockSummaryUnits");

        const stockSummaryLow =
            document.getElementById("stockSummaryLow");

        const stockSummaryOut =
            document.getElementById("stockSummaryOut");

        const modalViewStock =
            getModal("modalViewStock");

        const lowStockLimit =
            normalizePositiveInteger(
                document.body.dataset.lowStockLimit
            ) || 10;

        if (!table || !tableBody) {
            console.warn(
                "No se encontró la tabla de existencias."
            );
            return;
        }

        if (!window.Api) {
            console.error(
                "api.js no está disponible."
            );
            return;
        }

        formatInitialValues();

        /* ======================================================
           ACTUALIZAR EXISTENCIAS
           ====================================================== */

        btnRefreshStock?.addEventListener(
            "click",
            async function () {
                if (btnRefreshStock.disabled) {
                    return;
                }

                setRefreshLoading(true);

                try {
                    await Promise.all([
                        loadStock(),
                        loadStockSummary()
                    ]);

                    showSuccess(
                        "Las existencias se actualizaron correctamente."
                    );

                } catch (error) {
                    handleRequestError(error);

                } finally {
                    setRefreshLoading(false);
                }
            }
        );

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadStock() {
            const result =
                await Api.get("/stock/list");

            if (!result.success) {
                throw createApiError(
                    result,
                    "No fue posible consultar las existencias."
                );
            }

            renderStock(
                Array.isArray(result.data)
                    ? result.data
                    : []
            );
        }

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadStockSummary() {
            const result =
                await Api.get("/stock/summary");

            if (!result.success) {
                throw createApiError(
                    result,
                    "No fue posible consultar el resumen de existencias."
                );
            }

            renderSummary(
                result.data || {}
            );
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
        function setRefreshLoading(loading) {
            if (!btnRefreshStock) {
                return;
            }

            btnRefreshStock.disabled = loading;

            const icon =
                btnRefreshStock.querySelector("i");

            const text =
                btnRefreshStock.querySelector("span");

            if (icon) {
                icon.className = loading
                    ? "bi bi-arrow-clockwise spin"
                    : "bi bi-arrow-clockwise";
            }

            if (text) {
                text.textContent = loading
                    ? "Actualizando..."
                    : "Actualizar";
            }
        }

        /* ======================================================
           TARJETAS DE RESUMEN
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} summary valor de summary requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderSummary(summary) {
            setElementText(
                stockSummaryProducts,
                formatNumber(
                    summary.totalProducts
                )
            );

            setElementText(
                stockSummaryUnits,
                formatNumber(
                    summary.totalUnits
                )
            );

            setElementText(
                stockSummaryLow,
                formatNumber(
                    summary.lowStockProducts
                )
            );

            setElementText(
                stockSummaryOut,
                formatNumber(
                    summary.outOfStockProducts
                )
            );
        }

        /* ======================================================
           RECONSTRUIR TABLA
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} stockList valor de stockList requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderStock(stockList) {
            tableBody.replaceChildren();

            stockList.forEach(function (stock) {
                tableBody.appendChild(
                    createStockRow(stock)
                );
            });

            updateTableVisibility(
                stockList.length
            );

            rebuildFilterOptions(stockList);

            if (
                typeof window.filterTable
                === "function"
            ) {
                window.filterTable(TABLE_ID);
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} stock valor de stock requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createStockRow(stock) {
            const idStock =
                normalizePositiveInteger(
                    stock.idStock
                ) || 0;

            const idProductProvider =
                normalizePositiveInteger(
                    stock.idProductProvider
                ) || 0;

            const idProduct =
                normalizePositiveInteger(
                    stock.idProduct
                ) || 0;

            const idProvider =
                normalizePositiveInteger(
                    stock.idProvider
                ) || 0;

            const idMetric =
                normalizePositiveInteger(
                    stock.idMetric
                ) || 0;

            const productCode =
                normalizeText(
                    stock.productCode
                );

            const productName =
                normalizeText(
                    stock.productName
                );

            const providerName =
                normalizeText(
                    stock.providerName
                );

            const providerRfc =
                normalizeText(
                    stock.providerRfc
                );

            const metricName =
                normalizeText(
                    stock.metricName
                );

            const metricShortName =
                normalizeText(
                    stock.metricShortName
                );

            const purchasePrice =
                normalizeNumber(
                    stock.purchasePrice
                );

            const quantity =
                normalizeInteger(
                    stock.quantity
                );

            const stockStatus =
                normalizeStockStatus(
                    stock.stockStatus,
                    quantity
                );

            const stockStatusLabel =
                normalizeText(
                    stock.stockStatusLabel
                ) || getStatusLabel(stockStatus);

            const stockPercentage =
                normalizePercentage(
                    stock.stockPercentage,
                    quantity
                );

            const productStatus=
                Number(stock.productStatus)===1?1:0;

            const providerStatus=
                Number(stock.providerStatus)===1?1:0;

            const metricStatus=
                Number(stock.metricStatus)===1?1:0;

            const relationStatus=
                Number(stock.relationStatus)===1?1:0;

            const row =
                document.createElement("tr");

            const administrativeStatusCell=
                createAdministrativeStatusCell({
                    productStatus,
                    providerStatus,
                    metricStatus,
                    relationStatus
                });

            row.className =
                "js-table-row stock-table-row";

            row.dataset.id =
                String(idStock);

            row.dataset.idProductProvider =
                String(idProductProvider);

            row.dataset.productId =
                String(idProduct);

            row.dataset.productCode =
                productCode;

            row.dataset.productName =
                productName;

            row.dataset.provider =
                String(idProvider);

            row.dataset.providerName =
                providerName;

            row.dataset.providerRfc =
                providerRfc;

            row.dataset.metric =
                String(idMetric);

            row.dataset.metricName =
                metricName;

            row.dataset.metricShortName =
                metricShortName;

            row.dataset.price =
                purchasePrice.toFixed(2);

            row.dataset.quantity =
                String(quantity);

            row.dataset.status =
                stockStatus;

            row.dataset.statusLabel =
                stockStatusLabel;

            row.dataset.percentage =
                String(stockPercentage);

            row.dataset.search = [
                productCode,
                productName,
                providerName,
                providerRfc,
                metricName,
                metricShortName
            ].join(" ");

            row.dataset.productStatus=
                String(productStatus);

            row.dataset.providerStatus=
                String(providerStatus);

            row.dataset.metricStatus=
                String(metricStatus);

            row.dataset.relationStatus=
                String(relationStatus);

            const idCell =
                createCell(
                    String(idStock),
                    "table-cell-secondary table-cell-nowrap"
                );

            const productCell =
                createProductCell(
                    productName,
                    productCode
                );

            const providerCell =
                createProviderCell(
                    providerName,
                    providerRfc
                );

            const metricCell =
                createMetricCell(
                    metricName,
                    metricShortName
                );

            const priceCell =
                createCell(
                    formatCurrency(
                        purchasePrice
                    ),
                    "stock-price stock-money-cell"
                );

            priceCell.dataset.moneyValue =
                purchasePrice.toFixed(2);

            const quantityCell =
                createQuantityCell({
                    quantity,
                    metricShortName,
                    stockStatus,
                    stockPercentage
                });

            const statusCell =
                createStatusCell(
                    stockStatus,
                    stockStatusLabel
                );

            const actionCell =
                createActionCell();

            row.append(
                idCell,
                productCell,
                providerCell,
                metricCell,
                priceCell,
                quantityCell,
                statusCell,
                administrativeStatusCell,
                actionCell
            );

            return row;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} data datos que serán procesados por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createAdministrativeStatusCell(data){
            const cell=document.createElement("td");
            const badge=document.createElement("span");

            let text="Operativo";
            let className="table-badge table-badge-success";

            if(data.productStatus!==1){
                text="Producto inactivo";
                className="table-badge table-badge-danger";

            }else if(data.providerStatus!==1){
                text="Proveedor inactivo";
                className="table-badge table-badge-warning";

            }else if(data.metricStatus!==1){
                text="Unidad inactiva";
                className="table-badge table-badge-warning";

            }else if(data.relationStatus!==1){
                text="Relación inactiva";
                className="table-badge table-badge-warning";
            }

            badge.className=className;
            badge.textContent=text;

            cell.appendChild(badge);

            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} productName valor de productName requerido por la función
         * @param {*} productCode valor de productCode requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createProductCell(
            productName,
            productCode
        ) {
            const cell =
                document.createElement("td");

            cell.className =
                "stock-product-cell";

            const name =
                document.createElement("span");

            const code =
                document.createElement("span");

            name.className =
                "stock-product-name";

            name.textContent =
                productName || "Producto sin nombre";

            code.className =
                "stock-product-code";

            code.textContent =
                productCode || "Sin clave";

            cell.append(
                name,
                code
            );

            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} providerName valor de providerName requerido por la función
         * @param {*} providerRfc valor de providerRfc requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createProviderCell(
            providerName,
            providerRfc
        ) {
            const cell =
                document.createElement("td");

            const name =
                document.createElement("span");

            const rfc =
                document.createElement("span");

            name.className =
                "stock-provider-name";

            name.textContent =
                providerName
                || "Proveedor no disponible";

            rfc.className =
                "stock-provider-rfc";

            rfc.textContent =
                providerRfc
                || "RFC no disponible";

            cell.append(
                name,
                rfc
            );

            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} metricName valor de metricName requerido por la función
         * @param {*} metricShortName valor de metricShortName requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createMetricCell(
            metricName,
            metricShortName
        ) {
            const cell =
                document.createElement("td");

            const badge =
                document.createElement("span");

            const name =
                document.createElement("span");

            badge.className =
                "table-badge table-badge-primary";

            badge.textContent =
                metricShortName || "—";

            name.className =
                "table-cell-secondary ms-1";

            name.textContent =
                metricName
                || "Sin unidad";

            cell.append(
                badge,
                name
            );

            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} data datos que serán procesados por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createQuantityCell(data) {
            const cell =
                document.createElement("td");

            const wrapper =
                document.createElement("div");

            const top =
                document.createElement("div");

            const quantityValue =
                document.createElement("strong");

            const unit =
                document.createElement("span");

            const progress =
                document.createElement("div");

            const progressBar =
                document.createElement("div");

            wrapper.className =
                "stock-quantity-wrapper";

            top.className =
                "stock-quantity-top";

            quantityValue.className =
                "stock-quantity-value";

            quantityValue.textContent =
                formatNumber(
                    data.quantity
                );

            unit.className =
                "stock-quantity-unit";

            unit.textContent =
                data.metricShortName
                || "unidades";

            progress.className =
                "stock-progress";

            progress.setAttribute(
                "role",
                "progressbar"
            );

            progress.setAttribute(
                "aria-label",
                "Nivel de existencia"
            );

            progress.setAttribute(
                "aria-valuemin",
                "0"
            );

            progress.setAttribute(
                "aria-valuemax",
                "100"
            );

            progress.setAttribute(
                "aria-valuenow",
                String(data.stockPercentage)
            );

            progressBar.className =
                `stock-progress-bar is-${data.stockStatus}`;

            progressBar.style.width =
                `${data.stockPercentage}%`;

            top.append(
                quantityValue,
                unit
            );

            progress.appendChild(
                progressBar
            );

            wrapper.append(
                top,
                progress
            );

            cell.appendChild(wrapper);

            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} status valor de status requerido por la función
         * @param {*} label valor de label requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createStatusCell(
            status,
            label
        ) {
            const cell =
                document.createElement("td");

            const badge =
                document.createElement("span");

            badge.className =
                `stock-status is-${status}`;

            badge.textContent =
                label;

            cell.appendChild(badge);

            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createActionCell() {
            const cell =
                document.createElement("td");

            const actions =
                document.createElement("div");

            const button =
                document.createElement("button");

            const icon =
                document.createElement("i");

            actions.className =
                "table-actions";

            button.type = "button";

            button.className =
                "table-action-btn table-action-view btn-view-stock";

            button.title =
                "Ver detalles";

            button.setAttribute(
                "aria-label",
                "Ver detalles"
            );

            icon.className =
                "bi bi-eye";

            button.appendChild(icon);

            actions.appendChild(button);

            cell.appendChild(actions);

            return cell;
        }

        /* ======================================================
           MODAL DE DETALLES
           ====================================================== */

        tableBody.addEventListener(
            "click",
            function (event) {
                const button =
                    event.target.closest(
                        ".btn-view-stock"
                    );

                if (!button) {
                    return;
                }

                const row =
                    button.closest(
                        ".stock-table-row"
                    );

                if (!row) {
                    return;
                }

                event.preventDefault();

                openStockDetails(row);
            }
        );

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openStockDetails(row) {
            const productName =
                normalizeText(
                    row.dataset.productName
                );

            const productCode =
                normalizeText(
                    row.dataset.productCode
                );

            const providerName =
                normalizeText(
                    row.dataset.providerName
                );

            const providerRfc =
                normalizeText(
                    row.dataset.providerRfc
                );

            const metricName =
                normalizeText(
                    row.dataset.metricName
                );

            const metricShortName =
                normalizeText(
                    row.dataset.metricShortName
                );

            const price =
                normalizeNumber(
                    row.dataset.price
                );

            const quantity =
                normalizeInteger(
                    row.dataset.quantity
                );

            const status =
                normalizeStockStatus(
                    row.dataset.status,
                    quantity
                );

            const statusLabel =
                normalizeText(
                    row.dataset.statusLabel
                ) || getStatusLabel(status);

            const percentage =
                normalizePercentage(
                    row.dataset.percentage,
                    quantity
                );

            setText(
                "viewStockProductName",
                productName || "-"
            );

            setText(
                "viewStockProductCode",
                productCode || "-"
            );

            setText(
                "viewStockProvider",
                providerName || "-"
            );

            setText(
                "viewStockProviderRfc",
                providerRfc || "-"
            );

            setText(
                "viewStockMetric",
                metricShortName
                    ? `${metricName} (${metricShortName})`
                    : metricName || "-"
            );

            setText(
                "viewStockPrice",
                formatCurrency(price)
            );

            setText(
                "viewStockQuantity",
                formatNumber(quantity)
            );

            setText(
                "viewStockUnit",
                metricShortName || "unidades"
            );

            updateStatusElement(
                document.getElementById(
                    "viewStockStatus"
                ),
                status,
                statusLabel
            );

            updateStatusElement(
                document.getElementById(
                    "viewStockQuantityStatus"
                ),
                status,
                statusLabel
            );

            updateDetailProgress(
                status,
                percentage
            );

            modalViewStock?.show();
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} element elemento del DOM relacionado con la operación
         * @param {*} status valor de status requerido por la función
         * @param {*} label valor de label requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateStatusElement(
            element,
            status,
            label
        ) {
            if (!element) {
                return;
            }

            element.classList.remove(
                "is-available",
                "is-low",
                "is-out"
            );

            element.classList.add(
                `is-${status}`
            );

            element.textContent =
                label;
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} status valor de status requerido por la función
         * @param {*} percentage valor de percentage requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateDetailProgress(
            status,
            percentage
        ) {
            const progress =
                document.querySelector(
                    "#modalViewStock .stock-detail-progress"
                );

            const progressBar =
                document.getElementById(
                    "viewStockProgressBar"
                );

            if (progress) {
                progress.setAttribute(
                    "aria-valuenow",
                    String(percentage)
                );
            }

            if (!progressBar) {
                return;
            }

            progressBar.classList.remove(
                "is-available",
                "is-low",
                "is-out"
            );

            progressBar.classList.add(
                `is-${status}`
            );

            progressBar.style.width =
                `${percentage}%`;
        }

        /* ======================================================
           FILTROS DINÁMICOS
           ====================================================== */

        /**
         * Ejecuta la operación rebuildFilterOptions del módulo de interfaz.
         *
         * @param {*} stockList valor de stockList requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function rebuildFilterOptions(stockList) {
            rebuildSelectOptions({
                selectId: "stockProviderFilter",
                items: stockList,
                getValue: function (item) {
                    return normalizePositiveInteger(
                        item.idProvider
                    );
                },
                getLabel: function (item) {
                    return normalizeText(
                        item.providerName
                    );
                },
                defaultLabel: "Todos"
            });

            rebuildSelectOptions({
                selectId: "stockMetricFilter",
                items: stockList,
                getValue: function (item) {
                    return normalizePositiveInteger(
                        item.idMetric
                    );
                },
                getLabel: function (item) {
                    const metricName =
                        normalizeText(
                            item.metricName
                        );

                    const shortName =
                        normalizeText(
                            item.metricShortName
                        );

                    return shortName
                        ? `${metricName} (${shortName})`
                        : metricName;
                },
                defaultLabel: "Todas"
            });
        }

        /**
         * Ejecuta la operación rebuildSelectOptions del módulo de interfaz.
         *
         * @param {*} config valor de config requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function rebuildSelectOptions(config) {
            const select =
                document.getElementById(
                    config.selectId
                );

            if (!select) {
                return;
            }

            const currentValue =
                select.value;

            const uniqueValues =
                new Map();

            config.items.forEach(function (item) {
                const value =
                    config.getValue(item);

                const label =
                    config.getLabel(item);

                if (
                    value !== null
                    && label
                    && !uniqueValues.has(value)
                ) {
                    uniqueValues.set(
                        value,
                        label
                    );
                }
            });

            select.replaceChildren(
                createOption(
                    "all",
                    config.defaultLabel
                )
            );

            Array.from(
                uniqueValues.entries()
            )
                .sort(function (first, second) {
                    return first[1].localeCompare(
                        second[1],
                        "es",
                        {
                            sensitivity: "base"
                        }
                    );
                })
                .forEach(function (entry) {
                    select.appendChild(
                        createOption(
                            entry[0],
                            entry[1]
                        )
                    );
                });

            if (
                currentValue !== "all"
                && uniqueValues.has(
                    Number(currentValue)
                )
            ) {
                select.value =
                    currentValue;
            } else {
                select.value = "all";
            }
        }

        /* ======================================================
           FORMATO INICIAL
           ====================================================== */

        /**
         * Convierte el valor al formato utilizado para su presentación.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatInitialValues() {
            document
                .querySelectorAll(
                    ".stock-money-cell"
                )
                .forEach(function (cell) {
                    cell.textContent =
                        formatCurrency(
                            cell.dataset.moneyValue
                            || cell.textContent
                        );
                });

            [
                stockSummaryProducts,
                stockSummaryUnits,
                stockSummaryLow,
                stockSummaryOut
            ].forEach(function (element) {
                if (element) {
                    element.textContent =
                        formatNumber(
                            element.textContent
                        );
                }
            });
        }

        /* ======================================================
           VISIBILIDAD DE TABLA
           ====================================================== */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} totalRows valor de totalRows requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateTableVisibility(
            totalRows
        ) {
            const responsive =
                table.closest(
                    ".table-responsive"
                );

            const pagination =
                document.querySelector(
                    `.table-pagination[data-table-target="${TABLE_ID}"]`
                );

            const generalEmptyState =
                document.getElementById(
                    "stockGeneralEmptyState"
                );

            const filterEmptyState =
                document.getElementById(
                    "stockFilterEmptyState"
                );

            if (responsive) {
                responsive.style.display =
                    totalRows > 0
                        ? ""
                        : "none";
            }

            if (pagination) {
                pagination.style.display =
                    totalRows > 0
                        ? "grid"
                        : "none";
            }

            if (generalEmptyState) {
                generalEmptyState.style.display =
                    totalRows === 0
                        ? "block"
                        : "none";
            }

            if (filterEmptyState) {
                filterEmptyState.style.display =
                    "none";
            }
        }

        /* ======================================================
           ESTADO Y PORCENTAJE
           ====================================================== */

        /**
         * Ejecuta la operación normalizeStockStatus del módulo de interfaz.
         *
         * @param {*} status valor de status requerido por la función
         * @param {*} quantity valor de quantity requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeStockStatus(
            status,
            quantity
        ) {
            const normalized =
                normalizeText(status)
                    .toLowerCase();

            if (
                normalized === "available"
                || normalized === "low"
                || normalized === "out"
            ) {
                return normalized;
            }

            if (quantity <= 0) {
                return "out";
            }

            if (quantity <= lowStockLimit) {
                return "low";
            }

            return "available";
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         *
         * @param {*} status valor de status requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getStatusLabel(status) {
            switch (status) {
                case "out":
                    return "Agotado";

                case "low":
                    return "Stock bajo";

                default:
                    return "Disponible";
            }
        }

        /**
         * Ejecuta la operación normalizePercentage del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @param {*} quantity valor de quantity requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizePercentage(
            value,
            quantity
        ) {
            const number =
                Number(value);

            if (
                Number.isFinite(number)
                && number >= 0
            ) {
                return Math.min(
                    Math.max(
                        Math.round(number),
                        0
                    ),
                    100
                );
            }

            return calculatePercentage(
                quantity
            );
        }

        /**
         * Ejecuta la operación calculatePercentage del módulo de interfaz.
         *
         * @param {*} quantity valor de quantity requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function calculatePercentage(
            quantity
        ) {
            if (quantity <= 0) {
                return 0;
            }

            const referenceMaximum =
                lowStockLimit * 2;

            const percentage =
                Math.round(
                    quantity * 100
                    / referenceMaximum
                );

            return Math.min(
                Math.max(
                    percentage,
                    5
                ),
                100
            );
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

            if (Number(error.status) === 401) {
                window.location.href =
                    `${Api.getContextPath()}/login`;

                return;
            }

            if (
                error.data
                && typeof error.data
                === "object"
            ) {
                showResponseMessage(
                    error.data
                );

                return;
            }

            const message =
                error.message
                || "No fue posible actualizar las existencias.";

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

            if (
                window.Api?.ApiError
            ) {
                return new Api.ApiError(
                    message,
                    400,
                    result
                );
            }

            const error =
                new Error(message);

            error.data = result;
            error.status = 400;

            return error;
        }

        /* ======================================================
           AUXILIARES
           ====================================================== */

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         *
         * @param {*} id identificador del registro o componente
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getModal(id) {
            const element =
                document.getElementById(id);

            if (
                !element
                || typeof bootstrap
                === "undefined"
                || !bootstrap.Modal
            ) {
                return null;
            }

            return bootstrap.Modal
                .getOrCreateInstance(element);
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} text valor de text requerido por la función
         * @param {*} className valor de className requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createCell(
            text,
            className = ""
        ) {
            const cell =
                document.createElement("td");

            cell.className =
                className;

            cell.textContent =
                text;

            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} value valor que se transformará o validará
         * @param {*} text valor de text requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createOption(
            value,
            text
        ) {
            const option =
                document.createElement("option");

            option.value =
                String(value);

            option.textContent =
                text;

            return option;
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
        function setText(id, value) {
            const element =
                document.getElementById(id);

            if (element) {
                element.textContent =
                    value == null
                        ? ""
                        : String(value);
            }
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} element elemento del DOM relacionado con la operación
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setElementText(
            element,
            value
        ) {
            if (element) {
                element.textContent =
                    value;
            }
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
         * Ejecuta la operación normalizeInteger del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeInteger(value) {
            const number =
                Number(value);

            if (!Number.isFinite(number)) {
                return 0;
            }

            return Math.max(
                Math.trunc(number),
                0
            );
        }

        /**
         * Ejecuta la operación normalizePositiveInteger del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizePositiveInteger(value) {
            const normalized =
                normalizeText(value);

            if (!/^\d+$/.test(normalized)) {
                return null;
            }

            const number =
                Number(normalized);

            return Number.isSafeInteger(number)
            && number > 0
                ? number
                : null;
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
                "es-MX"
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
        function formatCurrency(value) {
            return new Intl.NumberFormat(
                "es-MX",
                {
                    style: "currency",
                    currency: "MXN",
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                }
            ).format(
                normalizeNumber(value)
            );
        }
    });
})();
