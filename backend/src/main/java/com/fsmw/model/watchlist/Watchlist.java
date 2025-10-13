package com.fsmw.model.watchlist;

import com.fsmw.model.common.BaseEntity;
import com.fsmw.model.movie.Movie;
import com.fsmw.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"user", "movie"})
@SuperBuilder(toBuilder = true)
@Entity
@Table(
        name = "watchlists",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
)

public class Watchlist extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false, updatable = false)
    private Movie movie;
}