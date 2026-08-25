package com.almacen.integradora.utils;

import com.almacen.integradora.models.report.MovementReport;
import com.almacen.integradora.models.report.ReportMovement;
import com.almacen.integradora.models.report.ReportProductDetail;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
/**
 * Define XmlReportGenerator y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public final class XmlReportGenerator {

    private static final DateTimeFormatter DATE_FORMATTER=DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private XmlReportGenerator(){
    }

    /* ==========================================================
       GENERAR XML
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
    public static void generate(
            MovementReport report,
            OutputStream outputStream
    ) {
        if (report == null) {
            throw new IllegalArgumentException(
                    "El reporte no puede ser nulo."
            );
        }

        if (outputStream == null) {
            throw new IllegalArgumentException(
                    "El flujo de salida no puede ser nulo."
            );
        }

        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);

            DocumentBuilder builder =
                    factory.newDocumentBuilder();

            Document document =
                    builder.newDocument();

            Element root =
                    document.createElement("movementReport");

            document.appendChild(root);

            appendHeader(document, root, report);
            appendSummary(document, root, report);
            appendMovements(document, root, report);

            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();

            Transformer transformer =
                    transformerFactory.newTransformer();

            transformer.setOutputProperty(
                    OutputKeys.ENCODING,
                    StandardCharsets.UTF_8.name()
            );

            transformer.setOutputProperty(
                    OutputKeys.INDENT,
                    "yes"
            );

            transformer.setOutputProperty(
                    OutputKeys.METHOD,
                    "xml"
            );

            transformer.setOutputProperty(
                    OutputKeys.OMIT_XML_DECLARATION,
                    "no"
            );

            transformer.setOutputProperty(
                    OutputKeys.STANDALONE,
                    "yes"
            );

            /*
             * Algunas implementaciones no soportan indent-amount.
             * Por eso se coloca dentro de un try separado.
             */
            try {
                transformer.setOutputProperty(
                        "{http://xml.apache.org/xslt}indent-amount",
                        "4"
                );
            } catch (IllegalArgumentException ignored) {
                // El XML se genera aunque no soporte esta propiedad.
            }

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(outputStream)
            );

        } catch (Exception exception) {
            throw new RuntimeException(
                    "No fue posible generar el reporte XML.",
                    exception
            );
        }
    }

    /* ==========================================================
       ENCABEZADO
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param document valor de document requerido por la operación
     * @param root valor de root requerido por la operación
     * @param report valor de report requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static void appendHeader(Document document,Element root,MovementReport report){
        Element header=document.createElement("header");
        root.appendChild(header);

        appendTextElement(document,header,"systemName","Sistema Gestor de Almacén");
        appendTextElement(document,header,"title",safe(report.getTitle()));
        appendTextElement(document,header,"reportType",safe(report.getReportType()));
        appendTextElement(document,header,"period",safe(report.getPeriod()));
        appendTextElement(document,header,"startDate",formatDate(report.getStartDate()));
        appendTextElement(document,header,"endDate",formatDate(report.getEndDate()));
        appendTextElement(document,header,"generatedAt",report.getGeneratedAt()==null?"":report.getGeneratedAt().format(DATE_TIME_FORMATTER));
        appendTextElement(document,header,"generatedBy",safe(report.getGeneratedBy()));
    }

    /* ==========================================================
       RESUMEN
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param document valor de document requerido por la operación
     * @param root valor de root requerido por la operación
     * @param report valor de report requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static void appendSummary(Document document,Element root,MovementReport report){
        Element summary=document.createElement("summary");
        root.appendChild(summary);

        appendTextElement(document,summary,"movementCount",String.valueOf(report.getMovementCount()));
        appendTextElement(document,summary,"entryCount",String.valueOf(report.getEntryCount()));
        appendTextElement(document,summary,"exitCount",String.valueOf(report.getExitCount()));
        appendTextElement(document,summary,"totalProductsQuantity",String.valueOf(report.getTotalProductsQuantity()));
        appendTextElement(document,summary,"grandTotal",formatDecimal(report.getGrandTotal()));
    }

    /* ==========================================================
       MOVIMIENTOS
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param document valor de document requerido por la operación
     * @param root valor de root requerido por la operación
     * @param report valor de report requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static void appendMovements(Document document,Element root,MovementReport report){
        Element movementsElement=document.createElement("movements");
        root.appendChild(movementsElement);

        if(report.getMovements()==null||report.getMovements().isEmpty()){
            movementsElement.setAttribute("empty","true");
            return;
        }

        movementsElement.setAttribute("empty","false");
        movementsElement.setAttribute("count",String.valueOf(report.getMovementCount()));

        for(ReportMovement movement:report.getMovements()){
            if(movement==null){
                continue;
            }

            Element movementElement=document.createElement("movement");
            movementElement.setAttribute("id",String.valueOf(movement.getMovementId()));
            movementElement.setAttribute("type",safe(movement.getType()));
            movementsElement.appendChild(movementElement);

            appendTextElement(document,movementElement,"folio",safe(movement.getFolio()));
            appendTextElement(document,movementElement,"invoiceNumber",safe(movement.getInvoiceNumber()));
            appendTextElement(document,movementElement,"changeDate",formatTimestamp(movement.getChangeDate()));
            appendTextElement(document,movementElement,"userId",String.valueOf(movement.getUserId()));
            appendTextElement(document,movementElement,"userName",safe(movement.getUserName()));

            Element destination=document.createElement("destination");
            movementElement.appendChild(destination);

            appendTextElement(document,destination,"type",safe(movement.getDestinationType()));
            appendTextElement(document,destination,"name",safe(movement.getDestinationName()));
            appendTextElement(document,destination,"buyerName",safe(movement.getBuyerName()));

            appendTextElement(document,movementElement,"total",formatDecimal(movement.getTotal()));

            appendDetails(document,movementElement,movement);
        }
    }

    /* ==========================================================
       DETALLES
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param document valor de document requerido por la operación
     * @param movementElement valor de movementElement requerido por la operación
     * @param movement valor de movement requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static void appendDetails(Document document,Element movementElement,ReportMovement movement){
        Element detailsElement=document.createElement("details");
        movementElement.appendChild(detailsElement);

        if(movement.getDetails()==null||movement.getDetails().isEmpty()){
            detailsElement.setAttribute("empty","true");
            return;
        }

        detailsElement.setAttribute("empty","false");
        detailsElement.setAttribute("count",String.valueOf(movement.getDetailCount()));
        detailsElement.setAttribute("totalQuantity",String.valueOf(movement.getTotalQuantity()));

        for(ReportProductDetail detail:movement.getDetails()){
            if(detail==null){
                continue;
            }

            Element detailElement=document.createElement("detail");
            detailElement.setAttribute("productId",String.valueOf(detail.getProductId()));
            detailsElement.appendChild(detailElement);

            appendTextElement(document,detailElement,"productCode",safe(detail.getProductCode()));
            appendTextElement(document,detailElement,"productName",safe(detail.getProductName()));
            appendTextElement(document,detailElement,"metricName",safe(detail.getMetricName()));
            appendTextElement(document,detailElement,"quantity",String.valueOf(detail.getQuantity()));
            appendTextElement(document,detailElement,"unitPrice",formatDecimal(detail.getUnitPrice()));
            appendTextElement(document,detailElement,"subtotal",formatDecimal(detail.getSubtotal()));
        }
    }

    /* ==========================================================
       FUNCIONES AUXILIARES
       ========================================================== */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param document valor de document requerido por la operación
     * @param parent valor de parent requerido por la operación
     * @param name valor de name requerido por la operación
     * @param value valor de value requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static void appendTextElement(Document document,Element parent,String name,String value){
        Element element=document.createElement(name);
        element.appendChild(document.createTextNode(value==null?"":value));
        parent.appendChild(element);
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
    private static String safe(String value){
        return value==null?"":value.trim();
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param date valor de date requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static String formatDate(java.time.LocalDate date){
        return date==null?"":date.format(DATE_FORMATTER);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param timestamp valor de timestamp requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private static String formatTimestamp(Timestamp timestamp){
        return timestamp==null?"":timestamp.toLocalDateTime().format(DATE_TIME_FORMATTER);
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
    private static String formatDecimal(BigDecimal value){
        return value==null?"0.00":value.setScale(2,java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
