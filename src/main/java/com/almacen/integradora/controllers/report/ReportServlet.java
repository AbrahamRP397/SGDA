package com.almacen.integradora.controllers.report;

import com.almacen.integradora.models.report.MovementReport;
import com.almacen.integradora.models.report.ReportDao;
import com.almacen.integradora.models.user.User;
import com.almacen.integradora.utils.PdfReportGenerator;
import com.almacen.integradora.utils.XmlReportGenerator;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
/**
 * Define ReportServlet y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
@WebServlet(
        name="ReportServlet",
        urlPatterns={
                "/report/xml",
                "/report/pdf"
        }
)
/** Controlador de exportación de reportes de movimientos en PDF y XML.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
public class ReportServlet extends HttpServlet{
    private ReportDao reportDao;
    private Gson gson;

    /**
     * Inicializa los recursos y dependencias necesarios para el componente.
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    public void init(){
        reportDao=new ReportDao();
        gson=new Gson();
    }

    /**
     * Atiende solicitudes HTTP GET y prepara la respuesta correspondiente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    )throws IOException{
        disableCache(response);

        switch(request.getServletPath()){
            case "/report/xml"->generateReport(request,response,"xml");
            case "/report/pdf"->generateReport(request,response,"pdf");
            default->sendJsonError(
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    "La ruta de reporte solicitada no existe."
            );
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param format valor de format requerido por la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void generateReport(
            HttpServletRequest request,
            HttpServletResponse response,
            String format
    )throws IOException{
        String type=normalizeType(
                request.getParameter("type")
        );

        String period=normalizePeriod(
                request.getParameter("period")
        );

        String generatedBy=
                getGeneratedBy(request);

        try{
            MovementReport report=createReport(
                    request,
                    type,
                    period,
                    generatedBy
            );

            if(report==null){
                throw new IllegalStateException(
                        "No fue posible construir el reporte."
                );
            }

            ByteArrayOutputStream buffer=
                    new ByteArrayOutputStream();

            if("pdf".equals(format)){
                PdfReportGenerator.generate(
                        report,
                        buffer
                );
            }else{
                XmlReportGenerator.generate(
                        report,
                        buffer
                );
            }

            byte[] fileContent=
                    buffer.toByteArray();

            if(fileContent.length==0){
                throw new IllegalStateException(
                        "El generador produjo un archivo vacío."
                );
            }

            String fileName=
                    buildFileName(
                            report,
                            format
                    );

            configureResponse(
                    response,
                    fileName,
                    format,
                    fileContent.length
            );

            try(OutputStream outputStream=
                        response.getOutputStream()){
                outputStream.write(fileContent);
                outputStream.flush();
            }

        }catch(InvalidDateRangeException exception){
            getServletContext().log(
                    "Rango de fechas inválido al generar reporte.",
                    exception
            );

            if(!response.isCommitted()){
                sendJsonError(
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        getDateErrorMessage(
                                exception.getMessageCode()
                        )
                );
            }

        }catch(RuntimeException exception){
            getServletContext().log(
                    "Error al generar el reporte "
                            +format.toUpperCase(Locale.ROOT)+".",
                    exception
            );

            if(!response.isCommitted()){
                sendJsonError(
                        response,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "No fue posible generar el reporte. Revisa los datos e intenta nuevamente."
                );
            }else{
                throw exception;
            }
        }
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @param type valor de type requerido por la operación
     * @param period valor de period requerido por la operación
     * @param generatedBy valor de generatedBy requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private MovementReport createReport(
            HttpServletRequest request,
            String type,
            String period,
            String generatedBy
    ){
        if(!"custom".equals(period)){
            return reportDao.getMovementReport(
                    type,
                    period,
                    generatedBy
            );
        }

        LocalDate startDate=
                parseDate(
                        request.getParameter(
                                "startDate"
                        )
                );

        LocalDate endDate=
                parseDate(
                        request.getParameter(
                                "endDate"
                        )
                );

        validateDateRange(
                startDate,
                endDate
        );

        return reportDao.getMovementReport(
                type,
                startDate,
                endDate,
                generatedBy
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private LocalDate parseDate(
            String value
    ){
        if(value==null
                ||value.isBlank()){
            return null;
        }

        try{
            return LocalDate.parse(
                    value.trim()
            );

        }catch(DateTimeParseException exception){
            throw new InvalidDateRangeException(
                    "invalid-report-date"
            );
        }
    }

    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @param startDate valor de startDate requerido por la operación
     * @param endDate valor de endDate requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
    ){
        if(startDate==null||endDate==null){
            throw new InvalidDateRangeException(
                    "empty-report-dates"
            );
        }

        if(endDate.isBefore(startDate)){
            throw new InvalidDateRangeException(
                    "invalid-report-range"
            );
        }

        if(startDate.isAfter(LocalDate.now())
                ||endDate.isAfter(LocalDate.now())){
            throw new InvalidDateRangeException(
                    "future-report-date"
            );
        }

        if(startDate.plusYears(10)
                .isBefore(endDate)){
            throw new InvalidDateRangeException(
                    "large-report-range"
            );
        }
    }

    /**
     * Construye o envía la respuesta requerida por el cliente HTTP.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param fileName valor de fileName requerido por la operación
     * @param format valor de format requerido por la operación
     * @param contentLength valor de contentLength requerido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void configureResponse(
            HttpServletResponse response,
            String fileName,
            String format,
            int contentLength
    ){
        response.reset();

        response.setStatus(
                HttpServletResponse.SC_OK
        );

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );

        response.setHeader(
                "X-Content-Type-Options",
                "nosniff"
        );

        if("pdf".equals(format)){
            response.setContentType(
                    "application/pdf"
            );
        }else{
            response.setContentType(
                    "application/xml"
            );

            response.setCharacterEncoding(
                    StandardCharsets.UTF_8.name()
            );
        }

        response.setHeader(
                "Content-Disposition",
                buildContentDisposition(
                        fileName
                )
        );

        response.setContentLength(
                contentLength
        );
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param request solicitud HTTP recibida por el servlet
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String getGeneratedBy(
            HttpServletRequest request
    ){
        HttpSession session=
                request.getSession(false);

        if(session==null){
            return "Usuario del sistema";
        }

        Object fullName=
                session.getAttribute(
                        "nombreCompleto"
                );

        if(fullName!=null
                &&!fullName.toString()
                .isBlank()){
            return fullName.toString()
                    .trim();
        }

        Object sessionUser=
                session.getAttribute(
                        "usuario"
                );

        if(sessionUser instanceof User user){
            String generatedBy=
                    String.join(
                                    " ",
                                    safeText(user.getName()),
                                    safeText(user.getSurname()),
                                    safeText(user.getLastname())
                            )
                            .trim()
                            .replaceAll(
                                    "\\s+",
                                    " "
                            );

            if(!generatedBy.isBlank()){
                return generatedBy;
            }

            if(user.getEmail()!=null
                    &&!user.getEmail()
                    .isBlank()){
                return user.getEmail()
                        .trim();
            }
        }

        return "Usuario del sistema";
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param report valor de report requerido por la operación
     * @param extension valor de extension requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildFileName(
            MovementReport report,
            String extension
    ){
        String type=
                normalizeFilePart(
                        report.getReportType()
                );

        String period=
                normalizeFilePart(
                        report.getPeriod()
                );

        String datePart;

        if("custom".equals(
                report.getPeriod()
        )){
            datePart=
                    report.getStartDate()
                            .format(
                                    DateTimeFormatter.ISO_LOCAL_DATE
                            )
                            +"-a-"
                            +report.getEndDate()
                            .format(
                                    DateTimeFormatter.ISO_LOCAL_DATE
                            );
        }else{
            datePart=
                    LocalDate.now().format(
                            DateTimeFormatter.ISO_LOCAL_DATE
                    );
        }

        return "reporte-"
                +type
                +"-"
                +period
                +"-"
                +datePart
                +"."
                +extension;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizeFilePart(
            String value
    ){
        if(value==null
                ||value.isBlank()){
            return "general";
        }

        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll(
                        "[^a-z0-9_-]",
                        "-"
                )
                .replaceAll(
                        "-+",
                        "-"
                );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param fileName valor de fileName requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String buildContentDisposition(
            String fileName
    ){
        String encodedFileName=
                URLEncoder.encode(
                                fileName,
                                StandardCharsets.UTF_8
                        )
                        .replace(
                                "+",
                                "%20"
                        );

        return "attachment; filename=\""
                +fileName
                +"\"; filename*=UTF-8''"
                +encodedFileName;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param type valor de type requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizeType(
            String type
    ){
        if(type==null){
            return "movements";
        }

        return switch(
                type.trim()
                        .toLowerCase(Locale.ROOT)
                ){
            case "entries"->"entries";
            case "exits"->"exits";
            default->"movements";
        };
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param period valor de period requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String normalizePeriod(
            String period
    ){
        if(period==null){
            return "monthly";
        }

        return switch(
                period.trim()
                        .toLowerCase(Locale.ROOT)
                ){
            case "daily"->"daily";
            case "weekly"->"weekly";
            case "annual"->"annual";
            case "custom"->"custom";
            default->"monthly";
        };
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param code valor de code requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String getDateErrorMessage(
            String code
    ){
        if(code==null){
            return "El rango de fechas no es válido.";
        }

        return switch(code){
            case "empty-report-dates"->
                    "Selecciona la fecha inicial y final.";

            case "invalid-report-range"->
                    "La fecha final no puede ser anterior a la fecha inicial.";

            case "future-report-date"->
                    "No puedes generar reportes utilizando fechas futuras.";

            case "large-report-range"->
                    "El rango seleccionado es demasiado amplio.";

            case "invalid-report-date"->
                    "Una de las fechas seleccionadas no es válida.";

            default->
                    "El rango de fechas no es válido.";
        };
    }

    /**
     * Construye o envía la respuesta requerida por el cliente HTTP.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
     * @param status estado que se utilizará en la operación
     * @param message valor de message requerido por la operación
     * @throws IOException si no puede completarse la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void sendJsonError(
            HttpServletResponse response,
            int status,
            String message
    )throws IOException{
        if(response.isCommitted()){
            return;
        }

        response.reset();

        response.setStatus(status);
        response.setContentType(
                "application/json"
        );

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name()
        );

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );

        Map<String,Object> result=
                new LinkedHashMap<>();

        result.put(
                "success",
                false
        );

        result.put(
                "type",
                "error"
        );

        result.put(
                "message",
                message
        );

        response.getWriter().write(
                gson.toJson(result)
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param response respuesta HTTP donde se escribirá el resultado
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private void disableCache(
            HttpServletResponse response
    ){
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setDateHeader(
                "Expires",
                0
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
    private String safeText(
            String value
    ){
        return value==null
                ?""
                :value.trim();
    }

    private static final class InvalidDateRangeException
            extends RuntimeException{
        private final String messageCode;

        private InvalidDateRangeException(
                String messageCode
        ){
            super(messageCode);
            this.messageCode=messageCode;
        }

        /**
         * Consulta y devuelve la información solicitada por los criterios recibidos.
         *
         * @return resultado producido por la operación
 *
 *
 * @author Abraham Ríos Peña
 * @since 2026-08-24
 */
        private String getMessageCode(){
            return messageCode;
        }
    }
}
