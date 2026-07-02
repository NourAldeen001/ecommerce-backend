package com.ecommerce.ecommerce_backend.common.util;

import com.ecommerce.ecommerce_backend.common.exception.BusinessRuleViolationException;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PaginationValidator {

    private PaginationValidator() {}

    public static void validatePageParams(int page, int size) {
        if(page < 0) {
            throw new BusinessRuleViolationException("Page number cannot be negative");
        }
        if(size < 1 || size > 100) {
            throw new BusinessRuleViolationException("Page size must be between 1 and 100");
        }
    }

    public static String validateSortField(
            Set<String> sortableFields, String sortBy, String defaultSortField) {
        if(sortBy == null || sortBy.isBlank()) return defaultSortField;
        if (!sortableFields.contains(sortBy)) {
            throw new BusinessRuleViolationException(
                    "Invalid sort field: '" + sortBy + "'. " +
                    "Allowed: " + sortableFields
            );
        }
        return sortBy;
    }

    public static Sort buildSort(String sortBy, String direction) {
        Sort.Order order = "desc".equalsIgnoreCase(direction)
                ? Sort.Order.desc(sortBy)
                : Sort.Order.asc(sortBy);
        return Sort.by(order);
    }
}
