package com.almacen.integradora.models.user;

import java.sql.Timestamp;

/** Entidad de usuario, rol, estado y política de cambio de contraseña.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class User {

    private Long id;
    private String name;
    private String surname;
    private String lastname;
    private String phone;
    private String email;
    private String password;
    private String role;
    private Integer status;
    private Integer mustChangePassword;
    private Timestamp temporaryPasswordExpiration;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public User() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param id identificador del registro relacionado con la operación
     * @param name valor de name requerido por la operación
     * @param surname valor de surname requerido por la operación
     * @param lastname valor de lastname requerido por la operación
     * @param phone valor de phone requerido por la operación
     * @param email dirección de correo asociada a la operación
     * @param password contraseña que se procesará de forma segura
     * @param role valor de role requerido por la operación
     * @param status estado que se utilizará en la operación
     * @param mustChangePassword contraseña que se procesará de forma segura
     * @param temporaryPasswordExpiration contraseña que se procesará de forma segura
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public User(Long id, String name, String surname, String lastname,
                String phone, String email, String password, String role,
                Integer status, Integer mustChangePassword,
                Timestamp temporaryPasswordExpiration) {

        this.id = id;
        this.name = name;
        this.surname = surname;
        this.lastname = lastname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.mustChangePassword = mustChangePassword;
        this.temporaryPasswordExpiration = temporaryPasswordExpiration;
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status=" + status +
                ", mustChangePassword=" + mustChangePassword +
                ", temporaryPasswordExpiration=" + temporaryPasswordExpiration +
                '}';
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
    public Long getId() {
        return id;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param id identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setId(Long id) {
        this.id = id;
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
    public String getName() {
        return name;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param name valor de name requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setName(String name) {
        this.name = name;
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
    public String getSurname() {
        return surname;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param surname valor de surname requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setSurname(String surname) {
        this.surname = surname;
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
    public String getLastname() {
        return lastname;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param lastname valor de lastname requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setLastname(String lastname) {
        this.lastname = lastname;
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
    public String getPhone() {
        return phone;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param phone valor de phone requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setPhone(String phone) {
        this.phone = phone;
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
    public String getEmail() {
        return email;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param email dirección de correo asociada a la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setEmail(String email) {
        this.email = email;
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
    public String getPassword() {
        return password;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param password contraseña que se procesará de forma segura
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setPassword(String password) {
        this.password = password;
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
    public String getRole() {
        return role;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param role valor de role requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setRole(String role) {
        this.role = role;
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
    public Integer getStatus() {
        return status;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param status estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setStatus(Integer status) {
        this.status = status;
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
    public Integer getMustChangePassword() {
        return mustChangePassword;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param mustChangePassword contraseña que se procesará de forma segura
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setMustChangePassword(Integer mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
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
    public Timestamp getTemporaryPasswordExpiration() {
        return temporaryPasswordExpiration;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param temporaryPasswordExpiration contraseña que se procesará de forma segura
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTemporaryPasswordExpiration(Timestamp temporaryPasswordExpiration) {
        this.temporaryPasswordExpiration = temporaryPasswordExpiration;
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean requiresPasswordChange() {
        return Integer.valueOf(1).equals(mustChangePassword);
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
    public boolean isTemporaryPasswordExpired() {
        return temporaryPasswordExpiration != null
                && temporaryPasswordExpiration.before(
                new Timestamp(System.currentTimeMillis())
        );
    }
}
