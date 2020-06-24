package com.github.derrop.proxy.plugins.gommecw.labyconnect.protocol;


public enum EnumConnectionState {
    HELLO(-1, "d"), LOGIN(0, "b"), PLAY(1, "a"), ALL(2, "f", "ALL"), OFFLINE(3, "c");
    private String buttonState;
    private String displayColor;

    private int number;

    EnumConnectionState(int number, String displayColor, String buttonState) {
        this.number = number;
        this.displayColor = displayColor;
        this.buttonState = buttonState;
    }

    EnumConnectionState(int number, String displayColor) {
        this.number = number;
        this.displayColor = displayColor;
        this.buttonState = ("chat_button_state_" + name().toLowerCase());
    }

    public int getNumber() {
        return this.number;
    }

    public String getDisplayColor() {
        return this.displayColor;
    }

    public String getButtonState() {
        return this.buttonState;
    }
}


