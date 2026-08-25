/**
 * ==========================================================
 * MÓDULO: PROVEEDORES
 * ==========================================================
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */

(function () {
    "use strict";

    if (window.providerModuleInitialized) {
        return;
    }

    window.providerModuleInitialized = true;

    document.addEventListener("DOMContentLoaded", function () {
        const TABLE_ID = "providersTable";
        const table = document.getElementById(TABLE_ID);
        const tableBody = table?.querySelector("tbody");
        const formCreate = document.getElementById("formCreateProvider");
        const formEdit = document.getElementById("formEditProvider");
        const formChangeStatus = document.getElementById("formChangeStatus");

        const btnNuevoProveedor = document.getElementById("btnNuevoProveedor");
        const btnOpenConfirmEdit = document.getElementById("btnOpenConfirmEdit");
        const btnConfirmEdit = document.getElementById("btnConfirmEdit");
        const btnConfirmStatus = document.getElementById("btnConfirmStatus");
        const createSubmitButton = document.querySelector(
            '[type="submit"][form="formCreateProvider"]'
        );

        const modalCreate = getModal("modalCreate");
        const modalView = getModal("modalView");
        const modalEdit = getModal("modalEdit");
        const modalConfirmEdit = getModal("modalConfirmEdit");
        const modalConfirmStatus = getModal("modalConfirmStatus");

        if (!table || !tableBody) {
            console.warn("No se encontró la tabla de proveedores.");
            return;
        }

        if (!window.Api) {
            console.error("api.js no está disponible.");
            return;
        }

        /* ======================================================
           REGISTRAR
           ====================================================== */

        btnNuevoProveedor?.addEventListener("click", function () {
            resetForm(formCreate, true);
            modalCreate?.show();
        });

        formCreate?.addEventListener("submit", async function (event) {
            event.preventDefault();
            event.stopImmediatePropagation();

            if (!validateForm(formCreate)
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
                await loadProviders();
            } catch (error) {
                handleRequestError(error);
            } finally {
                delete formCreate.dataset.fetchSubmitting;
                setFormLoading(formCreate, createSubmitButton, false);
            }
        });

        /* ======================================================
           ACTUALIZAR
           ====================================================== */

        btnOpenConfirmEdit?.addEventListener("click", function () {
            if (!validateForm(formEdit)) {
                return;
            }

            setText("editConfirmProviderName", getValue("editName") || "-");
            modalConfirmEdit?.show();
        });

        btnConfirmEdit?.addEventListener("click", async function () {
            if (!formEdit
                || btnConfirmEdit.disabled
                || formEdit.dataset.fetchSubmitting === "true") {
                return;
            }

            if (!validateForm(formEdit)) {
                modalConfirmEdit?.hide();
                return;
            }

            if (!isPositiveInteger(getValue("editProviderId"))) {
                showWarning("No se pudo determinar el proveedor que deseas actualizar.");
                modalConfirmEdit?.hide();
                return;
            }

            formEdit.dataset.fetchSubmitting = "true";
            setFormLoading(formEdit, btnConfirmEdit, true, "Actualizando...");

            try {
                const result = await Api.submitForm(formEdit);
                showToast(result);

                if (!result.success) {
                    return;
                }

                modalConfirmEdit?.hide();
                modalEdit?.hide();
                resetForm(formEdit, false);
                await loadProviders();
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

            const providerId = getValue("statusProviderId");
            const newStatus = getValue("statusNewValue");

            if (!isPositiveInteger(providerId)) {
                showWarning("No se pudo determinar el proveedor.");
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
                await loadProviders();
            } catch (error) {
                handleRequestError(error);
            } finally {
                delete formChangeStatus.dataset.fetchSubmitting;
                setFormLoading(formChangeStatus, btnConfirmStatus, false);
            }
        });

        /* ======================================================
           EVENTOS DE LA TABLA
           ====================================================== */

        tableBody.addEventListener("click", function (event) {
            const button = event.target.closest(".table-action-btn");

            if (!button) {
                return;
            }

            const row = button.closest(".provider-table-row");

            if (!row) {
                return;
            }

            event.preventDefault();
            event.stopPropagation();

            if (button.classList.contains("btn-view-provider")) {
                openViewModal(row);
                return;
            }

            if (button.classList.contains("btn-edit-provider")) {
                openEditModal(row);
                return;
            }

            if (button.classList.contains("btn-change-status")) {
                openStatusModal(button, row);
            }
        });

        /* ======================================================
           CONSULTAR Y RECONSTRUIR LA TABLA
           ====================================================== */

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadProviders() {
            const result = await Api.get("/providers/list");

            if (!result.success) {
                throw new Api.ApiError(
                    result.message || "No fue posible consultar los proveedores.",
                    400,
                    result
                );
            }

            renderProviders(
                Array.isArray(result.data)
                    ? result.data
                    : []
            );
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
        function renderProviders(providers) {
            tableBody.replaceChildren();

            providers.forEach(function (provider) {
                tableBody.appendChild(createProviderRow(provider));
            });

            updateTableVisibility(providers.length);

            if (typeof window.filterTable === "function") {
                window.filterTable(TABLE_ID);
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} provider valor de provider requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createProviderRow(provider) {
            const id = normalizeId(provider.idProvider);
            const name = normalizeText(provider.name);
            const socialCase = normalizeText(provider.socialCase);
            const rfc = normalizeText(provider.rfc);
            const phone = normalizeText(provider.phone);
            const email = normalizeText(provider.email);
            const address = normalizeText(provider.address);
            const postCode = normalizeText(provider.postCode);
            const contactName = normalizeText(provider.contactName);
            const contactPhone = normalizeText(provider.contactPhone);
            const contactEmail = normalizeText(provider.contactEmail);
            const active = Number(provider.status) === 1;

            const row = document.createElement("tr");
            row.className = "js-table-row provider-table-row";
            row.dataset.id = String(id);
            row.dataset.name = name;
            row.dataset.socialCase = socialCase;
            row.dataset.rfc = rfc;
            row.dataset.phone = phone;
            row.dataset.email = email;
            row.dataset.address = address;
            row.dataset.postCode = postCode;
            row.dataset.contactName = contactName;
            row.dataset.contactPhone = contactPhone;
            row.dataset.contactEmail = contactEmail;
            row.dataset.status = active ? "active" : "inactive";
            row.dataset.search = [
                name,
                socialCase,
                rfc,
                phone,
                email,
                contactName,
                contactPhone,
                contactEmail
            ].join(" ");

            const idCell = document.createElement("td");
            idCell.className = "table-cell-secondary table-cell-nowrap";
            idCell.textContent = String(id);

            const providerCell = document.createElement("td");
            const providerName = document.createElement("div");
            const providerSocialCase = document.createElement("div");

            providerName.className = "table-cell-primary";
            providerName.textContent = name;
            providerSocialCase.className = "table-cell-secondary";
            providerSocialCase.textContent = socialCase;
            providerCell.append(providerName, providerSocialCase);

            const rfcCell = document.createElement("td");
            rfcCell.className = "table-cell-primary table-cell-nowrap";
            rfcCell.textContent = rfc;

            const contactCell = document.createElement("td");

            if (contactName) {
                const contactNameElement = document.createElement("div");
                const contactPhoneElement = document.createElement("div");

                contactNameElement.className = "table-cell-primary";
                contactNameElement.textContent = contactName;
                contactPhoneElement.className = "table-cell-secondary";
                contactPhoneElement.textContent = contactPhone || "Sin teléfono";

                contactCell.append(contactNameElement, contactPhoneElement);
            } else {
                const noContact = document.createElement("span");
                noContact.className = "table-cell-secondary";
                noContact.textContent = "Sin contacto";
                contactCell.appendChild(noContact);
            }

            const phoneCell = document.createElement("td");
            phoneCell.className = "table-cell-secondary table-cell-nowrap";
            phoneCell.textContent = phone || "Sin teléfono";

            const emailCell = document.createElement("td");
            emailCell.className = "table-cell-secondary";
            emailCell.textContent = email || "Sin correo";

            const statusCell = document.createElement("td");
            const statusBadge = document.createElement("span");

            statusBadge.className = active
                ? "table-badge table-badge-success"
                : "table-badge table-badge-danger";
            statusBadge.textContent = active ? "Activo" : "Inactivo";
            statusCell.appendChild(statusBadge);

            const actionsCell = document.createElement("td");
            const actionsContainer = document.createElement("div");
            actionsContainer.className = "table-actions";

            actionsContainer.append(
                createActionButton({
                    className: "table-action-btn table-action-view btn-view-provider",
                    title: "Ver detalles",
                    icon: "bi bi-eye"
                }),
                createActionButton({
                    className: "table-action-btn table-action-edit btn-edit-provider",
                    title: "Editar proveedor",
                    icon: "bi bi-pencil"
                }),
                createStatusButton({
                    id,
                    name,
                    active
                })
            );

            actionsCell.appendChild(actionsContainer);

            row.append(
                idCell,
                providerCell,
                rfcCell,
                contactCell,
                phoneCell,
                emailCell,
                statusCell,
                actionsCell
            );

            return row;
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
            const button = document.createElement("button");
            const icon = document.createElement("i");

            button.type = "button";
            button.className = config.className;
            button.title = config.title;
            button.setAttribute("aria-label", config.title);

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
                    "table-action-btn btn-change-status " +
                    (
                        config.active
                            ? "table-action-delete"
                            : "table-action-success"
                    ),
                title:
                    config.active
                        ? "Desactivar proveedor"
                        : "Activar proveedor",
                icon:
                    config.active
                        ? "bi bi-toggle-on"
                        : "bi bi-toggle-off"
            });

            button.dataset.providerId = String(config.id);
            button.dataset.providerName = config.name;
            button.dataset.newStatus = config.active ? "0" : "1";

            return button;
        }

        /* ======================================================
           MODAL DE DETALLES
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
            const active = row.dataset.status === "active";

            setText("viewProviderName", row.dataset.name || "-");
            setText(
                "viewProviderSocialCase",
                row.dataset.socialCase || "Sin razón social"
            );
            setText("viewProviderRfc", row.dataset.rfc || "-");
            setText(
                "viewProviderPhone",
                row.dataset.phone || "Sin teléfono"
            );
            setText(
                "viewProviderEmail",
                row.dataset.email || "Sin correo"
            );
            setText(
                "viewProviderAddress",
                row.dataset.address || "Sin dirección"
            );
            setText(
                "viewProviderPostCode",
                row.dataset.postCode || "Sin código postal"
            );
            setText(
                "viewProviderContactName",
                row.dataset.contactName || "Sin contacto"
            );
            setText(
                "viewProviderContactPhone",
                row.dataset.contactPhone || "Sin teléfono"
            );
            setText(
                "viewProviderContactEmail",
                row.dataset.contactEmail || "Sin correo"
            );

            const badge = document.getElementById("viewProviderStatus");

            if (badge) {
                badge.textContent = active ? "Activo" : "Inactivo";
                badge.className =
                    "table-badge " +
                    (
                        active
                            ? "table-badge-success"
                            : "table-badge-danger"
                    );
            }

            modalView?.show();
        }

        /* ======================================================
           MODAL DE EDICIÓN
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
        function openEditModal(row) {
            resetForm(formEdit, false);

            setValue("editProviderId", row.dataset.id);
            setValue("editName", row.dataset.name);
            setValue("editSocialCase", row.dataset.socialCase);
            setValue("editRfc", row.dataset.rfc);
            setValue("editPhone", row.dataset.phone);
            setValue("editEmail", row.dataset.email);
            setValue("editAddress", row.dataset.address);
            setValue("editPostCode", row.dataset.postCode);
            setValue("editContactName", row.dataset.contactName);
            setValue("editContactPhone", row.dataset.contactPhone);
            setValue("editContactEmail", row.dataset.contactEmail);
            setText(
                "editConfirmProviderName",
                row.dataset.name || "-"
            );

            modalEdit?.show();
        }

        /* ======================================================
           MODAL DE CAMBIO DE ESTADO
           ====================================================== */

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
            const providerId = normalizeText(
                button.dataset.providerId || row.dataset.id
            );
            const providerName = normalizeText(
                button.dataset.providerName || row.dataset.name
            );
            const newStatus = normalizeText(button.dataset.newStatus);

            if (!isPositiveInteger(providerId) || !isValidStatus(newStatus)) {
                showWarning(
                    "No se pudo determinar el proveedor o el nuevo estado."
                );
                return;
            }

            const activating = newStatus === "1";

            setValue("statusProviderId", providerId);
            setValue("statusNewValue", newStatus);
            setText(
                "statusConfirmProviderName",
                providerName || "Proveedor"
            );
            setText(
                "statusModalQuestion",
                activating
                    ? "¿Deseas activar este proveedor?"
                    : "¿Deseas desactivar este proveedor?"
            );
            setText(
                "statusModalDescription",
                activating
                    ? "El proveedor volverá a estar disponible para nuevas entradas."
                    : "El proveedor dejará de estar disponible para nuevas entradas."
            );
            setText(
                "statusConfirmButtonText",
                activating ? "Activar" : "Desactivar"
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
            const modalIcon = document.getElementById("statusModalIcon");
            const headerIcon = document.getElementById(
                "statusModalHeaderIcon"
            );
            const confirmIcon = document.getElementById(
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
                        ? "bi bi-truck-front-fill"
                        : "bi bi-truck-front";
                modalIcon.style.color =
                    activating ? "#57d38c" : "#ff6666";
            }

            if (headerIcon) {
                headerIcon.className =
                    activating
                        ? "bi bi-check-circle-fill me-2"
                        : "bi bi-exclamation-triangle-fill me-2";
                headerIcon.style.color =
                    activating ? "#57d38c" : "#ff6666";
            }

            if (confirmIcon) {
                confirmIcon.className =
                    activating
                        ? "bi bi-check-circle me-1"
                        : "bi bi-x-circle me-1";
            }
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
            const responsive = table.closest(".table-responsive");
            const pagination = document.querySelector(
                `.table-pagination[data-table-target="${TABLE_ID}"]`
            );
            const generalEmptyState = document.getElementById(
                "providersGeneralEmptyState"
            );
            const filterEmptyState = document.getElementById(
                "providersFilterEmptyState"
            );

            if (responsive) {
                responsive.style.display = totalRows > 0 ? "" : "none";
            }

            if (pagination) {
                pagination.style.display =
                    totalRows > 0 ? "grid" : "none";
            }

            if (generalEmptyState) {
                generalEmptyState.style.display =
                    totalRows === 0 ? "block" : "none";
            }

            if (filterEmptyState) {
                filterEmptyState.style.display = "none";
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

            if (window.Form && typeof Form.validate === "function") {
                return Form.validate(form);
            }

            const valid = form.checkValidity();
            form.classList.toggle("was-validated", !valid);

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

            if (window.Form && typeof Form.reset === "function") {
                Form.reset(form, {
                    resetValues: Boolean(resetValues),
                    unlock: true
                });
                return;
            }

            if (resetValues) {
                HTMLFormElement.prototype.reset.call(form);
            }

            form.classList.remove("was-validated");

            form.querySelectorAll(".is-valid, .is-invalid")
                .forEach(function (field) {
                    field.classList.remove("is-valid", "is-invalid");
                    field.removeAttribute("aria-invalid");
                });
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
        function setFormLoading(form, button, loading, loadingText) {
            if (window.Form) {
                if (typeof Form.loading === "function") {
                    Form.loading(button, loading, loadingText);
                }

                if (loading && typeof Form.lock === "function") {
                    Form.lock(form);
                }

                if (!loading && typeof Form.unlock === "function") {
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
            if (
                window.AppToast &&
                typeof AppToast.fromResponse === "function"
            ) {
                AppToast.fromResponse(result);
                return;
            }

            if (
                window.AppToast &&
                typeof AppToast.show === "function"
            ) {
                AppToast.show(
                    result.message || "Operación realizada.",
                    result.type || (result.success ? "success" : "error")
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
            if (
                window.AppToast &&
                typeof AppToast.warning === "function"
            ) {
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

            if (error.data && typeof error.data === "object") {
                showToast(error.data);
                return;
            }

            const message =
                error.message ||
                "No fue posible completar la operación.";

            if (
                window.AppToast &&
                typeof AppToast.error === "function"
            ) {
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
            const element = document.getElementById(id);

            if (
                !element ||
                typeof bootstrap === "undefined" ||
                !bootstrap.Modal
            ) {
                return null;
            }

            return bootstrap.Modal.getOrCreateInstance(element);
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
            const normalizedValue = normalizeText(value);

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
            const normalizedValue = normalizeText(value);

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

            return Number.isInteger(id) && id > 0
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
            const element = document.getElementById(id);

            if (element) {
                element.textContent =
                    value == null ? "" : String(value);
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
            const element = document.getElementById(id);

            if (element) {
                element.value =
                    value == null ? "" : String(value);
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
            const element = document.getElementById(id);

            return element
                ? normalizeText(element.value)
                : "";
        }
    });
})();
