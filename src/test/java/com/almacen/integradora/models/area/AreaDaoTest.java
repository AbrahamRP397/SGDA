package com.almacen.integradora.models.area;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de persistencia y baja lógica para {@link AreaDao}.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class AreaDaoTest extends OracleDaoTestSupport {
    /** Recorre alta, lectura, modificación, desactivación y reactivación.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void crudCompleto() {
        AreaDao dao = new AreaDao();
        String token = Long.toString(System.nanoTime(), 36).toUpperCase();
        Area area = new Area(null, "T" + token.substring(0, Math.min(5, token.length())), "Area test " + token, "Creada por JUnit", 1);

        assertTrue(dao.create(area));
        Area created = dao.findAnyByName(area.getName());
        assertNotNull(created);
        assertNotNull(dao.getById(created.getIdArea().intValue()));

        created.setDescription("Actualizada por JUnit");
        assertTrue(dao.update(created));
        assertEquals("Actualizada por JUnit", dao.getById(created.getIdArea().intValue()).getDescription());

        assertTrue(dao.changeStatus(created.getIdArea(), 0));
        assertEquals(0, dao.getById(created.getIdArea().intValue()).getStatus());
        assertTrue(dao.changeStatus(created.getIdArea(), 1));
        assertEquals(1, dao.getById(created.getIdArea().intValue()).getStatus());
    }
}
