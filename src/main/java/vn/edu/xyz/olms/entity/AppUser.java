package vn.edu.xyz.olms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user") // tránh "user" - từ khoá dành riêng trong PostgreSQL
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role; // READER | LIBRARIAN | ADMIN

    @Column(name = "is_active")
    private boolean active = true;

    private int failedAttempts = 0;

    private Instant lockedUntil;

    // Tài khoản LIBRARIAN/ADMIN có thể không gắn Member nào -> để optional
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
