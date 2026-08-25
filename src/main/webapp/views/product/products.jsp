<%--
    Vista técnica: products.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Gestión de Productos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/table.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/modals.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/form.css">
    <style>
        .product-provider-section{padding:16px;border-radius:16px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .product-provider-header{display:flex;align-items:center;justify-content:space-between;gap:12px;margin-bottom:14px}
        .product-provider-header>div:first-child{min-width:0}
        .product-provider-title{margin:0;color:var(--text-color);font-size:15px;font-weight:700}
        .product-provider-description{margin:4px 0 0;color:var(--text-muted);font-size:13px}
        .product-provider-list{display:flex;flex-direction:column;gap:12px}
        .product-provider-row{display:grid;grid-template-columns:minmax(0,1fr) minmax(190px,230px) 42px;gap:12px;align-items:end;padding:12px;border-radius:14px;background:var(--card-bg);box-shadow:var(--neumo-shadow)}
        .product-provider-row>.form-field{min-width:0;margin:0}
        .product-provider-row .form-label{display:block;margin-bottom:6px}
        .product-provider-select{width:100%;min-width:0;height:42px}
        .product-provider-row .input-group{display:flex;flex-wrap:nowrap!important;align-items:stretch;width:100%;min-width:0;height:42px;overflow:hidden;border-radius:12px;background:var(--input-bg);box-shadow:var(--neumo-shadow-inset)}
        .product-provider-row .input-group-text{flex:0 0 42px;width:42px;min-width:42px;height:42px;display:flex;align-items:center;justify-content:center;padding:0;border:0;border-radius:12px 0 0 12px;background:transparent;color:var(--text-muted)}
        .product-provider-row .product-provider-price{flex:1 1 auto!important;width:1%!important;min-width:0!important;height:42px;margin:0;padding:9px 12px;border:0!important;border-radius:0 12px 12px 0!important;background:transparent!important;color:var(--text-color);box-shadow:none!important}
        .product-provider-row .product-provider-price:focus{transform:none!important;outline:none;box-shadow:none!important}
        .product-provider-row .product-provider-price::placeholder{color:var(--text-muted);opacity:.72}
        .product-provider-remove{width:42px;min-width:42px;height:42px;align-self:end;display:grid;place-items:center;padding:0!important}
        .product-provider-empty{padding:18px;border:1px dashed var(--border-color);border-radius:14px;color:var(--text-muted);font-size:13px;text-align:center}
        .product-provider-summary{display:flex;flex-direction:column;gap:8px}
        .product-provider-summary-item{display:flex;align-items:center;justify-content:space-between;gap:12px;padding:10px 12px;border-radius:12px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .product-provider-summary-info{min-width:0}
        .product-provider-summary-name{display:block;color:var(--text-color);font-weight:600}
        .product-provider-summary-rfc{display:block;color:var(--text-muted);font-size:12px}
        .product-provider-summary-price{flex-shrink:0;color:var(--text-color);font-weight:700}
        html[data-theme="dark"] .product-provider-row .input-group,html[data-bs-theme="dark"] .product-provider-row .input-group{color-scheme:dark}
        @media(max-width:767.98px){
            .product-provider-section{padding:14px}
            .product-provider-row{grid-template-columns:minmax(0,1fr) minmax(170px,210px) 42px;gap:10px}
            .product-provider-header{align-items:flex-start}
        }
        @media(max-width:575.98px){
            .product-provider-section{padding:12px;border-radius:14px}
            .product-provider-header{flex-direction:column;align-items:stretch;gap:10px}
            .product-provider-header .btn,.product-provider-header .btn-add-product-provider{width:100%;justify-content:center}
            .product-provider-row{grid-template-columns:minmax(0,1fr) 42px;gap:10px;padding:11px}
            .product-provider-row>.form-field:first-child{grid-column:1/-1}
            .product-provider-row>.form-field:nth-child(2){grid-column:1}
            .product-provider-remove{grid-column:2}
            .product-provider-summary-item{align-items:flex-start;flex-direction:column}
            .product-provider-summary-price{align-self:flex-end}
        }
        @media(max-width:380px){
            .product-provider-section{padding:10px}
            .product-provider-row{padding:10px;gap:8px}
            .product-provider-title{font-size:14px}
            .product-provider-description{font-size:12px;line-height:1.45}
        }
        .product-provider-header{
            display:flex;
            align-items:flex-start;
            justify-content:space-between;
            gap:16px;
            margin-bottom:14px
        }

        .product-provider-header-info{
            flex:1;
            min-width:0
        }

        .product-provider-header .btn-open-quick-provider{
            flex-shrink:0;
            white-space:nowrap
        }

        .product-provider-add-row{
            width:100%;
            margin-top:12px;
            display:flex;
            align-items:center;
            justify-content:center
        }

        .product-provider-empty{
            margin-top:12px
        }

        @media(max-width:575.98px){
            .product-provider-header{
                flex-direction:column;
                align-items:stretch
            }

            .product-provider-header .btn-open-quick-provider{
                width:100%;
                justify-content:center
            }
        }
    </style>
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
            <div class="table-page-header">
                <h2 class="table-page-title">
                    <i class="bi bi-box-seam-fill"></i>
                    Gestión de Productos
                </h2>
                <div class="table-header-actions">
                    <button type="button" class="table-primary-btn" id="btnNuevoProducto">
                        <i class="bi bi-plus-lg"></i>
                        Registrar
                    </button>
                </div>
            </div>

            <section class="table-toolbar">
                <div class="table-toolbar-top">
                    <div class="table-search">
                        <i class="bi bi-search"></i>
                        <input type="search"
                               class="js-table-search"
                               data-table-target="productsTable"
                               placeholder="Buscar por clave, producto, unidad, proveedor o descripción..."
                               autocomplete="off">
                    </div>

                    <button type="button"
                            class="table-toolbar-btn js-filter-toggle"
                            data-filter-target="productsFilters">
                        <i class="bi bi-funnel"></i>
                        <span>Filtros</span>
                    </button>

                    <button type="button"
                            class="table-toolbar-btn js-clear-filters"
                            data-table-target="productsTable">
                        <i class="bi bi-eraser"></i>
                        <span>Limpiar</span>
                    </button>
                </div>

                <div class="table-filters" id="productsFilters" style="--filter-columns:2;">
                    <div class="table-filter-group">
                        <label class="table-filter-label" for="productMetricFilter">Unidad de medida</label>
                        <select id="productMetricFilter"
                                class="form-select js-table-filter"
                                data-table-target="productsTable"
                                data-filter-field="metric"
                                aria-label="Filtrar por unidad de medida">
                            <option value="all">Todas</option>
                            <c:forEach var="metric" items="${metrics}">
                                <option value="${metric.idMetric}">${metric.name} (${metric.shortName})</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="table-filter-group">
                        <span class="table-filter-label">Estado</span>
                        <div class="table-filter-options">
                            <input type="radio"
                                   id="productStatusAll"
                                   name="productStatusFilter"
                                   value="all"
                                   class="js-table-filter"
                                   data-table-target="productsTable"
                                   data-filter-field="status"
                                   checked>
                            <label for="productStatusAll" class="table-filter-option">Todos</label>

                            <input type="radio"
                                   id="productStatusActive"
                                   name="productStatusFilter"
                                   value="active"
                                   class="js-table-filter"
                                   data-table-target="productsTable"
                                   data-filter-field="status">
                            <label for="productStatusActive" class="table-filter-option">Activos</label>

                            <input type="radio"
                                   id="productStatusInactive"
                                   name="productStatusFilter"
                                   value="inactive"
                                   class="js-table-filter"
                                   data-table-target="productsTable"
                                   data-filter-field="status">
                            <label for="productStatusInactive" class="table-filter-option">Inactivos</label>
                        </div>
                    </div>
                </div>
            </section>

            <section class="table-panel">
                <div class="table-responsive" style="${empty products ? 'display:none;' : ''}">
                    <table class="app-table" id="productsTable" style="--table-min-width:1100px;">
                        <thead>
                        <tr>
                            <th>Clave</th>
                            <th>Producto</th>
                            <th>Unidad</th>
                            <th>Descripción</th>
                            <th>Proveedores</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="product" items="${products}">
                            <tr class="product-table-row"
                                data-id="${product.idProduct}"
                                data-code="${fn:escapeXml(product.code)}"
                                data-name="${fn:escapeXml(product.name)}"
                                data-id-metric="${product.idMetric}"
                                data-metric-name="${fn:escapeXml(product.metricName)}"
                                data-metric-short-name="${fn:escapeXml(product.metricShortName)}"
                                data-description="${fn:escapeXml(product.description)}"
                                data-status="${product.status}"
                                data-filter-status="${product.status == 1 ? 'active' : 'inactive'}"
                                data-filter-metric="${product.idMetric}">
                                <td data-label="Clave">${product.code}</td>
                                <td data-label="Producto">${product.name}</td>
                                <td data-label="Unidad">${product.metricName} (${product.metricShortName})</td>
                                <td data-label="Descripción">${empty product.description ? '-' : product.description}</td>
                                <td data-label="Proveedores">${product.providerCount}</td>
                                <td data-label="Estado">
                                    <span class="table-badge ${product.status == 1 ? 'table-badge-success' : 'table-badge-danger'}">
                                            ${product.status == 1 ? 'Activo' : 'Inactivo'}
                                    </span>
                                </td>
                                <td data-label="Acciones">
                                    <div class="table-actions">
                                        <button type="button"
                                                class="table-action-btn table-action-view btn-view-product"
                                                title="Ver producto"
                                                aria-label="Ver producto">
                                            <i class="bi bi-eye"></i>
                                        </button>

                                        <button type="button"
                                                class="table-action-btn table-action-edit btn-edit-product"
                                                title="Editar producto"
                                                aria-label="Editar producto">
                                            <i class="bi bi-pencil"></i>
                                        </button>

                                        <button type="button"
                                                class="table-action-btn btn-change-status ${product.status == 1 ? 'table-action-delete' : 'table-action-success'}"
                                                title="${product.status == 1 ? 'Desactivar producto' : 'Activar producto'}"
                                                aria-label="${product.status == 1 ? 'Desactivar producto' : 'Activar producto'}"
                                                data-product-id="${product.idProduct}"
                                                data-product-name="${fn:escapeXml(product.name)}"
                                                data-new-status="${product.status == 1 ? 0 : 1}">
                                            <i class="bi ${product.status == 1 ? 'bi-toggle-on' : 'bi-toggle-off'}"></i>
                                        </button>
                                    </div>

                                    <div class="product-provider-data-list" hidden>
                                        <c:forEach var="relation" items="${product.providers}">
                                            <span class="product-provider-data"
                                                  data-id-product-provider="${relation.idProductProvider}"
                                                  data-id-provider="${relation.idProvider}"
                                                  data-provider-name="${fn:escapeXml(relation.providerName)}"
                                                  data-provider-rfc="${fn:escapeXml(relation.providerRfc)}"
                                                  data-purchase-price="${relation.purchasePrice}"
                                                  data-status="${relation.status}"
                                                  data-provider-status="${relation.providerStatus}">

                                            </span>
                                        </c:forEach>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="table-pagination"
                     data-table-target="productsTable"
                     data-page-size="5"
                     style="${empty products ? 'display:none;' : ''}">
                    <div class="table-pagination-left">
                        <label class="table-page-size-label" for="productsPageSize">Mostrar</label>
                        <select id="productsPageSize"
                                class="table-page-size-select js-page-size"
                                data-table-target="productsTable"
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

                <div id="productsGeneralEmptyState"
                     class="table-empty-state"
                     style="${empty products ? 'display:block;' : 'display:none;'}">
                    <i class="bi bi-inbox"></i>
                    No hay productos registrados.
                </div>

                <div id="productsFilterEmptyState"
                     class="table-empty-state js-filter-empty-state"
                     style="display:none;">
                    <i class="bi bi-search"></i>
                    No se encontraron productos con esos filtros.
                </div>
            </section>
        </div>
    </main>
</div>

<div class="modal fade modal-neumorphic"
     id="modalCreate"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-box-seam-fill me-2" style="color:#6390ff;"></i>
                    Registrar Producto
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <form id="formCreateProduct"
                      class="js-form"
                      action="${pageContext.request.contextPath}/product/save"
                      method="post"
                      novalidate
                      data-reset-on-close="true"
                      data-submit-mode="manual">

                    <div class="row">
                        <div class="col-md-6 mb-3 form-field">
                            <label for="createCode" class="form-label">
                                Clave
                                <span class="text-danger required-marker">*</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field text-uppercase"
                                   id="createCode"
                                   name="code"
                                   placeholder="Ejemplo: PROD-001"
                                   minlength="2"
                                   maxlength="50"
                                   pattern="[A-Za-z0-9][A-Za-z0-9._\x2D]{1,49}"
                                   data-label="Clave"
                                   data-valid-message="Clave válida."
                                   autocomplete="off"
                                   required>
                            <div class="valid-feedback">Clave válida.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="createName" class="form-label">
                                Nombre
                                <span class="text-danger required-marker">*</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field"
                                   id="createName"
                                   name="name"
                                   placeholder="Ingrese el nombre del producto"
                                   minlength="2"
                                   maxlength="150"
                                   data-label="Nombre"
                                   data-valid-message="Nombre válido."
                                   autocomplete="off"
                                   required>
                            <div class="valid-feedback">Nombre válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="createMetric" class="form-label">
                            Unidad de medida
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <div class="d-flex align-items-start gap-2">
                            <div class="flex-grow-1">
                                <select class="form-select js-form-field"
                                        id="createMetric"
                                        name="idMetric"
                                        data-label="Unidad de medida"
                                        data-valid-message="Unidad seleccionada."
                                        required>
                                    <option value="">Seleccione una unidad</option>
                                    <c:forEach var="metric" items="${metrics}">
                                        <option value="${metric.idMetric}">
                                                ${metric.name} (${metric.shortName})
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="valid-feedback">Unidad seleccionada.</div>
                                <div class="invalid-feedback"></div>
                            </div>

                            <button type="button"
                                    class="btn btn-primary btn-open-quick-metric"
                                    data-target-select="createMetric"
                                    title="Registrar nueva unidad de medida"
                                    aria-label="Registrar nueva unidad de medida">
                                <i class="bi bi-plus-lg"></i>
                            </button>
                        </div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="createDescription" class="form-label">
                            Descripción
                            <span class="text-muted small">(opcional)</span>
                        </label>
                        <textarea class="form-control js-form-field"
                                  id="createDescription"
                                  name="description"
                                  rows="3"
                                  maxlength="500"
                                  placeholder="Descripción del producto"
                                  data-label="Descripción"></textarea>
                        <div class="valid-feedback">Descripción válida.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <section class="product-provider-section">
                        <div class="product-provider-header">
                            <div class="product-provider-header-info">
                                <h6 class="product-provider-title">Proveedores del producto</h6>
                                <p class="product-provider-description">Asocia uno o varios proveedores y captura su precio de compra actual.</p>
                            </div>

                            <button type="button"
                                    class="btn btn-secondary btn-open-quick-provider"
                                    data-target-list="createProviderList"
                                    data-parent-modal="modalCreate">
                                <i class="bi bi-building-add me-1"></i>
                                Nuevo proveedor
                            </button>
                        </div>

                        <div id="createProviderList"
                             class="product-provider-list"
                             data-provider-list>
                        </div>

                        <button type="button"
                                class="btn btn-primary btn-add-product-provider product-provider-add-row"
                                data-target-list="createProviderList">
                            <i class="bi bi-plus-lg me-1"></i>
                            Agregar otro proveedor
                        </button>

                        <div id="createProviderEmpty"
                             class="product-provider-empty"
                             data-provider-empty>
                            <i class="bi bi-truck me-1"></i>
                            Agrega al menos un proveedor.
                        </div>
                    </section>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                </button>

                <button type="submit"
                        class="btn btn-primary js-form-submit"
                        form="formCreateProduct"
                        data-loading-text="Guardando...">
                    <i class="bi bi-check-lg me-1"></i>
                    Guardar
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-neumorphic"
     id="modalView"
     tabindex="-1"
     aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-box-seam me-2" style="color:#6390ff;"></i>
                    Detalles del Producto
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <div class="text-center mb-4">
                    <i class="bi bi-box-seam" style="font-size:4rem;color:var(--text-muted);"></i>
                    <h4 id="viewProductName" class="mt-2" style="color:var(--text-color);">-</h4>
                    <span id="viewProductStatus" class="table-badge">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-hash me-2"></i>
                        Clave
                    </span>
                    <span class="modal-detail-value" id="viewProductCode">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-rulers me-2"></i>
                        Unidad de medida
                    </span>
                    <span class="modal-detail-value" id="viewProductMetric">-</span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-card-text me-2"></i>
                        Descripción
                    </span>
                    <span class="modal-detail-value" id="viewProductDescription">-</span>
                </div>

                <div class="mt-4">
                    <h6 style="color:var(--text-color);font-weight:700;">
                        <i class="bi bi-truck me-2"></i>
                        Proveedores asociados
                    </h6>

                    <div id="viewProductProviders"
                         class="product-provider-summary mt-3">
                    </div>

                    <div id="viewProductProvidersEmpty"
                         class="product-provider-empty mt-3"
                         style="display:none;">
                        No hay proveedores asociados.
                    </div>
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

<div class="modal fade modal-neumorphic"
     id="modalEdit"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-pencil-fill me-2" style="color:#ffc857;"></i>
                    Editar Producto
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <form id="formEditProduct"
                      class="js-form"
                      action="${pageContext.request.contextPath}/product/update"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="false">

                    <input type="hidden" id="editProductId" name="id">

                    <div class="row">
                        <div class="col-md-6 mb-3 form-field">
                            <label for="editCode" class="form-label">
                                Clave
                                <span class="text-danger required-marker">*</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field text-uppercase"
                                   id="editCode"
                                   name="code"
                                   placeholder="Ejemplo: PROD-001"
                                   minlength="2"
                                   maxlength="50"
                                   pattern="[A-Za-z0-9][A-Za-z0-9._\x2D]{1,49}"
                                   data-label="Clave"
                                   data-valid-message="Clave válida."
                                   autocomplete="off"
                                   required>
                            <div class="valid-feedback">Clave válida.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="editName" class="form-label">
                                Nombre
                                <span class="text-danger required-marker">*</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field"
                                   id="editName"
                                   name="name"
                                   placeholder="Ingrese el nombre del producto"
                                   minlength="2"
                                   maxlength="150"
                                   data-label="Nombre"
                                   data-valid-message="Nombre válido."
                                   autocomplete="off"
                                   required>
                            <div class="valid-feedback">Nombre válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="editMetric" class="form-label">
                            Unidad de medida
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <div class="d-flex align-items-start gap-2">
                            <div class="flex-grow-1">
                                <select class="form-select js-form-field"
                                        id="editMetric"
                                        name="idMetric"
                                        data-label="Unidad de medida"
                                        data-valid-message="Unidad seleccionada."
                                        required>
                                    <option value="">Seleccione una unidad</option>
                                    <c:forEach var="metric" items="${metrics}">
                                        <option value="${metric.idMetric}">
                                                ${metric.name} (${metric.shortName})
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="valid-feedback">Unidad seleccionada.</div>
                                <div class="invalid-feedback"></div>
                            </div>

                            <button type="button"
                                    class="btn btn-primary btn-open-quick-metric"
                                    data-target-select="editMetric"
                                    title="Registrar nueva unidad de medida"
                                    aria-label="Registrar nueva unidad de medida">
                                <i class="bi bi-plus-lg"></i>
                            </button>
                        </div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="editDescription" class="form-label">
                            Descripción
                            <span class="text-muted small">(opcional)</span>
                        </label>
                        <textarea class="form-control js-form-field"
                                  id="editDescription"
                                  name="description"
                                  rows="3"
                                  maxlength="500"
                                  placeholder="Descripción del producto"
                                  data-label="Descripción"></textarea>
                        <div class="valid-feedback">Descripción válida.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <section class="product-provider-section">
                        <div class="product-provider-header">
                            <div class="product-provider-header-info">
                                <h6 class="product-provider-title">Proveedores del producto</h6>
                                <p class="product-provider-description">Asocia uno o varios proveedores y captura su precio de compra actual.</p>
                            </div>

                            <button type="button"
                                    class="btn btn-secondary btn-open-quick-provider"
                                    data-target-list="editProviderList"
                                    data-parent-modal="modalEdit">
                                <i class="bi bi-building-add me-1"></i>
                                Nuevo proveedor
                            </button>
                        </div>

                        <div id="editProviderList"
                             class="product-provider-list"
                             data-provider-list>
                        </div>

                        <button type="button"
                                class="btn btn-primary btn-add-product-provider product-provider-add-row"
                                data-target-list="editProviderList">
                            <i class="bi bi-plus-lg me-1"></i>
                            Agregar otro proveedor
                        </button>

                        <div id="editProviderEmpty"
                             class="product-provider-empty"
                             data-provider-empty>
                            <i class="bi bi-truck me-1"></i>
                            Agrega al menos un proveedor.
                        </div>
                    </section>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                </button>

                <button type="button"
                        class="btn btn-primary"
                        id="btnOpenConfirmEdit">
                    <i class="bi bi-check-lg me-1"></i>
                    Guardar cambios
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-neumorphic"
     id="modalConfirmEdit"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-question-circle me-2" style="color:#ffc857;"></i>
                    Confirmar cambios
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body text-center">
                <p style="color:var(--text-color);font-size:16px;font-weight:500;">
                    ¿Deseas guardar los cambios del producto?
                </p>
                <p style="color:var(--text-muted);font-size:14px;">
                    Producto:
                    <strong id="editConfirmProductName">-</strong>
                </p>
            </div>

            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
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

<div class="modal fade modal-neumorphic"
     id="modalConfirmStatus"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-exclamation-circle me-2" style="color:#ffc857;"></i>
                    Cambiar estado
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body text-center">
                <p id="statusModalQuestion"
                   style="color:var(--text-color);font-size:16px;font-weight:500;">
                    ¿Deseas cambiar el estado de este producto?
                </p>

                <p style="color:var(--text-muted);font-size:14px;">
                    Producto:
                    <strong id="statusConfirmProductName">-</strong>
                </p>

                <p id="statusModalDescription"
                   style="color:var(--text-muted);font-size:13px;margin-bottom:0;">
                    El cambio se aplicará inmediatamente.
                </p>

                <form id="formChangeStatus"
                      class="js-form"
                      action="${pageContext.request.contextPath}/product/change-status"
                      method="post"
                      data-submit-mode="manual"
                      data-reset-on-close="false">
                    <input type="hidden" id="statusProductId" name="id">
                    <input type="hidden" id="statusNewValue" name="status">
                </form>
            </div>

            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                </button>

                <button type="button"
                        class="btn"
                        id="btnConfirmStatus"
                        data-loading-text="Procesando...">
                    <i id="statusConfirmButtonIcon" class="bi bi-check-lg me-1"></i>
                    <span id="statusConfirmButtonText">Confirmar</span>
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-neumorphic"
     id="modalQuickMetric"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-rulers me-2" style="color:#6390ff;"></i>
                    Nueva unidad de medida
                </h5>
                <button type="button"
                        class="btn-close"
                        id="btnCloseQuickMetric"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <p class="small mb-3 quick-metric-description">
                    La nueva unidad se agregará a los selectores y quedará seleccionada automáticamente.
                </p>

                <form id="formQuickMetric"
                      class="js-form"
                      action="${pageContext.request.contextPath}/metric/save"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="true">

                    <div class="mb-3 form-field">
                        <label for="quickMetricName" class="form-label">
                            Nombre
                            <span class="text-danger required-marker">*</span>
                        </label>
                        <input type="text"
                               class="form-control js-form-field"
                               id="quickMetricName"
                               name="name"
                               placeholder="Ejemplo: Kilogramos"
                               minlength="2"
                               maxlength="100"
                               data-label="Nombre"
                               data-valid-message="Nombre válido."
                               autocomplete="off"
                               required>
                        <div class="valid-feedback">Nombre válido.</div>
                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="quickMetricShortName" class="form-label">
                            Abreviatura
                            <span class="text-danger required-marker">*</span>
                        </label>
                        <input type="text"
                               class="form-control js-form-field text-uppercase"
                               id="quickMetricShortName"
                               name="shortName"
                               placeholder="Ejemplo: KG"
                               minlength="1"
                               maxlength="10"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9._\-]{1,10}"
                               data-label="Abreviatura"
                               data-valid-message="Abreviatura válida."
                               autocomplete="off"
                               required>
                        <div class="valid-feedback">Abreviatura válida.</div>
                        <div class="invalid-feedback"></div>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" id="btnCancelQuickMetric">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                </button>

                <button type="submit"
                        class="btn btn-primary"
                        form="formQuickMetric"
                        id="btnSaveQuickMetric"
                        data-loading-text="Guardando...">
                    <i class="bi bi-check-lg me-1"></i>
                    Guardar unidad
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-neumorphic"
     id="modalQuickProvider"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-truck me-2" style="color:#6390ff;"></i>
                    Nuevo proveedor
                </h5>

                <button type="button"
                        class="btn-close"
                        id="btnCloseQuickProvider"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <p class="small mb-3" style="color:var(--text-muted);">
                    El proveedor se agregará al producto automáticamente y se conservarán los datos ya capturados.
                </p>

                <form id="formQuickProvider"
                      class="js-form"
                      action="${pageContext.request.contextPath}/provider/save"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="true">

                    <div class="row">
                        <div class="col-md-6 mb-3 form-field">
                            <label for="quickProviderName" class="form-label">
                                Nombre comercial
                                <span class="text-danger required-marker">*</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field"
                                   id="quickProviderName"
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
                            <label for="quickProviderSocialCase" class="form-label">
                                Razón social
                                <span class="text-danger required-marker">*</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field"
                                   id="quickProviderSocialCase"
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
                            <label for="quickProviderRfc" class="form-label">
                                RFC
                                <span class="text-danger required-marker">*</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field text-uppercase"
                                   id="quickProviderRfc"
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
                            <label for="quickProviderPhone" class="form-label">
                                Teléfono
                                <span class="text-muted small">(opcional)</span>
                            </label>
                            <input type="tel"
                                   class="form-control js-form-field"
                                   id="quickProviderPhone"
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
                            <label for="quickProviderEmail" class="form-label">
                                Correo
                                <span class="text-muted small">(opcional)</span>
                            </label>
                            <input type="email"
                                   class="form-control js-form-field"
                                   id="quickProviderEmail"
                                   name="email"
                                   placeholder="proveedor@correo.com"
                                   maxlength="150"
                                   data-label="Correo"
                                   data-valid-message="Correo válido."
                                   autocomplete="email">
                            <div class="valid-feedback">Correo válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="quickProviderPostCode" class="form-label">
                                Código postal
                                <span class="text-muted small">(opcional)</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field"
                                   id="quickProviderPostCode"
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
                            <label for="quickProviderAddress" class="form-label">
                                Dirección
                                <span class="text-muted small">(opcional)</span>
                            </label>
                            <textarea class="form-control js-form-field"
                                      id="quickProviderAddress"
                                      name="address"
                                      rows="2"
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
                            <label for="quickProviderContactName" class="form-label">
                                Nombre
                                <span class="text-muted small">(opcional)</span>
                            </label>
                            <input type="text"
                                   class="form-control js-form-field"
                                   id="quickProviderContactName"
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
                            <label for="quickProviderContactPhone" class="form-label">
                                Teléfono
                                <span class="text-muted small">(opcional)</span>
                            </label>
                            <input type="tel"
                                   class="form-control js-form-field"
                                   id="quickProviderContactPhone"
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
                            <label for="quickProviderContactEmail" class="form-label">
                                Correo
                                <span class="text-muted small">(opcional)</span>
                            </label>
                            <input type="email"
                                   class="form-control js-form-field"
                                   id="quickProviderContactEmail"
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
                        id="btnCancelQuickProvider">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                </button>

                <button type="submit"
                        class="btn btn-primary"
                        form="formQuickProvider"
                        id="btnSaveQuickProvider"
                        data-loading-text="Guardando...">
                    <i class="bi bi-check-lg me-1"></i>
                    Guardar proveedor
                </button>
            </div>
        </div>
    </div>
</div>

<template id="productProviderRowTemplate">
    <div class="product-provider-row">
        <div class="form-field">
            <label class="form-label">
                Proveedor
                <span class="text-danger required-marker">*</span>
            </label>

            <select class="form-select js-form-field product-provider-select"
                    name="providerId[]"
                    data-label="Proveedor"
                    data-valid-message="Proveedor seleccionado."
                    required>
                <option value="">Seleccione un proveedor</option>

                <c:forEach var="provider" items="${providers}">
                    <option value="${provider.idProvider}"
                            data-provider-name="${fn:escapeXml(provider.name)}"
                            data-provider-rfc="${fn:escapeXml(provider.rfc)}">
                            ${provider.name} — ${provider.rfc}
                    </option>
                </c:forEach>
            </select>

            <div class="valid-feedback">Proveedor seleccionado.</div>
            <div class="invalid-feedback"></div>
        </div>

        <div class="form-field">
            <label class="form-label">
                Precio de compra
                <span class="text-danger required-marker">*</span>
            </label>

            <div class="input-group">
                <span class="input-group-text">$</span>

                <input type="number"
                       class="form-control js-form-field product-provider-price"
                       name="purchasePrice[]"
                       placeholder="0.00"
                       min="0"
                       max="9999999999.99"
                       step="0.01"
                       inputmode="decimal"
                       data-label="Precio de compra"
                       data-valid-message="Precio válido."
                       autocomplete="off"
                       required>
            </div>

            <div class="valid-feedback">Precio válido.</div>
            <div class="invalid-feedback"></div>
        </div>

        <button type="button"
                class="btn btn-danger product-provider-remove"
                title="Quitar proveedor"
                aria-label="Quitar proveedor">
            <i class="bi bi-trash"></i>
        </button>
    </div>
</template>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/table.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/form.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/toast.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/api.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/product.js?v=3"></script>
</body>
</html>
<%-- Administración de productos y sus relaciones con proveedores. --%>
