package com.almacen.integradora.models.exit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Detalle de producto incluido en una salida de almacén.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ExitProduct {
    private Long idExitProduct;
    private Long idExit;
    private Long idProduct;
    private String productCode;
    private String productName;
    private Long idMetric;
    private String metricName;
    private String metricShortName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private List<ExitAllocation> allocations = new ArrayList<>();

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ExitProduct() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idExitProduct identificador del registro relacionado con la operación
     * @param idExit identificador del registro relacionado con la operación
     * @param idProduct identificador del registro relacionado con la operación
     * @param quantity valor de quantity requerido por la operación
     * @param unitPrice valor de unitPrice requerido por la operación
     * @param totalPrice valor de totalPrice requerido por la operación
     * @param allocations valor de allocations requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ExitProduct(Long idExitProduct, Long idExit, Long idProduct,
                       Integer quantity, BigDecimal unitPrice,
                       BigDecimal totalPrice,
                       List<ExitAllocation> allocations) {
        this.idExitProduct = idExitProduct;
        this.idExit = idExit;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        setAllocations(allocations);
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
    public Long getIdExit() {
        return idExit;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idExit identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdExit(Long idExit) {
        this.idExit = idExit;
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
     * Obtiene todos los registros disponibles para esta consulta.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<ExitAllocation> getAllocations() {
        return allocations;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param allocations valor de allocations requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setAllocations(List<ExitAllocation> allocations) {
        this.allocations = allocations == null
                ? new ArrayList<>()
                : new ArrayList<>(allocations);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param allocation valor de allocation requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void addAllocation(ExitAllocation allocation) {
        if (allocation != null) {
            allocations.add(allocation);
        }
    }

    /**
     * Obtiene todos los registros disponibles para esta consulta.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public int getAllocationCount() {
        return allocations == null ? 0 : allocations.size();
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
    public int calculateAllocatedQuantity() {
        if (allocations == null || allocations.isEmpty()) {
            return 0;
        }

        return allocations.stream()
                .map(ExitAllocation::getQuantity)
                .filter(value -> value != null)
                .mapToInt(Integer::intValue)
                .sum();
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
    public BigDecimal calculateAllocationCost() {
        if (allocations == null || allocations.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return allocations.stream()
                .map(ExitAllocation::calculateTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
        return "ExitProduct{" +
                "idExitProduct=" + idExitProduct +
                ", idExit=" + idExit +
                ", idProduct=" + idProduct +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", idMetric=" + idMetric +
                ", metricName='" + metricName + '\'' +
                ", metricShortName='" + metricShortName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", allocations=" + allocations +
                '}';
    }
}
