<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="offcanvas offcanvas-end"
     tabindex="-1"
     id="usuarioCanvas">

  <div class="offcanvas-header border-bottom">

    <div>

      <h4 class="offcanvas-title fw-bold"
          id="tituloFormulario">

        Nuevo Usuario

      </h4>

      <small class="text-secondary">

        Complete la información del usuario

      </small>

    </div>

    <button
            class="btn-close"
            data-bs-dismiss="offcanvas">

    </button>

  </div>

  <div class="offcanvas-body">

    <form id="formUsuario"
          action="UsuarioServlet"
          method="post">

      <input type="hidden"
             name="accion"
             id="accion"
             value="guardar">

      <input type="hidden"
             name="id"
             id="idUsuario">

      <!-- DATOS PERSONALES -->

      <h6 class="text-uppercase text-secondary mb-3">

        Datos personales

      </h6>

      <div class="mb-3">

        <label class="form-label">

          Nombre completo

        </label>

        <div class="input-group">

                    <span class="input-group-text">

                        <i class="bi bi-person"></i>

                    </span>

          <input
                  type="text"
                  class="form-control"
                  id="nombre"
                  name="nombre"
                  required>

        </div>

      </div>

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
                  id="correo"
                  name="correo"
                  required>

        </div>

      </div>

      <div class="mb-3">

        <label class="form-label">

          Teléfono

        </label>

        <div class="input-group">

                    <span class="input-group-text">

                        <i class="bi bi-telephone"></i>

                    </span>

          <input
                  type="text"
                  class="form-control"
                  id="telefono"
                  name="telefono">

        </div>

      </div>

      <hr>

      <!-- DATOS DE ACCESO -->

      <h6 class="text-uppercase text-secondary mb-3">

        Datos de acceso

      </h6>

      <div class="mb-3">

        <label class="form-label">

          Usuario

        </label>

        <div class="input-group">

                    <span class="input-group-text">

                        <i class="bi bi-person-badge"></i>

                    </span>

          <input
                  type="text"
                  class="form-control"
                  id="usuario"
                  name="usuario"
                  required>

        </div>

      </div>

      <div class="row">

        <div class="col-6">

          <div class="mb-3">

            <label class="form-label">

              Contraseña

            </label>

            <input
                    type="password"
                    class="form-control"
                    id="password"
                    name="password">

          </div>

        </div>

        <div class="col-6">

          <div class="mb-3">

            <label class="form-label">

              Confirmar

            </label>

            <input
                    type="password"
                    class="form-control"
                    id="confirmar">

          </div>

        </div>

      </div>

      <div class="row">

        <div class="col-md-6">

          <div class="mb-3">

            <label class="form-label">

              Rol

            </label>

            <select
                    class="form-select"
                    id="rol"
                    name="rol">

              <option value="Administrador">

                Administrador

              </option>

              <option value="Empleado">

                Empleado

              </option>

            </select>

          </div>

        </div>

        <div class="col-md-6">

          <div class="mb-3">

            <label class="form-label">

              Estado

            </label>

            <select
                    class="form-select"
                    id="estado"
                    name="estado">

              <option value="Activo">

                Activo

              </option>

              <option value="Inactivo">

                Inactivo

              </option>

            </select>

          </div>

        </div>

      </div>

    </form>

  </div>

  <div class="offcanvas-footer border-top p-3">

    <div class="d-grid gap-2">

      <button
              form="formUsuario"
              class="btn btn-primary">

        <i class="bi bi-floppy-fill"></i>

        Guardar Usuario

      </button>

      <button
              class="btn btn-outline-secondary"
              data-bs-dismiss="offcanvas">

        Cancelar

      </button>

    </div>

  </div>

</div>