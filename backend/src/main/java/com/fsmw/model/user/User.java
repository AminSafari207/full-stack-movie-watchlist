package com.fsmw.model.user;

import com.fsmw.model.common.BaseEntity;
import com.fsmw.model.movie.Movie;
import com.fsmw.model.watchlist.Watchlist;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "watchlist")
@ToString(callSuper = true, exclude = {"password", "watchlist"})
@SuperBuilder(toBuilder = true)
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class User extends BaseEntity {
    @NotBlank
    @Column(name = "username", nullable = false)
    private String username;

    @NotBlank
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    private Set<Watchlist> watchlist = new HashSet<>();

    public void addToWatchlist(Movie movie, String status) {
        Watchlist w = Watchlist
                .builder()
                .user(this)
                .movie(movie)
                .build();

        watchlist.add(w);
        movie.getWatchlist().add(w);
    }

    public void removeFromWatchlist(Movie movie) {
        watchlist.removeIf(w -> w.getMovie().equals(movie));
        movie.getWatchlist().removeIf(w -> w.getUser().equals(this));
    }
}
