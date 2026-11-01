package com.edtech.crm.controller;

import com.edtech.crm.config.CorsConfig;
import com.edtech.crm.config.SecurityConfig;
import com.edtech.crm.dto.InquiryRequest;
import com.edtech.crm.dto.InquiryResponse;
import com.edtech.crm.dto.PageResponse;
import com.edtech.crm.entity.InquiryCategory;
import com.edtech.crm.entity.InquiryStatus;
import com.edtech.crm.entity.UserType;
import com.edtech.crm.exception.GlobalExceptionHandler;
import com.edtech.crm.exception.ResourceNotFoundException;
import com.edtech.crm.security.CustomAccessDeniedHandler;
import com.edtech.crm.security.CustomUserDetailsService;
import com.edtech.crm.security.JwtAuthenticationEntryPoint;
import com.edtech.crm.security.JwtAuthenticationFilter;
import com.edtech.crm.security.JwtTokenProvider;
import com.edtech.crm.service.InquiryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InquiryController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class InquiryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InquiryService inquiryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Public POST /api/inquiries with valid payload should return 201 Created")
    void shouldCreateInquiryPublicly() throws Exception {
        InquiryRequest request = new InquiryRequest(
                "Jane Parent",
                "parent@example.com",
                "9876543210",
                UserType.PARENT,
                InquiryCategory.SCHOLARSHIP,
                "Need scholarship details"
        );

        InquiryResponse response = new InquiryResponse(
                1L, "Jane Parent", "parent@example.com", "9876543210",
                UserType.PARENT, InquiryCategory.SCHOLARSHIP, "Need scholarship details",
                InquiryStatus.NEW, OffsetDateTime.now(ZoneOffset.UTC)
        );

        when(inquiryService.createInquiry(any(InquiryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jane Parent"))
                .andExpect(jsonPath("$.email").value("parent@example.com"))
                .andExpect(jsonPath("$.status").value("New"))
                .andExpect(jsonPath("$.userType").value("Parent"))
                .andExpect(jsonPath("$.category").value("Scholarship"));
    }

    @Test
    @DisplayName("Public POST /api/inquiries with missing required fields should return 400 Bad Request")
    void shouldRejectInquiryWithMissingFields() throws Exception {
        String invalidJson = "{\"name\":\"\",\"email\":\"not-an-email\",\"message\":\"\"}";

        mockMvc.perform(post("/api/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.email").exists())
                .andExpect(jsonPath("$.details.name").exists());
    }

    @Test
    @DisplayName("Public POST /api/inquiries with invalid enum value should return 400 Bad Request")
    void shouldRejectInquiryWithInvalidEnum() throws Exception {
        String badEnumJson = "{" +
                "\"name\":\"Test Person\"," +
                "\"email\":\"person@example.com\"," +
                "\"phone\":\"9876543210\"," +
                "\"userType\":\"InvalidType\"," +
                "\"category\":\"Scholarship\"," +
                "\"message\":\"Valid inquiry message\"" +
                "}";

        mockMvc.perform(post("/api/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badEnumJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /api/inquiries without JWT should return 401 Unauthorized")
    void shouldRejectListingWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/inquiries"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/inquiries with authenticated admin should return 200 and paginated inquiries")
    void shouldReturnInquiriesForAdmin() throws Exception {
        InquiryResponse item = new InquiryResponse(
                10L, "Student A", "student@example.com", "1234567890",
                UserType.STUDENT, InquiryCategory.ADMISSION, "Admission question",
                InquiryStatus.NEW, OffsetDateTime.now(ZoneOffset.UTC)
        );
        PageResponse<InquiryResponse> pageResponse = new PageResponse<>(
                List.of(item), 1L, 1, 0, 10
        );

        when(inquiryService.getInquiries(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/inquiries?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Student A"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/inquiries/{id} for nonexistent ID should return 404 Not Found")
    void shouldReturn404ForNonexistentInquiry() throws Exception {
        when(inquiryService.getInquiryById(999L))
                .thenThrow(new ResourceNotFoundException("Inquiry not found with ID: 999"));

        mockMvc.perform(get("/api/inquiries/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Inquiry not found with ID: 999"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/inquiries/{id}/status with valid status should return 200 OK")
    void shouldUpdateStatusForAdmin() throws Exception {
        InquiryResponse updated = new InquiryResponse(
                5L, "School Admin", "school@edu.org", "9876543210",
                UserType.SCHOOL, InquiryCategory.CONCERT, "Concert question",
                InquiryStatus.CONTACTED, OffsetDateTime.now(ZoneOffset.UTC)
        );

        when(inquiryService.updateInquiryStatus(eq(5L), any())).thenReturn(updated);

        mockMvc.perform(patch("/api/inquiries/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Contacted\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Contacted"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/inquiries/{id}/status with invalid status should return 400 Bad Request")
    void shouldRejectInvalidStatusUpdate() throws Exception {
        mockMvc.perform(patch("/api/inquiries/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"UnknownStatus\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
