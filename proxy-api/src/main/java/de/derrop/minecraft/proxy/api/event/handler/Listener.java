package de.derrop.minecraft.proxy.api.event.handler;

import de.derrop.minecraft.proxy.api.event.priority.EventPriority;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

    EventPriority priority() default EventPriority.NORMAL;
}
