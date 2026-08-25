package com.almacen.integradora.models.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Proyección normalizada de una entrada o salida para reportes.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ReportMovement {

    private long movementId;
    private String type;
    private String folio;
    private String invoiceNumber;
    private Timestamp changeDate;
    private long userId;
    private String userName;
    private String destinationType;
    private String destinationName;
    private String buyerName;
    private BigDecimal total;
    private List<ReportProductDetail> details;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ReportMovement() {
        this.total=BigDecimal.ZERO;
        this.details=new ArrayList<>();
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param movementId valor de movementId requerido por la operación
     * @param type valor de type requerido por la operación
     * @param folio valor de folio requerido por la operación
     * @param invoiceNumber valor de invoiceNumber requerido por la operación
     * @param changeDate valor de changeDate requerido por la operación
     * @param userId valor de userId requerido por la operación
     * @param userName valor de userName requerido por la operación
     * @param destinationType valor de destinationType requerido por la operación
     * @param destinationName valor de destinationName requerido por la operación
     * @param buyerName valor de buyerName requerido por la operación
     * @param total valor de total requerido por la operación
     * @param details valor de details requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public ReportMovement(long movementId,String type,String folio,String invoiceNumber,Timestamp changeDate,long userId,String userName,String destinationType,String destinationName,String buyerName,BigDecimal total,List<ReportProductDetail> details) {
        this.movementId=movementId;
        this.type=type;
        this.folio=folio;
        this.invoiceNumber=invoiceNumber;
        this.changeDate=changeDate;
        this.userId=userId;
        this.userName=userName;
        this.destinationType=destinationType;
        this.destinationName=destinationName;
        this.buyerName=buyerName;
        this.total=total==null?BigDecimal.ZERO:total;
        this.details=details==null?new ArrayList<>():details;
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
    public long getMovementId() {
        return movementId;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param movementId valor de movementId requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setMovementId(long movementId) {
        this.movementId=movementId;
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
    public String getType() {
        return type;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param type valor de type requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setType(String type) {
        this.type=type;
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
    public String getFolio() {
        return folio;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param folio valor de folio requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setFolio(String folio) {
        this.folio=folio;
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
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param invoiceNumber valor de invoiceNumber requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber=invoiceNumber;
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
    public Timestamp getChangeDate() {
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
    public void setChangeDate(Timestamp changeDate) {
        this.changeDate=changeDate;
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
    public long getUserId() {
        return userId;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param userId valor de userId requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setUserId(long userId) {
        this.userId=userId;
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
    public String getUserName() {
        return userName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param userName valor de userName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setUserName(String userName) {
        this.userName=userName;
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
    public String getDestinationType() {
        return destinationType;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param destinationType valor de destinationType requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setDestinationType(String destinationType) {
        this.destinationType=destinationType;
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
        this.destinationName=destinationName;
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
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param buyerName valor de buyerName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setBuyerName(String buyerName) {
        this.buyerName=buyerName;
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
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param total valor de total requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTotal(BigDecimal total) {
        this.total=total==null?BigDecimal.ZERO:total;
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
    public List<ReportProductDetail> getDetails() {
        return details;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param details valor de details requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setDetails(List<ReportProductDetail> details) {
        this.details=details==null?new ArrayList<>():details;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param detail valor de detail requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void addDetail(ReportProductDetail detail) {
        if(detail!=null) {
            details.add(detail);
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
    public int getDetailCount() {
        return details.size();
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
    public long getTotalQuantity() {
        return details.stream().mapToLong(ReportProductDetail::getQuantity).sum();
    }
}
