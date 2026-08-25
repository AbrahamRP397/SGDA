package com.almacen.integradora.utils;

import java.util.regex.Pattern;
/**
 * Define PasswordPolicy y centraliza las responsabilidades técnicas de este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public final class PasswordPolicy{
    private static final int MIN_LENGTH=8;
    private static final int MAX_LENGTH=72;

    private static final Pattern PASSWORD_PATTERN=
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,72}$");

    /**
     * Construye una instancia con los datos necesarios para representar este componente.
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    private PasswordPolicy(){
    }

    /**
     * Evalúa la condición indicada para el estado actual.
     *
     * @param password contraseña que se procesará de forma segura
     * @return resultado producido por la operación
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    public static boolean isValid(String password){
        return password!=null
                &&PASSWORD_PATTERN.matcher(password).matches();
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
    public static String getValidationMessage(){
        return "La contraseña debe tener entre "
                +MIN_LENGTH
                +" y "
                +MAX_LENGTH
                +" caracteres e incluir letras y números.";
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
    public static int getMinLength(){
        return MIN_LENGTH;
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
    public static int getMaxLength(){
        return MAX_LENGTH;
    }
}
