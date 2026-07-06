package vn.edu.xyz.olms.security;

import java.util.UUID;

public record UserPrincipal(String username, String role, UUID memberId) {
}
