package de.derklaro.minecraft.proxy.event.handler;

import de.derklaro.minecraft.proxy.event.priority.EventPriority;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listener {

    EventPriority priority() default EventPriority.NORMAL;
}
