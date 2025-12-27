package com.arbusi.api.models;

import com.arbusi.api.configuration.logging.LoggableEntity;
import com.arbusi.api.configuration.logging.LoggableEntityListener;
import com.arbusi.api.enums.AuthSource;
import com.arbusi.api.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "users_id_seq",
        sequenceName = "users_id_seq",
        allocationSize = 1
)
@EntityListeners(LoggableEntityListener.class)
@Entity
@Table(name = "users")
public class User implements LoggableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 256)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash; // null for pure OAuth

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_source", nullable = false, length = 20)
    private AuthSource authSource;

    @Column(name = "auth_source_user_id", length = 100)
    private String authSourceUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String getLogBody() {
        return "User[" + "id=" + id +
                ", email=" + email +
                ", authSource=" + authSource +
                ", authSourceUserId=" + authSourceUserId +
                ", role=" + role +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "]";
    }
}
