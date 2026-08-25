package com.almacen.integradora.models.passwordreset;

import com.almacen.integradora.models.OracleDaoTestSupport;
import com.almacen.integradora.models.user.User;
import com.almacen.integradora.models.user.UserDao;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas del ciclo de vida de un token de recuperación.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
class PasswordResetTokenDaoTest extends OracleDaoTestSupport {
    /** Verifica creación, consulta vigente, consumo e invalidación efectiva.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Test
    void cicloDeVidaDelToken() {
        String token = "junit-" + Long.toString(System.nanoTime(), 36);
        UserDao userDao = new UserDao();
        User user = new User(null, "JUnit", "Token", "DAO", null, token + "@example.test",
                "Temporal123!", "Almacenista", 1, 0, null);
        assertTrue(userDao.create(user));
        User createdUser = userDao.findAnyByEmail(user.getEmail());

        PasswordResetTokenDao dao = new PasswordResetTokenDao();
        assertTrue(dao.create(createdUser.getId(), token, LocalDateTime.now().plusMinutes(10)));
        PasswordResetToken created = dao.findValidToken(token);
        assertNotNull(created);
        assertTrue(dao.markAsUsed(created.getIdToken()));
        assertNull(dao.findValidToken(token));
        assertTrue(userDao.updateStatus(createdUser.getId().intValue(), 0));
    }
}
