package de.derrop.minecraft.proxy.api.util;

import de.derrop.minecraft.proxy.api.chat.component.BaseComponent;
import de.derrop.minecraft.proxy.api.connection.ProxiedPlayer;

public interface Title {

    /**
     * Set the title to send to the player.
     *
     * @param text The text to use as the title.
     * @return This title configuration.
     */
    Title title(BaseComponent text);

    /**
     * Set the title to send to the player.
     *
     * @param text The text to use as the title.
     * @return This title configuration.
     */
    Title title(BaseComponent... text);

    /**
     * Set the subtitle to send to the player.
     *
     * @param text The text to use as the subtitle.
     * @return This title configuration.
     */
    Title subTitle(BaseComponent text);

    /**
     * Set the subtitle to send to the player.
     *
     * @param text The text to use as the subtitle.
     * @return This title configuration.
     */
    Title subTitle(BaseComponent... text);

    /**
     * Set the duration in ticks of the fade in effect of the title. Once this
     * period of time is over the title will stay for the amount of time
     * specified in {@link #stay(int)}. The default value for the official
     * Minecraft version is 20 (1 second).
     *
     * @param ticks The amount of ticks (1/20 second) for the fade in effect.
     * @return This title configuration.
     */
    Title fadeIn(int ticks);

    /**
     * Set the duration in ticks how long the title should stay on the screen.
     * Once this period of time is over the title will fade out using the
     * duration specified in {@link #fadeOut(int)}. The default value for the
     * official Minecraft version is 60 (3 seconds).
     *
     * @param ticks The amount of ticks (1/20 second) for the stay effect.
     * @return This title configuration.
     */
    Title stay(int ticks);

    /**
     * Set the duration in ticks of the fade out effect of the title. The
     * default value for the official Minecraft version is 20 (1 second).
     *
     * @param ticks The amount of ticks (1/20 second) for the fade out effect.
     * @return This title configuration.
     */
    Title fadeOut(int ticks);

    /**
     * Remove the currently displayed title from the player's screen. This will
     * keep the currently used display times and will only remove the title.
     *
     * @return This title configuration.
     */
    Title clear();

    /**
     * Remove the currently displayed title from the player's screen and set the
     * configuration back to the default values.
     *
     * @return This title configuration.
     */
    Title reset();

    /**
     * Send this title configuration to the specified player. This is the same
     * as calling {@link ProxiedPlayer#sendTitle(Title)}.
     *
     * @param player The player to send the title to.
     * @return This title configuration.
     */
    Title send(ProxiedPlayer player);
}
