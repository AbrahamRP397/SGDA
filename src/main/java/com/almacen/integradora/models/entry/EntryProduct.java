package com.almacen.integradora.models.entry;

import java.math.BigDecimal;

/** Detalle o lote de producto incluido en una entrada de almacén.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class EntryProduct {

    private Long idEntryProduct;
    private Long idEntry;
    private Long idProductProvider;
    private Long idStock;
    private Long idProduct;
    private Long idProvider;

    private String productCode;
    private String productName;

    private Long idMetric;
    private String metricName;
    private String metricShortName;

    private String providerName;

    private Integer quantity;
    private Integer remainingQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public EntryProduct() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idEntryProduct identificador del registro relacionado con la operación
     * @param idEntry identificador del registro relacionado con la operación
     * @param idProductProvider identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
     * @param remainingQuantity valor de remainingQuantity requerido por la operación
     * @param unitPrice valor de unitPrice requerido por la operación
     * @param totalPrice valor de totalPrice requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public EntryProduct(
            Long idEntryProduct,
            Long idEntry,
            Long idProductProvider,
            Integer quantity,
            Integer remainingQuantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {
        this.idEntryProduct = idEntryProduct;
        this.idEntry = idEntry;
        this.idProductProvider = idProductProvider;
        this.quantity = quantity;
        this.remainingQuantity = remainingQuantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
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
    public Long getIdProduct() {
        return idProduct;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idProduct identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
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
    public String getProductCode() {
        return productCode;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param productCode valor de productCode requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
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
    public String getProductName() {
        return productName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param productName valor de productName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProductName(String productName) {
        this.productName = productName;
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
    public Long getIdMetric() {
        return idMetric;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idMetric identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdMetric(Long idMetric) {
        this.idMetric = idMetric;
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
    public String getMetricName() {
        return metricName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param metricName valor de metricName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setMetricName(String metricName) {
        this.metricName = metricName;
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
    public String getMetricShortName() {
        return metricShortName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param metricShortName valor de metricShortName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setMetricShortName(String metricShortName) {
        this.metricShortName = metricShortName;
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
    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param remainingQuantity valor de remainingQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
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
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param unitPrice valor de unitPrice requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param totalPrice valor de totalPrice requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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
    public BigDecimal calculateTotal() {
        if (quantity == null
                || quantity <= 0
                || unitPrice == null) {
            return BigDecimal.ZERO;
        }

        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean hasAvailableQuantity() {
        return remainingQuantity != null
                && remainingQuantity > 0;
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
        return "EntryProduct{" +
                "idEntryProduct=" + idEntryProduct +
                ", idEntry=" + idEntry +
                ", idProductProvider=" + idProductProvider +
                ", idStock=" + idStock +
                ", idProduct=" + idProduct +
                ", idProvider=" + idProvider +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", idMetric=" + idMetric +
                ", metricName='" + metricName + '\'' +
                ", metricShortName='" + metricShortName + '\'' +
                ", providerName='" + providerName + '\'' +
                ", quantity=" + quantity +
                ", remainingQuantity=" + remainingQuantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
