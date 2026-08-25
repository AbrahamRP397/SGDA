package com.almacen.integradora.models.exit;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Asignación de una cantidad de salida contra un lote de entrada específico.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ExitAllocation {
    private Long idExitAllocation;
    private Long idExitProduct;
    private Long idEntryProduct;
    private Long idEntry;
    private Long idProductProvider;
    private Long idStock;
    private Long idProvider;
    private String entryFolio;
    private String providerName;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ExitAllocation() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idExitAllocation identificador del registro relacionado con la operación
     * @param idExitProduct identificador del registro relacionado con la operación
     * @param idEntryProduct identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
     * @param unitCost valor de unitCost requerido por la operación
     * @param totalCost valor de totalCost requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ExitAllocation(Long idExitAllocation, Long idExitProduct,
                          Long idEntryProduct, Integer quantity,
                          BigDecimal unitCost, BigDecimal totalCost) {
        this.idExitAllocation = idExitAllocation;
        this.idExitProduct = idExitProduct;
        this.idEntryProduct = idEntryProduct;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Long getIdExitAllocation() {
        return idExitAllocation;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idExitAllocation identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdExitAllocation(Long idExitAllocation) {
        this.idExitAllocation = idExitAllocation;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Long getIdExitProduct() {
        return idExitProduct;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idExitProduct identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdExitProduct(Long idExitProduct) {
        this.idExitProduct = idExitProduct;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Long getIdEntryProduct() {
        return idEntryProduct;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idEntryProduct identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdEntryProduct(Long idEntryProduct) {
        this.idEntryProduct = idEntryProduct;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Long getIdStock() {
        return idStock;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idStock identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdStock(Long idStock) {
        this.idStock = idStock;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Long getIdEntry() {
        return idEntry;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idEntry identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdEntry(Long idEntry) {
        this.idEntry = idEntry;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Long getIdProductProvider() {
        return idProductProvider;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idProductProvider identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdProductProvider(Long idProductProvider) {
        this.idProductProvider = idProductProvider;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Long getIdProvider() {
        return idProvider;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idProvider identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdProvider(Long idProvider) {
        this.idProvider = idProvider;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public String getEntryFolio() {
        return entryFolio;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param entryFolio valor de entryFolio requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setEntryFolio(String entryFolio) {
        this.entryFolio = entryFolio;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param providerName valor de providerName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param quantity valor de quantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public BigDecimal getUnitCost() {
        return unitCost;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param unitCost valor de unitCost requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param totalCost valor de totalCost requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public BigDecimal calculateTotalCost() {
        if (quantity == null || quantity <= 0 || unitCost == null) {
            return BigDecimal.ZERO;
        }

        return unitCost
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public String toString() {
        return "ExitAllocation{" +
                "idExitAllocation=" + idExitAllocation +
                ", idExitProduct=" + idExitProduct +
                ", idEntryProduct=" + idEntryProduct +
                ", idEntry=" + idEntry +
                ", idProductProvider=" + idProductProvider +
                ", idStock=" + idStock +
                ", idProvider=" + idProvider +
                ", entryFolio='" + entryFolio + '\'' +
                ", providerName='" + providerName + '\'' +
                ", quantity=" + quantity +
                ", unitCost=" + unitCost +
                ", totalCost=" + totalCost +
                '}';
    }
}
