package com.edtech.crm.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InquiryCategory {
    SCHOLARSHIP("Scholarship"),
    CONCERT("Concert"),
    ADMISSION("Admission"),
    GENERAL_QUERY("General Query");

    private final String value;

    InquiryCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InquiryCategory fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (InquiryCategory category : InquiryCategory.values()) {
            if (category.value.equalsIgnoreCase(value.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + value + ". Allowed values: Scholarship, Concert, Admission, General Query");
    }

    @Override
    public String toString() {
        return value;
    }
}
