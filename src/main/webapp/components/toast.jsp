<%--
    Vista técnica: toast.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- ==========================================================
COMPONENTE: TOAST DE NOTIFICACIONES (REUTILIZABLE)
========================================================== -->
<div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;">
  <div id="toastNotification" class="toast align-items-center border-0" role="alert" aria-live="assertive" aria-atomic="true" style="background: var(--card-bg, #e0e5ec); border-radius: 14px; box-shadow: var(--neumo-shadow, 8px 8px 16px #a3b1c6, -8px -8px 16px #ffffff);">
    <div class="d-flex">
      <div class="toast-body d-flex align-items-center gap-2" style="color: var(--text-color, #2d3748);">
        <i id="toastIcon" class="bi bi-check-circle-fill" style="color: #57d38c; font-size: 20px;"></i>
        <span id="toastMessage">Operación exitosa</span>
      </div>
      <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar" style="color: var(--text-muted, #718096);"></button>
    </div>
  </div>
</div>
<%-- Componente reutilizable para notificaciones visuales accesibles. --%>
