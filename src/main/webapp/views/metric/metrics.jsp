<%--
    Vista técnica: metrics.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Unidades de Medida</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-sidebar.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/table.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/modals.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/form.css">
</head>

<body data-context-path="${pageContext.request.contextPath}">
<jsp:include page="/components/theme-toggle.jsp"/>

<div class="d-flex">
  <jsp:include page="/components/sidebar.jsp"/>

  <main class="content p-3 p-md-4">
    <button type="button" id="btnOpenSidebar" class="sidebar-open-button" aria-label="Abrir menú">
      <i class="bi bi-list"></i>
      <span>Menú</span>
    </button>

    <div class="container-fluid px-0">
      <!-- Encabezado -->
      <div class="table-page-header">
        <h2 class="table-page-title">
          <i class="bi bi-rulers"></i>
          Unidades de Medida
        </h2>

        <div class="table-header-actions">
          <button type="button" class="table-primary-btn" id="btnNuevaUnidad">
            <i class="bi bi-plus-lg"></i>
            Registrar
          </button>
        </div>
      </div>

      <!-- Búsqueda y filtros -->
      <section class="table-toolbar">
        <div class="table-toolbar-top">
          <div class="table-search">
            <i class="bi bi-search"></i>
            <input type="search" class="js-table-search" data-table-target="metricsTable"
                   placeholder="Buscar por nombre o abreviatura..." autocomplete="off">
          </div>

          <button type="button" class="table-toolbar-btn js-filter-toggle"
                  data-filter-target="metricsFilters">
            <i class="bi bi-funnel"></i>
            <span>Filtros</span>
          </button>

          <button type="button" class="table-toolbar-btn js-clear-filters"
                  data-table-target="metricsTable">
            <i class="bi bi-eraser"></i>
            <span>Limpiar</span>
          </button>
        </div>

        <div class="table-filters" id="metricsFilters" style="--filter-columns: 1;">
          <div class="table-filter-group">
            <span class="table-filter-label">Estado</span>

            <div class="table-filter-options">
              <input type="radio" id="metricStatusAll" name="metricStatusFilter" value="all"
                     class="js-table-filter" data-table-target="metricsTable"
                     data-filter-field="status" checked>
              <label for="metricStatusAll" class="table-filter-option">Todos</label>

              <input type="radio" id="metricStatusActive" name="metricStatusFilter" value="active"
                     class="js-table-filter" data-table-target="metricsTable"
                     data-filter-field="status">
              <label for="metricStatusActive" class="table-filter-option">Activos</label>

              <input type="radio" id="metricStatusInactive" name="metricStatusFilter" value="inactive"
                     class="js-table-filter" data-table-target="metricsTable"
                     data-filter-field="status">
              <label for="metricStatusInactive" class="table-filter-option">Inactivos</label>
            </div>
          </div>
        </div>
      </section>

      <!-- Tabla -->
      <section class="table-panel">
        <div class="table-responsive" style="${empty metrics ? 'display:none;' : ''}">
          <table class="app-table" id="metricsTable" style="--table-min-width: 720px;">
            <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Abreviatura</th>
              <th>Estado</th>
              <th class="table-text-center">Acciones</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="metric" items="${metrics}">
              <tr class="js-table-row metric-table-row"
                  data-id="${metric.idMetric}"
                  data-name="${fn:escapeXml(metric.name)}"
                  data-short-name="${fn:escapeXml(metric.shortName)}"
                  data-status="${metric.status == 1 ? 'active' : 'inactive'}"
                  data-search="${fn:escapeXml(metric.name)} ${fn:escapeXml(metric.shortName)}">

                <td class="table-cell-secondary table-cell-nowrap">
                    ${metric.idMetric}
                </td>

                <td class="table-cell-primary">
                    ${metric.name}
                </td>

                <td>
                                    <span class="table-badge table-badge-primary">
                                        ${metric.shortName}
                                    </span>
                </td>

                <td>
                                    <span class="table-badge ${metric.status == 1
                                            ? 'table-badge-success'
                                            : 'table-badge-danger'}">
                                        ${metric.status == 1 ? 'Activo' : 'Inactivo'}
                                    </span>
                </td>

                <td>
                  <div class="table-actions">
                    <button type="button"
                            class="table-action-btn table-action-view btn-view-metric"
                            title="Ver detalles" data-bs-toggle="modal"
                            data-bs-target="#modalView">
                      <i class="bi bi-eye"></i>
                    </button>

                    <button type="button"
                            class="table-action-btn table-action-edit btn-edit-metric"
                            title="Editar unidad" data-bs-toggle="modal"
                            data-bs-target="#modalEdit">
                      <i class="bi bi-pencil"></i>
                    </button>

                    <button type="button"
                            class="table-action-btn btn-change-status
                                                ${metric.status == 1
                                                ? 'table-action-delete'
                                                : 'table-action-success'}"
                            title="${metric.status == 1
                                                ? 'Desactivar unidad'
                                                : 'Activar unidad'}"
                            data-metric-id="${metric.idMetric}"
                            data-metric-name="${fn:escapeXml(metric.name)}"
                            data-new-status="${metric.status == 1 ? 0 : 1}">
                      <i class="bi ${metric.status == 1
                                                ? 'bi-toggle-on'
                                                : 'bi-toggle-off'}"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>

        <!-- Paginación -->
        <div class="table-pagination" data-table-target="metricsTable" data-page-size="5"
             style="${empty metrics ? 'display:none;' : ''}">
          <div class="table-pagination-left">
            <label class="table-page-size-label" for="metricsPageSize">Mostrar</label>

            <select id="metricsPageSize" class="table-page-size-select js-page-size"
                    data-table-target="metricsTable" aria-label="Registros por página">
              <option value="5" selected>5</option>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>

            <span class="table-page-size-text">registros</span>
          </div>

          <div class="table-pagination-info">
            Mostrando <strong class="js-page-start">0</strong>
            a <strong class="js-page-end">0</strong>
            de <strong class="js-page-total">0</strong>
          </div>

          <div class="table-pagination-controls">
            <button type="button" class="table-pagination-button js-page-previous"
                    aria-label="Página anterior" title="Página anterior">
              <i class="bi bi-chevron-left"></i>
            </button>

            <div class="js-page-numbers"></div>

            <button type="button" class="table-pagination-button js-page-next"
                    aria-label="Página siguiente" title="Página siguiente">
              <i class="bi bi-chevron-right"></i>
            </button>
          </div>
        </div>

        <div
                id="metricsGeneralEmptyState"
                class="table-empty-state"
                style="${empty metrics ? 'display:block;' : 'display:none;'}">
          <i class="bi bi-inbox"></i>
          No hay unidades de medida registradas.
        </div>

        <div
                id="metricsFilterEmptyState"
                class="table-empty-state js-filter-empty-state"
                style="display:none;">
          <i class="bi bi-search"></i>
          No se encontraron unidades con esos filtros.
        </div>
      </section>
    </div>
  </main>
