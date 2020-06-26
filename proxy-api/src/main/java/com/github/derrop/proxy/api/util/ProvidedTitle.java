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
package com.github.derrop.proxy.api.util;

import com.github.derrop.proxy.api.player.Player;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import org.jetbrains.annotations.NotNull;

@Deprecated // TODO: fix some shit in here
public interface ProvidedTitle {

    /**
     * Set the title to send to the player.
     *
     * @param text The text to use as the title.
     * @return This title configuration.
     */
    @NotNull
    ProvidedTitle title(@NotNull Component text);

    @NotNull
    default ProvidedTitle title(@NotNull String text) {
        return this.title(TextComponent.of(text));
    }

    /**
     * Set the subtitle to send to the player.
     *
     * @param text The text to use as the subtitle.
     * @return This title configuration.
     */
    @NotNull
    ProvidedTitle subTitle(@NotNull Component text);

    @NotNull
    default ProvidedTitle subTitle(@NotNull String text) {
        return this.subTitle(TextComponent.of(text));
    }

    /**
     * Set the duration in ticks of the fade in effect of the title. Once this
     * period of time is over the title will stay for the amount of time
     * specified in {@link #stay(int)}. The default value for the official
     * Minecraft version is 20 (1 second).
     *
     * @param ticks The amount of ticks (1/20 second) for the fade in effect.
     * @return This title configuration.
     */
    @NotNull
    ProvidedTitle fadeIn(int ticks);

    /**
     * Set the duration in ticks how long the title should stay on the screen.
     * Once this period of time is over the title will fade out using the
     * duration specified in {@link #fadeOut(int)}. The default value for the
     * official Minecraft version is 60 (3 seconds).
     *
     * @param ticks The amount of ticks (1/20 second) for the stay effect.
     * @return This title configuration.
     */
    @NotNull
    ProvidedTitle stay(int ticks);

    /**
     * Set the duration in ticks of the fade out effect of the title. The
     * default value for the official Minecraft version is 20 (1 second).
     *
     * @param ticks The amount of ticks (1/20 second) for the fade out effect.
     * @return This title configuration.
     */
    @NotNull
    ProvidedTitle fadeOut(int ticks);

    /**
     * Remove the currently displayed title from the player's screen. This will
     * keep the currently used display times and will only remove the title.
     *
     * @return This title configuration.
     */
    @NotNull
    ProvidedTitle clear();

    /**
     * Remove the currently displayed title from the player's screen and set the
     * configuration back to the default values.
     *
     * @return This title configuration.
     */
    @NotNull
    ProvidedTitle reset();

    /**
     * Send this title configuration to the specified player. This is the same
     * as calling {@link Player#sendTitle(ProvidedTitle)}.
     *
     * @param player The player to send the title to.
     */
    void send(@NotNull Player player);

    default void send(@NotNull Player... players) {
        for (Player player : players) {
            this.send(player);
        }
    }
}
