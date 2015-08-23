package com.kms.challenges.rbh.annotation;

import java.lang.annotation.*;

/**
 * @author tkhuu.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {
    String value();
}
