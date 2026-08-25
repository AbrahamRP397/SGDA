package com.almacen.integradora.models.stock;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Pruebas de acceso a la proyección consolidada del inventario.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class StockDaoTest extends OracleDaoTestSupport {
    /** Confirma el contrato de colección aun cuando no existan existencias.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void consultaInventario() {
        assertNotNull(new StockDao().getAllStock());
    }
}
