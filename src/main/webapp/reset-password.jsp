<%--
    Vista técnica: reset-password.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Restablecer contraseña</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>

<body>
<jsp:include page="/components/theme-toggle.jsp"/>
<div class="container">
    <div class="row justify-content-center align-items-center">

        <div class="col-11 col-sm-10 col-md-8 col-lg-5 col-xl-4">

            <div class="card shadow-lg border-0">

                <div class="card-body p-4 p-sm-5">

                    <div class="text-center">
                        <h3 class="mt-3">Restablecimiento de contraseña</h3>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger d-flex align-items-center py-2" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2 flex-shrink-0"></i>
                            <div class="small">${error}</div>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/reset-password" method="post">
                        <input type="hidden"
                               name="token"
                               value="<c:out value='${token}'/>">

                        <input type="hidden"
                               name="csrfToken"
                               value="<c:out value='${requestScope.csrfToken}'/>">

                        <div class="mb-3">

                        <div class="mb-3">
                            <label for="password" class="form-label">Nueva contraseña</label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="bi bi-lock"></i>
                                </span>
                                <input type="password"
                                       id="password"
                                       name="password"
                                       class="form-control passwordAct"
                                       minlength="8"
                                       maxlength="72"
                                       placeholder="Ingresa tu nueva contraseña"
                                       required>
                                <button class="btn btn-outline-secondary mostrarPassword"
                                        type="button"
                                        aria-label="Mostrar contraseña">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Confirmar contraseña</label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="bi bi-lock"></i>
                                </span>
                                <input type="password"
                                       id="confirmPassword"
                                       name="confirmPassword"
                                       class="form-control passwordAct"
                                       minlength="8"
                                       maxlength="72"
                                       placeholder="Confirma tu contraseña"
                                       required>
                                <button class="btn btn-outline-secondary mostrarPassword"
                                        type="button"
                                        aria-label="Mostrar contraseña">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="login-button">
                                Cambiar contraseña
                            </button>
                        </div>

                    </form>

                    <hr>

                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                            <i class="bi bi-arrow-left"></i>
                            Volver al inicio de sesión
                        </a>
                    </div>

                </div>

            </div>

        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/show-password.js"></script>

</body>

</html>
<%-- Vista de captura de la nueva contraseña asociada a un token vigente. --%>
