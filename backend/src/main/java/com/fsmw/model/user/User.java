package com.fsmw.model.user;

import com.fsmw.model.common.BaseEntity;
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
@EqualsAndHashCode(callSuper = true, exclude = "movies")
@ToString(callSuper = true, exclude = {"password", "movies"})
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

    @ManyToMany
    @JoinTable(
        name = "watchlist",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "movie_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private Set<Movie> movies = new HashSet<>();
}
