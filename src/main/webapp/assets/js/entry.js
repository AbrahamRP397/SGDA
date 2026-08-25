/**
 * Módulo de entradas de almacén.
 * Administra partidas, proveedores, importes y el envío transaccional a EntryServlet.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
(function(){
    "use strict";

    if(window.entryModuleInitialized)return;
    window.entryModuleInitialized=true;

    document.addEventListener("DOMContentLoaded",function(){
        const TABLE_ID="entriesTable";
        const table=document.getElementById(TABLE_ID);
        const tableBody=table?.querySelector("tbody");
        const formCreateEntry=document.getElementById("formCreateEntry");
        const formQuickProviderEntry=document.getElementById("formQuickProviderEntry");
        const entryProvider=document.getElementById("entryProvider");
        const entryProductList=document.getElementById("entryProductList");
        const entryProductEmpty=document.getElementById("entryProductEmpty");
        const entryProductsLoading=document.getElementById("entryProductsLoading");
        const entryProviderHelp=document.getElementById("entryProviderHelp");
        const entryGrandTotal=document.getElementById("entryGrandTotal");
        const entryProductRowTemplate=document.getElementById("entryProductRowTemplate");
        const modalCreateEntryElement=document.getElementById("modalCreateEntry");
        const modalQuickProviderEntryElement=document.getElementById("modalQuickProviderEntry");
        const btnNewEntry=document.getElementById("btnNewEntry");
        const btnAddEntryProduct=document.getElementById("btnAddEntryProduct");
        const btnSaveEntry=document.getElementById("btnSaveEntry");
        const btnOpenQuickProviderEntry=document.getElementById("btnOpenQuickProviderEntry");
        const btnCloseQuickProviderEntry=document.getElementById("btnCloseQuickProviderEntry");
        const btnCancelQuickProviderEntry=document.getElementById("btnCancelQuickProviderEntry");
        const btnSaveQuickProviderEntry=document.getElementById("btnSaveQuickProviderEntry");
        const modalCreateEntry=getModal("modalCreateEntry");
        const modalViewEntry=getModal("modalViewEntry");
        const modalQuickProviderEntry=getModal("modalQuickProviderEntry");

        let providerProducts=[];
        let providerRequestId=0;
        let quickProviderReturning=false;

        if(!table||!tableBody){
            console.warn("No se encontró la tabla de entradas.");
            return;
        }

        if(!window.Api){
            console.error("api.js no está disponible.");
            return;
        }

        formatInitialTableValues();

        btnNewEntry?.addEventListener("click",function(){
            resetEntryForm();
            modalCreateEntry?.show();
            window.setTimeout(function(){
                document.getElementById("entryInvoiceNumber")?.focus();
            },250);
        });

        entryProvider?.addEventListener("change",async function(){
            await loadProviderProducts();
        });

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadProviderProducts(){
            const idProvider=normalizePositiveInteger(entryProvider?.value);
            providerRequestId++;
            const currentRequestId=providerRequestId;
            providerProducts=[];
            clearProductRows();
            updateGrandTotal();

            if(btnAddEntryProduct)btnAddEntryProduct.disabled=true;
            entryProviderHelp?.classList.toggle("is-visible",idProvider!==null);

            if(idProvider===null){
                setProductsLoading(false);
                updateProductEmptyState();
                return;
            }

            clearFieldValidation(entryProvider);
            setProductsLoading(true);

            try{
                const result=await Api.get("/products/by-provider",{idProvider});

                if(currentRequestId!==providerRequestId)return;

                if(!result.success){
                    throw new Api.ApiError(
                        result.message||"No fue posible consultar los productos del proveedor.",
                        400,
                        result
                    );
                }

                providerProducts=normalizeProviderProducts(result.data);

                if(providerProducts.length===0){
                    showWarning("El proveedor seleccionado no tiene productos activos asociados.");
                    if(btnAddEntryProduct)btnAddEntryProduct.disabled=true;
                    updateProductEmptyState("Este proveedor no tiene productos disponibles.");
                    return;
                }

                if(btnAddEntryProduct)btnAddEntryProduct.disabled=false;
                addProductRow();
            }catch(error){
                if(currentRequestId!==providerRequestId)return;
                providerProducts=[];
                if(btnAddEntryProduct)btnAddEntryProduct.disabled=true;
                updateProductEmptyState("No fue posible cargar los productos del proveedor.");
                handleRequestError(error);
            }finally{
                if(currentRequestId===providerRequestId)setProductsLoading(false);
            }
        }

        /**
         * Ejecuta la operación normalizeProviderProducts del módulo de interfaz.
         *
         * @param {*} data datos que serán procesados por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function normalizeProviderProducts(data){
            if(!Array.isArray(data))return[];

            return data.map(function(product){
                const relations=Array.isArray(product.providers)?product.providers:[];
                const relation=relations.find(function(item){
                    return Number(item.status)===1;
                })||relations[0];

                return{
                    idProduct:normalizePositiveInteger(product.idProduct),
                    idProductProvider:normalizePositiveInteger(relation?.idProductProvider),
                    code:normalizeText(product.code),
                    name:normalizeText(product.name),
                    metricName:normalizeText(product.metricName),
                    metricShortName:normalizeText(product.metricShortName),
                    purchasePrice:normalizeNumber(relation?.purchasePrice)
                };
            }).filter(function(product){
                return product.idProduct!==null&&product.idProductProvider!==null;
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
            entryProductsLoading?.classList.toggle("is-visible",loading);
            if(entryProvider)entryProvider.disabled=loading;
            if(btnOpenQuickProviderEntry)btnOpenQuickProviderEntry.disabled=loading;
        }

        btnAddEntryProduct?.addEventListener("click",function(){
            if(!normalizePositiveInteger(entryProvider?.value)){
                showWarning("Selecciona primero un proveedor.");
                entryProvider?.focus();
                return;
            }

            if(providerProducts.length===0){
                showWarning("El proveedor no tiene productos disponibles.");
                return;
            }

            if(getAvailableProducts().length===0){
                showWarning("Ya agregaste todos los productos disponibles de este proveedor.");
                return;
            }

            addProductRow();
        });

        entryProductList?.addEventListener("click",function(event){
            const removeButton=event.target.closest(".entry-product-remove");
            if(!removeButton)return;

            removeButton.closest(".entry-product-row")?.remove();
            refreshProductOptions();
            updateProductEmptyState();
            updateGrandTotal();
        });

        entryProductList?.addEventListener("change",function(event){
            const select=event.target.closest(".entry-product-select");
            if(!select)return;

            const row=select.closest(".entry-product-row");
            updateRowFromSelectedProduct(row);
            refreshProductOptions();
            updateRowSubtotal(row);
            updateGrandTotal();
        });

        entryProductList?.addEventListener("input",function(event){
            const field=event.target;

            if(!field.classList.contains("entry-product-quantity")&&!field.classList.contains("entry-product-price"))return;

            const row=field.closest(".entry-product-row");
            clearFieldValidation(field);
            updateRowSubtotal(row);
            updateGrandTotal();
        });

        /**
         * Ejecuta la operación addProductRow del módulo de interfaz.
         *
         * @param {*} productRelationId valor de productRelationId requerido por la función
         * @param {*} quantity valor de quantity requerido por la función
         * @param {*} unitPrice valor de unitPrice requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function addProductRow(productRelationId=null,quantity="",unitPrice=null){
            if(!entryProductList||!entryProductRowTemplate)return;

            const fragment=entryProductRowTemplate.content.cloneNode(true);
            const select=fragment.querySelector(".entry-product-select");
            const quantityInput=fragment.querySelector(".entry-product-quantity");
            const priceInput=fragment.querySelector(".entry-product-price");

            populateProductSelect(select,productRelationId);

            if(quantityInput&&quantity!=="")quantityInput.value=String(quantity);
            if(priceInput&&unitPrice!==null)priceInput.value=formatDecimalInput(unitPrice);

            entryProductList.appendChild(fragment);

            const insertedRow=entryProductList.lastElementChild;

            if(productRelationId!==null)updateRowFromSelectedProduct(insertedRow,false);

            updateProductEmptyState();
            refreshProductOptions();
            updateRowSubtotal(insertedRow);
            updateGrandTotal();

            if(productRelationId===null){
                insertedRow?.querySelector(".entry-product-select")?.focus();
            }
        }

        /**
         * Ejecuta la operación populateProductSelect del módulo de interfaz.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} selectedId valor de selectedId requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function populateProductSelect(select,selectedId=null){
            if(!select)return;

            select.replaceChildren(createOption("","Seleccione un producto"));

            providerProducts.forEach(function(product){
                const label=product.code?`${product.code} — ${product.name}`:product.name;
                const option=createOption(product.idProductProvider,label);
                option.dataset.productId=String(product.idProduct);
                option.dataset.productCode=product.code;
                option.dataset.productName=product.name;
                option.dataset.metricName=product.metricName;
                option.dataset.metricShortName=product.metricShortName;
                option.dataset.purchasePrice=String(product.purchasePrice);
                select.appendChild(option);
            });

            select.value=selectedId!==null?String(selectedId):"";
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} row valor de row requerido por la función
         * @param {*} replacePrice valor de replacePrice requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateRowFromSelectedProduct(row,replacePrice=true){
            if(!row)return;

            const select=row.querySelector(".entry-product-select");
            const priceInput=row.querySelector(".entry-product-price");
            const metricText=row.querySelector(".entry-product-metric");
            const selectedOption=select?.selectedOptions?.[0];

            if(!selectedOption||!selectedOption.value){
                if(metricText)metricText.textContent="Unidad: —";
                if(replacePrice&&priceInput)priceInput.value="";
                return;
            }

            const metricName=normalizeText(selectedOption.dataset.metricName);
            const metricShortName=normalizeText(selectedOption.dataset.metricShortName);
            const purchasePrice=normalizeNumber(selectedOption.dataset.purchasePrice);

            if(metricText){
                metricText.textContent=metricShortName
                    ?`Unidad: ${metricName} (${metricShortName})`
                    :`Unidad: ${metricName||"No disponible"}`;
            }

            if(replacePrice&&priceInput){
                priceInput.value=formatDecimalInput(purchasePrice);
                clearFieldValidation(priceInput);
            }

            clearFieldValidation(select);
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
            if(!entryProductList)return;

            const selects=Array.from(entryProductList.querySelectorAll(".entry-product-select"));
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

                    option.disabled=option.value!==select.value&&selectedValues.has(option.value);
                });
            });

            if(btnAddEntryProduct){
                btnAddEntryProduct.disabled=
                    providerProducts.length===0||
                    selectedValues.size>=providerProducts.length;
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
        function getAvailableProducts(){
            const selectedValues=new Set(
                Array.from(entryProductList?.querySelectorAll(".entry-product-select")||[])
                    .map(function(select){
                        return select.value;
                    })
                    .filter(Boolean)
            );

            return providerProducts.filter(function(product){
                return !selectedValues.has(String(product.idProductProvider));
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
            entryProductList?.replaceChildren();
            updateProductEmptyState();
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
            if(!entryProductEmpty||!entryProductList)return;

            const empty=entryProductList.children.length===0;
            entryProductEmpty.style.display=empty?"":"none";

            if(!empty)return;

            if(customMessage){
                entryProductEmpty.innerHTML="";
                const icon=document.createElement("i");
                icon.className="bi bi-box-seam me-1";
                entryProductEmpty.append(icon,document.createTextNode(customMessage));
                return;
            }

            entryProductEmpty.innerHTML='<i class="bi bi-box-seam me-1"></i>Selecciona un proveedor y agrega al menos un producto.';
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
        function updateRowSubtotal(row){
            if(!row)return;

            const quantityInput=row.querySelector(".entry-product-quantity");
            const priceInput=row.querySelector(".entry-product-price");
            const totalInput=row.querySelector(".entry-product-total-input");
            const quantity=normalizeNumber(quantityInput?.value);
            const unitPrice=normalizeNumber(priceInput?.value);
            const subtotal=quantity>0&&unitPrice>=0?quantity*unitPrice:0;

            row.dataset.subtotal=subtotal.toFixed(2);

            if(totalInput)totalInput.value=subtotal.toFixed(2);
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateGrandTotal(){
            const rows=Array.from(entryProductList?.querySelectorAll(".entry-product-row")||[]);
            const total=rows.reduce(function(sum,row){
                return sum+normalizeNumber(row.dataset.subtotal);
            },0);

            if(entryGrandTotal){
                entryGrandTotal.dataset.totalValue=total.toFixed(2);
                entryGrandTotal.textContent=formatCurrency(total);
            }
        }

        btnOpenQuickProviderEntry?.addEventListener("click",function(){
            openQuickProvider();
        });

        btnCloseQuickProviderEntry?.addEventListener("click",function(){
            closeQuickProvider();
        });

        btnCancelQuickProviderEntry?.addEventListener("click",function(){
            closeQuickProvider();
        });

        /**
         * Muestra el componente visual solicitado y prepara sus datos.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function openQuickProvider(){
            if(!modalCreateEntry||!modalQuickProviderEntry||!modalCreateEntryElement)return;

            resetForm(formQuickProviderEntry,true);
            quickProviderReturning=true;
            modalCreateEntryElement.dataset.preserveFormState="true";

            modalCreateEntryElement.addEventListener("hidden.bs.modal",function(){
                modalQuickProviderEntry.show();

                window.setTimeout(function(){
                    document.getElementById("entryQuickProviderName")?.focus();
                },180);
            },{once:true});

            modalCreateEntry.hide();
        }

        /**
         * Oculta el componente visual y restablece su estado temporal.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function closeQuickProvider(){
            modalQuickProviderEntry?.hide();
        }

        modalQuickProviderEntryElement?.addEventListener("hidden.bs.modal",function(){
            if(!quickProviderReturning)return;

            modalCreateEntry?.show();

            if(modalCreateEntryElement){
                delete modalCreateEntryElement.dataset.preserveFormState;
            }

            quickProviderReturning=false;
        });

        formQuickProviderEntry?.addEventListener("submit",async function(event){
            event.preventDefault();
            event.stopImmediatePropagation();

            if(
                !validateForm(formQuickProviderEntry)||
                btnSaveQuickProviderEntry?.disabled||
                formQuickProviderEntry.dataset.fetchSubmitting==="true"
            ){
                return;
            }

            const providerName=normalizeText(
                document.getElementById("entryQuickProviderName")?.value
            );

            const providerRfc=normalizeText(
                document.getElementById("entryQuickProviderRfc")?.value
            ).toUpperCase();

            formQuickProviderEntry.dataset.fetchSubmitting="true";
            setFormLoading(
                formQuickProviderEntry,
                btnSaveQuickProviderEntry,
                true,
                "Guardando..."
            );

            try{
                const result=await Api.submitForm(formQuickProviderEntry);
                showToast(result);

                if(!result.success)return;

                const createdProvider=await reloadProviderSelects({
                    name:providerName,
                    rfc:providerRfc
                });

                if(!createdProvider){
                    showWarning(
                        "El proveedor se registró, pero no fue posible identificarlo automáticamente."
                    );
                }

                resetForm(formQuickProviderEntry,true);
                modalQuickProviderEntry?.hide();
            }catch(error){
                handleRequestError(error);
            }finally{
                delete formQuickProviderEntry.dataset.fetchSubmitting;

                setFormLoading(
                    formQuickProviderEntry,
                    btnSaveQuickProviderEntry,
                    false
                );
            }
        });

        /**
         * Ejecuta la operación reloadProviderSelects del módulo de interfaz.
         *
         * @param {*} newProvider valor de newProvider requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function reloadProviderSelects(newProvider){
            const result=await Api.get("/providers/list");

            if(!result.success){
                throw new Api.ApiError(
                    result.message||"No fue posible consultar los proveedores.",
                    400,
                    result
                );
            }

            const providers=Array.isArray(result.data)
                ?result.data.filter(function(provider){
                    return Number(provider.status)===1;
                })
                :[];

            const createdProvider=providers.find(function(provider){
                return normalizeComparable(provider.name)===normalizeComparable(newProvider.name)
                    &&normalizeComparable(provider.rfc)===normalizeComparable(newProvider.rfc);
            })||null;

            const previousProvider=entryProvider?.value||"";
            const alreadyHasProducts=(entryProductList?.children.length||0)>0;

            updateProviderSelect(
                entryProvider,
                providers,
                previousProvider
            );

            updateProviderFilter(
                document.getElementById("entryProviderFilter"),
                providers
            );

            /*
             * Si ya había proveedor seleccionado o productos escritos,
             * NO cambiamos automáticamente de proveedor.
             */
            if(
                createdProvider&&
                !previousProvider&&
                !alreadyHasProducts&&
                entryProvider
            ){
                entryProvider.value=String(createdProvider.idProvider);
                clearFieldValidation(entryProvider);
                entryProvider.dispatchEvent(
                    new Event("change",{bubbles:true})
                );
            }

            return createdProvider;
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} providers valor de providers requerido por la función
         * @param {*} selectedValue valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateProviderSelect(select,providers,selectedValue=""){
            if(!select)return;

            select.replaceChildren(
                createOption("","Seleccione un proveedor")
            );

            providers.forEach(function(provider){
                const name=normalizeText(provider.name);
                const rfc=normalizeText(provider.rfc);
                const option=createOption(
                    provider.idProvider,
                    rfc?`${name} — ${rfc}`:name
                );

                option.dataset.providerName=name;
                option.dataset.providerRfc=rfc;
                select.appendChild(option);
            });

            const exists=Array.from(select.options).some(function(option){
                return option.value===String(selectedValue);
            });

            select.value=exists?String(selectedValue):"";
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} select valor de select requerido por la función
         * @param {*} providers valor de providers requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function updateProviderFilter(select,providers){
            if(!select)return;

            const previousValue=select.value;

            select.replaceChildren(
                createOption("all","Todos")
            );

            providers.forEach(function(provider){
                select.appendChild(
                    createOption(
                        provider.idProvider,
                        normalizeText(provider.name)
                    )
                );
            });

            const exists=Array.from(select.options).some(function(option){
                return option.value===previousValue;
            });

            select.value=exists?previousValue:"all";
        }

        formCreateEntry?.addEventListener("submit",async function(event){
            event.preventDefault();
            event.stopImmediatePropagation();

            if(
                formCreateEntry.dataset.fetchSubmitting==="true"||
                btnSaveEntry?.disabled
            ){
                return;
            }

            if(!validateEntryForm())return;

            formCreateEntry.dataset.fetchSubmitting="true";
            setFormLoading(
                formCreateEntry,
                btnSaveEntry,
                true,
                "Registrando..."
            );

            try{
                const result=await Api.submitForm(formCreateEntry);
                showToast(result);

                if(!result.success)return;

                modalCreateEntry?.hide();
                resetEntryForm();
                await loadEntries();
            }catch(error){
                handleRequestError(error);
            }finally{
                delete formCreateEntry.dataset.fetchSubmitting;

                setFormLoading(
                    formCreateEntry,
                    btnSaveEntry,
                    false
                );
            }
        });

        /**
         * Evalúa que los datos cumplan las reglas requeridas por la interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function validateEntryForm(){
            if(!formCreateEntry)return false;

            let valid=validateForm(formCreateEntry);
            const rows=Array.from(
                entryProductList?.querySelectorAll(".entry-product-row")||[]
            );

            if(rows.length===0){
                showWarning("Agrega al menos un producto a la entrada.");
                return false;
            }

            const relationIds=new Set();
            let firstInvalidField=null;

            rows.forEach(function(row){
                const select=row.querySelector(".entry-product-select");
                const quantityInput=row.querySelector(".entry-product-quantity");
                const priceInput=row.querySelector(".entry-product-price");
                const relationId=normalizePositiveInteger(select?.value);
                const quantity=normalizePositiveInteger(quantityInput?.value);
                const price=parseDecimal(priceInput?.value);

                if(relationId===null){
                    markFieldInvalid(select,"Selecciona un producto.");
                    firstInvalidField??=select;
                    valid=false;
                }else if(relationIds.has(relationId)){
                    markFieldInvalid(select,"Este producto ya fue agregado.");
                    firstInvalidField??=select;
                    valid=false;
                }else{
                    relationIds.add(relationId);
                }

                if(quantity===null||quantity>999999999){
                    markFieldInvalid(
                        quantityInput,
                        "Captura una cantidad entera mayor que cero."
                    );
                    firstInvalidField??=quantityInput;
                    valid=false;
                }

                if(price===null||price<0||price>9999999999.99){
                    markFieldInvalid(
                        priceInput,
                        "Captura un precio unitario válido."
                    );
                    firstInvalidField??=priceInput;
                    valid=false;
                }
            });

            if(!valid)firstInvalidField?.focus();
            return valid;
        }

        /**
         * Ejecuta la operación resetEntryForm del módulo de interfaz.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function resetEntryForm(){
            providerRequestId++;
            providerProducts=[];
            resetForm(formCreateEntry,true);
            clearProductRows();
            updateGrandTotal();

            if(entryProvider){
                entryProvider.disabled=false;
                entryProvider.value="";
            }

            if(btnAddEntryProduct){
                btnAddEntryProduct.disabled=true;
            }

            if(btnOpenQuickProviderEntry){
                btnOpenQuickProviderEntry.disabled=false;
            }

            entryProviderHelp?.classList.remove("is-visible");
            setProductsLoading(false);
        }

        /**
         * Carga la información requerida desde el servidor.
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        async function loadEntries(){
            const result=await Api.get("/entries/list");

            if(!result.success){
                throw new Api.ApiError(
                    result.message||"No fue posible consultar las entradas.",
                    400,
                    result
                );
            }

            renderEntries(
                Array.isArray(result.data)
                    ?result.data
                    :[]
            );
        }

        /**
         * Actualiza la representación visual o el estado asociado al componente.
         *
         * @param {*} entries valor de entries requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function renderEntries(entries){
            tableBody.replaceChildren();

            entries.forEach(function(entry){
                tableBody.appendChild(
                    createEntryRow(entry)
                );
            });

            updateTableVisibility(entries.length);

            if(typeof window.filterTable==="function"){
                window.filterTable(TABLE_ID);
            }
        }

        /**
         * Valida y envía la información capturada por el usuario.
         *
         * @param {*} entry valor de entry requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function createEntryRow(entry){
            const id=normalizePositiveInteger(entry.idEntry)||0;
            const folio=normalizeText(entry.folioNumber);
            const invoice=normalizeText(entry.invoiceNumber);
            const providerId=normalizePositiveInteger(entry.idProvider)||0;
            const providerName=normalizeText(entry.providerName);
            const providerRfc=normalizeText(entry.providerRfc);
            const userName=normalizeText(entry.userName);
            const changeDate=normalizeText(entry.changeDate);
            const dateFilter=getDateFilterValue(changeDate);
            const total=normalizeNumber(entry.totalAllPrices);
            const products=Array.isArray(entry.products)?entry.products:[];

            const productSearch=products.map(function(product){
                return[
                    normalizeText(product.productCode),
                    normalizeText(product.productName),
                    normalizeText(product.metricName),
                    normalizeText(product.metricShortName)
                ].join(" ");
            }).join(" ");

            const row=document.createElement("tr");
            row.className="js-table-row entry-table-row";
            row.dataset.id=String(id);
            row.dataset.folio=folio;
            row.dataset.invoice=invoice;
            row.dataset.provider=String(providerId);
            row.dataset.providerName=providerName;
            row.dataset.providerRfc=providerRfc;
            row.dataset.userName=userName;
            row.dataset.date=dateFilter;
            row.dataset.dateTime=changeDate;
            row.dataset.total=total.toFixed(2);
            row.dataset.search=[
                folio,
                invoice,
                providerName,
                providerRfc,
                userName,
                productSearch
            ].join(" ");

            const idCell=createCell(
                String(id),
                "table-cell-secondary table-cell-nowrap"
            );

            const folioCell=createCell(
                folio,
                "table-cell-primary table-cell-nowrap"
            );

            const dateCell=createCell(
                formatDateTime(changeDate),
                "table-cell-secondary table-cell-nowrap entry-date-cell"
            );

            dateCell.dataset.dateValue=changeDate;

            const invoiceCell=createCell(
                invoice,
                "table-cell-primary table-cell-nowrap"
            );

            const providerCell=document.createElement("td");
            const providerNameElement=document.createElement("span");
            const providerRfcElement=document.createElement("span");

            providerNameElement.className="table-cell-primary";
            providerNameElement.textContent=providerName||"Sin proveedor";

            providerRfcElement.className=
                "table-cell-secondary d-block small";

            providerRfcElement.textContent=
                providerRfc||"RFC no disponible";

            providerCell.append(
                providerNameElement,
                providerRfcElement
            );

            const productsCell=document.createElement("td");
            const productsBadge=document.createElement("span");

            productsBadge.className=
                "table-badge table-badge-info";

            productsBadge.textContent=
                `${products.length} ${products.length===1?"producto":"productos"}`;

            productsCell.appendChild(productsBadge);

            const totalCell=createCell(
                formatCurrency(total),
                "table-cell-primary table-cell-nowrap entry-money-cell"
            );

            totalCell.dataset.moneyValue=total.toFixed(2);

            const userCell=createCell(
                userName||"Usuario no disponible",
                "table-cell-secondary"
            );

            const actionsCell=document.createElement("td");
            const actionsContainer=document.createElement("div");
            const viewButton=document.createElement("button");
            const viewIcon=document.createElement("i");

            actionsContainer.className="table-actions";
            viewButton.type="button";
            viewButton.className=
                "table-action-btn table-action-view btn-view-entry";

            viewButton.title="Ver detalles";
            viewButton.setAttribute("aria-label","Ver detalles");
            viewIcon.className="bi bi-eye";

            viewButton.appendChild(viewIcon);
            actionsContainer.appendChild(viewButton);

            actionsCell.append(
                actionsContainer,
                createEntryProductsData(products)
            );

            row.append(
                idCell,
                folioCell,
                dateCell,
                invoiceCell,
                providerCell,
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
        function createEntryProductsData(products){
            const container=document.createElement("div");
            container.className="entry-products-data";
            container.hidden=true;

            products.forEach(function(product){
                const element=document.createElement("span");
                element.className="entry-product-data";

                element.dataset.idEntryProduct=
                    String(
                        normalizePositiveInteger(
                            product.idEntryProduct
                        )||0
                    );

                element.dataset.idProductProvider=
                    String(
                        normalizePositiveInteger(
                            product.idProductProvider
                        )||0
                    );

                element.dataset.productCode=
                    normalizeText(product.productCode);

                element.dataset.productName=
                    normalizeText(product.productName);

                element.dataset.metricName=
                    normalizeText(product.metricName);

                element.dataset.metricShortName=
                    normalizeText(product.metricShortName);

                element.dataset.quantity=
                    String(normalizeNumber(product.quantity));

                element.dataset.remainingQuantity=
                    String(
                        normalizeNumber(
                            product.remainingQuantity
                        )
                    );

                element.dataset.unitPrice=
                    normalizeNumber(product.unitPrice)
                        .toFixed(2);

                element.dataset.totalPrice=
                    normalizeNumber(product.totalPrice)
                        .toFixed(2);

                container.appendChild(element);
            });

            return container;
        }

        tableBody.addEventListener("click",function(event){
            const button=event.target.closest(".btn-view-entry");
            if(!button)return;

            const row=button.closest(".entry-table-row");
            if(!row)return;

            event.preventDefault();
            openViewEntryModal(row);
        });

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
        function openViewEntryModal(row){
            setText(
                "viewEntryFolio",
                row.dataset.folio||"-"
            );

            setText(
                "viewEntryDate",
                formatDateTime(
                    row.dataset.dateTime
                )
            );

            setText(
                "viewEntryInvoice",
                row.dataset.invoice||"-"
            );

            const providerText=row.dataset.providerRfc
                ?`${row.dataset.providerName} — ${row.dataset.providerRfc}`
                :row.dataset.providerName;

            setText(
                "viewEntryProvider",
                providerText||"-"
            );

            setText(
                "viewEntryUser",
                row.dataset.userName||"-"
            );

            setText(
                "viewEntryTotal",
                formatCurrency(
                    row.dataset.total
                )
            );

            renderEntryProducts(
                readEntryProductsFromRow(row)
            );

            modalViewEntry?.show();
        }

        /**
         * Ejecuta la operación readEntryProductsFromRow del módulo de interfaz.
         *
         * @param {*} row valor de row requerido por la función
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function readEntryProductsFromRow(row){
            return Array.from(
                row.querySelectorAll(
                    ".entry-product-data"
                )
            ).map(function(element){
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
                    remainingQuantity:
                        normalizeNumber(
                            element.dataset.remainingQuantity
                        ),
                    unitPrice:
                        normalizeNumber(
                            element.dataset.unitPrice
                        ),
                    totalPrice:
                        normalizeNumber(
                            element.dataset.totalPrice
                        )
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
        function renderEntryProducts(products){
            const container=
                document.getElementById(
                    "viewEntryProducts"
                );

            if(!container)return;

            container.replaceChildren();

            products.forEach(function(product){
                const item=document.createElement("div");
                const information=document.createElement("div");
                const name=document.createElement("span");
                const metadata=document.createElement("span");
                const values=document.createElement("div");
                const unitPrice=document.createElement("span");
                const subtotal=document.createElement("span");

                item.className="entry-detail-product";
                information.className="entry-detail-product-info";
                name.className="entry-detail-product-name";
                metadata.className="entry-detail-product-meta";
                values.className="entry-detail-product-values";
                unitPrice.className="entry-detail-product-price";
                subtotal.className="entry-detail-product-subtotal";

                name.textContent=product.productCode
                    ?`${product.productCode} — ${product.productName}`
                    :product.productName;

                const metric=product.metricShortName
                    ?`${product.metricName} (${product.metricShortName})`
                    :product.metricName;

                metadata.textContent=
                    `${formatNumber(product.quantity)} ${metric||"unidades"} · Disponible en lote: ${formatNumber(product.remainingQuantity)}`;

                unitPrice.textContent=
                    `${formatCurrency(product.unitPrice)} c/u`;

                subtotal.textContent=
                    `Subtotal: ${formatCurrency(product.totalPrice)}`;

                information.append(
                    name,
                    metadata
                );

                values.append(
                    unitPrice,
                    subtotal
                );

                item.append(
                    information,
                    values
                );

                container.appendChild(item);
            });
        }

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
                ".entry-date-cell"
            ).forEach(function(cell){
                cell.textContent=
                    formatDateTime(
                        cell.dataset.dateValue||
                        cell.textContent
                    );
            });

            document.querySelectorAll(
                ".entry-money-cell"
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
                    "entriesGeneralEmptyState"
                );

            const filterEmptyState=
                document.getElementById(
                    "entriesFilterEmptyState"
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
            ).forEach(
                clearFieldValidation
            );
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

            field.removeAttribute(
                "aria-invalid"
            );
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
            field.setAttribute("aria-invalid","true");

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
                if(
                    typeof Form.loading==="function"
                ){
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

            return bootstrap.Modal
                .getOrCreateInstance(element);
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
        function createCell(
            text,
            className=""
        ){
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
            const normalizedValue=
                normalizeText(value)
                    .replace(",",".");

            const number=
                Number(normalizedValue);

            return Number.isFinite(number)
                ?number
                :0;
        }

        /**
         * Ejecuta la operación parseDecimal del módulo de interfaz.
         *
         * @param {*} value valor que se transformará o validará
         * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        function parseDecimal(value){
            const normalizedValue=
                normalizeText(value)
                    .replace(",",".");

            if(normalizedValue==="")return null;

            const number=
                Number(normalizedValue);

            return Number.isFinite(number)
                ?number
                :null;
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
            const normalizedValue=
                normalizeText(value);

            if(!/^\d+$/.test(normalizedValue)){
                return null;
            }

            const number=
                Number(normalizedValue);

            return Number.isSafeInteger(number)&&number>0
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
        function formatDecimalInput(value){
            return normalizeNumber(value)
                .toFixed(2);
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

            const text=
                normalizeText(value);

            if(/^\d{4}-\d{2}-\d{2}/.test(text)){
                return text.substring(0,10);
            }

            const date=
                new Date(value);

            if(Number.isNaN(date.getTime())){
                return"";
            }

            const year=date.getFullYear();
            const month=String(
                date.getMonth()+1
            ).padStart(2,"0");

            const day=String(
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
    });
})();
