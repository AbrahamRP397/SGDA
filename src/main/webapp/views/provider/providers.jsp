<%--
    Vista técnica: providers.
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
  <title>Gestión de Proveedores</title>

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

      <!-- ======================================================
           ENCABEZADO
           ====================================================== -->

      <div class="table-page-header">
        <h2 class="table-page-title">
          <i class="bi bi-truck-front-fill"></i>
          Gestión de Proveedores
        </h2>

        <div class="table-header-actions">
          <button type="button" class="table-primary-btn" id="btnNuevoProveedor">
            <i class="bi bi-plus-lg"></i>
            Registrar
          </button>
        </div>
      </div>

      <!-- ======================================================
           BÚSQUEDA Y FILTROS
           ====================================================== -->

      <section class="table-toolbar">
        <div class="table-toolbar-top">
          <div class="table-search">
            <i class="bi bi-search"></i>
            <input type="search"
                   class="js-table-search"
                   data-table-target="providersTable"
                   placeholder="Buscar por nombre, RFC, correo o contacto..."
                   autocomplete="off">
          </div>

          <button type="button"
                  class="table-toolbar-btn js-filter-toggle"
                  data-filter-target="providersFilters">
            <i class="bi bi-funnel"></i>
            <span>Filtros</span>
          </button>

          <button type="button"
                  class="table-toolbar-btn js-clear-filters"
                  data-table-target="providersTable">
            <i class="bi bi-eraser"></i>
            <span>Limpiar</span>
          </button>
        </div>

        <div class="table-filters" id="providersFilters" style="--filter-columns:1;">
          <div class="table-filter-group">
            <span class="table-filter-label">Estado</span>

            <div class="table-filter-options">
              <input type="radio"
                     id="providerStatusAll"
                     name="providerStatusFilter"
                     value="all"
                     class="js-table-filter"
                     data-table-target="providersTable"
                     data-filter-field="status"
                     checked>
              <label for="providerStatusAll" class="table-filter-option">Todos</label>

              <input type="radio"
                     id="providerStatusActive"
                     name="providerStatusFilter"
                     value="active"
                     class="js-table-filter"
                     data-table-target="providersTable"
                     data-filter-field="status">
              <label for="providerStatusActive" class="table-filter-option">Activos</label>

              <input type="radio"
                     id="providerStatusInactive"
                     name="providerStatusFilter"
                     value="inactive"
                     class="js-table-filter"
                     data-table-target="providersTable"
                     data-filter-field="status">
              <label for="providerStatusInactive" class="table-filter-option">Inactivos</label>
            </div>
          </div>
        </div>
      </section>

      <!-- ======================================================
           TABLA
           ====================================================== -->

      <section class="table-panel">
        <div class="table-responsive"
             style="${empty providers ? 'display:none;' : ''}">

          <table class="app-table"
                 id="providersTable"
                 style="--table-min-width:1150px;">

            <thead>
            <tr>
              <th>ID</th>
              <th>Proveedor</th>
              <th>RFC</th>
              <th>Contacto</th>
              <th>Teléfono</th>
              <th>Correo</th>
              <th>Estado</th>
              <th class="table-text-center">Acciones</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="provider" items="${providers}">
              <tr class="js-table-row provider-table-row"
                  data-id="${provider.idProvider}"
                  data-name="${fn:escapeXml(provider.name)}"
                  data-rfc="${fn:escapeXml(provider.rfc)}"
                  data-phone="${fn:escapeXml(provider.phone)}"
                  data-email="${fn:escapeXml(provider.email)}"
                  data-contact-name="${fn:escapeXml(provider.contactName)}"
                  data-address="${fn:escapeXml(provider.address)}"
                  data-post-code="${fn:escapeXml(provider.postCode)}"
                  data-social-case="${fn:escapeXml(provider.socialCase)}"
                  data-contact-phone="${fn:escapeXml(provider.contactPhone)}"
                  data-contact-email="${fn:escapeXml(provider.contactEmail)}"
                  data-status="${provider.status == 1 ? 'active' : 'inactive'}"
                  data-search="${fn:escapeXml(provider.name)}
                                             ${fn:escapeXml(provider.socialCase)}
                                             ${fn:escapeXml(provider.rfc)}
                                             ${fn:escapeXml(provider.phone)}
                                             ${fn:escapeXml(provider.email)}
                                             ${fn:escapeXml(provider.contactName)}
                                             ${fn:escapeXml(provider.contactPhone)}
                                             ${fn:escapeXml(provider.contactEmail)}">

                <td class="table-cell-secondary table-cell-nowrap">
                    ${provider.idProvider}
                </td>

                <td>
                  <div class="table-cell-primary">
                      ${provider.name}
                  </div>

                  <div class="table-cell-secondary">
                      ${provider.socialCase}
                  </div>
                </td>

                <td class="table-cell-primary table-cell-nowrap">
                    ${provider.rfc}
                </td>

                <td>
                  <c:choose>
                    <c:when test="${not empty provider.contactName}">
                      <div class="table-cell-primary">
                          ${provider.contactName}
                      </div>

                      <div class="table-cell-secondary">
                          ${empty provider.contactPhone
                                  ? 'Sin teléfono'
                                  : provider.contactPhone}
                      </div>
                    </c:when>

                    <c:otherwise>
                                            <span class="table-cell-secondary">
                                                Sin contacto
                                            </span>
                    </c:otherwise>
                  </c:choose>
                </td>

                <td class="table-cell-secondary table-cell-nowrap">
                    ${empty provider.phone
                            ? 'Sin teléfono'
                            : provider.phone}
                </td>

                <td class="table-cell-secondary">
                    ${empty provider.email
                            ? 'Sin correo'
                            : provider.email}
                </td>

                <td>
                                    <span class="table-badge ${provider.status == 1
                                            ? 'table-badge-success'
                                            : 'table-badge-danger'}">
                                        ${provider.status == 1 ? 'Activo' : 'Inactivo'}
                                    </span>
                </td>

                <td>
                  <div class="table-actions">
                    <button type="button"
                            class="table-action-btn table-action-view btn-view-provider"
                            title="Ver detalles"
                            aria-label="Ver detalles">
                      <i class="bi bi-eye"></i>
                    </button>

                    <button type="button"
                            class="table-action-btn table-action-edit btn-edit-provider"
                            title="Editar proveedor"
                            aria-label="Editar proveedor">
                      <i class="bi bi-pencil"></i>
                    </button>

                    <button type="button"
                            class="table-action-btn btn-change-status ${provider.status == 1
                                                        ? 'table-action-delete'
                                                        : 'table-action-success'}"
                            title="${provider.status == 1
                                                        ? 'Desactivar proveedor'
                                                        : 'Activar proveedor'}"
                            aria-label="${provider.status == 1
                                                        ? 'Desactivar proveedor'
                                                        : 'Activar proveedor'}"
                            data-provider-id="${provider.idProvider}"
                            data-provider-name="${fn:escapeXml(provider.name)}"
                            data-new-status="${provider.status == 1 ? 0 : 1}">
                      <i class="bi ${provider.status == 1
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

        <!-- ==================================================
             PAGINACIÓN
             ================================================== -->

        <div class="table-pagination"
             data-table-target="providersTable"
             data-page-size="5"
             style="${empty providers ? 'display:none;' : ''}">

          <div class="table-pagination-left">
            <label class="table-page-size-label" for="providersPageSize">
              Mostrar
            </label>

            <select id="providersPageSize"
                    class="table-page-size-select js-page-size"
                    data-table-target="providersTable"
                    aria-label="Registros por página">
              <option value="5" selected>5</option>
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
            </select>

            <span class="table-page-size-text">
                            registros
                        </span>
          </div>

          <div class="table-pagination-info">
            Mostrando
            <strong class="js-page-start">0</strong>
            a
            <strong class="js-page-end">0</strong>
            de
            <strong class="js-page-total">0</strong>
          </div>

          <div class="table-pagination-controls">
            <button type="button"
                    class="table-pagination-button js-page-previous"
                    aria-label="Página anterior"
                    title="Página anterior">
              <i class="bi bi-chevron-left"></i>
            </button>

            <div class="js-page-numbers"></div>

            <button type="button"
                    class="table-pagination-button js-page-next"
                    aria-label="Página siguiente"
                    title="Página siguiente">
              <i class="bi bi-chevron-right"></i>
            </button>
          </div>
        </div>

        <!-- ==================================================
             ESTADOS VACÍOS
             ================================================== -->

        <div id="providersGeneralEmptyState"
             class="table-empty-state"
             style="${empty providers ? 'display:block;' : 'display:none;'}">
          <i class="bi bi-inbox"></i>
          No hay proveedores registrados.
        </div>

        <div id="providersFilterEmptyState"
             class="table-empty-state js-filter-empty-state"
             style="display:none;">
          <i class="bi bi-search"></i>
          No se encontraron proveedores con esos filtros.
        </div>
      </section>
    </div>
  </main>
