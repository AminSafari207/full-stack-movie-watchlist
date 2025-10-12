package com.fsmw.repository.movie;

import com.fsmw.model.movie.Movie;
import com.fsmw.repository.base.AbstractBaseRepository;
import jakarta.persistence.EntityManager;

public class MovieRepositoryImpl extends AbstractBaseRepository<Movie, Long> implements MovieRepository {
    public MovieRepositoryImpl(EntityManager em) {
        super(em, Movie.class);
    }
}
