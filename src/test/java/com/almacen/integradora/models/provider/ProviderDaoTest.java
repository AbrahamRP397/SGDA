package com.almacen.integradora.models.provider;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de persistencia y baja lógica para {@link ProviderDao}.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class ProviderDaoTest extends OracleDaoTestSupport {
    /** Valida el ciclo del proveedor sin eliminar físicamente su historial.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void crudCompleto() {
        ProviderDao dao = new ProviderDao();
        String token = Long.toString(System.nanoTime(), 36).toUpperCase();
        String rfcToken = token.substring(Math.max(0, token.length() - 7));
        String rfc = "TST" + rfcToken + "000";
        Provider provider = new Provider(null, "Proveedor " + token, rfc, "7771000000",
                "proveedor." + token.toLowerCase() + "@example.test", "Contacto", "Direccion", "62000",
                "Proveedor test", "7771000001", "contacto." + token.toLowerCase() + "@example.test", 1);

        assertTrue(dao.create(provider));
        Provider created = dao.findAnyByEmail(provider.getEmail());
        assertNotNull(created);
        assertNotNull(dao.getById(created.getIdProvider().intValue()));

        created.setName("Proveedor actualizado " + token);
        assertTrue(dao.update(created));
        assertEquals(created.getName(), dao.getById(created.getIdProvider().intValue()).getName());

        assertTrue(dao.changeStatus(created.getIdProvider(), 0));
        assertEquals(0, dao.getById(created.getIdProvider().intValue()).getStatus());
        assertTrue(dao.changeStatus(created.getIdProvider(), 1));
        assertEquals(1, dao.getById(created.getIdProvider().intValue()).getStatus());
    }
}
