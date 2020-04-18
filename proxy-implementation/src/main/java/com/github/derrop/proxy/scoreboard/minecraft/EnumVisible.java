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
package com.github.derrop.proxy.scoreboard.minecraft;

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
