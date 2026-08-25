<%--
    Vista técnica: sidebar.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside id="sidebar-menu">

  <div class="sidebar-header">
    <span class="sidebar-title">Menú</span>

    <button type="button"
            id="btnCloseSidebar"
            class="sidebar-close"
            aria-label="Cerrar menú"
            title="Cerrar menú">
      <i class="bi bi-x-lg"></i>
    </button>
  </div>

  <div class="sidebar-content">

    <ul class="sidebar-list">

      <li>
        <a href="${pageContext.request.contextPath}/dashboard"
           class="sidebar-link"
           data-sidebar-path="/dashboard"
           title="Dashboard">
          <i class="bi bi-house-door"></i>
          <span>Dashboard</span>
        </a>
      </li>

      <c:if test="${sessionScope.rol eq 'Administrador'}">
        <li>
          <a href="${pageContext.request.contextPath}/users"
             class="sidebar-link"
             data-sidebar-path="/users"
             title="Usuarios">
            <i class="bi bi-people"></i>
            <span>Usuarios</span>
          </a>
        </li>
      </c:if>

      <li>
        <a href="${pageContext.request.contextPath}/products"
           class="sidebar-link"
           data-sidebar-path="/products"
           title="Productos">
          <i class="bi bi-box-seam"></i>
          <span>Productos</span>
        </a>
      </li>

      <li class="has-submenu">

        <details class="sidebar-details">

          <summary class="sidebar-link submenu-trigger"
                   title="Almacén">
            <i class="bi bi-building"></i>
            <span>Almacén</span>
            <i class="bi bi-chevron-right submenu-arrow"></i>
          </summary>

          <ul class="submenu">

            <li>
              <a href="${pageContext.request.contextPath}/entries"
                 data-sidebar-path="/entries"
                 title="Entradas">
                <i class="bi bi-box-arrow-in-down"></i>
                <span>Entradas</span>
              </a>
            </li>

            <li>
              <a href="${pageContext.request.contextPath}/exits"
                 data-sidebar-path="/exits"
                 title="Salidas">
                <i class="bi bi-box-arrow-up"></i>
                <span>Salidas</span>
              </a>
            </li>

          </ul>

        </details>

      </li>

      <li>
        <a href="${pageContext.request.contextPath}/stock"
           class="sidebar-link"
           data-sidebar-path="/stock"
           title="Stock">
          <i class="bi bi-boxes"></i>
          <span>Stock</span>
        </a>
      </li>

      <li class="has-submenu">

        <details class="sidebar-details">

          <summary class="sidebar-link submenu-trigger"
                   title="Datos generales">
            <i class="bi bi-database"></i>
            <span>Datos generales</span>
            <i class="bi bi-chevron-right submenu-arrow"></i>
          </summary>

          <ul class="submenu">

            <li>
              <a href="${pageContext.request.contextPath}/metrics"
                 data-sidebar-path="/metrics"
                 title="Unidades de medida">
                <i class="bi bi-rulers"></i>
                <span>Unidades de medida</span>
              </a>
            </li>

            <li>
              <a href="${pageContext.request.contextPath}/areas"
                 data-sidebar-path="/areas"
                 title="Áreas de destino">
                <i class="bi bi-diagram-3"></i>
                <span>Áreas de destino</span>
              </a>
            </li>

          </ul>

        </details>

      </li>

      <li>
        <a href="${pageContext.request.contextPath}/providers"
           class="sidebar-link"
           data-sidebar-path="/providers"
           title="Proveedores">
          <i class="bi bi-truck"></i>
          <span>Proveedores</span>
        </a>
      </li>

    </ul>

  </div>

  <div class="sidebar-footer">

    <a href="${pageContext.request.contextPath}/perfil"
       class="sidebar-link sidebar-profile"
       data-sidebar-path="/perfil"
       title="Perfil">
      <i class="bi bi-person-circle"></i>
      <span>Perfil</span>
    </a>

    <form action="${pageContext.request.contextPath}/logout"
          method="post"
          class="sidebar-logout-form">

      <input type="hidden"
             name="csrfToken"
             value="${csrfToken}">

      <button type="submit"
              class="sidebar-link sidebar-logout"
              title="Cerrar sesión">

        <i class="bi bi-box-arrow-right"></i>

        <span>
      Cerrar sesión
    </span>

      </button>

    </form>

  </div>

</aside>

<div id="sidebarOverlay"
     class="sidebar-overlay">
</div>

<jsp:include page="/components/toast.jsp"/>
<%-- Navegación principal compartida; muestra opciones según sesión y rol. --%>
