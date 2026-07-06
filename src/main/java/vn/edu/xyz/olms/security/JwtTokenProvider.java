package vn.edu.xyz.olms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.edu.xyz.olms.entity.AppUser;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessExpirationMs;

    public JwtTokenProvider(
            @Value("${olms.jwt.secret}") String secret,
            @Value("${olms.jwt.access-expiration-ms}") long accessExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExpirationMs;
    }

    public String generateAccessToken(AppUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpirationMs);
        var builder = Jwts.builder()
            .setSubject(user.getUsername())
            .claim("role", user.getRole())
            .setIssuedAt(now)
            .setExpiration(expiry);
        if (user.getMember() != null) {
            builder.claim("memberId", user.getMember().getId().toString());
        }
        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public UUID getMemberIdFromToken(String token) {
        Claims claims = parseClaims(token);
        String memberId = claims.get("memberId", String.class);
        return memberId != null ? UUID.fromString(memberId) : null;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