</div>

<!-- Modal: registrar -->
<div class="modal fade modal-neumorphic" id="modalCreate" tabindex="-1"
     aria-hidden="true" data-bs-backdrop="static">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-rulers me-2" style="color:#6390ff;"></i>
          Registrar Unidad de Medida
        </h5>

        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <form id="formCreateMetric" class="js-form"
              action="${pageContext.request.contextPath}/metric/save"
              method="post" novalidate data-reset-on-close="true" data-submit-mode="manual">

          <div class="mb-3 form-field">
            <label for="createName" class="form-label">
              Nombre <span class="text-danger required-marker">*</span>
            </label>

            <input type="text" class="form-control js-form-field"
                   id="createName" name="name"
                   value="${param.open == 'create' ? fn:escapeXml(param.name) : ''}"
                   placeholder="Ejemplo: Kilogramo"
                   minlength="2" maxlength="100"
                   data-label="Nombre"
                   data-valid-message="Nombre válido."
                   autocomplete="off" required>

            <div class="valid-feedback">Nombre válido.</div>
            <div class="invalid-feedback"></div>
          </div>

          <div class="mb-3 form-field">
            <label for="createShortName" class="form-label">
              Abreviatura <span class="text-danger required-marker">*</span>
            </label>

            <input type="text" class="form-control js-form-field"
                   id="createShortName" name="shortName"
                   value="${param.open == 'create' ? fn:escapeXml(param.shortName) : ''}"
                   placeholder="Ejemplo: KG"
                   minlength="1" maxlength="10"
                   pattern="[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9.%/\-]{1,10}"
                   data-label="Abreviatura"
                   data-valid-message="Abreviatura válida."
                   autocomplete="off" required>

            <div class="valid-feedback">Abreviatura válida.</div>
            <div class="invalid-feedback"></div>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="submit" class="btn btn-primary js-form-submit"
                form="formCreateMetric" data-loading-text="Guardando...">
          <i class="bi bi-check-lg me-1"></i>
          Guardar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Modal: detalles -->
<div class="modal fade modal-neumorphic" id="modalView" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-rulers me-2" style="color:#6390ff;"></i>
          Detalles de la Unidad
        </h5>

        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <div class="text-center mb-4">
          <i class="bi bi-rulers" style="font-size:4rem;color:var(--text-muted,#718096);"></i>
          <h4 id="viewMetricName" class="mt-2"
              style="color:var(--text-color,#2d3748);">-</h4>
          <span id="viewMetricStatus" class="table-badge">-</span>
        </div>

        <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-hash me-2"></i>ID
                    </span>
          <span class="modal-detail-value" id="viewMetricId">-</span>
        </div>

        <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-fonts me-2"></i>Abreviatura
                    </span>
          <span class="modal-detail-value" id="viewMetricShortName">-</span>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cerrar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Modal: editar -->
