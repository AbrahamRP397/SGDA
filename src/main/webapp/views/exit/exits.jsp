<%--
    Vista técnica: exits.
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
    <title>Gestión de Salidas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/table.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/modals.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/form.css">

    <style>
        .exit-header-field{min-width:0}
        .exit-area-control{display:grid;grid-template-columns:minmax(0,1fr) 42px;gap:8px;align-items:start}
        .exit-area-control .btn{width:42px;min-width:42px;height:42px;padding:0!important;display:grid;place-items:center}
        .exit-products-section{padding:16px;border-radius:16px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .exit-products-header{display:flex;align-items:flex-start;justify-content:space-between;gap:14px;margin-bottom:14px}
        .exit-products-header>div:first-child{min-width:0;flex:1}
        .exit-products-title{margin:0;color:var(--text-color);font-size:15px;font-weight:700}
        .exit-products-description{margin:4px 0 0;color:var(--text-muted);font-size:13px;line-height:1.45}
        .exit-product-list{display:flex;flex-direction:column;gap:12px}
        .exit-product-row{display:grid;grid-template-columns:minmax(260px,1.6fr) minmax(110px,.45fr) minmax(190px,.7fr) 42px;gap:10px;align-items:end;padding:13px;border-radius:15px;background:var(--card-bg);box-shadow:var(--neumo-shadow)}
        .exit-product-row>.exit-product-field{min-width:0;margin:0}
        .exit-product-row .form-label{display:block;margin-bottom:6px}
        .exit-product-row .form-control,.exit-product-row .form-select{height:42px;min-width:0}
        .exit-product-row .input-group{display:flex;flex-wrap:nowrap!important;align-items:stretch;width:100%;min-width:0;height:42px;overflow:hidden;border-radius:12px;background:var(--input-bg);box-shadow:var(--neumo-shadow-inset)}
        .exit-product-row .input-group-text{flex:0 0 42px;width:42px;min-width:42px;height:42px;display:flex;align-items:center;justify-content:center;padding:0;border:0;border-radius:12px 0 0 12px;background:transparent;color:var(--text-muted)}
        .exit-product-row .input-group .form-control{flex:1 1 auto!important;width:1%!important;min-width:0!important;height:42px;margin:0;padding:9px 12px;border:0!important;border-radius:0 12px 12px 0!important;background:transparent!important;color:var(--text-color);box-shadow:none!important}
        .exit-product-row .input-group .form-control:focus{transform:none!important;outline:none;box-shadow:none!important}
        .exit-product-remove{width:42px;min-width:42px;height:42px;display:grid;place-items:center;padding:0!important;align-self:end}
        .exit-product-empty{padding:20px;border:1px dashed var(--border-color);border-radius:14px;color:var(--text-muted);font-size:13px;text-align:center}
        .exit-product-metric,.exit-product-stock{display:block;margin-top:5px;color:var(--text-muted);font-size:12px}
        .exit-product-stock strong{color:var(--text-color)}
        .exit-summary{display:flex;align-items:center;justify-content:space-between;gap:16px;margin-top:16px;padding:15px 17px;border-radius:14px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .exit-summary-item{display:flex;flex-direction:column;gap:3px}
        .exit-summary-label{color:var(--text-muted);font-size:13px;font-weight:600}
        .exit-summary-value{color:var(--text-color);font-size:20px;font-weight:800}
        .exit-detail-products{display:flex;flex-direction:column;gap:12px}
        .exit-detail-product{padding:14px;border-radius:14px;background:var(--card-bg);box-shadow:var(--neumo-shadow-inset)}
        .exit-detail-product-header{display:flex;justify-content:space-between;gap:15px}
        .exit-detail-product-name{display:block;color:var(--text-color);font-weight:700}
        .exit-detail-product-meta{display:block;margin-top:3px;color:var(--text-muted);font-size:12px}
        .exit-detail-product-values{text-align:right;flex-shrink:0}
        .exit-detail-product-price{display:block;color:var(--text-color);font-weight:700}
        .exit-detail-product-subtotal{display:block;margin-top:3px;color:var(--text-muted);font-size:12px}
        .exit-allocation-list{display:flex;flex-direction:column;gap:8px;margin-top:12px}
        .exit-allocation-item{display:grid;grid-template-columns:minmax(0,1fr) auto;gap:12px;padding:10px 12px;border-radius:12px;background:var(--card-bg);box-shadow:var(--neumo-shadow)}
        .exit-allocation-provider{display:block;color:var(--text-color);font-size:13px;font-weight:600}
        .exit-allocation-folio{display:block;color:var(--text-muted);font-size:11px}
        .exit-allocation-values{text-align:right;color:var(--text-color);font-size:12px;font-weight:700}
        .exit-products-loading{display:none;align-items:center;gap:8px;margin-top:8px;color:var(--text-muted);font-size:12px}
        .exit-products-loading.is-visible{display:flex}
        html[data-theme="dark"] .exit-product-row .input-group,html[data-bs-theme="dark"] .exit-product-row .input-group{color-scheme:dark}

        @media(max-width:991.98px){
            .exit-product-row{grid-template-columns:minmax(0,1fr) minmax(110px,.45fr) minmax(170px,.7fr) 42px}
            .exit-product-row .exit-product-field:first-child{grid-column:1/-1}
        }

        @media(max-width:767.98px){
            .exit-product-row{grid-template-columns:1fr 1fr 42px}
            .exit-product-row .exit-product-field:first-child{grid-column:1/-1}
            .exit-product-row .exit-product-field:nth-child(2){grid-column:1}
            .exit-product-row .exit-product-field:nth-child(3){grid-column:2}
            .exit-product-remove{grid-column:3}
        }

        @media(max-width:575.98px){
            .exit-products-section{padding:12px;border-radius:14px}
            .exit-products-header{flex-direction:column;align-items:stretch}
            .exit-products-header .btn{width:100%;justify-content:center}
            .exit-area-control{grid-template-columns:minmax(0,1fr) 42px}
            .exit-product-row{grid-template-columns:1fr;padding:11px}
            .exit-product-row .exit-product-field:first-child,.exit-product-row .exit-product-field:nth-child(2),.exit-product-row .exit-product-field:nth-child(3),.exit-product-remove{grid-column:auto}
            .exit-product-remove{width:100%;display:flex;align-items:center;justify-content:center;gap:7px}
            .exit-product-remove::after{content:"Quitar producto"}
            .exit-summary{flex-direction:column;align-items:stretch;padding:13px 14px}
            .exit-summary-item{flex-direction:row;align-items:center;justify-content:space-between}
            .exit-detail-product-header{flex-direction:column}
            .exit-detail-product-values{text-align:left}
            .exit-allocation-item{grid-template-columns:1fr}
            .exit-allocation-values{text-align:left}
        }

        @media(max-width:380px){
            .exit-products-section{padding:10px}
            .exit-product-row{padding:10px;gap:9px}
            .exit-products-title{font-size:14px}
            .exit-products-description{font-size:12px}
            .exit-area-control{grid-template-columns:minmax(0,1fr) 40px;gap:6px}
            .exit-area-control .btn{width:40px;min-width:40px}
        }

        @keyframes stock-spin{
            from{transform:rotate(0deg)}
            to{transform:rotate(360deg)}
        }

        .spin{display:inline-block;animation:stock-spin .8s linear infinite}
    </style>
</head>

<body data-context-path="${pageContext.request.contextPath}">
<jsp:include page="/components/theme-toggle.jsp"/>

<div class="d-flex">
    <jsp:include page="/components/sidebar.jsp"/>

    <main class="content p-3 p-md-4">
        <button type="button"
                id="btnOpenSidebar"
                class="sidebar-open-button"
                aria-label="Abrir menú">
            <i class="bi bi-list"></i>
            <span>Menú</span>
        </button>

        <div class="container-fluid px-0">
            <div class="table-page-header">
                <h2 class="table-page-title">
                    <i class="bi bi-box-arrow-up"></i>
                    Gestión de Salidas
                </h2>

                <div class="table-header-actions">
                    <button type="button"
                            class="table-primary-btn"
                            id="btnNewExit">
                        <i class="bi bi-plus-lg"></i>
                        Registrar salida
                    </button>
                </div>
            </div>

            <section class="table-toolbar">
                <div class="table-toolbar-top">
                    <div class="table-search">
                        <i class="bi bi-search"></i>

                        <input type="search"
                               class="js-table-search"
                               data-table-target="exitsTable"
                               placeholder="Buscar por folio, documento, área, receptor o producto..."
                               autocomplete="off">
                    </div>

                    <button type="button"
                            class="table-toolbar-btn js-filter-toggle"
                            data-filter-target="exitsFilters">
                        <i class="bi bi-funnel"></i>
                        <span>Filtros</span>
                    </button>

                    <button type="button"
                            class="table-toolbar-btn js-clear-filters"
                            data-table-target="exitsTable">
                        <i class="bi bi-eraser"></i>
                        <span>Limpiar</span>
                    </button>
                </div>

                <div class="table-filters"
                     id="exitsFilters"
                     style="--filter-columns:2;">

                    <div class="table-filter-group">
                        <label for="exitAreaFilter"
                               class="table-filter-label">
                            Área
                        </label>

                        <select id="exitAreaFilter"
                                class="form-select js-table-filter"
                                data-table-target="exitsTable"
                                data-filter-field="area">
                            <option value="all">Todas</option>

                            <c:forEach var="area" items="${areas}">
                                <option value="${area.idArea}">
                                        ${area.name} (${area.shortName})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="table-filter-group">
                        <label for="exitDateFilter"
                               class="table-filter-label">
                            Fecha
                        </label>

                        <input type="date"
                               id="exitDateFilter"
                               class="form-control js-table-filter"
                               data-table-target="exitsTable"
                               data-filter-field="date">
                    </div>
                </div>
            </section>

            <section class="table-panel">
                <div class="table-responsive"
                     style="${empty exits ? 'display:none;' : ''}">

                    <table class="app-table"
                           id="exitsTable"
                           style="--table-min-width:1200px;">

                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Folio</th>
                            <th>Fecha</th>
                            <th>Documento</th>
                            <th>Área</th>
                            <th>Receptor</th>
                            <th>Productos</th>
                            <th>Costo total</th>
                            <th>Registró</th>
                            <th class="table-text-center">Acciones</th>
                        </tr>
                        </thead>

                        <tbody>
                        <c:forEach var="exit" items="${exits}">
                            <c:set var="productSearch" value=""/>

                            <c:forEach var="product" items="${exit.products}">
                                <c:set var="productSearch"
                                       value="${productSearch} ${product.productCode} ${product.productName} ${product.metricName} ${product.metricShortName}"/>
                            </c:forEach>

                            <tr class="js-table-row exit-table-row"
                                data-id="${exit.idExit}"
                                data-folio="${fn:escapeXml(exit.folioNumber)}"
                                data-invoice="${fn:escapeXml(exit.invoiceNumber)}"
                                data-area="${exit.idArea}"
                                data-area-name="${fn:escapeXml(exit.areaName)}"
                                data-area-short-name="${fn:escapeXml(exit.areaShortName)}"
                                data-buyer-name="${fn:escapeXml(exit.buyerName)}"
                                data-user-name="${fn:escapeXml(exit.userName)}"
                                data-date="${exit.changeDate.toLocalDate()}"
                                data-date-time="${exit.changeDate}"
                                data-total="${exit.totalAllPrices}"
                                data-search="${fn:escapeXml(exit.folioNumber)} ${fn:escapeXml(exit.invoiceNumber)} ${fn:escapeXml(exit.areaName)} ${fn:escapeXml(exit.areaShortName)} ${fn:escapeXml(exit.buyerName)} ${fn:escapeXml(exit.userName)} ${fn:escapeXml(productSearch)}">

                                <td class="table-cell-secondary table-cell-nowrap">
                                        ${exit.idExit}
                                </td>

                                <td class="table-cell-primary table-cell-nowrap">
                                        ${exit.folioNumber}
                                </td>

                                <td class="table-cell-secondary table-cell-nowrap exit-date-cell"
                                    data-date-value="${exit.changeDate}">
                                        ${exit.changeDate}
                                </td>

                                <td class="table-cell-primary table-cell-nowrap">
                                        ${exit.invoiceNumber}
                                </td>

                                <td>
                                    <span class="table-cell-primary">
                                            ${exit.areaName}
                                    </span>

                                    <span class="table-cell-secondary d-block small">
                                            ${exit.areaShortName}
                                    </span>
                                </td>

                                <td class="table-cell-secondary">
                                        ${exit.buyerName}
                                </td>

                                <td>
                                    <span class="table-badge table-badge-info">
                                        ${fn:length(exit.products)}
                                        ${fn:length(exit.products) == 1 ? 'producto' : 'productos'}
                                    </span>
                                </td>

                                <td class="table-cell-primary table-cell-nowrap exit-money-cell"
                                    data-money-value="${exit.totalAllPrices}">
                                        ${exit.totalAllPrices}
                                </td>

                                <td class="table-cell-secondary">
                                        ${exit.userName}
                                </td>

                                <td>
                                    <div class="table-actions">
                                        <button type="button"
                                                class="table-action-btn table-action-view btn-view-exit"
                                                title="Ver detalles"
                                                aria-label="Ver detalles">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                    </div>

                                    <div class="exit-products-data" hidden>
                                        <c:forEach var="product"
                                                   items="${exit.products}">

                                            <span class="exit-product-data"
                                                  data-id-exit-product="${product.idExitProduct}"
                                                  data-id-product="${product.idProduct}"
                                                  data-product-code="${fn:escapeXml(product.productCode)}"
                                                  data-product-name="${fn:escapeXml(product.productName)}"
                                                  data-metric-name="${fn:escapeXml(product.metricName)}"
                                                  data-metric-short-name="${fn:escapeXml(product.metricShortName)}"
                                                  data-quantity="${product.quantity}"
                                                  data-unit-price="${product.unitPrice}"
                                                  data-total-price="${product.totalPrice}">

                                                <c:forEach var="allocation"
                                                           items="${product.allocations}">

                                                    <span class="exit-allocation-data"
                                                          data-id-exit-allocation="${allocation.idExitAllocation}"
                                                          data-id-entry-product="${allocation.idEntryProduct}"
                                                          data-entry-folio="${fn:escapeXml(allocation.entryFolio)}"
                                                          data-provider-name="${fn:escapeXml(allocation.providerName)}"
                                                          data-quantity="${allocation.quantity}"
                                                          data-unit-cost="${allocation.unitCost}"
                                                          data-total-cost="${allocation.totalCost}">
                                                    </span>
                                                </c:forEach>
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
                     data-table-target="exitsTable"
                     data-page-size="5"
                     style="${empty exits ? 'display:none;' : ''}">

                    <div class="table-pagination-left">
                        <label class="table-page-size-label"
                               for="exitsPageSize">
                            Mostrar
                        </label>

                        <select id="exitsPageSize"
                                class="table-page-size-select js-page-size"
                                data-table-target="exitsTable"
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

                <div id="exitsGeneralEmptyState"
                     class="table-empty-state"
                     style="${empty exits ? 'display:block;' : 'display:none;'}">
                    <i class="bi bi-inbox"></i>
                    No hay salidas registradas.
                </div>

                <div id="exitsFilterEmptyState"
                     class="table-empty-state js-filter-empty-state"
                     style="display:none;">
                    <i class="bi bi-search"></i>
                    No se encontraron salidas con esos filtros.
                </div>
            </section>
        </div>
    </main>
</div>

<!-- REGISTRAR SALIDA -->
<div class="modal fade modal-neumorphic"
     id="modalCreateExit"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered modal-xl">
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-box-arrow-up me-2"
                       style="color:#ff8c69;"></i>
                    Registrar salida
                </h5>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <form id="formCreateExit"
                      class="js-form"
                      action="${pageContext.request.contextPath}/exit/save"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="true">

                    <div class="row">
                        <div class="col-lg-4 col-md-6 mb-3 form-field exit-header-field">
                            <label for="exitInvoiceNumber"
                                   class="form-label">
                                Vale, factura o documento
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <input type="text"
                                   id="exitInvoiceNumber"
                                   name="invoiceNumber"
                                   class="form-control js-form-field text-uppercase"
                                   placeholder="Ejemplo: VALE-001"
                                   minlength="1"
                                   maxlength="50"
                                   pattern="[A-Za-zÁÉÍÓÚáéíóúÑñ0-9._/#\x2D\s]{1,50}"
                                   data-label="Documento"
                                   data-valid-message="Documento válido."
                                   autocomplete="off"
                                   required>

                            <div class="valid-feedback">Documento válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-lg-4 col-md-6 mb-3 form-field exit-header-field">
                            <label for="exitArea"
                                   class="form-label">
                                Área de destino
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <div class="exit-area-control">
                                <select id="exitArea"
                                        name="idArea"
                                        class="form-select js-form-field"
                                        data-label="Área de destino"
                                        data-valid-message="Área seleccionada."
                                        required>

                                    <option value="">
                                        Seleccione un área
                                    </option>

                                    <c:forEach var="area" items="${areas}">
                                        <option value="${area.idArea}">
                                                ${area.name} (${area.shortName})
                                        </option>
                                    </c:forEach>
                                </select>

                                <button type="button"
                                        id="btnOpenQuickAreaExit"
                                        class="btn btn-primary"
                                        title="Registrar nueva área"
                                        aria-label="Registrar nueva área">
                                    <i class="bi bi-plus-lg"></i>
                                </button>
                            </div>

                            <div class="valid-feedback">
                                Área seleccionada.
                            </div>

                            <div class="invalid-feedback"></div>
                        </div>

                        <div class="col-lg-4 col-md-12 mb-3 form-field exit-header-field">
                            <label for="exitBuyerName"
                                   class="form-label">
                                Persona que recibe
                                <span class="text-danger required-marker">*</span>
                            </label>

                            <input type="text"
                                   id="exitBuyerName"
                                   name="buyerName"
                                   class="form-control js-form-field"
                                   placeholder="Nombre completo"
                                   minlength="2"
                                   maxlength="150"
                                   pattern="[A-Za-zÁÉÍÓÚáéíóúÑñÜü\s.'\x2D]{2,150}"
                                   data-label="Persona que recibe"
                                   data-valid-message="Nombre válido."
                                   autocomplete="off"
                                   required>

                            <div class="valid-feedback">Nombre válido.</div>
                            <div class="invalid-feedback"></div>
                        </div>
                    </div>

                    <section class="exit-products-section">
                        <div class="exit-products-header">
                            <div>
                                <h6 class="exit-products-title">
                                    Productos solicitados
                                </h6>

                                <p class="exit-products-description">
                                    El sistema descontará automáticamente los lotes más antiguos mediante FIFO.
                                </p>

                                <span id="exitProductsLoading"
                                      class="exit-products-loading">
                                    <span class="spinner-border spinner-border-sm"
                                          aria-hidden="true"></span>
                                    Consultando existencias...
                                </span>
                            </div>

                            <button type="button"
                                    id="btnAddExitProduct"
                                    class="btn btn-primary"
                                    disabled>
                                <i class="bi bi-plus-lg me-1"></i>
                                Agregar producto
                            </button>
                        </div>

                        <div id="exitProductList"
                             class="exit-product-list"
                             data-exit-product-list>
                        </div>

                        <div id="exitProductEmpty"
                             class="exit-product-empty">
                            <i class="bi bi-box-seam me-1"></i>
                            Agrega al menos un producto con existencia disponible.
                        </div>

                        <div class="exit-summary">
                            <div class="exit-summary-item">
                                <span class="exit-summary-label">
                                    Productos distintos
                                </span>

                                <strong id="exitProductCount"
                                        class="exit-summary-value">
                                    0
                                </strong>
                            </div>

                            <div class="exit-summary-item">
                                <span class="exit-summary-label">
                                    Unidades solicitadas
                                </span>

                                <strong id="exitQuantityTotal"
                                        class="exit-summary-value">
                                    0
                                </strong>
                            </div>
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

                <button type="button"
                        id="btnOpenConfirmExit"
                        class="btn btn-danger">
                    <i class="bi bi-box-arrow-up me-1"></i>
                    Registrar salida
                </button>
            </div>
        </div>
    </div>
</div>

<!-- REGISTRO RÁPIDO DE ÁREA -->
<div class="modal fade modal-neumorphic"
     id="modalQuickAreaExit"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-building-add me-2"
                       style="color:#6390ff;"></i>
                    Nueva área
                </h5>

                <button type="button"
                        class="btn-close"
                        id="btnCloseQuickAreaExit"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body">
                <p class="small mb-3"
                   style="color:var(--text-muted);">
                    Registra una nueva área sin perder los datos de la salida.
                </p>

                <form id="formQuickAreaExit"
                      class="js-form"
                      action="${pageContext.request.contextPath}/area/save"
                      method="post"
                      novalidate
                      data-submit-mode="manual"
                      data-reset-on-close="true">

                    <div class="mb-3 form-field">
                        <label for="exitQuickAreaShortName"
                               class="form-label">
                            Abreviatura
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field text-uppercase"
                               id="exitQuickAreaShortName"
                               name="shortName"
                               placeholder="Ejemplo: RH"
                               minlength="1"
                               maxlength="20"
                               pattern="[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9._\-]{1,20}"
                               data-label="Abreviatura"
                               data-valid-message="Abreviatura válida."
                               autocomplete="off"
                               required>

                        <div class="valid-feedback">
                            Abreviatura válida.
                        </div>

                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-3 form-field">
                        <label for="exitQuickAreaName"
                               class="form-label">
                            Nombre
                            <span class="text-danger required-marker">*</span>
                        </label>

                        <input type="text"
                               class="form-control js-form-field"
                               id="exitQuickAreaName"
                               name="name"
                               placeholder="Ejemplo: Recursos Humanos"
                               minlength="2"
                               maxlength="100"
                               data-label="Nombre"
                               data-valid-message="Nombre válido."
                               autocomplete="off"
                               required>

                        <div class="valid-feedback">
                            Nombre válido.
                        </div>

                        <div class="invalid-feedback"></div>
                    </div>

                    <div class="mb-0 form-field">
                        <label for="exitQuickAreaDescription"
                               class="form-label">
                            Descripción
                            <span class="text-muted small">(opcional)</span>
                        </label>

                        <textarea class="form-control js-form-field"
                                  id="exitQuickAreaDescription"
                                  name="description"
                                  rows="3"
                                  maxlength="500"
                                  placeholder="Describe el uso o función del área"
                                  data-label="Descripción"></textarea>

                        <div class="valid-feedback">
                            Descripción válida.
                        </div>

                        <div class="invalid-feedback"></div>
                    </div>
                </form>
            </div>

            <div class="modal-footer">
                <button type="button"
                        class="btn btn-secondary"
                        id="btnCancelQuickAreaExit">
                    <i class="bi bi-x-lg me-1"></i>
                    Cancelar
                </button>

                <button type="submit"
                        class="btn btn-primary"
                        form="formQuickAreaExit"
                        id="btnSaveQuickAreaExit"
                        data-loading-text="Guardando...">
                    <i class="bi bi-check-lg me-1"></i>
                    Guardar área
                </button>
            </div>
        </div>
    </div>
</div>

<!-- CONFIRMAR SALIDA -->
<div class="modal fade modal-neumorphic"
     id="modalConfirmExit"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-exclamation-triangle-fill me-2"
                       style="color:#ff8c69;"></i>
                    Confirmar salida
                </h5>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                    <i class="bi bi-x-lg"></i>
                </button>
            </div>

            <div class="modal-body text-center py-4">
                <i class="bi bi-box-arrow-up"
                   style="display:block;margin-bottom:16px;font-size:3rem;color:#ff8c69;">
                </i>

                <p style="color:var(--text-color);font-size:16px;font-weight:600;">
                    ¿Deseas registrar esta salida?
                </p>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        Área
                    </span>

                    <span class="modal-detail-value"
                          id="confirmExitArea">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        Receptor
                    </span>

                    <span class="modal-detail-value"
                          id="confirmExitBuyer">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        Productos
                    </span>

                    <span class="modal-detail-value"
                          id="confirmExitProducts">
                        0
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        Unidades
                    </span>

                    <span class="modal-detail-value"
                          id="confirmExitQuantity">
                        0
                    </span>
                </div>

                <p class="mt-3 mb-0"
                   style="color:var(--text-muted);font-size:13px;">
                    El sistema descontará existencias automáticamente siguiendo FIFO.
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
                        id="btnConfirmExit"
                        class="btn btn-danger"
                        data-loading-text="Registrando...">
                    <i class="bi bi-check-lg me-1"></i>
                    Confirmar
                </button>
            </div>
        </div>
    </div>
</div>

<!-- DETALLES DE SALIDA -->
<div class="modal fade modal-neumorphic"
     id="modalViewExit"
     tabindex="-1"
     aria-hidden="true">

    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">

            <div class="modal-header">
                <h5 class="modal-title">
                    <i class="bi bi-receipt me-2"
                       style="color:#6390ff;"></i>
                    Detalles de la salida
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
                    <i class="bi bi-box-arrow-up"
                       style="font-size:4rem;color:#ff8c69;">
                    </i>

                    <h4 id="viewExitFolio"
                        class="mt-2 mb-2"
                        style="color:var(--text-color);">
                        -
                    </h4>

                    <span class="table-badge table-badge-danger">
                        Salida registrada
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-calendar-event me-2"></i>
                        Fecha
                    </span>

                    <span class="modal-detail-value"
                          id="viewExitDate">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-receipt-cutoff me-2"></i>
                        Documento
                    </span>

                    <span class="modal-detail-value"
                          id="viewExitInvoice">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-building me-2"></i>
                        Área
                    </span>

                    <span class="modal-detail-value"
                          id="viewExitArea">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-person-check me-2"></i>
                        Receptor
                    </span>

                    <span class="modal-detail-value"
                          id="viewExitBuyer">
                        -
                    </span>
                </div>

                <div class="modal-detail-row">
                    <span class="modal-detail-label">
                        <i class="bi bi-person me-2"></i>
                        Registró
                    </span>

                    <span class="modal-detail-value"
                          id="viewExitUser">
                        -
                    </span>
                </div>

                <div class="mt-4">
                    <h6 style="color:var(--text-color);font-weight:700;">
                        <i class="bi bi-boxes me-2"></i>
                        Productos entregados
                    </h6>

                    <div id="viewExitProducts"
                         class="exit-detail-products mt-3">
                    </div>
                </div>

                <div class="exit-summary mt-4">
                    <span class="exit-summary-label">
                        Costo total FIFO
                    </span>

                    <strong id="viewExitTotal"
                            class="exit-summary-value">
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

<!-- PLANTILLA PRODUCTO -->
<template id="exitProductRowTemplate">
    <div class="exit-product-row">

        <div class="form-field exit-product-field">
            <label class="form-label">
                Producto
                <span class="text-danger required-marker">*</span>
            </label>

            <select name="idProduct[]"
                    class="form-select js-form-field exit-product-select"
                    data-label="Producto"
                    data-valid-message="Producto seleccionado."
                    required>
                <option value="">
                    Seleccione un producto
                </option>
            </select>

            <span class="exit-product-metric">
                Unidad: —
            </span>

            <span class="exit-product-stock">
                Existencia disponible:
                <strong>0</strong>
            </span>

            <div class="valid-feedback">
                Producto seleccionado.
            </div>

            <div class="invalid-feedback"></div>
        </div>

        <div class="form-field exit-product-field">
            <label class="form-label">
                Cantidad
                <span class="text-danger required-marker">*</span>
            </label>

            <input type="number"
                   name="quantity[]"
                   class="form-control js-form-field exit-product-quantity"
                   placeholder="0"
                   min="1"
                   max="999999999"
                   step="1"
                   inputmode="numeric"
                   data-label="Cantidad"
                   data-valid-message="Cantidad válida."
                   autocomplete="off"
                   required>

            <div class="valid-feedback">
                Cantidad válida.
            </div>

            <div class="invalid-feedback"></div>
        </div>

        <div class="form-field exit-product-field">
            <label class="form-label">
                Estado
            </label>

            <div class="input-group">
                <span class="input-group-text">
                    <i class="bi bi-boxes"></i>
                </span>

                <input type="text"
                       class="form-control exit-product-status"
                       value="Sin seleccionar"
                       readonly
                       tabindex="-1">
            </div>
        </div>

        <button type="button"
                class="btn btn-danger exit-product-remove"
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
<script src="${pageContext.request.contextPath}/assets/js/exit.js?v=2"></script>
</body>
</html>
<%-- Registro transaccional de salidas y asignación de lotes disponibles. --%>
