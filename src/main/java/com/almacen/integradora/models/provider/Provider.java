package com.almacen.integradora.models.provider;

/** Entidad de proveedor y sus datos comerciales y de contacto.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class Provider {

    private Long idProvider;
    private String name;
    private String rfc;
    private String phone;
    private String email;
    private String contactName;
    private String address;
    private String postCode;
    private String socialCase;
    private String contactPhone;
    private String contactEmail;
    private Integer status;

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Provider() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idProvider identificador del registro relacionado con la operación
     * @param name valor de name requerido por la operación
     * @param rfc valor de rfc requerido por la operación
     * @param phone valor de phone requerido por la operación
     * @param email dirección de correo asociada a la operación
     * @param contactName valor de contactName requerido por la operación
     * @param address valor de address requerido por la operación
     * @param postCode valor de postCode requerido por la operación
     * @param socialCase valor de socialCase requerido por la operación
     * @param contactPhone valor de contactPhone requerido por la operación
     * @param contactEmail dirección de correo asociada a la operación
     * @param status estado que se utilizará en la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Provider(Long idProvider, String name, String rfc, String phone, String email,
                    String contactName, String address, String postCode, String socialCase,
                    String contactPhone, String contactEmail, Integer status) {
        this.idProvider = idProvider;
        this.name = name;
        this.rfc = rfc;
        this.phone = phone;
        this.email = email;
        this.contactName = contactName;
        this.address = address;
        this.postCode = postCode;
        this.socialCase = socialCase;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
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
    public Long getIdProvider() {
        return idProvider;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idProvider identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdProvider(Long idProvider) {
        this.idProvider = idProvider;
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
    public String getRfc() {
        return rfc;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param rfc valor de rfc requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setRfc(String rfc) {
        this.rfc = rfc;
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
    public String getContactName() {
        return contactName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param contactName valor de contactName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setContactName(String contactName) {
        this.contactName = contactName;
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
    public String getAddress() {
        return address;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param address valor de address requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setAddress(String address) {
        this.address = address;
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
    public String getPostCode() {
        return postCode;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param postCode valor de postCode requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setPostCode(String postCode) {
        this.postCode = postCode;
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
    public String getSocialCase() {
        return socialCase;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param socialCase valor de socialCase requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setSocialCase(String socialCase) {
        this.socialCase = socialCase;
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
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param contactPhone valor de contactPhone requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param contactEmail dirección de correo asociada a la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
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
     * Evalúa la condición indicada para el estado actual.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public boolean isActive() {
        return status != null && status == 1;
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
        return "Provider{" +
                "idProvider=" + idProvider +
                ", name='" + name + '\'' +
                ", rfc='" + rfc + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", contactName='" + contactName + '\'' +
                ", address='" + address + '\'' +
                ", postCode='" + postCode + '\'' +
                ", socialCase='" + socialCase + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", status=" + status +
                '}';
    }
}
