package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fsmw.model.dto.CreateMovieDto;
import com.fsmw.model.dto.ErrorDto;
import com.fsmw.model.dto.MovieDto;
import com.fsmw.model.movie.Movie;
import com.fsmw.service.movie.MovieService;
import com.fsmw.service.movie.MovieServiceImpl;
import jakarta.servlet.ServletException;
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
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private MovieService movieService;

    @Override
    public void init() {
        this.movieService = new MovieServiceImpl();

        mapper.registerModule(new JavaTimeModule());
//        mapper.registerModule(new Hibernate5Module());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            String idParam = req.getParameter("id");

            if (idParam != null) {
                Long id = Long.valueOf(idParam);
                Optional<Movie> movieOpt = movieService.findById(id);

                if (movieOpt.isPresent()) {
                    writeJson(resp, MovieDto.from(movieOpt.get()));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                List<Movie> movies = movieService.findAll();
                List<MovieDto> dtos = movies.stream()
                        .map(MovieDto::from)
                        .collect(Collectors.toList());

                writeJson(resp, dtos);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, new ErrorDto("Server error", e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            CreateMovieDto dto = mapper.readValue(req.getInputStream(), CreateMovieDto.class);

            Movie movie = Movie.builder()
                    .title(dto.title())
                    .genre(dto.genre())
                    .duration(dto.duration())
                    .build();

            Movie saved = movieService.create(movie);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            writeJson(resp, MovieDto.from(saved));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorDto("Invalid request", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String idParam = req.getParameter("id");

        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorDto("Missing parameter", "id is required"));
            return;
        }

        try {
            Long id = Long.valueOf(idParam);
            boolean deleted = movieService.deleteById(id);

            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJson(resp, new ErrorDto("Not found", "Movie with id " + id + " not found"));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorDto("Invalid id", "id must be a number"));
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        mapper.writeValue(resp.getOutputStream(), data);
    }
}
