<%@ page contentType="text/html;charset=UTF-8" %>

<div class="modal fade"
     id="modalEliminar"
     tabindex="-1">

    <div class="modal-dialog modal-dialog-centered">

        <div class="modal-content border-0 shadow">

            <div class="modal-header bg-danger text-white">

                <h5 class="modal-title">

                    <i class="bi bi-trash3-fill me-2"></i>

                    Eliminar usuario

                </h5>

                <button class="btn-close btn-close-white"
                        data-bs-dismiss="modal">

                </button>

            </div>

            <div class="modal-body text-center">

                <i class="bi bi-exclamation-triangle-fill
                          text-warning"
                   style="font-size:70px"></i>

                <h4 class="mt-3">

                    ¿Desea eliminar este usuario?

                </h4>

                <p class="text-secondary">

                    Esta acción no podrá deshacerse.

                </p>

                <strong id="nombreEliminar">

                    Usuario

                </strong>

            </div>

            <div class="modal-footer">

                <button class="btn btn-secondary"
                        data-bs-dismiss="modal">

                    Cancelar

                </button>

                <form action="UsuarioServlet"
                      method="post">

                    <input type="hidden"
                           name="accion"
                           value="eliminar">

                    <input type="hidden"
                           id="idEliminar"
                           name="id">

                    <button class="btn btn-danger">

                        <i class="bi bi-trash-fill"></i>

                        Eliminar

                    </button>

                </form>

            </div>

        </div>

    </div>

</div>