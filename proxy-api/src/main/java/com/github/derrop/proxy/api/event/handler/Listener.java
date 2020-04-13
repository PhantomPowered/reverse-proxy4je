package com.github.derrop.proxy.api.event.handler;

import com.github.derrop.proxy.api.event.priority.EventPriority;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

    EventPriority priority() default EventPriority.NORMAL;
}