</div>

<!-- ==========================================================
     MODAL: REGISTRAR PROVEEDOR
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalCreate"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-truck-front-fill me-2"
             style="color:#6390ff;"></i>
          Registrar Proveedor
        </h5>

        <button type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <form id="formCreateProvider"
              class="js-form"
              action="${pageContext.request.contextPath}/provider/save"
              method="post"
              novalidate
              data-reset-on-close="true"
              data-submit-mode="manual">

          <div class="row">

            <div class="col-md-6 mb-3 form-field">
              <label for="createName" class="form-label">
                Nombre comercial
                <span class="text-danger required-marker">*</span>
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="createName"
                     name="name"
                     placeholder="Nombre comercial"
                     minlength="2"
                     maxlength="150"
                     data-label="Nombre comercial"
                     data-valid-message="Nombre válido."
                     autocomplete="off"
                     required>

              <div class="valid-feedback">Nombre válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="createSocialCase" class="form-label">
                Razón social
                <span class="text-danger required-marker">*</span>
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="createSocialCase"
                     name="socialCase"
                     placeholder="Razón social"
                     minlength="2"
                     maxlength="150"
                     data-label="Razón social"
                     data-valid-message="Razón social válida."
                     autocomplete="off"
                     required>

              <div class="valid-feedback">Razón social válida.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="createRfc" class="form-label">
                RFC
                <span class="text-danger required-marker">*</span>
              </label>

              <input type="text"
                     class="form-control js-form-field text-uppercase"
                     id="createRfc"
                     name="rfc"
                     placeholder="Ejemplo: ABC123456XYZ"
                     minlength="12"
                     maxlength="13"
                     pattern="[A-Za-zÑñ&]{3,4}[0-9]{6}[A-Za-z0-9]{3}"
                     data-label="RFC"
                     data-valid-message="RFC válido."
                     autocomplete="off"
                     required>

              <div class="valid-feedback">RFC válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="createPhone" class="form-label">
                Teléfono
                <span class="text-muted small">(opcional)</span>
              </label>

              <input type="tel"
                     class="form-control js-form-field"
                     id="createPhone"
                     name="phone"
                     placeholder="10 dígitos"
                     minlength="10"
                     maxlength="10"
                     pattern="[0-9]{10}"
                     inputmode="numeric"
                     data-label="Teléfono"
                     data-valid-message="Teléfono válido."
                     autocomplete="tel">

              <div class="valid-feedback">Teléfono válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="createEmail" class="form-label">
                Correo del proveedor
                <span class="text-muted small">(opcional)</span>
              </label>

              <input type="email"
                     class="form-control js-form-field"
                     id="createEmail"
                     name="email"
                     placeholder="proveedor@correo.com"
                     maxlength="150"
                     data-label="Correo del proveedor"
                     data-valid-message="Correo válido."
                     autocomplete="email">

              <div class="valid-feedback">Correo válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="createPostCode" class="form-label">
                Código postal
                <span class="text-muted small">(opcional)</span>
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="createPostCode"
                     name="postCode"
                     placeholder="5 dígitos"
                     minlength="5"
                     maxlength="5"
                     pattern="[0-9]{5}"
                     inputmode="numeric"
                     data-label="Código postal"
                     data-valid-message="Código postal válido."
                     autocomplete="postal-code">

              <div class="valid-feedback">Código postal válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-12 mb-3 form-field">
              <label for="createAddress" class="form-label">
                Dirección
                <span class="text-muted small">(opcional)</span>
              </label>

              <textarea class="form-control js-form-field"
                        id="createAddress"
                        name="address"
                        rows="3"
                        maxlength="300"
                        placeholder="Dirección del proveedor"
                        data-label="Dirección"></textarea>

              <div class="valid-feedback">Dirección válida.</div>
              <div class="invalid-feedback"></div>
            </div>
          </div>

          <hr>

          <h6 class="mb-3">
            <i class="bi bi-person-lines-fill me-2"></i>
            Datos del contacto
          </h6>

          <div class="row">

            <div class="col-md-6 mb-3 form-field">
              <label for="createContactName" class="form-label">
                Nombre del contacto
                <span class="text-muted small">(opcional)</span>
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="createContactName"
                     name="contactName"
                     placeholder="Nombre completo"
                     minlength="2"
                     maxlength="150"
                     data-label="Nombre del contacto"
                     data-valid-message="Nombre válido."
                     autocomplete="off">

              <div class="valid-feedback">Nombre válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="createContactPhone" class="form-label">
                Teléfono del contacto
                <span class="text-muted small">(opcional)</span>
              </label>

              <input type="tel"
                     class="form-control js-form-field"
                     id="createContactPhone"
                     name="contactPhone"
                     placeholder="10 dígitos"
                     minlength="10"
                     maxlength="10"
                     pattern="[0-9]{10}"
                     inputmode="numeric"
                     data-label="Teléfono del contacto"
                     data-valid-message="Teléfono válido."
                     autocomplete="tel">

              <div class="valid-feedback">Teléfono válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-12 mb-3 form-field">
              <label for="createContactEmail" class="form-label">
                Correo del contacto
                <span class="text-muted small">(opcional)</span>
              </label>

              <input type="email"
                     class="form-control js-form-field"
                     id="createContactEmail"
                     name="contactEmail"
                     placeholder="contacto@correo.com"
                     maxlength="150"
                     data-label="Correo del contacto"
                     data-valid-message="Correo válido."
                     autocomplete="email">

              <div class="valid-feedback">Correo válido.</div>
              <div class="invalid-feedback"></div>
            </div>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button type="button"
                class="btn btn-secondary"
                data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="submit"
                class="btn btn-primary js-form-submit"
                form="formCreateProvider"
                data-loading-text="Guardando...">
          <i class="bi bi-check-lg me-1"></i>
          Guardar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- ==========================================================
     MODAL: DETALLES
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalView"
     tabindex="-1"
     aria-hidden="true">

  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-truck-front me-2"
             style="color:#6390ff;"></i>
          Detalles del Proveedor
        </h5>

        <button type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <div class="text-center mb-4">
          <i class="bi bi-truck-front"
             style="font-size:4rem;color:var(--text-muted,#718096);"></i>

          <h4 id="viewProviderName"
              class="mt-2"
              style="color:var(--text-color,#2d3748);">
            -
          </h4>

          <span id="viewProviderStatus" class="table-badge">
                        -
                    </span>
        </div>

        <div class="row">

          <div class="col-md-6">
            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-building me-2"></i>
                                Razón social
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderSocialCase">-</span>
            </div>

            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-card-heading me-2"></i>
                                RFC
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderRfc">-</span>
            </div>

            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-telephone me-2"></i>
                                Teléfono
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderPhone">-</span>
            </div>

            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-envelope me-2"></i>
                                Correo
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderEmail">-</span>
            </div>

            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-geo-alt me-2"></i>
                                Dirección
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderAddress">-</span>
            </div>

            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-mailbox me-2"></i>
                                Código postal
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderPostCode">-</span>
            </div>
          </div>

          <div class="col-md-6">
            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-person me-2"></i>
                                Contacto
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderContactName">-</span>
            </div>

            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-telephone me-2"></i>
                                Teléfono del contacto
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderContactPhone">-</span>
            </div>

            <div class="modal-detail-row">
                            <span class="modal-detail-label">
                                <i class="bi bi-envelope me-2"></i>
                                Correo del contacto
                            </span>
              <span class="modal-detail-value"
                    id="viewProviderContactEmail">-</span>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button"
                class="btn btn-secondary"
                data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cerrar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- ==========================================================
     MODAL: EDITAR PROVEEDOR
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalEdit"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-pencil-fill me-2"
             style="color:#ffc857;"></i>
          Editar Proveedor
        </h5>

        <button type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <form id="formEditProvider"
              class="js-form"
              action="${pageContext.request.contextPath}/provider/update"
              method="post"
              novalidate
              data-submit-mode="manual"
              data-reset-on-close="false">

          <input type="hidden"
                 id="editProviderId"
                 name="id">

          <div class="row">

            <div class="col-md-6 mb-3 form-field">
              <label for="editName" class="form-label">
                Nombre comercial
                <span class="text-danger required-marker">*</span>
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="editName"
                     name="name"
                     placeholder="Nombre comercial"
                     minlength="2"
                     maxlength="150"
                     data-label="Nombre comercial"
                     data-valid-message="Nombre válido."
                     autocomplete="off"
                     required>

              <div class="valid-feedback">Nombre válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="editSocialCase" class="form-label">
                Razón social
                <span class="text-danger required-marker">*</span>
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="editSocialCase"
                     name="socialCase"
                     placeholder="Razón social"
                     minlength="2"
                     maxlength="150"
                     data-label="Razón social"
                     data-valid-message="Razón social válida."
                     autocomplete="off"
                     required>

              <div class="valid-feedback">Razón social válida.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="editRfc" class="form-label">
                RFC
                <span class="text-danger required-marker">*</span>
              </label>

              <input type="text"
                     class="form-control js-form-field text-uppercase"
                     id="editRfc"
                     name="rfc"
                     placeholder="Ejemplo: ABC123456XYZ"
                     minlength="12"
                     maxlength="13"
                     pattern="[A-Za-zÑñ&]{3,4}[0-9]{6}[A-Za-z0-9]{3}"
                     data-label="RFC"
                     data-valid-message="RFC válido."
                     autocomplete="off"
                     required>

              <div class="valid-feedback">RFC válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="editPhone" class="form-label">
                Teléfono
              </label>

              <input type="tel"
                     class="form-control js-form-field"
                     id="editPhone"
                     name="phone"
                     placeholder="10 dígitos"
                     minlength="10"
                     maxlength="10"
                     pattern="[0-9]{10}"
                     inputmode="numeric"
                     data-label="Teléfono"
                     data-valid-message="Teléfono válido."
                     autocomplete="tel">

              <div class="valid-feedback">Teléfono válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="editEmail" class="form-label">
                Correo del proveedor
              </label>

              <input type="email"
                     class="form-control js-form-field"
                     id="editEmail"
                     name="email"
                     placeholder="proveedor@correo.com"
                     maxlength="150"
                     data-label="Correo del proveedor"
                     data-valid-message="Correo válido."
                     autocomplete="email">

              <div class="valid-feedback">Correo válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="editPostCode" class="form-label">
                Código postal
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="editPostCode"
                     name="postCode"
                     placeholder="5 dígitos"
                     minlength="5"
                     maxlength="5"
                     pattern="[0-9]{5}"
                     inputmode="numeric"
                     data-label="Código postal"
                     data-valid-message="Código postal válido."
                     autocomplete="postal-code">

              <div class="valid-feedback">Código postal válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-12 mb-3 form-field">
              <label for="editAddress" class="form-label">
                Dirección
              </label>

              <textarea class="form-control js-form-field"
                        id="editAddress"
                        name="address"
                        rows="3"
                        maxlength="300"
                        placeholder="Dirección del proveedor"
                        data-label="Dirección"></textarea>

              <div class="valid-feedback">Dirección válida.</div>
              <div class="invalid-feedback"></div>
            </div>
          </div>

          <hr>

          <h6 class="mb-3">
            <i class="bi bi-person-lines-fill me-2"></i>
            Datos del contacto
          </h6>

          <div class="row">

            <div class="col-md-6 mb-3 form-field">
              <label for="editContactName" class="form-label">
                Nombre del contacto
              </label>

              <input type="text"
                     class="form-control js-form-field"
                     id="editContactName"
                     name="contactName"
                     placeholder="Nombre completo"
                     minlength="2"
                     maxlength="150"
                     data-label="Nombre del contacto"
                     data-valid-message="Nombre válido."
                     autocomplete="off">

              <div class="valid-feedback">Nombre válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-md-6 mb-3 form-field">
              <label for="editContactPhone" class="form-label">
                Teléfono del contacto
              </label>

              <input type="tel"
                     class="form-control js-form-field"
                     id="editContactPhone"
                     name="contactPhone"
                     placeholder="10 dígitos"
                     minlength="10"
                     maxlength="10"
                     pattern="[0-9]{10}"
                     inputmode="numeric"
                     data-label="Teléfono del contacto"
                     data-valid-message="Teléfono válido."
                     autocomplete="tel">

              <div class="valid-feedback">Teléfono válido.</div>
              <div class="invalid-feedback"></div>
            </div>

            <div class="col-12 mb-3 form-field">
              <label for="editContactEmail" class="form-label">
                Correo del contacto
              </label>

              <input type="email"
                     class="form-control js-form-field"
                     id="editContactEmail"
                     name="contactEmail"
                     placeholder="contacto@correo.com"
                     maxlength="150"
                     data-label="Correo del contacto"
                     data-valid-message="Correo válido."
                     autocomplete="email">

              <div class="valid-feedback">Correo válido.</div>
              <div class="invalid-feedback"></div>
            </div>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button type="button"
                class="btn btn-secondary"
                data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="button"
                class="btn btn-primary"
                id="btnOpenConfirmEdit">
          <i class="bi bi-check-lg me-1"></i>
          Actualizar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- ==========================================================
     MODAL: CONFIRMAR ACTUALIZACIÓN
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalConfirmEdit"
     tabindex="-1"
     aria-hidden="true">

  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">
          <i class="bi bi-question-circle-fill me-2"
             style="color:#ffc857;"></i>
          Confirmar Actualización
        </h5>

        <button type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body text-center py-4">
        <i class="bi bi-pencil-square"
           style="font-size:3rem;color:#ffc857;display:block;margin-bottom:16px;"></i>

        <p style="color:var(--text-color,#2d3748);font-size:16px;font-weight:500;">
          ¿Deseas actualizar el proveedor
          <strong id="editConfirmProviderName">-</strong>?
        </p>

        <p style="color:var(--text-muted,#718096);font-size:14px;">
          Los cambios se aplicarán inmediatamente.
        </p>
      </div>

      <div class="modal-footer justify-content-center">
        <button type="button"
                class="btn btn-secondary"
                data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="button"
                class="btn btn-primary"
                id="btnConfirmEdit"
                data-loading-text="Actualizando...">
          <i class="bi bi-check-lg me-1"></i>
          Confirmar
        </button>
      </div>
    </div>
  </div>
