package com.fsmw.service.watchlist;

import com.fsmw.model.movie.Movie;
import com.fsmw.model.watchlist.Watchlist;
import com.fsmw.service.base.BaseService;

import java.util.List;
import java.util.Optional;

public interface WatchlistService extends BaseService<Watchlist, Long> {
    Watchlist addToWatchlist(Long userId, Long movieId);
    boolean removeFromWatchlist(Long userId, Long movieId);
    List<Movie> getMoviesByUserId(Long userId);
    Optional<Watchlist> getByUserIdAndMovieId(Long userId, Long movieId);
}
