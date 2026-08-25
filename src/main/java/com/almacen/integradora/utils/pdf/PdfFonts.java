package com.almacen.integradora.utils.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import java.awt.Color;
/**
 * Define PdfFonts y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public final class PdfFonts {

    private static final String DEFAULT_FONT=FontFactory.HELVETICA;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private PdfFonts(){
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
    public static Font documentTitle(){
        return create(18,Font.BOLD,PdfColors.TEXT);
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
    public static Font systemTitle(){
        return create(13,Font.BOLD,PdfColors.WHITE);
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
    public static Font sectionTitle(){
        return create(12,Font.BOLD,PdfColors.TEXT);
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
    public static Font subtitle(){
        return create(10,Font.NORMAL,PdfColors.TEXT_SECONDARY);
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
    public static Font normal(){
        return create(9,Font.NORMAL,PdfColors.TEXT);
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
    public static Font normalBold(){
        return create(9,Font.BOLD,PdfColors.TEXT);
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
    public static Font small(){
        return create(8,Font.NORMAL,PdfColors.TEXT_SECONDARY);
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
    public static Font smallBold(){
        return create(8,Font.BOLD,PdfColors.TEXT);
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
    public static Font tableHeader(){
        return create(8,Font.BOLD,PdfColors.WHITE);
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
    public static Font tableCell(){
        return create(8,Font.NORMAL,PdfColors.TEXT);
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
    public static Font tableCellBold(){
        return create(8,Font.BOLD,PdfColors.TEXT);
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
    public static Font total(){
        return create(10,Font.BOLD,PdfColors.TEXT);
    }

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param size valor de size requerido por la operación
     * @param style valor de style requerido por la operación
     * @param color valor de color requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public static Font create(float size,int style,Color color){
        return FontFactory.getFont(DEFAULT_FONT,size,style,color);
    }
}
