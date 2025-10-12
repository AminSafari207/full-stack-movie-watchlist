package com.fsmw.model.movie;

import com.fsmw.model.common.BaseEntity;
import com.fsmw.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "users")
@ToString(callSuper = true, exclude = "users")
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "movies")
public class Movie extends BaseEntity {
    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Column(name = "genre", nullable = false)
    private String genre;

    @NotBlank
    @Column(name = "duration", nullable = false)
    private Long duration;

    @ManyToMany(mappedBy = "movies")
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
