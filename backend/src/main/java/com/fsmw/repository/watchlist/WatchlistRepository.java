package com.fsmw.repository.watchlist;

import com.fsmw.model.watchlist.Watchlist;
import com.fsmw.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends BaseRepository<Watchlist, Long> {
    Optional<Watchlist> findByUserIdAndMovieId(Long userId, Long movieId);
    List<Watchlist> findByUserId(Long userId);
    List<Watchlist> findByMovieId(Long movieId);
    boolean deleteByUserIdAndMovieId(Long userId, Long movieId);
    boolean existsByUserIdAndMovieId(Long userId, Long movieId);
}
