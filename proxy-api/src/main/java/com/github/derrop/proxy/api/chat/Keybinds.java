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
package com.github.derrop.proxy.api.chat;

import com.github.derrop.proxy.api.chat.component.KeybindComponent;

/**
 * All keybind values supported by vanilla Minecraft.
 * <br>
 * Values may be removed if they are no longer supported.
 *
 * @see KeybindComponent
 */
public interface Keybinds {

    String JUMP = "key.jump";
    String SNEAK = "key.sneak";
    String SPRINT = "key.sprint";
    String LEFT = "key.left";
    String RIGHT = "key.right";
    String BACK = "key.back";
    String FORWARD = "key.forward";

    String ATTACK = "key.attack";
    String PICK_ITEM = "key.pickItem";
    String USE = "key.use";

    String DROP = "key.drop";
    String HOTBAR_1 = "key.hotbar.1";
    String HOTBAR_2 = "key.hotbar.2";
    String HOTBAR_3 = "key.hotbar.3";
    String HOTBAR_4 = "key.hotbar.4";
    String HOTBAR_5 = "key.hotbar.5";
    String HOTBAR_6 = "key.hotbar.6";
    String HOTBAR_7 = "key.hotbar.7";
    String HOTBAR_8 = "key.hotbar.8";
    String HOTBAR_9 = "key.hotbar.9";
    String INVENTORY = "key.inventory";
    String SWAP_HANDS = "key.swapHands";

    String LOAD_TOOLBAR_ACTIVATOR = "key.loadToolbarActivator";
    String SAVE_TOOLBAR_ACTIVATOR = "key.saveToolbarActivator";

    String PLAYERLIST = "key.playerlist";
    String CHAT = "key.chat";
    String COMMAND = "key.command";

    String ADVANCEMENTS = "key.advancements";
    String SPECTATOR_OUTLINES = "key.spectatorOutlines";
    String SCREENSHOT = "key.screenshot";
    String SMOOTH_CAMERA = "key.smoothCamera";
    String FULLSCREEN = "key.fullscreen";
    String TOGGLE_PERSPECTIVE = "key.togglePerspective";
}
