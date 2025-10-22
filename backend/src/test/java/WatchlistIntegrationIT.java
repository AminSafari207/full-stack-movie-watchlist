package com.fsmw.test;

import com.fsmw.config.PersistenceUnit;
import com.fsmw.model.auth.Role;
import com.fsmw.model.auth.RoleType;
import com.fsmw.model.movie.Movie;
import com.fsmw.model.user.User;
import com.fsmw.service.RolePermissionSeeder;
import com.fsmw.service.ServiceProvider;
import com.fsmw.service.auth.RoleService;
import com.fsmw.service.movie.MovieService;
import com.fsmw.service.user.UserService;
import com.fsmw.service.watchlist.WatchlistService;
import com.fsmw.config.JpaUtils;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class WatchlistIntegrationIT {
    private static final PersistenceUnit PERSISTENCE_UNIT = PersistenceUnit.MW;

    private static EntityManagerFactory emf;

    private static UserService userService;
    private static MovieService movieService;
    private static WatchlistService watchlistService;
    private static RoleService roleService;

    @BeforeAll
    public static void init() {
        ServiceProvider sp = new ServiceProvider(PERSISTENCE_UNIT);

        userService = sp.getUserService();
        movieService = sp.getMovieService();
        watchlistService = sp.getWatchlistService();
        roleService = sp.getRoleService();
    }

//    @BeforeEach
    public void resetTables() {
        var em = JpaUtils.getEm(PERSISTENCE_UNIT);
        em.getTransaction().begin();
        em.createNativeQuery("DELETE FROM watchlists").executeUpdate();
        em.createNativeQuery("DELETE FROM movies").executeUpdate();
        em.createNativeQuery("DELETE FROM user_roles").executeUpdate();
        em.createNativeQuery("DELETE FROM role_permissions").executeUpdate();
        em.createNativeQuery("DELETE FROM permissions").executeUpdate();
        em.createNativeQuery("DELETE FROM roles").executeUpdate();
        em.createNativeQuery("DELETE FROM users").executeUpdate();
        em.getTransaction().commit();
        em.close();

        new RolePermissionSeeder(PersistenceUnit.TEST).seed();
    }

    @AfterAll
    public static void tearDown() {
        JpaUtils.shutdown();
    }

    @Test
    public void createUser() {
        Role userRole = roleService.findByName(RoleType.USER).get();
        User u = User.builder()
                .username("johndoe")
                .email("johndoe@example.com")
                .password("password")
                .roles(Set.of(userRole))
                .build();

        User created = userService.save(u);
        assertNotNull(created.getId(), "created user should have id");

        User fetched = userService.findById(created.getId()).orElseThrow();
        assertEquals("johndoe", fetched.getUsername());
        assertEquals("johndoe@example.com", fetched.getEmail());
        assertNotNull(fetched.getPassword(), "password should be present");
        assertTrue(fetched.hasRole(RoleType.USER), "role type should be USER");
    }

    @Test
    public void createMovie() {
        Movie m = Movie.builder()
                .title("The Matrix")
                .description("Test Description")
                .genre("Sci-Fi")
                .rating(8)
                .duration(8160L)
                .releaseDate(LocalDate.now())
                .posterImageBase64("")
                .build();

        Movie created = movieService.save(m);
        assertNotNull(created.getId(), "created movie should have id");

        Movie fetched = movieService.findById(created.getId()).orElseThrow();
        assertEquals("The Matrix", fetched.getTitle());
        assertEquals("Sci-Fi", fetched.getGenre());
        assertEquals(8160, fetched.getDuration());
    }

    @Test
    public void addMultipleWatchlistWithConcurrency_removeOneAlso() throws InterruptedException, ExecutionException, TimeoutException {
        Role adminRole = roleService.findByName(RoleType.ADMIN).get();
        User u = User.builder()
                .username("johndoe")
                .email("johndoe@example.com")
                .password("password")
                .roles(Set.of(adminRole))
                .build();

        Long userId = userService.save(u).getId();
        List<Long> movieIds = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Movie m = Movie.builder()
                    .title("Movie-" + i)
                    .genre("Genre-" + i)
                    .duration(5000L + i * 60L)
                    .description("Desc-" + i)
                    .rating(i)
                    .releaseDate(LocalDate.now())
                    .posterImageBase64("")
                    .build();

            movieIds.add(movieService.save(m).getId());
        }

        List<Thread> threads = new ArrayList<>();

        for (int t = 0; t < 4; t++) {
            Thread th = new Thread(() -> {
                for (Long mid : movieIds) {
                    try {
                        watchlistService.addToWatchlist(userId, mid);
                    } catch (Exception ignored) {
                    }
                }
            });

            threads.add(th);
            th.start();
        }

        for (Thread th : threads) {
            th.join();
        }

        List<Movie> movies = watchlistService.getMoviesByUserId(userId);
        Set<Long> ids = new HashSet<>();

        for (Movie m : movies) ids.add(m.getId());

        assertEquals(movieIds.size(), ids.size());
        assertTrue(ids.containsAll(movieIds));

        Long movieToRemove = movieIds.get(1);
        boolean removed = watchlistService.removeFromWatchlist(userId, movieToRemove);

        assertTrue(removed, "movie should be removed from watchlist");

        List<Movie> afterRemoval = watchlistService.getMoviesByUserId(userId);
        Set<Long> afterIds = new HashSet<>();

        for (Movie mv : afterRemoval) afterIds.add(mv.getId());

        assertFalse(afterIds.contains(movieToRemove), "removed movie should not be in watchlist anymore");
        assertEquals(movieIds.size() - 1, afterIds.size(), "watchlist size should reduce by one");
    }
}