package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fsmw.model.dto.CreateWatchlistDto;
import com.fsmw.model.dto.ErrorDto;
import com.fsmw.model.dto.MovieDto;
import com.fsmw.model.dto.WatchlistDto;
import com.fsmw.model.movie.Movie;
import com.fsmw.model.watchlist.Watchlist;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.watchlist.WatchlistService;
import com.fsmw.service.watchlist.WatchlistServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/watchlist")
public class WatchlistServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();;
    private WatchlistService watchlistService;

    @Override
    public void init() {
        ServiceProvider serviceProvider = new ServiceProvider();

        this.watchlistService = serviceProvider.getWatchlistService();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {

            String userIdParam = req.getParameter("userId");

            if (userIdParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(resp, new ErrorDto("Missing parameter", "userId is required"));
                return;
            }

            Long userId = Long.valueOf(userIdParam);

            List<Movie> entries = watchlistService.getMoviesByUserId(userId);
            List<MovieDto> movies = entries.stream()
                    .map(MovieDto::from)
                    .toList();

            writeJson(resp, movies);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, new ErrorDto("Server error", e.getMessage()));
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            CreateWatchlistDto dto = mapper.readValue(req.getInputStream(), CreateWatchlistDto.class);
            Watchlist saved = watchlistService.addToWatchlist(dto.userId(), dto.movieId());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            writeJson(resp, WatchlistDto.from(saved));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorDto("Invalid request", e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String userIdParam = req.getParameter("userId");
        String movieIdParam = req.getParameter("movieId");

        if (userIdParam == null || movieIdParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorDto("Missing parameter", "userId and movieId are required"));
            return;
        }

        Long userId = Long.valueOf(userIdParam);
        Long movieId = Long.valueOf(movieIdParam);

        boolean removed = watchlistService.removeFromWatchlist(userId, movieId);

        if (removed) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(resp, new ErrorDto("Not found", "No watchlist entry for user " + userId + " and movie " + movieId));
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        mapper.writeValue(resp.getOutputStream(), data);
    }
}