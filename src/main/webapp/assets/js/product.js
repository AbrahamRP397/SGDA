/**
 * ==========================================================
 * MÓDULO: PRODUCTOS
 * ==========================================================
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function () {
    "use strict";

    if (window.productModuleInitialized) {
        return;
    }

    window.productModuleInitialized = true;

    document.addEventListener("DOMContentLoaded", function () {
        const TABLE_ID = "productsTable";
        const table = document.getElementById(TABLE_ID);
        const tableBody = table?.querySelector("tbody");
        const providerRowTemplate = document.getElementById("productProviderRowTemplate");

        const formCreate = document.getElementById("formCreateProduct");
        const formEdit = document.getElementById("formEditProduct");
        const formChangeStatus = document.getElementById("formChangeStatus");
        const formQuickMetric = document.getElementById("formQuickMetric");
        const formQuickProvider = document.getElementById("formQuickProvider");

        const createProviderList = document.getElementById("createProviderList");
        const editProviderList = document.getElementById("editProviderList");

        const btnNuevoProducto = document.getElementById("btnNuevoProducto");
        const btnOpenConfirmEdit = document.getElementById("btnOpenConfirmEdit");
        const btnConfirmEdit = document.getElementById("btnConfirmEdit");
        const btnConfirmStatus = document.getElementById("btnConfirmStatus");
        const btnSaveQuickMetric = document.getElementById("btnSaveQuickMetric");
        const btnCloseQuickMetric = document.getElementById("btnCloseQuickMetric");
        const btnCancelQuickMetric = document.getElementById("btnCancelQuickMetric");
        const btnSaveQuickProvider = document.getElementById("btnSaveQuickProvider");
        const btnCloseQuickProvider = document.getElementById("btnCloseQuickProvider");
        const btnCancelQuickProvider = document.getElementById("btnCancelQuickProvider");

        const createSubmitButton = document.querySelector(
            '[type="submit"][form="formCreateProduct"]'
        );

        const modalCreateElement = document.getElementById("modalCreate");
        const modalEditElement = document.getElementById("modalEdit");
        const modalQuickMetricElement = document.getElementById("modalQuickMetric");
        const modalQuickProviderElement = document.getElementById("modalQuickProvider");

        const modalCreate = getModal("modalCreate");
        const modalView = getModal("modalView");
        const modalEdit = getModal("modalEdit");
        const modalConfirmEdit = getModal("modalConfirmEdit");
        const modalConfirmStatus = getModal("modalConfirmStatus");
        const modalQuickMetric = getModal("modalQuickMetric");
        const modalQuickProvider = getModal("modalQuickProvider");

        let quickProviderParentModal = null;
        let quickProviderParentElement = null;
        let quickProviderTargetList = null;

        let quickMetricTargetSelectId = "";
        let quickMetricParentModal = null;

        if (!table || !tableBody) {
            console.warn("No se encontró la tabla de productos.");
            return;
        }

        if (!window.Api) {
            console.error("api.js no está disponible.");
            return;
        }

        /* ======================================================
           REGISTRAR PRODUCTO
           ====================================================== */

        btnNuevoProducto?.addEventListener("click", function () {
            resetForm(formCreate, true);
            clearProviderList(createProviderList);
            addProviderRow(createProviderList);
            modalCreate?.show();
        });

        formCreate?.addEventListener("submit", async function (event) {
            event.preventDefault();
            event.stopImmediatePropagation();

            if (!validateProductForm(formCreate)
                || createSubmitButton?.disabled
                || formCreate.dataset.fetchSubmitting === "true") {
                return;
            }

            formCreate.dataset.fetchSubmitting = "true";
            setFormLoading(formCreate, createSubmitButton, true, "Guardando...");

            try {
                const result = await Api.submitForm(formCreate);
                showToast(result);

                if (!result.success) {
                    return;
                }

                modalCreate?.hide();
                resetForm(formCreate, true);
                clearProviderList(createProviderList);
                await loadProducts();
            } catch (error) {
                handleRequestError(error);
            } finally {
                delete formCreate.dataset.fetchSubmitting;
                setFormLoading(formCreate, createSubmitButton, false);
            }
        });

        /* ======================================================
           ACTUALIZAR PRODUCTO
           ====================================================== */

        btnOpenConfirmEdit?.addEventListener("click", function () {
            if (!validateProductForm(formEdit)) {
                return;
            }

            setText(
                "editConfirmProductName",
                getValue("editName") || "-"
            );

            modalConfirmEdit?.show();
        });

        btnConfirmEdit?.addEventListener("click", async function () {
            if (!formEdit
                || btnConfirmEdit.disabled
                || formEdit.dataset.fetchSubmitting === "true") {
                return;
            }

            if (!validateProductForm(formEdit)) {
                modalConfirmEdit?.hide();
                return;
            }

            if (!isPositiveInteger(getValue("editProductId"))) {
                showWarning(
                    "No se pudo determinar el producto que deseas actualizar."
                );

                modalConfirmEdit?.hide();
                return;
            }

            formEdit.dataset.fetchSubmitting = "true";
            setFormLoading(
                formEdit,
                btnConfirmEdit,
                true,
                "Actualizando..."
            );

            try {
                const result = await Api.submitForm(formEdit);
                showToast(result);

                if (!result.success) {
                    return;
                }

                modalConfirmEdit?.hide();
                modalEdit?.hide();
                resetForm(formEdit, false);
                clearProviderList(editProviderList);
                await loadProducts();
            } catch (error) {
                handleRequestError(error);
            } finally {
                delete formEdit.dataset.fetchSubmitting;
                setFormLoading(formEdit, btnConfirmEdit, false);
            }
        });

        /* ======================================================
           CAMBIAR ESTADO
           ====================================================== */

        btnConfirmStatus?.addEventListener("click", async function () {
            if (!formChangeStatus
                || btnConfirmStatus.disabled
                || formChangeStatus.dataset.fetchSubmitting === "true") {
                return;
            }

            const productId = getValue("statusProductId");
            const newStatus = getValue("statusNewValue");

            if (!isPositiveInteger(productId)) {
                showWarning("No se pudo determinar el producto.");
                return;
            }

            if (!isValidStatus(newStatus)) {
                showWarning("No se pudo determinar el nuevo estado.");
                return;
            }

            formChangeStatus.dataset.fetchSubmitting = "true";
            setFormLoading(
                formChangeStatus,
                btnConfirmStatus,
                true,
                "Procesando..."
            );

            try {
                const result = await Api.submitForm(formChangeStatus);
                showToast(result);

                if (!result.success) {
                    return;
                }

                modalConfirmStatus?.hide();
                await loadProducts();
            } catch (error) {
                handleRequestError(error);
            } finally {
                delete formChangeStatus.dataset.fetchSubmitting;
                setFormLoading(formChangeStatus, btnConfirmStatus, false);
            }
        });

        /* ======================================================
           FILAS DE PROVEEDORES
           ====================================================== */

        document.addEventListener("click", function (event) {
            const addButton = event.target.closest(
                ".btn-add-product-provider"
            );

            if (addButton) {
                const targetListId = normalizeText(
                    addButton.dataset.targetList
                );

                const targetList = document.getElementById(
                    targetListId
                );

                if (!targetList) {
                    showWarning(
                        "No se encontró la sección de proveedores."
                    );
                    return;
                }

                addProviderRow(targetList);
                return;
            }

            const removeButton = event.target.closest(
                ".product-provider-remove"
            );

            if (!removeButton) {
                return;
            }

            const row = removeButton.closest(
                ".product-provider-row"
            );

            const list = row?.closest(
                "[data-provider-list]"
            );

            row?.remove();
            updateProviderEmptyState(list);
            refreshProviderOptions(list);
        });

        document.addEventListener("change", function (event) {
            const select = event.target.closest(
                ".product-provider-select"
            );

            if (!select) {
                return;
            }

            const list = select.closest(
                "[data-provider-list]"
            );

            clearFieldValidation(select);
            refreshProviderOptions(list);
        });

        /**
         * Ejecuta la operación addProviderRow del módulo de interfaz.
         *
         * @param {*} targetList valor de targetList requerido por la función
         * @param {*} relation valor de relation requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function addProviderRow(
            targetList,
            relation = null
        ) {
            if (!targetList || !providerRowTemplate) {
                return;
            }

            const fragment =
                providerRowTemplate.content.cloneNode(true);

            const row = fragment.querySelector(
                ".product-provider-row"
            );

            const select = fragment.querySelector(
                ".product-provider-select"
            );

            const priceInput = fragment.querySelector(
                ".product-provider-price"
            );

            if (relation && select) {
                select.value = String(
                    normalizeId(relation.idProvider)
                );
            }

            if (relation && priceInput) {
                priceInput.value = formatDecimalInput(
                    relation.purchasePrice
                );
            }

            targetList.appendChild(fragment);

            updateProviderEmptyState(targetList);
            refreshProviderOptions(targetList);

            if (!relation) {
                row?.querySelector(
                    ".product-provider-select"
                )?.focus();
            }
        }

        /**
         * Retira o limpia la información indicada de la interfaz.
         *
         * @param {*} list valor de list requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function clearProviderList(list) {
            if (!list) {
                return;
            }

            list.replaceChildren();
            updateProviderEmptyState(list);
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} list valor de list requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateProviderEmptyState(list) {
            if (!list) {
                return;
            }

            const section = list.closest(
                ".product-provider-section"
            );

            const emptyState = section?.querySelector(
                "[data-provider-empty]"
            );

            if (emptyState) {
                emptyState.style.display =
                    list.children.length === 0
                        ? ""
                        : "none";
            }
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} list valor de list requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function refreshProviderOptions(list) {
            if (!list) {
                return;
            }

            const selects = Array.from(
                list.querySelectorAll(
                    ".product-provider-select"
                )
            );

            const selectedValues = new Set(
                selects
                    .map(function (select) {
                        return select.value;
                    })
                    .filter(Boolean)
            );

            selects.forEach(function (select) {
                Array.from(select.options)
                    .forEach(function (option) {
                        if (!option.value) {
                            option.disabled = false;
                            return;
                        }

                        option.disabled =
                            option.value !== select.value
                            && selectedValues.has(option.value);
                    });
            });
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function validateProductForm(form) {
            if (!form) {
                return false;
            }

            const formValid = validateForm(form);
            const providersValid =
                validateProviderRows(form);

            return formValid && providersValid;
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function validateProviderRows(form) {
            const list = form.querySelector(
                "[data-provider-list]"
            );

            if (!list) {
                return true;
            }

            const rows = Array.from(
                list.querySelectorAll(
                    ".product-provider-row"
                )
            );

            if (rows.length === 0) {
                showWarning(
                    "Agrega al menos un proveedor para el producto."
                );

                return false;
            }

            const providerIds = new Set();
            let valid = true;
            let firstInvalidField = null;

            rows.forEach(function (row) {
                const select = row.querySelector(
                    ".product-provider-select"
                );

                const priceInput = row.querySelector(
                    ".product-provider-price"
                );

                const providerId = normalizeText(
                    select?.value
                );

                const price = Number(
                    normalizeDecimal(
                        priceInput?.value
                    )
                );

                if (!isPositiveInteger(providerId)) {
                    markFieldInvalid(
                        select,
                        "Selecciona un proveedor."
                    );

                    firstInvalidField ??= select;
                    valid = false;
                } else if (providerIds.has(providerId)) {
                    markFieldInvalid(
                        select,
                        "Este proveedor ya fue agregado."
                    );

                    firstInvalidField ??= select;
                    valid = false;
                } else {
                    providerIds.add(providerId);
                }

                if (!Number.isFinite(price)
                    || price < 0
                    || price > 9999999999.99) {

                    markFieldInvalid(
                        priceInput,
                        "Captura un precio válido."
                    );

                    firstInvalidField ??= priceInput;
                    valid = false;
                }
            });

            if (!valid) {
                firstInvalidField?.focus();
            }

            return valid;
        }

        /* ======================================================
           EVENTOS DE LA TABLA
           ====================================================== */

        tableBody.addEventListener("click", function (event) {
            const button = event.target.closest(
                ".table-action-btn"
            );

            if (!button) {
                return;
            }

            const row = button.closest(
                ".product-table-row"
            );

            if (!row) {
                return;
            }

            event.preventDefault();
            event.stopPropagation();

            if (button.classList.contains(
                "btn-view-product"
            )) {
                openViewModal(row);
                return;
            }

            if (button.classList.contains(
                "btn-edit-product"
            )) {
                openEditModal(row);
                return;
            }

            if (button.classList.contains(
                "btn-change-status"
            )) {
                openStatusModal(button, row);
            }
        });

        /* ======================================================
           CONSULTAR Y RECONSTRUIR PRODUCTOS
           ====================================================== */

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadProducts() {
            const result = await Api.get("/products/list");

            if (!result.success) {
                throw new Api.ApiError(
                    result.message
                    || "No fue posible consultar los productos.",
                    400,
                    result
                );
            }

            renderProducts(
                Array.isArray(result.data)
                    ? result.data
                    : []
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} products valor de products requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderProducts(products) {
            tableBody.replaceChildren();

            products.forEach(function (product) {
                tableBody.appendChild(
                    createProductRow(product)
                );
            });

            updateTableVisibility(products.length);

            if (typeof window.filterTable === "function") {
                window.filterTable(TABLE_ID);
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} product valor de product requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createProductRow(product) {
            const id = normalizeId(product.idProduct);
            const code = normalizeText(product.code);
            const name = normalizeText(product.name);
            const metricId = normalizeId(product.idMetric);
            const metricName = normalizeText(product.metricName);
            const metricShortName = normalizeText(
                product.metricShortName
            );

            const description = normalizeText(
                product.description
            );

            const active = Number(product.status) === 1;

            const providers = Array.isArray(product.providers)
                ? product.providers
                : [];

            const activeProviders = providers.filter(
                function (provider) {
                    return Number(provider.status) === 1
                        && Number(provider.providerStatus) === 1;
                }
            );

            const providerSearch = providers
                .map(function (provider) {
                    return [
                        normalizeText(provider.providerName),
                        normalizeText(provider.providerRfc)
                    ].join(" ");
                })
                .join(" ");

            const row = document.createElement("tr");

            row.className =
                "js-table-row product-table-row";

            row.dataset.id = String(id);
            row.dataset.code = code;
            row.dataset.name = name;
            row.dataset.metric = String(metricId);
            row.dataset.metricName = metricName;
            row.dataset.metricShortName = metricShortName;
            row.dataset.description = description;
            row.dataset.status =
                active ? "active" : "inactive";

            row.dataset.search = [
                code,
                name,
                metricName,
                metricShortName,
                description,
                providerSearch
            ].join(" ");

            const idCell = createCell(
                String(id),
                "table-cell-secondary table-cell-nowrap"
            );

            const codeCell = createCell(
                code,
                "table-cell-primary table-cell-nowrap"
            );

            const nameCell = createCell(
                name,
                "table-cell-primary"
            );

            const metricCell = document.createElement("td");
            const metricBadge = document.createElement("span");
            const metricText = document.createElement("span");

            metricBadge.className =
                "table-badge table-badge-primary";

            metricBadge.textContent =
                metricShortName || "-";

            metricText.className =
                "table-cell-secondary ms-1";

            metricText.textContent =
                metricName || "Sin unidad";

            metricCell.append(
                metricBadge,
                metricText
            );

            const providersCell =
                document.createElement("td");

            const providersBadge =
                document.createElement("span");

            providersBadge.className =
                "table-badge table-badge-info";

            providersBadge.textContent =
                `${activeProviders.length} ${
                    activeProviders.length === 1
                        ? "proveedor"
                        : "proveedores"
                }`;

            providersCell.appendChild(providersBadge);

            const descriptionCell = createCell(
                description || "Sin descripción",
                "table-cell-secondary"
            );

            const statusCell = document.createElement("td");
            const statusBadge = document.createElement("span");

            statusBadge.className = active
                ? "table-badge table-badge-success"
                : "table-badge table-badge-danger";

            statusBadge.textContent = active
                ? "Activo"
                : "Inactivo";

            statusCell.appendChild(statusBadge);

            const actionsCell = document.createElement("td");
            const actionsContainer =
                document.createElement("div");

            actionsContainer.className =
                "table-actions";

            actionsContainer.append(
                createActionButton({
                    className:
                        "table-action-btn table-action-view btn-view-product",
                    title: "Ver detalles",
                    icon: "bi bi-eye"
                }),
                createActionButton({
                    className:
                        "table-action-btn table-action-edit btn-edit-product",
                    title: "Editar producto",
                    icon: "bi bi-pencil"
                }),
                createStatusButton({
                    id,
                    name,
                    active
                })
            );

            const providerDataList =
                createProviderDataList(providers);

            actionsCell.append(
                actionsContainer,
                providerDataList
            );

            row.append(
                idCell,
                codeCell,
                nameCell,
                metricCell,
                providersCell,
                descriptionCell,
                statusCell,
                actionsCell
            );

            return row;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} providers valor de providers requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createProviderDataList(providers) {
            const container =
                document.createElement("div");

            container.className =
                "product-provider-data-list";

            container.hidden = true;

            providers.forEach(function (provider) {
                const element =
                    document.createElement("span");

                element.className =
                    "product-provider-data";

                element.dataset.idProductProvider =
                    String(
                        normalizeId(
                            provider.idProductProvider
                        )
                    );

                element.dataset.idProvider =
                    String(
                        normalizeId(provider.idProvider)
                    );

                element.dataset.providerName =
                    normalizeText(
                        provider.providerName
                    );

                element.dataset.providerRfc =
                    normalizeText(
                        provider.providerRfc
                    );

                element.dataset.purchasePrice =
                    formatDecimalInput(
                        provider.purchasePrice
                    );

                element.dataset.status =
                    Number(provider.status) === 1
                        ? "1"
                        : "0";

                element.dataset.providerStatus =
                    Number(provider.providerStatus) === 1
                        ? "1"
                        : "0";

                container.appendChild(element);
            });

            return container;
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
        function createCell(text, className = "") {
            const cell = document.createElement("td");
            cell.className = className;
            cell.textContent = text;
            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} config valor de config requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createActionButton(config) {
            const button =
                document.createElement("button");

            const icon =
                document.createElement("i");

            button.type = "button";
            button.className = config.className;
            button.title = config.title;
            button.setAttribute(
                "aria-label",
                config.title
            );

            icon.className = config.icon;
            button.appendChild(icon);

            return button;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} config valor de config requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createStatusButton(config) {
            const button = createActionButton({
                className:
                    "table-action-btn btn-change-status "
                    + (
                        config.active
                            ? "table-action-delete"
                            : "table-action-success"
                    ),
                title: config.active
                    ? "Desactivar producto"
                    : "Activar producto",
                icon: config.active
                    ? "bi bi-toggle-on"
                    : "bi bi-toggle-off"
            });

            button.dataset.productId =
                String(config.id);

            button.dataset.productName =
                config.name;

            button.dataset.newStatus =
                config.active ? "0" : "1";

            return button;
        }

        /* ======================================================
           MODALES DEL PRODUCTO
           ====================================================== */

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
        function openViewModal(row) {
            const active =
                row.dataset.status === "active";

            const metricName =
                row.dataset.metricName || "-";

            const metricShortName =
                row.dataset.metricShortName || "-";

            const providers =
                readProvidersFromRow(row);

            setText(
                "viewProductName",
                row.dataset.name || "-"
            );

            setText(
                "viewProductCode",
                row.dataset.code || "-"
            );

            setText(
                "viewProductMetric",
                `${metricName} (${metricShortName})`
            );

            setText(
                "viewProductDescription",
                row.dataset.description
                || "Sin descripción"
            );

            const badge =
                document.getElementById(
                    "viewProductStatus"
                );

            if (badge) {
                badge.textContent =
                    active ? "Activo" : "Inactivo";

                badge.className =
                    "table-badge "
                    + (
                        active
                            ? "table-badge-success"
                            : "table-badge-danger"
                    );
            }

            renderProviderSummary(providers);
            modalView?.show();
        }

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
        function openEditModal(row) {
            resetForm(formEdit, false);
            clearProviderList(editProviderList);

            setValue(
                "editProductId",
                row.dataset.id
            );

            setValue(
                "editCode",
                row.dataset.code
            );

            setValue(
                "editName",
                row.dataset.name
            );

            setValue(
                "editMetric",
                row.dataset.metric
            );

            setValue(
                "editDescription",
                row.dataset.description
            );

            setText(
                "editConfirmProductName",
                row.dataset.name || "-"
            );

            const providers = readProvidersFromRow(row)
                .filter(function (provider) {
                    return Number(provider.status) === 1
                        && Number(provider.providerStatus) === 1;
                });

            providers.forEach(function (provider) {
                addProviderRow(
                    editProviderList,
                    provider
                );
            });

            if (providers.length === 0) {
                addProviderRow(editProviderList);
            }

            updateProviderEmptyState(editProviderList);
            modalEdit?.show();
        }

        /**
         * Ejecuta la operación readProvidersFromRow del módulo de interfaz.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function readProvidersFromRow(row) {
            if (!row) {
                return [];
            }

            return Array.from(
                row.querySelectorAll(
                    ".product-provider-data"
                )
            ).map(function (element) {
                return {
                    idProductProvider:
                        normalizeId(
                            element.dataset
                                .idProductProvider
                        ),
                    idProvider:
                        normalizeId(
                            element.dataset.idProvider
                        ),
                    providerName:
                        normalizeText(
                            element.dataset.providerName
                        ),
                    providerRfc:
                        normalizeText(
                            element.dataset.providerRfc
                        ),
                    purchasePrice:
                        normalizeText(
                            element.dataset.purchasePrice
                        ),
                    status:
                        Number(element.dataset.status
                        ),

                    providerStatus:
                        Number(
                            element.dataset.providerStatus
                        )
                };
            });
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} providers valor de providers requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderProviderSummary(providers) {
            const container =
                document.getElementById(
                    "viewProductProviders"
                );

            const emptyState =
                document.getElementById(
                    "viewProductProvidersEmpty"
                );

            if (!container) {
                return;
            }

            container.replaceChildren();

            const activeProviders = providers.filter(
                function (provider) {
                    return Number(provider.status) === 1;
                }
            );

            if (activeProviders.length === 0) {
                container.style.display = "none";

                if (emptyState) {
                    emptyState.style.display = "";
                }

                return;
            }

            activeProviders.forEach(function (provider) {
                const item =
                    document.createElement("div");

                const info =
                    document.createElement("div");

                const name =
                    document.createElement("span");

                const rfc =
                    document.createElement("span");

                const price =
                    document.createElement("span");

                item.className =
                    "product-provider-summary-item";

                info.className =
                    "product-provider-summary-info";

                name.className =
                    "product-provider-summary-name";

                rfc.className =
                    "product-provider-summary-rfc";

                price.className =
                    "product-provider-summary-price";

                name.textContent =
                    provider.providerName
                    || "Proveedor sin nombre";

                rfc.textContent =
                    provider.providerRfc
                    || "RFC no disponible";

                price.textContent =
                    formatCurrency(
                        provider.purchasePrice
                    );

                info.append(name, rfc);
                item.append(info, price);
                container.appendChild(item);
            });

            container.style.display = "";

            if (emptyState) {
                emptyState.style.display = "none";
            }
        }

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} button valor de button requerido por la función
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openStatusModal(button, row) {
            const productId = normalizeText(
                button.dataset.productId
                || row.dataset.id
            );

            const productName = normalizeText(
                button.dataset.productName
                || row.dataset.name
            );

            const newStatus = normalizeText(
                button.dataset.newStatus
            );

            if (!isPositiveInteger(productId)
                || !isValidStatus(newStatus)) {

                showWarning(
                    "No se pudo determinar el producto o el nuevo estado."
                );

                return;
            }

            const activating = newStatus === "1";

            setValue(
                "statusProductId",
                productId
            );

            setValue(
                "statusNewValue",
                newStatus
            );

            setText(
                "statusConfirmProductName",
                productName || "Producto"
            );

            setText(
                "statusModalQuestion",
                activating
                    ? "¿Deseas activar este producto?"
                    : "¿Deseas desactivar este producto?"
            );

            setText(
                "statusModalDescription",
                activating
                    ? "El producto volverá a estar disponible para las operaciones del sistema."
                    : "El producto dejará de estar disponible para nuevas operaciones."
            );

            setText(
                "statusConfirmButtonText",
                activating
                    ? "Activar"
                    : "Desactivar"
            );

            configureStatusModal(activating);
            modalConfirmStatus?.show();
        }

        /**
         * Ejecuta la operación configureStatusModal del módulo de interfaz.
         *
         * @param {*} activating valor de activating requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function configureStatusModal(activating) {
            const modalIcon =
                document.getElementById(
                    "statusModalIcon"
                );

            const headerIcon =
                document.getElementById(
                    "statusModalHeaderIcon"
                );

            const confirmIcon =
                document.getElementById(
                    "statusConfirmButtonIcon"
                );

            if (btnConfirmStatus) {
                btnConfirmStatus.disabled = false;

                btnConfirmStatus.className =
                    activating
                        ? "btn btn-success"
                        : "btn btn-danger";
            }

            if (modalIcon) {
                modalIcon.className =
                    activating
                        ? "bi bi-box-seam-fill"
                        : "bi bi-box-seam";

                modalIcon.style.color =
                    activating
                        ? "#57d38c"
                        : "#ff6666";
            }

            if (headerIcon) {
                headerIcon.className =
                    activating
                        ? "bi bi-check-circle-fill me-2"
                        : "bi bi-exclamation-triangle-fill me-2";

                headerIcon.style.color =
                    activating
                        ? "#57d38c"
                        : "#ff6666";
            }

            if (confirmIcon) {
                confirmIcon.className =
                    activating
                        ? "bi bi-check-circle me-1"
                        : "bi bi-x-circle me-1";
            }
        }

        /* ======================================================
           REGISTRO RÁPIDO DE MÉTRICA
           ====================================================== */

        document.addEventListener("click", function (event) {
            const button = event.target.closest(
                ".btn-open-quick-metric"
            );

            if (!button) {
                return;
            }

            const targetSelectId = normalizeText(
                button.dataset.targetSelect
            );

            if (!targetSelectId
                || !document.getElementById(
                    targetSelectId
                )) {

                showWarning(
                    "No se encontró el selector de unidades de medida."
                );

                return;
            }

            openQuickMetric(targetSelectId);
        });

        btnCloseQuickMetric?.addEventListener(
            "click",
            closeQuickMetric
        );

        btnCancelQuickMetric?.addEventListener(
            "click",
            closeQuickMetric
        );

        /* ======================================================
   REGISTRO RÁPIDO DE PROVEEDOR
   ====================================================== */

        document.addEventListener("click", function (event) {
            const button = event.target.closest(
                ".btn-open-quick-provider"
            );

            if (!button) {
                return;
            }

            event.preventDefault();

            const targetListId = normalizeText(
                button.dataset.targetList
            );

            const parentModalId = normalizeText(
                button.dataset.parentModal
            );

            const targetList =
                document.getElementById(targetListId);

            const parentElement =
                document.getElementById(parentModalId);

            if (!targetList || !parentElement) {
                showWarning(
                    "No se pudo abrir el registro de proveedor."
                );
                return;
            }

            quickProviderTargetList = targetList;
            quickProviderParentElement = parentElement;

            quickProviderParentModal =
                parentModalId === "modalEdit"
                    ? modalEdit
                    : modalCreate;

            openQuickProvider();
        });

        btnCloseQuickProvider?.addEventListener(
            "click",
            closeQuickProvider
        );

        btnCancelQuickProvider?.addEventListener(
            "click",
            closeQuickProvider
        );

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openQuickProvider() {
            if (!quickProviderParentModal
                || !quickProviderParentElement
                || !modalQuickProvider) {
                return;
            }

            resetForm(
                formQuickProvider,
                true
            );

            /*
             * Evitamos que form.js limpie el producto
             * al ocultar temporalmente su modal.
             */
            quickProviderParentElement.dataset
                .preserveFormState = "true";

            quickProviderParentElement.addEventListener(
                "hidden.bs.modal",
                function () {
                    modalQuickProvider.show();

                    document.getElementById(
                        "quickProviderName"
                    )?.focus();
                },
                {
                    once: true
                }
            );

            quickProviderParentModal.hide();
        }

        /**
         * Oculta el componente visual y restablece su estado temporal.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function closeQuickProvider() {
            modalQuickProvider?.hide();
        }

        modalQuickProviderElement?.addEventListener(
            "hidden.bs.modal",
            function () {
                const parentModal =
                    quickProviderParentModal;

                const parentElement =
                    quickProviderParentElement;

                if (parentModal) {
                    parentModal.show();
                }

                if (parentElement) {
                    delete parentElement.dataset
                        .preserveFormState;
                }

                quickProviderParentModal = null;
                quickProviderParentElement = null;
                quickProviderTargetList = null;
            }
        );

        formQuickProvider?.addEventListener(
            "submit",
            async function (event) {
                event.preventDefault();
                event.stopImmediatePropagation();

                if (!validateForm(formQuickProvider)
                    || btnSaveQuickProvider?.disabled
                    || formQuickProvider.dataset
                        .fetchSubmitting === "true") {
                    return;
                }

                const providerName =
                    getValue("quickProviderName");

                const providerRfc =
                    getValue("quickProviderRfc")
                        .toUpperCase();

                formQuickProvider.dataset
                    .fetchSubmitting = "true";

                setFormLoading(
                    formQuickProvider,
                    btnSaveQuickProvider,
                    true,
                    "Guardando..."
                );

                try {
                    const result =
                        await Api.submitForm(
                            formQuickProvider
                        );

                    showToast(result);

                    if (!result.success) {
                        return;
                    }

                    const createdProvider =
                        await reloadProviderSelects({
                            name: providerName,
                            rfc: providerRfc
                        });

                    if (!createdProvider) {
                        showWarning(
                            "El proveedor se registró, pero no fue posible seleccionarlo automáticamente."
                        );
                    }

                    resetForm(
                        formQuickProvider,
                        true
                    );

                    modalQuickProvider?.hide();

                } catch (error) {
                    handleRequestError(error);
                } finally {
                    delete formQuickProvider.dataset
                        .fetchSubmitting;

                    setFormLoading(
                        formQuickProvider,
                        btnSaveQuickProvider,
                        false
                    );
                }
            }
        );

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} targetSelectId valor de targetSelectId requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openQuickMetric(targetSelectId) {
            quickMetricTargetSelectId = targetSelectId;

            quickMetricParentModal =
                targetSelectId === "editMetric"
                    ? modalEdit
                    : modalCreate;

            const parentElement =
                targetSelectId === "editMetric"
                    ? modalEditElement
                    : modalCreateElement;

            resetForm(formQuickMetric, true);

            if (!quickMetricParentModal
                || !modalQuickMetric
                || !parentElement) {
                return;
            }

            /*
             * IMPORTANTE:
             * estamos ocultando el modal, no cerrándolo.
             * Sus datos deben permanecer intactos.
             */
            parentElement.dataset.preserveFormState = "true";

            parentElement.addEventListener(
                "hidden.bs.modal",
                function () {
                    modalQuickMetric.show();

                    document.getElementById(
                        "quickMetricName"
                    )?.focus();
                },
                {once: true}
            );

            quickMetricParentModal.hide();
        }

        /**
         * Oculta el componente visual y restablece su estado temporal.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function closeQuickMetric() {
            modalQuickMetric?.hide();
        }

        modalQuickMetricElement?.addEventListener(
            "hidden.bs.modal",
            function () {
                const parentModal = quickMetricParentModal;

                if (!parentModal) {
                    return;
                }

                const parentElement =
                    quickMetricTargetSelectId === "editMetric"
                        ? modalEditElement
                        : modalCreateElement;

                parentModal.show();

                if (parentElement) {
                    delete parentElement.dataset.preserveFormState;
                }

                quickMetricParentModal = null;
                quickMetricTargetSelectId = "";
            }
        );

        formQuickMetric?.addEventListener(
            "submit",
            async function (event) {
                event.preventDefault();
                event.stopImmediatePropagation();

                if (!validateForm(formQuickMetric)
                    || btnSaveQuickMetric?.disabled
                    || formQuickMetric.dataset
                        .fetchSubmitting === "true") {
                    return;
                }

                const metricName =
                    getValue("quickMetricName");

                const metricShortName =
                    getValue(
                        "quickMetricShortName"
                    ).toUpperCase();

                formQuickMetric.dataset.fetchSubmitting =
                    "true";

                setFormLoading(
                    formQuickMetric,
                    btnSaveQuickMetric,
                    true,
                    "Guardando..."
                );

                try {
                    const result =
                        await Api.submitForm(
                            formQuickMetric
                        );

                    showToast(result);

                    if (!result.success) {
                        return;
                    }

                    const selectedMetricId =
                        await reloadMetricSelects({
                            name: metricName,
                            shortName:
                            metricShortName,
                            targetSelectId:
                            quickMetricTargetSelectId
                        });

                    if (!selectedMetricId) {
                        showWarning(
                            "La unidad se registró, pero no fue posible seleccionarla automáticamente."
                        );
                    }

                    resetForm(
                        formQuickMetric,
                        true
                    );

                    modalQuickMetric?.hide();
                } catch (error) {
                    handleRequestError(error);
                } finally {
                    delete formQuickMetric.dataset
                        .fetchSubmitting;

                    setFormLoading(
                        formQuickMetric,
                        btnSaveQuickMetric,
                        false
                    );
                }
            }
        );

        /**
         * Ejecuta la operación reloadMetricSelects del módulo de interfaz.
         *
         * @param {*} newMetric valor de newMetric requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function reloadMetricSelects(
            newMetric
        ) {
            const result =
                await Api.get("/metrics/list");

            if (!result.success) {
                throw new Api.ApiError(
                    result.message
                    || "No fue posible consultar las unidades de medida.",
                    400,
                    result
                );
            }

            const metrics = Array.isArray(result.data)
                ? result.data.filter(function (metric) {
                    return Number(metric.status) === 1;
                })
                : [];

            const createdMetric =
                metrics.find(function (metric) {
                    return normalizeComparable(
                            metric.name
                        ) === normalizeComparable(
                            newMetric.name
                        )
                        && normalizeComparable(
                            metric.shortName
                        ) === normalizeComparable(
                            newMetric.shortName
                        );
                });

            updateMetricSelect(
                document.getElementById(
                    "createMetric"
                ),
                metrics,
                newMetric.targetSelectId
                === "createMetric"
                    ? createdMetric?.idMetric
                    : null
            );

            updateMetricSelect(
                document.getElementById(
                    "editMetric"
                ),
                metrics,
                newMetric.targetSelectId
                === "editMetric"
                    ? createdMetric?.idMetric
                    : null
            );

            updateMetricFilter(
                document.getElementById(
                    "productMetricFilter"
                ),
                metrics
            );

            return createdMetric?.idMetric || null;
        }

        /**
         * Ejecuta la operación reloadProviderSelects del módulo de interfaz.
         *
         * @param {*} newProvider valor de newProvider requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function reloadProviderSelects(newProvider) {
            const result =
                await Api.get("/providers/list");

            if (!result.success) {
                throw new Api.ApiError(
                    result.message
                    || "No fue posible consultar los proveedores.",
                    400,
                    result
                );
            }

            const providers =
                Array.isArray(result.data)
                    ? result.data.filter(function (provider) {
                        return Number(provider.status) === 1;
                    })
                    : [];

            const createdProvider =
                providers.find(function (provider) {
                    return normalizeComparable(
                            provider.name
                        ) === normalizeComparable(
                            newProvider.name
                        )
                        && normalizeComparable(
                            provider.rfc
                        ) === normalizeComparable(
                            newProvider.rfc
                        );
                });

            /*
             * Actualizamos TODAS las filas ya existentes,
             * conservando el proveedor que cada una tenía seleccionado.
             */
            document.querySelectorAll(
                ".product-provider-select"
            ).forEach(function (select) {
                updateProviderSelect(
                    select,
                    providers
                );
            });

            /*
             * Muy importante:
             * actualizamos también el <template>.
             *
             * De lo contrario, si después pulsas
             * "Agregar proveedor", la fila nueva no conocería
             * al proveedor que acabamos de registrar.
             */
            const templateSelect =
                providerRowTemplate?.content
                    ?.querySelector(
                        ".product-provider-select"
                    );

            if (templateSelect) {
                updateProviderSelect(
                    templateSelect,
                    providers
                );
            }

            if (createdProvider
                && quickProviderTargetList) {

                let targetSelect =
                    Array.from(
                        quickProviderTargetList.querySelectorAll(
                            ".product-provider-select"
                        )
                    ).find(function (select) {
                        return !select.value;
                    });

                /*
                 * Si todas las filas ya tienen un proveedor,
                 * creamos automáticamente una nueva.
                 */
                if (!targetSelect) {
                    addProviderRow(
                        quickProviderTargetList
                    );

                    const selects =
                        quickProviderTargetList.querySelectorAll(
                            ".product-provider-select"
                        );

                    targetSelect =
                        selects[
                        selects.length - 1
                            ];
                }

                if (targetSelect) {
                    targetSelect.value =
                        String(
                            createdProvider.idProvider
                        );

                    clearFieldValidation(
                        targetSelect
                    );

                    targetSelect.dispatchEvent(
                        new Event(
                            "change",
                            {
                                bubbles: true
                            }
                        )
                    );
                }

                refreshProviderOptions(
                    quickProviderTargetList
                );
            }

            return createdProvider || null;
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} providers valor de providers requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateProviderSelect(
            select,
            providers
        ) {
            if (!select) {
                return;
            }

            const previousValue =
                select.value;

            select.replaceChildren(
                createOption(
                    "",
                    "Seleccione un proveedor"
                )
            );

            providers.forEach(function (provider) {
                const option =
                    createOption(
                        provider.idProvider,
                        `${normalizeText(
                            provider.name
                        )} — ${normalizeText(
                            provider.rfc
                        )}`
                    );

                option.dataset.providerName =
                    normalizeText(
                        provider.name
                    );

                option.dataset.providerRfc =
                    normalizeText(
                        provider.rfc
                    );

                select.appendChild(option);
            });

            const previousExists =
                Array.from(select.options)
                    .some(function (option) {
                        return option.value
                            === previousValue;
                    });

            select.value =
                previousExists
                    ? previousValue
                    : "";
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} metrics valor de metrics requerido por la función
         * @param {*} selectedId valor de selectedId requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateMetricSelect(
            select,
            metrics,
            selectedId = null
        ) {
            if (!select) {
                return;
            }

            const previousValue = select.value;

            const finalSelectedValue =
                selectedId != null
                    ? String(selectedId)
                    : previousValue;

            select.replaceChildren(
                createOption(
                    "",
                    "Seleccione una unidad"
                )
            );

            metrics.forEach(function (metric) {
                select.appendChild(
                    createOption(
                        metric.idMetric,
                        `${normalizeText(
                            metric.name
                        )} (${normalizeText(
                            metric.shortName
                        )})`
                    )
                );
            });

            const optionExists =
                Array.from(select.options)
                    .some(function (option) {
                        return option.value
                            === finalSelectedValue;
                    });

            select.value = optionExists
                ? finalSelectedValue
                : "";

            if (selectedId != null) {
                clearFieldValidation(select);

                select.dispatchEvent(
                    new Event(
                        "change",
                        {bubbles: true}
                    )
                );
            }
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} metrics valor de metrics requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateMetricFilter(
            select,
            metrics
        ) {
            if (!select) {
                return;
            }

            const previousValue = select.value;

            select.replaceChildren(
                createOption("all", "Todas")
            );

            metrics.forEach(function (metric) {
                select.appendChild(
                    createOption(
                        metric.idMetric,
                        `${normalizeText(
                            metric.name
                        )} (${normalizeText(
                            metric.shortName
                        )})`
                    )
                );
            });

            const optionExists =
                Array.from(select.options)
                    .some(function (option) {
                        return option.value
                            === previousValue;
                    });

            select.value = optionExists
                ? previousValue
                : "all";
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
        function createOption(value, text) {
            const option =
                document.createElement("option");

            option.value = String(value);
            option.textContent = text;

            return option;
        }

        /* ======================================================
           ESTADOS VISUALES
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
        function updateTableVisibility(totalRows) {
            const responsive =
                table.closest(".table-responsive");

            const pagination =
                document.querySelector(
                    `.table-pagination[data-table-target="${TABLE_ID}"]`
                );

            const generalEmptyState =
                document.getElementById(
                    "productsGeneralEmptyState"
                );

            const filterEmptyState =
                document.getElementById(
                    "productsFilterEmptyState"
                );

            if (responsive) {
                responsive.style.display =
                    totalRows > 0 ? "" : "none";
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
           FORMULARIOS
           ====================================================== */

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function validateForm(form) {
            if (!form) {
                return false;
            }

            if (window.Form
                && typeof Form.validate
                === "function") {
                return Form.validate(form);
            }

            const valid =
                form.checkValidity();

            form.classList.toggle(
                "was-validated",
                !valid
            );

            return valid;
        }

        /**
         * Ejecuta la operación resetForm del módulo de interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @param {*} resetValues valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function resetForm(form, resetValues) {
            if (!form) {
                return;
            }

            if (window.Form
                && typeof Form.reset
                === "function") {

                Form.reset(form, {
                    resetValues:
                        Boolean(resetValues),
                    unlock: true
                });

                return;
            }

            if (resetValues) {
                HTMLFormElement.prototype
                    .reset.call(form);
            }

            form.classList.remove(
                "was-validated"
            );

            form.querySelectorAll(
                ".is-valid, .is-invalid"
            ).forEach(clearFieldValidation);
        }

        /**
         * Retira o limpia la información indicada de la interfaz.
         *
         * @param {*} field valor de field requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function clearFieldValidation(field) {
            if (!field) {
                return;
            }

            field.classList.remove(
                "is-valid",
                "is-invalid"
            );

            field.removeAttribute(
                "aria-invalid"
            );
        }

        /**
         * Ejecuta la operación markFieldInvalid del módulo de interfaz.
         *
         * @param {*} field valor de field requerido por la función
         * @param {*} message valor de message requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function markFieldInvalid(
            field,
            message
        ) {
            if (!field) {
                return;
            }

            field.classList.remove("is-valid");
            field.classList.add("is-invalid");

            field.setAttribute(
                "aria-invalid",
                "true"
            );

            const formField =
                field.closest(".form-field");

            const feedback =
                formField?.querySelector(
                    ".invalid-feedback"
                );

            if (feedback) {
                feedback.textContent = message;
            }
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} form valor de form requerido por la función
         * @param {*} button valor de button requerido por la función
         * @param {*} loading valor de loading requerido por la función
         * @param {*} loadingText valor de loadingText requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setFormLoading(
            form,
            button,
            loading,
            loadingText
        ) {
            if (window.Form) {
                if (typeof Form.loading
                    === "function") {

                    Form.loading(
                        button,
                        loading,
                        loadingText
                    );
                }

                if (loading
                    && typeof Form.lock
                    === "function") {
                    Form.lock(form);
                }

                if (!loading
                    && typeof Form.unlock
                    === "function") {
                    Form.unlock(form);
                }

                return;
            }

            if (button) {
                button.disabled = loading;
            }
        }

        /* ======================================================
           TOAST Y ERRORES
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
        function showToast(result) {
            if (window.AppToast
                && typeof AppToast.fromResponse
                === "function") {

                AppToast.fromResponse(result);
                return;
            }

            if (window.AppToast
                && typeof AppToast.show
                === "function") {

                AppToast.show(
                    result.message
                    || "Operación realizada.",
                    result.type
                    || (
                        result.success
                            ? "success"
                            : "error"
                    )
                );

                return;
            }

            console.log(result.message);
        }

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
        function showWarning(message) {
            if (window.AppToast
                && typeof AppToast.warning
                === "function") {

                AppToast.warning(message);
                return;
            }

            window.alert(message);
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

            if (error.data
                && typeof error.data
                === "object") {

                showToast(error.data);
                return;
            }

            const message =
                error.message
                || "No fue posible completar la operación.";

            if (window.AppToast
                && typeof AppToast.error
                === "function") {

                AppToast.error(message);
                return;
            }

            window.alert(message);
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

            if (!element
                || typeof bootstrap
                === "undefined"
                || !bootstrap.Modal) {
                return null;
            }

            return bootstrap.Modal
                .getOrCreateInstance(element);
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function isPositiveInteger(value) {
            const normalizedValue =
                normalizeText(value);

            return /^\d+$/.test(normalizedValue)
                && Number(normalizedValue) > 0;
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function isValidStatus(value) {
            const normalizedValue =
                normalizeText(value);

            return normalizedValue === "0"
                || normalizedValue === "1";
        }

        /**
         * Ejecuta la operación normalizeId del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeId(value) {
            const id = Number(value);

            return Number.isInteger(id)
            && id > 0
                ? id
                : 0;
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
         * Ejecuta la operación normalizeDecimal del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeDecimal(value) {
            return normalizeText(value)
                .replace(",", ".");
        }

        /**
         * Ejecuta la operación normalizeComparable del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeComparable(value) {
            return normalizeText(value)
                .toLocaleLowerCase("es-MX");
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
        function formatDecimalInput(value) {
            const number = Number(
                normalizeDecimal(value)
            );

            return Number.isFinite(number)
                ? number.toFixed(2)
                : "";
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
            const number = Number(
                normalizeDecimal(value)
            );

            return new Intl.NumberFormat(
                "es-MX",
                {
                    style: "currency",
                    currency: "MXN",
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                }
            ).format(
                Number.isFinite(number)
                    ? number
                    : 0
            );
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
         * @param {*} id identificador del registro o componente
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setValue(id, value) {
            const element =
                document.getElementById(id);

            if (element) {
                element.value =
                    value == null
                        ? ""
                        : String(value);
            }
        }

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
        function getValue(id) {
            const element =
                document.getElementById(id);

            return element
                ? normalizeText(element.value)
                : "";
        }
    });
})();
