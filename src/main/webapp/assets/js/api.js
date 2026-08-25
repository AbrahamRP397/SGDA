/**
 * Cliente HTTP compartido del SGDA.
 * Normaliza JSON, errores, tokens CSRF y respuestas no exitosas para los módulos.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function(){
    "use strict";

    if(window.Api){
        return;
    }

    class ApiError extends Error{
        constructor(message,status=0,data=null){
            super(message);
            this.name="ApiError";
            this.status=status;
            this.data=data;
        }
    }

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getContextPath(){
        const contextPath=
            document.body?.dataset?.contextPath;

        if(contextPath!==undefined){
            return contextPath;
        }

        return"";
    }

    /**
     * Ejecuta la operación buildUrl del módulo de interfaz.
     *
     * @param {*} url dirección del recurso solicitado
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function buildUrl(url){
        if(!url){
            throw new ApiError(
                "No se proporcionó una dirección para la petición."
            );
        }

        const normalizedUrl=
            String(url).trim();

        if(
            normalizedUrl.startsWith("http://")||
            normalizedUrl.startsWith("https://")
        ){
            return normalizedUrl;
        }

        if(normalizedUrl.startsWith("/")){
            return`${getContextPath()}${normalizedUrl}`;
        }

        return`${getContextPath()}/${normalizedUrl}`;
    }

    /**
     * Ejecuta la operación objectToUrlSearchParams del módulo de interfaz.
     *
     * @param {*} data datos que serán procesados por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function objectToUrlSearchParams(data){
        const params=new URLSearchParams();

        if(!data){
            return params;
        }

        Object.entries(data).forEach(
            function([key,value]){
                if(
                    value===undefined||
                    value===null
                ){
                    return;
                }

                if(Array.isArray(value)){
                    value.forEach(function(item){
                        params.append(
                            key,
                            String(item)
                        );
                    });

                    return;
                }

                params.append(
                    key,
                    String(value)
                );
            }
        );

        return params;
    }

    /**
     * Ejecuta la operación formToUrlSearchParams del módulo de interfaz.
     *
     * @param {*} form valor de form requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function formToUrlSearchParams(form){
        if(!form){
            throw new ApiError(
                "No se encontró el formulario."
            );
        }

        const params=new URLSearchParams();

        Array.from(form.elements).forEach(
            function(field){
                if(
                    !field.name||
                    field.disabled
                ){
                    return;
                }

                if(
                    (
                        field.type==="checkbox"||
                        field.type==="radio"
                    )&&
                    !field.checked
                ){
                    return;
                }

                if(
                    field instanceof HTMLSelectElement&&
                    field.multiple
                ){
                    Array.from(field.selectedOptions)
                        .forEach(function(option){
                            params.append(
                                field.name,
                                option.value
                            );
                        });

                    return;
                }

                params.append(
                    field.name,
                    field.value
                );
            }
        );

        return params;
    }

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} name valor de name requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getCookie(name){
        if(!name||!document.cookie){
            return"";
        }

        const cookies=document.cookie.split(";");

        for(const cookie of cookies){
            const separatorIndex=cookie.indexOf("=");

            if(separatorIndex<0){
                continue;
            }

            const key=cookie
                .slice(0,separatorIndex)
                .trim();

            if(key!==name){
                continue;
            }

            const value=cookie
                .slice(separatorIndex+1)
                .trim();

            try{
                return decodeURIComponent(value);
            }catch(error){
                return value;
            }
        }

        return"";
    }

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function getCsrfToken(){
        return getCookie("XSRF-TOKEN");
    }

    /**
     * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
     *
     * @param {*} method valor de method requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    function isUnsafeMethod(method){
        const normalizedMethod=
            String(method||"GET")
                .trim()
                .toUpperCase();

        return[
            "POST",
            "PUT",
            "PATCH",
            "DELETE"
        ].includes(normalizedMethod);
    }

    /**
     * Ejecuta la operación readJsonResponse del módulo de interfaz.
     *
     * @param {*} response valor de response requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    async function readJsonResponse(response){
        const contentType=
            response.headers.get(
                "content-type"
            )||"";

        if(
            !contentType
                .toLowerCase()
                .includes("application/json")
        ){
            if(
                response.redirected&&
                response.url
            ){
                window.location.href=
                    response.url;

                throw new ApiError(
                    "Tu sesión ha expirado.",
                    401
                );
            }

            throw new ApiError(
                "El servidor devolvió una respuesta no válida.",
                response.status
            );
        }

        let result;

        try{
            result=await response.json();
        }catch(error){
            throw new ApiError(
                "No fue posible interpretar la respuesta del servidor.",
                response.status
            );
        }

        if(!response.ok){
            if(response.status===401){
                window.setTimeout(function(){
                    window.location.href=
                        `${getContextPath()}/login`;
                },300);
            }

            throw new ApiError(
                result.message||
                "No fue posible completar la operación.",
                response.status,
                result
            );
        }

        return result;
    }

    /**
     * Ejecuta la operación request del módulo de interfaz.
     *
     * @param {*} url dirección del recurso solicitado
     * @param {*} options valor de options requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    async function request(url,options={}){
        const method=
            String(
                options.method||"GET"
            )
                .trim()
                .toUpperCase();

        const headers={
            "Accept":"application/json",
            "X-Requested-With":"XMLHttpRequest",
            ...(options.headers||{})
        };

        if(isUnsafeMethod(method)){
            const csrfToken=getCsrfToken();

            if(!csrfToken){
                throw new ApiError(
                    "No se encontró el token de seguridad. Recarga la página e intenta nuevamente.",
                    403,
                    {
                        success:false,
                        type:"error",
                        message:"No se encontró el token de seguridad. Recarga la página e intenta nuevamente."
                    }
                );
            }

            headers["X-CSRF-Token"]=csrfToken;
        }

        const requestOptions={
            method,
            credentials:"same-origin",
            cache:options.cache||"no-store",
            headers
        };

        if(options.body!==undefined){
            requestOptions.body=options.body;
        }

        let response;

        try{
            response=await fetch(
                buildUrl(url),
                requestOptions
            );
        }catch(error){
            throw new ApiError(
                "No fue posible conectar con el servidor.",
                0
            );
        }

        return readJsonResponse(response);
    }

    /**
     * Obtiene el valor solicitado a partir del estado actual de la interfaz.
     *
     * @param {*} url dirección del recurso solicitado
     * @param {*} query valor de query requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    async function get(url,query=null){
        let finalUrl=buildUrl(url);

        if(query){
            const params=
                objectToUrlSearchParams(query);

            const queryString=
                params.toString();

            if(queryString){
                finalUrl+=
                    finalUrl.includes("?")
                        ?`&${queryString}`
                        :`?${queryString}`;
            }
        }

        return request(
            finalUrl,
            {
                method:"GET"
            }
        );
    }

    /**
     * Ejecuta la operación post del módulo de interfaz.
     *
     * @param {*} url dirección del recurso solicitado
     * @param {*} data datos que serán procesados por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    async function post(url,data=null){
        const body=
            objectToUrlSearchParams(data);

        return request(
            url,
            {
                method:"POST",
                headers:{
                    "Content-Type":
                        "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body
            }
        );
    }

    /**
     * Valida y envía la información capturada por el usuario.
     *
     * @param {*} form valor de form requerido por la función
     * @param {*} options valor de options requerido por la función
     * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    async function submitForm(form,options={}){
        if(!form){
            throw new ApiError(
                "No se encontró el formulario."
            );
        }

        const method=
            String(
                options.method||
                form.method||
                "POST"
            )
                .trim()
                .toUpperCase();

        const url=
            options.url||
            form.action;

        const containsFiles=
            Array.from(
                form.querySelectorAll(
                    'input[type="file"]'
                )
            ).some(
                function(input){
                    return input.files?.length>0;
                }
            );

        if(
            options.multipart===true||
            containsFiles
        ){
            return request(
                url,
                {
                    method,
                    body:new FormData(form)
                }
            );
        }

        return request(
            url,
            {
                method,
                headers:{
                    "Content-Type":
                        "application/x-www-form-urlencoded;charset=UTF-8"
                },
                body:
                    formToUrlSearchParams(form)
            }
        );
    }

    window.Api={
        request,
        get,
        post,
        submitForm,
        formToUrlSearchParams,
        objectToUrlSearchParams,
        getContextPath,
        getCsrfToken,
        ApiError
    };
})();
