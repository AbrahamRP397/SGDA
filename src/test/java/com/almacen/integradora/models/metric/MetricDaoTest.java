package com.almacen.integradora.models.metric;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de persistencia y baja lógica para {@link MetricDao}.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class MetricDaoTest extends OracleDaoTestSupport {
    /** Valida alta, consulta, actualización y alternancia del estado.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void crudCompleto() {
        MetricDao dao = new MetricDao();
        String token = Long.toString(System.nanoTime(), 36).toUpperCase();
        Metric metric = new Metric(null, "Metrica test " + token, "M" + token.substring(0, Math.min(4, token.length())), 1);

        assertTrue(dao.create(metric));
        Metric created = dao.findAnyByName(metric.getName());
        assertNotNull(created);
        assertNotNull(dao.getById(created.getIdMetric().intValue()));

        created.setName("Metrica actualizada " + token);
        assertTrue(dao.update(created));
        assertEquals(created.getName(), dao.getById(created.getIdMetric().intValue()).getName());

        assertTrue(dao.changeStatus(created.getIdMetric(), 0));
        assertEquals(0, dao.getById(created.getIdMetric().intValue()).getStatus());
        assertTrue(dao.changeStatus(created.getIdMetric(), 1));
        assertEquals(1, dao.getById(created.getIdMetric().intValue()).getStatus());
    }
}
