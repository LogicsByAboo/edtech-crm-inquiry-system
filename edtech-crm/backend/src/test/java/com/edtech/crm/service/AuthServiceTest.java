package com.edtech.crm.service;

import com.edtech.crm.dto.AdminProfileResponse;
import com.edtech.crm.dto.AuthRequest;
import com.edtech.crm.dto.AuthResponse;
import com.edtech.crm.entity.Admin;
import com.edtech.crm.exception.ResourceNotFoundException;
import com.edtech.crm.repository.AdminRepository;
import com.edtech.crm.security.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(adminRepository, passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("Should successfully authenticate admin with valid credentials and return safe profile")
    void shouldLoginSuccessfully() {
        String rawPassword = "StrongAdminPassword123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        Admin admin = new Admin("System Admin", "admin@edtech.com", encodedPassword);
        admin.setId(10L);

        when(adminRepository.findByEmail("admin@edtech.com")).thenReturn(Optional.of(admin));
        when(tokenProvider.generateToken(10L, "admin@edtech.com", "System Admin")).thenReturn("mock-jwt-token");
        when(tokenProvider.getExpirationMs()).thenReturn(3600000L);

        AuthRequest request = new AuthRequest("admin@edtech.com", rawPassword);
        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600000L);
        assertThat(response.getAdmin()).isNotNull();
        assertThat(response.getAdmin().getId()).isEqualTo(10L);
        assertThat(response.getAdmin().getEmail()).isEqualTo("admin@edtech.com");
        assertThat(response.getAdmin().getName()).isEqualTo("System Admin");
    }

    @Test
    @DisplayName("Should reject login with incorrect password and throw BadCredentialsException")
    void shouldRejectInvalidPassword() {
        String rawPassword = "CorrectPassword123!";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        Admin admin = new Admin("System Admin", "admin@edtech.com", encodedPassword);
        admin.setId(10L);

        when(adminRepository.findByEmail("admin@edtech.com")).thenReturn(Optional.of(admin));

        AuthRequest request = new AuthRequest("admin@edtech.com", "WrongPassword!");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("Should reject login when admin email does not exist")
    void shouldRejectNonexistentEmail() {
        when(adminRepository.findByEmail("notfound@edtech.com")).thenReturn(Optional.empty());

        AuthRequest request = new AuthRequest("notfound@edtech.com", "SomePassword");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("Should return admin profile for current authenticated admin")
    void shouldReturnCurrentAdminProfile() {
        Admin admin = new Admin("Lead Admin", "admin@edtech.com", "hash");
        admin.setId(5L);

        when(adminRepository.findByEmail("admin@edtech.com")).thenReturn(Optional.of(admin));

        AdminProfileResponse profile = authService.getCurrentAdminProfile("admin@edtech.com");

        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isEqualTo(5L);
        assertThat(profile.getName()).isEqualTo("Lead Admin");
        assertThat(profile.getEmail()).isEqualTo("admin@edtech.com");
    }
}
