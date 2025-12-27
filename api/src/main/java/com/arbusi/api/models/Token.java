package com.arbusi.api.models;

import com.arbusi.api.configuration.logging.LoggableEntity;
import com.arbusi.api.configuration.logging.LoggableEntityListener;
import com.arbusi.api.enums.TokenType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@EntityListeners(LoggableEntityListener.class)
@Entity
@Table(name = "token")
@Builder
@SequenceGenerator(
        name = "token_id_seq",
        sequenceName = "token_id_seq",
        allocationSize = 1
)
public class Token implements LoggableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "token", nullable = false, length = 36)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TokenType type;

    @Column(name = "used", nullable = false)
    private Boolean used;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String getLogBody() {
        return "Token[" + "id=" + id +
                ", token=" + token +
                ", user=" + user.getId() +
                ", type=" + type +
                ", used=" + used +
                ", expireAt=" + expireAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "]";
    }
}