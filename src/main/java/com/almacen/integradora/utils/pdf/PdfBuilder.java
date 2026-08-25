package com.almacen.integradora.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * Define PdfBuilder y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public final class PdfBuilder implements AutoCloseable {

    private static final float DEFAULT_MARGIN=36f;
    private static final DateTimeFormatter DATE_FORMATTER=DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final NumberFormat CURRENCY_FORMATTER=NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"));

    private final Document document;
    private final PdfWriter writer;
    private boolean opened;
    private boolean closed;

    /* ==========================================================
       CONSTRUCTOR
       ========================================================== */

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param outputStream valor de outputStream requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder(OutputStream outputStream){
        this(outputStream,PageSize.A4);
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param outputStream valor de outputStream requerido por la operación
     * @param pageSize valor de pageSize requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder(OutputStream outputStream,Rectangle pageSize){
        if(outputStream==null){
            throw new IllegalArgumentException("El flujo de salida no puede ser nulo.");
        }

        Rectangle safePageSize=pageSize==null?PageSize.A4:pageSize;
        document=new Document(safePageSize,DEFAULT_MARGIN,DEFAULT_MARGIN,55f,45f);

        try{
            writer=PdfWriter.getInstance(document,outputStream);
            writer.setPageEvent(new PageFooterEvent());
            document.addTitle("Sistema Gestor de Almacén");
            document.addCreator("SGDA");
            document.addAuthor("Sistema Gestor de Almacén");
        }catch(DocumentException exception){
            throw new IllegalStateException("No fue posible inicializar el documento PDF.",exception);
        }
    }

    /* ==========================================================
       APERTURA
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
    public PdfBuilder open(){
        if(closed){
            throw new IllegalStateException("El documento PDF ya fue cerrado.");
        }

        if(!opened){
            document.open();
            opened=true;
        }

        return this;
    }

    /**
     * Ejecuta la operación específica de este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void ensureOpen(){
        if(closed){
            throw new IllegalStateException("El documento PDF ya fue cerrado.");
        }

        if(!opened){
            open();
        }
    }

    /* ==========================================================
       ENCABEZADO PRINCIPAL
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param systemName valor de systemName requerido por la operación
     * @param logo valor de logo requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addHeader(String systemName,Image logo){
        ensureOpen();

        PdfPTable table=new PdfPTable(logo==null?1:2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(18f);

        try{
            if(logo!=null){
                table.setWidths(new float[]{1.1f,5.9f});
                logo.scaleToFit(48f,48f);

                PdfPCell logoCell=new PdfPCell(logo,false);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setBackgroundColor(PdfColors.TABLE_HEADER);
                logoCell.setPadding(10f);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(logoCell);
            }

            Paragraph title=new Paragraph(safeText(systemName,"Sistema Gestor de Almacén"),PdfFonts.systemTitle());
            title.setAlignment(Element.ALIGN_LEFT);

            PdfPCell titleCell=new PdfPCell(title);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setBackgroundColor(PdfColors.TABLE_HEADER);
            titleCell.setPadding(15f);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(titleCell);

            document.add(table);
            return this;
        }catch(DocumentException exception){
            throw new IllegalStateException("No fue posible agregar el encabezado del PDF.",exception);
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param systemName valor de systemName requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addHeader(String systemName){
        return addHeader(systemName,null);
    }

    /* ==========================================================
       TÍTULOS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param title valor de title requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addTitle(String title){
        ensureOpen();

        Paragraph paragraph=new Paragraph(safeText(title,"Reporte"),PdfFonts.documentTitle());
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(5f);
        addElement(paragraph);
        return this;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param subtitle valor de subtitle requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addSubtitle(String subtitle){
        ensureOpen();

        if(subtitle==null||subtitle.isBlank()){
            return this;
        }

        Paragraph paragraph=new Paragraph(subtitle.trim(),PdfFonts.subtitle());
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(16f);
        addElement(paragraph);
        return this;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param title valor de title requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addSectionTitle(String title){
        ensureOpen();

        PdfPTable table=new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(8f);

        PdfPCell cell=new PdfPCell(new Phrase(safeText(title,"Sección"),PdfFonts.sectionTitle()));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(1f);
        cell.setBorderColorBottom(PdfColors.BORDER);
        cell.setPaddingBottom(7f);
        table.addCell(cell);

        addElement(table);
        return this;
    }

    /* ==========================================================
       INFORMACIÓN GENERAL
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param information valor de information requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addInformation(Map<String,String> information){
        ensureOpen();

        if(information==null||information.isEmpty()){
            return this;
        }

        PdfPTable table=new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(15f);

        try{
            table.setWidths(new float[]{1.35f,3.65f});
        }catch(DocumentException exception){
            throw new IllegalStateException("No fue posible configurar la información del PDF.",exception);
        }

        for(Map.Entry<String,String> entry:information.entrySet()){
            PdfPCell labelCell=createCell(safeText(entry.getKey(),""),PdfFonts.normalBold(),PdfColors.BACKGROUND);
            labelCell.setPadding(7f);
            labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell valueCell=createCell(safeText(entry.getValue(),"-"),PdfFonts.normal(),PdfColors.PANEL);
            valueCell.setPadding(7f);
            valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);

            table.addCell(labelCell);
            table.addCell(valueCell);
        }

        addElement(table);
        return this;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param label valor de label requerido por la operación
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addInformation(String label,String value){
        return addInformation(Map.of(safeText(label,"Información"),safeText(value,"-")));
    }

    /* ==========================================================
       TEXTO
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param text valor de text requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addParagraph(String text){
        ensureOpen();

        if(text==null||text.isBlank()){
            return this;
        }

        Paragraph paragraph=new Paragraph(text.trim(),PdfFonts.normal());
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        paragraph.setLeading(14f);
        paragraph.setSpacingAfter(10f);
        addElement(paragraph);
        return this;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param height valor de height requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addSpacer(float height){
        ensureOpen();

        Paragraph spacer=new Paragraph(" ");
        spacer.setLeading(Math.max(height,1f));
        addElement(spacer);
        return this;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addSeparator(){
        ensureOpen();

        PdfPTable separator=new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingBefore(7f);
        separator.setSpacingAfter(10f);

        PdfPCell cell=new PdfPCell(new Phrase(""));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBorderWidthBottom(1f);
        cell.setBorderColorBottom(PdfColors.BORDER);
        cell.setFixedHeight(2f);
        separator.addCell(cell);

        addElement(separator);
        return this;
    }

    /* ==========================================================
       TABLAS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param headers valor de headers requerido por la operación
     * @param rows valor de rows requerido por la operación
     * @param widths valor de widths requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addTable(String[] headers,List<String[]> rows,float[] widths){
        ensureOpen();

        if(headers==null||headers.length==0){
            throw new IllegalArgumentException("La tabla debe contener al menos un encabezado.");
        }

        PdfPTable table=new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSplitLate(false);
        table.setSplitRows(true);
        table.setSpacingAfter(12f);

        if(widths!=null&&widths.length==headers.length){
            try{
                table.setWidths(widths);
            }catch(DocumentException exception){
                throw new IllegalArgumentException("Los anchos de la tabla no son válidos.",exception);
            }
        }

        for(String header:headers){
            PdfPCell cell=createCell(safeText(header,""),PdfFonts.tableHeader(),PdfColors.TABLE_HEADER);
            cell.setPadding(7f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }

        if(rows!=null){
            for(int rowIndex=0;rowIndex<rows.size();rowIndex++){
                String[] row=rows.get(rowIndex);

                for(int columnIndex=0;columnIndex<headers.length;columnIndex++){
                    String value=row!=null&&columnIndex<row.length?row[columnIndex]:"";
                    Color background=rowIndex%2==0?PdfColors.PANEL:PdfColors.TABLE_ALTERNATE;

                    PdfPCell cell=createCell(safeText(value,""),PdfFonts.tableCell(),background);
                    cell.setPadding(6f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(cell);
                }
            }
        }

        addElement(table);
        return this;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param headers valor de headers requerido por la operación
     * @param rows valor de rows requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addTable(String[] headers,List<String[]> rows){
        return addTable(headers,rows,null);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param table valor de table requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addCustomTable(PdfPTable table){
        ensureOpen();

        if(table==null){
            return this;
        }

        table.setWidthPercentage(100);
        addElement(table);
        return this;
    }

    /* ==========================================================
       CELDAS REUTILIZABLES
       ========================================================== */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param text valor de text requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfPCell createHeaderCell(String text){
        PdfPCell cell=createCell(safeText(text,""),PdfFonts.tableHeader(),PdfColors.TABLE_HEADER);
        cell.setPadding(7f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param text valor de text requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfPCell createBodyCell(String text){
        PdfPCell cell=createCell(safeText(text,""),PdfFonts.tableCell(),PdfColors.PANEL);
        cell.setPadding(6f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param text valor de text requerido por la operación
     * @param alignment valor de alignment requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfPCell createBodyCell(String text,int alignment){
        PdfPCell cell=createBodyCell(text);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param text valor de text requerido por la operación
     * @param textColor valor de textColor requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfPCell createHighlightedCell(String text,Color textColor){
        Font font=PdfFonts.create(8,Font.BOLD,textColor==null?PdfColors.TEXT:textColor);
        PdfPCell cell=createCell(safeText(text,""),font,PdfColors.BACKGROUND);
        cell.setPadding(7f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param text valor de text requerido por la operación
     * @param font valor de font requerido por la operación
     * @param background valor de background requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private PdfPCell createCell(String text,Font font,Color background){
        PdfPCell cell=new PdfPCell(new Phrase(text,font));
        cell.setBorder(Rectangle.BOX);
        cell.setBorderWidth(.5f);
        cell.setBorderColor(PdfColors.BORDER);
        cell.setBackgroundColor(background);
        return cell;
    }

    /* ==========================================================
       TOTALES
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param label valor de label requerido por la operación
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addTotal(String label,BigDecimal value){
        return addTotal(label,formatCurrency(value));
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param label valor de label requerido por la operación
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder addTotal(String label,String value){
        ensureOpen();

        PdfPTable table=new PdfPTable(2);
        table.setWidthPercentage(45f);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(5f);
        table.setSpacingAfter(12f);

        try{
            table.setWidths(new float[]{1.4f,1f});
        }catch(DocumentException exception){
            throw new IllegalStateException("No fue posible configurar el total del PDF.",exception);
        }

        PdfPCell labelCell=createCell(safeText(label,"Total"),PdfFonts.total(),PdfColors.BACKGROUND);
        labelCell.setPadding(8f);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell valueCell=createCell(safeText(value,"$0.00"),PdfFonts.total(),PdfColors.PANEL);
        valueCell.setPadding(8f);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);

        addElement(table);
        return this;
    }

    /* ==========================================================
       NUEVA PÁGINA
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
    public PdfBuilder newPage(){
        ensureOpen();
        document.newPage();
        return this;
    }

    /* ==========================================================
       METADATOS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param title valor de title requerido por la operación
     * @param subject valor de subject requerido por la operación
     * @param author valor de author requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PdfBuilder metadata(String title,String subject,String author){
        if(opened){
            return this;
        }

        document.addTitle(safeText(title,"Reporte SGDA"));
        document.addSubject(safeText(subject,"Reporte del Sistema Gestor de Almacén"));
        document.addAuthor(safeText(author,"Sistema Gestor de Almacén"));
        return this;
    }

    /* ==========================================================
       FORMATO
       ========================================================== */

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
    public static String formatCurrency(BigDecimal value){
        BigDecimal safeValue=value==null?BigDecimal.ZERO:value.setScale(2,RoundingMode.HALF_UP);
        return CURRENCY_FORMATTER.format(safeValue);
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
    public static String formatDate(LocalDate value){
        return value==null?"-":value.format(DATE_FORMATTER);
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
    public static String formatDateTime(LocalDateTime value){
        return value==null?"-":value.format(DATE_TIME_FORMATTER);
    }

    /* ==========================================================
       AGREGAR ELEMENTOS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param element valor de element requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void addElement(Element element){
        try{
            document.add(element);
        }catch(DocumentException exception){
            throw new IllegalStateException("No fue posible agregar contenido al documento PDF.",exception);
        }
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
    private String safeText(String value,String fallback){
        return value==null||value.isBlank()?fallback:value.trim();
    }

    /* ==========================================================
       CIERRE
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public void close(){
        if(closed){
            return;
        }

        if(!opened){
            document.open();
            opened=true;
        }

        document.close();
        closed=true;
    }

    /* ==========================================================
       PIE DE PÁGINA
       ========================================================== */

    private static final class PageFooterEvent extends PdfPageEventHelper {

        private BaseFont baseFont;

        /**
         * Ejecuta la operación específica de este componente.
         *
         * @param writer valor de writer requerido por la operación
         * @param document valor de document requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
        @Override
        public void onOpenDocument(PdfWriter writer,Document document){
            try{
                baseFont=BaseFont.createFont(BaseFont.HELVETICA,BaseFont.WINANSI,BaseFont.NOT_EMBEDDED);
            }catch(Exception exception){
                baseFont=null;
            }
        }

        /**
         * Ejecuta la operación específica de este componente.
         *
         * @param writer valor de writer requerido por la operación
         * @param document valor de document requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
        @Override
        public void onEndPage(PdfWriter writer,Document document){
            if(baseFont==null){
                return;
            }

            String pageText="SGDA · Página "+writer.getPageNumber();
            String generationText="Generado el "+LocalDateTime.now().format(DATE_TIME_FORMATTER);

            writer.getDirectContent().saveState();
            writer.getDirectContent().setColorFill(PdfColors.TEXT_SECONDARY);
            writer.getDirectContent().beginText();
            writer.getDirectContent().setFontAndSize(baseFont,8f);

            writer.getDirectContent().showTextAligned(
                    Element.ALIGN_LEFT,
                    generationText,
                    document.left(),
                    document.bottom()-20f,
                    0
            );

            writer.getDirectContent().showTextAligned(
                    Element.ALIGN_RIGHT,
                    pageText,
                    document.right(),
                    document.bottom()-20f,
                    0
            );

            writer.getDirectContent().endText();
            writer.getDirectContent().restoreState();
        }
    }
}
