<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">

    <title>Iniciar sesión</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/login.css">

</head>

<body>

<nav class="navbar navbar-dark navbar-expand-lg shadow">

    <div class="container">

        <span class="navbar-brand fw-bold">

            Sistema Gestor de Almacén

        </span>

        <img src="${pageContext.request.contextPath}/assets/img/logoSGDAClosed.svg"
             width="45">

    </div>

</nav>

<div class="container">

    <div class="row justify-content-center align-items-center vh-100">

        <div class="col-md-6 col-lg-4">

            <div class="card shadow-lg border-0">

                <div class="card-body p-5">

                    <div class="text-center">

                        <img src="${pageContext.request.contextPath}/assets/img/logoSGDAClosed.svg"
                             width="120">

                        <h3 class="mt-3">

                            Inicio de sesión

                        </h3>

                    </div>

                    <form action="LoginServlet" method="post">

                        <div class="mb-3">

                            <label class="form-label">

                                Correo electrónico

                            </label>

                            <div class="input-group">

                                <span class="input-group-text">

                                    <i class="bi bi-envelope"></i>

                                </span>

                                <input
                                        type="email"
                                        class="form-control"
                                        name="correo"
                                        required>

                            </div>

                        </div>

                        <div class="mb-3">

                            <label class="form-label">

                                Contraseña

                            </label>

                            <div class="input-group">

                                <span class="input-group-text">

                                    <i class="bi bi-lock"></i>

                                </span>

                                <input
                                        type="password"
                                        id="password"
                                        class="form-control"
                                        name="password"
                                        required>

                                <button
                                        class="btn btn-outline-secondary"
                                        type="button"
                                        id="mostrarPassword">

                                    <i class="bi bi-eye"></i>

                                </button>

                            </div>

                        </div>

                        <div class="form-check mb-4">

                            <input
                                    class="form-check-input"
                                    type="checkbox">

                            <label class="form-check-label">

                                Recordarme

                            </label>

                        </div>

                        <button
                                class="btn btn-primary w-100">

                            <i class="bi bi-box-arrow-in-right"></i>

                            Iniciar sesión

                        </button>

                    </form>

                    <div class="text-center mt-4">

                        <a href="recuperarPassword.jsp"
                           class="text-decoration-none">

                            ¿Olvidaste tu contraseña?

                        </a>

                    </div>

                </div>

            </div>

        </div>

    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/login.js"></script>

</body>

</html>