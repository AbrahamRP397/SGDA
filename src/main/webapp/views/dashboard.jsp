<%--
    Vista técnica: dashboard.
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
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard | SGDA</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css" rel="stylesheet">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-sidebar.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/modals.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/form.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
</head>

<body data-context-path="${pageContext.request.contextPath}"
      data-selected-period="${selectedPeriod}">

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

      <header class="dashboard-page-header">

        <div>
          <h1 class="dashboard-title">
            <i class="bi bi-grid-1x2-fill"></i>
            Dashboard
          </h1>

          <p class="dashboard-description">
            Consulta los movimientos y el comportamiento actual
            de los productos del almacén.
          </p>
        </div>

        <div class="dashboard-header-actions">

          <button type="button"
                  id="btnRefreshDashboard"
                  class="dashboard-action-button">
            <i class="bi bi-arrow-clockwise"></i>
            <span>Actualizar</span>
          </button>

          <button type="button"
                  id="btnOpenReports"
                  class="dashboard-action-button is-primary">
            <i class="bi bi-file-earmark-bar-graph"></i>
            <span>Generar reporte</span>
          </button>

        </div>

      </header>

      <!-- ==================================================
           ERROR INICIAL
           ================================================== -->

      <c:if test="${not empty dashboardError}">
        <div class="dashboard-error">
          <i class="bi bi-exclamation-triangle-fill"></i>
          <div>
            <c:out value="${dashboardError}"/>
          </div>
        </div>
      </c:if>

      <!-- ==================================================
           GRÁFICA
           ================================================== -->

      <section class="dashboard-panel dashboard-chart-panel">

        <div class="dashboard-panel-header">

          <div>
            <h2 class="dashboard-panel-title">
              <i class="bi bi-graph-up"></i>
              Entradas y salidas
            </h2>

            <p class="dashboard-panel-description">
              Cantidad total de unidades movidas durante el periodo.
            </p>
          </div>

          <div class="dashboard-period-selector"
               role="group"
               aria-label="Periodo de la gráfica">

            <button type="button"
                    class="dashboard-period-button ${selectedPeriod == 'daily' ? 'is-active' : ''}"
                    data-dashboard-period="daily">
              Diario
            </button>

            <button type="button"
                    class="dashboard-period-button ${selectedPeriod == 'weekly' ? 'is-active' : ''}"
                    data-dashboard-period="weekly">
              Semanal
            </button>

            <button type="button"
                    class="dashboard-period-button ${selectedPeriod == 'monthly' ? 'is-active' : ''}"
                    data-dashboard-period="monthly">
              Mensual
            </button>

            <button type="button"
                    class="dashboard-period-button ${selectedPeriod == 'annual' ? 'is-active' : ''}"
                    data-dashboard-period="annual">
              Anual
            </button>

          </div>

        </div>

        <div class="dashboard-chart-container">

          <canvas id="dashboardMovementsChart"
                  aria-label="Gráfica de entradas y salidas"
                  role="img">
          </canvas>

          <div id="dashboardChartLoading"
               class="dashboard-chart-loading">
            <span class="spinner-border spinner-border-sm"
                  aria-hidden="true"></span>
            <span>Cargando gráfica...</span>
          </div>

          <div id="dashboardChartEmpty"
               class="dashboard-chart-empty">
            <i class="bi bi-bar-chart-line"></i>
            <span>
              No hay movimientos disponibles para este periodo.
            </span>
          </div>

        </div>

        <div id="dashboardInitialChartData" hidden>

          <c:forEach var="movement"
                     items="${chartMovements}">

            <span class="dashboard-chart-data"
                  data-period-key="${fn:escapeXml(movement.periodKey)}"
                  data-period-label="${fn:escapeXml(movement.periodLabel)}"
                  data-entry-quantity="${movement.entryQuantity}"
                  data-exit-quantity="${movement.exitQuantity}">
            </span>

          </c:forEach>

        </div>

      </section>

      <!-- ==================================================
           MOVIMIENTOS RECIENTES + MÁS STOCK
           ================================================== -->

      <div class="dashboard-content-grid">

        <!-- MOVIMIENTOS RECIENTES -->

        <section class="dashboard-panel">

          <div class="dashboard-panel-header">
            <div>
              <h2 class="dashboard-panel-title">
                <i class="bi bi-clock-history"></i>
                Movimientos recientes
              </h2>

              <p class="dashboard-panel-description">
                Últimas entradas y salidas registradas.
              </p>
            </div>
          </div>

          <div id="dashboardRecentMovements"
               class="dashboard-movement-list">

            <c:forEach var="movement"
                       items="${recentMovements}">

              <c:set var="isEntry"
                     value="${movement.movementType == 'ENTRY'}"/>

              <article class="dashboard-movement-item">

                <div class="dashboard-movement-icon ${isEntry ? 'is-entry' : 'is-exit'}">
                  <i class="bi ${isEntry ? 'bi-box-arrow-in-down' : 'bi-box-arrow-up'}"></i>
                </div>

                <div class="dashboard-movement-information">

                  <div class="dashboard-movement-title">

                    <span class="dashboard-movement-type ${isEntry ? 'is-entry' : 'is-exit'}">
                        ${isEntry ? 'Entrada' : 'Salida'}
                    </span>

                    <span>
                      <c:out value="${movement.folioNumber}"/>
                    </span>

                  </div>

                  <span class="dashboard-movement-destination">
                    ${isEntry ? 'Proveedor:' : 'Área:'}
                    <c:out value="${movement.destinationName}"/>
                  </span>

                  <span class="dashboard-movement-responsible">
                    Registró:
                    <c:out value="${movement.responsibleName}"/>
                  </span>

                </div>

                <div class="dashboard-movement-values">

                  <strong class="dashboard-movement-quantity">
                    <c:out value="${movement.totalQuantity}"
                           default="0"/>
                    unidades
                  </strong>

                  <span class="dashboard-movement-date"
                        data-dashboard-date="${movement.changeDate}">
                    <c:out value="${movement.changeDate}"/>
                  </span>

                </div>

              </article>

            </c:forEach>

            <c:if test="${empty recentMovements}">
              <div class="dashboard-empty-state">
                <i class="bi bi-clock-history"></i>
                <span>No hay movimientos recientes.</span>
              </div>
            </c:if>

          </div>

        </section>

        <!-- PRODUCTOS CON MÁS STOCK -->

        <section class="dashboard-panel">

          <div class="dashboard-panel-header">
            <div>
              <h2 class="dashboard-panel-title">
                <i class="bi bi-boxes"></i>
                Productos con más stock
              </h2>

              <p class="dashboard-panel-description">
                Existencia acumulada entre todos los proveedores.
              </p>
            </div>
          </div>

          <div id="dashboardMostStockProducts"
               class="dashboard-product-list">

            <c:forEach var="product"
                       items="${productsWithMostStock}"
                       varStatus="status">

              <article class="dashboard-product-item">

                <div class="dashboard-product-position">
                    ${status.index + 1}
                </div>

                <div class="dashboard-product-information">

                  <span class="dashboard-product-name">
                    <c:out value="${product.productName}"/>
                  </span>

                  <div class="dashboard-product-meta">

                    <span class="dashboard-product-code">
                      <c:out value="${product.productCode}"/>
                    </span>

                    <c:if test="${product.productStatus != 1}">
                      <span class="dashboard-product-status is-inactive">
                        <i class="bi bi-exclamation-circle"></i>
                        Inactivo
                      </span>
                    </c:if>

                  </div>

                </div>

                <div class="dashboard-product-value">

                  <strong class="dashboard-product-quantity">
                    <c:out value="${product.stockQuantity}"
                           default="0"/>
                  </strong>

                  <span class="dashboard-product-unit">
                    <c:out value="${product.metricShortName}"
                           default="unidades"/>
                  </span>

                </div>

              </article>

            </c:forEach>

            <c:if test="${empty productsWithMostStock}">
              <div class="dashboard-empty-state">
                <i class="bi bi-box-seam"></i>
                <span>No hay productos con existencia.</span>
              </div>
            </c:if>

          </div>

        </section>

      </div>

      <!-- ==================================================
           MÁS Y MENOS MOVIMIENTO
           ================================================== -->

      <div class="dashboard-rankings-grid">

        <!-- PRODUCTOS CON MÁS MOVIMIENTO -->

        <section class="dashboard-panel">

          <div class="dashboard-panel-header">
            <div>

              <h2 class="dashboard-panel-title">
                <i class="bi bi-graph-up-arrow"></i>
                Productos que más se mueven
              </h2>

              <p class="dashboard-panel-description">
                Suma histórica de entradas y salidas.
              </p>

            </div>
          </div>

          <div id="dashboardMostMovedProducts"
               class="dashboard-product-list">

            <c:forEach var="product"
                       items="${mostMovedProducts}"
                       varStatus="status">

              <article class="dashboard-product-item">

                <div class="dashboard-product-position">
                    ${status.index + 1}
                </div>

                <div class="dashboard-product-information">

                  <span class="dashboard-product-name">
                    <c:out value="${product.productName}"/>
                  </span>

                  <span class="dashboard-product-code">
                    <c:out value="${product.productCode}"/>
                  </span>

                  <span class="dashboard-product-breakdown">
                    Entradas:
                    <c:out value="${product.entryQuantity}"
                           default="0"/>

                    · Salidas:
                    <c:out value="${product.exitQuantity}"
                           default="0"/>
                  </span>

                </div>

                <div class="dashboard-product-value">

                  <strong class="dashboard-product-quantity">
                    <c:out value="${product.totalMovement}"
                           default="0"/>
                  </strong>

                  <span class="dashboard-product-unit">
                    <c:out value="${product.metricShortName}"
                           default="unidades"/>
                  </span>

                </div>

              </article>

            </c:forEach>

            <c:if test="${empty mostMovedProducts}">
              <div class="dashboard-empty-state">
                <i class="bi bi-graph-up"></i>
                <span>Aún no hay productos con movimientos.</span>
              </div>
            </c:if>

          </div>

        </section>

        <!-- PRODUCTOS CON MENOS MOVIMIENTO -->

        <section class="dashboard-panel">

          <div class="dashboard-panel-header">
            <div>

              <h2 class="dashboard-panel-title">
                <i class="bi bi-graph-down-arrow"></i>
                Productos que menos se mueven
              </h2>

              <p class="dashboard-panel-description">
                Productos con menor actividad registrada.
              </p>

            </div>
          </div>

          <div id="dashboardLeastMovedProducts"
               class="dashboard-product-list">

            <c:forEach var="product"
                       items="${leastMovedProducts}"
                       varStatus="status">

              <article class="dashboard-product-item">

                <div class="dashboard-product-position">
                    ${status.index + 1}
                </div>

                <div class="dashboard-product-information">

                  <span class="dashboard-product-name">
                    <c:out value="${product.productName}"/>
                  </span>

                  <span class="dashboard-product-code">
                    <c:out value="${product.productCode}"/>
                  </span>

                  <span class="dashboard-product-breakdown">
                    Entradas:
                    <c:out value="${product.entryQuantity}"
                           default="0"/>

                    · Salidas:
                    <c:out value="${product.exitQuantity}"
                           default="0"/>
                  </span>

                </div>

                <div class="dashboard-product-value">

                  <strong class="dashboard-product-quantity">
                    <c:out value="${product.totalMovement}"
                           default="0"/>
                  </strong>

                  <span class="dashboard-product-unit">
                    <c:out value="${product.metricShortName}"
                           default="unidades"/>
                  </span>

                </div>

              </article>

            </c:forEach>

            <c:if test="${empty leastMovedProducts}">
              <div class="dashboard-empty-state">
                <i class="bi bi-graph-down"></i>
                <span>Aún no hay productos con movimientos.</span>
              </div>
            </c:if>

          </div>

        </section>

      </div>

    </div>

  </main>

