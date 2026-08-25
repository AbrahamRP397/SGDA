<%--
    Vista técnica: profile.
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

    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Mi Perfil</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.css"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style-sidebar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/table.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/modals.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/form.css">

</head>

<body data-context-path="${pageContext.request.contextPath}">

<jsp:include page="/components/theme-toggle.jsp"/>

<div class="d-flex">

    <jsp:include page="/components/sidebar.jsp"/>

    <main class="content p-3 p-md-4">

        <button type="button"
                id="btnOpenSidebar"
                class="sidebar-open-button"
                aria-label="Abrir menú">

            <i class="bi bi-list"></i>

            <span>Menú</span>

        </button>

        <div class="container-fluid px-0">

            <div class="table-page-header">

                <div>

                    <h2 class="table-page-title">

                        <i class="bi bi-person-circle"></i>

                        Mi Perfil

                    </h2>

                    <p class="mb-0 text-secondary">

                        Consulta y administra la información de tu cuenta.

                    </p>

                </div>

            </div>

            <c:if test="${not empty profileError}">

                <div class="alert alert-danger">

                    <i class="bi bi-exclamation-triangle-fill me-2"></i>

                    <c:out value="${profileError}"/>

                </div>

            </c:if>

            <c:if test="${not empty profileUser}">

                <!-- =====================================================
                INFORMACIÓN PERSONAL
                ===================================================== -->

                <section class="table-panel mb-4">

                    <div class="table-panel-header">

                        <div>

                            <h2 class="table-panel-title">

                                <i class="bi bi-person-vcard"></i>

                                Información personal

                            </h2>

                            <p class="mb-0 text-secondary">

                                Información actualmente asociada a tu cuenta.

                            </p>

                        </div>

                    </div>

                    <div class="row g-3">

                        <div class="col-12 col-lg-6">

                            <label class="form-label fw-semibold">

                                Nombre completo

                            </label>

                            <div class="form-control">

                                <i class="bi bi-person me-2"></i>

                                <span id="profileFullName">

                                    <c:out value="${profileUser.name}"/>
                                    <c:out value="${profileUser.surname}"/>
                                    <c:out value="${profileUser.lastname}"/>

                                </span>

                            </div>

                        </div>

                        <div class="col-12 col-lg-6">

                            <label class="form-label fw-semibold">

                                Correo electrónico

                            </label>

                            <div class="form-control">

                                <i class="bi bi-envelope me-2"></i>

                                <span id="profileEmail">

                                    <c:out value="${profileUser.email}"/>

                                </span>

                            </div>

                        </div>

                        <div class="col-12 col-lg-6">

                            <label class="form-label fw-semibold">

                                Teléfono

                            </label>

                            <div class="form-control">

                                <i class="bi bi-telephone me-2"></i>

                                <span id="profilePhone">

                                    <c:out value="${profileUser.phone}"/>

                                </span>

                            </div>

                        </div>

                        <div class="col-12 col-lg-6">

                            <label class="form-label fw-semibold">

                                Rol

                            </label>

                            <div class="form-control">

                                <i class="bi bi-shield-check me-2"></i>

                                <span id="profileRole">

                                    <c:out value="${profileUser.role}"/>

                                </span>

                            </div>

                        </div>

                    </div>

                    <div class="alert alert-info mt-4 mb-0">

                        <i class="bi bi-shield-lock-fill me-2"></i>

                        El rol y el estado de tu cuenta no pueden
                        modificarse desde tu perfil.

                    </div>

                </section>

                <!-- =====================================================
                ACCIONES
                ===================================================== -->

                <section class="table-panel">

                    <div class="table-panel-header">

                        <h2 class="table-panel-title">

                            <i class="bi bi-sliders"></i>

                            Acciones de cuenta

                        </h2>

                    </div>

                    <div class="d-flex flex-column flex-md-row gap-3">

                        <button type="button"
                                class="table-primary-btn"
                                data-bs-toggle="modal"
                                data-bs-target="#modalEditProfile">

                            <i class="bi bi-pencil-square"></i>

                            <span>
                                Editar información
                            </span>

                        </button>

                        <button type="button"
                                class="table-toolbar-btn"
                                data-bs-toggle="modal"
                                data-bs-target="#modalChangePassword">

                            <i class="bi bi-key"></i>

                            <span>
                                Cambiar contraseña
                            </span>

                        </button>

                        <form action="${pageContext.request.contextPath}/logout"
                              method="post"
                              class="d-inline">

                            <input type="hidden"
                                   name="csrfToken"
                                   value="${csrfToken}">

                            <button type="submit"
                                    class="table-toolbar-btn">

                                <i class="bi bi-box-arrow-right"></i>

                                <span>
                                    Cerrar sesión
                                </span>

                            </button>

                        </form>

                    </div>

                </section>

            </c:if>

        </div>

    </main>

