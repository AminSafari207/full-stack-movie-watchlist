package com.fsmw.repository.base;

import com.fsmw.model.common.BaseEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T extends BaseEntity, ID extends Serializable> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean deleteById(ID id);
    boolean exists(ID id);
    long count();

    Class<T> getEntityClassRef();
    String getEntityName();
}

