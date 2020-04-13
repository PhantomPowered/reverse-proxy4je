package com.github.derrop.proxy.logging;
/*
 * Created by Mc_Ruben on 08.02.2019
 */

import lombok.Getter;

@Getter
public enum LoggingLevel {

    INFORMATION(null),
    ERROR(ConsoleColor.RED),
    WARNING(ConsoleColor.YELLOW),
    DEBUG(ConsoleColor.CYAN);

    LoggingLevel(ConsoleColor color) {
        this.color = color;
    }

    private ConsoleColor color;

}
