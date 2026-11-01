package com.edtech.crm.repository;

import com.edtech.crm.entity.Inquiry;
import com.edtech.crm.entity.InquiryCategory;
import com.edtech.crm.entity.InquiryStatus;
import com.edtech.crm.entity.UserType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class InquirySpecification {

    private InquirySpecification() {
    }

    public static Specification<Inquiry> withFilters(
            InquiryCategory category,
            UserType userType,
            InquiryStatus status,
            String search
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (userType != null) {
                predicates.add(cb.equal(root.get("userType"), userType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
                Predicate phoneMatch = cb.like(root.get("phone"), pattern);
                predicates.add(cb.or(nameMatch, phoneMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
