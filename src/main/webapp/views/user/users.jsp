<%--
    Vista técnica: users.
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
    <title>Gestión de Usuarios</title>

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
                    <i class="bi bi-people-fill"></i>
                    Gestión de Usuarios
                </h2>

                <div class="table-header-actions">
                    <button type="button" class="table-primary-btn" id="btnNuevoUsuario">
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
                               data-table-target="usersTable"
                               placeholder="Buscar por nombre, correo o teléfono..."
                               autocomplete="off">
                    </div>

                    <button type="button"
                            class="table-toolbar-btn js-filter-toggle"
                            data-filter-target="usersFilters">
                        <i class="bi bi-funnel"></i>
                        <span>Filtros</span>
                    </button>

                    <button type="button"
                            class="table-toolbar-btn js-clear-filters"
                            data-table-target="usersTable">
                        <i class="bi bi-eraser"></i>
                        <span>Limpiar</span>
                    </button>
                </div>

                <div class="table-filters" id="usersFilters" style="--filter-columns:2;">
                    <div class="table-filter-group">
                        <span class="table-filter-label">Rol</span>

                        <div class="table-filter-options">
                            <input type="radio"
                                   id="roleAll"
                                   name="roleFilter"
                                   value="all"
                                   class="js-table-filter"
                                   data-table-target="usersTable"
                                   data-filter-field="role"
                                   checked>
                            <label for="roleAll" class="table-filter-option">Todos</label>

                            <input type="radio"
                                   id="roleAdministrador"
                                   name="roleFilter"
                                   value="administrador"
                                   class="js-table-filter"
                                   data-table-target="usersTable"
                                   data-filter-field="role">
                            <label for="roleAdministrador" class="table-filter-option">
                                Administrador
                            </label>

                            <input type="radio"
                                   id="roleAlmacenista"
                                   name="roleFilter"
                                   value="almacenista"
                                   class="js-table-filter"
                                   data-table-target="usersTable"
                                   data-filter-field="role">
                            <label for="roleAlmacenista" class="table-filter-option">
                                Almacenista
                            </label>
                        </div>
                    </div>

                    <div class="table-filter-group">
                        <span class="table-filter-label">Estado</span>

                        <div class="table-filter-options">
                            <input type="radio"
                                   id="statusAll"
                                   name="statusFilter"
                                   value="all"
                                   class="js-table-filter"
                                   data-table-target="usersTable"
                                   data-filter-field="status"
                                   checked>
                            <label for="statusAll" class="table-filter-option">Todos</label>

                            <input type="radio"
                                   id="statusActive"
                                   name="statusFilter"
                                   value="active"
                                   class="js-table-filter"
                                   data-table-target="usersTable"
                                   data-filter-field="status">
                            <label for="statusActive" class="table-filter-option">Activos</label>

                            <input type="radio"
                                   id="statusInactive"
                                   name="statusFilter"
                                   value="inactive"
                                   class="js-table-filter"
                                   data-table-target="usersTable"
                                   data-filter-field="status">
                            <label for="statusInactive" class="table-filter-option">Inactivos</label>
                        </div>
                    </div>
                </div>
            </section>

            <!-- ======================================================
                 TABLA
                 ====================================================== -->

            <section class="table-panel">
                <div class="table-responsive" style="${empty users ? 'display:none;' : ''}">
                    <table class="app-table" id="usersTable" style="--table-min-width:1050px;">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre completo</th>
                            <th>Correo</th>
                            <th>Teléfono</th>
                            <th>Rol</th>
                            <th>Acceso</th>
                            <th>Estado</th>
                            <th class="table-text-center">Acciones</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr class="js-table-row user-table-row"
                                data-id="${user.id}"
                                data-name="${fn:escapeXml(user.name)}"
                                data-surname="${fn:escapeXml(user.surname)}"
                                data-lastname="${fn:escapeXml(user.lastname)}"
                                data-full-name="${fn:escapeXml(user.name)} ${fn:escapeXml(user.surname)} ${fn:escapeXml(user.lastname)}"
                                data-email="${fn:escapeXml(user.email)}"
                                data-phone="${fn:escapeXml(user.phone)}"
                                data-role="${fn:toLowerCase(user.role)}"
                                data-must-change-password="${user.mustChangePassword == 1 ? 'true' : 'false'}"
                                data-status="${user.status == 1 ? 'active' : 'inactive'}"
                                data-search="${fn:escapeXml(user.name)} ${fn:escapeXml(user.surname)} ${fn:escapeXml(user.lastname)} ${fn:escapeXml(user.email)} ${fn:escapeXml(user.phone)}">

                                <td class="table-cell-secondary table-cell-nowrap">
                                        ${user.id}
                                </td>

                                <td class="table-cell-primary">
                                        ${user.name} ${user.surname} ${user.lastname}
                                </td>

                                <td class="table-cell-secondary">
                                        ${user.email}
                                </td>

                                <td class="table-cell-secondary table-cell-nowrap">
                                    <c:choose>
                                        <c:when test="${not empty user.phone}">
                                            ${user.phone}
                                        </c:when>
                                        <c:otherwise>
                                            Sin teléfono
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <span class="table-badge ${fn:toLowerCase(user.role) == 'administrador'
                                            ? 'table-badge-primary'
                                            : 'table-badge-warning'}">
                                            ${user.role}
                                    </span>
                                </td>

                                <td>
                                    <span class="table-badge ${user.mustChangePassword == 1
                                            ? 'table-badge-warning'
                                            : 'table-badge-success'}">
                                            ${user.mustChangePassword == 1
                                                    ? 'Cambio pendiente'
                                                    : 'Normal'}
                                    </span>
                                </td>

                                <td>
                                    <span class="table-badge ${user.status == 1
                                            ? 'table-badge-success'
                                            : 'table-badge-danger'}">
                                            ${user.status == 1 ? 'Activo' : 'Inactivo'}
                                    </span>
                                </td>

                                <td>
                                    <div class="table-actions">
                                        <button type="button"
                                                class="table-action-btn table-action-view btn-view-user"
                                                title="Ver detalles"
                                                aria-label="Ver detalles">
                                            <i class="bi bi-eye"></i>
                                        </button>

                                        <button type="button"
                                                class="table-action-btn table-action-edit btn-edit-user"
                                                title="Editar usuario"
                                                aria-label="Editar usuario">
                                            <i class="bi bi-pencil"></i>
                                        </button>

                                        <button type="button"
                                                class="table-action-btn table-action-warning btn-reset-access"
                                                title="Restablecer acceso"
                                                aria-label="Restablecer acceso"
                                                data-user-id="${user.id}"
                                                data-user-name="${fn:escapeXml(user.name)} ${fn:escapeXml(user.surname)} ${fn:escapeXml(user.lastname)}">
                                            <i class="bi bi-key"></i>
                                        </button>

                                        <button type="button"
                                                class="table-action-btn btn-change-status ${user.status == 1
                                                        ? 'table-action-delete'
                                                        : 'table-action-success'}"
                                                title="${user.status == 1
                                                        ? 'Desactivar usuario'
                                                        : 'Activar usuario'}"
                                                aria-label="${user.status == 1
                                                        ? 'Desactivar usuario'
                                                        : 'Activar usuario'}"
                                                data-user-id="${user.id}"
                                                data-user-name="${fn:escapeXml(user.name)} ${fn:escapeXml(user.surname)} ${fn:escapeXml(user.lastname)}"
                                                data-new-status="${user.status == 1 ? 0 : 1}">
                                            <i class="bi ${user.status == 1
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
                     data-table-target="usersTable"
                     data-page-size="5"
                     style="${empty users ? 'display:none;' : ''}">

                    <div class="table-pagination-left">
                        <label class="table-page-size-label" for="usersPageSize">
                            Mostrar
                        </label>

                        <select id="usersPageSize"
                                class="table-page-size-select js-page-size"
                                data-table-target="usersTable"
                                aria-label="Registros por página">
                            <option value="5" selected>5</option>
                            <option value="10">10</option>
                            <option value="20">20</option>
                            <option value="50">50</option>
                        </select>

                        <span class="table-page-size-text">registros</span>
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

                <div id="usersGeneralEmptyState"
                     class="table-empty-state"
                     style="${empty users ? 'display:block;' : 'display:none;'}">
                    <i class="bi bi-inbox"></i>
                    No hay usuarios registrados.
                </div>

                <div id="usersFilterEmptyState"
                     class="table-empty-state js-filter-empty-state"
                     style="display:none;">
                    <i class="bi bi-search"></i>
                    No se encontraron usuarios con esos filtros.
                </div>
            </section>
        </div>
    </main>
