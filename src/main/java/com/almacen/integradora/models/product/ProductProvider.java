package com.almacen.integradora.models.product;

import java.math.BigDecimal;

/** Relación comercial entre un producto y un proveedor, incluido precio y estado.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ProductProvider {

    private Long idProductProvider;
    private Long idProduct;
    private Long idProvider;
    private BigDecimal purchasePrice;

    /*
     * Estado de la relación product_providers.
     *
     * 1 = el proveedor está actualmente asociado al producto.
     * 0 = relación histórica/inactiva.
     */
    private Integer status;

    /*
     * Estado global del proveedor.
     *
     * Es diferente del estado de la relación.
     *
     * Ejemplo:
     *
     * relation.status = 1
     * providerStatus = 0
     *
     * significa que la relación sigue registrada como activa,
     * pero el proveedor fue desactivado globalmente.
     */
    private Integer providerStatus;

    private String providerName;
    private String providerRfc;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ProductProvider() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idProductProvider identificador del registro relacionado con la operación
     * @param idProduct identificador del registro relacionado con la operación
     * @param idProvider identificador del registro relacionado con la operación
     * @param purchasePrice valor de purchasePrice requerido por la operación
     * @param status estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ProductProvider(
            Long idProductProvider,
            Long idProduct,
            Long idProvider,
            BigDecimal purchasePrice,
            Integer status
    ) {
        this.idProductProvider = idProductProvider;
        this.idProduct = idProduct;
        this.idProvider = idProvider;
        this.purchasePrice = purchasePrice;
        this.status = status;
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
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Integer getStatus() {
        return status;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param status estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setStatus(Integer status) {
        this.status = status;
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
     * Evalúa la condición indicada para el estado actual.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean isRelationActive() {
        return Integer.valueOf(1).equals(status);
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
    public boolean isOperational() {
        return isRelationActive()
                && isProviderActive();
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
        return "ProductProvider{" +
                "idProductProvider=" + idProductProvider +
                ", idProduct=" + idProduct +
                ", idProvider=" + idProvider +
                ", purchasePrice=" + purchasePrice +
                ", status=" + status +
                ", providerStatus=" + providerStatus +
                ", providerName='" + providerName + '\'' +
                ", providerRfc='" + providerRfc + '\'' +
                '}';
    }
}
