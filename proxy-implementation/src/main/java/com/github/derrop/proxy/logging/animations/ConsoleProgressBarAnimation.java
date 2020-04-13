package com.github.derrop.proxy.logging.animations;
/*
 * Created by Mc_Ruben on 23.11.2018
 */

import com.github.derrop.proxy.logging.AbstractConsoleAnimation;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a progess bar animation in the console that is updated all 10 milliseconds
 */
public class ConsoleProgressBarAnimation extends AbstractConsoleAnimation {

    @Getter
    @Setter
    private int length;
    @Getter
    @Setter
    private int currentValue;
    private char progressChar;
    private String prefix;
    private String suffix;
    private char lastProgressChar;

    /**
     * Creates a new {@link ConsoleProgressBarAnimation}
     *
     * @param fullLength       the maximum of the animation
     * @param startValue       the initial value for this animation
     * @param progressChar     the {@link Character} for each percent in the animation
     * @param lastProgressChar the {@link Character} which is at the last position of the animation
     * @param prefix           the prefix for this animation
     * @param suffix           the suffix for this animation
     */
    public ConsoleProgressBarAnimation(int fullLength, int startValue, char progressChar, char lastProgressChar, String prefix, String suffix) {
        this.length = fullLength;
        this.currentValue = startValue;
        this.progressChar = progressChar;
        this.lastProgressChar = lastProgressChar;
        this.prefix = prefix;
        this.suffix = suffix;

        this.setUpdateInterval(10);
    }

    @Override
    protected boolean handleTick() {
        if (this.currentValue < this.length) {
            this.doUpdate(((double) this.currentValue / (double) this.length) * 100.0D);
            return false;
        }
        this.doUpdate(100D);
        return true;
    }

    protected String formatCurrentValue(long currentValue) {
        return String.valueOf(currentValue);
    }

    protected String formatLength(long length) {
        return String.valueOf(length);
    }

    protected String formatTime(long millis) {
        long seconds = (millis / 1000);
        String min = String.valueOf(seconds / 60);
        String sec = String.valueOf(seconds - ((seconds / 60) * 60));
        if (min.length() == 1)
            min = "0" + min;
        if (sec.length() == 1)
            sec = "0" + sec;
        return min + ":" + sec;
    }

    private void doUpdate(double percent) {
        char[] chars = new char[100];
        for (int i = 0; i < (int) percent; i++) {
            chars[i] = this.progressChar;
        }
        for (int i = (int) percent; i < 100; i++) {
            chars[i] = ' ';
        }
        if ((int) percent > 0) {
            chars[(int) percent - 1] = this.lastProgressChar;
        } else {
            chars[0] = this.lastProgressChar;
        }
        super.print(
                this.format(this.prefix, percent),
                String.valueOf(chars),
                this.format(this.suffix, percent)
        );
    }

    protected String format(String input, double percent) {
        long millis = System.currentTimeMillis() - getStartTime();
        long time = millis / 1000;
        return input == null ? "" : input
                .replace("%value%", formatCurrentValue(this.currentValue))
                .replace("%length%", formatLength(this.length))
                .replace("%percent%", String.format("%.2f", percent))
                .replace("%time%", formatTime(millis))
                .replace("%bips%", String.valueOf(time == 0 ? "0" : (this.currentValue / 1024 * 8) / time)) //bits per second
                .replace("%byps%", String.valueOf(time == 0 ? "0" : (this.currentValue / 1024) / time)); //bytes per second
    }
}