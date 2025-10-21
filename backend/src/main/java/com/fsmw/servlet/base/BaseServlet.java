package com.fsmw.servlet.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.ServletUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class BaseServlet extends HttpServlet {
    private final Map<String, BiConsumer<HttpServletRequest, HttpServletResponse>> getRoutes = new HashMap<>();
    private final Map<String, BiConsumer<HttpServletRequest, HttpServletResponse>> postRoutes = new HashMap<>();

    protected void registerGet(String path, BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        getRoutes.put(path, handler);
    }

    protected void registerPost(String path, BiConsumer<HttpServletRequest, HttpServletResponse> handler) {
        postRoutes.put(path, handler);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp, getRoutes);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        dispatch(req, resp, postRoutes);
    }

    private void dispatch(
            HttpServletRequest req,
            HttpServletResponse resp,
            Map<String, BiConsumer<HttpServletRequest, HttpServletResponse>> routes)
            throws IOException {

        String path = req.getPathInfo();
        BiConsumer<HttpServletRequest, HttpServletResponse> handler = routes.get(path);

        if (handler != null) {
            handler.accept(req, resp);
        } else {
            ServletUtil.handleCommonInvalidPath(resp, ObjectMapperProvider.get(), path);
        }
    }
}
