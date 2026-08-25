package com.almacen.integradora.models.exit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Encabezado de una salida de almacén con destino, responsable y detalles.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class Exit {
    private Long idExit;
    private LocalDateTime changeDate;
    private String invoiceNumber;
    private String folioNumber;
    private Long idUser;
    private String userName;
    private Long idArea;
    private String areaName;
    private String areaShortName;
    private String buyerName;
    private BigDecimal totalAllPrices;
    private List<ExitProduct> products = new ArrayList<>();

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Exit() {
    }

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
     *
     * @param idExit identificador del registro relacionado con la operación
     * @param changeDate valor de changeDate requerido por la operación
     * @param invoiceNumber valor de invoiceNumber requerido por la operación
     * @param folioNumber valor de folioNumber requerido por la operación
     * @param idUser identificador del registro relacionado con la operación
     * @param userName valor de userName requerido por la operación
     * @param idArea identificador del registro relacionado con la operación
     * @param areaName valor de areaName requerido por la operación
     * @param areaShortName valor de areaShortName requerido por la operación
     * @param buyerName valor de buyerName requerido por la operación
     * @param totalAllPrices valor de totalAllPrices requerido por la operación
     * @param products valor de products requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public Exit(Long idExit, LocalDateTime changeDate, String invoiceNumber,
                String folioNumber, Long idUser, String userName, Long idArea,
                String areaName, String areaShortName, String buyerName,
                BigDecimal totalAllPrices, List<ExitProduct> products) {
        this.idExit = idExit;
        this.changeDate = changeDate;
        this.invoiceNumber = invoiceNumber;
        this.folioNumber = folioNumber;
        this.idUser = idUser;
        this.userName = userName;
        this.idArea = idArea;
        this.areaName = areaName;
        this.areaShortName = areaShortName;
        this.buyerName = buyerName;
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
    public Long getIdExit() {
        return idExit;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idExit identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdExit(Long idExit) {
        this.idExit = idExit;
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
    public Long getIdArea() {
        return idArea;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param idArea identificador del registro relacionado con la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setIdArea(Long idArea) {
        this.idArea = idArea;
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
    public String getAreaName() {
        return areaName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param areaName valor de areaName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
    public String getAreaShortName() {
        return areaShortName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param areaShortName valor de areaShortName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setAreaShortName(String areaShortName) {
        this.areaShortName = areaShortName;
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
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * Actualiza el valor de la propiedad indicada.
     *
     * @param buyerName valor de buyerName requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
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
    public List<ExitProduct> getProducts() {
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
    public void setProducts(List<ExitProduct> products) {
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
    public void addProduct(ExitProduct product) {
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
                .map(ExitProduct::getTotalPrice)
                .filter(total -> total != null)
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
        return "Exit{" +
                "idExit=" + idExit +
                ", changeDate=" + changeDate +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", folioNumber='" + folioNumber + '\'' +
                ", idUser=" + idUser +
                ", userName='" + userName + '\'' +
                ", idArea=" + idArea +
                ", areaName='" + areaName + '\'' +
                ", areaShortName='" + areaShortName + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", totalAllPrices=" + totalAllPrices +
                ", products=" + products +
                '}';
    }
}
