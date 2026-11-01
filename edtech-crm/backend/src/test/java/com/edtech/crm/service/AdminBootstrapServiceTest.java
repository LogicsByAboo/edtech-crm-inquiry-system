package com.edtech.crm.service;

import com.edtech.crm.entity.Admin;
import com.edtech.crm.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapServiceTest {

    @Mock
    private AdminRepository adminRepository;

    private PasswordEncoder passwordEncoder;
    private AdminBootstrapService bootstrapService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        bootstrapService = new AdminBootstrapService(adminRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should bootstrap initial admin when credentials configured and no admin exists")
    void shouldBootstrapNewAdmin() {
        ReflectionTestUtils.setField(bootstrapService, "adminName", "Super Admin");
        ReflectionTestUtils.setField(bootstrapService, "adminEmail", "admin@edtech.com");
        ReflectionTestUtils.setField(bootstrapService, "adminPassword", "Password123!");

        when(adminRepository.existsByEmail("admin@edtech.com")).thenReturn(false);

        bootstrapService.bootstrapAdmin();

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(captor.capture());
        Admin saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("admin@edtech.com");
        assertThat(saved.getName()).isEqualTo("Super Admin");
        assertThat(passwordEncoder.matches("Password123!", saved.getPasswordHash())).isTrue();
    }

    @Test
    @DisplayName("Should be idempotent and not re-create or overwrite existing admin")
    void shouldSkipWhenAdminAlreadyExists() {
        ReflectionTestUtils.setField(bootstrapService, "adminName", "Super Admin");
        ReflectionTestUtils.setField(bootstrapService, "adminEmail", "admin@edtech.com");
        ReflectionTestUtils.setField(bootstrapService, "adminPassword", "Password123!");

        when(adminRepository.existsByEmail("admin@edtech.com")).thenReturn(true);

        bootstrapService.bootstrapAdmin();

        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    @DisplayName("Should skip bootstrap if credentials are empty or blank")
    void shouldSkipWhenCredentialsBlank() {
        ReflectionTestUtils.setField(bootstrapService, "adminEmail", "");
        ReflectionTestUtils.setField(bootstrapService, "adminPassword", "");

        bootstrapService.bootstrapAdmin();

        verify(adminRepository, never()).existsByEmail(anyString());
        verify(adminRepository, never()).save(any(Admin.class));
    }
}
