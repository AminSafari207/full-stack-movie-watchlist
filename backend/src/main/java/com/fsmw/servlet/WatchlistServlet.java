package com.fsmw.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsmw.config.PersistenceUnit;
import com.fsmw.model.auth.PermissionType;
import com.fsmw.model.dto.response.watchlist.WatchlistResponseDto;
import com.fsmw.model.user.User;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.auth.AuthorizationService;
import com.fsmw.service.user.UserService;
import com.fsmw.service.watchlist.WatchlistService;
import com.fsmw.servlet.base.BaseServlet;
import com.fsmw.utils.ObjectMapperProvider;
import com.fsmw.utils.ServletResponseUtil;
import com.fsmw.utils.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Optional;

@WebServlet("/watchlist/*")
public class WatchlistServlet extends BaseServlet {
    private final ObjectMapper mapper = ObjectMapperProvider.get();
    private UserService userService;
    private WatchlistService watchlistService;
    private AuthorizationService authorizationService;

    @Override
    public void init() {
        ServiceProvider sp = new ServiceProvider(PersistenceUnit.MW);

        this.userService = sp.getUserService();
        this.watchlistService = sp.getWatchlistService();
        this.authorizationService = sp.getAuthorizationService();

        registerPost("/getwatchlist", this::handleGetWatchlist);
    }

    private void handleGetWatchlist(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/text");

        try {
            if (!authorizationService.requirePermission(req, resp, mapper, PermissionType.CAN_VIEW_WATCHLIST)) return;

            Long userId = (Long) req.getAttribute("userId");
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isEmpty()) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_NOT_FOUND,
                        "user with ID '" + userId +"' not found",
                        "User not found"
                );
                return;
            }

            User foundUser = userOpt.get();

            if (foundUser.getWatchlist().isEmpty()) {
                ServletResponseUtil.writeError(
                        resp,
                        HttpServletResponse.SC_NOT_FOUND,
                        "watchlist for user with ID '" + userId + "' is empty",
                        "Watchlist is empty"
                );
                return;
            }

            WatchlistResponseDto watchlist = WatchlistResponseDto.fromUser(foundUser, mapper);

            ServletResponseUtil.writeSuccess(
                    resp,
                    HttpServletResponse.SC_OK,
                    "",
                    "",
                    watchlist
            );
        } catch (Exception e) {
            ServletUtil.handleCommonInternalException(resp, mapper, e);
        }
    }
}