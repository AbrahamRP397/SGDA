package com.almacen.integradora.models.report;

import com.almacen.integradora.utils.SQLConnector;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Construye reportes de movimientos para su exportación.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class ReportDao{
    private static final String TYPE_MOVEMENTS="movements";
    private static final String TYPE_ENTRIES="entries";
    private static final String TYPE_EXITS="exits";
    private static final String PERIOD_DAILY="daily";
    private static final String PERIOD_WEEKLY="weekly";
    private static final String PERIOD_MONTHLY="monthly";
    private static final String PERIOD_ANNUAL="annual";
    private static final String PERIOD_CUSTOM="custom";

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param type valor de type requerido por la operación
     * @param period valor de period requerido por la operación
     * @param generatedBy valor de generatedBy requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public MovementReport getMovementReport(String type,String period,String generatedBy){
        String safePeriod=normalizePeriod(period);
        DateRange dateRange=calculateDateRange(safePeriod);
        return buildMovementReport(type,safePeriod,dateRange.startDate(),dateRange.endDateInclusive(),generatedBy);
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param type valor de type requerido por la operación
     * @param startDate valor de startDate requerido por la operación
     * @param endDate valor de endDate requerido por la operación
     * @param generatedBy valor de generatedBy requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public MovementReport getMovementReport(String type,LocalDate startDate,LocalDate endDate,String generatedBy){
        validateCustomDateRange(startDate,endDate);
        return buildMovementReport(type,PERIOD_CUSTOM,startDate,endDate,generatedBy);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param type valor de type requerido por la operación
     * @param period valor de period requerido por la operación
     * @param startDate valor de startDate requerido por la operación
     * @param endDate valor de endDate requerido por la operación
     * @param generatedBy valor de generatedBy requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private MovementReport buildMovementReport(String type,String period,LocalDate startDate,LocalDate endDate,String generatedBy){
        if(startDate==null||endDate==null){
            throw new IllegalArgumentException("El rango de fechas del reporte no es válido.");
        }

        String safeType=normalizeType(type);
        LocalDate endDateExclusive=endDate.plusDays(1);
        List<ReportMovement> movements=new ArrayList<>();

        try(Connection connection=SQLConnector.getConnection()){
            if(TYPE_MOVEMENTS.equals(safeType)||TYPE_ENTRIES.equals(safeType)){
                movements.addAll(getEntries(connection,startDate,endDateExclusive));
            }

            if(TYPE_MOVEMENTS.equals(safeType)||TYPE_EXITS.equals(safeType)){
                movements.addAll(getExits(connection,startDate,endDateExclusive));
            }
        }catch(SQLException exception){
            throw new RuntimeException("Error al consultar los datos del reporte.",exception);
        }

        movements.sort(
                Comparator.comparing(
                        ReportMovement::getChangeDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );

        MovementReport report=new MovementReport();
        report.setTitle(getReportTitle(safeType));
        report.setReportType(safeType);
        report.setPeriod(period);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setGeneratedAt(LocalDateTime.now());
        report.setGeneratedBy(normalizeGeneratedBy(generatedBy));
        report.setMovements(movements);
        return report;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param startDate valor de startDate requerido por la operación
     * @param endDateExclusive valor de endDateExclusive requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ReportMovement> getEntries(Connection connection,LocalDate startDate,LocalDate endDateExclusive)throws SQLException{
        String sql="""
                SELECT
                    e.id_entry AS movement_id,
                    e.folio_number AS folio,
                    e.invoice_number AS invoice_number,
                    e.change_date AS change_date,
                    e.id_user AS user_id,
                    TRIM(u.name || ' ' || u.surname || ' ' || u.lastname) AS user_name,
                    prov.name AS destination_name,
                    NVL(e.total_all_prices,0) AS movement_total,
                    p.id_product AS product_id,
                    p.code AS product_code,
                    p.name AS product_name,
                    m.name AS metric_name,
                    ep.quantity AS product_quantity,
                    ep.unit_price AS unit_price,
                    NVL(ep.total_price,ep.unit_price*ep.quantity) AS subtotal
                FROM entries e
                INNER JOIN users u
                    ON e.id_user=u.id_user
                INNER JOIN providers prov
                    ON e.id_provider=prov.id_provider
                LEFT JOIN entry_products ep
                    ON e.id_entry=ep.id_entry
                LEFT JOIN product_providers pp
                    ON ep.id_product_provider=pp.id_product_provider
                LEFT JOIN products p
                    ON pp.id_product=p.id_product
                LEFT JOIN metrics m
                    ON p.id_metric=m.id_metric
                WHERE e.change_date>=?
                  AND e.change_date<?
                ORDER BY
                    e.change_date DESC,
                    e.id_entry DESC,
                    ep.id_entry_product
                """;

        Map<Long,ReportMovement> movements=new LinkedHashMap<>();

        try(PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setTimestamp(1,Timestamp.valueOf(startDate.atStartOfDay()));
            statement.setTimestamp(2,Timestamp.valueOf(endDateExclusive.atStartOfDay()));

            try(ResultSet resultSet=statement.executeQuery()){
                while(resultSet.next()){
                    long movementId=resultSet.getLong("movement_id");
                    ReportMovement movement=movements.get(movementId);

                    if(movement==null){
                        movement=mapEntryMovement(resultSet);
                        movements.put(movementId,movement);
                    }

                    addProductDetail(resultSet,movement);
                }
            }
        }

        return new ArrayList<>(movements.values());
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private ReportMovement mapEntryMovement(ResultSet resultSet)throws SQLException{
        ReportMovement movement=new ReportMovement();
        movement.setMovementId(resultSet.getLong("movement_id"));
        movement.setType("ENTRADA");
        movement.setFolio(resultSet.getString("folio"));
        movement.setInvoiceNumber(resultSet.getString("invoice_number"));
        movement.setChangeDate(resultSet.getTimestamp("change_date"));
        movement.setUserId(resultSet.getLong("user_id"));
        movement.setUserName(resultSet.getString("user_name"));
        movement.setDestinationType("PROVEEDOR");
        movement.setDestinationName(resultSet.getString("destination_name"));
        movement.setBuyerName(null);
        movement.setTotal(getBigDecimalOrZero(resultSet,"movement_total"));
        return movement;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param connection conexión JDBC activa
     * @param startDate valor de startDate requerido por la operación
     * @param endDateExclusive valor de endDateExclusive requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ReportMovement> getExits(Connection connection,LocalDate startDate,LocalDate endDateExclusive)throws SQLException{
        String sql="""
                SELECT
                    x.id_exit AS movement_id,
                    x.folio_number AS folio,
                    x.invoice_number AS invoice_number,
                    x.change_date AS change_date,
                    x.id_user AS user_id,
                    TRIM(u.name || ' ' || u.surname || ' ' || u.lastname) AS user_name,
                    a.name AS destination_name,
                    x.buyer_name AS buyer_name,
                    NVL(x.total_all_prices,0) AS movement_total,
                    p.id_product AS product_id,
                    p.code AS product_code,
                    p.name AS product_name,
                    m.name AS metric_name,
                    xp.quantity AS product_quantity,
                    xp.unit_price AS unit_price,
                    NVL(xp.total_price,xp.unit_price*xp.quantity) AS subtotal
                FROM exits x
                INNER JOIN users u
                    ON x.id_user=u.id_user
                INNER JOIN areas a
                    ON x.id_area=a.id_area
                LEFT JOIN exit_products xp
                    ON x.id_exit=xp.id_exit
                LEFT JOIN products p
                    ON xp.id_product=p.id_product
                LEFT JOIN metrics m
                    ON p.id_metric=m.id_metric
                WHERE x.change_date>=?
                  AND x.change_date<?
                ORDER BY
                    x.change_date DESC,
                    x.id_exit DESC,
                    xp.id_exit_product
                """;

        Map<Long,ReportMovement> movements=new LinkedHashMap<>();

        try(PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setTimestamp(1,Timestamp.valueOf(startDate.atStartOfDay()));
            statement.setTimestamp(2,Timestamp.valueOf(endDateExclusive.atStartOfDay()));

            try(ResultSet resultSet=statement.executeQuery()){
                while(resultSet.next()){
                    long movementId=resultSet.getLong("movement_id");
                    ReportMovement movement=movements.get(movementId);

                    if(movement==null){
                        movement=mapExitMovement(resultSet);
                        movements.put(movementId,movement);
                    }

                    addProductDetail(resultSet,movement);
                }
            }
        }

        return new ArrayList<>(movements.values());
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private ReportMovement mapExitMovement(ResultSet resultSet)throws SQLException{
        ReportMovement movement=new ReportMovement();
        movement.setMovementId(resultSet.getLong("movement_id"));
        movement.setType("SALIDA");
        movement.setFolio(resultSet.getString("folio"));
        movement.setInvoiceNumber(resultSet.getString("invoice_number"));
        movement.setChangeDate(resultSet.getTimestamp("change_date"));
        movement.setUserId(resultSet.getLong("user_id"));
        movement.setUserName(resultSet.getString("user_name"));
        movement.setDestinationType("ÁREA");
        movement.setDestinationName(resultSet.getString("destination_name"));
        movement.setBuyerName(resultSet.getString("buyer_name"));
        movement.setTotal(getBigDecimalOrZero(resultSet,"movement_total"));
        return movement;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @param movement valor de movement requerido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void addProductDetail(ResultSet resultSet,ReportMovement movement)throws SQLException{
        long productId=resultSet.getLong("product_id");

        if(resultSet.wasNull()){
            return;
        }

        ReportProductDetail detail=new ReportProductDetail();
        detail.setProductId(productId);
        detail.setProductCode(resultSet.getString("product_code"));
        detail.setProductName(resultSet.getString("product_name"));
        detail.setMetricName(resultSet.getString("metric_name"));
        detail.setQuantity(resultSet.getLong("product_quantity"));
        detail.setUnitPrice(getBigDecimalOrZero(resultSet,"unit_price"));
        detail.setSubtotal(getBigDecimalOrZero(resultSet,"subtotal"));
        movement.addDetail(detail);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param period valor de period requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private DateRange calculateDateRange(String period){
        LocalDate today=LocalDate.now();
        LocalDate startDate;

        switch(period){
            case PERIOD_DAILY->startDate=today;
            case PERIOD_WEEKLY->startDate=today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case PERIOD_ANNUAL->startDate=today.withDayOfYear(1);
            default->startDate=today.withDayOfMonth(1);
        }

        return new DateRange(startDate,today);
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param startDate valor de startDate requerido por la operación
     * @param endDate valor de endDate requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void validateCustomDateRange(LocalDate startDate,LocalDate endDate){
        if(startDate==null||endDate==null){
            throw new IllegalArgumentException("La fecha inicial y final son obligatorias.");
        }

        if(endDate.isBefore(startDate)){
            throw new IllegalArgumentException("La fecha final no puede ser anterior a la fecha inicial.");
        }

        LocalDate today=LocalDate.now();

        if(startDate.isAfter(today)){
            throw new IllegalArgumentException("La fecha inicial no puede estar en el futuro.");
        }

        if(endDate.isAfter(today)){
            throw new IllegalArgumentException("La fecha final no puede estar en el futuro.");
        }

        if(startDate.plusYears(10).isBefore(endDate)){
            throw new IllegalArgumentException("El rango seleccionado es demasiado amplio.");
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param type valor de type requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String normalizeType(String type){
        if(type==null){
            return TYPE_MOVEMENTS;
        }

        return switch(type.trim().toLowerCase(Locale.ROOT)){
            case TYPE_ENTRIES->TYPE_ENTRIES;
            case TYPE_EXITS->TYPE_EXITS;
            default->TYPE_MOVEMENTS;
        };
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param period valor de period requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String normalizePeriod(String period){
        if(period==null){
            return PERIOD_MONTHLY;
        }

        return switch(period.trim().toLowerCase(Locale.ROOT)){
            case PERIOD_DAILY->PERIOD_DAILY;
            case PERIOD_WEEKLY->PERIOD_WEEKLY;
            case PERIOD_ANNUAL->PERIOD_ANNUAL;
            case PERIOD_MONTHLY->PERIOD_MONTHLY;
            default->PERIOD_MONTHLY;
        };
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param type valor de type requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String getReportTitle(String type){
        return switch(type){
            case TYPE_ENTRIES->"Reporte de entradas";
            case TYPE_EXITS->"Reporte de salidas";
            default->"Reporte de entradas y salidas";
        };
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param generatedBy valor de generatedBy requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String normalizeGeneratedBy(String generatedBy){
        return generatedBy==null||generatedBy.isBlank()
                ?"Usuario del sistema"
                :generatedBy.trim().replaceAll("\\s+"," ");
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @param columnName valor de columnName requerido por la operación
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private BigDecimal getBigDecimalOrZero(ResultSet resultSet,String columnName)throws SQLException{
        BigDecimal value=resultSet.getBigDecimal(columnName);
        return value==null?BigDecimal.ZERO:value;
    }

    private record DateRange(LocalDate startDate,LocalDate endDateInclusive){}
}
