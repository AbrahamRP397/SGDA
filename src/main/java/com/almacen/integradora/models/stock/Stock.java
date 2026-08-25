package com.almacen.integradora.models.stock;

import java.math.BigDecimal;

/** Proyección de existencias con información de producto, métrica y proveedor.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class Stock {

    private Long idStock;
    private Long idProductProvider;
    private Integer quantity;

    /* PRODUCTO */
    private Long idProduct;
    private String productCode;
    private String productName;
    private Integer productStatus;

    /* MÉTRICA */
    private Long idMetric;
    private String metricName;
    private String metricShortName;
    private Integer metricStatus;

    /* PROVEEDOR */
    private Long idProvider;
    private String providerName;
    private String providerRfc;
    private Integer providerStatus;

    /* RELACIÓN PRODUCTO-PROVEEDOR */
    private Integer relationStatus;

    private BigDecimal purchasePrice;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Stock() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idStock identificador del registro relacionado con la operación
     * @param idProductProvider identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Stock(
            Long idStock,
            Long idProductProvider,
            Integer quantity
    ) {
        this.idStock = idStock;
        this.idProductProvider = idProductProvider;
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
    public Integer getProductStatus() {
        return productStatus;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param productStatus estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
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
    public Integer getMetricStatus() {
        return metricStatus;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param metricStatus estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setMetricStatus(Integer metricStatus) {
        this.metricStatus = metricStatus;
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
    public String getProviderRfc() {
        return providerRfc;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param providerRfc valor de providerRfc requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProviderRfc(String providerRfc) {
        this.providerRfc = providerRfc;
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
    public Integer getProviderStatus() {
        return providerStatus;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param providerStatus estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProviderStatus(Integer providerStatus) {
        this.providerStatus = providerStatus;
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
    public Integer getRelationStatus() {
        return relationStatus;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param relationStatus estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setRelationStatus(Integer relationStatus) {
        this.relationStatus = relationStatus;
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
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param purchasePrice valor de purchasePrice requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
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
    public boolean hasStock() {
        return quantity != null && quantity > 0;
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
    public boolean isProductActive() {
        return Integer.valueOf(1).equals(productStatus);
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
    public boolean isMetricActive() {
        return Integer.valueOf(1).equals(metricStatus);
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
    public boolean isProviderActive() {
        return Integer.valueOf(1).equals(providerStatus);
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
    public boolean isRelationActive() {
        return Integer.valueOf(1).equals(relationStatus);
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
    public boolean isOperationalForEntry() {
        return isProductActive()
                && isMetricActive()
                && isProviderActive()
                && isRelationActive();
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
    public boolean isAvailableForExit() {
        return hasStock();
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
        return "Stock{" +
                "idStock=" + idStock +
                ", idProductProvider=" + idProductProvider +
                ", quantity=" + quantity +
                ", idProduct=" + idProduct +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", productStatus=" + productStatus +
                ", idMetric=" + idMetric +
                ", metricName='" + metricName + '\'' +
                ", metricShortName='" + metricShortName + '\'' +
                ", metricStatus=" + metricStatus +
                ", idProvider=" + idProvider +
                ", providerName='" + providerName + '\'' +
                ", providerRfc='" + providerRfc + '\'' +
                ", providerStatus=" + providerStatus +
                ", relationStatus=" + relationStatus +
                ", purchasePrice=" + purchasePrice +
                '}';
    }
}
