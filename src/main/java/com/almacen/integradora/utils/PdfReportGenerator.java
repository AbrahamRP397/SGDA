package com.almacen.integradora.utils;

import com.almacen.integradora.models.report.MovementReport;
import com.almacen.integradora.models.report.ReportMovement;
import com.almacen.integradora.models.report.ReportProductDetail;
import com.almacen.integradora.utils.pdf.PdfBuilder;
import com.almacen.integradora.utils.pdf.PdfColors;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Define PdfReportGenerator y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public final class PdfReportGenerator {

    private static final String LOGO_RESOURCE="reports/logoSGDA.png";

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private PdfReportGenerator(){
    }

    /* ==========================================================
       GENERAR PDF
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param report valor de report requerido por la operación
     * @param outputStream valor de outputStream requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public static void generate(MovementReport report,OutputStream outputStream){
        if(report==null){
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }

        if(outputStream==null){
            throw new IllegalArgumentException("El flujo de salida no puede ser nulo.");
        }

        try(PdfBuilder pdf=new PdfBuilder(outputStream)){
            Image logo=loadLogo();

            pdf.metadata(
                    report.getTitle(),
                    "Reporte de movimientos del Sistema Gestor de Almacén",
                    report.getGeneratedBy()
            );

            pdf.open()
                    .addHeader("Sistema Gestor de Almacén",logo)
                    .addTitle(report.getTitle())
                    .addSubtitle(buildSubtitle(report))
                    .addInformation(buildGeneralInformation(report))
                    .addSectionTitle("Resumen")
                    .addInformation(buildSummary(report));

            if(report.isEmpty()){
                pdf.addParagraph("No se encontraron movimientos dentro del periodo seleccionado.");
                return;
            }

            pdf.addSectionTitle("Movimientos");

            for(ReportMovement movement:report.getMovements()){
                addMovement(pdf,movement);
            }

            pdf.addSeparator()
                    .addTotal("Gran total",report.getGrandTotal());

        }catch(RuntimeException exception){
            throw new RuntimeException(
                    "No fue posible generar el reporte PDF.",
                    exception
            );
        }
    }

    /* ==========================================================
       CARGAR LOGO
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static Image loadLogo(){
        try{
            ClassLoader classLoader=PdfReportGenerator.class.getClassLoader();
            URL logoUrl=classLoader.getResource(LOGO_RESOURCE);

            if(logoUrl==null){
                return null;
            }

            Image logo=Image.getInstance(logoUrl);
            logo.scaleToFit(52f,52f);
            logo.setAlignment(Image.ALIGN_CENTER);

            return logo;

        }catch(Exception exception){
            /*
             * Si el logo no puede cargarse, el PDF se genera
             * de todas formas sin imagen.
             */
            return null;
        }
    }

    /* ==========================================================
       INFORMACIÓN GENERAL
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param report valor de report requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static Map<String,String> buildGeneralInformation(MovementReport report){
        Map<String,String> information=new LinkedHashMap<>();

        information.put(
                "Tipo de reporte",
                translateType(report.getReportType())
        );

        information.put(
                "Periodo",
                translatePeriod(report.getPeriod())
        );

        information.put(
                "Fecha inicial",
                PdfBuilder.formatDate(report.getStartDate())
        );

        information.put(
                "Fecha final",
                PdfBuilder.formatDate(report.getEndDate())
        );

        information.put(
                "Generado por",
                safeOrDash(report.getGeneratedBy())
        );

        information.put(
                "Fecha de generación",
                PdfBuilder.formatDateTime(report.getGeneratedAt())
        );

        return information;
    }

    /* ==========================================================
       RESUMEN
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param report valor de report requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static Map<String,String> buildSummary(MovementReport report){
        Map<String,String> summary=new LinkedHashMap<>();

        summary.put(
                "Movimientos",
                String.valueOf(report.getMovementCount())
        );

        summary.put(
                "Entradas",
                String.valueOf(report.getEntryCount())
        );

        summary.put(
                "Salidas",
                String.valueOf(report.getExitCount())
        );

        summary.put(
                "Cantidad total de productos",
                String.valueOf(report.getTotalProductsQuantity())
        );

        summary.put(
                "Importe acumulado",
                PdfBuilder.formatCurrency(report.getGrandTotal())
        );

        return summary;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param report valor de report requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static String buildSubtitle(MovementReport report){
        return translatePeriod(report.getPeriod())
                +" · "
                +PdfBuilder.formatDate(report.getStartDate())
                +" al "
                +PdfBuilder.formatDate(report.getEndDate());
    }

    /* ==========================================================
       MOVIMIENTO
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param pdf valor de pdf requerido por la operación
     * @param movement valor de movement requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static void addMovement(PdfBuilder pdf,ReportMovement movement){
        if(movement==null){
            return;
        }

        pdf.addCustomTable(
                createMovementHeader(pdf,movement)
        );

        if(movement.getDetails()==null||movement.getDetails().isEmpty()){
            pdf.addParagraph(
                    "Este movimiento no contiene productos registrados."
            );
        }else{
            pdf.addCustomTable(
                    createDetailsTable(pdf,movement)
            );
        }

        pdf.addTotal(
                "Total del movimiento",
                movement.getTotal()
        );

        pdf.addSpacer(6f);
    }

    /* ==========================================================
       ENCABEZADO DEL MOVIMIENTO
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param pdf valor de pdf requerido por la operación
     * @param movement valor de movement requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static PdfPTable createMovementHeader(
            PdfBuilder pdf,
            ReportMovement movement
    ){
        PdfPTable table=new PdfPTable(4);

        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(8f);
        table.setKeepTogether(true);

        try{
            table.setWidths(
                    new float[]{1.15f,2f,1.15f,2f}
            );
        }catch(Exception exception){
            throw new IllegalStateException(
                    "No fue posible configurar el encabezado del movimiento.",
                    exception
            );
        }

        PdfPCell titleCell=pdf.createHighlightedCell(
                translateMovementType(movement.getType())
                        +" · "
                        +safeOrDash(movement.getFolio()),
                "ENTRADA".equalsIgnoreCase(movement.getType())
                        ?PdfColors.INFO
                        :PdfColors.DANGER
        );

        titleCell.setColspan(4);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        table.addCell(titleCell);

        addLabelValue(
                table,
                pdf,
                "Folio",
                safeOrDash(movement.getFolio())
        );

        addLabelValue(
                table,
                pdf,
                "Fecha",
                formatTimestamp(movement)
        );

        addLabelValue(
                table,
                pdf,
                "Factura",
                safeOrDash(movement.getInvoiceNumber())
        );

        addLabelValue(
                table,
                pdf,
                "Usuario",
                safeOrDash(movement.getUserName())
        );

        addLabelValue(
                table,
                pdf,
                safeOrDefault(
                        movement.getDestinationType(),
                        "Destino"
                ),
                safeOrDash(movement.getDestinationName())
        );

        addLabelValue(
                table,
                pdf,
                "Comprador",
                safeOrDash(movement.getBuyerName())
        );

        return table;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param table valor de table requerido por la operación
     * @param pdf valor de pdf requerido por la operación
     * @param label valor de label requerido por la operación
     * @param value valor de value requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static void addLabelValue(
            PdfPTable table,
            PdfBuilder pdf,
            String label,
            String value
    ){
        PdfPCell labelCell=pdf.createHighlightedCell(
                label,
                PdfColors.TEXT
        );

        labelCell.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        PdfPCell valueCell=pdf.createBodyCell(
                value
        );

        valueCell.setHorizontalAlignment(
                Element.ALIGN_LEFT
        );

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    /* ==========================================================
       TABLA DE PRODUCTOS
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param pdf valor de pdf requerido por la operación
     * @param movement valor de movement requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static PdfPTable createDetailsTable(
            PdfBuilder pdf,
            ReportMovement movement
    ){
        PdfPTable table=new PdfPTable(6);

        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSplitLate(false);
        table.setSplitRows(true);
        table.setSpacingAfter(8f);

        try{
            table.setWidths(
                    new float[]{1.2f,2.7f,1.1f,.9f,1.2f,1.3f}
            );
        }catch(Exception exception){
            throw new IllegalStateException(
                    "No fue posible configurar la tabla de productos.",
                    exception
            );
        }

        table.addCell(
                pdf.createHeaderCell("Código")
        );

        table.addCell(
                pdf.createHeaderCell("Producto")
        );

        table.addCell(
                pdf.createHeaderCell("Unidad")
        );

        table.addCell(
                pdf.createHeaderCell("Cantidad")
        );

        table.addCell(
                pdf.createHeaderCell("Precio")
        );

        table.addCell(
                pdf.createHeaderCell("Subtotal")
        );

        for(ReportProductDetail detail:movement.getDetails()){
            if(detail==null){
                continue;
            }

            table.addCell(
                    pdf.createBodyCell(
                            safeOrDash(detail.getProductCode())
                    )
            );

            table.addCell(
                    pdf.createBodyCell(
                            safeOrDash(detail.getProductName())
                    )
            );

            table.addCell(
                    pdf.createBodyCell(
                            safeOrDash(detail.getMetricName())
                    )
            );

            table.addCell(
                    pdf.createBodyCell(
                            String.valueOf(detail.getQuantity()),
                            Element.ALIGN_RIGHT
                    )
            );

            table.addCell(
                    pdf.createBodyCell(
                            PdfBuilder.formatCurrency(
                                    detail.getUnitPrice()
                            ),
                            Element.ALIGN_RIGHT
                    )
            );

            table.addCell(
                    pdf.createBodyCell(
                            PdfBuilder.formatCurrency(
                                    detail.getSubtotal()
                            ),
                            Element.ALIGN_RIGHT
                    )
            );
        }

        return table;
    }

    /* ==========================================================
       TRADUCCIONES
       ========================================================== */

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
    private static String translateType(String type){
        if(type==null){
            return "Entradas y salidas";
        }

        return switch(type.toLowerCase()){
            case "entries"->"Entradas";
            case "exits"->"Salidas";
            default->"Entradas y salidas";
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
    private static String translatePeriod(String period){
        if(period==null){
            return "Mensual";
        }

        return switch(period.toLowerCase()){
            case "daily"->"Hoy";
            case "weekly"->"Semanal";
            case "annual"->"Anual";
            case "custom"->"Rango personalizado";
            default->"Mensual";
        };
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
    private static String translateMovementType(String type){
        return "ENTRADA".equalsIgnoreCase(type)
                ?"Entrada"
                :"Salida";
    }

    /* ==========================================================
       FORMATO Y TEXTO SEGURO
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param movement valor de movement requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static String formatTimestamp(ReportMovement movement){
        if(movement.getChangeDate()==null){
            return "-";
        }

        return movement.getChangeDate()
                .toLocalDateTime()
                .format(
                        java.time.format.DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm"
                        )
                );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static String safeOrDash(String value){
        return value==null||value.isBlank()
                ?"-"
                :value.trim();
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @param fallback valor de fallback requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static String safeOrDefault(
            String value,
            String fallback
    ){
        return value==null||value.isBlank()
                ?fallback
                :value.trim();
    }
}
