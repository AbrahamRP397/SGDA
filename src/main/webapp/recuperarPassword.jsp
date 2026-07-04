<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">

<head>

  <meta charset="UTF-8">

  <title>Recuperar contraseña</title>

  <meta name="viewport"
        content="width=device-width, initial-scale=1">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css"
        rel="stylesheet">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css"
        rel="stylesheet">

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/login.css">

</head>

<body>

<nav class="navbar navbar-dark shadow">

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

    <div class="col-md-6 col-lg-5">

      <div class="card shadow-lg border-0">

        <div class="card-body p-5">

          <div class="text-center">

            <h3 class="mt-3">

              Recuperar contraseña

            </h3>

            <p class="text-muted">

              Introduce el correo electrónico
              registrado para enviarte un enlace
              de recuperación.

            </p>

          </div>

          <form action="RecuperarPasswordServlet"
                method="post">

            <div class="mb-4">

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

            <div class="d-grid">

              <button
                      class="btn btn-primary">

                <i class="bi bi-send-fill"></i>

                Enviar enlace

              </button>

            </div>

          </form>

          <hr>

          <div class="text-center">

            <a href="index.jsp"
               class="text-decoration-none">

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

</body>

</html>