</div>

<!-- ==========================================================
     MODAL: REGISTRAR USUARIO
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalCreate"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-person-plus-fill me-2" style="color:#6390ff;"></i>
                    Registrar Usuario
                </h5>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <form id="formCreateUser"
                      class="js-form"
                      action="${pageContext.request.contextPath}/user/save"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="true">

                    <div class="mb-3 form-field">
                        <label for="createName" class="form-label">
                            Nombre
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field"
                               id="createName"
                               name="name"
                               placeholder="Ingrese el nombre"
                               minlength="2"
                               maxlength="50"
                               data-type="name"
                               data-label="Nombre"
                               data-valid-message="Nombre válido."
                               autocomplete="given-name"
                               required>

                        <div class="valid-feedback">Nombre válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="createSurname" class="form-label">
                            Apellido paterno
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field"
                               id="createSurname"
                               name="surname"
                               placeholder="Ingrese el apellido paterno"
                               minlength="2"
                               maxlength="50"
                               data-type="name"
                               data-label="Apellido paterno"
                               data-valid-message="Apellido válido."
                               autocomplete="family-name"
                               required>

                        <div class="valid-feedback">Apellido válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="createLastname" class="form-label">
                            Apellido materno
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field"
                               id="createLastname"
                               name="lastname"
                               placeholder="Ingrese el apellido materno"
                               minlength="2"
                               maxlength="50"
                               data-type="name"
                               data-label="Apellido materno"
                               data-valid-message="Apellido válido."
                               autocomplete="additional-name"
                               required>

                        <div class="valid-feedback">Apellido válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="createEmail" class="form-label">
                            Correo electrónico
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="email"
                               class="form-control js-form-field"
                               id="createEmail"
                               name="email"
                               placeholder="ejemplo@correo.com"
                               maxlength="100"
                               data-type="email"
                               data-label="Correo electrónico"
                               data-valid-message="Correo válido."
                               autocomplete="email"
                               required>

                        <div class="valid-feedback">Correo válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="createPhone" class="form-label">
                            Teléfono
                            <span class="text-danger required-marker">*</span>
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
                               data-type="phone"
                               data-label="Teléfono"
                               data-valid-message="Teléfono válido."
                               autocomplete="tel"
                               required>

                        <div class="valid-feedback">Teléfono válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="createRole" class="form-label">
                            Rol
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <select class="form-select js-form-field"
                                id="createRole"
                                name="role"
                                data-label="Rol"
                                data-valid-message="Rol seleccionado."
                                required>
                            <option value="">Seleccione un rol</option>
                            <option value="Administrador">Administrador</option>
                            <option value="Almacenista">Almacenista</option>
                        </select>

                        <div class="valid-feedback">Rol seleccionado.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="neumo-alert neumo-alert-info mb-0">
                        <i class="bi bi-info-circle-fill"></i>

                        <div class="neumo-alert-text">
                            Al registrar al usuario se generará una contraseña temporal con vigencia de 24 horas.
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
                        id="btnSaveCreate"
                        form="formCreateUser"
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

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-person-badge me-2" style="color:#6390ff;"></i>
                    Detalles del Usuario
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
                    <i class="bi bi-person-circle"
                       style="font-size:4rem;color:var(--text-muted,#718096);"></i>

                    <h4 id="viewName"
                        class="mt-2"
                        style="color:var(--text-color,#2d3748);">
                        -
                    </h4>

                    <span id="viewRoleBadge" class="table-badge">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-envelope me-2"></i>
                        Correo
                    </span>
                    <span class="modal-detail-value" id="viewEmail">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-telephone me-2"></i>
                        Teléfono
                    </span>
                    <span class="modal-detail-value" id="viewPhone">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-person me-2"></i>
                        Nombre completo
                    </span>
                    <span class="modal-detail-value" id="viewFullName">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-shield me-2"></i>
                        Rol
                    </span>
                    <span class="modal-detail-value" id="viewRole">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-key me-2"></i>
                        Tipo de acceso
                    </span>
                    <span class="modal-detail-value" id="viewAccessStatus">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-toggle-on me-2"></i>
                        Estado
                    </span>
                    <span class="modal-detail-value" id="viewStatus">-</span>
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
     MODAL: EDITAR USUARIO
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalEdit"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-pencil-fill me-2" style="color:#ffc857;"></i>
                    Editar Usuario
                </h5>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <form id="formEditUser"
                      class="js-form"
                      action="${pageContext.request.contextPath}/user/update"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="false">

                    <input type="hidden" id="editUserId" name="id">

                    <div class="mb-3 form-field">
                        <label for="editName" class="form-label">
                            Nombre
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field"
                               id="editName"
                               name="name"
                               placeholder="Ingrese el nombre"
                               minlength="2"
                               maxlength="50"
                               data-type="name"
                               data-label="Nombre"
                               data-valid-message="Nombre válido."
                               autocomplete="given-name"
                               required>

                        <div class="valid-feedback">Nombre válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="editSurname" class="form-label">
                            Apellido paterno
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field"
                               id="editSurname"
                               name="surname"
                               placeholder="Ingrese el apellido paterno"
                               minlength="2"
                               maxlength="50"
                               data-type="name"
                               data-label="Apellido paterno"
                               data-valid-message="Apellido válido."
                               autocomplete="family-name"
                               required>

                        <div class="valid-feedback">Apellido válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="editLastname" class="form-label">
                            Apellido materno
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field"
                               id="editLastname"
                               name="lastname"
                               placeholder="Ingrese el apellido materno"
                               minlength="2"
                               maxlength="50"
                               data-type="name"
                               data-label="Apellido materno"
                               data-valid-message="Apellido válido."
                               autocomplete="additional-name"
                               required>

                        <div class="valid-feedback">Apellido válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="editEmail" class="form-label">
                            Correo electrónico
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="email"
                               class="form-control js-form-field"
                               id="editEmail"
                               name="email"
                               placeholder="ejemplo@correo.com"
                               maxlength="100"
                               data-type="email"
                               data-label="Correo electrónico"
                               data-valid-message="Correo válido."
                               autocomplete="email"
                               required>

                        <div class="valid-feedback">Correo válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="editPhone" class="form-label">
                            Teléfono
                            <span class="text-danger required-marker">*</span>
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
                               data-type="phone"
                               data-label="Teléfono"
                               data-valid-message="Teléfono válido."
                               autocomplete="tel"
                               required>

                        <div class="valid-feedback">Teléfono válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="editRole" class="form-label">
                            Rol
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <select class="form-select js-form-field"
                                id="editRole"
                                name="role"
                                data-label="Rol"
                                data-valid-message="Rol seleccionado."
                                required>
                            <option value="">Seleccione un rol</option>
                            <option value="Administrador">Administrador</option>
                            <option value="Almacenista">Almacenista</option>
                        </select>

                        <div class="valid-feedback">Rol seleccionado.</div>
                        <div class="invalid-feedback"></div>
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
                    <i class="bi bi-question-circle-fill me-2" style="color:#ffc857;"></i>
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
                    ¿Deseas actualizar los datos de
                    <strong id="editConfirmName">-</strong>?
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
     MODAL: CONFIRMAR CAMBIO DE ESTADO
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
                   class="bi bi-person-x"
                   style="display:block;margin-bottom:16px;font-size:3rem;color:#ff6666;"></i>

                <p id="statusModalQuestion"
                   style="color:var(--text-color,#2d3748);font-size:16px;font-weight:500;">
                    ¿Deseas cambiar el estado del usuario?
                </p>

                <p style="color:var(--text-muted,#718096);font-size:14px;">
                    Usuario:
                    <strong id="statusConfirmUserName">-</strong>
                </p>

                <p id="statusModalDescription"
                   style="color:var(--text-muted,#718096);font-size:13px;margin-bottom:0;">
                    El cambio se aplicará inmediatamente.
                </p>

                <form id="formChangeStatus"
                      class="js-form"
                      action="${pageContext.request.contextPath}/user/change-status"
                      method="post"
                      data-submit-mode="manual"
                      data-reset-on-close="false">
                    <input type="hidden" id="statusUserId" name="id">
                    <input type="hidden" id="statusNewValue" name="status">
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
                    <span id="statusConfirmButtonText">Confirmar</span>
                </button>
            </div>
        </div>
    </div>
