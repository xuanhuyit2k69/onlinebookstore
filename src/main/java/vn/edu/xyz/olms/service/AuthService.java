package vn.edu.xyz.olms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.xyz.olms.dto.AuthResponse;
import vn.edu.xyz.olms.dto.LoginRequest;
import vn.edu.xyz.olms.dto.RefreshRequest;
import vn.edu.xyz.olms.entity.AppUser;
import vn.edu.xyz.olms.entity.RefreshToken;
import vn.edu.xyz.olms.exception.ApiException;
import vn.edu.xyz.olms.exception.InvalidCredentialsException;
import vn.edu.xyz.olms.repository.AppUserRepository;
import vn.edu.xyz.olms.repository.RefreshTokenRepository;
import vn.edu.xyz.olms.security.JwtTokenProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 15;

    private final AppUserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;

    @Value("${olms.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public AuthResponse authenticate(LoginRequest req) {
        AppUser user = userRepo.findByUsername(req.getUsername())
            .orElseThrow(InvalidCredentialsException::new);

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new InvalidCredentialsException();
        }

        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        userRepo.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshRequest req) {
        if (req.getRefreshToken() == null || req.getRefreshToken().isBlank()) {
            throw new ApiException("Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED);
        }
        String hash = hashToken(req.getRefreshToken());
        RefreshToken stored = refreshTokenRepo.findByTokenHashAndRevokedFalse(hash)
            .orElseThrow(() -> new ApiException("Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException("Refresh token đã hết hạn", HttpStatus.UNAUTHORIZED);
        }

        stored.setRevoked(true);
        refreshTokenRepo.save(stored);

        return buildAuthResponse(stored.getUser());
    }

    private void handleFailedLogin(AppUser user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(Instant.now().plusSeconds(LOCK_MINUTES * 60L));
            user.setFailedAttempts(0);
        }
        userRepo.save(user);
    }

    private AuthResponse buildAuthResponse(AppUser user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(hashToken(refreshToken));
        entity.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        entity.setRevoked(false);
        refreshTokenRepo.save(entity);

        UUID memberId = user.getMember() != null ? user.getMember().getId() : null;
        return new AuthResponse(
            accessToken,
            refreshToken,
            jwtProvider.getAccessExpirationMs() / 1000,
            user.getUsername(),
            user.getRole(),
            memberId
        );
    }

    static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
