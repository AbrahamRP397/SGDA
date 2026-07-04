<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">

    <title>Gestión de Usuarios</title>

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <!-- Bootstrap -->

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <!-- Bootstrap Icons -->

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css"
          rel="stylesheet">

    <!-- DataTables -->

    <link href="https://cdn.datatables.net/2.3.2/css/dataTables.bootstrap5.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/usuarios.css">

</head>

<body>

<jsp:include page="../../components/header.jsp"/>

<div class="d-flex">

    <jsp:include page="../../components/sidebar.jsp"/>

    <main class="content flex-grow-1 p-4">

        <div class="container-fluid">

            <!-- Encabezado -->

            <div class="card shadow-sm border-0 mb-4">

                <div class="card-body">

                    <div class="row align-items-center">

                        <div class="col-md-8">

                            <h2 class="fw-bold mb-1">

                                Gestión de Usuarios

                            </h2>

                            <p class="text-secondary mb-0">

                                Administración de usuarios del sistema.

                            </p>

                        </div>

                        <div class="col-md-4 text-end">

                            <button
                                    class="btn btn-primary px-4"
                                    data-bs-toggle="offcanvas"
                                    data-bs-target="#usuarioCanvas">

                                <i class="bi bi-person-plus-fill"></i>

                                Nuevo Usuario

                            </button>

                        </div>

                    </div>

                </div>

            </div>

            <!-- Filtros -->

            <div class="card border-0 shadow-sm mb-4">

                <div class="card-body">

                    <div class="row g-3">

                        <div class="col-lg-4">

                            <label class="form-label">

                                Buscar

                            </label>

                            <input
                                    type="text"
                                    class="form-control"
                                    placeholder="Nombre o correo">

                        </div>

                        <div class="col-lg-3">

                            <label class="form-label">

                                Rol

                            </label>

                            <select class="form-select">

                                <option>Todos</option>

                                <option>Administrador</option>

                                <option>Empleado</option>

                            </select>

                        </div>

                        <div class="col-lg-3">

                            <label class="form-label">

                                Estado

                            </label>

                            <select class="form-select">

                                <option>Todos</option>

                                <option>Activo</option>

                                <option>Inactivo</option>

                            </select>

                        </div>

                        <div class="col-lg-2 d-grid">

                            <label class="form-label">

                                &nbsp;

                            </label>

                            <button class="btn btn-secondary">

                                <i class="bi bi-search"></i>

                                Buscar

                            </button>

                        </div>

                    </div>

                </div>

            </div>

            <!-- Tabla -->

            <div class="card border-0 shadow-sm">

                <div class="card-body">

                    <div class="table-responsive">

                        <table
                                id="tablaUsuarios"
                                class="table table-hover align-middle">

                            <thead>

                            <tr>

                                <th>ID</th>

                                <th>Nombre</th>

                                <th>Correo</th>

                                <th>Teléfono</th>

                                <th>Rol</th>

                                <th>Estado</th>

                                <th width="170">

                                    Acciones

                                </th>

                            </tr>

                            </thead>

                            <tbody>

                            <tr>

                                <td>1</td>

                                <td>

                                    Administrador

                                </td>

                                <td>

                                    admin@gmail.com

                                </td>

                                <td>

                                    7771234567

                                </td>

                                <td>

                                    <span class="badge bg-primary">

                                        Administrador

                                    </span>

                                </td>

                                <td>

                                    <span class="badge bg-success">

                                        Activo

                                    </span>

                                </td>

                                <td>

                                    <button
                                            class="btn btn-warning btn-sm editar">

                                        <i class="bi bi-pencil-square"></i>

                                    </button>

                                    <button
                                            class="btn btn-danger btn-sm eliminar">

                                        <i class="bi bi-trash-fill"></i>

                                    </button>

                                </td>

                            </tr>

                            <tr>

                                <td>2</td>

                                <td>

                                    Juan Pérez

                                </td>

                                <td>

                                    juan@gmail.com

                                </td>

                                <td>

                                    7775551234

                                </td>

                                <td>

                                    <span class="badge bg-secondary">

                                        Empleado

                                    </span>

                                </td>

                                <td>

                                    <span class="badge bg-success">

                                        Activo

                                    </span>

                                </td>

                                <td>

                                    <button
                                            class="btn btn-warning btn-sm editar">

                                        <i class="bi bi-pencil-square"></i>

                                    </button>

                                    <button
                                            class="btn btn-danger btn-sm eliminar">

                                        <i class="bi bi-trash-fill"></i>

                                    </button>

                                </td>

                            </tr>

                            </tbody>

                        </table>

                    </div>

                </div>

            </div>

        </div>

    </main>

</div>

<!-- Aquí se incluirá el Offcanvas -->

<jsp:include page="../../components/offcanvasUsuario.jsp"/>

<!-- Aquí se incluirá el Modal -->

<jsp:include page="../../components/modalEliminar.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>

<script src="https://code.jquery.com/jquery-3.7.1.js"></script>

<script src="https://cdn.datatables.net/2.3.2/js/dataTables.js"></script>

<script src="https://cdn.datatables.net/2.3.2/js/dataTables.bootstrap5.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/usuarios.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>

</body>

</html>