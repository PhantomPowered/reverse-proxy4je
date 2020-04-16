package com.github.derrop.proxy.api.network;

import com.github.derrop.proxy.api.connection.ProtocolDirection;
import com.github.derrop.proxy.api.connection.ProtocolState;
import com.github.derrop.proxy.api.event.priority.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketHandler {

    int[] packetIds() default {};

    @NotNull ProtocolState protocolState() default ProtocolState.PLAY;

    @Nullable ProtocolDirection[] directions() default {};

    @NotNull EventPriority priority() default EventPriority.NORMAL;
}
