package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.model.dto.CreateMovieDto;
import com.fsmw.model.dto.ErrorDto;
import com.fsmw.model.dto.MovieDto;
import com.fsmw.model.dto.request.movie.AddMovieRequestDto;
import com.fsmw.model.dto.request.movie.EditMovieRequestDto;
import com.fsmw.model.dto.response.common.ApiResponseDto;
import com.fsmw.model.dto.response.movie.MovieResponseDto;
import com.fsmw.model.movie.Movie;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.auth.AuthorizationService;
import com.fsmw.service.movie.MovieService;
import com.fsmw.servlet.base.BaseServlet;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.ServletResponseUtil;
import com.fsmw.utils.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/movie/*")
public class MovieServlet extends BaseServlet {
    private final ObjectMapper mapper = ObjectMapperProvider.get();
    private MovieService movieService;
    private AuthorizationService authorizationService;

    @Override
    public void init() {
        ServiceProvider sp = new ServiceProvider();

        this.movieService = sp.getMovieService();
        this.authorizationService = sp.getAuthorizationService();

        registerPost("/getmovies", this::handleGetMovies);
        registerPost("/addmovie", this::handleAddMovie);
        registerPost("/editmovie", this::handleEditMovie);
        registerPost("/deletemovie", this::handleDeleteMovie);
    }

    private void handleGetMovies(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/text");

        try {
            List<Movie> movies = movieService.findAll();

            if (movies.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                mapper.writeValue(
                        resp.getWriter(),
                        ApiResponseDto.error(
                                HttpServletResponse.SC_NOT_FOUND,
                                "there is no movie here",
                                "No movies found"
                        )
                );
                return;
            }

            List<MovieResponseDto> movieDtos = movies.stream()
                    .map(movie -> mapper.convertValue(movie, MovieResponseDto.class))
                    .toList();

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(
                    resp.getWriter(),
                    ApiResponseDto.success(
                            HttpServletResponse.SC_OK,
                            "",
                            "Movies found successfully",
                            movieDtos
                    )
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }

    private void handleAddMovie(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/text");

        try {
            if (!authorizationService.requirePermission(req, resp, mapper, PermissionType.CAN_ADD_MOVIE)) return;

            String body = ServletUtil.readRequestBody(req);
            AddMovieRequestDto addDto = mapper.readValue(body, AddMovieRequestDto.class);

            if (addDto.title() == null) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "title must be provided",
                        "Title must be provided"
                );
                return;
            }

            if (addDto.description() == null) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "description must be provided",
                        "Description must be provided"
                );
                return;
            }

            if (addDto.genre() == null) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "genre must be provided",
                        "Genre must be provided"
                );
                return;
            }

            if (addDto.duration() == null) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "duration must be provided",
                        "Duration must be provided"
                );
                return;
            }

            if (addDto.releaseDate() == null) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "release date must be provided",
                        "Release dat must be provided"
                );
                return;
            }

            if (addDto.rating() < 1 || addDto.rating() > 10) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "rating must be between 1 and 10",
                        "Rating must be between 1 and 10"
                );
                return;
            }

            Movie newMovie = Movie.builder()
                    .title(addDto.title())
                    .description(addDto.description())
                    .genre(addDto.genre())
                    .posterImageBase64(Objects.requireNonNullElse(addDto.posterImageBase64(), ""))
                    .duration(addDto.duration())
                    .releaseDate(addDto.releaseDate())
                    .rating(addDto.rating())
                    .build();
            Movie savedMovie = movieService.save(newMovie);

            ServletResponseUtil.writeSuccess(
                    resp,
                    HttpServletResponse.SC_OK,
                    "",
                    "Movie added successfully",
                    mapper.convertValue(savedMovie, MovieResponseDto.class)
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }

    private void handleEditMovie(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/text");

        try {
            if (!authorizationService.requirePermission(req, resp, mapper, PermissionType.CAN_EDIT_MOVIE)) return;

            String body = ServletUtil.readRequestBody(req);
            EditMovieRequestDto editDto = mapper.readValue(body, EditMovieRequestDto.class);

            Optional<Movie> movieOpt = movieService.findById(editDto.movieId());

            if (movieOpt.isEmpty()) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_NOT_FOUND,
                        "movie with ID '" + editDto.movieId() + "' not found",
                        "Movie not found"
                );
                return;
            }

            Movie movie = movieOpt.get();

            if (editDto.title() != null) movie.setTitle(editDto.title());
            if (editDto.description() != null) movie.setDescription(editDto.description());
            if (editDto.genre() != null) movie.setGenre(editDto.genre());
            if (editDto.posterImageBase64() != null) movie.setPosterImageBase64(editDto.posterImageBase64());
            if (editDto.duration() != null) movie.setDuration(editDto.duration());
            if (editDto.releaseDate() != null) movie.setReleaseDate(editDto.releaseDate());
            if (editDto.rating() < 1 || editDto.rating() > 10) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "rating must be between 1 and 10",
                        "Rating must be between 1 and 10"
                );
                return;
            } else {
                movie.setRating(editDto.rating());
            }

            Movie updatedMovie = movieService.save(movie);

            ServletResponseUtil.writeSuccess(
                    resp,
                    HttpServletResponse.SC_OK,
                    "",
                    "Movie edited successfully",
                    mapper.convertValue(updatedMovie, MovieResponseDto.class)
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }

    private void handleDeleteMovie(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/text");

        try {
            if (!authorizationService.requirePermission(req, resp, mapper, PermissionType.CAN_DELETE_MOVIE)) return;

            Long movieId = ServletUtil.getRequiredLongParam(req, resp, "movieId");

            if (movieId == null) return;

            if (movieService.deleteById(movieId)) {
                ServletResponseUtil.writeSuccess(
                        resp,
                        HttpServletResponse.SC_OK,
                        "",
                        "Movie removed successfully",
                        new Object()
                );
            } else {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_NOT_FOUND,
                        "movie with ID '" + movieId + "' not found",
                        "Movie not found"
                );
            }
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }
}
