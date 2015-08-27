package com.kms.challenges.rbh.model.validation.annotation;

/**
 * @author tkhuu.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MatchWith {
    String fieldName();

    String errorMessage();
}
