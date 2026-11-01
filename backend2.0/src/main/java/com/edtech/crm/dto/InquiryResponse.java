package com.edtech.crm.dto;

import com.edtech.crm.entity.Inquiry;
import com.edtech.crm.entity.InquiryCategory;
import com.edtech.crm.entity.InquiryStatus;
import com.edtech.crm.entity.UserType;
import java.time.OffsetDateTime;

public class InquiryResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserType userType;
    private InquiryCategory category;
    private String message;
    private InquiryStatus status;
    private OffsetDateTime createdAt;

    public InquiryResponse() {
    }

    public InquiryResponse(Long id, String name, String email, String phone, UserType userType,
                           InquiryCategory category, String message, InquiryStatus status,
                           OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.category = category;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static InquiryResponse fromEntity(Inquiry inquiry) {
        if (inquiry == null) {
            return null;
        }
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getPhone(),
                inquiry.getUserType(),
                inquiry.getCategory(),
                inquiry.getMessage(),
                inquiry.getStatus(),
                inquiry.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public InquiryCategory getCategory() {
        return category;
    }

    public void setCategory(InquiryCategory category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
