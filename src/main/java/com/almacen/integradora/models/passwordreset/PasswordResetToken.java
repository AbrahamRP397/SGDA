package com.almacen.integradora.models.passwordreset;

import java.time.LocalDateTime;

/** Token temporal de recuperación asociado con un usuario.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class PasswordResetToken {

    private long idToken;
    private long idUser;
    private String token;
    private LocalDateTime expiration;
    private boolean used;
    private LocalDateTime createdAt;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public PasswordResetToken() {
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public long getIdToken() {
        return idToken;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idToken identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdToken(long idToken) {
        this.idToken = idToken;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public long getIdUser() {
        return idUser;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idUser identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public String getToken() {
        return token;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param token token utilizado para validar la solicitud
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public LocalDateTime getExpiration() {
        return expiration;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param expiration valor de expiration requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean isUsed() {
        return used;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param used valor de used requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param createdAt valor de createdAt requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
