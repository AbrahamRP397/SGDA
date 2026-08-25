/**
 * ==========================================================
 * MÓDULO: ÁREAS DE DESTINO
 * ==========================================================
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */

(function () {
    "use strict";

    if (window.areaModuleInitialized) {
        return;
    }

    window.areaModuleInitialized = true;

    document.addEventListener("DOMContentLoaded", function () {
        const TABLE_ID = "areasTable";
        const table = document.getElementById(TABLE_ID);
        const tableBody = table?.querySelector("tbody");
        const formCreate = document.getElementById("formCreateArea");
        const formEdit = document.getElementById("formEditArea");
        const formChangeStatus = document.getElementById("formChangeStatus");
        const btnNuevaArea = document.getElementById("btnNuevaArea");
        const btnOpenConfirmEdit = document.getElementById("btnOpenConfirmEdit");
        const btnConfirmEdit = document.getElementById("btnConfirmEdit");
        const btnConfirmStatus = document.getElementById("btnConfirmStatus");
        const createSubmitButton = document.querySelector(
            '[type="submit"][form="formCreateArea"]'
        );

        const modalCreate = getModal("modalCreate");
        const modalView = getModal("modalView");
        const modalEdit = getModal("modalEdit");
        const modalConfirmEdit = getModal("modalConfirmEdit");
        const modalConfirmStatus = getModal("modalConfirmStatus");

        if (!table || !tableBody) {
            console.warn("No se encontró la tabla de áreas.");
            return;
        }

        /* ======================================================
           REGISTRAR
           ====================================================== */

        btnNuevaArea?.addEventListener("click", function () {
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
                await loadAreas();

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

            setText(
                "editConfirmAreaName",
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

            if (!validateForm(formEdit)) {
                modalConfirmEdit?.hide();
                return;
            }

            if (!isPositiveInteger(getValue("editAreaId"))) {
                AppToast.warning(
                    "No se pudo determinar el área que deseas actualizar."
                );
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
                await loadAreas();

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

            const areaId = getValue("statusAreaId");
            const newStatus = getValue("statusNewValue");

            if (!isPositiveInteger(areaId)) {
                AppToast.warning("No se pudo determinar el área.");
                return;
            }

            if (!isValidStatus(newStatus)) {
                AppToast.warning("No se pudo determinar el nuevo estado.");
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
                await loadAreas();

            } catch (error) {
                handleRequestError(error);

            } finally {
                delete formChangeStatus.dataset.fetchSubmitting;
                setFormLoading(formChangeStatus, btnConfirmStatus, false);
            }
        });

        /* ======================================================
           EVENTOS DE TABLA
           ====================================================== */

        tableBody.addEventListener("click", function (event) {
            const button = event.target.closest(".table-action-btn");

            if (!button) {
                return;
            }

            const row = button.closest(".area-table-row");

            if (!row) {
                return;
            }

            event.preventDefault();
            event.stopPropagation();

            if (button.classList.contains("btn-view-area")) {
                openViewModal(row);
                return;
            }

            if (button.classList.contains("btn-edit-area")) {
                openEditModal(row);
                return;
            }

            if (button.classList.contains("btn-change-status")) {
                openStatusModal(button, row);
            }
        });

        /* ======================================================
           CONSULTAR Y RECONSTRUIR
           ====================================================== */

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadAreas() {
            const result = await Api.get("/areas/list");

            if (!result.success) {
                throw new Api.ApiError(
                    result.message || "No fue posible consultar las áreas.",
                    400,
                    result
                );
            }

            renderAreas(
                Array.isArray(result.data)
                    ? result.data
                    : []
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} areas valor de areas requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderAreas(areas) {
            tableBody.replaceChildren();

            areas.forEach(function (area) {
                tableBody.appendChild(createAreaRow(area));
            });

            updateTableVisibility(areas.length);

            if (typeof window.filterTable === "function") {
                window.filterTable(TABLE_ID);
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} area valor de area requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createAreaRow(area) {
            const id = normalizeId(area.idArea);
            const shortName = normalizeText(area.shortName);
            const name = normalizeText(area.name);
            const description = normalizeText(area.description);
            const active = Number(area.status) === 1;
            const row = document.createElement("tr");

            row.className = "js-table-row area-table-row";
            row.dataset.id = String(id);
            row.dataset.shortName = shortName;
            row.dataset.name = name;
            row.dataset.description = description;
            row.dataset.status = active ? "active" : "inactive";
            row.dataset.search = `${shortName} ${name} ${description}`;

            const idCell = document.createElement("td");
            idCell.className = "table-cell-secondary table-cell-nowrap";
            idCell.textContent = String(id);

            const shortNameCell = document.createElement("td");
            const shortNameBadge = document.createElement("span");
            shortNameBadge.className = "table-badge table-badge-primary";
            shortNameBadge.textContent = shortName;
            shortNameCell.appendChild(shortNameBadge);

            const nameCell = document.createElement("td");
            nameCell.className = "table-cell-primary";
            nameCell.textContent = name;

            const descriptionCell = document.createElement("td");
            descriptionCell.className = "table-cell-secondary";
            descriptionCell.textContent = description || "Sin descripción";

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
                    className:
                        "table-action-btn table-action-view btn-view-area",
                    title: "Ver detalles",
                    icon: "bi bi-eye"
                }),
                createActionButton({
                    className:
                        "table-action-btn table-action-edit btn-edit-area",
                    title: "Editar área",
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
                shortNameCell,
                nameCell,
                descriptionCell,
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
                        ? "Desactivar área"
                        : "Activar área",
                icon:
                    config.active
                        ? "bi bi-toggle-on"
                        : "bi bi-toggle-off"
            });

            button.dataset.areaId = String(config.id);
            button.dataset.areaName = config.name;
            button.dataset.newStatus = config.active ? "0" : "1";

            return button;
        }

        /* ======================================================
           MODALES
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

            setText("viewAreaId", row.dataset.id || "-");
            setText("viewAreaName", row.dataset.name || "-");
            setText("viewAreaShortName", row.dataset.shortName || "-");
            setText(
                "viewAreaDescription",
                row.dataset.description || "Sin descripción"
            );

            const badge = document.getElementById("viewAreaStatus");

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

            setValue("editAreaId", row.dataset.id);
            setValue("editShortName", row.dataset.shortName);
            setValue("editName", row.dataset.name);
            setValue("editDescription", row.dataset.description);
            setText("editConfirmAreaName", row.dataset.name || "-");

            modalEdit?.show();
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
            const areaId = normalizeText(
                button.dataset.areaId || row.dataset.id
            );
            const areaName = normalizeText(
                button.dataset.areaName || row.dataset.name
            );
            const newStatus = normalizeText(button.dataset.newStatus);

            if (!isPositiveInteger(areaId) || !isValidStatus(newStatus)) {
                AppToast.warning(
                    "No se pudo determinar el área o el nuevo estado."
                );
                return;
            }

            const activating = newStatus === "1";

            setValue("statusAreaId", areaId);
            setValue("statusNewValue", newStatus);
            setText("statusConfirmAreaName", areaName || "Área");

            setText(
                "statusModalQuestion",
                activating
                    ? "¿Deseas activar esta área de destino?"
                    : "¿Deseas desactivar esta área de destino?"
            );

            setText(
                "statusModalDescription",
                activating
                    ? "El área volverá a estar disponible para nuevas operaciones."
                    : "El área dejará de estar disponible para nuevas operaciones."
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
                        ? "bi bi-building-check"
                        : "bi bi-building-x";
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
                "areasGeneralEmptyState"
            );
            const filterEmptyState = document.getElementById(
                "areasFilterEmptyState"
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

            console.log(result.message);
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
