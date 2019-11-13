package com.km.peter.payment.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HstyPay {

    String value();

    String defaultValue() default "";

    boolean required() default true;

    String type() default "string";

    int length() default 32;
}
