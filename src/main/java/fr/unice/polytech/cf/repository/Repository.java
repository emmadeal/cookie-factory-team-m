package fr.unice.polytech.cf.repository;

import java.util.Optional;

public interface Repository <T, ID> {

    // Returns the number of entities available.
    long count();

    // Deletes all entities managed by the repository.
    void deleteAll();

    // Deletes the entity with the given id.
    void deleteById(ID id);

    // Returns whether an entity with the given id exists.
    boolean existsById(ID id);

    // Returns all instances of the type.
    Iterable<T> findAll();

    // Retrieves an entity by its id.
    Optional<T> findById(ID id);


    // Saves a given entity through its id.
    <S extends T> void save(S entity, ID id);

}
