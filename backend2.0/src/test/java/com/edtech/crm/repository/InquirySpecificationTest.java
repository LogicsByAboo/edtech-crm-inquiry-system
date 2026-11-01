package com.edtech.crm.repository;

import com.edtech.crm.entity.Inquiry;
import com.edtech.crm.entity.InquiryCategory;
import com.edtech.crm.entity.InquiryStatus;
import com.edtech.crm.entity.UserType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InquirySpecificationTest {

    @Mock
    private Root<Inquiry> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Test
    @DisplayName("Specification should create predicates for category, userType, status, and combined search")
    void shouldBuildPredicatesProperly() {
        Path categoryPath = mock(Path.class);
        Path userTypePath = mock(Path.class);
        Path statusPath = mock(Path.class);
        Path namePath = mock(Path.class);
        Path phonePath = mock(Path.class);
        Expression lowerNameExp = mock(Expression.class);

        Predicate categoryPred = mock(Predicate.class);
        Predicate userTypePred = mock(Predicate.class);
        Predicate statusPred = mock(Predicate.class);
        Predicate nameLikePred = mock(Predicate.class);
        Predicate phoneLikePred = mock(Predicate.class);
        Predicate searchCombinedPred = mock(Predicate.class);
        Predicate andPred = mock(Predicate.class);

        when(root.get("category")).thenReturn(categoryPath);
        when(root.get("userType")).thenReturn(userTypePath);
        when(root.get("status")).thenReturn(statusPath);
        when(root.get("name")).thenReturn(namePath);
        when(root.get("phone")).thenReturn(phonePath);

        when(cb.equal(categoryPath, InquiryCategory.SCHOLARSHIP)).thenReturn(categoryPred);
        when(cb.equal(userTypePath, UserType.PARENT)).thenReturn(userTypePred);
        when(cb.equal(statusPath, InquiryStatus.NEW)).thenReturn(statusPred);

        when(cb.lower(namePath)).thenReturn(lowerNameExp);
        when(cb.like(eq(lowerNameExp), eq("%test%"))).thenReturn(nameLikePred);
        when(cb.like(eq(phonePath), eq("%test%"))).thenReturn(phoneLikePred);
        when(cb.or(nameLikePred, phoneLikePred)).thenReturn(searchCombinedPred);
        when(cb.and(any(Predicate[].class))).thenReturn(andPred);

        Specification<Inquiry> spec = InquirySpecification.withFilters(
                InquiryCategory.SCHOLARSHIP,
                UserType.PARENT,
                InquiryStatus.NEW,
                "test"
        );

        Predicate result = spec.toPredicate(root, query, cb);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(andPred);
    }
}
