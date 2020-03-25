package de.derrop.minecraft.proxy.logging;
/*
 * Created by Mc_Ruben on 08.02.2019
 */

public interface LogHandler {

    void handleLine(String line, String formattedLine);

    void close();

}
