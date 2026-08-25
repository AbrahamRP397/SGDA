package com.almacen.integradora.templates;

import java.util.List;

/**
 * Contrato base para repositorios con operaciones de persistencia comunes.
 *
 * <p>En las entidades que conservan historial, {@link #delete(Object)} puede
 * representar una baja lógica. Cada implementación debe documentar esa
 * semántica y mantenerla consistente con la interfaz de usuario.</p>
 *
 * @param <T> entidad administrada
 * @param <K> tipo de su identificador
 *
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
public interface Dao<T, K> {
    /**
     * Persiste una entidad nueva.
     *
     * @param entidad entidad que se almacenará
     * @return {@code true} cuando la operación se completa correctamente
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    boolean create(T entidad);

    /**
     * Devuelve todas las entidades visibles para la operación administrativa.
     *
     * @return lista de entidades, posiblemente vacía
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    List<T> getAll();

    /**
     * Busca una entidad por su identificador.
     *
     * @param id identificador de la entidad buscada
     * @return entidad encontrada o {@code null} cuando no existe
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    T getById(K id);

    /**
     * Persiste los cambios permitidos de una entidad existente.
     *
     * @param entidad entidad con los valores actualizados
     * @return {@code true} cuando se modifica el registro
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    boolean update(T entidad);

    /**
     * Ejecuta la baja definida por el módulo, normalmente una baja lógica.
     *
     * @param id identificador de la entidad que se dará de baja
     * @return {@code true} cuando se aplica la baja
 *
 * @author Daniel Jared Flores Beltrán
 * @since 2026-08-24
 */
    boolean delete(K id);
}