</div>

<!-- ==========================================================
     MODAL: CONFIRMAR RESTABLECIMIENTO DE ACCESO
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalConfirmResetAccess"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-key-fill me-2" style="color:#ffc857;"></i>
                    Restablecer acceso
                </h5>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body text-center py-4">
                <i class="bi bi-person-lock"
                   style="display:block;margin-bottom:16px;font-size:3rem;color:#ffc857;"></i>

                <p style="color:var(--text-color,#2d3748);font-size:16px;font-weight:500;">
                    ¿Deseas generar un nuevo acceso temporal para
                    <strong id="resetAccessConfirmUserName">-</strong>?
                </p>

                <p style="color:var(--text-muted,#718096);font-size:14px;">
                    La contraseña actual dejará de funcionar. Se generará una contraseña temporal válida durante 24 horas y se enviará directamente al correo electrónico del usuario.
                </p>

                <form id="formResetAccess"
                      class="js-form"
                      action="${pageContext.request.contextPath}/user/reset-access"
                      method="post"
                      data-submit-mode="manual"
                      data-reset-on-close="false">
                    <input type="hidden" id="resetAccessUserId" name="id">
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
                        class="btn btn-warning"
                        id="btnConfirmResetAccess"
                        data-loading-text="Enviando...">
                    <i class="bi bi-envelope-check me-1"></i>
                    Restablecer y enviar
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
<script src="${pageContext.request.contextPath}/assets/js/user.js"></script>
</body>
</html>
<%-- Administración de usuarios, contraseñas temporales y baja lógica. --%>
