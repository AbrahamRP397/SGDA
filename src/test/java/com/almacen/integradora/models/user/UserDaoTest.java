package com.almacen.integradora.models.user;

import com.almacen.integradora.models.OracleDaoTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del ciclo funcional de usuarios mediante baja lógica.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class UserDaoTest extends OracleDaoTestSupport {
    /** No usa delete: valida el flujo real de desactivación y reactivación.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void crudCompleto() {
        UserDao dao = new UserDao();
        String token = Long.toString(System.nanoTime(), 36).toLowerCase();
        User user = new User(null, "JUnit", "Prueba", "DAO", "7771000000",
                "junit." + token + "@example.test", "Temporal123!", "Almacenista", 1, 0, null);

        assertTrue(dao.create(user));
        User created = dao.findAnyByEmail(user.getEmail());
        assertNotNull(created);
        assertNotNull(dao.getById(created.getId().intValue()));

        created.setPhone("7771000099");
        assertTrue(dao.update(created));
        assertEquals("7771000099", dao.getById(created.getId().intValue()).getPhone());

        assertTrue(dao.updateStatus(created.getId().intValue(), 0));
        assertEquals(0, dao.getById(created.getId().intValue()).getStatus());
        assertTrue(dao.updateStatus(created.getId().intValue(), 1));
        assertEquals(1, dao.getById(created.getId().intValue()).getStatus());
    }
}
