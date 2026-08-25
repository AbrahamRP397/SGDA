package com.almacen.integradora.models.exit;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Pruebas iniciales de lectura para movimientos de salida.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class ExitDaoTest extends OracleDaoTestSupport {
    /** Comprueba el contrato de colección cuando no existen salidas.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void consultaSalidas() {
        assertNotNull(new ExitDao().getAll());
    }
}
