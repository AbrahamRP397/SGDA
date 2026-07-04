const boton = document.getElementById("mostrarPassword");

const password = document.getElementById("password");

boton.addEventListener("click",()=>{

    if(password.type==="password"){

        password.type="text";

        boton.innerHTML='<i class="bi bi-eye-slash"></i>';

    }else{

        password.type="password";

        boton.innerHTML='<i class="bi bi-eye"></i>';

    }

});