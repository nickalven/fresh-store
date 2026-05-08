package com.nikos.freshstore.catalog.validator;

import com.nikos.freshstore.catalog.dto.ProductImageRequest;
import com.nikos.freshstore.catalog.constraints.PrimaryImageConstraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class PrimaryImageValidator implements ConstraintValidator<PrimaryImageConstraint, List<ProductImageRequest>> {

    @Override
    public boolean isValid(List<ProductImageRequest> images, ConstraintValidatorContext context) {
        if (images == null || images.isEmpty()) {
            return true;
        }
        long primaryCount = images.stream()
                .filter(ProductImageRequest::isPrimary)
                .count();
        return primaryCount == 1;
    }
}