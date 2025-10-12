package com.fsmw.repository.base;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID extends Serializable> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean deleteById(ID id);
    long count();

    Class<T> getEntityClassRef();
    String getEntityName();
}

