package com.example.loggingstarter.annotation;


public @interface Cached {
    String cacheName() default "defaultCache";
}
