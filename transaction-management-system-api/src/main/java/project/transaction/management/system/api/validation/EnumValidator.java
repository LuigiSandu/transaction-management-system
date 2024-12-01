package project.transaction.management.system.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<EnumOf, String> {
    private Enum<?>[] enumValues;

    @Override
    public void initialize(EnumOf constraintAnnotation) {
        enumValues = constraintAnnotation.value().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return Arrays.stream(enumValues).anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}
