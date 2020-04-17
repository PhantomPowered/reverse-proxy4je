package com.github.derrop.proxy.api.plugin.annotation;

import com.github.derrop.proxy.api.plugin.PluginState;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Inject {

    @NotNull PluginState state();
}
