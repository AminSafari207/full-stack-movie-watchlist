package com.fsmw.repository.watchlist;

import com.fsmw.model.watchlist.Watchlist;
import com.fsmw.repository.base.AbstractBaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class WatchlistRepositoryImpl extends AbstractBaseRepository<Watchlist, Long> implements WatchlistRepository {
    public WatchlistRepositoryImpl(EntityManager em) {
        super(em, Watchlist.class);
    }

    @Override
    public Optional<Watchlist> findByUserIdAndMovieId(Long userId, Long movieId) {
        TypedQuery<Watchlist> q = em.createQuery(
                "select w from " + getEntityName() + " w where w.user.id = :uid and w.movie.id = :mid",
                        getEntityClassRef()
                )
                .setParameter("uid", userId)
                .setParameter("mid", movieId)
                .setMaxResults(1);

        try {
            return Optional.of(q.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Watchlist> findByUserId(Long userId) {
        return em.createQuery(
                "select w from " + getEntityName() + " w where w.user.id = :uid",
                        getEntityClassRef()
                )
                .setParameter("uid", userId)
                .getResultList();

    }

    @Override
    public List<Watchlist> findByMovieId(Long movieId) {
        return em.createQuery(
                "select w from" + getEntityName() + " w where w.movie.id = :mid",
                        getEntityClassRef()
                )
                .setParameter("mid", movieId)
                .getResultList();
    }

    @Override
    public boolean deleteByUserIdAndMovieId(Long userId, Long movieId) {
        int deleted = em.createQuery(
                "delete from " + getEntityName() + " w where w.user.id = :uid and w.movie.id = :mid"
                )
                .setParameter("uid", userId)
                .setParameter("mid", movieId)
                .executeUpdate();

        return deleted > 0;
    }

    @Override
    public boolean existsByUserIdAndMovieId(Long userId, Long movieId) {
        TypedQuery<Integer> q = em.createQuery(
                "select 1 from " + getEntityName() + " w where w.user.id = :uid and w.movie.id = :mid",
                        Integer.class
                )
                .setParameter("uid", userId)
                .setParameter("mid", movieId)
                .setMaxResults(1);

        try {
            q.getSingleResult();
            return true;
        } catch (NoResultException ex) {
            return false;
        }
    }
}
