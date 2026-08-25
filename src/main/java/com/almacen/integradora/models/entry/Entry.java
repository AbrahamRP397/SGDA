package com.almacen.integradora.models.entry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Encabezado de una entrada de almacén con proveedor y partidas.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class Entry {

    private Long idEntry;
    private LocalDateTime changeDate;
    private String invoiceNumber;
    private String folioNumber;
    private Long idUser;
    private String userName;
    private Long idProvider;
    private String providerName;
    private String providerRfc;
    private BigDecimal totalAllPrices;
    private List<EntryProduct> products = new ArrayList<>();

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Entry() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idEntry identificador del registro relacionado con la operación
     * @param changeDate valor de changeDate requerido por la operación
     * @param invoiceNumber valor de invoiceNumber requerido por la operación
     * @param folioNumber valor de folioNumber requerido por la operación
     * @param idUser identificador del registro relacionado con la operación
     * @param userName valor de userName requerido por la operación
     * @param idProvider identificador del registro relacionado con la operación
     * @param providerName valor de providerName requerido por la operación
     * @param providerRfc valor de providerRfc requerido por la operación
     * @param totalAllPrices valor de totalAllPrices requerido por la operación
     * @param products valor de products requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Entry(
            Long idEntry,
            LocalDateTime changeDate,
            String invoiceNumber,
            String folioNumber,
            Long idUser,
            String userName,
            Long idProvider,
            String providerName,
            String providerRfc,
            BigDecimal totalAllPrices,
            List<EntryProduct> products
    ) {
        this.idEntry = idEntry;
        this.changeDate = changeDate;
        this.invoiceNumber = invoiceNumber;
        this.folioNumber = folioNumber;
        this.idUser = idUser;
        this.userName = userName;
        this.idProvider = idProvider;
        this.providerName = providerName;
        this.providerRfc = providerRfc;
        this.totalAllPrices = totalAllPrices;
        setProducts(products);
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
    public Long getIdEntry() {
        return idEntry;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idEntry identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdEntry(Long idEntry) {
        this.idEntry = idEntry;
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
    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param changeDate valor de changeDate requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
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
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param invoiceNumber valor de invoiceNumber requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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
    public String getFolioNumber() {
        return folioNumber;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param folioNumber valor de folioNumber requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
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
    public Long getIdUser() {
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
    public void setIdUser(Long idUser) {
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
    public String getUserName() {
        return userName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param userName valor de userName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setUserName(String userName) {
        this.userName = userName;
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
    public String getProviderName() {
        return providerName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param providerName valor de providerName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
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
    public String getProviderRfc() {
        return providerRfc;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param providerRfc valor de providerRfc requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProviderRfc(String providerRfc) {
        this.providerRfc = providerRfc;
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
    public BigDecimal getTotalAllPrices() {
        return totalAllPrices;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param totalAllPrices valor de totalAllPrices requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setTotalAllPrices(BigDecimal totalAllPrices) {
        this.totalAllPrices = totalAllPrices;
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
    public List<EntryProduct> getProducts() {
        return products;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param products valor de products requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setProducts(List<EntryProduct> products) {
        this.products = products == null
                ? new ArrayList<>()
                : new ArrayList<>(products);
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param product valor de product requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void addProduct(EntryProduct product) {
        if (product != null) {
            products.add(product);
        }
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
    public int getProductCount() {
        return products == null ? 0 : products.size();
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
    public BigDecimal calculateTotal() {
        if (products == null || products.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return products.stream()
                .map(EntryProduct::calculateTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
        return "Entry{" +
                "idEntry=" + idEntry +
                ", changeDate=" + changeDate +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", folioNumber='" + folioNumber + '\'' +
                ", idUser=" + idUser +
                ", userName='" + userName + '\'' +
                ", idProvider=" + idProvider +
                ", providerName='" + providerName + '\'' +
                ", providerRfc='" + providerRfc + '\'' +
                ", totalAllPrices=" + totalAllPrices +
                ", products=" + products +
                '}';
    }
}
