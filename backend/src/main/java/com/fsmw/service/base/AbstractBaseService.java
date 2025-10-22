package com.fsmw.service.base;

import com.fsmw.model.common.BaseEntity;
import com.fsmw.repository.base.BaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AbstractBaseService<
        T extends BaseEntity,
        ID extends Serializable,
        R extends BaseRepository<T, ID>
        >
        extends TransactionalService
        implements BaseService<T, ID>
{
    protected final Function<EntityManager, R> repositoryFactory;

    public AbstractBaseService(EntityManagerFactory emf, Function<EntityManager, R> repositoryFactory) {
        super(emf);

        if (repositoryFactory == null) throw new IllegalArgumentException("'repositoryFactory' must not be null");

        this.repositoryFactory = repositoryFactory;
    }

    protected final R coreRepository(EntityManager em) {
        return repositoryFactory.apply(em);
    }

    @Override
    public T save(T entity) {
        return executeTransaction(em -> coreRepository(em).save(entity));
    }

    @Override
    public Optional<T> findById(ID id) {
        return executeTransaction(em -> coreRepository(em).findById(id));
    }

    @Override
    public List<T> findAll() {
        return executeTransaction(em -> coreRepository(em).findAll());
    }

    @Override
    public boolean deleteById(ID id) {
        return executeTransaction(em -> coreRepository(em).deleteById(id));
    }

    @Override
    public boolean existsById(ID id) {
        return executeTransaction(em -> coreRepository(em).exists(id));
    }
}
