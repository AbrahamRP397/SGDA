package com.almacen.integradora.models.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Modelo raíz de un reporte de movimientos y su periodo.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class MovementReport {

    private String title;
    private String reportType;
    private String period;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime generatedAt;
    private String generatedBy;
    private List<ReportMovement> movements;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public MovementReport() {
        this.generatedAt=LocalDateTime.now();
        this.movements=new ArrayList<>();
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param title valor de title requerido por la operación
     * @param reportType valor de reportType requerido por la operación
     * @param period valor de period requerido por la operación
     * @param startDate valor de startDate requerido por la operación
     * @param endDate valor de endDate requerido por la operación
     * @param generatedAt valor de generatedAt requerido por la operación
     * @param generatedBy valor de generatedBy requerido por la operación
     * @param movements valor de movements requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public MovementReport(String title,String reportType,String period,LocalDate startDate,LocalDate endDate,LocalDateTime generatedAt,String generatedBy,List<ReportMovement> movements) {
        this.title=title;
        this.reportType=reportType;
        this.period=period;
        this.startDate=startDate;
        this.endDate=endDate;
        this.generatedAt=generatedAt==null?LocalDateTime.now():generatedAt;
        this.generatedBy=generatedBy;
        this.movements=movements==null?new ArrayList<>():movements;
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
    public String getTitle() {
        return title;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param title valor de title requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTitle(String title) {
        this.title=title;
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
    public String getReportType() {
        return reportType;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param reportType valor de reportType requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setReportType(String reportType) {
        this.reportType=reportType;
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
    public String getPeriod() {
        return period;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param period valor de period requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setPeriod(String period) {
        this.period=period;
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
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param startDate valor de startDate requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setStartDate(LocalDate startDate) {
        this.startDate=startDate;
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
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param endDate valor de endDate requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setEndDate(LocalDate endDate) {
        this.endDate=endDate;
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
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param generatedAt valor de generatedAt requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt=generatedAt==null?LocalDateTime.now():generatedAt;
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
    public String getGeneratedBy() {
        return generatedBy;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param generatedBy valor de generatedBy requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setGeneratedBy(String generatedBy) {
        this.generatedBy=generatedBy;
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
    public List<ReportMovement> getMovements() {
        return movements;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param movements valor de movements requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setMovements(List<ReportMovement> movements) {
        this.movements=movements==null?new ArrayList<>():movements;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param movement valor de movement requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void addMovement(ReportMovement movement) {
        if(movement!=null) {
            movements.add(movement);
        }
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
    public int getMovementCount() {
        return movements.size();
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
    public int getEntryCount() {
        return (int) movements.stream().filter(movement -> "ENTRADA".equalsIgnoreCase(movement.getType())).count();
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
    public int getExitCount() {
        return (int) movements.stream().filter(movement -> "SALIDA".equalsIgnoreCase(movement.getType())).count();
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
    public long getTotalProductsQuantity() {
        return movements.stream().mapToLong(ReportMovement::getTotalQuantity).sum();
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
    public BigDecimal getGrandTotal() {
        return movements.stream().map(ReportMovement::getTotal).reduce(BigDecimal.ZERO,BigDecimal::add);
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
    public boolean isEmpty() {
        return movements.isEmpty();
    }
}
