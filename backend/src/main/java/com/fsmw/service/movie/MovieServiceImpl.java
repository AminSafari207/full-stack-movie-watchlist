package com.fsmw.service.movie;

import com.fsmw.model.movie.Movie;
import com.fsmw.repository.movie.MovieRepository;
import com.fsmw.repository.movie.MovieRepositoryImpl;
import com.fsmw.service.base.AbstractBaseService;
import com.fsmw.service.base.BaseService;
import jakarta.persistence.EntityManagerFactory;

public class MovieServiceImpl
        extends AbstractBaseService<Movie, Long, MovieRepository>
        implements MovieService {

    public MovieServiceImpl(EntityManagerFactory emf) {
        super(emf, MovieRepositoryImpl::new);
    }
}
