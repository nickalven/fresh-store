package com.nikos.freshstore.catalog.constraints;

import com.nikos.freshstore.catalog.validator.PrimaryImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PrimaryImageValidator.class)
public @interface PrimaryImageConstraint {
    String message() default "Exactly one image must be marked as primary";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}