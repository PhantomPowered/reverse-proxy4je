package com.github.derrop.proxy.api.plugin.annotation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Plugin {

    @NotNull String id();

    @NotNull String displayName() default "";

    int version();

    @NotNull String website() default "";

    @NotNull String description() default "";

    @NotNull String[] authors() default "";

    @NotNull Dependency[] dependencies() default {};
}
