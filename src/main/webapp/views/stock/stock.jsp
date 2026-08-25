<%--
    Vista técnica: stock.
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

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Existencias</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style-sidebar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/table.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/modals.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/form.css">

    <style>
        /* ==========================================================
           TARJETAS DE RESUMEN
           ========================================================== */

        .stock-summary-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 18px;
            margin-bottom: 22px;
        }

        .stock-summary-card {
            position: relative;
            display: flex;
            align-items: center;
            gap: 16px;
            min-height: 125px;
            padding: 20px;
            overflow: hidden;
            border-radius: 20px;
            background: var(--card-bg);
            box-shadow: var(--neumo-shadow);
        }

        .stock-summary-card::after {
            content: "";
            position: absolute;
            right: -25px;
            bottom: -35px;
            width: 100px;
            height: 100px;
            border-radius: 50%;
            background: currentColor;
            opacity: .06;
            pointer-events: none;
        }

        .stock-summary-icon {
            flex-shrink: 0;
            width: 58px;
            height: 58px;
            display: grid;
            place-items: center;
            border-radius: 18px;
            font-size: 26px;
            background: var(--card-bg);
            box-shadow: var(--neumo-shadow-inset);
        }

        .stock-summary-information {
            min-width: 0;
        }

        .stock-summary-label {
            display: block;
            margin-bottom: 4px;
            color: var(--text-muted);
            font-size: 13px;
            font-weight: 600;
        }

        .stock-summary-value {
            display: block;
            color: var(--text-color);
            font-size: 29px;
            font-weight: 800;
            line-height: 1.15;
        }

        .stock-summary-description {
            display: block;
            margin-top: 5px;
            color: var(--text-muted);
            font-size: 11px;
        }

        .stock-summary-products {
            color: #6390ff;
        }

        .stock-summary-units {
            color: #57d38c;
        }

        .stock-summary-low {
            color: #f3b44b;
        }

        .stock-summary-out {
            color: #ff6666;
        }

        /* ==========================================================
           TABLA DE STOCK
           ========================================================== */

        .stock-product-cell {
            min-width: 190px;
        }

        .stock-product-name {
            display: block;
            color: var(--text-color);
            font-weight: 700;
        }

        .stock-product-code {
            display: block;
            margin-top: 3px;
            color: var(--text-muted);
            font-size: 12px;
        }

        .stock-provider-name {
            display: block;
            color: var(--text-color);
            font-weight: 600;
        }

        .stock-provider-rfc {
            display: block;
            margin-top: 3px;
            color: var(--text-muted);
            font-size: 11px;
        }

        .stock-quantity-wrapper {
            min-width: 190px;
        }

        .stock-quantity-top {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 10px;
            margin-bottom: 7px;
        }

        .stock-quantity-value {
            color: var(--text-color);
            font-size: 14px;
            font-weight: 800;
        }

        .stock-quantity-unit {
            color: var(--text-muted);
            font-size: 11px;
            font-weight: 600;
        }

        .stock-progress {
            width: 100%;
            height: 9px;
            overflow: hidden;
            border-radius: 999px;
            background: var(--card-bg);
            box-shadow: var(--neumo-shadow-inset);
        }

        .stock-progress-bar {
            min-width: 0;
            height: 100%;
            border-radius: inherit;
            transition: width .25s ease;
        }

        .stock-progress-bar.is-available {
            background: #57d38c;
        }

        .stock-progress-bar.is-low {
            background: #f3b44b;
        }

        .stock-progress-bar.is-out {
            background: #ff6666;
        }

        .stock-status {
            display: inline-flex;
            align-items: center;
            gap: 7px;
            padding: 6px 10px;
            white-space: nowrap;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 700;
        }

        .stock-status::before {
            content: "";
            width: 8px;
            height: 8px;
            flex-shrink: 0;
            border-radius: 50%;
            background: currentColor;
            box-shadow: 0 0 8px currentColor;
        }

        .stock-status.is-available {
            color: #36b86f;
            background: rgba(87, 211, 140, .12);
        }

        .stock-status.is-low {
            color: #d99627;
            background: rgba(243, 180, 75, .14);
        }

        .stock-status.is-out {
            color: #e24d4d;
            background: rgba(255, 102, 102, .12);
        }

        .stock-price {
            color: var(--text-color);
            font-weight: 700;
            white-space: nowrap;
        }

        /* ==========================================================
           MODAL DE DETALLES
           ========================================================== */

        .stock-detail-header {
            margin-bottom: 22px;
            text-align: center;
        }

        .stock-detail-icon {
            width: 78px;
            height: 78px;
            display: grid;
            place-items: center;
            margin: 0 auto 14px;
            border-radius: 24px;
            color: #6390ff;
            font-size: 38px;
            background: var(--card-bg);
            box-shadow: var(--neumo-shadow);
        }

        .stock-detail-title {
            margin: 0;
            color: var(--text-color);
            font-size: 22px;
            font-weight: 800;
        }

        .stock-detail-code {
            display: block;
            margin-top: 5px;
            color: var(--text-muted);
            font-size: 13px;
        }

        .stock-detail-quantity-card {
            margin-top: 20px;
            padding: 20px;
            border-radius: 18px;
            background: var(--card-bg);
            box-shadow: var(--neumo-shadow-inset);
        }

        .stock-detail-quantity-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 14px;
            margin-bottom: 12px;
        }

        .stock-detail-quantity-label {
            color: var(--text-muted);
            font-size: 13px;
            font-weight: 600;
        }

        .stock-detail-quantity-value {
            color: var(--text-color);
            font-size: 26px;
            font-weight: 800;
        }

        .stock-detail-progress {
            height: 13px;
        }

        .stock-detail-note {
            margin: 12px 0 0;
            color: var(--text-muted);
            font-size: 12px;
        }

        .stock-readonly-notice {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            margin-bottom: 20px;
            padding: 14px 16px;
            border-radius: 16px;
            color: var(--text-muted);
            font-size: 13px;
            background: var(--card-bg);
            box-shadow: var(--neumo-shadow-inset);
        }

        .stock-readonly-notice i {
            flex-shrink: 0;
            color: #6390ff;
            font-size: 20px;
        }

        @media (max-width: 1199.98px) {
            .stock-summary-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 575.98px) {
            .stock-summary-grid {
                grid-template-columns: 1fr;
            }

            .stock-summary-card {
                min-height: 105px;
            }

            .stock-detail-quantity-header {
                align-items: flex-start;
                flex-direction: column;
            }
        }
    </style>
