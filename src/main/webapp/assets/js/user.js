/**
 * Módulo de administración de usuarios.
 * Coordina tabla, formularios, validación cliente y endpoints de UserServlet.
 * Las altas y cambios de estado se confirman en el servidor.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function(){
    "use strict";

    if(window.userModuleInitialized)return;
    window.userModuleInitialized=true;

    document.addEventListener("DOMContentLoaded",function(){
        const TABLE_ID="usersTable";
        const table=document.getElementById(TABLE_ID);
        const tableBody=table?.querySelector("tbody");

        const formCreate=document.getElementById("formCreateUser");
        const formEdit=document.getElementById("formEditUser");
        const formChangeStatus=document.getElementById("formChangeStatus");
        const formResetAccess=document.getElementById("formResetAccess");

        const btnNuevoUsuario=document.getElementById("btnNuevoUsuario");
        const btnSaveCreate=document.getElementById("btnSaveCreate");
        const btnOpenConfirmEdit=document.getElementById("btnOpenConfirmEdit");
        const btnConfirmEdit=document.getElementById("btnConfirmEdit");
        const btnConfirmStatus=document.getElementById("btnConfirmStatus");
        const btnConfirmResetAccess=document.getElementById("btnConfirmResetAccess");

        const modalCreate=getModal("modalCreate");
        const modalView=getModal("modalView");
        const modalEdit=getModal("modalEdit");
        const modalConfirmEdit=getModal("modalConfirmEdit");
        const modalConfirmStatus=getModal("modalConfirmStatus");
        const modalConfirmResetAccess=getModal("modalConfirmResetAccess");

        if(!table||!tableBody){
            console.warn("No se encontró la tabla de usuarios.");
            return;
        }

        if(!window.Api){
            console.error("api.js no está disponible.");
            return;
        }

        const resetDescription=document.querySelector(
            "#modalConfirmResetAccess .modal-body p:nth-of-type(2)"
        );

        if(resetDescription){
            resetDescription.textContent=
                "La contraseña actual dejará de funcionar. Se generará una contraseña temporal válida durante 24 horas y se enviará al correo electrónico del usuario.";
        }

        if(btnConfirmResetAccess){
            btnConfirmResetAccess.innerHTML=
                '<i class="bi bi-envelope-check me-1"></i>Restablecer y enviar';
            btnConfirmResetAccess.dataset.loadingText="Enviando...";
        }

        /* ================= REGISTRAR ================= */

        btnNuevoUsuario?.addEventListener("click",function(){
            resetForm(formCreate,true);
            modalCreate?.show();

            window.setTimeout(function(){
                document.getElementById("createName")?.focus();
            },200);
        });

        formCreate?.addEventListener("submit",async function(event){
            event.preventDefault();
            event.stopImmediatePropagation();

            if(
                !validateForm(formCreate)||
                btnSaveCreate?.disabled||
                formCreate.dataset.fetchSubmitting==="true"
            ){
                return;
            }

            formCreate.dataset.fetchSubmitting="true";
            setFormLoading(formCreate,btnSaveCreate,true,"Guardando...");

            try{
                const result=await Api.submitForm(formCreate);

                if(!result.success){
                    showToast(result);
                    return;
                }

                modalCreate?.hide();
                resetForm(formCreate,true);
                await loadUsers();
                showToast(result);
            }catch(error){
                handleRequestError(error);
            }finally{
                delete formCreate.dataset.fetchSubmitting;
                setFormLoading(formCreate,btnSaveCreate,false);
            }
        });

        /* ================= EDITAR ================= */

        btnOpenConfirmEdit?.addEventListener("click",function(){
            if(!validateForm(formEdit))return;

            setText(
                "editConfirmName",
                buildFullName(
                    getValue("editName"),
                    getValue("editSurname"),
                    getValue("editLastname")
                )
            );

            modalConfirmEdit?.show();
        });

        btnConfirmEdit?.addEventListener("click",async function(){
            if(
                !formEdit||
                btnConfirmEdit.disabled||
                formEdit.dataset.fetchSubmitting==="true"
            ){
                return;
            }

            if(!validateForm(formEdit)){
                modalConfirmEdit?.hide();
                return;
            }

            if(!isPositiveInteger(getValue("editUserId"))){
                showWarning("No se pudo determinar el usuario que deseas actualizar.");
                modalConfirmEdit?.hide();
                return;
            }

            formEdit.dataset.fetchSubmitting="true";
            setFormLoading(formEdit,btnConfirmEdit,true,"Actualizando...");

            try{
                const result=await Api.submitForm(formEdit);
                showToast(result);

                if(!result.success)return;

                modalConfirmEdit?.hide();
                modalEdit?.hide();
                resetForm(formEdit,false);
                await loadUsers();
            }catch(error){
                handleRequestError(error);
            }finally{
                delete formEdit.dataset.fetchSubmitting;
                setFormLoading(formEdit,btnConfirmEdit,false);
            }
        });

        /* ================= ESTADO ================= */

        btnConfirmStatus?.addEventListener("click",async function(){
            if(
                !formChangeStatus||
                btnConfirmStatus.disabled||
                formChangeStatus.dataset.fetchSubmitting==="true"
            ){
                return;
            }

            const userId=getValue("statusUserId");
            const newStatus=getValue("statusNewValue");

            if(!isPositiveInteger(userId)){
                showWarning("No se pudo determinar el usuario.");
                return;
            }

            if(!isValidStatus(newStatus)){
                showWarning("No se pudo determinar el nuevo estado.");
                return;
            }

            formChangeStatus.dataset.fetchSubmitting="true";
            setFormLoading(formChangeStatus,btnConfirmStatus,true,"Procesando...");

            try{
                const result=await Api.submitForm(formChangeStatus);
                showToast(result);

                if(!result.success)return;

                modalConfirmStatus?.hide();
                await loadUsers();
            }catch(error){
                handleRequestError(error);
            }finally{
                delete formChangeStatus.dataset.fetchSubmitting;
                setFormLoading(formChangeStatus,btnConfirmStatus,false);
            }
        });

        /* ================= RESTABLECER ACCESO ================= */

        btnConfirmResetAccess?.addEventListener("click",async function(){
            if(
                !formResetAccess||
                btnConfirmResetAccess.disabled||
                formResetAccess.dataset.fetchSubmitting==="true"
            ){
                return;
            }

            const userId=getValue("resetAccessUserId");

            if(!isPositiveInteger(userId)){
                showWarning("No se pudo determinar el usuario.");
                return;
            }

            formResetAccess.dataset.fetchSubmitting="true";

            setFormLoading(
                formResetAccess,
                btnConfirmResetAccess,
                true,
                "Enviando..."
            );

            try{
                const result=await Api.submitForm(formResetAccess);

                if(!result.success){
                    showToast(result);
                    return;
                }

                modalConfirmResetAccess?.hide();
                await loadUsers();
                showToast(result);
            }catch(error){
                handleRequestError(error);
            }finally{
                delete formResetAccess.dataset.fetchSubmitting;

                setFormLoading(
                    formResetAccess,
                    btnConfirmResetAccess,
                    false
                );
            }
        });

        /* ================= TABLA ================= */

        tableBody.addEventListener("click",function(event){
            const button=event.target.closest(".table-action-btn");

            if(!button)return;

            const row=button.closest(".user-table-row");

            if(!row)return;

            event.preventDefault();
            event.stopPropagation();

            if(button.classList.contains("btn-view-user")){
                openViewModal(row);
                return;
            }

            if(button.classList.contains("btn-edit-user")){
                openEditModal(row);
                return;
            }

            if(button.classList.contains("btn-reset-access")){
                openResetAccessModal(button,row);
                return;
            }

            if(button.classList.contains("btn-change-status")){
                openStatusModal(button,row);
            }
        });

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadUsers(){
            const result=await Api.get("/users/list");

            if(!result.success){
                throw new Api.ApiError(
                    result.message||"No fue posible consultar los usuarios.",
                    400,
                    result
                );
            }

            renderUsers(
                Array.isArray(result.data)
                    ?result.data
                    :[]
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} users valor de users requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderUsers(users){
            tableBody.replaceChildren();

            users.forEach(function(user){
                tableBody.appendChild(
                    createUserRow(user)
                );
            });

            updateTableVisibility(users.length);

            if(typeof window.filterTable==="function"){
                window.filterTable(TABLE_ID);
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} user valor de user requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createUserRow(user){
            const id=normalizeId(user.id);
            const name=normalizeText(user.name);
            const surname=normalizeText(user.surname);
            const lastname=normalizeText(user.lastname);
            const fullName=buildFullName(name,surname,lastname);
            const email=normalizeText(user.email);
            const phone=normalizeText(user.phone);
            const role=normalizeRole(user.role);
            const roleValue=role.toLocaleLowerCase("es-MX");
            const mustChangePassword=Number(user.mustChangePassword)===1;
            const active=Number(user.status)===1;

            const row=document.createElement("tr");
            row.className="js-table-row user-table-row";
            row.dataset.id=String(id);
            row.dataset.name=name;
            row.dataset.surname=surname;
            row.dataset.lastname=lastname;
            row.dataset.fullName=fullName;
            row.dataset.email=email;
            row.dataset.phone=phone;
            row.dataset.role=roleValue;
            row.dataset.mustChangePassword=String(mustChangePassword);
            row.dataset.status=active?"active":"inactive";
            row.dataset.search=[
                name,
                surname,
                lastname,
                fullName,
                email,
                phone,
                role
            ].join(" ");

            const idCell=createCell(
                String(id),
                "table-cell-secondary table-cell-nowrap"
            );

            const nameCell=createCell(
                fullName||"Sin nombre",
                "table-cell-primary"
            );

            const emailCell=createCell(
                email||"Sin correo",
                "table-cell-secondary"
            );

            const phoneCell=createCell(
                phone||"Sin teléfono",
                "table-cell-secondary table-cell-nowrap"
            );

            const roleCell=document.createElement("td");
            const roleBadge=document.createElement("span");

            roleBadge.className=
                roleValue==="administrador"
                    ?"table-badge table-badge-primary"
                    :"table-badge table-badge-warning";

            roleBadge.textContent=role;
            roleCell.appendChild(roleBadge);

            const accessCell=document.createElement("td");
            const accessBadge=document.createElement("span");

            accessBadge.className=
                mustChangePassword
                    ?"table-badge table-badge-warning"
                    :"table-badge table-badge-success";

            accessBadge.textContent=
                mustChangePassword
                    ?"Cambio pendiente"
                    :"Normal";

            accessCell.appendChild(accessBadge);

            const statusCell=document.createElement("td");
            const statusBadge=document.createElement("span");

            statusBadge.className=
                active
                    ?"table-badge table-badge-success"
                    :"table-badge table-badge-danger";

            statusBadge.textContent=
                active
                    ?"Activo"
                    :"Inactivo";

            statusCell.appendChild(statusBadge);

            const actionsCell=document.createElement("td");
            const actionsContainer=document.createElement("div");

            actionsContainer.className="table-actions";

            actionsContainer.append(
                createActionButton({
                    className:"table-action-btn table-action-view btn-view-user",
                    title:"Ver detalles",
                    icon:"bi bi-eye"
                }),
                createActionButton({
                    className:"table-action-btn table-action-edit btn-edit-user",
                    title:"Editar usuario",
                    icon:"bi bi-pencil"
                }),
                createResetAccessButton({
                    id,
                    fullName,
                    active
                }),
                createStatusButton({
                    id,
                    fullName,
                    active
                })
            );

            actionsCell.appendChild(actionsContainer);

            row.append(
                idCell,
                nameCell,
                emailCell,
                phoneCell,
                roleCell,
                accessCell,
                statusCell,
                actionsCell
            );

            return row;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} text valor de text requerido por la función
         * @param {*} className valor de className requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createCell(text,className=""){
            const cell=document.createElement("td");
            cell.className=className;
            cell.textContent=text;
            return cell;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} config valor de config requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createActionButton(config){
            const button=document.createElement("button");
            const icon=document.createElement("i");

            button.type="button";
            button.className=config.className;
            button.title=config.title;
            button.setAttribute("aria-label",config.title);

            icon.className=config.icon;
            button.appendChild(icon);

            return button;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} config valor de config requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createResetAccessButton(config){
            const button=createActionButton({
                className:"table-action-btn table-action-warning btn-reset-access",
                title:config.active
                    ?"Restablecer acceso y enviar por correo"
                    :"Activa al usuario para restablecer su acceso",
                icon:"bi bi-key"
            });

            button.dataset.userId=String(config.id);
            button.dataset.userName=config.fullName;
            button.disabled=!config.active;

            return button;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} config valor de config requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createStatusButton(config){
            const button=createActionButton({
                className:
                    "table-action-btn btn-change-status "+
                    (
                        config.active
                            ?"table-action-delete"
                            :"table-action-success"
                    ),
                title:
                    config.active
                        ?"Desactivar usuario"
                        :"Activar usuario",
                icon:
                    config.active
                        ?"bi bi-toggle-on"
                        :"bi bi-toggle-off"
            });

            button.dataset.userId=String(config.id);
            button.dataset.userName=config.fullName;
            button.dataset.newStatus=config.active?"0":"1";

            return button;
        }

        /* ================= MODALES ================= */

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openViewModal(row){
            const fullName=row.dataset.fullName||"-";

            const role=
                row.dataset.role==="administrador"
                    ?"Administrador"
                    :"Almacenista";

            const active=row.dataset.status==="active";
            const mustChangePassword=
                row.dataset.mustChangePassword==="true";

            setText("viewName",fullName);
            setText("viewFullName",fullName);
            setText("viewEmail",row.dataset.email||"Sin correo");
            setText("viewPhone",row.dataset.phone||"Sin teléfono");
            setText("viewRole",role);

            const roleBadge=document.getElementById("viewRoleBadge");

            if(roleBadge){
                roleBadge.textContent=role;

                roleBadge.className=
                    role==="Administrador"
                        ?"table-badge table-badge-primary"
                        :"table-badge table-badge-warning";
            }

            setBadge(
                "viewAccessStatus",
                mustChangePassword
                    ?"Cambio pendiente"
                    :"Normal",
                mustChangePassword
                    ?"table-badge-warning"
                    :"table-badge-success"
            );

            setBadge(
                "viewStatus",
                active
                    ?"Activo"
                    :"Inactivo",
                active
                    ?"table-badge-success"
                    :"table-badge-danger"
            );

            modalView?.show();
        }

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openEditModal(row){
            resetForm(formEdit,false);

            setValue("editUserId",row.dataset.id);
            setValue("editName",row.dataset.name);
            setValue("editSurname",row.dataset.surname);
            setValue("editLastname",row.dataset.lastname);
            setValue("editEmail",row.dataset.email);
            setValue("editPhone",row.dataset.phone);

            setValue(
                "editRole",
                row.dataset.role==="administrador"
                    ?"Administrador"
                    :"Almacenista"
            );

            setText(
                "editConfirmName",
                row.dataset.fullName||"-"
            );

            modalEdit?.show();
        }

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} button valor de button requerido por la función
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openResetAccessModal(button,row){
            const userId=normalizeText(
                button.dataset.userId||
                row.dataset.id
            );

            const userName=normalizeText(
                button.dataset.userName||
                row.dataset.fullName
            );

            if(!isPositiveInteger(userId)){
                showWarning("No se pudo determinar el usuario.");
                return;
            }

            if(row.dataset.status!=="active"){
                showWarning("Activa al usuario antes de restablecer su acceso.");
                return;
            }

            setValue("resetAccessUserId",userId);

            setText(
                "resetAccessConfirmUserName",
                userName||"Usuario"
            );

            modalConfirmResetAccess?.show();
        }

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} button valor de button requerido por la función
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openStatusModal(button,row){
            const userId=normalizeText(
                button.dataset.userId||
                row.dataset.id
            );

            const userName=normalizeText(
                button.dataset.userName||
                row.dataset.fullName
            );

            const newStatus=normalizeText(
                button.dataset.newStatus
            );

            if(
                !isPositiveInteger(userId)||
                !isValidStatus(newStatus)
            ){
                showWarning(
                    "No se pudo determinar el usuario o el nuevo estado."
                );
                return;
            }

            const activating=newStatus==="1";

            setValue("statusUserId",userId);
            setValue("statusNewValue",newStatus);
            setText(
                "statusConfirmUserName",
                userName||"Usuario"
            );

            setText(
                "statusModalQuestion",
                activating
                    ?"¿Deseas activar este usuario?"
                    :"¿Deseas desactivar este usuario?"
            );

            setText(
                "statusModalDescription",
                activating
                    ?"El usuario podrá volver a ingresar y utilizar el sistema."
                    :"El usuario ya no podrá ingresar al sistema mientras permanezca inactivo."
            );

            setText(
                "statusConfirmButtonText",
                activating
                    ?"Activar"
                    :"Desactivar"
            );

            configureStatusModal(activating);
            modalConfirmStatus?.show();
        }

        /**
         * Ejecuta la operación configureStatusModal del módulo de interfaz.
         *
         * @param {*} activating valor de activating requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function configureStatusModal(activating){
            const modalIcon=document.getElementById("statusModalIcon");
            const headerIcon=document.getElementById("statusModalHeaderIcon");
            const confirmIcon=document.getElementById("statusConfirmButtonIcon");

            if(btnConfirmStatus){
                btnConfirmStatus.disabled=false;

                btnConfirmStatus.className=
                    activating
                        ?"btn btn-success"
                        :"btn btn-danger";
            }

            if(modalIcon){
                modalIcon.className=
                    activating
                        ?"bi bi-person-check"
                        :"bi bi-person-x";

                modalIcon.style.color=
                    activating
                        ?"#57d38c"
                        :"#ff6666";
            }

            if(headerIcon){
                headerIcon.className=
                    activating
                        ?"bi bi-check-circle-fill me-2"
                        :"bi bi-exclamation-triangle-fill me-2";

                headerIcon.style.color=
                    activating
                        ?"#57d38c"
                        :"#ff6666";
            }

            if(confirmIcon){
                confirmIcon.className=
                    activating
                        ?"bi bi-person-check me-1"
                        :"bi bi-person-x me-1";
            }
        }

        /* ================= VISIBILIDAD ================= */

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} totalRows valor de totalRows requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateTableVisibility(totalRows){
            const responsive=table.closest(".table-responsive");

            const pagination=document.querySelector(
                `.table-pagination[data-table-target="${TABLE_ID}"]`
            );

            const generalEmptyState=
                document.getElementById(
                    "usersGeneralEmptyState"
                );

            const filterEmptyState=
                document.getElementById(
                    "usersFilterEmptyState"
                );

            if(responsive){
                responsive.style.display=
                    totalRows>0
                        ?""
                        :"none";
            }

            if(pagination){
                pagination.style.display=
                    totalRows>0
                        ?"grid"
                        :"none";
            }

            if(generalEmptyState){
                generalEmptyState.style.display=
                    totalRows===0
                        ?"block"
                        :"none";
            }

            if(filterEmptyState){
                filterEmptyState.style.display="none";
            }
        }

        /* ================= FORMULARIOS ================= */

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function validateForm(form){
            if(!form)return false;

            if(
                window.Form&&
                typeof Form.validate==="function"
            ){
                return Form.validate(form);
            }

            const valid=form.checkValidity();

            form.classList.toggle(
                "was-validated",
                !valid
            );

            return valid;
        }

        /**
         * Ejecuta la operación resetForm del módulo de interfaz.
         *
         * @param {*} form valor de form requerido por la función
         * @param {*} resetValues valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function resetForm(form,resetValues){
            if(!form)return;

            if(
                window.Form&&
                typeof Form.reset==="function"
            ){
                Form.reset(
                    form,
                    {
                        resetValues:Boolean(resetValues),
                        unlock:true
                    }
                );

                return;
            }

            if(resetValues){
                HTMLFormElement.prototype.reset.call(form);
            }

            form.classList.remove("was-validated");

            form.querySelectorAll(
                ".is-valid,.is-invalid"
            ).forEach(function(field){
                field.classList.remove(
                    "is-valid",
                    "is-invalid"
                );

                field.removeAttribute(
                    "aria-invalid"
                );
            });
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} form valor de form requerido por la función
         * @param {*} button valor de button requerido por la función
         * @param {*} loading valor de loading requerido por la función
         * @param {*} loadingText valor de loadingText requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setFormLoading(form,button,loading,loadingText){
            if(window.Form){
                if(typeof Form.loading==="function"){
                    Form.loading(
                        button,
                        loading,
                        loadingText
                    );
                }

                if(
                    loading&&
                    typeof Form.lock==="function"
                ){
                    Form.lock(form);
                }

                if(
                    !loading&&
                    typeof Form.unlock==="function"
                ){
                    Form.unlock(form);
                }

                return;
            }

            if(button){
                button.disabled=loading;
            }
        }

        /* ================= MENSAJES ================= */

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} result valor de result requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function showToast(result){
            if(
                window.AppToast&&
                typeof AppToast.fromResponse==="function"
            ){
                AppToast.fromResponse(result);
                return;
            }

            if(
                window.AppToast&&
                typeof AppToast.show==="function"
            ){
                AppToast.show(
                    result.message||"Operación realizada.",
                    result.type||
                    (
                        result.success
                            ?"success"
                            :"error"
                    )
                );

                return;
            }

            console.log(result.message);
        }

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         *
         * @param {*} message valor de message requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function showWarning(message){
            if(
                window.AppToast&&
                typeof AppToast.warning==="function"
            ){
                AppToast.warning(message);
                return;
            }

            window.alert(message);
        }

        /**
         * Procesa el evento de interfaz asociado a esta función.
         *
         * @param {*} error valor de error requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function handleRequestError(error){
            console.error(error);

            if(Number(error.status)===401){
                window.location.href=
                    `${Api.getContextPath()}/login`;
                return;
            }

            if(
                error.data&&
                typeof error.data==="object"
            ){
                showToast(error.data);
                return;
            }

            const message=
                error.message||
                "No fue posible completar la operación.";

            if(
                window.AppToast&&
                typeof AppToast.error==="function"
            ){
                AppToast.error(message);
                return;
            }

            window.alert(message);
        }

        /* ================= AUXILIARES ================= */

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         *
         * @param {*} id identificador del registro o componente
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getModal(id){
            const element=document.getElementById(id);

            if(
                !element||
                typeof bootstrap==="undefined"||
                !bootstrap.Modal
            ){
                return null;
            }

            return bootstrap.Modal.getOrCreateInstance(element);
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} id identificador del registro o componente
         * @param {*} text valor de text requerido por la función
         * @param {*} badgeClass valor de badgeClass requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setBadge(id,text,badgeClass){
            const container=document.getElementById(id);

            if(!container)return;

            container.replaceChildren();

            const badge=document.createElement("span");

            badge.className=
                `table-badge ${badgeClass}`;

            badge.textContent=text;

            container.appendChild(badge);
        }

        /**
         * Ejecuta la operación buildFullName del módulo de interfaz.
         *
         * @param {*} name valor de name requerido por la función
         * @param {*} surname valor de surname requerido por la función
         * @param {*} lastname valor de lastname requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function buildFullName(name,surname,lastname){
            return[
                normalizeText(name),
                normalizeText(surname),
                normalizeText(lastname)
            ].filter(Boolean).join(" ")||"-";
        }

        /**
         * Ejecuta la operación normalizeRole del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeRole(value){
            const role=normalizeText(value);

            if(
                role.toLocaleLowerCase("es-MX")==="administrador"
            ){
                return"Administrador";
            }

            return"Almacenista";
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function isPositiveInteger(value){
            const normalizedValue=normalizeText(value);

            return(
                /^\d+$/.test(normalizedValue)&&
                Number(normalizedValue)>0
            );
        }

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function isValidStatus(value){
            const normalizedValue=normalizeText(value);

            return(
                normalizedValue==="0"||
                normalizedValue==="1"
            );
        }

        /**
         * Ejecuta la operación normalizeId del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeId(value){
            const id=Number(value);

            return(
                Number.isInteger(id)&&
                id>0
            )
                ?id
                :0;
        }

        /**
         * Ejecuta la operación normalizeText del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeText(value){
            return value==null
                ?""
                :String(value).trim();
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} id identificador del registro o componente
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setText(id,value){
            const element=document.getElementById(id);

            if(element){
                element.textContent=
                    value==null
                        ?""
                        :String(value);
            }
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} id identificador del registro o componente
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setValue(id,value){
            const element=document.getElementById(id);

            if(element){
                element.value=
                    value==null
                        ?""
                        :String(value);
            }
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         *
         * @param {*} id identificador del registro o componente
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getValue(id){
            const element=document.getElementById(id);

            return element
                ?normalizeText(element.value)
                :"";
        }
    });
})();
