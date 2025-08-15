package com.intern.order.validation.annotations;

import com.intern.order.validation.validators.ValueOfEnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumValidator.class) // Mantiq qaysi klassda ekanligini ko'rsatadi
public @interface ValueOfEnum {
    /**
     * Enum klassini belgilaydi.
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * Xatolik xabari.
     */
    String message() default "must be any of the enum values";

    /**
     * Validatsiya guruhlari.
     */
    Class<?>[] groups() default {};

    /**
     * Qo'shimcha ma'lumot (payload).
     */
    Class<? extends Payload>[] payload() default {};
}