</head>

<body data-context-path="${pageContext.request.contextPath}"
      data-low-stock-limit="${lowStockLimit}">

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

            <!-- ==================================================
                 ENCABEZADO
                 ================================================== -->

            <div class="table-page-header">

                <div>
                    <h2 class="table-page-title">
                        <i class="bi bi-boxes"></i>
                        Existencias
                    </h2>

                    <p class="mb-0"
                       style="color:var(--text-muted);font-size:13px;">

                        Consulta las cantidades disponibles por producto y proveedor.

                    </p>
                </div>

                <div class="table-header-actions">

                    <button type="button"
                            class="table-toolbar-btn"
                            id="btnRefreshStock">

                        <i class="bi bi-arrow-clockwise"></i>
                        <span>Actualizar</span>

                    </button>

                </div>

            </div>

            <!-- ==================================================
                 AVISO DE SOLO CONSULTA
                 ================================================== -->

            <div class="stock-readonly-notice">

                <i class="bi bi-shield-check"></i>

                <div>
                    <strong style="color:var(--text-color);">
                        Información automática
                    </strong>

                    <div class="mt-1">
                        Las existencias aumentan con las entradas y disminuyen
                        con las salidas FIFO. No pueden modificarse manualmente
                        desde esta pantalla.
                    </div>
                </div>

            </div>

            <!-- ==================================================
                 TARJETAS
                 ================================================== -->

            <section class="stock-summary-grid">

                <article class="stock-summary-card stock-summary-products">

                    <div class="stock-summary-icon">
                        <i class="bi bi-box-seam"></i>
                    </div>

                    <div class="stock-summary-information">

                        <span class="stock-summary-label">
                            Productos con historial
                        </span>

                        <strong id="stockSummaryProducts"
                                class="stock-summary-value">

                            <c:out value="${stockSummary.totalProducts}"
                                   default="0"/>

                        </strong>

                        <span class="stock-summary-description">
                            Productos que han tenido existencia
                        </span>

                    </div>

                </article>

                <article class="stock-summary-card stock-summary-units">

                    <div class="stock-summary-icon">
                        <i class="bi bi-boxes"></i>
                    </div>

                    <div class="stock-summary-information">

                        <span class="stock-summary-label">
                            Unidades disponibles
                        </span>

                        <strong id="stockSummaryUnits"
                                class="stock-summary-value">

                            <c:out value="${stockSummary.totalUnits}"
                                   default="0"/>

                        </strong>

                        <span class="stock-summary-description">
                            Suma de todas las existencias
                        </span>

                    </div>

                </article>

                <article class="stock-summary-card stock-summary-low">

                    <div class="stock-summary-icon">
                        <i class="bi bi-exclamation-triangle"></i>
                    </div>

                    <div class="stock-summary-information">

                        <span class="stock-summary-label">
                            Stock bajo
                        </span>

                        <strong id="stockSummaryLow"
                                class="stock-summary-value">

                            <c:out value="${stockSummary.lowStockProducts}"
                                   default="0"/>

                        </strong>

                        <span class="stock-summary-description">
                            Entre 1 y ${lowStockLimit} unidades
                        </span>

                    </div>

                </article>

                <article class="stock-summary-card stock-summary-out">

                    <div class="stock-summary-icon">
                        <i class="bi bi-x-octagon"></i>
                    </div>

                    <div class="stock-summary-information">

                        <span class="stock-summary-label">
                            Productos agotados
                        </span>

                        <strong id="stockSummaryOut"
                                class="stock-summary-value">

                            <c:out value="${stockSummary.outOfStockProducts}"
                                   default="0"/>

                        </strong>

                        <span class="stock-summary-description">
                            Sin existencia disponible
                        </span>

                    </div>

                </article>

            </section>

            <!-- ==================================================
                 HERRAMIENTAS Y FILTROS
                 ================================================== -->

            <section class="table-toolbar">

                <div class="table-toolbar-top">

                    <div class="table-search">

                        <i class="bi bi-search"></i>

                        <input type="search"
                               class="js-table-search"
                               data-table-target="stockTable"
                               placeholder="Buscar por clave, producto, proveedor, RFC o unidad..."
                               autocomplete="off">

                    </div>

                    <button type="button"
                            class="table-toolbar-btn js-filter-toggle"
                            data-filter-target="stockFilters">

                        <i class="bi bi-funnel"></i>
                        <span>Filtros</span>

                    </button>

                    <button type="button"
                            class="table-toolbar-btn js-clear-filters"
                            data-table-target="stockTable">

                        <i class="bi bi-eraser"></i>
                        <span>Limpiar</span>

                    </button>

                </div>

                <div class="table-filters"
                     id="stockFilters"
                     style="--filter-columns:3;">

                    <!-- PROVEEDOR -->

                    <div class="table-filter-group">

                        <label for="stockProviderFilter"
                               class="table-filter-label">
                            Proveedor
                        </label>

                        <select id="stockProviderFilter"
                                class="form-select js-table-filter"
                                data-table-target="stockTable"
                                data-filter-field="provider">

                            <option value="all">
                                Todos
                            </option>

                            <c:set var="usedProviderIds" value=","/>

                            <c:forEach var="stock" items="${stockList}">

                                <c:set var="providerToken"
                                       value=",${stock.idProvider},"/>

                                <c:if test="${not fn:contains(usedProviderIds, providerToken)}">

                                    <option value="${stock.idProvider}">
                                            ${stock.providerName}
                                    </option>

                                    <c:set var="usedProviderIds"
                                           value="${usedProviderIds}${stock.idProvider},"/>

                                </c:if>

                            </c:forEach>

                        </select>

                    </div>

                    <!-- UNIDAD -->

                    <div class="table-filter-group">

                        <label for="stockMetricFilter"
                               class="table-filter-label">
                            Unidad de medida
                        </label>

                        <select id="stockMetricFilter"
                                class="form-select js-table-filter"
                                data-table-target="stockTable"
                                data-filter-field="metric">

                            <option value="all">
                                Todas
                            </option>

                            <c:set var="usedMetricIds" value=","/>

                            <c:forEach var="stock" items="${stockList}">

                                <c:set var="metricToken"
                                       value=",${stock.idMetric},"/>

                                <c:if test="${not fn:contains(usedMetricIds, metricToken)}">

                                    <option value="${stock.idMetric}">
                                            ${stock.metricName}
                                        (${stock.metricShortName})
                                    </option>

                                    <c:set var="usedMetricIds"
                                           value="${usedMetricIds}${stock.idMetric},"/>

                                </c:if>

                            </c:forEach>

                        </select>

                    </div>

                    <!-- ESTADO -->

                    <div class="table-filter-group">

                        <label for="stockStatusFilter"
                               class="table-filter-label">
                            Estado
                        </label>

                        <select id="stockStatusFilter"
                                class="form-select js-table-filter"
                                data-table-target="stockTable"
                                data-filter-field="status">

                            <option value="all">
                                Todos
                            </option>

                            <option value="available">
                                Disponible
                            </option>

                            <option value="low">
                                Stock bajo
                            </option>

                            <option value="out">
                                Agotado
                            </option>

                        </select>

                    </div>

                </div>

            </section>

            <!-- ==================================================
                 TABLA
                 ================================================== -->

            <section class="table-panel">

                <div class="table-responsive"
                     style="${empty stockList ? 'display:none;' : ''}">

                    <table class="app-table"
                           id="stockTable"
                           style="--table-min-width:1150px;">

                        <thead>

                        <tr>
                            <th>ID</th>
                            <th>Producto</th>
                            <th>Proveedor</th>
                            <th>Unidad</th>
                            <th>Costo actual</th>
                            <th>Existencia</th>
                            <th>Estado</th>
                            <th>Situación</th>
                            <th class="table-text-center">
                                Acciones
                            </th>
                        </tr>

                        </thead>

                        <tbody>

                        <c:forEach var="stock" items="${stockList}">

                            <c:choose>

                                <c:when test="${stock.quantity <= 0}">
                                    <c:set var="stockStatus"
                                           value="out"/>

                                    <c:set var="stockStatusLabel"
                                           value="Agotado"/>

                                    <c:set var="stockPercentage"
                                           value="0"/>
                                </c:when>

                                <c:when test="${stock.quantity <= lowStockLimit}">
                                    <c:set var="stockStatus"
                                           value="low"/>

                                    <c:set var="stockStatusLabel"
                                           value="Stock bajo"/>

                                    <c:set var="stockPercentage"
                                           value="${stock.quantity * 5}"/>
                                </c:when>

                                <c:otherwise>
                                    <c:set var="stockStatus"
                                           value="available"/>

                                    <c:choose>
                                        <c:when test="${stock.quantity >= lowStockLimit * 2}">
                                            <c:set var="stockPercentage"
                                                   value="100"/>
                                        </c:when>

                                        <c:otherwise>
                                            <c:set var="stockPercentage"
                                                   value="${stock.quantity * 5}"/>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:set var="stockStatusLabel"
                                           value="Disponible"/>
                                </c:otherwise>

                            </c:choose>

                            <tr class="js-table-row stock-table-row"
                                data-id="${stock.idStock}"
                                data-id-product-provider="${stock.idProductProvider}"
                                data-product-id="${stock.idProduct}"
                                data-product-code="${fn:escapeXml(stock.productCode)}"
                                data-product-name="${fn:escapeXml(stock.productName)}"
                                data-provider="${stock.idProvider}"
                                data-provider-name="${fn:escapeXml(stock.providerName)}"
                                data-provider-rfc="${fn:escapeXml(stock.providerRfc)}"
                                data-metric="${stock.idMetric}"
                                data-metric-name="${fn:escapeXml(stock.metricName)}"
                                data-metric-short-name="${fn:escapeXml(stock.metricShortName)}"
                                data-price="${stock.purchasePrice}"
                                data-quantity="${stock.quantity}"
                                data-status="${stockStatus}"
                                data-status-label="${stockStatusLabel}"
                                data-percentage="${stockPercentage}"
                                data-search="${fn:escapeXml(stock.productCode)}
                                             ${fn:escapeXml(stock.productName)}
                                             ${fn:escapeXml(stock.providerName)}
                                             ${fn:escapeXml(stock.providerRfc)}
                                             ${fn:escapeXml(stock.metricName)}
                                             ${fn:escapeXml(stock.metricShortName)}"
                                data-product-status="${stock.productStatus}"
                                data-provider-status="${stock.providerStatus}"
                                data-metric-status="${stock.metricStatus}"
                                data-relation-status="${stock.relationStatus}">

                                <td class="table-cell-secondary table-cell-nowrap">
                                        ${stock.idStock}
                                </td>

                                <td class="stock-product-cell">

                                    <span class="stock-product-name">
                                            ${stock.productName}
                                    </span>

                                    <span class="stock-product-code">
                                            ${stock.productCode}
                                    </span>

                                </td>

                                <td>

                                    <span class="stock-provider-name">
                                            ${stock.providerName}
                                    </span>

                                    <span class="stock-provider-rfc">
                                            ${stock.providerRfc}
                                    </span>

                                </td>

                                <td>

                                    <span class="table-badge table-badge-primary">
                                            ${stock.metricShortName}
                                    </span>

                                    <span class="table-cell-secondary ms-1">
                                            ${stock.metricName}
                                    </span>

                                </td>

                                <td class="stock-price stock-money-cell"
                                    data-money-value="${stock.purchasePrice}">

                                        ${stock.purchasePrice}

                                </td>

                                <td>

                                    <div class="stock-quantity-wrapper">

                                        <div class="stock-quantity-top">

                                            <strong class="stock-quantity-value">
                                                    ${stock.quantity}
                                            </strong>

                                            <span class="stock-quantity-unit">
                                                    ${stock.metricShortName}
                                            </span>

                                        </div>

                                        <div class="stock-progress"
                                             role="progressbar"
                                             aria-label="Nivel de existencia"
                                             aria-valuemin="0"
                                             aria-valuemax="100"
                                             aria-valuenow="${stockPercentage}">

                                            <div class="stock-progress-bar is-${stockStatus}"
                                                 style="width:${stockPercentage}%;">

                                            </div>

                                        </div>

                                    </div>

                                </td>

                                <td>

                                    <span class="stock-status is-${stockStatus}">
                                            ${stockStatusLabel}
                                    </span>

                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${stock.productStatus != 1}">
                                            <span class="table-badge table-badge-danger">
                                                Producto inactivo
                                            </span>
                                                                        </c:when>

                                                                        <c:when test="${stock.providerStatus != 1}">
                                            <span class="table-badge table-badge-warning">
                                                Proveedor inactivo
                                            </span>
                                                                        </c:when>

                                                                        <c:when test="${stock.metricStatus != 1}">
                                            <span class="table-badge table-badge-warning">
                                                Unidad inactiva
                                            </span>
                                                                        </c:when>

                                                                        <c:when test="${stock.relationStatus != 1}">
                                            <span class="table-badge table-badge-warning">
                                                Relación inactiva
                                            </span>
                                                                        </c:when>

                                                                        <c:otherwise>
                                            <span class="table-badge table-badge-success">
                                                Operativo
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>

                                    <div class="table-actions">

                                        <button type="button"
                                                class="table-action-btn table-action-view btn-view-stock"
                                                title="Ver detalles"
                                                aria-label="Ver detalles">

                                            <i class="bi bi-eye"></i>

                                        </button>

                                    </div>

                                </td>

                            </tr>

                        </c:forEach>

                        </tbody>

                    </table>

                </div>

                <!-- PAGINACIÓN -->

                <div class="table-pagination"
                     data-table-target="stockTable"
                     data-page-size="10"
                     style="${empty stockList ? 'display:none;' : ''}">

                    <div class="table-pagination-left">

                        <label for="stockPageSize"
                               class="table-page-size-label">
                            Mostrar
                        </label>

                        <select id="stockPageSize"
                                class="table-page-size-select js-page-size"
                                data-table-target="stockTable"
                                aria-label="Registros por página">

                            <option value="5">5</option>
                            <option value="10" selected>10</option>
                            <option value="20">20</option>
                            <option value="50">50</option>

                        </select>

                        <span class="table-page-size-text">
                            registros
                        </span>

                    </div>

                    <div class="table-pagination-info">

                        Mostrando

                        <strong class="js-page-start">
                            0
                        </strong>

                        a

                        <strong class="js-page-end">
                            0
                        </strong>

                        de

                        <strong class="js-page-total">
                            0
                        </strong>

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

                <!-- SIN REGISTROS -->

                <div id="stockGeneralEmptyState"
                     class="table-empty-state"
                     style="${empty stockList ? 'display:block;' : 'display:none;'}">

                    <i class="bi bi-box-seam"></i>

                    No hay existencias registradas. Registra una entrada para
                    comenzar a generar stock.

                </div>

                <!-- SIN RESULTADOS POR FILTROS -->

                <div id="stockFilterEmptyState"
                     class="table-empty-state js-filter-empty-state"
                     style="display:none;">

                    <i class="bi bi-search"></i>

                    No se encontraron existencias con esos filtros.

                </div>

            </section>

        </div>

    </main>

