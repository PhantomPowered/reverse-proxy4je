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
package com.github.derrop.proxy.plugins.gomme;

/**
 * Commented lines cannot be detected easily because Gomme doesn't send them on the 'GoMod' channel.
 * TODO maybe we could just parse it out of the tablist header?
 */
public enum GommeServerType {

    LOBBY("Lobby", "LOBBY", "compass"),
    //CITY_BUILD("CityBuild", "", "workbench"), 1.13.2+ only
    CORES("Cores", "CORES", "beacon"),
    CWCORES("CWCores", "CWCORES", "beacon"),
    JUMP_LEAGUE("JumpLeague", "JL", "diamond_boots"),
    TTT("TTT", "TTT", "stick"),
    SPEED_UHC("SpeedUHC", "SPEEDUHC", "golden_apple"),
    SKY_WARS("SkyWars", "SKYWARS", "grass_block"),
    //ENDER_GAMES("EnderGames", "", "eye_of_ender"),
    GUN_GAME("GunGame", "GUNGAME", "wooden_axe"),
    FFA_HARDCODE("FFF HardCore", "HARDCORE", "diamond_chestplate"),
    COOKIES("Cookies", "COOKIES", "cookie"),
    //GAME_1v1("Game 1vs1", "", "golden_helmet"),
    MASTER_BUILDERS("MasterBuilders", "MASTERBUILDERS", "iron_pickaxe"),
    //SURVIVAL_GAMES("SurvivalGames", "", "iron_sword"),
    //QUICK_SURVIVAL_GAMES("Quick SurvivalGames", "", "iron_sword"),
    BED_WARS("BedWars", "BW", "bed"),
    CWBW("CWBW", "CWBW", "bed"),
    TRAINING("Training", "TRAINING", "armor_stand");

    private final String displayName;
    private final String gommeInternalName;
    private final String icon;

    GommeServerType(String displayName, String gommeInternalName, String icon) {
        this.displayName = displayName;
        this.gommeInternalName = gommeInternalName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGommeInternalName() {
        return gommeInternalName;
    }

    public String getIcon() {
        return icon;
    }

    public static GommeServerType getByGommeName(String name) {
        for (GommeServerType value : values()) {
            if (value.gommeInternalName.equals(name)) {
                return value;
            }
        }
        return null;
    }

}
