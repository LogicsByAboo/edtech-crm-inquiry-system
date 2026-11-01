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
import com.edtech.crm.repository.InquirySpecification;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InquiryService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @Transactional
    public InquiryResponse createInquiry(InquiryRequest request) {
        Inquiry inquiry = new Inquiry(
                request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getPhone().trim(),
                request.getUserType(),
                request.getCategory(),
                request.getMessage().trim()
        );
        inquiry.setStatus(InquiryStatus.NEW);
        inquiry.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        Inquiry saved = inquiryRepository.save(inquiry);
        return InquiryResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<InquiryResponse> getInquiries(
            InquiryCategory category,
            UserType userType,
            InquiryStatus status,
            String search,
            int page,
            int size
    ) {
        if (page < 0) {
            throw new BadRequestException("Page index must not be less than zero");
        }
        if (size <= 0) {
            size = DEFAULT_PAGE_SIZE;
        } else if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Inquiry> spec = InquirySpecification.withFilters(category, userType, status, search);

        Page<Inquiry> inquiryPage = inquiryRepository.findAll(spec, pageable);
        Page<InquiryResponse> dtoPage = inquiryPage.map(InquiryResponse::fromEntity);

        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public InquiryResponse getInquiryById(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with ID: " + id));
        return InquiryResponse.fromEntity(inquiry);
    }

    @Transactional
    public InquiryResponse updateInquiryStatus(Long id, InquiryStatusUpdateRequest request) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with ID: " + id));

        inquiry.setStatus(request.getStatus());
        Inquiry updated = inquiryRepository.save(inquiry);
        return InquiryResponse.fromEntity(updated);
    }
}
