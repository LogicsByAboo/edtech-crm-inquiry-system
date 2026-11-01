package com.edtech.crm.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public class EnumsConverter {

    @Converter(autoApply = true)
    public static class UserTypeConverter implements AttributeConverter<UserType, String> {
        @Override
        public String convertToDatabaseColumn(UserType attribute) {
            return attribute != null ? attribute.getValue() : null;
        }

        @Override
        public UserType convertToEntityAttribute(String dbData) {
            return dbData != null ? UserType.fromValue(dbData) : null;
        }
    }

    @Converter(autoApply = true)
    public static class InquiryCategoryConverter implements AttributeConverter<InquiryCategory, String> {
        @Override
        public String convertToDatabaseColumn(InquiryCategory attribute) {
            return attribute != null ? attribute.getValue() : null;
        }

        @Override
        public InquiryCategory convertToEntityAttribute(String dbData) {
            return dbData != null ? InquiryCategory.fromValue(dbData) : null;
        }
    }

    @Converter(autoApply = true)
    public static class InquiryStatusConverter implements AttributeConverter<InquiryStatus, String> {
        @Override
        public String convertToDatabaseColumn(InquiryStatus attribute) {
            return attribute != null ? attribute.getValue() : null;
        }

        @Override
        public InquiryStatus convertToEntityAttribute(String dbData) {
            return dbData != null ? InquiryStatus.fromValue(dbData) : null;
        }
    }
}
