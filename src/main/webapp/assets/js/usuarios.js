document.addEventListener("DOMContentLoaded", () => {

    //----------------------------------------
    // DataTable
    //----------------------------------------

    new DataTable("#tablaUsuarios", {

        language: {

            url: "https://cdn.datatables.net/plug-ins/2.3.2/i18n/es-MX.json"

        },

        responsive: true,

        pageLength: 10

    });

    //----------------------------------------
    // Nuevo Usuario
    //----------------------------------------

    const btnNuevo = document.querySelector("[data-bs-target='#usuarioCanvas']");

    btnNuevo.addEventListener("click", () => {

        document.getElementById("tituloFormulario").innerHTML = "Nuevo Usuario";

        document.getElementById("accion").value = "guardar";

        document.getElementById("formUsuario").reset();

        document.getElementById("idUsuario").value = "";

    });

    //----------------------------------------
    // Editar
    //----------------------------------------

    document.querySelectorAll(".editar").forEach(btn => {

        btn.addEventListener("click", function () {

            let fila = this.closest("tr");

            let columnas = fila.querySelectorAll("td");

            document.getElementById("tituloFormulario").innerHTML = "Editar Usuario";

            document.getElementById("accion").value = "editar";

            document.getElementById("idUsuario").value = columnas[0].innerText;

            document.getElementById("nombre").value = columnas[1].innerText;

            document.getElementById("correo").value = columnas[2].innerText;

            document.getElementById("telefono").value = columnas[3].innerText;

            document.getElementById("rol").value =
                columnas[4].innerText.trim();

            document.getElementById("estado").value =
                columnas[5].innerText.trim();

            document.getElementById("password").value = "";

            document.getElementById("confirmar").value = "";

            new bootstrap.Offcanvas(
                document.getElementById("usuarioCanvas")
            ).show();

        });

    });

    //----------------------------------------
    // Eliminar
    //----------------------------------------

    document.querySelectorAll(".eliminar").forEach(btn => {

        btn.addEventListener("click", function () {

            let fila = this.closest("tr");

            let columnas = fila.querySelectorAll("td");

            document.getElementById("idEliminar").value =
                columnas[0].innerText;

            document.getElementById("nombreEliminar").innerHTML =
                columnas[1].innerText;

            new bootstrap.Modal(
                document.getElementById("modalEliminar")
            ).show();

        });

    });

    //----------------------------------------
    // Validación
    //----------------------------------------

    document.getElementById("formUsuario")
        .addEventListener("submit", function (e) {

            let password = document.getElementById("password").value;

            let confirmar = document.getElementById("confirmar").value;

            if (password !== confirmar) {

                e.preventDefault();

                alert("Las contraseñas no coinciden.");

                return;

            }

        });

});