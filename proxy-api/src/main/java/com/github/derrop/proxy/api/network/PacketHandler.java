package com.github.derrop.proxy.api.network;

import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.event.priority.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketHandler {

    int[] packetIds() default {};

    @NotNull ProtocolState protocolState() default ProtocolState.PLAY;

    @NotNull EventPriority priority() default EventPriority.NORMAL;
}
