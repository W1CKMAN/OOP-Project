package DAO;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface for CRUD operations.
 * @param <T> The entity type
 * @param <ID> The ID type
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Save a new entity
     */
    T save(T entity);
    
    /**
     * Update an existing entity
     */
    T update(T entity);
    
    /**
     * Delete an entity by ID
     */
    boolean delete(ID id);
    
    /**
     * Delete an entity by ID (alias for delete)
     */
    default boolean deleteById(ID id) {
        return delete(id);
    }
    
    /**
     * Find an entity by ID
     */
    Optional<T> findById(ID id);
    
    /**
     * Find all entities
     */
    List<T> findAll();
    
    /**
     * Count all entities
     */
    long count();
    
    /**
     * Check if an entity exists by ID
     */
    boolean existsById(ID id);
}
