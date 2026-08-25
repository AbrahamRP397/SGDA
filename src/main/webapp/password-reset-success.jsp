<%--
    Vista técnica: password-reset-success.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Contraseña actualizada</title>

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

                <div class="card-body p-4 p-sm-5 text-center">

                    <div class="mb-4">
                        <i class="bi bi-check-circle-fill text-success" style="font-size: 4rem;"></i>
                    </div>

                    <h3 class="fw-bold">Contraseña actualizada</h3>

                    <p class="text-muted mt-3 small">
                        Tu contraseña fue cambiada correctamente.
                        Ahora puedes iniciar sesión con tu nueva contraseña.
                    </p>

                    <div class="alert alert-success mt-4" role="alert">
                        Serás redirigido al inicio de sesión en
                        <strong><span id="contador">5</span></strong>
                        segundos.
                    </div>

                    <div class="d-grid mt-4">
                        <a href="${pageContext.request.contextPath}/login" class="login-button">
                            <i class="bi bi-box-arrow-in-right me-2"></i>
                            Volver al inicio de sesión
                        </a>
                    </div>

                </div>

            </div>

        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>

<script>
    (function() {
        let segundos = 5;
        const contador = document.getElementById('contador');

        if (!contador) return;

        const intervalo = setInterval(function() {
            segundos--;
            contador.textContent = segundos;

            if (segundos <= 0) {
                clearInterval(intervalo);
                window.location.href = '${pageContext.request.contextPath}/login';
            }
        }, 1000);
    })();
</script>

</body>

</html>
<%-- Confirmación final del restablecimiento de contraseña. --%>
