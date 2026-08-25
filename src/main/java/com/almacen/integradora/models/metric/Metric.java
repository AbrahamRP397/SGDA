package com.almacen.integradora.models.metric;

/** Entidad de una unidad de medida utilizada por los productos.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class Metric {

    private Long idMetric;
    private String name;
    private String shortName;
    private Integer status;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Metric() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idMetric identificador del registro relacionado con la operación
     * @param name valor de name requerido por la operación
     * @param shortName valor de shortName requerido por la operación
     * @param status estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Metric(Long idMetric, String name, String shortName, Integer status) {
        this.idMetric = idMetric;
        this.name = name;
        this.shortName = shortName;
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
    public String getShortName() {
        return shortName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param shortName valor de shortName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setShortName(String shortName) {
        this.shortName = shortName;
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
     * Evalúa la condición indicada para el estado actual.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean isActive() {
        return status != null && status == 1;
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
        return "Metric{" +
                "idMetric=" + idMetric +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", status=" + status +
                '}';
    }
}
