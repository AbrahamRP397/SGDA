package com.almacen.integradora.models.dashboard;

import java.time.LocalDateTime;

/** Proyección resumida de un movimiento reciente del tablero.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class DashboardMovement {

    private Long idMovement;
    private String movementType;
    private String folioNumber;
    private LocalDateTime changeDate;

    private String destinationName;
    private String responsibleName;

    private Integer productCount;
    private Long totalQuantity;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public DashboardMovement() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idMovement identificador del registro relacionado con la operación
     * @param movementType valor de movementType requerido por la operación
     * @param folioNumber valor de folioNumber requerido por la operación
     * @param changeDate valor de changeDate requerido por la operación
     * @param destinationName valor de destinationName requerido por la operación
     * @param responsibleName valor de responsibleName requerido por la operación
     * @param productCount valor de productCount requerido por la operación
     * @param totalQuantity valor de totalQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public DashboardMovement(
            Long idMovement,
            String movementType,
            String folioNumber,
            LocalDateTime changeDate,
            String destinationName,
            String responsibleName,
            Integer productCount,
            Long totalQuantity
    ) {
        this.idMovement = idMovement;
        this.movementType = movementType;
        this.folioNumber = folioNumber;
        this.changeDate = changeDate;
        this.destinationName = destinationName;
        this.responsibleName = responsibleName;
        this.productCount = productCount;
        this.totalQuantity = totalQuantity;
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
    public Long getIdMovement() {
        return idMovement;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idMovement identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdMovement(Long idMovement) {
        this.idMovement = idMovement;
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
    public String getMovementType() {
        return movementType;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param movementType valor de movementType requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setMovementType(String movementType) {
        this.movementType = movementType;
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
    public String getFolioNumber() {
        return folioNumber;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param folioNumber valor de folioNumber requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
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
    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param changeDate valor de changeDate requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
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
    public String getDestinationName() {
        return destinationName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param destinationName valor de destinationName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
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
    public String getResponsibleName() {
        return responsibleName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param responsibleName valor de responsibleName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
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
    public Integer getProductCount() {
        return productCount;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param productCount valor de productCount requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
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
    public Long getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param totalQuantity valor de totalQuantity requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
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
    public boolean isEntry() {
        return "ENTRY".equalsIgnoreCase(movementType);
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
    public boolean isExit() {
        return "EXIT".equalsIgnoreCase(movementType);
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
        return "DashboardMovement{" +
                "idMovement=" + idMovement +
                ", movementType='" + movementType + '\'' +
                ", folioNumber='" + folioNumber + '\'' +
                ", changeDate=" + changeDate +
                ", destinationName='" + destinationName + '\'' +
                ", responsibleName='" + responsibleName + '\'' +
                ", productCount=" + productCount +
                ", totalQuantity=" + totalQuantity +
                '}';
    }
}
