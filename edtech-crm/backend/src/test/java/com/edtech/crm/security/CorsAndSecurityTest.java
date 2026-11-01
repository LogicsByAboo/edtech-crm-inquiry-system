package com.edtech.crm.security;

import com.edtech.crm.config.CorsConfig;
import com.edtech.crm.config.SecurityConfig;
import com.edtech.crm.controller.InquiryController;
import com.edtech.crm.exception.GlobalExceptionHandler;
import com.edtech.crm.service.InquiryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InquiryController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class CorsAndSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InquiryService inquiryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Preflight OPTIONS request from allowed origin http://localhost:5173 should succeed with CORS headers")
    void shouldPermitOptionsPreflightFromAllowedOrigin() throws Exception {
        mockMvc.perform(options("/api/inquiries")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type,Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PATCH,PUT,DELETE,OPTIONS"));
    }

    @Test
    @DisplayName("Preflight OPTIONS request from disallowed origin should be rejected with 403 Forbidden")
    void shouldRejectOptionsPreflightFromDisallowedOrigin() throws Exception {
        mockMvc.perform(options("/api/inquiries")
                        .header(HttpHeaders.ORIGIN, "http://malicious-site.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}
