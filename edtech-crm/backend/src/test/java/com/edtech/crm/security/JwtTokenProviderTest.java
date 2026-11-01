package com.edtech.crm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String validSecret = "a_super_strong_and_secure_jwt_secret_key_with_sufficient_entropy_1234567890";

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", validSecret);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 3600000L);
        tokenProvider.init();
    }

    @Test
    @DisplayName("Should generate a valid JWT and correctly extract claims")
    void shouldGenerateAndValidateToken() {
        String email = "admin@edtech.com";
        Long adminId = 42L;
        String name = "Test Admin";

        String token = tokenProvider.generateToken(adminId, email, name);

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getEmailFromToken(token)).isEqualTo(email);
        assertThat(tokenProvider.getAdminIdFromToken(token)).isEqualTo(adminId);
    }

    @Test
    @DisplayName("Should reject malformed or tampered token")
    void shouldRejectInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalidpayload.invalidsignature";
        assertThat(tokenProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() {
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(shortLivedProvider, "jwtSecret", validSecret);
        ReflectionTestUtils.setField(shortLivedProvider, "jwtExpirationMs", -1000L); // Already expired
        shortLivedProvider.init();

        String token = shortLivedProvider.generateToken(1L, "admin@edtech.com", "Admin");
        assertThat(tokenProvider.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("Should fail initialization if secret is shorter than 256 bits (32 bytes)")
    void shouldFailOnWeakSecret() {
        JwtTokenProvider weakProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(weakProvider, "jwtSecret", "short_secret");
        ReflectionTestUtils.setField(weakProvider, "jwtExpirationMs", 3600000L);

        assertThatThrownBy(weakProvider::init)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 256 bits");
    }
}
