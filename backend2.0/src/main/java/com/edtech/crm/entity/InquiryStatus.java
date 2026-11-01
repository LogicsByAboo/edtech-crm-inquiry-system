package com.edtech.crm.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum InquiryStatus {
    NEW("New"),
    CONTACTED("Contacted"),
    RESOLVED("Resolved");

    private final String value;

    InquiryStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InquiryStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (InquiryStatus status : InquiryStatus.values()) {
            if (status.value.equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + value + ". Allowed values: New, Contacted, Resolved");
    }

    @Override
    public String toString() {
        return value;
    }
}
