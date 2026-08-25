package com.almacen.integradora.models.dashboard;

/** Punto agregado de entradas y salidas utilizado en las gráficas.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ChartMovement {

    private String periodKey;
    private String periodLabel;
    private Long entryQuantity;
    private Long exitQuantity;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ChartMovement() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param periodKey valor de periodKey requerido por la operación
     * @param periodLabel valor de periodLabel requerido por la operación
     * @param entryQuantity valor de entryQuantity requerido por la operación
     * @param exitQuantity valor de exitQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ChartMovement(
            String periodKey,
            String periodLabel,
            Long entryQuantity,
            Long exitQuantity
    ) {
        this.periodKey = periodKey;
        this.periodLabel = periodLabel;
        this.entryQuantity = entryQuantity;
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
    public String getPeriodKey() {
        return periodKey;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param periodKey valor de periodKey requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey;
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
    public String getPeriodLabel() {
        return periodLabel;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param periodLabel valor de periodLabel requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
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
        return "ChartMovement{" +
                "periodKey='" + periodKey + '\'' +
                ", periodLabel='" + periodLabel + '\'' +
                ", entryQuantity=" + entryQuantity +
                ", exitQuantity=" + exitQuantity +
                '}';
    }
}
