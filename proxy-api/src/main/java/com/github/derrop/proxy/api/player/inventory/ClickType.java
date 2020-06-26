package com.github.derrop.proxy.api.player.inventory;

public enum ClickType {

    LEFT,
    SHIFT_LEFT,
    RIGHT,
    SHIFT_RIGHT,
    WINDOW_BORDER_LEFT,
    WINDOW_BORDER_RIGHT,
    MIDDLE,
    NUMBER_KEY,
    DOUBLE_CLICK,
    DROP,
    CONTROL_DROP,
    CREATIVE,

    UNKNOWN,
    ;

    public boolean isKeyboardClick() {
        return (this == ClickType.NUMBER_KEY) || (this == ClickType.DROP) || (this == ClickType.CONTROL_DROP);
    }

    public boolean isCreativeAction() {
        return (this == ClickType.MIDDLE) || (this == ClickType.CREATIVE);
    }

    public boolean isRightClick() {
        return (this == ClickType.RIGHT) || (this == ClickType.SHIFT_RIGHT);
    }

    public boolean isLeftClick() {
        return (this == ClickType.LEFT) || (this == ClickType.SHIFT_LEFT) || (this == ClickType.DOUBLE_CLICK) || (this == ClickType.CREATIVE);
    }

    public boolean isShiftClick() {
        return (this == ClickType.SHIFT_LEFT) || (this == ClickType.SHIFT_RIGHT) || (this == ClickType.CONTROL_DROP);
    }
}
