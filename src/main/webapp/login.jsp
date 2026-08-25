<%--
    Vista técnica: login.
    Responsabilidad: estructura la interfaz, enlaza recursos y expone datos preparados por los controladores.
    Autor: Dulce Janet Ríos Aguilar.
    Desde: 2026-08-24.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css" rel="stylesheet">
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
                        <img id="logoSistema"
                             src="${pageContext.request.contextPath}/assets/img/logoSGDAClosed.svg"
                             alt="Logo del sistema"
                             class="img-fluid">
                        <h3 class="mt-3">Inicio de sesión</h3>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger d-flex align-items-center py-2" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2 flex-shrink-0"></i>
                            <div class="small">
                                <c:out value="${error}"/>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${not empty mensaje}">
                        <div class="alert alert-info d-flex align-items-center py-2" role="alert">
                            <i class="bi bi-info-circle-fill me-2 flex-shrink-0"></i>
                            <div class="small">
                                <c:out value="${mensaje}"/>
                            </div>
                        </div>
                    </c:if>

                    <form id="loginForm"
                          action="${pageContext.request.contextPath}/login"
                          method="post">
                        <input type="hidden"
                               id="loginCsrfToken"
                               name="csrfToken"
                               value="<c:out value='${requestScope.csrfToken}'/>">

                        <div class="mb-3">
                            <label for="email" class="form-label">Correo electrónico</label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="bi bi-envelope"></i>
                                </span>
                                <input type="email"
                                       id="email"
                                       class="form-control"
                                       name="email"
                                       placeholder="ejemplo@correo.com"
                                       value="${emailIngresado}"
                                       required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label">Contraseña</label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="bi bi-lock"></i>
                                </span>
                                <input type="password"
                                       id="password"
                                       class="form-control passwordAct"
                                       name="password"
                                       placeholder="Ingresa tu contraseña"
                                       required>
                                <button class="btn btn-outline-secondary mostrarPassword"
                                        type="button"
                                        aria-label="Mostrar contraseña">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>

                        <button class="login-button" type="submit">
                            <i class="bi bi-box-arrow-in-right"></i>
                            <span>Iniciar sesión</span>
                        </button>

                    </form>

                    <div class="text-center mt-4">
                        <a href="${pageContext.request.contextPath}/verify-email"
                           class="text-decoration-none">
                            ¿Olvidaste tu contraseña?
                        </a>
                    </div>

                </div>

            </div>

        </div>

    </div>
</div>

<script>
    window.addEventListener("pageshow", function (event) {
        if (event.persisted) {
            window.location.reload();
        }
    });

    window.loginExitoso = ${loginExitoso == true};
    window.contextPath = "${pageContext.request.contextPath}";
    window.redirectUrl = "${redirectUrl}";
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/show-password.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/login-animation.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/theme-logo.js"></script>

<script>
    (function(){
        "use strict";

        const form=document.getElementById("loginForm");
        const csrfInput=document.getElementById("loginCsrfToken");

        if(!form||!csrfInput){
            return;
        }

        function getCookie(name){
            const prefix=name+"=";
            const cookies=document.cookie?document.cookie.split(";"):[];

            for(const cookieValue of cookies){
                const cookie=cookieValue.trim();

                if(cookie.startsWith(prefix)){
                    return decodeURIComponent(
                        cookie.substring(prefix.length)
                    );
                }
            }

            return "";
        }

        function synchronizeCsrfToken(){
            const token=getCookie("XSRF-TOKEN");

            if(token){
                csrfInput.value=token;
            }
        }

        synchronizeCsrfToken();

        window.addEventListener(
            "pageshow",
            synchronizeCsrfToken
        );

        window.addEventListener(
            "focus",
            synchronizeCsrfToken
        );

        form.addEventListener(
            "submit",
            synchronizeCsrfToken
        );
    })();
</script>

</body>

</html>
<%-- Vista pública de autenticación; delega la validación de credenciales a LoginServlet. --%>
