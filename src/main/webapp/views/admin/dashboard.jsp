<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE html>

<html lang="es">

<head>

  <meta charset="UTF-8">

  <title>Dashboard</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css" rel="stylesheet">

  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css" rel="stylesheet">

  <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/style.css">

</head>

<body>

<jsp:include page="../../components/header.jsp"/>

<div class="d-flex">

  <jsp:include page="../../components/sidebar.jsp"/>

  <main class="content p-4">

    <h2 class="fw-bold mb-4">

      Dashboard

    </h2>

    <div class="row g-4">

      <div class="col-lg-3">

        <div class="card shadow border-0">

          <div class="card-body">

            <i class="bi bi-people display-5 text-primary"></i>

            <h5 class="mt-3">

              Usuarios

            </h5>

            <h2>

              25

            </h2>

          </div>

        </div>

      </div>

      <div class="col-lg-3">

        <div class="card shadow border-0">

          <div class="card-body">

            <i class="bi bi-box display-5 text-success"></i>

            <h5 class="mt-3">

              Productos

            </h5>

            <h2>

              420

            </h2>

          </div>

        </div>

      </div>

      <div class="col-lg-3">

        <div class="card shadow border-0">

          <div class="card-body">

            <i class="bi bi-truck display-5 text-warning"></i>

            <h5 class="mt-3">

              Proveedores

            </h5>

            <h2>

              18

            </h2>

          </div>

        </div>

      </div>

      <div class="col-lg-3">

        <div class="card shadow border-0">

          <div class="card-body">

            <i class="bi bi-boxes display-5 text-danger"></i>

            <h5 class="mt-3">

              Stock

            </h5>

            <h2>

              1300

            </h2>

          </div>

        </div>

      </div>

    </div>

  </main>

</div>

<jsp:include page="../../components/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>

</body>

</html>