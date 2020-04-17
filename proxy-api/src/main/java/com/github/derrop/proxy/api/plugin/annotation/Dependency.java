package com.github.derrop.proxy.api.plugin.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {

    @NotNull String id();

    int minimumVersion() default -1;

    boolean optional() default false;
}
