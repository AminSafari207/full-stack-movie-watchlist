package com.fsmw.model.auth;

import com.fsmw.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "permissions")
public class Permission extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PermissionType name;
}
