<%--
    Vista técnica: force-password-change.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cambiar contraseña</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/form.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">

    <style>
        body {
            min-height: 100vh;
            margin: 0;
            display: grid;
            place-items: center;
            padding: 20px;
            color: var(--text-color);
            background:
                    linear-gradient(var(--overlay-color), var(--overlay-color)),
                    var(--login-bg-image) center center / cover no-repeat fixed;
            transition: background 0.3s ease, color 0.3s ease;
        }

        .password-change-card {
            width: 100%;
            max-width: 480px;
            padding: 30px;
            border-radius: 20px;
            background: var(--card-bg, #e0e5ec);
            box-shadow: var(
                    --neumo-shadow,
                    8px 8px 16px #a3b1c6,
                    -8px -8px 16px #ffffff
            );
        }

        .password-change-icon {
            display: grid;
            place-items: center;
            width: 72px;
            height: 72px;
            margin: 0 auto 18px;
            border-radius: 50%;
            font-size: 32px;
            color: #6390ff;
            box-shadow: inset 3px 3px 7px rgba(0, 0, 0, .12),
            inset -3px -3px 7px rgba(255, 255, 255, .08);
        }

        .password-change-description {
            color: var(--text-muted, #718096);
            line-height: 1.6;
        }
    </style>
</head>

<body>
<jsp:include page="/components/theme-toggle.jsp"/>

<section class="password-change-card">
    <div class="password-change-icon">
        <i class="bi bi-shield-lock-fill"></i>
    </div>

    <h2 class="text-center mb-2">
        Crea una contraseña nueva
    </h2>

    <p class="password-change-description text-center mb-4">
        Iniciaste sesión con una contraseña temporal. Debes establecer una contraseña definitiva para continuar.
    </p>

    <c:if test="${not empty error}">
        <div class="neumo-alert neumo-alert-error" role="alert">
            <i class="bi bi-exclamation-circle-fill"></i>

            <div class="neumo-alert-text">
                <c:out value="${error}"/>
            </div>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/force-password-change"
          method="post"
          class="js-form"
          novalidate>

        <input type="hidden"
               name="csrfToken"
               value="<c:out value='${requestScope.csrfToken}'/>">

        <div class="mb-3 form-field">
            <label for="password" class="form-label">
                Nueva contraseña
                <span class="text-danger">*</span>
            </label>

            <div class="input-group form-password-group">
                <span class="input-group-text">
                    <i class="bi bi-lock"></i>
                </span>

                <input type="password"
                       class="form-control passwordAct js-form-field"
                       id="password"
                       name="password"
                       minlength="8"
                       maxlength="72"
                       data-type="password"
                       data-label="Nueva contraseña"
                       data-valid-message="Contraseña válida."
                       autocomplete="new-password"
                       required>

                <button type="button"
                        class="btn btn-outline-secondary mostrarPassword"
                        aria-label="Mostrar contraseña">
                    <i class="bi bi-eye"></i>
                </button>
            </div>

            <div class="form-text password-change-description">
                Debe contener entre 8 y 72 caracteres, incluyendo letras y números.
            </div>

            <div class="valid-feedback">
                Contraseña válida.
            </div>

            <div class="invalid-feedback"></div>
        </div>

        <div class="mb-4 form-field">
            <label for="confirmation" class="form-label">
                Confirmar contraseña
                <span class="text-danger">*</span>
            </label>

            <div class="input-group form-password-group">
                <span class="input-group-text">
                    <i class="bi bi-lock-fill"></i>
                </span>

                <input type="password"
                       class="form-control passwordAct js-form-field"
                       id="confirmation"
                       name="confirmation"
                       minlength="8"
                       maxlength="72"
                       data-type="password"
                       data-label="Confirmación de contraseña"
                       data-valid-message="Confirmación válida."
                       autocomplete="new-password"
                       required>

                <button type="button"
                        class="btn btn-outline-secondary mostrarPassword"
                        aria-label="Mostrar contraseña">
                    <i class="bi bi-eye"></i>
                </button>
            </div>

            <div class="valid-feedback">
                Confirmación válida.
            </div>

            <div class="invalid-feedback"></div>
        </div>

        <button type="submit"
                class="btn btn-primary w-100 js-form-submit"
                data-loading-text="Actualizando...">
            <i class="bi bi-check-circle me-1"></i>
            Guardar contraseña
        </button>
    </form>
</section>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/show-password.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/form.js"></script>
</body>
</html>
<%-- Vista obligatoria para sustituir una contraseña temporal antes de continuar. --%>