</div>

<!-- ==========================================================
     MODAL: CAMBIAR ESTADO
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalConfirmStatus"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">

      <div class="modal-header">
        <h5 class="modal-title">
          <i id="statusModalHeaderIcon"
             class="bi bi-question-circle-fill me-2"
             style="color:#ffc857;"></i>
          Confirmar cambio
        </h5>

        <button type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body text-center py-4">
        <i id="statusModalIcon"
           class="bi bi-truck-front"
           style="display:block;margin-bottom:16px;font-size:3rem;color:#ff6666;"></i>

        <p id="statusModalQuestion"
           style="color:var(--text-color,#2d3748);font-size:16px;font-weight:500;">
          ¿Deseas cambiar el estado del proveedor?
        </p>

        <p style="color:var(--text-muted,#718096);font-size:14px;">
          Proveedor:
          <strong id="statusConfirmProviderName">-</strong>
        </p>

        <p id="statusModalDescription"
           style="color:var(--text-muted,#718096);font-size:13px;margin-bottom:0;">
          El cambio se aplicará inmediatamente.
        </p>

        <form id="formChangeStatus"
              class="js-form"
              action="${pageContext.request.contextPath}/provider/change-status"
              method="post"
              data-submit-mode="manual"
              data-reset-on-close="false">

          <input type="hidden"
                 id="statusProviderId"
                 name="id">

          <input type="hidden"
                 id="statusNewValue"
                 name="status">
        </form>
      </div>

      <div class="modal-footer justify-content-center">
        <button type="button"
                class="btn btn-secondary"
                data-bs-dismiss="modal">
          <i class="bi bi-x-lg me-1"></i>
          Cancelar
        </button>

        <button type="button"
                class="btn"
                id="btnConfirmStatus"
                data-loading-text="Procesando...">
          <i id="statusConfirmButtonIcon"
             class="bi bi-check-lg me-1"></i>

          <span id="statusConfirmButtonText">
                        Confirmar
                    </span>
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
<script src="${pageContext.request.contextPath}/assets/js/provider.js"></script>
</body>
</html>
<%-- Catálogo de proveedores y datos de contacto. --%>
