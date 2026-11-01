package com.edtech.crm.dto;

import com.edtech.crm.entity.InquiryCategory;
import com.edtech.crm.entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class InquiryRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 7, max = 30, message = "Phone number must be between 7 and 30 characters")
    @Pattern(regexp = "^[0-9+()\\-\\s]{7,30}$", message = "Phone number contains invalid characters")
    private String phone;

    @NotNull(message = "User type is required (allowed: Parent, Student, School)")
    private UserType userType;

    @NotNull(message = "Category is required (allowed: Scholarship, Concert, Admission, General Query)")
    private InquiryCategory category;

    @NotBlank(message = "Message is required")
    @Size(min = 5, max = 5000, message = "Message must be between 5 and 5000 characters")
    private String message;

    public InquiryRequest() {
    }

    public InquiryRequest(String name, String email, String phone, UserType userType, InquiryCategory category, String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.category = category;
        this.message = message;
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
}
