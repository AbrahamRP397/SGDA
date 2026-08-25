package com.almacen.integradora.models.area;

/** Entidad de área de destino con nombre corto, descripción y estado.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class Area {

    private Long idArea;
    private String shortName;
    private String name;
    private String description;
    private Integer status;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Area() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idArea identificador del registro relacionado con la operación
     * @param shortName valor de shortName requerido por la operación
     * @param name valor de name requerido por la operación
     * @param description valor de description requerido por la operación
     * @param status estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Area(Long idArea, String shortName, String name, String description, Integer status) {
        this.idArea = idArea;
        this.shortName = shortName;
        this.name = name;
        this.description = description;
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
    public Long getIdArea() {
        return idArea;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idArea identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdArea(Long idArea) {
        this.idArea = idArea;
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
        return "Area{" +
                "idArea=" + idArea +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
