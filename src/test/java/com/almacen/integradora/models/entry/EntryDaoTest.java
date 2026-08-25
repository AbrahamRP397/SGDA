package com.almacen.integradora.models.entry;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Pruebas iniciales de lectura para movimientos de entrada.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class EntryDaoTest extends OracleDaoTestSupport {
    /** Comprueba el contrato de colección cuando no existen entradas.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void consultaEntradas() {
        assertNotNull(new EntryDao().getAll());
    }
}