</div>

<!-- ============================================================
     MODAL EDITAR INFORMACIÓN
     ============================================================ -->

<div class="modal fade"
     id="modalEditProfile"
     tabindex="-1"
     aria-hidden="true">

    <div class="modal-dialog modal-dialog-centered modal-lg">

        <div class="modal-content">

            <div class="modal-header">

                <div>

                    <h5 class="modal-title">

                        <i class="bi bi-pencil-square"></i>

                        Editar información

                    </h5>

                    <p class="modal-subtitle mb-0">

                        Actualiza tus datos personales.

                    </p>

                </div>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                </button>

            </div>

            <form id="formEditProfile"
                  action="${pageContext.request.contextPath}/perfil/update"
                  method="post"
                  novalidate>

                <input type="hidden"
                       name="csrfToken"
                       value="<c:out value='${requestScope.csrfToken}'/>">

                <div class="modal-body">

                    <div class="row g-3">

                        <div class="col-12 col-md-6">

                            <label for="profileName"
                                   class="form-label">

                                Nombre

                                <span class="text-danger">
                                    *
                                </span>

                            </label>

                            <input type="text"
                                   id="profileName"
                                   name="name"
                                   class="form-control"
                                   value="<c:out value='${profileUser.name}'/>"
                                   maxlength="50"
                                   autocomplete="given-name"
                                   required>

                            <div class="invalid-feedback">

                                Ingresa un nombre válido.

                            </div>

                        </div>

                        <div class="col-12 col-md-6">

                            <label for="profileSurname"
                                   class="form-label">

                                Apellido paterno

                                <span class="text-danger">
                                    *
                                </span>

                            </label>

                            <input type="text"
                                   id="profileSurname"
                                   name="surname"
                                   class="form-control"
                                   value="<c:out value='${profileUser.surname}'/>"
                                   maxlength="50"
                                   required>

                            <div class="invalid-feedback">

                                Ingresa el apellido paterno.

                            </div>

                        </div>

                        <div class="col-12 col-md-6">

                            <label for="profileLastname"
                                   class="form-label">

                                Apellido materno

                                <span class="text-danger">
                                    *
                                </span>

                            </label>

                            <input type="text"
                                   id="profileLastname"
                                   name="lastname"
                                   class="form-control"
                                   value="<c:out value='${profileUser.lastname}'/>"
                                   maxlength="50"
                                   required>

                            <div class="invalid-feedback">

                                Ingresa el apellido materno.

                            </div>

                        </div>

                        <div class="col-12 col-md-6">

                            <label for="profileEditPhone"
                                   class="form-label">

                                Teléfono

                                <span class="text-danger">
                                    *
                                </span>

                            </label>

                            <input type="tel"
                                   id="profileEditPhone"
                                   name="phone"
                                   class="form-control"
                                   value="<c:out value='${profileUser.phone}'/>"
                                   inputmode="numeric"
                                   minlength="10"
                                   maxlength="10"
                                   pattern="[0-9]{10}"
                                   autocomplete="tel"
                                   required>

                            <div class="invalid-feedback">

                                Ingresa exactamente 10 dígitos.

                            </div>

                        </div>

                        <div class="col-12">

                            <label for="profileEditEmail"
                                   class="form-label">

                                Correo electrónico

                                <span class="text-danger">
                                    *
                                </span>

                            </label>

                            <input type="email"
                                   id="profileEditEmail"
                                   name="email"
                                   class="form-control"
                                   value="<c:out value='${profileUser.email}'/>"
                                   maxlength="100"
                                   autocomplete="email"
                                   required>

                            <div class="invalid-feedback">

                                Ingresa un correo electrónico válido.

                            </div>

                        </div>

                    </div>

                </div>

                <div class="modal-footer">

                    <button type="button"
                            class="table-toolbar-btn"
                            data-bs-dismiss="modal">

                        Cancelar

                    </button>

                    <button type="submit"
                            id="btnSaveProfile"
                            class="table-primary-btn">

                        <i class="bi bi-check-lg"></i>

                        <span>
                            Guardar cambios
                        </span>

                    </button>

                </div>

            </form>

        </div>

    </div>

