package com.almacen.integradora.models.dashboard;

/** Proyección de producto utilizada por los indicadores del tablero.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class DashboardProduct {

    private Long idProduct;
    private String productCode;
    private String productName;
    private Integer productStatus;

    private String metricName;
    private String metricShortName;

    private Long entryQuantity;
    private Long exitQuantity;
    private Long totalMovement;
    private Long stockQuantity;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public DashboardProduct() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idProduct identificador del registro relacionado con la operación
     * @param productCode valor de productCode requerido por la operación
     * @param productName valor de productName requerido por la operación
     * @param productStatus estado que se utilizará en la operación
     * @param metricName valor de metricName requerido por la operación
     * @param metricShortName valor de metricShortName requerido por la operación
     * @param entryQuantity valor de entryQuantity requerido por la operación
     * @param exitQuantity valor de exitQuantity requerido por la operación
     * @param totalMovement valor de totalMovement requerido por la operación
     * @param stockQuantity valor de stockQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public DashboardProduct(
            Long idProduct,
            String productCode,
            String productName,
            Integer productStatus,
            String metricName,
            String metricShortName,
            Long entryQuantity,
            Long exitQuantity,
            Long totalMovement,
            Long stockQuantity
    ) {
        this.idProduct = idProduct;
        this.productCode = productCode;
        this.productName = productName;
        this.productStatus = productStatus;
        this.metricName = metricName;
        this.metricShortName = metricShortName;
        this.entryQuantity = entryQuantity;
        this.exitQuantity = exitQuantity;
        this.totalMovement = totalMovement;
        this.stockQuantity = stockQuantity;
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
    public Long getEntryQuantity() {
        return entryQuantity;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param entryQuantity valor de entryQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setEntryQuantity(Long entryQuantity) {
        this.entryQuantity = entryQuantity;
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
    public Long getExitQuantity() {
        return exitQuantity;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param exitQuantity valor de exitQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setExitQuantity(Long exitQuantity) {
        this.exitQuantity = exitQuantity;
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
    public Long getTotalMovement() {
        return totalMovement;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param totalMovement valor de totalMovement requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTotalMovement(Long totalMovement) {
        this.totalMovement = totalMovement;
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
    public Long getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param stockQuantity valor de stockQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
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
    public boolean isActive() {
        return Integer.valueOf(1).equals(productStatus);
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
        return "DashboardProduct{" +
                "idProduct=" + idProduct +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", productStatus=" + productStatus +
                ", metricName='" + metricName + '\'' +
                ", metricShortName='" + metricShortName + '\'' +
                ", entryQuantity=" + entryQuantity +
                ", exitQuantity=" + exitQuantity +
                ", totalMovement=" + totalMovement +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}
