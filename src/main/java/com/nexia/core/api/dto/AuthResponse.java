package com.nexia.core.api.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds
) {
}
