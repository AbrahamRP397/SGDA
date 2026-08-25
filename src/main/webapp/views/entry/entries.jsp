<%--
    Vista técnica: entries.
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
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Gestión de Entradas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/table.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/modals.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/form.css">
    <style>
        .entry-products-section{padding:16px;border-radius:16px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .entry-products-header{display:flex;align-items:flex-start;justify-content:space-between;gap:14px;margin-bottom:14px}
        .entry-products-header>div:first-child{min-width:0;flex:1}
        .entry-products-title{margin:0;color:var(--text-color);font-size:15px;font-weight:700}
        .entry-products-description{margin:4px 0 0;color:var(--text-muted);font-size:13px;line-height:1.45}
        .entry-provider-control{display:grid;grid-template-columns:minmax(0,1fr) 42px;gap:8px;align-items:start}
        .entry-provider-control .btn{width:42px;min-width:42px;height:42px;padding:0!important;display:grid;place-items:center}
        .entry-product-list{display:flex;flex-direction:column;gap:12px}
        .entry-product-row{display:grid;grid-template-columns:minmax(240px,1.5fr) minmax(90px,.45fr) minmax(180px,.7fr) minmax(180px,.7fr) 42px;gap:10px;align-items:end;padding:13px;border-radius:15px;background:var(--card-bg);box-shadow:var(--neumo-shadow)}
        .entry-product-row>.entry-product-field{min-width:0;margin:0}
        .entry-product-row .form-label{display:block;margin-bottom:6px}
        .entry-product-row .form-control,.entry-product-row .form-select{height:42px;min-width:0}
        .entry-product-row .input-group{display:flex;flex-wrap:nowrap!important;align-items:stretch;width:100%;min-width:0;height:42px;overflow:hidden;border-radius:12px;background:var(--input-bg);box-shadow:var(--neumo-shadow-inset)}
        .entry-product-row .input-group-text{flex:0 0 42px;width:42px;min-width:42px;height:42px;display:flex;align-items:center;justify-content:center;padding:0;border:0;border-radius:12px 0 0 12px;background:transparent;color:var(--text-muted)}
        .entry-product-row .input-group .form-control{flex:1 1 auto!important;width:1%!important;min-width:0!important;height:42px;margin:0;padding:9px 12px;border:0!important;border-radius:0 12px 12px 0!important;background:transparent!important;color:var(--text-color);box-shadow:none!important}
        .entry-product-row .input-group .form-control:focus{transform:none!important;outline:none;box-shadow:none!important}
        .entry-product-remove{width:42px;min-width:42px;height:42px;display:grid;place-items:center;padding:0!important;align-self:end}
        .entry-product-empty{padding:20px;border:1px dashed var(--border-color);border-radius:14px;color:var(--text-muted);font-size:13px;text-align:center}
        .entry-product-metric{display:block;margin-top:5px;color:var(--text-muted);font-size:12px}
        .entry-product-total-input{font-weight:700}
        .entry-summary{display:flex;align-items:center;justify-content:flex-end;gap:16px;margin-top:16px;padding:15px 17px;border-radius:14px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .entry-summary-label{color:var(--text-muted);font-size:14px;font-weight:600}
        .entry-summary-value{color:var(--text-color);font-size:22px;font-weight:800}
        .entry-detail-products{display:flex;flex-direction:column;gap:10px}
        .entry-detail-product{display:grid;grid-template-columns:minmax(0,1fr) auto;gap:15px;padding:13px;border-radius:14px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .entry-detail-product-name{display:block;color:var(--text-color);font-weight:700}
        .entry-detail-product-meta{display:block;margin-top:3px;color:var(--text-muted);font-size:12px}
        .entry-detail-product-values{text-align:right}
        .entry-detail-product-price{display:block;color:var(--text-color);font-weight:700}
        .entry-detail-product-subtotal{display:block;margin-top:3px;color:var(--text-muted);font-size:12px}
        .entry-provider-help{display:none;margin-top:8px;color:var(--text-muted);font-size:12px}
        .entry-provider-help.is-visible{display:block}
        .entry-loading-products{display:none;align-items:center;gap:8px;margin-top:8px;color:var(--text-muted);font-size:12px}
        .entry-loading-products.is-visible{display:flex}
        html[data-theme="dark"] .entry-product-row .input-group,html[data-bs-theme="dark"] .entry-product-row .input-group{color-scheme:dark}
        @media(max-width:991.98px){
            .entry-product-row{grid-template-columns:minmax(0,1fr) minmax(90px,.45fr) minmax(160px,.7fr) minmax(160px,.7fr) 42px}
            .entry-product-row .entry-product-field:first-child{grid-column:1/-1}
        }
        @media(max-width:767.98px){
            .entry-product-row{grid-template-columns:1fr 1fr 42px}
            .entry-product-row .entry-product-field:first-child{grid-column:1/-1}
            .entry-product-row .entry-product-field:nth-child(2){grid-column:1}
            .entry-product-row .entry-product-field:nth-child(3){grid-column:2/-1}
            .entry-product-row .entry-product-field:nth-child(4){grid-column:1/3}
            .entry-product-remove{grid-column:3}
        }
        @media(max-width:575.98px){
            .entry-products-section{padding:12px;border-radius:14px}
            .entry-products-header{flex-direction:column;align-items:stretch}
            .entry-products-header .btn{width:100%;justify-content:center}
            .entry-provider-control{grid-template-columns:minmax(0,1fr) 42px}
            .entry-product-row{grid-template-columns:1fr;padding:11px}
            .entry-product-row .entry-product-field:first-child,.entry-product-row .entry-product-field:nth-child(2),.entry-product-row .entry-product-field:nth-child(3),.entry-product-row .entry-product-field:nth-child(4),.entry-product-remove{grid-column:auto}
            .entry-product-remove{width:100%;display:flex;align-items:center;justify-content:center;gap:7px}
            .entry-product-remove::after{content:"Quitar producto"}
            .entry-summary{justify-content:space-between;padding:13px 14px}
            .entry-summary-value{font-size:20px}
            .entry-detail-product{grid-template-columns:1fr}
            .entry-detail-product-values{text-align:left}
        }
        @media(max-width:380px){
            .entry-products-section{padding:10px}
            .entry-product-row{padding:10px;gap:9px}
            .entry-products-title{font-size:14px}
            .entry-products-description{font-size:12px}
            .entry-provider-control{grid-template-columns:minmax(0,1fr) 40px;gap:6px}
            .entry-provider-control .btn{width:40px;min-width:40px}
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
                    <i class="bi bi-box-arrow-in-down"></i>
                    Gestión de Entradas
                </h2>

                <div class="table-header-actions">
                    <button type="button" class="table-primary-btn" id="btnNewEntry">
                        <i class="bi bi-plus-lg"></i>
                        Registrar entrada
                    </button>
                </div>
            </div>

            <section class="table-toolbar">
                <div class="table-toolbar-top">
                    <div class="table-search">
                        <i class="bi bi-search"></i>
                        <input type="search"
                               class="js-table-search"
                               data-table-target="entriesTable"
                               placeholder="Buscar por folio, factura, proveedor, usuario o producto..."
                               autocomplete="off">
                    </div>

                    <button type="button"
                            class="table-toolbar-btn js-filter-toggle"
                            data-filter-target="entriesFilters">
                        <i class="bi bi-funnel"></i>
                        <span>Filtros</span>
                    </button>

                    <button type="button"
                            class="table-toolbar-btn js-clear-filters"
                            data-table-target="entriesTable">
                        <i class="bi bi-eraser"></i>
                        <span>Limpiar</span>
                    </button>
                </div>

                <div class="table-filters" id="entriesFilters" style="--filter-columns:2;">
                    <div class="table-filter-group">
                        <label class="table-filter-label" for="entryProviderFilter">Proveedor</label>
                        <select id="entryProviderFilter"
                                class="form-select js-table-filter"
                                data-table-target="entriesTable"
                                data-filter-field="provider"
                                aria-label="Filtrar por proveedor">
                            <option value="all">Todos</option>
                            <c:forEach var="provider" items="${providers}">
                                <option value="${provider.idProvider}">${provider.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="table-filter-group">
                        <label class="table-filter-label" for="entryDateFilter">Fecha</label>
                        <input type="date"
                               id="entryDateFilter"
                               class="form-control js-table-filter"
                               data-table-target="entriesTable"
                               data-filter-field="date">
                    </div>
                </div>
            </section>

            <section class="table-panel">
                <div class="table-responsive" style="${empty entries ? 'display:none;' : ''}">
                    <table class="app-table" id="entriesTable" style="--table-min-width:1150px;">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Folio</th>
                            <th>Fecha</th>
                            <th>Factura / remisión</th>
                            <th>Proveedor</th>
                            <th>Productos</th>
                            <th>Total</th>
                            <th>Registró</th>
                            <th class="table-text-center">Acciones</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="entry" items="${entries}">
                            <c:set var="productSearch" value=""/>

                            <c:forEach var="product" items="${entry.products}">
                                <c:set var="productSearch"
                                       value="${productSearch} ${product.productCode} ${product.productName} ${product.metricName} ${product.metricShortName}"/>
                            </c:forEach>

                            <tr class="js-table-row entry-table-row"
                                data-id="${entry.idEntry}"
                                data-folio="${fn:escapeXml(entry.folioNumber)}"
                                data-invoice="${fn:escapeXml(entry.invoiceNumber)}"
                                data-provider="${entry.idProvider}"
                                data-provider-name="${fn:escapeXml(entry.providerName)}"
                                data-provider-rfc="${fn:escapeXml(entry.providerRfc)}"
                                data-user-name="${fn:escapeXml(entry.userName)}"
                                data-date="${entry.changeDate.toLocalDate()}"
                                data-date-time="${entry.changeDate}"
                                data-total="${entry.totalAllPrices}"
                                data-search="${fn:escapeXml(entry.folioNumber)} ${fn:escapeXml(entry.invoiceNumber)} ${fn:escapeXml(entry.providerName)} ${fn:escapeXml(entry.providerRfc)} ${fn:escapeXml(entry.userName)} ${fn:escapeXml(productSearch)}">

                                <td class="table-cell-secondary table-cell-nowrap">${entry.idEntry}</td>
                                <td class="table-cell-primary table-cell-nowrap">${entry.folioNumber}</td>

                                <td class="table-cell-secondary table-cell-nowrap entry-date-cell"
                                    data-date-value="${entry.changeDate}">
                                        ${entry.changeDate}
                                </td>

                                <td class="table-cell-primary table-cell-nowrap">${entry.invoiceNumber}</td>

                                <td>
                                    <span class="table-cell-primary">${entry.providerName}</span>
                                    <span class="table-cell-secondary d-block small">${entry.providerRfc}</span>
                                </td>

                                <td>
                                    <span class="table-badge table-badge-info">
                                        ${fn:length(entry.products)}
                                        ${fn:length(entry.products) == 1 ? 'producto' : 'productos'}
                                    </span>
                                </td>

                                <td class="table-cell-primary table-cell-nowrap entry-money-cell"
                                    data-money-value="${entry.totalAllPrices}">
                                        ${entry.totalAllPrices}
                                </td>

                                <td class="table-cell-secondary">${entry.userName}</td>

                                <td>
                                    <div class="table-actions">
                                        <button type="button"
                                                class="table-action-btn table-action-view btn-view-entry"
                                                title="Ver detalles"
                                                aria-label="Ver detalles">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>

                                    <div class="entry-products-data" hidden>
                                        <c:forEach var="product" items="${entry.products}">
                                            <span class="entry-product-data"
                                                  data-id-entry-product="${product.idEntryProduct}"
                                                  data-id-product-provider="${product.idProductProvider}"
                                                  data-product-code="${fn:escapeXml(product.productCode)}"
                                                  data-product-name="${fn:escapeXml(product.productName)}"
                                                  data-metric-name="${fn:escapeXml(product.metricName)}"
                                                  data-metric-short-name="${fn:escapeXml(product.metricShortName)}"
                                                  data-quantity="${product.quantity}"
                                                  data-remaining-quantity="${product.remainingQuantity}"
                                                  data-unit-price="${product.unitPrice}"
                                                  data-total-price="${product.totalPrice}">
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
                     data-table-target="entriesTable"
                     data-page-size="5"
                     style="${empty entries ? 'display:none;' : ''}">

                    <div class="table-pagination-left">
                        <label class="table-page-size-label" for="entriesPageSize">Mostrar</label>
                        <select id="entriesPageSize"
                                class="table-page-size-select js-page-size"
                                data-table-target="entriesTable"
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

                <div id="entriesGeneralEmptyState"
                     class="table-empty-state"
                     style="${empty entries ? 'display:block;' : 'display:none;'}">
                    <i class="bi bi-inbox"></i>
                    No hay entradas registradas.
                </div>

                <div id="entriesFilterEmptyState"
                     class="table-empty-state js-filter-empty-state"
                     style="display:none;">
                    <i class="bi bi-search"></i>
                    No se encontraron entradas con esos filtros.
                </div>
            </section>
        </div>
    </main>
</div>

<div class="modal fade modal-neumorphic"
     id="modalCreateEntry"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-box-arrow-in-down me-2" style="color:#57d38c;"></i>
                    Registrar entrada
                </h5>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <form id="formCreateEntry"
                      class="js-form"
                      action="${pageContext.request.contextPath}/entry/save"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="true">

                    <div class="row">
                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryInvoiceNumber" class="form-label">
                                Número de factura o remisión
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <input type="text"
                                   id="entryInvoiceNumber"
                                   name="invoiceNumber"
                                   class="form-control js-form-field text-uppercase"
                                   placeholder="Ejemplo: FAC-2026-001"
                                   minlength="1"
                                   maxlength="50"
                                   pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ0-9._/#\x2D\s]{1,50}"
                                   data-label="Número de factura o remisión"
                                   data-valid-message="Número válido."
                                   autocomplete="off"
                                   required>

                            <div class="valid-feedback">Número válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryProvider" class="form-label">
                                Proveedor
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <div class="entry-provider-control">
                                <select id="entryProvider"
                                        name="idProvider"
                                        class="form-select js-form-field"
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

                                <button type="button"
                                        id="btnOpenQuickProviderEntry"
                                        class="btn btn-primary"
                                        title="Registrar nuevo proveedor"
                                        aria-label="Registrar nuevo proveedor">
                                    <i class="bi bi-plus-lg"></i>
                                </button>
                            </div>

                            <div class="valid-feedback">Proveedor seleccionado.</div>
                            <div class="invalid-feedback"></div>

                            <span id="entryProviderHelp"
                                  class="entry-provider-help">
                                Al cambiar el proveedor se limpiarán los productos agregados.
                            </span>

                            <span id="entryProductsLoading"
                                  class="entry-loading-products">
                                <span class="spinner-border spinner-border-sm"
                                      aria-hidden="true"></span>
                                Consultando productos del proveedor...
                            </span>
                        </div>
                    </div>

                    <section class="entry-products-section">
                        <div class="entry-products-header">
                            <div>
                                <h6 class="entry-products-title">
                                    Productos recibidos
                                </h6>

                                <p class="entry-products-description">
                                    Selecciona los productos entregados, la cantidad y el precio indicado en la factura.
                                </p>
                            </div>

                            <button type="button"
                                    id="btnAddEntryProduct"
                                    class="btn btn-primary"
                                    disabled>
                                <i class="bi bi-plus-lg me-1"></i>
                                Agregar producto
                            </button>
                        </div>

                        <div id="entryProductList"
                             class="entry-product-list"
                             data-entry-product-list>
                        </div>

                        <div id="entryProductEmpty"
                             class="entry-product-empty">
                            <i class="bi bi-box-seam me-1"></i>
                            Selecciona un proveedor y agrega al menos un producto.
                        </div>

                        <div class="entry-summary">
                            <span class="entry-summary-label">
                                Total de la entrada
                            </span>

                            <strong id="entryGrandTotal"
                                    class="entry-summary-value"
                                    data-total-value="0">
                                $0.00
                            </strong>
                        </div>
                    </section>
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
                        class="btn btn-success js-form-submit"
                        form="formCreateEntry"
                        id="btnSaveEntry"
                        data-loading-text="Registrando...">
                    <i class="bi bi-check-lg me-1"></i>
                    Registrar entrada
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-neumorphic"
     id="modalQuickProviderEntry"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-truck me-2"
                       style="color:#6390ff;"></i>
                    Nuevo proveedor
                </h5>

                <button type="button"
                        class="btn-close"
                        id="btnCloseQuickProviderEntry"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <p class="small mb-3"
                   style="color:var(--text-muted);">
                    Registra el proveedor sin perder los datos de la entrada.
                </p>

                <form id="formQuickProviderEntry"
                      class="js-form"
                      action="${pageContext.request.contextPath}/provider/save"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="true">

                    <div class="row">
                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryQuickProviderName"
                                   class="form-label">
                                Nombre comercial
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <input type="text"
                                   class="form-control js-form-field"
                                   id="entryQuickProviderName"
                                   name="name"
                                   minlength="2"
                                   maxlength="150"
                                   placeholder="Nombre comercial"
                                   data-label="Nombre comercial"
                                   data-valid-message="Nombre válido."
                                   autocomplete="off"
                                   required>

                            <div class="valid-feedback">Nombre válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryQuickProviderSocialCase"
                                   class="form-label">
                                Razón social
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <input type="text"
                                   class="form-control js-form-field"
                                   id="entryQuickProviderSocialCase"
                                   name="socialCase"
                                   minlength="2"
                                   maxlength="150"
                                   placeholder="Razón social"
                                   data-label="Razón social"
                                   data-valid-message="Razón social válida."
                                   autocomplete="off"
                                   required>

                            <div class="valid-feedback">
                                Razón social válida.
                            </div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryQuickProviderRfc"
                                   class="form-label">
                                RFC
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <input type="text"
                                   class="form-control js-form-field text-uppercase"
                                   id="entryQuickProviderRfc"
                                   name="rfc"
                                   minlength="12"
                                   maxlength="13"
                                   pattern="[A-Za-zÑñ&]{3,4}[0-9]{6}[A-Za-z0-9]{3}"
                                   placeholder="Ejemplo: ABC123456XYZ"
                                   data-label="RFC"
                                   data-valid-message="RFC válido."
                                   autocomplete="off"
                                   required>

                            <div class="valid-feedback">RFC válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryQuickProviderPhone"
                                   class="form-label">
                                Teléfono
                                <span class="text-muted small">(opcional)</span>
                            </label>

                            <input type="tel"
                                   class="form-control js-form-field"
                                   id="entryQuickProviderPhone"
                                   name="phone"
                                   minlength="10"
                                   maxlength="10"
                                   pattern="[0-9]{10}"
                                   inputmode="numeric"
                                   placeholder="10 dígitos"
                                   data-label="Teléfono"
                                   data-valid-message="Teléfono válido."
                                   autocomplete="tel">

                            <div class="valid-feedback">Teléfono válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryQuickProviderEmail"
                                   class="form-label">
                                Correo
                                <span class="text-muted small">(opcional)</span>
                            </label>

                            <input type="email"
                                   class="form-control js-form-field"
                                   id="entryQuickProviderEmail"
                                   name="email"
                                   maxlength="150"
                                   placeholder="proveedor@correo.com"
                                   data-label="Correo"
                                   data-valid-message="Correo válido."
                                   autocomplete="email">

                            <div class="valid-feedback">Correo válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-md-6 mb-3 form-field">
                            <label for="entryQuickProviderPostCode"
                                   class="form-label">
                                Código postal
                                <span class="text-muted small">(opcional)</span>
                            </label>

                            <input type="text"
                                   class="form-control js-form-field"
                                   id="entryQuickProviderPostCode"
                                   name="postCode"
                                   minlength="5"
                                   maxlength="5"
                                   pattern="[0-9]{5}"
                                   inputmode="numeric"
                                   placeholder="5 dígitos"
                                   data-label="Código postal"
                                   data-valid-message="Código postal válido."
                                   autocomplete="postal-code">

                            <div class="valid-feedback">
                                Código postal válido.
                            </div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-12 mb-0 form-field">
                            <label for="entryQuickProviderAddress"
                                   class="form-label">
                                Dirección
                                <span class="text-muted small">(opcional)</span>
                            </label>

                            <textarea class="form-control js-form-field"
                                      id="entryQuickProviderAddress"
                                      name="address"
                                      rows="2"
                                      maxlength="300"
                                      placeholder="Dirección del proveedor"
                                      data-label="Dirección"></textarea>

                            <div class="valid-feedback">Dirección válida.</div>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button"
                        class="btn btn-secondary"
                        id="btnCancelQuickProviderEntry">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                </button>

                <button type="submit"
                        class="btn btn-primary"
                        form="formQuickProviderEntry"
                        id="btnSaveQuickProviderEntry"
                        data-loading-text="Guardando...">
                    <i class="bi bi-check-lg me-1"></i>
                    Guardar proveedor
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-neumorphic"
     id="modalViewEntry"
     tabindex="-1"
     aria-hidden="true">

    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-receipt me-2"
                       style="color:#6390ff;"></i>
                    Detalles de la entrada
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
                    <i class="bi bi-box-arrow-in-down"
                       style="font-size:4rem;color:#57d38c;"></i>

                    <h4 id="viewEntryFolio"
                        class="mt-2 mb-2"
                        style="color:var(--text-color);">
                        -
                    </h4>

                    <span class="table-badge table-badge-success">
                        Entrada registrada
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-calendar-event me-2"></i>
                        Fecha
                    </span>

                    <span class="modal-detail-value"
                          id="viewEntryDate">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-receipt-cutoff me-2"></i>
                        Factura o remisión
                    </span>

                    <span class="modal-detail-value"
                          id="viewEntryInvoice">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-truck me-2"></i>
                        Proveedor
                    </span>

                    <span class="modal-detail-value"
                          id="viewEntryProvider">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-person me-2"></i>
                        Registró
                    </span>

                    <span class="modal-detail-value"
                          id="viewEntryUser">
                        -
                    </span>
                </div>

                <div class="mt-4">
                    <h6 style="color:var(--text-color);font-weight:700;">
                        <i class="bi bi-boxes me-2"></i>
                        Productos recibidos
                    </h6>

                    <div id="viewEntryProducts"
                         class="entry-detail-products mt-3">
                    </div>
                </div>

                <div class="entry-summary mt-4">
                    <span class="entry-summary-label">
                        Total de la entrada
                    </span>

                    <strong id="viewEntryTotal"
                            class="entry-summary-value">
                        $0.00
                    </strong>
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

<template id="entryProductRowTemplate">
    <div class="entry-product-row">
        <div class="form-field entry-product-field">
            <label class="form-label">
                Producto
                <span class="text-danger required-marker">*</span>
            </label>

            <select name="idProductProvider[]"
                    class="form-select js-form-field entry-product-select"
                    data-label="Producto"
                    data-valid-message="Producto seleccionado."
                    required>
                <option value="">Seleccione un producto</option>
            </select>

            <span class="entry-product-metric">
                Unidad: —
            </span>

            <div class="valid-feedback">
                Producto seleccionado.
            </div>
            <div class="invalid-feedback"></div>
        </div>

        <div class="form-field entry-product-field">
            <label class="form-label">
                Cantidad
                <span class="text-danger required-marker">*</span>
            </label>

            <input type="number"
                   name="quantity[]"
                   class="form-control js-form-field entry-product-quantity"
                   placeholder="0"
                   min="1"
                   max="999999999"
                   step="1"
                   inputmode="numeric"
                   data-label="Cantidad"
                   data-valid-message="Cantidad válida."
                   autocomplete="off"
                   required>

            <div class="valid-feedback">Cantidad válida.</div>
            <div class="invalid-feedback"></div>
        </div>

        <div class="form-field entry-product-field">
            <label class="form-label">
                Precio unitario
                <span class="text-danger required-marker">*</span>
            </label>

            <div class="input-group">
                <span class="input-group-text">$</span>

                <input type="number"
                       name="unitPrice[]"
                       class="form-control js-form-field entry-product-price"
                       placeholder="0.00"
                       min="0"
                       max="9999999999.99"
                       step="0.01"
                       inputmode="decimal"
                       data-label="Precio unitario"
                       data-valid-message="Precio válido."
                       autocomplete="off"
                       required>
            </div>

            <div class="valid-feedback">Precio válido.</div>
            <div class="invalid-feedback"></div>
        </div>

        <div class="form-field entry-product-field">
            <label class="form-label">
                Subtotal
            </label>

            <div class="input-group">
                <span class="input-group-text">$</span>

                <input type="text"
                       class="form-control entry-product-total-input"
                       value="0.00"
                       tabindex="-1"
                       readonly>
            </div>
        </div>

        <button type="button"
                class="btn btn-danger entry-product-remove"
                title="Quitar producto"
                aria-label="Quitar producto">
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
<script src="${pageContext.request.contextPath}/assets/js/entry.js?v=2"></script>
</body>
</html>
<%-- Registro transaccional de entradas y lotes de inventario. --%>