</div>

<!-- ==========================================================
     MODAL DE DETALLES
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalViewStock"
     tabindex="-1"
     aria-hidden="true">

    <div class="modal-dialog modal-dialog-centered modal-lg">

        <div class="modal-content">

            <div class="modal-header">

                <h5 class="modal-title">

                    <i class="bi bi-boxes me-2"
                       style="color:#6390ff;"></i>

                    Detalles de existencia

                </h5>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">

                    <i class="bi bi-x-lg"></i>

                </button>

            </div>

            <div class="modal-body">

                <div class="stock-detail-header">

                    <div class="stock-detail-icon">
                        <i class="bi bi-box-seam"></i>
                    </div>

                    <h4 id="viewStockProductName"
                        class="stock-detail-title">
                        -
                    </h4>

                    <span id="viewStockProductCode"
                          class="stock-detail-code">
                        -
                    </span>

                    <div class="mt-3">

                        <span id="viewStockStatus"
                              class="stock-status is-available">
                            Disponible
                        </span>

                    </div>

                </div>

                <div class="modal-detail-row">

                    <span class="modal-detail-label">

                        <i class="bi bi-truck me-2"></i>
                        Proveedor

                    </span>

                    <span id="viewStockProvider"
                          class="modal-detail-value">
                        -
                    </span>

                </div>

                <div class="modal-detail-row">

                    <span class="modal-detail-label">

                        <i class="bi bi-card-text me-2"></i>
                        RFC

                    </span>

                    <span id="viewStockProviderRfc"
                          class="modal-detail-value">
                        -
                    </span>

                </div>

                <div class="modal-detail-row">

                    <span class="modal-detail-label">

                        <i class="bi bi-rulers me-2"></i>
                        Unidad de medida

                    </span>

                    <span id="viewStockMetric"
                          class="modal-detail-value">
                        -
                    </span>

                </div>

                <div class="modal-detail-row">

                    <span class="modal-detail-label">

                        <i class="bi bi-currency-dollar me-2"></i>
                        Último costo registrado

                    </span>

                    <span id="viewStockPrice"
                          class="modal-detail-value">
                        $0.00
                    </span>

                </div>

                <div class="stock-detail-quantity-card">

                    <div class="stock-detail-quantity-header">

                        <div>

                            <span class="stock-detail-quantity-label">
                                Existencia disponible
                            </span>

                            <div>

                                <strong id="viewStockQuantity"
                                        class="stock-detail-quantity-value">
                                    0
                                </strong>

                                <span id="viewStockUnit"
                                      class="stock-detail-quantity-label">
                                    unidades
                                </span>

                            </div>

                        </div>

                        <span id="viewStockQuantityStatus"
                              class="stock-status is-available">
                            Disponible
                        </span>

                    </div>

                    <div class="stock-progress stock-detail-progress"
                         role="progressbar"
                         aria-label="Nivel actual de existencia"
                         aria-valuemin="0"
                         aria-valuemax="100"
                         aria-valuenow="0">

                        <div id="viewStockProgressBar"
                             class="stock-progress-bar is-available"
                             style="width:0%;">
                        </div>

                    </div>

                    <p class="stock-detail-note">

                        El nivel visual utiliza ${lowStockLimit * 2}
                        unidades como referencia máxima. El estado de stock
                        bajo se aplica cuando quedan ${lowStockLimit}
                        unidades o menos.

                    </p>

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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/table.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/toast.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/api.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/stock.js?v=1"></script>

</body>

</html>
<%-- Consulta consolidada de existencias y disponibilidad por producto. --%>
