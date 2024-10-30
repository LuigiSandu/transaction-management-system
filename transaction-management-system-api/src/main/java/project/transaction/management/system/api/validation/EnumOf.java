package project.transaction.management.system.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumOf {
    Class<? extends Enum<?>> value();
    String message() default "must be a valid enum value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
