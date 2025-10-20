package com.fsmw.model.movie;

import com.fsmw.model.common.BaseEntity;
import com.fsmw.model.user.User;
import com.fsmw.model.watchlist.Watchlist;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "watchlist")
@ToString(callSuper = true, exclude = "watchlist")
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "movies")
public class Movie extends BaseEntity {
    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;

    @NotBlank
    @Column(name = "genre", nullable = false)
    private String genre;

    @Positive
    @Column(name = "duration", nullable = false)
    private Long duration;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "poster_image_base64")
    private String posterImageBase64;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @Builder.Default
    private Set<Watchlist> watchlist = new HashSet<>();
}
