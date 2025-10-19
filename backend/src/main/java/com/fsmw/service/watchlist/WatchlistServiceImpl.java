package com.fsmw.service.watchlist;

import com.fsmw.exceptions.MovieNotFoundException;
import com.fsmw.exceptions.UserNotFoundException;
import com.fsmw.exceptions.WatchlistAlreadyExistsException;
import com.fsmw.model.movie.Movie;
import com.fsmw.model.user.User;
import com.fsmw.model.watchlist.Watchlist;
import com.fsmw.repository.movie.MovieRepository;
import com.fsmw.repository.movie.MovieRepositoryImpl;
import com.fsmw.repository.user.UserRepository;
import com.fsmw.repository.user.UserRepositoryImpl;
import com.fsmw.repository.watchlist.WatchlistRepository;
import com.fsmw.repository.watchlist.WatchlistRepositoryImpl;
import com.fsmw.service.base.AbstractBaseService;
import com.fsmw.service.base.BaseService;
import com.fsmw.utils.concurrency.UserLocks;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WatchlistServiceImpl
        extends AbstractBaseService<Watchlist, Long, WatchlistRepository>
        implements WatchlistService {

    public WatchlistServiceImpl(EntityManagerFactory emf) {
        super(emf, WatchlistRepositoryImpl::new);
    }

    @Override
    public Watchlist addToWatchlist(Long userId, Long movieId) {
        ReentrantLock lock = UserLocks.of(userId);
        lock.lock();

        try {
            return executeTransaction(em -> {
                UserRepository userRepo = new UserRepositoryImpl(em);
                MovieRepository movieRepo = new MovieRepositoryImpl(em);
                WatchlistRepository watchRepo = this.coreRepository(em);

                if (!userRepo.exists(userId)) throw new UserNotFoundException(userId);
                if (!movieRepo.exists(movieId)) throw new MovieNotFoundException(movieId);

                Optional<Watchlist> existing = watchRepo.findByUserIdAndMovieId(userId, movieId);

                if (existing.isPresent()) throw new WatchlistAlreadyExistsException(userId, movieId);

                User userRef = em.getReference(User.class, userId);
                Movie movieRef = em.getReference(Movie.class, movieId);

                Watchlist toSave = Watchlist.builder()
                        .user(userRef)
                        .movie(movieRef)
                        .build();

                return watchRepo.save(toSave);
            });
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeFromWatchlist(Long userId, Long movieId) {
        ReentrantLock lock = UserLocks.of(userId);
        lock.lock();

        try {
            return executeTransaction(em -> coreRepository(em).deleteByUserIdAndMovieId(userId, movieId));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Movie> getMoviesByUserId(Long userId) {
        return executeTransaction(em ->
            coreRepository(em).findByUserId(userId)
                    .stream()
                    .map(Watchlist::getMovie)
                    .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<Watchlist> getByUserIdAndMovieId(Long userId, Long movieId) {
        return executeTransaction(em -> coreRepository(em).findByUserIdAndMovieId(userId, movieId));
    }
}