<div class="modal fade modal-neumorphic" id="modalEdit" tabindex="-1"
     aria-hidden="true" data-bs-backdrop="static">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-pencil-fill me-2" style="color:#ffc857;"></i>
          Editar Unidad de Medida
        </h5>

        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <form id="formEditMetric" class="js-form"
              action="${pageContext.request.contextPath}/metric/update"
              method="post" novalidate data-submit-mode="manual"
              data-reset-on-close="false">

          <input type="hidden" id="editMetricId" name="id"
                 value="${param.open == 'edit' ? fn:escapeXml(param.id) : ''}">

          <div class="mb-3 form-field">
            <label for="editName" class="form-label">
              Nombre <span class="text-danger required-marker">*</span>
            </label>

            <input type="text" class="form-control js-form-field"
                   id="editName" name="name"
                   value="${param.open == 'edit' ? fn:escapeXml(param.name) : ''}"
                   placeholder="Ejemplo: Kilogramo"
                   minlength="2" maxlength="100"
                   data-label="Nombre"
                   data-valid-message="Nombre válido."
                   autocomplete="off" required>

            <div class="valid-feedback">Nombre válido.</div>
            <div class="invalid-feedback"></div>
          </div>

          <div class="mb-3 form-field">
            <label for="editShortName" class="form-label">
              Abreviatura <span class="text-danger required-marker">*</span>
            </label>

            <input type="text" class="form-control js-form-field"
                   id="editShortName" name="shortName"
                   value="${param.open == 'edit' ? fn:escapeXml(param.shortName) : ''}"
                   placeholder="Ejemplo: KG"
                   minlength="1" maxlength="10"
                   pattern="[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9.%/\-]{1,10}"
                   data-label="Abreviatura"
                   data-valid-message="Abreviatura válida."
                   autocomplete="off" required>

            <div class="valid-feedback">Abreviatura válida.</div>
            <div class="invalid-feedback"></div>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="button" class="btn btn-primary" id="btnOpenConfirmEdit">
          <i class="bi bi-check-lg me-1"></i>
          Actualizar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Modal: confirmar edición -->
<div class="modal fade modal-neumorphic" id="modalConfirmEdit"
     tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-question-circle-fill me-2" style="color:#ffc857;"></i>
          Confirmar Actualización
        </h5>

        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body text-center py-4">
        <i class="bi bi-pencil-square"
           style="font-size:3rem;color:#ffc857;display:block;margin-bottom:16px;"></i>

        <p style="color:var(--text-color,#2d3748);font-size:16px;font-weight:500;">
          ¿Deseas actualizar la unidad
          <strong id="editConfirmMetricName">-</strong>?
        </p>

        <p style="color:var(--text-muted,#718096);font-size:14px;">
          Los cambios se aplicarán inmediatamente.
        </p>
      </div>

      <div class="modal-footer justify-content-center">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="button" class="btn btn-primary js-form-submit"
                id="btnConfirmEdit" data-loading-text="Actualizando...">
          <i class="bi bi-check-lg me-1"></i>
          Confirmar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- Modal: confirmar estado -->
<div class="modal fade modal-neumorphic" id="modalConfirmStatus"
     tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          <i id="statusModalHeaderIcon"
             class="bi bi-question-circle-fill me-2"
             style="color:#ffc857;"></i>
          Confirmar cambio
        </h5>

        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body text-center py-4">
        <i id="statusModalIcon" class="bi bi-rulers"
           style="display:block;margin-bottom:16px;font-size:3rem;color:#ff6666;"></i>

        <p id="statusModalQuestion"
           style="color:var(--text-color,#2d3748);font-size:16px;font-weight:500;">
          ¿Deseas cambiar el estado de la unidad?
        </p>

        <p style="color:var(--text-muted,#718096);font-size:14px;">
          Unidad:
          <strong id="statusConfirmMetricName">-</strong>
        </p>

        <p id="statusModalDescription"
           style="color:var(--text-muted,#718096);font-size:13px;margin-bottom:0;">
          El cambio se aplicará inmediatamente.
        </p>

        <form id="formChangeStatus" class="js-form"
              action="${pageContext.request.contextPath}/metric/change-status"
              method="post" novalidate
              data-submit-mode="manual"
              data-reset-on-close="false">
          <input type="hidden" id="statusMetricId" name="id">
          <input type="hidden" id="statusNewValue" name="status">
        </form>
      </div>

      <div class="modal-footer justify-content-center">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="button" class="btn" id="btnConfirmStatus">
          <i id="statusConfirmButtonIcon" class="bi bi-check-lg me-1"></i>
          <span id="statusConfirmButtonText">Confirmar</span>
        </button>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/table.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/form.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/toast.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/api.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/metric.js"></script>
</body>
</html>
<%-- Catálogo de unidades de medida con baja lógica. --%>