</div>

<!-- ============================================================
     MODAL CAMBIAR CONTRASEÑA
     ============================================================ -->

<div class="modal fade"
     id="modalChangePassword"
     tabindex="-1"
     aria-hidden="true">

    <div class="modal-dialog modal-dialog-centered">

        <div class="modal-content">

            <div class="modal-header">

                <div>

                    <h5 class="modal-title">

                        <i class="bi bi-key"></i>

                        Cambiar contraseña

                    </h5>

                    <p class="modal-subtitle mb-0">

                        Confirma tu contraseña actual y
                        establece una nueva.

                    </p>

                </div>

                <button type="button"
                        class="btn-close"
                        data-bs-dismiss="modal"
                        aria-label="Cerrar">
                </button>

            </div>

            <form id="formChangePassword"
                  action="${pageContext.request.contextPath}/perfil/change-password"
                  method="post"
                  novalidate>

                <input type="hidden"
                       name="csrfToken"
                       value="<c:out value='${requestScope.csrfToken}'/>">

                <div class="modal-body">

                    <div class="mb-3">

                        <label for="currentPassword"
                               class="form-label">

                            Contraseña actual

                            <span class="text-danger">
                                *
                            </span>

                        </label>

                        <input type="password"
                               id="currentPassword"
                               name="currentPassword"
                               class="form-control"
                               autocomplete="current-password"
                               maxlength="72"
                               required>

                        <div class="invalid-feedback">

                            Ingresa tu contraseña actual.

                        </div>

                    </div>

                    <div class="mb-3">

                        <label for="newPassword"
                               class="form-label">

                            Nueva contraseña

                            <span class="text-danger">
                                *
                            </span>

                        </label>

                        <input type="password"
                               id="newPassword"
                               name="newPassword"
                               class="form-control"
                               autocomplete="new-password"
                               minlength="8"
                               maxlength="72"
                               pattern="(?=.*[A-Za-z])(?=.*[0-9]).{8,72}"
                               required>

                        <div class="form-text">

                            Debe tener entre 8 y 72 caracteres
                            e incluir letras y números.

                        </div>

                        <div class="invalid-feedback">

                            La contraseña debe contener entre
                            8 y 72 caracteres, letras y números.

                        </div>

                    </div>

                    <div>

                        <label for="confirmation"
                               class="form-label">

                            Confirmar nueva contraseña

                            <span class="text-danger">
                                *
                            </span>

                        </label>

                        <input type="password"
                               id="confirmation"
                               name="confirmation"
                               class="form-control"
                               autocomplete="new-password"
                               minlength="8"
                               maxlength="72"
                               required>

                        <div class="invalid-feedback">

                            Confirma la nueva contraseña.

                        </div>

                    </div>

                </div>

                <div class="modal-footer">

                    <button type="button"
                            class="table-toolbar-btn"
                            data-bs-dismiss="modal">

                        Cancelar

                    </button>

                    <button type="submit"
                            id="btnConfirmPassword"
                            class="table-primary-btn">

                        <i class="bi bi-shield-check"></i>

                        <span>
                            Cambiar contraseña
                        </span>

                    </button>

                </div>

            </form>

        </div>

    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.7/dist/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/form.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/toast.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/api.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/profile.js"></script>

</body>
</html>
<%-- Consulta y actualización de los datos de la cuenta autenticada. --%>
