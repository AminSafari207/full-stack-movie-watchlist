package com.fsmw.service;

import com.fsmw.config.PersistenceUnit;
import com.fsmw.service.movie.MovieService;
import com.fsmw.service.movie.MovieServiceImpl;
import com.fsmw.service.user.UserService;
import com.fsmw.service.user.UserServiceImpl;
import com.fsmw.service.watchlist.WatchlistService;
import com.fsmw.service.watchlist.WatchlistServiceImpl;
import com.fsmw.config.JpaUtils;
import jakarta.persistence.EntityManagerFactory;

public final class ServiceProvider {
    private final EntityManagerFactory emf;

    public ServiceProvider() {
        this.emf = JpaUtils.getDefaultEmf();
    }

    public ServiceProvider(PersistenceUnit unit) {
        this.emf = JpaUtils.getEmf(unit);
    }

    public UserService getUserService() {
        return new UserServiceImpl(emf);
    }

    public MovieService getMovieService() {
        return new MovieServiceImpl(emf);
    }

    public WatchlistService getWatchlistService() {
        return new WatchlistServiceImpl(emf);
    }
}
