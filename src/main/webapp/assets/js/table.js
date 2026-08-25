/**
 * Utilidades compartidas para búsqueda, filtros y paginación de tablas.
 * Mantiene estado independiente por tabla para permitir varias instancias.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
const tablePaginationState={};

document.addEventListener("DOMContentLoaded",function(){
    initializePagination();
    configureFilterToggles();
    configureSearchInputs();
    configureTableFilters();
    configurePageSizeSelectors();
    configureClearButtons();
    configureModalActionButtons();
    initializeTables();
    configureResponsivePagination();
});

/**
 * Ejecuta la operación configureResponsivePagination del módulo de interfaz.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configureResponsivePagination(){
    let resizeTimer=null;
    window.addEventListener("resize",function(){
        window.clearTimeout(resizeTimer);
        resizeTimer=window.setTimeout(function(){
            Object.keys(tablePaginationState).forEach(function(tableId){
                if(document.getElementById(tableId)){
                    filterTable(tableId);
                }
            });
        },120);
    });
}

/**
 * Ejecuta la operación configureFilterToggles del módulo de interfaz.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configureFilterToggles(){
    document.querySelectorAll(".js-filter-toggle").forEach(function(button){
        const filterId=button.dataset.filterTarget;
        const filterPanel=document.getElementById(filterId);

        if(!filterPanel){
            console.warn(`No se encontró el panel de filtros: ${filterId}`);
            return;
        }

        const initiallyOpen=filterPanel.classList.contains("is-open");

        button.setAttribute(
            "aria-expanded",
            String(initiallyOpen)
        );

        button.classList.toggle(
            "is-pressed",
            initiallyOpen
        );

        const initialIcon=button.querySelector("i");

        if(initialIcon){
            initialIcon.classList.toggle(
                "bi-funnel",
                !initiallyOpen
            );

            initialIcon.classList.toggle(
                "bi-funnel-fill",
                initiallyOpen
            );
        }

        button.addEventListener(
            "click",
            function(){
                const isOpen=
                    filterPanel.classList.toggle(
                        "is-open"
                    );

                button.classList.toggle(
                    "is-pressed",
                    isOpen
                );

                button.setAttribute(
                    "aria-expanded",
                    String(isOpen)
                );

                const icon=
                    button.querySelector("i");

                if(icon){
                    icon.classList.toggle(
                        "bi-funnel",
                        !isOpen
                    );

                    icon.classList.toggle(
                        "bi-funnel-fill",
                        isOpen
                    );
                }
            }
        );
    });
}

/**
 * Ejecuta la operación configureSearchInputs del módulo de interfaz.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configureSearchInputs(){
    document.querySelectorAll(".js-table-search").forEach(function(searchInput){
        searchInput.addEventListener("input",function(){
            const tableId=searchInput.dataset.tableTarget;
            resetTablePage(tableId);
            filterTable(tableId);
        });
    });
}

/**
 * Ejecuta la operación configureTableFilters del módulo de interfaz.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configureTableFilters(){
    document.querySelectorAll(".js-table-filter").forEach(function(filter){
        filter.addEventListener("change",function(){
            const tableId=filter.dataset.tableTarget;
            resetTablePage(tableId);
            filterTable(tableId);
        });
    });
}

/**
 * Ejecuta la operación configureClearButtons del módulo de interfaz.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configureClearButtons(){
    document.querySelectorAll(".js-clear-filters").forEach(function(button){
        button.addEventListener("click",function(){
            const tableId=button.dataset.tableTarget;
            pressButtonTemporarily(button,180);
            clearTableFilters(tableId);
            resetTablePage(tableId);
            filterTable(tableId);
        });
    });
}

/**
 * Ejecuta la operación pressButtonTemporarily del módulo de interfaz.
 *
 * @param {*} button valor de button requerido por la función
 * @param {*} duration valor de duration requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function pressButtonTemporarily(button,duration=180){
    if(!button)return;
    button.classList.add("is-pressed");
    window.setTimeout(function(){
        button.classList.remove("is-pressed");
    },duration);
}

/**
 * Ejecuta la operación configureModalActionButtons del módulo de interfaz.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configureModalActionButtons(){
    const modalTriggerSelector=[
        ".table-action-btn[data-bs-target]",
        ".table-primary-btn[data-bs-target]",
        ".table-toolbar-btn[data-bs-target]"
    ].join(",");
    document.querySelectorAll(modalTriggerSelector).forEach(function(button){
        const modalSelector=button.getAttribute("data-bs-target");
        if(!modalSelector||!modalSelector.startsWith("#"))return;
        const modal=document.querySelector(modalSelector);
        if(!modal)return;
        modal.addEventListener("show.bs.modal",function(){
            clearPressedModalButtons(modal);
            button.classList.add("is-pressed");
            modal._tableTriggerButton=button;
        });
        modal.addEventListener("hidden.bs.modal",function(){
            const triggerButton=modal._tableTriggerButton;
            if(triggerButton){
                triggerButton.classList.remove("is-pressed");
            }
            modal._tableTriggerButton=null;
        });
    });
    configureManualModalButton("btnNuevoUsuario","modalCreate");
    configureManualModalButton("btnNuevoProducto","modalCreate");
    configureManualModalButton("btnNuevoProveedor","modalCreate");
    configureManualModalButton("btnNuevaMetrica","modalCreate");
    configureManualModalButton("btnNuevaArea","modalCreate");
}

/**
 * Ejecuta la operación configureManualModalButton del módulo de interfaz.
 *
 * @param {*} buttonId valor de buttonId requerido por la función
 * @param {*} modalId valor de modalId requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configureManualModalButton(buttonId,modalId){
    const button=document.getElementById(buttonId);
    const modal=document.getElementById(modalId);
    if(!button||!modal)return;
    modal.addEventListener("show.bs.modal",function(){
        button.classList.add("is-pressed");
        modal._tableTriggerButton=button;
    });
    modal.addEventListener("hidden.bs.modal",function(){
        button.classList.remove("is-pressed");
        modal._tableTriggerButton=null;
    });
}

/**
 * Retira o limpia la información indicada de la interfaz.
 *
 * @param {*} modal valor de modal requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function clearPressedModalButtons(modal){
    const currentButton=modal._tableTriggerButton;
    if(currentButton){
        currentButton.classList.remove("is-pressed");
    }
}

/**
 * Inicializa los eventos y el estado del módulo.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function initializePagination(){
    document.querySelectorAll(".table-pagination").forEach(function(pagination){
        const tableId=pagination.dataset.tableTarget;
        if(!tableId)return;
        const pageSizeSelect=document.querySelector(`.js-page-size[data-table-target="${tableId}"]`);
        const defaultPageSize=pageSizeSelect?Number.parseInt(pageSizeSelect.value,10):Number.parseInt(pagination.dataset.pageSize,10);
        tablePaginationState[tableId]={
            currentPage:1,
            pageSize:Number.isInteger(defaultPageSize)&&defaultPageSize>0?defaultPageSize:5
        };
        configurePaginationButtons(tableId);
    });
}

/**
 * Ejecuta la operación configurePageSizeSelectors del módulo de interfaz.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configurePageSizeSelectors(){
    document.querySelectorAll(".js-page-size").forEach(function(select){
        select.addEventListener("change",function(){
            const tableId=select.dataset.tableTarget;
            const state=tablePaginationState[tableId];
            if(!state)return;
            const newPageSize=Number.parseInt(select.value,10);
            if(Number.isInteger(newPageSize)&&newPageSize>0){
                state.pageSize=newPageSize;
                state.currentPage=1;
                filterTable(tableId);
            }
        });
    });
}

/**
 * Inicializa los eventos y el estado del módulo.
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function initializeTables(){
    const tableIds=new Set();
    document.querySelectorAll(".js-table-search,.table-pagination").forEach(function(element){
        const tableId=element.dataset.tableTarget;
        if(tableId)tableIds.add(tableId);
    });
    tableIds.forEach(function(tableId){
        filterTable(tableId);
    });
}

/**
 * Ejecuta la operación filterTable del módulo de interfaz.
 *
 * @param {*} tableId valor de tableId requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function filterTable(tableId){
    if(!tableId)return;
    const table=document.getElementById(tableId);
    if(!table){
        console.warn(`No se encontró la tabla: ${tableId}`);
        return;
    }
    const rows=Array.from(table.querySelectorAll("tbody .js-table-row,tbody .product-table-row"));
    const searchInput=document.querySelector(`.js-table-search[data-table-target="${tableId}"]`);
    const searchValue=normalizeText(searchInput?searchInput.value:"");
    const filters=document.querySelectorAll(`.js-table-filter[data-table-target="${tableId}"]`);
    const matchedRows=[];
    rows.forEach(function(row){
        const searchableText=normalizeText(row.dataset.search||row.textContent);
        const matchesSearch=searchValue===""||searchableText.includes(searchValue);
        let matchesFilters=true;
        filters.forEach(function(filter){
            if((filter.type==="radio"||filter.type==="checkbox")&&!filter.checked)return;
            const filterField=filter.dataset.filterField;
            const filterValue=normalizeText(filter.value);
            if(!filterField||filterValue===""||filterValue==="all")return;
            let rowValue="";
            if(filterField==="status"){
                rowValue=normalizeText(row.dataset.filterStatus||row.dataset.status||"");
                if(rowValue==="1")rowValue="active";
                if(rowValue==="0")rowValue="inactive";
            }else if(filterField==="metric"){
                rowValue=normalizeText(row.dataset.filterMetric||row.dataset.idMetric||row.dataset.metric||"");
            }else{
                rowValue=normalizeText(row.dataset[filterField]||"");
            }
            if(filter.type==="checkbox"){
                const checkedFilters=document.querySelectorAll(`.js-table-filter[type="checkbox"][data-table-target="${tableId}"][data-filter-field="${filterField}"]:checked`);
                const checkedValues=Array.from(checkedFilters).map(function(checkedFilter){
                    return normalizeText(checkedFilter.value);
                });
                if(checkedValues.length>0&&!checkedValues.includes(rowValue)){
                    matchesFilters=false;
                }
                return;
            }
            if(rowValue!==filterValue){
                matchesFilters=false;
            }
        });
        row.style.display="none";
        if(matchesSearch&&matchesFilters){
            matchedRows.push(row);
        }
    });
    updateEmptyState(table,matchedRows.length);
    paginateTable(tableId,matchedRows);
}

/**
 * Ejecuta la operación paginateTable del módulo de interfaz.
 *
 * @param {*} tableId valor de tableId requerido por la función
 * @param {*} matchedRows valor de matchedRows requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function paginateTable(tableId,matchedRows){
    const pagination=document.querySelector(`.table-pagination[data-table-target="${tableId}"]`);
    if(!pagination){
        matchedRows.forEach(function(row){
            row.style.display="";
        });
        return;
    }
    let state=tablePaginationState[tableId];
    if(!state){
        state={currentPage:1,pageSize:5};
        tablePaginationState[tableId]=state;
    }
    const totalRows=matchedRows.length;
    const totalPages=Math.max(1,Math.ceil(totalRows/state.pageSize));
    state.currentPage=Math.min(Math.max(state.currentPage,1),totalPages);
    const startIndex=(state.currentPage-1)*state.pageSize;
    const endIndex=Math.min(startIndex+state.pageSize,totalRows);
    matchedRows.forEach(function(row,index){
        row.style.display=index>=startIndex&&index<endIndex?"":"none";
    });
    updatePaginationInformation(pagination,totalRows,startIndex,endIndex);
    renderPageNumbers(tableId,pagination,totalPages);
    updatePaginationButtons(pagination,state.currentPage,totalPages);
    pagination.style.display=totalRows===0?"none":"grid";
}

/**
 * Ejecuta la operación configurePaginationButtons del módulo de interfaz.
 *
 * @param {*} tableId valor de tableId requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function configurePaginationButtons(tableId){
    const pagination=document.querySelector(`.table-pagination[data-table-target="${tableId}"]`);
    if(!pagination)return;
    const previousButton=pagination.querySelector(".js-page-previous");
    const nextButton=pagination.querySelector(".js-page-next");
    if(previousButton){
        previousButton.addEventListener("click",function(){
            const state=tablePaginationState[tableId];
            if(!state||state.currentPage<=1)return;
            state.currentPage--;
            filterTable(tableId);
        });
    }
    if(nextButton){
        nextButton.addEventListener("click",function(){
            const state=tablePaginationState[tableId];
            if(!state)return;
            const table=document.getElementById(tableId);
            if(!table)return;
            const totalRows=getMatchedRowsCount(tableId);
            const totalPages=Math.max(1,Math.ceil(totalRows/state.pageSize));
            if(state.currentPage>=totalPages)return;
            state.currentPage++;
            filterTable(tableId);
        });
    }
}

/**
 * Obtiene el valor solicitado a partir del estado actual de la interfaz.
 *
 * @param {*} tableId valor de tableId requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function getMatchedRowsCount(tableId){
    const table=document.getElementById(tableId);
    if(!table)return 0;
    const rows=Array.from(table.querySelectorAll("tbody .js-table-row,tbody .product-table-row"));
    const searchInput=document.querySelector(`.js-table-search[data-table-target="${tableId}"]`);
    const searchValue=normalizeText(searchInput?searchInput.value:"");
    const filters=document.querySelectorAll(`.js-table-filter[data-table-target="${tableId}"]`);
    return rows.filter(function(row){
        const searchableText=normalizeText(row.dataset.search||row.textContent);
        if(searchValue!==""&&!searchableText.includes(searchValue))return false;
        let valid=true;
        filters.forEach(function(filter){
            if(!valid)return;
            if((filter.type==="radio"||filter.type==="checkbox")&&!filter.checked)return;
            const filterField=filter.dataset.filterField;
            const filterValue=normalizeText(filter.value);
            if(!filterField||filterValue===""||filterValue==="all")return;
            let rowValue="";
            if(filterField==="status"){
                rowValue=normalizeText(row.dataset.filterStatus||row.dataset.status||"");
                if(rowValue==="1")rowValue="active";
                if(rowValue==="0")rowValue="inactive";
            }else if(filterField==="metric"){
                rowValue=normalizeText(row.dataset.filterMetric||row.dataset.idMetric||row.dataset.metric||"");
            }else{
                rowValue=normalizeText(row.dataset[filterField]||"");
            }
            if(filter.type==="checkbox"){
                const checkedFilters=document.querySelectorAll(`.js-table-filter[type="checkbox"][data-table-target="${tableId}"][data-filter-field="${filterField}"]:checked`);
                const checkedValues=Array.from(checkedFilters).map(function(item){
                    return normalizeText(item.value);
                });
                if(checkedValues.length>0&&!checkedValues.includes(rowValue)){
                    valid=false;
                }
            }else if(rowValue!==filterValue){
                valid=false;
            }
        });
        return valid;
    }).length;
}

/**
 * Actualiza la representación visual o el estado asociado al componente.
 *
 * @param {*} tableId valor de tableId requerido por la función
 * @param {*} pagination valor de pagination requerido por la función
 * @param {*} totalPages valor de totalPages requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function renderPageNumbers(tableId,pagination,totalPages){
    const container=pagination.querySelector(".js-page-numbers");
    const state=tablePaginationState[tableId];
    if(!container||!state)return;
    container.innerHTML="";
    const visiblePages=getVisiblePages(state.currentPage,totalPages);
    let previousPage=null;
    visiblePages.forEach(function(page){
        if(previousPage!==null&&page-previousPage>1){
            const ellipsis=document.createElement("span");
            ellipsis.className="table-pagination-ellipsis";
            ellipsis.textContent="…";
            container.appendChild(ellipsis);
        }
        const button=document.createElement("button");
        button.type="button";
        button.textContent=String(page);
        button.className="table-pagination-button";
        button.setAttribute("aria-label",`Ir a la página ${page}`);
        if(page===state.currentPage){
            button.classList.add("active","is-pressed");
            button.setAttribute("aria-current","page");
        }
        button.addEventListener("click",function(){
            state.currentPage=page;
            filterTable(tableId);
        });
        container.appendChild(button);
        previousPage=page;
    });
}

/**
 * Obtiene el valor solicitado a partir del estado actual de la interfaz.
 *
 * @param {*} currentPage valor de currentPage requerido por la función
 * @param {*} totalPages valor de totalPages requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function getVisiblePages(currentPage,totalPages){
    if(totalPages<=1)return[1];
    let radius=2;
    if(window.innerWidth<=380){
        radius=0;
    }else if(window.innerWidth<=576){
        radius=1;
    }
    const pages=new Set([1,totalPages,currentPage]);
    for(let page=currentPage-radius;page<=currentPage+radius;page++){
        if(page>=1&&page<=totalPages){
            pages.add(page);
        }
    }
    return Array.from(pages).sort(function(first,second){
        return first-second;
    });
}

/**
 * Actualiza la representación visual o el estado asociado al componente.
 *
 * @param {*} pagination valor de pagination requerido por la función
 * @param {*} totalRows valor de totalRows requerido por la función
 * @param {*} startIndex valor de startIndex requerido por la función
 * @param {*} endIndex valor de endIndex requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function updatePaginationInformation(pagination,totalRows,startIndex,endIndex){
    setPaginationText(pagination,".js-page-start",totalRows===0?0:startIndex+1);
    setPaginationText(pagination,".js-page-end",endIndex);
    setPaginationText(pagination,".js-page-total",totalRows);
}

/**
 * Actualiza la representación visual o el estado asociado al componente.
 *
 * @param {*} pagination valor de pagination requerido por la función
 * @param {*} selector valor de selector requerido por la función
 * @param {*} value valor que se transformará o validará
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function setPaginationText(pagination,selector,value){
    const element=pagination.querySelector(selector);
    if(element){
        element.textContent=String(value);
    }
}

/**
 * Actualiza la representación visual o el estado asociado al componente.
 *
 * @param {*} pagination valor de pagination requerido por la función
 * @param {*} currentPage valor de currentPage requerido por la función
 * @param {*} totalPages valor de totalPages requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function updatePaginationButtons(pagination,currentPage,totalPages){
    const previousButton=pagination.querySelector(".js-page-previous");
    const nextButton=pagination.querySelector(".js-page-next");
    if(previousButton){
        previousButton.disabled=currentPage<=1;
    }
    if(nextButton){
        nextButton.disabled=currentPage>=totalPages;
    }
}

/**
 * Ejecuta la operación resetTablePage del módulo de interfaz.
 *
 * @param {*} tableId valor de tableId requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function resetTablePage(tableId){
    const state=tablePaginationState[tableId];
    if(state){
        state.currentPage=1;
    }
}

/**
 * Actualiza la representación visual o el estado asociado al componente.
 *
 * @param {*} table valor de table requerido por la función
 * @param {*} visibleRows valor de visibleRows requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function updateEmptyState(table,visibleRows){
    const panel=table.closest(".table-panel");
    if(!panel)return;
    const filterEmptyState=panel.querySelector(".js-filter-empty-state");
    const responsiveContainer=table.closest(".table-responsive");
    const pagination=panel.querySelector(`.table-pagination[data-table-target="${table.id}"]`);
    const hasRows=visibleRows>0;
    if(filterEmptyState){
        filterEmptyState.style.display=hasRows?"none":"block";
    }
    if(responsiveContainer){
        responsiveContainer.style.display=hasRows?"block":"none";
    }
    if(pagination){
        pagination.style.display=hasRows?"grid":"none";
    }
}

/**
 * Retira o limpia la información indicada de la interfaz.
 *
 * @param {*} tableId valor de tableId requerido por la función
 * @returns {*} resultado producido por la función, cuando corresponda
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
function clearTableFilters(tableId){
    const searchInput=document.querySelector(`.js-table-search[data-table-target="${tableId}"]`);
    if(searchInput){
        searchInput.value="";
    }
    const filters=document.querySelectorAll(`.js-table-filter[data-table-target="${tableId}"]`);
    filters.forEach(function(filter){
        if(filter.type==="radio"){
            filter.checked=normalizeText(filter.value)==="all";
        }else if(filter.type==="checkbox"){
            filter.checked=false;
        }else if(filter.tagName==="SELECT"){
            filter.selectedIndex=0;
        }else{
            filter.value="";
        }
    });
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
    return String(value||"")
        .trim()
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g,"");
}
