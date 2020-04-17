package com.github.derrop.proxy.plugins.gomme;

public enum GommeGameMode {
    CORES("Cores", "CORES", "beacon"),
    BED_WARS("BedWars", "BW", "bed");

    private String displayName;
    private String gommeInternalName;
    private String icon;

    GommeGameMode(String displayName, String gommeInternalName, String icon) {
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

    public static GommeGameMode getByGommeName(String name) {
        for (GommeGameMode value : values()) {
            if (value.gommeInternalName.equals(name)) {
                return value;
            }
        }
        return null;
    }

}
