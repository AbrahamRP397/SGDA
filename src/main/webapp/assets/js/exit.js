/**
 * Módulo de salidas de almacén.
 * Construye el movimiento, valida existencias y comunica la operación a ExitServlet.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function(){
    "use strict";
    if(window.exitModuleInitialized)return;
    window.exitModuleInitialized=true;

    document.addEventListener("DOMContentLoaded",function(){
        const TABLE_ID="exitsTable";
        const table=document.getElementById(TABLE_ID);
        const tableBody=table?.querySelector("tbody");
        const formCreateExit=document.getElementById("formCreateExit");
        const formQuickAreaExit=document.getElementById("formQuickAreaExit");
        const exitArea=document.getElementById("exitArea");
        const exitAreaFilter=document.getElementById("exitAreaFilter");
        const exitProductList=document.getElementById("exitProductList");
        const exitProductEmpty=document.getElementById("exitProductEmpty");
        const exitProductsLoading=document.getElementById("exitProductsLoading");
        const exitProductRowTemplate=document.getElementById("exitProductRowTemplate");
        const exitProductCount=document.getElementById("exitProductCount");
        const exitQuantityTotal=document.getElementById("exitQuantityTotal");
        const btnNewExit=document.getElementById("btnNewExit");
        const btnAddExitProduct=document.getElementById("btnAddExitProduct");
        const btnOpenConfirmExit=document.getElementById("btnOpenConfirmExit");
        const btnConfirmExit=document.getElementById("btnConfirmExit");
        const btnOpenQuickAreaExit=document.getElementById("btnOpenQuickAreaExit");
        const btnCloseQuickAreaExit=document.getElementById("btnCloseQuickAreaExit");
        const btnCancelQuickAreaExit=document.getElementById("btnCancelQuickAreaExit");
        const btnSaveQuickAreaExit=document.getElementById("btnSaveQuickAreaExit");
        const modalCreateExitElement=document.getElementById("modalCreateExit");
        const modalQuickAreaExitElement=document.getElementById("modalQuickAreaExit");
        const modalCreateExit=getModal("modalCreateExit");
        const modalConfirmExit=getModal("modalConfirmExit");
        const modalViewExit=getModal("modalViewExit");
        const modalQuickAreaExit=getModal("modalQuickAreaExit");
        let availableProducts=[];
        let productsRequestId=0;
        let quickAreaReturning=false;

        if(!table||!tableBody){
            console.warn("No se encontró la tabla de salidas.");
            return;
        }

        if(!window.Api){
            console.error("api.js no está disponible.");
            return;
        }

        formatInitialTableValues();

        btnNewExit?.addEventListener("click",async function(){
            resetExitForm();
            modalCreateExit?.show();
            window.setTimeout(function(){
                document.getElementById("exitInvoiceNumber")?.focus();
            },250);
            await loadAvailableProducts();
        });

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadAvailableProducts(){
            productsRequestId++;
            const currentRequestId=productsRequestId;
            availableProducts=[];
            clearProductRows();
            setProductsLoading(true);

            if(btnAddExitProduct)btnAddExitProduct.disabled=true;

            try{
                const result=await Api.get("/exits/products");

                if(currentRequestId!==productsRequestId)return;

                if(!result.success){
                    throw new Api.ApiError(
                        result.message||"No fue posible consultar las existencias.",
                        400,
                        result
                    );
                }

                availableProducts=normalizeAvailableProducts(result.data);

                if(availableProducts.length===0){
                    updateProductEmptyState("No hay productos con existencia disponible.");
                    return;
                }

                btnAddExitProduct.disabled=false;
                addProductRow();
            }catch(error){
                if(currentRequestId!==productsRequestId)return;
                availableProducts=[];
                updateProductEmptyState("No fue posible cargar los productos disponibles.");
                handleRequestError(error);
            }finally{
                if(currentRequestId===productsRequestId)setProductsLoading(false);
            }
        }

        /**
         * Ejecuta la operación normalizeAvailableProducts del módulo de interfaz.
         *
         * @param {*} data datos que serán procesados por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeAvailableProducts(data){
            if(!Array.isArray(data))return[];

            return data.map(function(product){
                return{
                    idProduct:normalizePositiveInteger(product.idProduct),
                    code:normalizeText(product.code),
                    name:normalizeText(product.name),
                    metricName:normalizeText(product.metricName),
                    metricShortName:normalizeText(product.metricShortName),
                    availableQuantity:
                        normalizePositiveInteger(
                            product.availableQuantity
                        )||0,
                    status:Number(product.status)===1?1:0,
                    active:Number(product.status)===1,
                    statusLabel:
                        normalizeText(product.statusLabel)
                };
            }).filter(function(product){
                return product.idProduct!==null
                    &&product.availableQuantity>0;
            });
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} loading valor de loading requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setProductsLoading(loading){
            exitProductsLoading?.classList.toggle("is-visible",loading);
        }

        btnAddExitProduct?.addEventListener("click",function(){
            if(availableProducts.length===0){
                showWarning("No hay productos con existencia disponible.");
                return;
            }

            const remainingProducts=getAvailableProductsForNewRow();

            if(remainingProducts.length===0){
                showWarning("Ya agregaste todos los productos disponibles.");
                return;
            }

            addProductRow();
        });

        exitProductList?.addEventListener("click",function(event){
            const removeButton=event.target.closest(".exit-product-remove");
            if(!removeButton)return;

            const row=removeButton.closest(".exit-product-row");
            row?.remove();
            refreshProductOptions();
            updateProductEmptyState();
            updateExitSummary();
        });

        exitProductList?.addEventListener("change",function(event){
            const select=event.target.closest(".exit-product-select");
            if(!select)return;

            const row=select.closest(".exit-product-row");
            updateRowFromProduct(row);
            refreshProductOptions();
            updateExitSummary();
        });

        exitProductList?.addEventListener("input",function(event){
            const quantityInput=event.target.closest(".exit-product-quantity");
            if(!quantityInput)return;

            const row=quantityInput.closest(".exit-product-row");
            clearFieldValidation(quantityInput);
            updateQuantityStatus(row);
            updateExitSummary();
        });

        /**
         * Ejecuta la operación addProductRow del módulo de interfaz.
         *
         * @param {*} selectedProductId valor de selectedProductId requerido por la función
         * @param {*} quantity valor de quantity requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function addProductRow(selectedProductId=null,quantity=""){
            if(!exitProductList||!exitProductRowTemplate)return;

            const fragment=exitProductRowTemplate.content.cloneNode(true);
            const select=fragment.querySelector(".exit-product-select");
            const quantityInput=fragment.querySelector(".exit-product-quantity");

            populateProductSelect(select,selectedProductId);

            if(quantityInput&&quantity!=="")quantityInput.value=String(quantity);

            exitProductList.appendChild(fragment);

            const insertedRow=exitProductList.lastElementChild;

            if(selectedProductId!==null)updateRowFromProduct(insertedRow);

            updateProductEmptyState();
            refreshProductOptions();
            updateQuantityStatus(insertedRow);
            updateExitSummary();

            if(selectedProductId===null){
                insertedRow?.querySelector(".exit-product-select")?.focus();
            }
        }

        /**
         * Ejecuta la operación populateProductSelect del módulo de interfaz.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} selectedProductId valor de selectedProductId requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function populateProductSelect(select,selectedProductId=null){
            if(!select)return;

            select.replaceChildren(
                createOption("","Seleccione un producto")
            );

            availableProducts.forEach(function(product){
                const baseLabel=
                    product.code
                        ?`${product.code} — ${product.name}`
                        :product.name;

                const label=
                    product.active
                        ?baseLabel
                        :`${baseLabel} · INACTIVO · SOLO SALIDA`;

                const option=createOption(product.idProduct,label);
                option.dataset.productCode=product.code;
                option.dataset.productName=product.name;
                option.dataset.metricName=product.metricName;
                option.dataset.metricShortName=product.metricShortName;
                option.dataset.availableQuantity=String(product.availableQuantity);
                option.dataset.productStatus=String(product.status);
                select.appendChild(option);
            });

            select.value=selectedProductId!==null
                ?String(selectedProductId)
                :"";
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateRowFromProduct(row){
            if(!row)return;

            const select=row.querySelector(".exit-product-select");
            const metricText=row.querySelector(".exit-product-metric");
            const stockText=row.querySelector(".exit-product-stock strong");
            const quantityInput=row.querySelector(".exit-product-quantity");
            const selectedOption=select?.selectedOptions?.[0];

            if(!selectedOption||!selectedOption.value){
                row.dataset.availableQuantity="0";

                if(metricText)metricText.textContent="Unidad: —";
                if(stockText)stockText.textContent="0";

                if(quantityInput){
                    quantityInput.value="";
                    quantityInput.max="999999999";
                }

                updateQuantityStatus(row);
                return;
            }

            const metricName=normalizeText(selectedOption.dataset.metricName);
            const metricShortName=normalizeText(selectedOption.dataset.metricShortName);
            const availableQuantity=
                normalizePositiveInteger(selectedOption.dataset.availableQuantity)||0;

            row.dataset.availableQuantity=String(availableQuantity);

            if(metricText){
                metricText.textContent=metricShortName
                    ?`Unidad: ${metricName} (${metricShortName})`
                    :`Unidad: ${metricName||"No disponible"}`;
            }

            if(stockText){
                stockText.textContent=formatNumber(availableQuantity);
            }

            if(quantityInput){
                quantityInput.max=String(availableQuantity);

                if(
                    normalizePositiveInteger(quantityInput.value)>
                    availableQuantity
                ){
                    quantityInput.value="";
                }
            }

            clearFieldValidation(select);
            updateQuantityStatus(row);
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function refreshProductOptions(){
            if(!exitProductList)return;

            const selects=Array.from(
                exitProductList.querySelectorAll(".exit-product-select")
            );

            const selectedValues=new Set(
                selects.map(function(select){
                    return select.value;
                }).filter(Boolean)
            );

            selects.forEach(function(select){
                Array.from(select.options).forEach(function(option){
                    if(!option.value){
                        option.disabled=false;
                        return;
                    }

                    option.disabled=
                        option.value!==select.value&&
                        selectedValues.has(option.value);
                });
            });

            if(btnAddExitProduct){
                btnAddExitProduct.disabled=
                    availableProducts.length===0||
                    selectedValues.size>=availableProducts.length;
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
        function getAvailableProductsForNewRow(){
            const selectedValues=new Set(
                Array.from(
                    exitProductList?.querySelectorAll(".exit-product-select")||[]
                ).map(function(select){
                    return select.value;
                }).filter(Boolean)
            );

            return availableProducts.filter(function(product){
                return !selectedValues.has(String(product.idProduct));
            });
        }

        /**
         * Retira o limpia la información indicada de la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function clearProductRows(){
            exitProductList?.replaceChildren();
            updateProductEmptyState();
            updateExitSummary();
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} customMessage valor de customMessage requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateProductEmptyState(customMessage=""){
            if(!exitProductEmpty||!exitProductList)return;

            const empty=exitProductList.children.length===0;
            exitProductEmpty.style.display=empty?"":"none";

            if(!empty)return;

            exitProductEmpty.replaceChildren();

            const icon=document.createElement("i");
            icon.className="bi bi-box-seam me-1";

            exitProductEmpty.append(
                icon,
                document.createTextNode(
                    customMessage||
                    "Agrega al menos un producto con existencia disponible."
                )
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateQuantityStatus(row){
            if(!row)return;

            const quantityInput=row.querySelector(".exit-product-quantity");
            const statusInput=row.querySelector(".exit-product-status");
            const selectedProduct=
                row.querySelector(".exit-product-select")?.value;

            const availableQuantity=
                normalizePositiveInteger(row.dataset.availableQuantity)||0;

            const requestedQuantity=
                normalizePositiveInteger(quantityInput?.value);

            if(!selectedProduct){
                setStatusField(statusInput,"Sin seleccionar","");
                return;
            }

            if(requestedQuantity===null){
                setStatusField(statusInput,"Captura cantidad","");
                return;
            }

            if(requestedQuantity>availableQuantity){
                setStatusField(
                    statusInput,
                    "Stock insuficiente",
                    "is-invalid"
                );

                markFieldInvalid(
                    quantityInput,
                    `Disponible: ${formatNumber(availableQuantity)}.`
                );
                return;
            }

            clearFieldValidation(quantityInput);

            setStatusField(
                statusInput,
                "Disponible",
                "is-valid"
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} field valor de field requerido por la función
         * @param {*} value valor que se transformará o validará
         * @param {*} className valor de className requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function setStatusField(field,value,className){
            if(!field)return;

            field.value=value;
            field.classList.remove("is-valid","is-invalid");

            if(className)field.classList.add(className);
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateExitSummary(){
            const rows=Array.from(
                exitProductList?.querySelectorAll(".exit-product-row")||[]
            );

            let productCount=0;
            let quantityTotal=0;

            rows.forEach(function(row){
                const productId=
                    normalizePositiveInteger(
                        row.querySelector(".exit-product-select")?.value
                    );

                const quantity=
                    normalizePositiveInteger(
                        row.querySelector(".exit-product-quantity")?.value
                    );

                if(productId!==null)productCount++;
                if(quantity!==null)quantityTotal+=quantity;
            });

            if(exitProductCount){
                exitProductCount.textContent=formatNumber(productCount);
            }

            if(exitQuantityTotal){
                exitQuantityTotal.textContent=formatNumber(quantityTotal);
            }
        }

        /* ======================================================
           REGISTRO RÁPIDO DE ÁREA
           ====================================================== */

        btnOpenQuickAreaExit?.addEventListener("click",function(){
            openQuickArea();
        });

        btnCloseQuickAreaExit?.addEventListener("click",function(){
            closeQuickArea();
        });

        btnCancelQuickAreaExit?.addEventListener("click",function(){
            closeQuickArea();
        });

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openQuickArea(){
            if(
                !modalCreateExit||
                !modalQuickAreaExit||
                !modalCreateExitElement
            ){
                return;
            }

            resetForm(formQuickAreaExit,true);
            quickAreaReturning=true;

            modalCreateExitElement.dataset.preserveFormState="true";

            modalCreateExitElement.addEventListener(
                "hidden.bs.modal",
                function(){
                    modalQuickAreaExit.show();

                    window.setTimeout(function(){
                        document.getElementById(
                            "exitQuickAreaShortName"
                        )?.focus();
                    },180);
                },
                {once:true}
            );

            modalCreateExit.hide();
        }

        /**
         * Oculta el componente visual y restablece su estado temporal.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function closeQuickArea(){
            modalQuickAreaExit?.hide();
        }

        modalQuickAreaExitElement?.addEventListener(
            "hidden.bs.modal",
            function(){
                if(!quickAreaReturning)return;

                modalCreateExit?.show();

                if(modalCreateExitElement){
                    delete modalCreateExitElement.dataset.preserveFormState;
                }

                quickAreaReturning=false;
            }
        );

        formQuickAreaExit?.addEventListener(
            "submit",
            async function(event){
                event.preventDefault();
                event.stopImmediatePropagation();

                if(
                    !validateForm(formQuickAreaExit)||
                    btnSaveQuickAreaExit?.disabled||
                    formQuickAreaExit.dataset.fetchSubmitting==="true"
                ){
                    return;
                }

                const areaShortName=
                    getValue("exitQuickAreaShortName").toUpperCase();

                const areaName=
                    getValue("exitQuickAreaName");

                formQuickAreaExit.dataset.fetchSubmitting="true";

                setFormLoading(
                    formQuickAreaExit,
                    btnSaveQuickAreaExit,
                    true,
                    "Guardando..."
                );

                try{
                    const result=
                        await Api.submitForm(formQuickAreaExit);

                    showToast(result);

                    if(!result.success)return;

                    const createdArea=
                        await reloadAreaSelects({
                            shortName:areaShortName,
                            name:areaName
                        });

                    if(!createdArea){
                        showWarning(
                            "El área se registró, pero no fue posible seleccionarla automáticamente."
                        );
                    }

                    resetForm(formQuickAreaExit,true);
                    modalQuickAreaExit?.hide();
                }catch(error){
                    handleRequestError(error);
                }finally{
                    delete formQuickAreaExit.dataset.fetchSubmitting;

                    setFormLoading(
                        formQuickAreaExit,
                        btnSaveQuickAreaExit,
                        false
                    );
                }
            }
        );

        /**
         * Ejecuta la operación reloadAreaSelects del módulo de interfaz.
         *
         * @param {*} newArea valor de newArea requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function reloadAreaSelects(newArea){
            const result=await Api.get("/areas/list");

            if(!result.success){
                throw new Api.ApiError(
                    result.message||
                    "No fue posible consultar las áreas.",
                    400,
                    result
                );
            }

            const areas=
                Array.isArray(result.data)
                    ?result.data.filter(function(area){
                        return Number(area.status)===1;
                    })
                    :[];

            const createdArea=
                areas.find(function(area){
                    return(
                        normalizeComparable(area.name)===
                        normalizeComparable(newArea.name)&&
                        normalizeComparable(area.shortName)===
                        normalizeComparable(newArea.shortName)
                    );
                })||null;

            updateAreaSelect(
                exitArea,
                areas,
                createdArea?.idArea
            );

            updateAreaFilter(
                exitAreaFilter,
                areas
            );

            return createdArea;
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} areas valor de areas requerido por la función
         * @param {*} selectedId valor de selectedId requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateAreaSelect(
            select,
            areas,
            selectedId=null
        ){
            if(!select)return;

            const previousValue=select.value;

            const finalSelectedValue=
                selectedId!=null
                    ?String(selectedId)
                    :previousValue;

            select.replaceChildren(
                createOption("","Seleccione un área")
            );

            areas.forEach(function(area){
                const name=normalizeText(area.name);
                const shortName=normalizeText(area.shortName);

                select.appendChild(
                    createOption(
                        area.idArea,
                        shortName
                            ?`${name} (${shortName})`
                            :name
                    )
                );
            });

            const exists=
                Array.from(select.options)
                    .some(function(option){
                        return option.value===finalSelectedValue;
                    });

            select.value=
                exists
                    ?finalSelectedValue
                    :"";

            if(selectedId!=null){
                clearFieldValidation(select);

                select.dispatchEvent(
                    new Event(
                        "change",
                        {bubbles:true}
                    )
                );
            }
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} areas valor de areas requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateAreaFilter(select,areas){
            if(!select)return;

            const previousValue=select.value;

            select.replaceChildren(
                createOption("all","Todas")
            );

            areas.forEach(function(area){
                const name=normalizeText(area.name);
                const shortName=normalizeText(area.shortName);

                select.appendChild(
                    createOption(
                        area.idArea,
                        shortName
                            ?`${name} (${shortName})`
                            :name
                    )
                );
            });

            const exists=
                Array.from(select.options)
                    .some(function(option){
                        return option.value===previousValue;
                    });

            select.value=
                exists
                    ?previousValue
                    :"all";
        }

        /* ======================================================
           CONFIRMACIÓN
           ====================================================== */

        btnOpenConfirmExit?.addEventListener(
            "click",
            function(){
                if(!validateExitForm())return;

                const selectedArea=
                    exitArea?.selectedOptions?.[0];

                const buyerName=
                    getValue("exitBuyerName");

                const rows=
                    Array.from(
                        exitProductList?.querySelectorAll(".exit-product-row")||[]
                    );

                const totalQuantity=
                    rows.reduce(function(sum,row){
                        return sum+(
                            normalizePositiveInteger(
                                row.querySelector(".exit-product-quantity")?.value
                            )||0
                        );
                    },0);

                setText(
                    "confirmExitArea",
                    selectedArea?.textContent?.trim()||"-"
                );

                setText(
                    "confirmExitBuyer",
                    buyerName||"-"
                );

                setText(
                    "confirmExitProducts",
                    String(rows.length)
                );

                setText(
                    "confirmExitQuantity",
                    formatNumber(totalQuantity)
                );

                modalConfirmExit?.show();
            }
        );

        btnConfirmExit?.addEventListener(
            "click",
            async function(){
                if(
                    !formCreateExit||
                    btnConfirmExit.disabled||
                    formCreateExit.dataset.fetchSubmitting==="true"
                ){
                    return;
                }

                if(!validateExitForm()){
                    modalConfirmExit?.hide();
                    return;
                }

                formCreateExit.dataset.fetchSubmitting="true";

                setFormLoading(
                    formCreateExit,
                    btnConfirmExit,
                    true,
                    "Registrando..."
                );

                try{
                    const result=
                        await Api.submitForm(formCreateExit);

                    showToast(result);

                    if(!result.success)return;

                    modalConfirmExit?.hide();
                    modalCreateExit?.hide();

                    resetExitForm();
                    await loadExits();
                }catch(error){
                    handleRequestError(error);
                }finally{
                    delete formCreateExit.dataset.fetchSubmitting;

                    setFormLoading(
                        formCreateExit,
                        btnConfirmExit,
                        false
                    );
                }
            }
        );

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function validateExitForm(){
            if(!formCreateExit)return false;

            let valid=validateForm(formCreateExit);

            const rows=Array.from(
                exitProductList?.querySelectorAll(".exit-product-row")||[]
            );

            if(rows.length===0){
                showWarning(
                    "Agrega al menos un producto a la salida."
                );
                return false;
            }

            const productIds=new Set();
            let firstInvalidField=null;

            rows.forEach(function(row){
                const select=
                    row.querySelector(".exit-product-select");

                const quantityInput=
                    row.querySelector(".exit-product-quantity");

                const productId=
                    normalizePositiveInteger(select?.value);

                const quantity=
                    normalizePositiveInteger(quantityInput?.value);

                const availableQuantity=
                    normalizePositiveInteger(
                        row.dataset.availableQuantity
                    )||0;

                if(productId===null){
                    markFieldInvalid(
                        select,
                        "Selecciona un producto."
                    );

                    firstInvalidField??=select;
                    valid=false;
                }else if(productIds.has(productId)){
                    markFieldInvalid(
                        select,
                        "Este producto ya fue agregado."
                    );

                    firstInvalidField??=select;
                    valid=false;
                }else{
                    productIds.add(productId);
                }

                if(quantity===null){
                    markFieldInvalid(
                        quantityInput,
                        "Captura una cantidad entera mayor que cero."
                    );

                    firstInvalidField??=quantityInput;
                    valid=false;
                }else if(quantity>availableQuantity){
                    markFieldInvalid(
                        quantityInput,
                        `Solo hay ${formatNumber(
                            availableQuantity
                        )} unidades disponibles.`
                    );

                    firstInvalidField??=quantityInput;
                    valid=false;
                }
            });

            if(!valid)firstInvalidField?.focus();

            return valid;
        }

        /**
         * Ejecuta la operación resetExitForm del módulo de interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function resetExitForm(){
            productsRequestId++;
            availableProducts=[];

            resetForm(formCreateExit,true);
            clearProductRows();

            if(btnAddExitProduct){
                btnAddExitProduct.disabled=true;
            }

            setProductsLoading(false);
            updateExitSummary();
        }

        /* ======================================================
           TABLA
           ====================================================== */

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadExits(){
            const result=await Api.get("/exits/list");

            if(!result.success){
                throw new Api.ApiError(
                    result.message||
                    "No fue posible consultar las salidas.",
                    400,
                    result
                );
            }

            renderExits(
                Array.isArray(result.data)
                    ?result.data
                    :[]
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} exits valor de exits requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderExits(exits){
            tableBody.replaceChildren();

            exits.forEach(function(exit){
                tableBody.appendChild(
                    createExitRow(exit)
                );
            });

            updateTableVisibility(exits.length);

            if(typeof window.filterTable==="function"){
                window.filterTable(TABLE_ID);
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} exit valor de exit requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createExitRow(exit){
            const id=
                normalizePositiveInteger(exit.idExit)||0;

            const folio=
                normalizeText(exit.folioNumber);

            const invoice=
                normalizeText(exit.invoiceNumber);

            const areaId=
                normalizePositiveInteger(exit.idArea)||0;

            const areaName=
                normalizeText(exit.areaName);

            const areaShortName=
                normalizeText(exit.areaShortName);

            const buyerName=
                normalizeText(exit.buyerName);

            const userName=
                normalizeText(exit.userName);

            const changeDate=
                normalizeText(exit.changeDate);

            const dateFilter=
                getDateFilterValue(changeDate);

            const total=
                normalizeNumber(exit.totalAllPrices);

            const products=
                Array.isArray(exit.products)
                    ?exit.products
                    :[];

            const productSearch=
                products.map(function(product){
                    return[
                        normalizeText(product.productCode),
                        normalizeText(product.productName),
                        normalizeText(product.metricName),
                        normalizeText(product.metricShortName)
                    ].join(" ");
                }).join(" ");

            const row=document.createElement("tr");
            row.className="js-table-row exit-table-row";
            row.dataset.id=String(id);
            row.dataset.folio=folio;
            row.dataset.invoice=invoice;
            row.dataset.area=String(areaId);
            row.dataset.areaName=areaName;
            row.dataset.areaShortName=areaShortName;
            row.dataset.buyerName=buyerName;
            row.dataset.userName=userName;
            row.dataset.date=dateFilter;
            row.dataset.dateTime=changeDate;
            row.dataset.total=total.toFixed(2);

            row.dataset.search=[
                folio,
                invoice,
                areaName,
                areaShortName,
                buyerName,
                userName,
                productSearch
            ].join(" ");

            const idCell=
                createCell(
                    String(id),
                    "table-cell-secondary table-cell-nowrap"
                );

            const folioCell=
                createCell(
                    folio,
                    "table-cell-primary table-cell-nowrap"
                );

            const dateCell=
                createCell(
                    formatDateTime(changeDate),
                    "table-cell-secondary table-cell-nowrap exit-date-cell"
                );

            dateCell.dataset.dateValue=changeDate;

            const invoiceCell=
                createCell(
                    invoice,
                    "table-cell-primary table-cell-nowrap"
                );

            const areaCell=document.createElement("td");
            const areaNameElement=document.createElement("span");
            const areaShortElement=document.createElement("span");

            areaNameElement.className="table-cell-primary";
            areaNameElement.textContent=areaName||"Sin área";

            areaShortElement.className=
                "table-cell-secondary d-block small";

            areaShortElement.textContent=
                areaShortName||"-";

            areaCell.append(
                areaNameElement,
                areaShortElement
            );

            const buyerCell=
                createCell(
                    buyerName||"-",
                    "table-cell-secondary"
                );

            const productsCell=
                document.createElement("td");

            const productsBadge=
                document.createElement("span");

            productsBadge.className=
                "table-badge table-badge-info";

            productsBadge.textContent=
                `${products.length} ${
                    products.length===1
                        ?"producto"
                        :"productos"
                }`;

            productsCell.appendChild(productsBadge);

            const totalCell=
                createCell(
                    formatCurrency(total),
                    "table-cell-primary table-cell-nowrap exit-money-cell"
                );

            totalCell.dataset.moneyValue=
                total.toFixed(2);

            const userCell=
                createCell(
                    userName||"Usuario no disponible",
                    "table-cell-secondary"
                );

            const actionsCell=
                document.createElement("td");

            const actionsContainer=
                document.createElement("div");

            const viewButton=
                document.createElement("button");

            const viewIcon=
                document.createElement("i");

            actionsContainer.className="table-actions";
            viewButton.type="button";

            viewButton.className=
                "table-action-btn table-action-view btn-view-exit";

            viewButton.title="Ver detalles";

            viewButton.setAttribute(
                "aria-label",
                "Ver detalles"
            );

            viewIcon.className="bi bi-eye";

            viewButton.appendChild(viewIcon);
            actionsContainer.appendChild(viewButton);

            actionsCell.append(
                actionsContainer,
                createExitProductsData(products)
            );

            row.append(
                idCell,
                folioCell,
                dateCell,
                invoiceCell,
                areaCell,
                buyerCell,
                productsCell,
                totalCell,
                userCell,
                actionsCell
            );

            return row;
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} products valor de products requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createExitProductsData(products){
            const container=
                document.createElement("div");

            container.className="exit-products-data";
            container.hidden=true;

            products.forEach(function(product){
                const productElement=
                    document.createElement("span");

                productElement.className="exit-product-data";

                productElement.dataset.idExitProduct=
                    String(
                        normalizePositiveInteger(
                            product.idExitProduct
                        )||0
                    );

                productElement.dataset.idProduct=
                    String(
                        normalizePositiveInteger(
                            product.idProduct
                        )||0
                    );

                productElement.dataset.productCode=
                    normalizeText(product.productCode);

                productElement.dataset.productName=
                    normalizeText(product.productName);

                productElement.dataset.metricName=
                    normalizeText(product.metricName);

                productElement.dataset.metricShortName=
                    normalizeText(product.metricShortName);

                productElement.dataset.quantity=
                    String(
                        normalizeNumber(product.quantity)
                    );

                productElement.dataset.unitPrice=
                    normalizeNumber(product.unitPrice)
                        .toFixed(4);

                productElement.dataset.totalPrice=
                    normalizeNumber(product.totalPrice)
                        .toFixed(2);

                const allocations=
                    Array.isArray(product.allocations)
                        ?product.allocations
                        :[];

                allocations.forEach(function(allocation){
                    const allocationElement=
                        document.createElement("span");

                    allocationElement.className=
                        "exit-allocation-data";

                    allocationElement.dataset.idExitAllocation=
                        String(
                            normalizePositiveInteger(
                                allocation.idExitAllocation
                            )||0
                        );

                    allocationElement.dataset.idEntryProduct=
                        String(
                            normalizePositiveInteger(
                                allocation.idEntryProduct
                            )||0
                        );

                    allocationElement.dataset.entryFolio=
                        normalizeText(allocation.entryFolio);

                    allocationElement.dataset.providerName=
                        normalizeText(allocation.providerName);

                    allocationElement.dataset.quantity=
                        String(
                            normalizeNumber(allocation.quantity)
                        );

                    allocationElement.dataset.unitCost=
                        normalizeNumber(allocation.unitCost)
                            .toFixed(2);

                    allocationElement.dataset.totalCost=
                        normalizeNumber(allocation.totalCost)
                            .toFixed(2);

                    productElement.appendChild(
                        allocationElement
                    );
                });

                container.appendChild(productElement);
            });

            return container;
        }

        /* ======================================================
           DETALLES
           ====================================================== */

        tableBody.addEventListener(
            "click",
            function(event){
                const button=
                    event.target.closest(".btn-view-exit");

                if(!button)return;

                const row=
                    button.closest(".exit-table-row");

                if(!row)return;

                event.preventDefault();
                openViewExitModal(row);
            }
        );

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
        function openViewExitModal(row){
            setText(
                "viewExitFolio",
                row.dataset.folio||"-"
            );

            setText(
                "viewExitDate",
                formatDateTime(
                    row.dataset.dateTime
                )
            );

            setText(
                "viewExitInvoice",
                row.dataset.invoice||"-"
            );

            const areaText=
                row.dataset.areaShortName
                    ?`${row.dataset.areaName} (${row.dataset.areaShortName})`
                    :row.dataset.areaName;

            setText(
                "viewExitArea",
                areaText||"-"
            );

            setText(
                "viewExitBuyer",
                row.dataset.buyerName||"-"
            );

            setText(
                "viewExitUser",
                row.dataset.userName||"-"
            );

            setText(
                "viewExitTotal",
                formatCurrency(
                    row.dataset.total
                )
            );

            renderExitProducts(
                readExitProductsFromRow(row)
            );

            modalViewExit?.show();
        }

        /**
         * Ejecuta la operación readExitProductsFromRow del módulo de interfaz.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function readExitProductsFromRow(row){
            return Array.from(
                row.querySelectorAll(".exit-product-data")
            ).map(function(element){

                const allocations=
                    Array.from(
                        element.querySelectorAll(".exit-allocation-data")
                    ).map(function(allocationElement){
                        return{
                            entryFolio:
                                normalizeText(
                                    allocationElement.dataset.entryFolio
                                ),
                            providerName:
                                normalizeText(
                                    allocationElement.dataset.providerName
                                ),
                            quantity:
                                normalizeNumber(
                                    allocationElement.dataset.quantity
                                ),
                            unitCost:
                                normalizeNumber(
                                    allocationElement.dataset.unitCost
                                ),
                            totalCost:
                                normalizeNumber(
                                    allocationElement.dataset.totalCost
                                )
                        };
                    });

                return{
                    productCode:
                        normalizeText(
                            element.dataset.productCode
                        ),
                    productName:
                        normalizeText(
                            element.dataset.productName
                        ),
                    metricName:
                        normalizeText(
                            element.dataset.metricName
                        ),
                    metricShortName:
                        normalizeText(
                            element.dataset.metricShortName
                        ),
                    quantity:
                        normalizeNumber(
                            element.dataset.quantity
                        ),
                    unitPrice:
                        normalizeNumber(
                            element.dataset.unitPrice
                        ),
                    totalPrice:
                        normalizeNumber(
                            element.dataset.totalPrice
                        ),
                    allocations
                };
            });
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} products valor de products requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderExitProducts(products){
            const container=
                document.getElementById("viewExitProducts");

            if(!container)return;

            container.replaceChildren();

            products.forEach(function(product){
                const item=document.createElement("div");
                const header=document.createElement("div");
                const information=document.createElement("div");
                const name=document.createElement("span");
                const metadata=document.createElement("span");
                const values=document.createElement("div");
                const unitPrice=document.createElement("span");
                const subtotal=document.createElement("span");

                item.className="exit-detail-product";
                header.className="exit-detail-product-header";
                name.className="exit-detail-product-name";
                metadata.className="exit-detail-product-meta";
                values.className="exit-detail-product-values";
                unitPrice.className="exit-detail-product-price";
                subtotal.className="exit-detail-product-subtotal";

                name.textContent=
                    product.productCode
                        ?`${product.productCode} — ${product.productName}`
                        :product.productName;

                const metric=
                    product.metricShortName
                        ?`${product.metricName} (${product.metricShortName})`
                        :product.metricName;

                metadata.textContent=
                    `${formatNumber(
                        product.quantity
                    )} ${metric||"unidades"}`;

                unitPrice.textContent=
                    `${formatCurrency(
                        product.unitPrice
                    )} costo promedio`;

                subtotal.textContent=
                    `Costo total: ${formatCurrency(
                        product.totalPrice
                    )}`;

                information.append(
                    name,
                    metadata
                );

                values.append(
                    unitPrice,
                    subtotal
                );

                header.append(
                    information,
                    values
                );

                item.appendChild(header);

                if(
                    Array.isArray(product.allocations)&&
                    product.allocations.length>0
                ){
                    item.appendChild(
                        createAllocationList(
                            product.allocations
                        )
                    );
                }

                container.appendChild(item);
            });
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} allocations valor de allocations requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createAllocationList(allocations){
            const list=document.createElement("div");
            list.className="exit-allocation-list";

            allocations.forEach(function(allocation){
                const item=document.createElement("div");
                const information=document.createElement("div");
                const provider=document.createElement("span");
                const folio=document.createElement("span");
                const values=document.createElement("div");

                item.className="exit-allocation-item";
                provider.className="exit-allocation-provider";
                folio.className="exit-allocation-folio";
                values.className="exit-allocation-values";

                provider.textContent=
                    allocation.providerName||
                    "Proveedor no disponible";

                folio.textContent=
                    allocation.entryFolio
                        ?`Lote ${allocation.entryFolio}`
                        :"Lote sin folio";

                values.textContent=
                    `${formatNumber(
                        allocation.quantity
                    )} × ${formatCurrency(
                        allocation.unitCost
                    )} = ${formatCurrency(
                        allocation.totalCost
                    )}`;

                information.append(
                    provider,
                    folio
                );

                item.append(
                    information,
                    values
                );

                list.appendChild(item);
            });

            return list;
        }

        /* ======================================================
           FORMATO Y VISIBILIDAD
           ====================================================== */

        /**
         * Convierte el valor al formato utilizado para su presentación.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatInitialTableValues(){
            document.querySelectorAll(
                ".exit-date-cell"
            ).forEach(function(cell){
                cell.textContent=
                    formatDateTime(
                        cell.dataset.dateValue||
                        cell.textContent
                    );
            });

            document.querySelectorAll(
                ".exit-money-cell"
            ).forEach(function(cell){
                cell.textContent=
                    formatCurrency(
                        cell.dataset.moneyValue||
                        cell.textContent
                    );
            });
        }

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
            const responsive=
                table.closest(".table-responsive");

            const pagination=
                document.querySelector(
                    `.table-pagination[data-table-target="${TABLE_ID}"]`
                );

            const generalEmptyState=
                document.getElementById(
                    "exitsGeneralEmptyState"
                );

            const filterEmptyState=
                document.getElementById(
                    "exitsFilterEmptyState"
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

        /* ======================================================
           FORMULARIOS Y HELPERS
           ====================================================== */

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
                Form.reset(form,{
                    resetValues:Boolean(resetValues),
                    unlock:true
                });

                return;
            }

            if(resetValues){
                HTMLFormElement.prototype.reset.call(form);
            }

            form.classList.remove("was-validated");

            form.querySelectorAll(
                ".is-valid,.is-invalid"
            ).forEach(clearFieldValidation);
        }

        /**
         * Retira o limpia la información indicada de la interfaz.
         *
         * @param {*} field valor de field requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function clearFieldValidation(field){
            if(!field)return;

            field.classList.remove(
                "is-valid",
                "is-invalid"
            );

            field.removeAttribute("aria-invalid");
        }

        /**
         * Ejecuta la operación markFieldInvalid del módulo de interfaz.
         *
         * @param {*} field valor de field requerido por la función
         * @param {*} message valor de message requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function markFieldInvalid(field,message){
            if(!field)return;

            field.classList.remove("is-valid");
            field.classList.add("is-invalid");

            field.setAttribute(
                "aria-invalid",
                "true"
            );

            const formField=
                field.closest(".form-field");

            const feedback=
                formField?.querySelector(
                    ".invalid-feedback"
                );

            if(feedback){
                feedback.textContent=message;
            }
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
        function setFormLoading(
            form,
            button,
            loading,
            loadingText
        ){
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

            if(button)button.disabled=loading;
        }

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
                    result.message||
                    "Operación realizada.",
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
            const element=
                document.getElementById(id);

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
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} value valor que se transformará o validará
         * @param {*} text valor de text requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createOption(value,text){
            const option=
                document.createElement("option");

            option.value=String(value);
            option.textContent=text;

            return option;
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
            const cell=
                document.createElement("td");

            cell.className=className;
            cell.textContent=text;

            return cell;
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
         * Ejecuta la operación normalizeComparable del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeComparable(value){
            return normalizeText(value)
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g,"")
                .toLowerCase();
        }

        /**
         * Ejecuta la operación normalizeNumber del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeNumber(value){
            const normalized=
                normalizeText(value)
                    .replace(",",".");

            const number=
                Number(normalized);

            return Number.isFinite(number)
                ?number
                :0;
        }

        /**
         * Ejecuta la operación normalizePositiveInteger del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizePositiveInteger(value){
            const normalized=
                normalizeText(value);

            if(!/^\d+$/.test(normalized)){
                return null;
            }

            const number=Number(normalized);

            return(
                Number.isSafeInteger(number)&&
                number>0
            )
                ?number
                :null;
        }

        /**
         * Convierte el valor al formato utilizado para su presentación.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatNumber(value){
            return new Intl.NumberFormat(
                "es-MX"
            ).format(
                normalizeNumber(value)
            );
        }

        /**
         * Convierte el valor al formato utilizado para su presentación.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatCurrency(value){
            return new Intl.NumberFormat(
                "es-MX",
                {
                    style:"currency",
                    currency:"MXN",
                    minimumFractionDigits:2,
                    maximumFractionDigits:2
                }
            ).format(
                normalizeNumber(value)
            );
        }

        /**
         * Convierte el valor al formato utilizado para su presentación.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function formatDateTime(value){
            if(!value)return"Fecha no disponible";

            const date=new Date(value);

            if(Number.isNaN(date.getTime())){
                return normalizeText(value)
                    .replace("T"," ");
            }

            return new Intl.DateTimeFormat(
                "es-MX",
                {
                    day:"2-digit",
                    month:"2-digit",
                    year:"numeric",
                    hour:"2-digit",
                    minute:"2-digit"
                }
            ).format(date);
        }

        /**
         * Obtiene el valor solicitado a partir del estado actual de la interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function getDateFilterValue(value){
            if(!value)return"";

            const text=normalizeText(value);

            if(/^\d{4}-\d{2}-\d{2}/.test(text)){
                return text.substring(0,10);
            }

            const date=new Date(value);

            if(Number.isNaN(date.getTime())){
                return"";
            }

            const year=date.getFullYear();

            const month=
                String(
                    date.getMonth()+1
                ).padStart(2,"0");

            const day=
                String(
                    date.getDate()
                ).padStart(2,"0");

            return`${year}-${month}-${day}`;
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
            const element=
                document.getElementById(id);

            if(element){
                element.textContent=
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
            const element=
                document.getElementById(id);

            return element
                ?normalizeText(element.value)
                :"";
        }
    });
})();
