package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fsmw.model.dto.CreateMovieDto;
import com.fsmw.model.dto.ErrorDto;
import com.fsmw.model.dto.MovieDto;
import com.fsmw.model.dto.request.movie.AddMovieRequestDto;
import com.fsmw.model.dto.response.common.ApiResponseDto;
import com.fsmw.model.dto.response.movie.MovieResponseDto;
import com.fsmw.model.movie.Movie;
import com.fsmw.service.ServiceProvider;
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

    @Override
    public void init() {
        ServiceProvider serviceProvider = new ServiceProvider();

        this.movieService = serviceProvider.getMovieService();

        registerPost("/getmovies", this::handleGetMovies);
        registerPost("/addmovie", this::handleAddMovie);
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

            if (addDto.rating() == 0) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "rating must be higher than 0",
                        "Rating must be higher than 0"
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

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//
//        try {
//            String idParam = req.getParameter("id");
//
//            if (idParam != null) {
//                Long id = Long.valueOf(idParam);
//                Optional<Movie> movieOpt = movieService.findById(id);
//
//                if (movieOpt.isPresent()) {
//                    writeJson(resp, MovieDto.from(movieOpt.get()));
//                } else {
//                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                }
//            } else {
//                List<Movie> movies = movieService.findAll();
//                List<MovieDto> dtos = movies.stream()
//                        .map(MovieDto::from)
//                        .collect(Collectors.toList());
//
//                writeJson(resp, dtos);
//            }
//        } catch (Exception e) {
//            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            writeJson(resp, new ErrorDto("Server error", e.getMessage()));
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//
//        try {
//            CreateMovieDto dto = mapper.readValue(req.getInputStream(), CreateMovieDto.class);
//
//            Movie movie = Movie.builder()
//                    .title(dto.title())
//                    .genre(dto.genre())
//                    .duration(dto.duration())
//                    .build();
//
//            Movie saved = movieService.save(movie);
//
//            resp.setStatus(HttpServletResponse.SC_CREATED);
//            writeJson(resp, MovieDto.from(saved));
//        } catch (Exception e) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            writeJson(resp, new ErrorDto("Invalid request", e.getMessage()));
//        }
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        resp.setContentType("application/json");
//
//        String idParam = req.getParameter("id");
//
//        if (idParam == null) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            writeJson(resp, new ErrorDto("Missing parameter", "id is required"));
//            return;
//        }
//
//        try {
//            Long id = Long.valueOf(idParam);
//            boolean deleted = movieService.deleteById(id);
//
//            if (deleted) {
//                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//            } else {
//                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                writeJson(resp, new ErrorDto("Not found", "Movie with id " + id + " not found"));
//            }
//        } catch (NumberFormatException e) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            writeJson(resp, new ErrorDto("Invalid id", "id must be a number"));
//        }
//    }
//
//    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
//        mapper.writeValue(resp.getOutputStream(), data);
//    }
}
