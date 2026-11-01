package com.edtech.crm.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    PARENT("Parent"),
    STUDENT("Student"),
    SCHOOL("School");

    private final String value;

    UserType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (UserType type : UserType.values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid userType: " + value + ". Allowed values: Parent, Student, School");
    }

    @Override
    public String toString() {
        return value;
    }
}
