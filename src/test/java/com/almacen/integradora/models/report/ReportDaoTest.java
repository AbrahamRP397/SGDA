package com.almacen.integradora.models.report;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Pruebas de construcción del modelo de reporte de movimientos.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class ReportDaoTest extends OracleDaoTestSupport {
    /** Confirma que se genere un reporte válido aunque no haya movimientos.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void generaReporteSinMovimientos() {
        assertNotNull(new ReportDao().getMovementReport("all", "month", "JUnit"));
    }
}
