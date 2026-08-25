package com.almacen.integradora.models.product;

import java.util.ArrayList;
import java.util.List;

/** Entidad de producto con su métrica y proveedores asociados.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class Product {
    private Long idProduct;
    private String code;
    private String name;
    private Long idMetric;
    private String metricName;
    private String metricShortName;
    private String description;
    private Integer status;

    // Relaciones del producto con proveedores
    private List<ProductProvider> providers = new ArrayList<>();

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Product() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idProduct identificador del registro relacionado con la operación
     * @param code valor de code requerido por la operación
     * @param name valor de name requerido por la operación
     * @param idMetric identificador del registro relacionado con la operación
     * @param metricName valor de metricName requerido por la operación
     * @param metricShortName valor de metricShortName requerido por la operación
     * @param description valor de description requerido por la operación
     * @param status estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Product(Long idProduct, String code, String name, Long idMetric,
                   String metricName, String metricShortName,
                   String description, Integer status) {
        this.idProduct = idProduct;
        this.code = code;
        this.name = name;
        this.idMetric = idMetric;
        this.metricName = metricName;
        this.metricShortName = metricShortName;
        this.description = description;
        this.status = status;
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idProduct identificador del registro relacionado con la operación
     * @param code valor de code requerido por la operación
     * @param name valor de name requerido por la operación
     * @param idMetric identificador del registro relacionado con la operación
     * @param metricName valor de metricName requerido por la operación
     * @param metricShortName valor de metricShortName requerido por la operación
     * @param description valor de description requerido por la operación
     * @param status estado que se utilizará en la operación
     * @param providers valor de providers requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Product(Long idProduct, String code, String name, Long idMetric,
                   String metricName, String metricShortName,
                   String description, Integer status,
                   List<ProductProvider> providers) {
        this.idProduct = idProduct;
        this.code = code;
        this.name = name;
        this.idMetric = idMetric;
        this.metricName = metricName;
        this.metricShortName = metricShortName;
        this.description = description;
        this.status = status;
        setProviders(providers);
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
    public String getCode() {
        return code;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param code valor de code requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setCode(String code) {
        this.code = code;
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
    public String getName() {
        return name;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param name valor de name requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setName(String name) {
        this.name = name;
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
    public String getDescription() {
        return description;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param description valor de description requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setDescription(String description) {
        this.description = description;
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
    public List<ProductProvider> getProviders() {
        return providers;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param providers valor de providers requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProviders(List<ProductProvider> providers) {
        this.providers = providers == null
                ? new ArrayList<>()
                : new ArrayList<>(providers);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param provider valor de provider requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void addProvider(ProductProvider provider) {
        if (provider != null) {
            providers.add(provider);
        }
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
        return Integer.valueOf(1).equals(status);
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
    public int getProviderCount() {
        if (providers == null || providers.isEmpty()) {
            return 0;
        }

        return (int) providers.stream()
                .filter(ProductProvider::isOperational)
                .count();
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
        return "Product{" +
                "idProduct=" + idProduct +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", idMetric=" + idMetric +
                ", metricName='" + metricName + '\'' +
                ", metricShortName='" + metricShortName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", providers=" + providers +
                '}';
    }
}
