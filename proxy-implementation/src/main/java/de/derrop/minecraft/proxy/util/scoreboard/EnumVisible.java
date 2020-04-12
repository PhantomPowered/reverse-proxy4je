package de.derrop.minecraft.proxy.util.scoreboard;

import java.util.HashMap;
import java.util.Map;

public enum EnumVisible {
    ALWAYS("always", 0),
    NEVER("never", 1),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3),
    ;

    private static Map<String, EnumVisible> field_178828_g = new HashMap<>();
    public final String field_178830_e;
    public final int field_178827_f;

    public static String[] func_178825_a() {
        return field_178828_g.keySet().toArray(new String[field_178828_g.size()]);
    }

    public static EnumVisible func_178824_a(String p_178824_0_) {
        return field_178828_g.get(p_178824_0_);
    }

    private EnumVisible(String p_i45550_3_, int p_i45550_4_) {
        this.field_178830_e = p_i45550_3_;
        this.field_178827_f = p_i45550_4_;
    }

    static {
        for (EnumVisible team$enumvisible : values()) {
            field_178828_g.put(team$enumvisible.field_178830_e, team$enumvisible);
        }
    }
}