</div>

<!-- ==========================================================
     MODAL DE REPORTES
     ========================================================== -->

<div class="modal fade modal-neumorphic"
     id="modalReports"
     tabindex="-1"
     aria-hidden="true"
     data-bs-backdrop="static">

  <div class="modal-dialog modal-dialog-centered">

    <div class="modal-content">

      <div class="modal-header">

        <h5 class="modal-title">
          <i class="bi bi-file-earmark-bar-graph me-2"></i>
          Generar reporte
        </h5>

        <button type="button"
                class="btn-close"
                data-bs-dismiss="modal"
                aria-label="Cerrar">
          <i class="bi bi-x-lg"></i>
        </button>

      </div>

      <div class="modal-body">

        <form id="reportForm"
              class="dashboard-report-form"
              novalidate>

          <!-- TIPO -->

          <div class="form-field mb-3">

            <label for="reportType"
                   class="form-label">
              Tipo de reporte
            </label>

            <select id="reportType"
                    class="form-select">

              <option value="movements">
                Entradas y salidas
              </option>

              <option value="entries">
                Solo entradas
              </option>

              <option value="exits">
                Solo salidas
              </option>

              <option value="inventory"
                      disabled>
                Inventario — Próximamente
              </option>

            </select>

          </div>

          <!-- PERIODO -->

          <div class="form-field mb-3">

            <label for="reportPeriod"
                   class="form-label">
              Periodo
            </label>

            <select id="reportPeriod"
                    class="form-select">

              <option value="daily">
                Hoy
              </option>

              <option value="weekly">
                Semanal
              </option>

              <option value="monthly"
                      selected>
                Mensual
              </option>

              <option value="annual">
                Anual
              </option>

              <option value="custom">
                Rango personalizado
              </option>

            </select>

          </div>

          <!-- RANGO PERSONALIZADO -->

          <div id="customDateRange"
               class="dashboard-custom-date-range"
               hidden>

            <div class="form-field mb-3">

              <label for="reportStartDate"
                     class="form-label">
                Fecha inicial
              </label>

              <input type="date"
                     id="reportStartDate"
                     class="form-control"
                     autocomplete="off">

              <div class="invalid-feedback">
                Selecciona una fecha inicial válida.
              </div>

            </div>

            <div class="form-field mb-3">

              <label for="reportEndDate"
                     class="form-label">
                Fecha final
              </label>

              <input type="date"
                     id="reportEndDate"
                     class="form-control"
                     autocomplete="off">

              <div class="invalid-feedback">
                Selecciona una fecha final válida.
              </div>

            </div>

          </div>

          <!-- FORMATO -->

          <div class="form-field">

            <span class="form-label d-block">
              Formato
            </span>

            <div class="dashboard-format-options">

              <label class="dashboard-format-option">

                <input type="radio"
                       name="reportFormat"
                       value="pdf"
                       checked>

                <span>
                  <i class="bi bi-file-earmark-pdf"></i>
                  PDF
                </span>

              </label>

              <label class="dashboard-format-option">

                <input type="radio"
                       name="reportFormat"
                       value="xml">

                <span>
                  <i class="bi bi-filetype-xml"></i>
                  XML
                </span>

              </label>

            </div>

          </div>

        </form>

      </div>

      <div class="modal-footer">

        <button type="button"
                class="btn btn-secondary"
                data-bs-dismiss="modal">
          Cancelar
        </button>

        <button type="button"
                class="btn btn-primary"
                id="btnGenerateReport">
          <i class="bi bi-download me-1"></i>
          Generar
        </button>

      </div>

    </div>

  </div>

</div>

<!-- ==========================================================
     SCRIPTS
     ========================================================== -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.5.0/dist/chart.umd.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/toast.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/api.js"></script>

<script>
  window.dashboardConfig = {
    contextPath: "${pageContext.request.contextPath}"
  };
</script>

<script src="${pageContext.request.contextPath}/assets/js/dashboard.js?v=9"></script>

</body>
</html>
<%-- Tablero autenticado con indicadores, gráficas y accesos a reportes. --%>
