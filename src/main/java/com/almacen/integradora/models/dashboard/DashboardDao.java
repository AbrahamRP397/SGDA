package com.almacen.integradora.models.dashboard;

import com.almacen.integradora.utils.SQLConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Consultas agregadas y proyecciones del tablero principal.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public class DashboardDao {

    private static final int RECENT_MOVEMENT_LIMIT = 8;
    private static final int PRODUCT_RANKING_LIMIT = 5;

    private static final Set<String> VALID_PERIODS = Set.of(
            "daily",
            "weekly",
            "monthly",
            "annual"
    );

    private static final Locale SPANISH_LOCALE =
            Locale.forLanguageTag("es-MX");

    /*
     * ==========================================================
     * MOVIMIENTOS RECIENTES
     * ==========================================================
     */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<DashboardMovement> getRecentMovements() {

        String sql = """
                SELECT *
                FROM (
                    SELECT
                        movement_id,
                        movement_type,
                        folio_number,
                        change_date,
                        destination_name,
                        responsible_name,
                        product_count,
                        total_quantity
                    FROM (
                        SELECT
                            e.id_entry AS movement_id,
                            'ENTRY' AS movement_type,
                            e.folio_number,
                            e.change_date,
                            pr.name AS destination_name,
                            TRIM(
                                u.name || ' ' ||
                                u.surname || ' ' ||
                                u.lastname
                            ) AS responsible_name,
                            COUNT(ep.id_entry_product) AS product_count,
                            NVL(SUM(ep.quantity), 0) AS total_quantity
                        FROM entries e
                        INNER JOIN users u
                            ON e.id_user = u.id_user
                        INNER JOIN providers pr
                            ON e.id_provider = pr.id_provider
                        LEFT JOIN entry_products ep
                            ON e.id_entry = ep.id_entry
                        GROUP BY
                            e.id_entry,
                            e.folio_number,
                            e.change_date,
                            pr.name,
                            u.name,
                            u.surname,
                            u.lastname

                        UNION ALL

                        SELECT
                            x.id_exit AS movement_id,
                            'EXIT' AS movement_type,
                            x.folio_number,
                            x.change_date,
                            a.name AS destination_name,
                            TRIM(
                                u.name || ' ' ||
                                u.surname || ' ' ||
                                u.lastname
                            ) AS responsible_name,
                            COUNT(xp.id_exit_product) AS product_count,
                            NVL(SUM(xp.quantity), 0) AS total_quantity
                        FROM exits x
                        INNER JOIN users u
                            ON x.id_user = u.id_user
                        INNER JOIN areas a
                            ON x.id_area = a.id_area
                        LEFT JOIN exit_products xp
                            ON x.id_exit = xp.id_exit
                        GROUP BY
                            x.id_exit,
                            x.folio_number,
                            x.change_date,
                            a.name,
                            u.name,
                            u.surname,
                            u.lastname
                    )
                    ORDER BY
                        change_date DESC,
                        movement_id DESC
                )
                WHERE ROWNUM <= ?
                """;

        List<DashboardMovement> movements =
                new ArrayList<>();

        try (
                Connection connection =
                        SQLConnector.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    RECENT_MOVEMENT_LIMIT
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    movements.add(
                            mapDashboardMovement(
                                    resultSet
                            )
                    );
                }
            }

            return movements;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los movimientos recientes.",
                    exception
            );
        }
    }

    /*
     * ==========================================================
     * GRÁFICA
     * ==========================================================
     */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param period valor de period requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<ChartMovement> getMovementsByPeriod(
            String period
    ) {
        String normalizedPeriod =
                normalizePeriod(period);

        return switch (normalizedPeriod) {
            case "daily" ->
                    getDailyMovements();

            case "weekly" ->
                    getWeeklyMovements();

            case "annual" ->
                    getAnnualMovements();

            default ->
                    getMonthlyMovements();
        };
    }

    /*
     * Últimos siete días, incluyendo hoy.
     */
    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ChartMovement> getDailyMovements() {

        LocalDate endDate =
                LocalDate.now();

        LocalDate startDate =
                endDate.minusDays(6);

        LinkedHashMap<String, ChartMovement> periods =
                new LinkedHashMap<>();

        DateTimeFormatter keyFormatter =
                DateTimeFormatter.ISO_LOCAL_DATE;

        DateTimeFormatter labelFormatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM",
                        SPANISH_LOCALE
                );

        for (
                LocalDate date = startDate;
                !date.isAfter(endDate);
                date = date.plusDays(1)
        ) {
            String key =
                    keyFormatter.format(date);

            String label =
                    capitalize(
                            labelFormatter.format(date)
                    );

            periods.put(
                    key,
                    createEmptyChartMovement(
                            key,
                            label
                    )
            );
        }

        String entrySql = """
                SELECT
                    TRUNC(e.change_date) AS period_date,
                    NVL(SUM(ep.quantity), 0) AS total_quantity
                FROM entries e
                INNER JOIN entry_products ep
                    ON e.id_entry = ep.id_entry
                WHERE e.change_date >= ?
                  AND e.change_date < ?
                GROUP BY TRUNC(e.change_date)
                ORDER BY TRUNC(e.change_date)
                """;

        String exitSql = """
                SELECT
                    TRUNC(x.change_date) AS period_date,
                    NVL(SUM(xp.quantity), 0) AS total_quantity
                FROM exits x
                INNER JOIN exit_products xp
                    ON x.id_exit = xp.id_exit
                WHERE x.change_date >= ?
                  AND x.change_date < ?
                GROUP BY TRUNC(x.change_date)
                ORDER BY TRUNC(x.change_date)
                """;

        loadDateQuantities(
                periods,
                entrySql,
                startDate,
                endDate.plusDays(1),
                true
        );

        loadDateQuantities(
                periods,
                exitSql,
                startDate,
                endDate.plusDays(1),
                false
        );

        return new ArrayList<>(
                periods.values()
        );
    }

    /*
     * Últimas ocho semanas.
     *
     * Cada semana comienza en lunes.
     */
    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ChartMovement> getWeeklyMovements() {

        LocalDate currentWeekStart =
                LocalDate.now()
                        .with(
                                TemporalAdjusters.previousOrSame(
                                        DayOfWeek.MONDAY
                                )
                        );

        LocalDate startDate =
                currentWeekStart.minusWeeks(7);

        LocalDate endDateExclusive =
                currentWeekStart.plusWeeks(1);

        LinkedHashMap<String, ChartMovement> periods =
                new LinkedHashMap<>();

        DateTimeFormatter keyFormatter =
                DateTimeFormatter.ISO_LOCAL_DATE;

        DateTimeFormatter shortFormatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM",
                        SPANISH_LOCALE
                );

        for (int index = 0; index < 8; index++) {

            LocalDate weekStart =
                    startDate.plusWeeks(index);

            LocalDate weekEnd =
                    weekStart.plusDays(6);

            String key =
                    keyFormatter.format(weekStart);

            String label =
                    capitalize(
                            shortFormatter.format(
                                    weekStart
                            )
                    )
                            + " - "
                            + capitalize(
                            shortFormatter.format(
                                    weekEnd
                            )
                    );

            periods.put(
                    key,
                    createEmptyChartMovement(
                            key,
                            label
                    )
            );
        }

        String entrySql = """
                SELECT
                    TRUNC(e.change_date, 'IW') AS period_date,
                    NVL(SUM(ep.quantity), 0) AS total_quantity
                FROM entries e
                INNER JOIN entry_products ep
                    ON e.id_entry = ep.id_entry
                WHERE e.change_date >= ?
                  AND e.change_date < ?
                GROUP BY TRUNC(e.change_date, 'IW')
                ORDER BY TRUNC(e.change_date, 'IW')
                """;

        String exitSql = """
                SELECT
                    TRUNC(x.change_date, 'IW') AS period_date,
                    NVL(SUM(xp.quantity), 0) AS total_quantity
                FROM exits x
                INNER JOIN exit_products xp
                    ON x.id_exit = xp.id_exit
                WHERE x.change_date >= ?
                  AND x.change_date < ?
                GROUP BY TRUNC(x.change_date, 'IW')
                ORDER BY TRUNC(x.change_date, 'IW')
                """;

        loadDateQuantities(
                periods,
                entrySql,
                startDate,
                endDateExclusive,
                true
        );

        loadDateQuantities(
                periods,
                exitSql,
                startDate,
                endDateExclusive,
                false
        );

        return new ArrayList<>(
                periods.values()
        );
    }

    /*
     * Últimos doce meses, incluyendo el actual.
     */
    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ChartMovement> getMonthlyMovements() {

        YearMonth currentMonth =
                YearMonth.now();

        YearMonth firstMonth =
                currentMonth.minusMonths(11);

        LocalDate startDate =
                firstMonth.atDay(1);

        LocalDate endDateExclusive =
                currentMonth
                        .plusMonths(1)
                        .atDay(1);

        LinkedHashMap<String, ChartMovement> periods =
                new LinkedHashMap<>();

        DateTimeFormatter keyFormatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM"
                );

        DateTimeFormatter labelFormatter =
                DateTimeFormatter.ofPattern(
                        "MMM yyyy",
                        SPANISH_LOCALE
                );

        for (int index = 0; index < 12; index++) {

            YearMonth month =
                    firstMonth.plusMonths(index);

            LocalDate monthDate =
                    month.atDay(1);

            String key =
                    keyFormatter.format(monthDate);

            String label =
                    capitalize(
                            labelFormatter.format(
                                    monthDate
                            )
                    );

            periods.put(
                    key,
                    createEmptyChartMovement(
                            key,
                            label
                    )
            );
        }

        String entrySql = """
                SELECT
                    TRUNC(e.change_date, 'MM') AS period_date,
                    NVL(SUM(ep.quantity), 0) AS total_quantity
                FROM entries e
                INNER JOIN entry_products ep
                    ON e.id_entry = ep.id_entry
                WHERE e.change_date >= ?
                  AND e.change_date < ?
                GROUP BY TRUNC(e.change_date, 'MM')
                ORDER BY TRUNC(e.change_date, 'MM')
                """;

        String exitSql = """
                SELECT
                    TRUNC(x.change_date, 'MM') AS period_date,
                    NVL(SUM(xp.quantity), 0) AS total_quantity
                FROM exits x
                INNER JOIN exit_products xp
                    ON x.id_exit = xp.id_exit
                WHERE x.change_date >= ?
                  AND x.change_date < ?
                GROUP BY TRUNC(x.change_date, 'MM')
                ORDER BY TRUNC(x.change_date, 'MM')
                """;

        loadMonthlyQuantities(
                periods,
                entrySql,
                startDate,
                endDateExclusive,
                true
        );

        loadMonthlyQuantities(
                periods,
                exitSql,
                startDate,
                endDateExclusive,
                false
        );

        return new ArrayList<>(
                periods.values()
        );
    }

    /*
     * Últimos cinco años, incluyendo el actual.
     */
    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<ChartMovement> getAnnualMovements() {

        int currentYear =
                LocalDate.now().getYear();

        int firstYear =
                currentYear - 4;

        LocalDate startDate =
                LocalDate.of(
                        firstYear,
                        1,
                        1
                );

        LocalDate endDateExclusive =
                LocalDate.of(
                        currentYear + 1,
                        1,
                        1
                );

        LinkedHashMap<String, ChartMovement> periods =
                new LinkedHashMap<>();

        for (
                int year = firstYear;
                year <= currentYear;
                year++
        ) {
            String key =
                    String.valueOf(year);

            periods.put(
                    key,
                    createEmptyChartMovement(
                            key,
                            key
                    )
            );
        }

        String entrySql = """
                SELECT
                    EXTRACT(YEAR FROM e.change_date) AS period_year,
                    NVL(SUM(ep.quantity), 0) AS total_quantity
                FROM entries e
                INNER JOIN entry_products ep
                    ON e.id_entry = ep.id_entry
                WHERE e.change_date >= ?
                  AND e.change_date < ?
                GROUP BY EXTRACT(YEAR FROM e.change_date)
                ORDER BY EXTRACT(YEAR FROM e.change_date)
                """;

        String exitSql = """
                SELECT
                    EXTRACT(YEAR FROM x.change_date) AS period_year,
                    NVL(SUM(xp.quantity), 0) AS total_quantity
                FROM exits x
                INNER JOIN exit_products xp
                    ON x.id_exit = xp.id_exit
                WHERE x.change_date >= ?
                  AND x.change_date < ?
                GROUP BY EXTRACT(YEAR FROM x.change_date)
                ORDER BY EXTRACT(YEAR FROM x.change_date)
                """;

        loadAnnualQuantities(
                periods,
                entrySql,
                startDate,
                endDateExclusive,
                true
        );

        loadAnnualQuantities(
                periods,
                exitSql,
                startDate,
                endDateExclusive,
                false
        );

        return new ArrayList<>(
                periods.values()
        );
    }

    /*
     * ==========================================================
     * PRODUCTOS CON MÁS MOVIMIENTO
     * ==========================================================
     */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<DashboardProduct> getMostMovedProducts() {
        return getMovementProducts(
                false,
                PRODUCT_RANKING_LIMIT
        );
    }

    /*
     * ==========================================================
     * PRODUCTOS CON MENOS MOVIMIENTO
     * ==========================================================
     */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<DashboardProduct> getLeastMovedProducts() {
        return getMovementProducts(
                true,
                PRODUCT_RANKING_LIMIT
        );
    }

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @param ascending valor de ascending requerido por la operación
     * @param limit valor de limit requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private List<DashboardProduct> getMovementProducts(
            boolean ascending,
            int limit
    ) {
        String direction =
                ascending ? "ASC" : "DESC";

        String sql = """
                SELECT *
                FROM (
                    SELECT
                        p.id_product,
                        p.code AS product_code,
                        p.name AS product_name,
                        m.name AS metric_name,
                        m.shortName AS metric_short_name,
                        NVL(en.entry_quantity, 0) AS entry_quantity,
                        NVL(ex.exit_quantity, 0) AS exit_quantity,
                        (
                            NVL(en.entry_quantity, 0)
                            +
                            NVL(ex.exit_quantity, 0)
                        ) AS total_movement
                    FROM products p
                    INNER JOIN metrics m
                        ON p.id_metric = m.id_metric
                    LEFT JOIN (
                        SELECT
                            pp.id_product,
                            SUM(ep.quantity) AS entry_quantity
                        FROM entry_products ep
                        INNER JOIN product_providers pp
                            ON ep.id_product_provider =
                               pp.id_product_provider
                        GROUP BY pp.id_product
                    ) en
                        ON p.id_product = en.id_product
                    LEFT JOIN (
                        SELECT
                            xp.id_product,
                            SUM(xp.quantity) AS exit_quantity
                        FROM exit_products xp
                        GROUP BY xp.id_product
                    ) ex
                        ON p.id_product = ex.id_product
                    WHERE (
                        NVL(en.entry_quantity, 0)
                        +
                        NVL(ex.exit_quantity, 0)
                    ) > 0
                    ORDER BY
                        total_movement %s,
                        UPPER(p.name) ASC
                )
                WHERE ROWNUM <= ?
                """.formatted(direction);

        List<DashboardProduct> products =
                new ArrayList<>();

        try (
                Connection connection =
                        SQLConnector.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    limit
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(
                            mapMovementProduct(
                                    resultSet
                            )
                    );
                }
            }

            return products;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los productos por movimiento.",
                    exception
            );
        }
    }

    /*
     * ==========================================================
     * PRODUCTOS CON MÁS STOCK
     * ==========================================================
     */

    /**
     * Consulta y devuelve la información solicitada por los criterios recibidos.
     *
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public List<DashboardProduct> getProductsWithMostStock() {

        String sql = """
            SELECT *
            FROM (
                SELECT
                    p.id_product,
                    p.code AS product_code,
                    p.name AS product_name,
                    p.status AS product_status,
                    m.name AS metric_name,
                    m.shortName AS metric_short_name,
                    NVL(SUM(s.quantity), 0) AS stock_quantity
                FROM products p
                INNER JOIN metrics m
                    ON p.id_metric = m.id_metric
                INNER JOIN product_providers pp
                    ON p.id_product = pp.id_product
                INNER JOIN stock s
                    ON pp.id_product_provider =
                       s.id_product_provider
                GROUP BY
                    p.id_product,
                    p.code,
                    p.name,
                    p.status,
                    m.name,
                    m.shortName
                HAVING NVL(SUM(s.quantity), 0) > 0
                ORDER BY
                    stock_quantity DESC,
                    UPPER(p.name) ASC
            )
            WHERE ROWNUM <= ?
            """;

        List<DashboardProduct> products =
                new ArrayList<>();

        try (
                Connection connection =
                        SQLConnector.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    PRODUCT_RANKING_LIMIT
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    products.add(
                            mapStockProduct(
                                    resultSet
                            )
                    );
                }
            }

            return products;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los productos con más stock.",
                    exception
            );
        }
    }

    /*
     * ==========================================================
     * MÉTODOS PARA CARGAR DATOS DE LA GRÁFICA
     * ==========================================================
     */

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param periods valor de periods requerido por la operación
     * @param sql valor de sql requerido por la operación
     * @param startDate valor de startDate requerido por la operación
     * @param endDateExclusive valor de endDateExclusive requerido por la operación
     * @param entry valor de entry requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void loadDateQuantities(
            Map<String, ChartMovement> periods,
            String sql,
            LocalDate startDate,
            LocalDate endDateExclusive,
            boolean entry
    ) {
        DateTimeFormatter keyFormatter =
                DateTimeFormatter.ISO_LOCAL_DATE;

        try (
                Connection connection =
                        SQLConnector.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(
                            startDate.atStartOfDay()
                    )
            );

            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            endDateExclusive.atStartOfDay()
                    )
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Date sqlDate =
                            resultSet.getDate(
                                    "period_date"
                            );

                    if (sqlDate == null) {
                        continue;
                    }

                    String key =
                            keyFormatter.format(
                                    sqlDate.toLocalDate()
                            );

                    long quantity =
                            resultSet.getLong(
                                    "total_quantity"
                            );

                    updateChartQuantity(
                            periods,
                            key,
                            quantity,
                            entry
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los movimientos por fecha.",
                    exception
            );
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param periods valor de periods requerido por la operación
     * @param sql valor de sql requerido por la operación
     * @param startDate valor de startDate requerido por la operación
     * @param endDateExclusive valor de endDateExclusive requerido por la operación
     * @param entry valor de entry requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void loadMonthlyQuantities(
            Map<String, ChartMovement> periods,
            String sql,
            LocalDate startDate,
            LocalDate endDateExclusive,
            boolean entry
    ) {
        DateTimeFormatter keyFormatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM"
                );

        try (
                Connection connection =
                        SQLConnector.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(
                            startDate.atStartOfDay()
                    )
            );

            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            endDateExclusive.atStartOfDay()
                    )
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Date sqlDate =
                            resultSet.getDate(
                                    "period_date"
                            );

                    if (sqlDate == null) {
                        continue;
                    }

                    String key =
                            keyFormatter.format(
                                    sqlDate.toLocalDate()
                            );

                    long quantity =
                            resultSet.getLong(
                                    "total_quantity"
                            );

                    updateChartQuantity(
                            periods,
                            key,
                            quantity,
                            entry
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los movimientos mensuales.",
                    exception
            );
        }
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param periods valor de periods requerido por la operación
     * @param sql valor de sql requerido por la operación
     * @param startDate valor de startDate requerido por la operación
     * @param endDateExclusive valor de endDateExclusive requerido por la operación
     * @param entry valor de entry requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void loadAnnualQuantities(
            Map<String, ChartMovement> periods,
            String sql,
            LocalDate startDate,
            LocalDate endDateExclusive,
            boolean entry
    ) {
        try (
                Connection connection =
                        SQLConnector.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setTimestamp(
                    1,
                    Timestamp.valueOf(
                            startDate.atStartOfDay()
                    )
            );

            statement.setTimestamp(
                    2,
                    Timestamp.valueOf(
                            endDateExclusive.atStartOfDay()
                    )
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    int year =
                            resultSet.getInt(
                                    "period_year"
                            );

                    String key =
                            String.valueOf(year);

                    long quantity =
                            resultSet.getLong(
                                    "total_quantity"
                            );

                    updateChartQuantity(
                            periods,
                            key,
                            quantity,
                            entry
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Error al consultar los movimientos anuales.",
                    exception
            );
        }
    }

    /**
     * Actualiza la información correspondiente de acuerdo con los parámetros recibidos.
     *
     * @param periods valor de periods requerido por la operación
     * @param key valor de key requerido por la operación
     * @param quantity valor de quantity requerido por la operación
     * @param entry valor de entry requerido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private void updateChartQuantity(
            Map<String, ChartMovement> periods,
            String key,
            long quantity,
            boolean entry
    ) {
        ChartMovement movement =
                periods.get(key);

        if (movement == null) {
            return;
        }

        if (entry) {
            movement.setEntryQuantity(
                    quantity
            );
        } else {
            movement.setExitQuantity(
                    quantity
            );
        }
    }

    /*
     * ==========================================================
     * MAPEO
     * ==========================================================
     */

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private DashboardMovement mapDashboardMovement(
            ResultSet resultSet
    ) throws SQLException {

        DashboardMovement movement =
                new DashboardMovement();

        movement.setIdMovement(
                resultSet.getLong(
                        "movement_id"
                )
        );

        movement.setMovementType(
                resultSet.getString(
                        "movement_type"
                )
        );

        movement.setFolioNumber(
                resultSet.getString(
                        "folio_number"
                )
        );

        Timestamp timestamp =
                resultSet.getTimestamp(
                        "change_date"
                );

        movement.setChangeDate(
                timestamp == null
                        ? null
                        : timestamp.toLocalDateTime()
        );

        movement.setDestinationName(
                resultSet.getString(
                        "destination_name"
                )
        );

        movement.setResponsibleName(
                resultSet.getString(
                        "responsible_name"
                )
        );

        movement.setProductCount(
                resultSet.getInt(
                        "product_count"
                )
        );

        movement.setTotalQuantity(
                resultSet.getLong(
                        "total_quantity"
                )
        );

        return movement;
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private DashboardProduct mapMovementProduct(
            ResultSet resultSet
    ) throws SQLException {

        DashboardProduct product =
                new DashboardProduct();

        product.setIdProduct(
                resultSet.getLong(
                        "id_product"
                )
        );

        product.setProductCode(
                resultSet.getString(
                        "product_code"
                )
        );

        product.setProductName(
                resultSet.getString(
                        "product_name"
                )
        );

        product.setMetricName(
                resultSet.getString(
                        "metric_name"
                )
        );

        product.setMetricShortName(
                resultSet.getString(
                        "metric_short_name"
                )
        );

        product.setEntryQuantity(
                resultSet.getLong(
                        "entry_quantity"
                )
        );

        product.setExitQuantity(
                resultSet.getLong(
                        "exit_quantity"
                )
        );

        product.setTotalMovement(
                resultSet.getLong(
                        "total_movement"
                )
        );

        product.setStockQuantity(0L);

        return product;
    }

    /**
     * Convierte los datos de entrada al modelo requerido por la aplicación.
     *
     * @param resultSet resultado JDBC posicionado en la fila actual
     * @return resultado producido por la operación
     * @throws SQLException si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private DashboardProduct mapStockProduct(
            ResultSet resultSet
    ) throws SQLException {

        DashboardProduct product =
                new DashboardProduct();

        product.setIdProduct(
                resultSet.getLong(
                        "id_product"
                )
        );

        product.setProductCode(
                resultSet.getString(
                        "product_code"
                )
        );

        product.setProductName(
                resultSet.getString(
                        "product_name"
                )
        );

        product.setProductStatus(
                resultSet.getInt(
                        "product_status"
                )
        );

        product.setMetricName(
                resultSet.getString(
                        "metric_name"
                )
        );

        product.setMetricShortName(
                resultSet.getString(
                        "metric_short_name"
                )
        );

        product.setEntryQuantity(0L);
        product.setExitQuantity(0L);
        product.setTotalMovement(0L);

        product.setStockQuantity(
                resultSet.getLong(
                        "stock_quantity"
                )
        );

        return product;
    }

    /*
     * ==========================================================
     * AUXILIARES
     * ==========================================================
     */

    /**
     * Registra la información recibida y confirma el resultado de la operación.
     *
     * @param key valor de key requerido por la operación
     * @param label valor de label requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private ChartMovement createEmptyChartMovement(
            String key,
            String label
    ) {
        return new ChartMovement(
                key,
                label,
                0L,
                0L
        );
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param period valor de period requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String normalizePeriod(
            String period
    ) {
        if (period == null) {
            return "monthly";
        }

        String normalized =
                period.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return VALID_PERIODS.contains(
                normalized
        )
                ? normalized
                : "monthly";
    }

    /**
     * Ejecuta la operación específica de este componente.
     *
     * @param value valor de value requerido por la operación
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private String capitalize(
            String value
    ) {
        if (value == null
                || value.isBlank()) {
            return "";
        }

        return value.substring(0, 1)
                .toUpperCase(
                        SPANISH_LOCALE
                )
                + value.substring(1);
    }
}
