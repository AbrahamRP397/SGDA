/** Habilita la visualización temporal de campos de contraseña accesibles.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
document.addEventListener("DOMContentLoaded", () => {

    const botones = document.querySelectorAll(".mostrarPassword");

    botones.forEach((boton) => {

        boton.addEventListener("click", () => {

            const grupo = boton.closest(".input-group");
            const password = grupo.querySelector(".passwordAct");
            const icono = boton.querySelector("i");

            if (password.type === "password") {
                password.type = "text";

                icono.classList.remove("bi-eye");
                icono.classList.add("bi-eye-slash");

            } else {
                password.type = "password";

                icono.classList.remove("bi-eye-slash");
                icono.classList.add("bi-eye");
            }
        });
    });
});
