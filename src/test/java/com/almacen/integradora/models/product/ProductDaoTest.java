package com.almacen.integradora.models.product;

import com.almacen.integradora.models.OracleDaoTestSupport;
import com.almacen.integradora.models.metric.Metric;
import com.almacen.integradora.models.metric.MetricDao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del producto y de su dependencia obligatoria con una métrica.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class ProductDaoTest extends OracleDaoTestSupport {
    /** Valida el ciclo completo y la baja lógica respetando la llave foránea.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void crudCompleto() {
        String token = Long.toString(System.nanoTime(), 36).toUpperCase();
        MetricDao metricDao = new MetricDao();
        Metric metric = new Metric(null, "Metrica producto " + token, "P" + token.substring(0, Math.min(4, token.length())), 1);
        assertTrue(metricDao.create(metric));
        Metric createdMetric = metricDao.findAnyByName(metric.getName());

        ProductDao dao = new ProductDao();
        Product product = new Product(null, "P-" + token, "Producto " + token, createdMetric.getIdMetric(),
                null, null, "Creado por JUnit", 1);
        assertTrue(dao.create(product));
        Product created = dao.findAnyByCode(product.getCode());
        assertNotNull(created);
        assertNotNull(dao.getById(created.getIdProduct().intValue()));

        created.setDescription("Actualizado por JUnit");
        assertTrue(dao.update(created));
        assertEquals("Actualizado por JUnit", dao.getById(created.getIdProduct().intValue()).getDescription());

        assertTrue(dao.changeStatus(created.getIdProduct(), 0));
        assertEquals(0, dao.getById(created.getIdProduct().intValue()).getStatus());
        assertTrue(dao.changeStatus(created.getIdProduct(), 1));
        assertEquals(1, dao.getById(created.getIdProduct().intValue()).getStatus());
        assertTrue(metricDao.changeStatus(createdMetric.getIdMetric(), 0));
    }
}
