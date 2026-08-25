package com.almacen.integradora.models;

import com.almacen.integradora.utils.SQLConnector;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Base común para las pruebas de integración de los DAO contra Oracle.
 *
 * <p>La configuración de pruebas apunta al usuario {@code SGDA_TEST} creado
 * por Docker. La comprobación del esquema evita ejecutar accidentalmente la
 * suite contra la base productiva.</p>
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public abstract class OracleDaoTestSupport {
    /** Confirma que la conexión activa pertenece al esquema aislado de pruebas.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    /**
     * Valida que los datos y condiciones requeridos sean correctos.
     *
     * @throws Exception si no puede completarse la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    @BeforeAll
    static void verifyOracleTestContainer() throws Exception {
        try (Connection connection = SQLConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') FROM DUAL")) {
            result.next();
            assertEquals("SGDA_TEST", result.getString(1));
        }
    }
}
