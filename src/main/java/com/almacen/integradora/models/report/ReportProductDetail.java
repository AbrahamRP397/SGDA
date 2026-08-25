package com.almacen.integradora.models.report;

import java.math.BigDecimal;

/** Proyección de producto utilizada en los reportes de movimientos.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ReportProductDetail {

    private long productId;
    private String productCode;
    private String productName;
    private String metricName;
    private long quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ReportProductDetail() {
        this.unitPrice=BigDecimal.ZERO;
        this.subtotal=BigDecimal.ZERO;
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param productId valor de productId requerido por la operación
     * @param productCode valor de productCode requerido por la operación
     * @param productName valor de productName requerido por la operación
     * @param metricName valor de metricName requerido por la operación
     * @param quantity valor de quantity requerido por la operación
     * @param unitPrice valor de unitPrice requerido por la operación
     * @param subtotal valor de subtotal requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ReportProductDetail(long productId,String productCode,String productName,String metricName,long quantity,BigDecimal unitPrice,BigDecimal subtotal) {
        this.productId=productId;
        this.productCode=productCode;
        this.productName=productName;
        this.metricName=metricName;
        this.quantity=quantity;
        this.unitPrice=unitPrice==null?BigDecimal.ZERO:unitPrice;
        this.subtotal=subtotal==null?BigDecimal.ZERO:subtotal;
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
    public long getProductId() {
        return productId;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param productId valor de productId requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProductId(long productId) {
        this.productId=productId;
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
        this.productCode=productCode;
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
        this.productName=productName;
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
        this.metricName=metricName;
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
    public long getQuantity() {
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
    public void setQuantity(long quantity) {
        this.quantity=quantity;
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
        this.unitPrice=unitPrice==null?BigDecimal.ZERO:unitPrice;
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
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param subtotal valor de subtotal requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal=subtotal==null?BigDecimal.ZERO:subtotal;
    }
}
