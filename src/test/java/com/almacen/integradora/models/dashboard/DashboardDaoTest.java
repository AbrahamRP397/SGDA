package com.almacen.integradora.models.dashboard;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Pruebas de las consultas agregadas consumidas por el tablero.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class DashboardDaoTest extends OracleDaoTestSupport {
    /** Verifica colecciones utilizables incluso en una base vacía.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void consultasDelDashboard() {
        DashboardDao dao = new DashboardDao();
        assertNotNull(dao.getRecentMovements());
        assertNotNull(dao.getMostMovedProducts());
        assertNotNull(dao.getLeastMovedProducts());
        assertNotNull(dao.getProductsWithMostStock());
    }
}
