/**
 * ==========================================================
 * MÓDULO: UNIDADES DE MEDIDA
 * ==========================================================
 *
 * Responsabilidades:
 *
 * - Registrar unidades mediante fetch.
 * - Actualizar unidades mediante fetch.
 * - Cambiar estado mediante fetch.
 * - Consultar nuevamente todas las unidades.
 * - Reconstruir la tabla sin recargar la página.
 * - Mostrar notificaciones con AppToast.
 *
 * form.js conserva:
 *
 * - Validación.
 * - Mensajes visuales.
 * - Bloqueo de formularios.
 * - Estado de carga de botones.
 * - Reinicio de formularios.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */

(function () {
    "use strict";

    /*
     * Evita que el módulo se inicialice dos veces si
     * metric.js se carga accidentalmente más de una vez.
     */
    if (window.metricModuleInitialized) {
        console.warn("El módulo de métricas ya fue inicializado.");
        return;
    }

    window.metricModuleInitialized = true;

    document.addEventListener("DOMContentLoaded", function () {

        /*
         * ======================================================
         * ELEMENTOS PRINCIPALES
         * ======================================================
         */

        const TABLE_ID =
            "metricsTable";

        const table =
            document.getElementById(TABLE_ID);

        const tableBody =
            table?.querySelector("tbody");

        const formCreate =
            document.getElementById("formCreateMetric");

        const formEdit =
            document.getElementById("formEditMetric");

        const formChangeStatus =
            document.getElementById("formChangeStatus");

        const btnNuevaUnidad =
            document.getElementById("btnNuevaUnidad");

        const btnOpenConfirmEdit =
            document.getElementById("btnOpenConfirmEdit");

        const btnConfirmEdit =
            document.getElementById("btnConfirmEdit");

        const btnConfirmStatus =
            document.getElementById("btnConfirmStatus");

        const createSubmitButton =
            document.querySelector(
                '[type="submit"][form="formCreateMetric"]'
            );

        /*
         * ======================================================
         * MODALES
         * ======================================================
         */

        const modalCreate =
            getModal("modalCreate");

        const modalView =
            getModal("modalView");

        const modalEdit =
            getModal("modalEdit");

        const modalConfirmEdit =
            getModal("modalConfirmEdit");

        const modalConfirmStatus =
            getModal("modalConfirmStatus");

        /*
         * Si la tabla no existe, no continuamos.
         */
        if (!table || !tableBody) {

            console.warn(
                "No se encontró la tabla de unidades de medida."
            );

            return;
        }

        /*
         * ======================================================
         * ABRIR MODAL DE REGISTRO
         * ======================================================
         */

        btnNuevaUnidad?.addEventListener(
            "click",
            function () {

                resetForm(
                    formCreate,
                    true
                );

                modalCreate?.show();
            }
        );

        /*
         * ======================================================
         * REGISTRAR UNIDAD
         * ======================================================
         */

        formCreate?.addEventListener(
            "submit",
            async function (event) {

                /*
                 * Evita el envío tradicional y la recarga.
                 */
                event.preventDefault();

                /*
                 * Impide que otro listener del mismo elemento
                 * intente procesar nuevamente el formulario.
                 */
                event.stopImmediatePropagation();

                if (!validateForm(formCreate)) {
                    return;
                }

                if (
                    createSubmitButton?.disabled ||
                    formCreate.dataset.fetchSubmitting === "true"
                ) {
                    return;
                }

                formCreate.dataset.fetchSubmitting =
                    "true";

                setFormLoading(
                    formCreate,
                    createSubmitButton,
                    true,
                    "Guardando..."
                );

                try {

                    const result =
                        await sendForm(formCreate);

                    showToast(result);

                    if (!result.success) {
                        return;
                    }

                    modalCreate?.hide();

                    resetForm(
                        formCreate,
                        true
                    );

                    await loadMetrics();

                } catch (error) {

                    handleRequestError(error);

                } finally {

                    delete formCreate.dataset
                        .fetchSubmitting;

                    setFormLoading(
                        formCreate,
                        createSubmitButton,
                        false
                    );
                }
            }
        );

        /*
         * ======================================================
         * ABRIR CONFIRMACIÓN DE EDICIÓN
         * ======================================================
         */

        btnOpenConfirmEdit?.addEventListener(
            "click",
            function () {

                if (!validateForm(formEdit)) {
                    return;
                }

                setText(
                    "editConfirmMetricName",
                    getValue("editName") || "-"
                );

                modalConfirmEdit?.show();
            }
        );

        /*
         * ======================================================
         * ACTUALIZAR UNIDAD
         * ======================================================
         */

        btnConfirmEdit?.addEventListener(
            "click",
            async function () {

                if (
                    !formEdit ||
                    btnConfirmEdit.disabled ||
                    formEdit.dataset.fetchSubmitting === "true"
                ) {
                    return;
                }

                if (!validateForm(formEdit)) {

                    modalConfirmEdit?.hide();
                    return;
                }

                const id =
                    getValue("editMetricId");

                if (!isPositiveInteger(id)) {

                    AppToast.warning(
                        "No se pudo determinar la unidad que deseas actualizar."
                    );

                    modalConfirmEdit?.hide();

                    return;
                }

                formEdit.dataset.fetchSubmitting =
                    "true";

                setFormLoading(
                    formEdit,
                    btnConfirmEdit,
                    true,
                    "Actualizando..."
                );

                try {

                    const result =
                        await sendForm(formEdit);

                    showToast(result);

                    if (!result.success) {
                        return;
                    }

                    modalConfirmEdit?.hide();
                    modalEdit?.hide();

                    resetForm(
                        formEdit,
                        false
                    );

                    await loadMetrics();

                } catch (error) {

                    handleRequestError(error);

                } finally {

                    delete formEdit.dataset
                        .fetchSubmitting;

                    setFormLoading(
                        formEdit,
                        btnConfirmEdit,
                        false
                    );
                }
            }
        );

        /*
         * ======================================================
         * CONFIRMAR CAMBIO DE ESTADO
         * ======================================================
         */

        btnConfirmStatus?.addEventListener(
            "click",
            async function () {

                if (
                    !formChangeStatus ||
                    btnConfirmStatus.disabled ||
                    formChangeStatus.dataset.fetchSubmitting === "true"
                ) {
                    return;
                }

                const metricId =
                    getValue("statusMetricId");

                const newStatus =
                    getValue("statusNewValue");

                if (!isPositiveInteger(metricId)) {

                    AppToast.warning(
                        "No se pudo determinar la unidad de medida."
                    );

                    return;
                }

                if (!isValidStatus(newStatus)) {

                    AppToast.warning(
                        "No se pudo determinar el nuevo estado."
                    );

                    return;
                }

                formChangeStatus.dataset.fetchSubmitting =
                    "true";

                setFormLoading(
                    formChangeStatus,
                    btnConfirmStatus,
                    true,
                    "Procesando..."
                );

                try {

                    const result =
                        await sendForm(
                            formChangeStatus
                        );

                    showToast(result);

                    if (!result.success) {
                        return;
                    }

                    modalConfirmStatus?.hide();

                    await loadMetrics();

                } catch (error) {

                    handleRequestError(error);

                } finally {

                    delete formChangeStatus.dataset
                        .fetchSubmitting;

                    setFormLoading(
                        formChangeStatus,
                        btnConfirmStatus,
                        false
                    );
                }
            }
        );

        /*
         * ======================================================
         * EVENTOS DE LA TABLA
         * ======================================================
         *
         * Se utiliza delegación porque el contenido de tbody
         * se reemplaza después de cada consulta.
         */

        tableBody.addEventListener(
            "click",
            function (event) {

                const actionButton =
                    event.target.closest(
                        ".table-action-btn"
                    );

                if (!actionButton) {
                    return;
                }

                const row =
                    actionButton.closest(
                        ".metric-table-row"
                    );

                if (!row) {
                    return;
                }

                /*
                 * Evita que los atributos data-bs-toggle antiguos
                 * abran otra vez el modal desde Bootstrap.
                 *
                 * metric.js será el único que abra los modales.
                 */
                event.preventDefault();
                event.stopPropagation();

                if (
                    actionButton.classList
                        .contains("btn-view-metric")
                ) {

                    openViewModal(row);
                    return;
                }

                if (
                    actionButton.classList
                        .contains("btn-edit-metric")
                ) {

                    openEditModal(row);
                    return;
                }

                if (
                    actionButton.classList
                        .contains("btn-change-status")
                ) {

                    openStatusModal(
                        actionButton,
                        row
                    );
                }
            }
        );

        /*
         * ======================================================
         * CONSULTAR TODAS LAS UNIDADES
         * ======================================================
         */

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadMetrics() {

            const result =
                await Api.get("/metrics/list");

            if (!result.success) {

                throw new Api.ApiError(
                    result.message ||
                    "No fue posible consultar las unidades de medida.",
                    400,
                    result
                );
            }

            const metrics =
                Array.isArray(result.data)
                    ? result.data
                    : [];

            renderMetrics(metrics);
        }

        /*
         * ======================================================
         * RECONSTRUIR TABLA
         * ======================================================
         */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} metrics valor de metrics requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderMetrics(metrics) {

            tableBody.replaceChildren();

            metrics.forEach(
                function (metric) {

                    const row =
                        createMetricRow(metric);

                    tableBody.appendChild(row);
                }
            );

            updateTableVisibility(
                metrics.length
            );

            /*
             * table.js vuelve a aplicar:
             *
             * - Búsqueda.
             * - Filtros.
             * - Paginación.
             */
            if (
                typeof window.filterTable ===
                "function"
            ) {

                window.filterTable(TABLE_ID);
            }
        }

        /*
         * ======================================================
         * CREAR FILA DE LA TABLA
         * ======================================================
         */

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} metric valor de metric requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createMetricRow(metric) {

            const id =
                normalizeId(
                    metric.idMetric
                );

            const name =
                normalizeText(
                    metric.name
                );

            const shortName =
                normalizeText(
                    metric.shortName
                );

            const active =
                Number(metric.status) === 1;

            const row =
                document.createElement("tr");

            row.className =
                "js-table-row metric-table-row";

            row.dataset.id =
                String(id);

            row.dataset.name =
                name;

            row.dataset.shortName =
                shortName;

            row.dataset.status =
                active
                    ? "active"
                    : "inactive";

            row.dataset.search =
                `${name} ${shortName}`;

            /*
             * ID
             */

            const idCell =
                document.createElement("td");

            idCell.className =
                "table-cell-secondary table-cell-nowrap";

            idCell.textContent =
                String(id);

            /*
             * Nombre
             */

            const nameCell =
                document.createElement("td");

            nameCell.className =
                "table-cell-primary";

            nameCell.textContent =
                name;

            /*
             * Abreviatura
             */

            const shortNameCell =
                document.createElement("td");

            const shortNameBadge =
                document.createElement("span");

            shortNameBadge.className =
                "table-badge table-badge-primary";

            shortNameBadge.textContent =
                shortName;

            shortNameCell.appendChild(
                shortNameBadge
            );

            /*
             * Estado
             */

            const statusCell =
                document.createElement("td");

            const statusBadge =
                document.createElement("span");

            statusBadge.className =
                active
                    ? "table-badge table-badge-success"
                    : "table-badge table-badge-danger";

            statusBadge.textContent =
                active
                    ? "Activo"
                    : "Inactivo";

            statusCell.appendChild(
                statusBadge
            );

            /*
             * Acciones
             */

            const actionsCell =
                document.createElement("td");

            const actionsContainer =
                document.createElement("div");

            actionsContainer.className =
                "table-actions";

            const viewButton =
                createActionButton({
                    className:
                        "table-action-btn table-action-view btn-view-metric",

                    title:
                        "Ver detalles",

                    icon:
                        "bi bi-eye"
                });

            const editButton =
                createActionButton({
                    className:
                        "table-action-btn table-action-edit btn-edit-metric",

                    title:
                        "Editar unidad",

                    icon:
                        "bi bi-pencil"
                });

            const statusButton =
                createStatusButton({
                    id,
                    name,
                    active
                });

            actionsContainer.append(
                viewButton,
                editButton,
                statusButton
            );

            actionsCell.appendChild(
                actionsContainer
            );

            row.append(
                idCell,
                nameCell,
                shortNameCell,
                statusCell,
                actionsCell
            );

            return row;
        }

        /*
         * ======================================================
         * CREAR BOTÓN DE ACCIÓN
         * ======================================================
         */

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

            button.type =
                "button";

            button.className =
                config.className;

            button.title =
                config.title;

            button.setAttribute(
                "aria-label",
                config.title
            );

            const icon =
                document.createElement("i");

            icon.className =
                config.icon;

            button.appendChild(icon);

            return button;
        }

        /*
         * ======================================================
         * CREAR BOTÓN DE CAMBIO DE ESTADO
         * ======================================================
         */

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

            const button =
                createActionButton({
                    className:
                        "table-action-btn btn-change-status " +
                        (
                            config.active
                                ? "table-action-delete"
                                : "table-action-success"
                        ),

                    title:
                        config.active
                            ? "Desactivar unidad"
                            : "Activar unidad",

                    icon:
                        config.active
                            ? "bi bi-toggle-on"
                            : "bi bi-toggle-off"
                });

            button.dataset.metricId =
                String(config.id);

            button.dataset.metricName =
                config.name;

            /*
             * Si está activa, el nuevo estado será 0.
             * Si está inactiva, el nuevo estado será 1.
             */
            button.dataset.newStatus =
                config.active
                    ? "0"
                    : "1";

            return button;
        }

        /*
         * ======================================================
         * MODAL DE DETALLES
         * ======================================================
         */

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
                row.dataset.status ===
                "active";

            setText(
                "viewMetricId",
                row.dataset.id || "-"
            );

            setText(
                "viewMetricName",
                row.dataset.name || "-"
            );

            setText(
                "viewMetricShortName",
                row.dataset.shortName || "-"
            );

            const badge =
                document.getElementById(
                    "viewMetricStatus"
                );

            if (badge) {

                badge.textContent =
                    active
                        ? "Activo"
                        : "Inactivo";

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

        /*
         * ======================================================
         * MODAL DE EDICIÓN
         * ======================================================
         */

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

            resetForm(
                formEdit,
                false
            );

            setValue(
                "editMetricId",
                row.dataset.id
            );

            setValue(
                "editName",
                row.dataset.name
            );

            setValue(
                "editShortName",
                row.dataset.shortName
            );

            setText(
                "editConfirmMetricName",
                row.dataset.name || "-"
            );

            modalEdit?.show();
        }

        /*
         * ======================================================
         * MODAL DE CAMBIO DE ESTADO
         * ======================================================
         */

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
        function openStatusModal(
            button,
            row
        ) {

            const metricId =
                normalizeText(
                    button.dataset.metricId ||
                    row.dataset.id
                );

            const metricName =
                normalizeText(
                    button.dataset.metricName ||
                    row.dataset.name
                );

            const newStatus =
                normalizeText(
                    button.dataset.newStatus
                );

            if (!isPositiveInteger(metricId)) {

                AppToast.warning(
                    "No se pudo determinar la unidad de medida."
                );

                return;
            }

            if (!isValidStatus(newStatus)) {

                AppToast.warning(
                    "No se pudo determinar el nuevo estado."
                );

                return;
            }

            const activating =
                newStatus === "1";

            setValue(
                "statusMetricId",
                metricId
            );

            setValue(
                "statusNewValue",
                newStatus
            );

            setText(
                "statusConfirmMetricName",
                metricName || "Unidad"
            );

            setText(
                "statusModalQuestion",
                activating
                    ? "¿Deseas activar esta unidad de medida?"
                    : "¿Deseas desactivar esta unidad de medida?"
            );

            setText(
                "statusModalDescription",
                activating
                    ? "La unidad volverá a estar disponible para nuevos productos."
                    : "La unidad dejará de estar disponible para nuevos productos."
            );

            setText(
                "statusConfirmButtonText",
                activating
                    ? "Activar"
                    : "Desactivar"
            );

            configureStatusButton(
                activating
            );

            configureStatusIcons(
                activating
            );

            modalConfirmStatus?.show();
        }

        /*
         * ======================================================
         * CONFIGURAR BOTÓN DE ESTADO
         * ======================================================
         */

        /**
         * Ejecuta la operación configureStatusButton del módulo de interfaz.
         *
         * @param {*} activating valor de activating requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function configureStatusButton(
            activating
        ) {

            if (!btnConfirmStatus) {
                return;
            }

            btnConfirmStatus.disabled =
                false;

            btnConfirmStatus.className =
                activating
                    ? "btn btn-success"
                    : "btn btn-danger";
        }

        /*
         * ======================================================
         * CONFIGURAR ICONOS DE ESTADO
         * ======================================================
         */

        /**
         * Ejecuta la operación configureStatusIcons del módulo de interfaz.
         *
         * @param {*} activating valor de activating requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function configureStatusIcons(
            activating
        ) {

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

            if (modalIcon) {

                modalIcon.className =
                    "bi bi-rulers";

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

        /*
         * ======================================================
         * ENVIAR FORMULARIO MEDIANTE FETCH
         * ======================================================
         */

        /**
         * Ejecuta la operación sendForm del módulo de interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function sendForm(form) {

            if (
                !window.Api ||
                typeof Api.submitForm !== "function"
            ) {

                throw new Error(
                    "El componente Api no está disponible."
                );
            }

            return Api.submitForm(form);
        }

        /*
         * ======================================================
         * MOSTRAR U OCULTAR TABLA
         * ======================================================
         */

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
                    "metricsGeneralEmptyState"
                );

            const filterEmptyState =
                document.getElementById(
                    "metricsFilterEmptyState"
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

        /*
         * ======================================================
         * VALIDAR FORMULARIO
         * ======================================================
         */

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

            if (
                window.Form &&
                typeof Form.validate ===
                "function"
            ) {

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

        /*
         * ======================================================
         * REINICIAR FORMULARIO
         * ======================================================
         */

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
        function resetForm(
            form,
            resetValues
        ) {

            if (!form) {
                return;
            }

            if (
                window.Form &&
                typeof Form.reset ===
                "function"
            ) {

                Form.reset(
                    form,
                    {
                        resetValues:
                            Boolean(resetValues),

                        unlock:
                            true
                    }
                );

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
            ).forEach(
                function (field) {

                    field.classList.remove(
                        "is-valid",
                        "is-invalid"
                    );

                    field.removeAttribute(
                        "aria-invalid"
                    );
                }
            );
        }

        /*
         * ======================================================
         * ESTADO DE CARGA
         * ======================================================
         */

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

            /*
             * Utilizamos form.js como componente reutilizable.
             */
            if (window.Form) {

                if (
                    typeof Form.loading ===
                    "function"
                ) {

                    Form.loading(
                        button,
                        loading,
                        loadingText
                    );
                }

                if (
                    loading &&
                    typeof Form.lock ===
                    "function"
                ) {

                    Form.lock(form);
                }

                if (
                    !loading &&
                    typeof Form.unlock ===
                    "function"
                ) {

                    Form.unlock(form);
                }

                return;
            }

            /*
             * Alternativa en caso de que form.js no cargue.
             */
            setButtonLoadingFallback(
                button,
                loading,
                loadingText
            );
        }

        /*
         * ======================================================
         * CARGA ALTERNATIVA DEL BOTÓN
         * ======================================================
         */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} button valor de button requerido por la función
         * @param {*} loading valor de loading requerido por la función
         * @param {*} loadingText valor de loadingText requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setButtonLoadingFallback(
            button,
            loading,
            loadingText
        ) {

            if (!button) {
                return;
            }

            if (loading) {

                if (
                    button.dataset
                        .originalContent ===
                    undefined
                ) {

                    button.dataset.originalContent =
                        button.innerHTML;
                }

                button.disabled =
                    true;

                button.setAttribute(
                    "aria-busy",
                    "true"
                );

                button.innerHTML = `
                    <span
                        class="spinner-border spinner-border-sm me-2"
                        aria-hidden="true">
                    </span>

                    <span>
                        ${escapeHtml(
                    loadingText ||
                    "Procesando..."
                )}
                    </span>
                `;

                return;
            }

            button.disabled =
                false;

            button.removeAttribute(
                "aria-busy"
            );

            if (
                button.dataset
                    .originalContent !==
                undefined
            ) {

                button.innerHTML =
                    button.dataset
                        .originalContent;

                delete button.dataset
                    .originalContent;
            }
        }

        /*
         * ======================================================
         * MOSTRAR TOAST
         * ======================================================
         */

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
                typeof AppToast.fromResponse ===
                "function"
            ) {

                AppToast.fromResponse(result);
                return;
            }

            if (
                window.AppToast &&
                typeof AppToast.show ===
                "function"
            ) {

                AppToast.show(
                    result.message ||
                    "Operación realizada.",
                    result.type ||
                    (
                        result.success
                            ? "success"
                            : "error"
                    )
                );

                return;
            }

            console.log(
                result.message
            );
        }

        /*
         * ======================================================
         * MANEJAR ERRORES
         * ======================================================
         */

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

            if (
                Number(error.status) === 401
            ) {

                window.location.href =
                    `${Api.getContextPath()}/login`;

                return;
            }

            const serverResult =
                error.data;

            if (
                serverResult &&
                typeof serverResult === "object"
            ) {

                showToast(serverResult);
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

        /*
         * ======================================================
         * OBTENER MODAL
         * ======================================================
         */

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
                !element ||
                typeof bootstrap ===
                "undefined" ||
                !bootstrap.Modal
            ) {

                return null;
            }

            return bootstrap.Modal
                .getOrCreateInstance(
                    element
                );
        }

        /*
         * ======================================================
         * OBTENER CONTEXTO
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

            const configuredContext =
                document.body.dataset
                    .contextPath;

            if (
                configuredContext !==
                undefined
            ) {

                return configuredContext;
            }

            return "";
        }

        /*
         * ======================================================
         * VALIDACIONES AUXILIARES
         * ======================================================
         */

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

            if (
                !/^\d+$/.test(
                    normalizedValue
                )
            ) {

                return false;
            }

            return Number(normalizedValue) > 0;
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

            return (
                normalizedValue === "0" ||
                normalizedValue === "1"
            );
        }

        /*
         * ======================================================
         * NORMALIZACIÓN
         * ======================================================
         */

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

            const id =
                Number(value);

            return (
                Number.isInteger(id) &&
                id > 0
            )
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

        /*
         * ======================================================
         * TEXTO Y VALORES
         * ======================================================
         */

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
                ? normalizeText(
                    element.value
                )
                : "";
        }

        /*
         * ======================================================
         * ESCAPAR HTML
         * ======================================================
         */

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

            const element =
                document.createElement("div");

            element.textContent =
                value == null
                    ? ""
                    : String(value);

            return element.innerHTML;
        }

        /*
         * ======================================================
         * ERROR PERSONALIZADO
         * ======================================================
         */

        class RequestError extends Error {

            constructor(
                message,
                status,
                response
            ) {

                super(message);

                this.name =
                    "RequestError";

                this.status =
                    status || 0;

                this.response =
                    response || null;
            }
        }
    });

})();
