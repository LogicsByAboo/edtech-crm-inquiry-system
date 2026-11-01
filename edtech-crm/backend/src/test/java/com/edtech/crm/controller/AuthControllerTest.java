package com.edtech.crm.controller;

import com.edtech.crm.config.CorsConfig;
import com.edtech.crm.config.SecurityConfig;
import com.edtech.crm.dto.AdminProfileResponse;
import com.edtech.crm.dto.AuthRequest;
import com.edtech.crm.dto.AuthResponse;
import com.edtech.crm.exception.GlobalExceptionHandler;
import com.edtech.crm.security.CustomAccessDeniedHandler;
import com.edtech.crm.security.CustomUserDetailsService;
import com.edtech.crm.security.JwtAuthenticationEntryPoint;
import com.edtech.crm.security.JwtAuthenticationFilter;
import com.edtech.crm.security.JwtTokenProvider;
import com.edtech.crm.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/auth/login with valid credentials returns 200, JWT token, and safe admin profile without password hash")
    void shouldLoginSuccessfully() throws Exception {
        AuthRequest request = new AuthRequest("admin@edtech.com", "AdminPassword123!");
        AdminProfileResponse profile = new AdminProfileResponse(1L, "Main Admin", "admin@edtech.com");
        AuthResponse response = new AuthResponse("mock-jwt-token-string", 3600000L, profile);

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-jwt-token-string"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600000))
                .andExpect(jsonPath("$.admin.id").value(1))
                .andExpect(jsonPath("$.admin.email").value("admin@edtech.com"))
                .andExpect(jsonPath("$.admin.name").value("Main Admin"))
                .andExpect(jsonPath("$.admin.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/login with invalid password returns 401 Unauthorized")
    void shouldRejectInvalidCredentials() throws Exception {
        AuthRequest request = new AuthRequest("admin@edtech.com", "WrongPassword");

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login with blank fields returns 400 Bad Request")
    void shouldRejectBlankLoginFields() throws Exception {
        AuthRequest request = new AuthRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.password").exists());
    }

    @Test
    @DisplayName("GET /api/auth/me without authentication returns 401 Unauthorized")
    void shouldRejectUnauthenticatedMe() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(username = "admin@edtech.com", roles = "ADMIN")
    @DisplayName("GET /api/auth/me with authenticated admin returns current admin profile")
    void shouldReturnAuthenticatedAdminProfile() throws Exception {
        AdminProfileResponse profile = new AdminProfileResponse(1L, "Main Admin", "admin@edtech.com");
        when(authService.getCurrentAdminProfile("admin@edtech.com")).thenReturn(profile);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Main Admin"))
                .andExpect(jsonPath("$.email").value("admin@edtech.com"));
    }
}
