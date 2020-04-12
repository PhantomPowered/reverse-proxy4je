package de.derrop.minecraft.proxy.api.chat;

import de.derrop.minecraft.proxy.api.chat.component.BaseComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class HoverEvent {

    private final Action action;
    private final BaseComponent[] value;

    public enum Action {

        SHOW_TEXT,
        SHOW_ACHIEVEMENT,
        SHOW_ITEM,
        SHOW_ENTITY
    }
}
