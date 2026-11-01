package com.edtech.crm.service;

import com.edtech.crm.dto.InquiryRequest;
import com.edtech.crm.dto.InquiryResponse;
import com.edtech.crm.dto.InquiryStatusUpdateRequest;
import com.edtech.crm.dto.PageResponse;
import com.edtech.crm.entity.Inquiry;
import com.edtech.crm.entity.InquiryCategory;
import com.edtech.crm.entity.InquiryStatus;
import com.edtech.crm.entity.UserType;
import com.edtech.crm.exception.BadRequestException;
import com.edtech.crm.exception.ResourceNotFoundException;
import com.edtech.crm.repository.InquiryRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InquiryServiceTest {

    @Mock
    private InquiryRepository inquiryRepository;

    private InquiryService inquiryService;

    @BeforeEach
    void setUp() {
        inquiryService = new InquiryService(inquiryRepository);
    }

    @Test
    @DisplayName("Should create inquiry with status NEW, server-generated timestamp, and ignore client overrides")
    void shouldCreateInquirySuccessfully() {
        InquiryRequest request = new InquiryRequest(
                "Jane Doe",
                "Jane.Doe@Example.com ",
                "+1 555-123-4567",
                UserType.PARENT,
                InquiryCategory.SCHOLARSHIP,
                "Need info on scholarship availability."
        );

        Inquiry savedEntity = new Inquiry(
                "Jane Doe",
                "jane.doe@example.com",
                "+1 555-123-4567",
                UserType.PARENT,
                InquiryCategory.SCHOLARSHIP,
                "Need info on scholarship availability."
        );
        savedEntity.setId(100L);
        savedEntity.setStatus(InquiryStatus.NEW);
        savedEntity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(savedEntity);

        InquiryResponse response = inquiryService.createInquiry(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getName()).isEqualTo("Jane Doe");
        assertThat(response.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(response.getStatus()).isEqualTo(InquiryStatus.NEW);
        assertThat(response.getCategory()).isEqualTo(InquiryCategory.SCHOLARSHIP);
        assertThat(response.getUserType()).isEqualTo(UserType.PARENT);

        ArgumentCaptor<Inquiry> captor = ArgumentCaptor.forClass(Inquiry.class);
        verify(inquiryRepository).save(captor.capture());
        Inquiry captured = captor.getValue();
        assertThat(captured.getStatus()).isEqualTo(InquiryStatus.NEW);
        assertThat(captured.getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    @DisplayName("Should retrieve paginated inquiries with filters")
    void shouldGetPaginatedInquiries() {
        Inquiry inquiry = new Inquiry(
                "John Smith", "john@example.com", "1234567890",
                UserType.STUDENT, InquiryCategory.ADMISSION, "Admission details"
        );
        inquiry.setId(1L);
        inquiry.setStatus(InquiryStatus.NEW);
        inquiry.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Page<Inquiry> page = new PageImpl<>(List.of(inquiry));
        when(inquiryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<InquiryResponse> result = inquiryService.getInquiries(
                InquiryCategory.ADMISSION, UserType.STUDENT, InquiryStatus.NEW, "John", 0, 10
        );

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getName()).isEqualTo("John Smith");
    }

    @Test
    @DisplayName("Should throw BadRequestException when page is negative")
    void shouldRejectNegativePageIndex() {
        assertThatThrownBy(() -> inquiryService.getInquiries(null, null, null, null, -1, 10))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Page index must not be less than zero");
    }

    @Test
    @DisplayName("Should retrieve inquiry by ID")
    void shouldGetInquiryById() {
        Inquiry inquiry = new Inquiry(
                "School Admin", "school@edu.org", "9876543210",
                UserType.SCHOOL, InquiryCategory.CONCERT, "Concert venue info"
        );
        inquiry.setId(5L);
        inquiry.setStatus(InquiryStatus.CONTACTED);

        when(inquiryRepository.findById(5L)).thenReturn(Optional.of(inquiry));

        InquiryResponse response = inquiryService.getInquiryById(5L);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getStatus()).isEqualTo(InquiryStatus.CONTACTED);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when inquiry ID is missing")
    void shouldThrowWhenInquiryNotFound() {
        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inquiryService.getInquiryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Inquiry not found with ID: 999");
    }

    @Test
    @DisplayName("Should update inquiry status successfully")
    void shouldUpdateInquiryStatus() {
        Inquiry inquiry = new Inquiry(
                "Alice", "alice@example.com", "5551234567",
                UserType.STUDENT, InquiryCategory.GENERAL_QUERY, "General query message"
        );
        inquiry.setId(7L);
        inquiry.setStatus(InquiryStatus.NEW);

        when(inquiryRepository.findById(7L)).thenReturn(Optional.of(inquiry));
        when(inquiryRepository.save(any(Inquiry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InquiryResponse response = inquiryService.updateInquiryStatus(7L, new InquiryStatusUpdateRequest(InquiryStatus.RESOLVED));

        assertThat(response.getStatus()).isEqualTo(InquiryStatus.RESOLVED);
    }
}
