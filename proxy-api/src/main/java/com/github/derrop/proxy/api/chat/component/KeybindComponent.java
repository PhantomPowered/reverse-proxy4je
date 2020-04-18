/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.api.chat.component;

import com.github.derrop.proxy.api.chat.Keybinds;

public final class KeybindComponent extends BaseComponent {

    /**
     * The keybind identifier to use.
     * <br>
     * Will be replaced with the actual key the client is using.
     */
    private String keybind;

    /**
     * Creates a keybind component from the original to clone it.
     *
     * @param original the original for the new keybind component.
     */
    public KeybindComponent(KeybindComponent original) {
        super(original);
        setKeybind(original.getKeybind());
    }

    /**
     * Creates a keybind component with the passed internal keybind value.
     *
     * @param keybind the keybind value
     * @see Keybinds
     */
    public KeybindComponent(String keybind) {
        setKeybind(keybind);
    }

    public KeybindComponent() {
    }

    @Override
    public BaseComponent duplicate() {
        return new KeybindComponent(this);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        builder.append(getKeybind());
        super.toPlainText(builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        addFormat(builder);
        builder.append(getKeybind());
        super.toLegacyText(builder);
    }

    public String getKeybind() {
        return this.keybind;
    }

    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof KeybindComponent))
            return false;
        final KeybindComponent other = (KeybindComponent) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$keybind = this.getKeybind();
        final Object other$keybind = other.getKeybind();
        if (this$keybind == null ? other$keybind != null : !this$keybind.equals(other$keybind))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof KeybindComponent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $keybind = this.getKeybind();
        result = result * PRIME + ($keybind == null ? 43 : $keybind.hashCode());
        return result;
    }

    public String toString() {
        return "KeybindComponent(keybind=" + this.getKeybind() + ")";
    }
}
