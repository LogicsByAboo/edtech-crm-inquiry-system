package com.edtech.crm.dto;

import com.edtech.crm.entity.InquiryStatus;
import jakarta.validation.constraints.NotNull;

public class InquiryStatusUpdateRequest {

    @NotNull(message = "Status is required (allowed: New, Contacted, Resolved)")
    private InquiryStatus status;

    public InquiryStatusUpdateRequest() {
    }

    public InquiryStatusUpdateRequest(InquiryStatus status) {
        this.status = status;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }
}
