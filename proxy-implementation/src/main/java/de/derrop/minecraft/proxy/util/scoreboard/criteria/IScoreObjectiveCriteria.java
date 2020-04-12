package de.derrop.minecraft.proxy.util.scoreboard.criteria;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IScoreObjectiveCriteria {

    Map<String, IScoreObjectiveCriteria> INSTANCES = Maps.<String, IScoreObjectiveCriteria>newHashMap();
    IScoreObjectiveCriteria DUMMY = new ScoreDummyCriteria("dummy");
    IScoreObjectiveCriteria TRIGGER = new ScoreDummyCriteria("trigger");
    IScoreObjectiveCriteria deathCount = new ScoreDummyCriteria("deathCount");
    IScoreObjectiveCriteria playerKillCount = new ScoreDummyCriteria("playerKillCount");
    IScoreObjectiveCriteria totalKillCount = new ScoreDummyCriteria("totalKillCount");

    String getName();

    int getScoreForPlayers(List<UUID> p_96635_1_);

    boolean isReadOnly();

    IScoreObjectiveCriteria.EnumRenderType getRenderType();

    public static enum EnumRenderType {
        INTEGER("integer"),
        HEARTS("hearts");

        private static final Map<String, EnumRenderType> field_178801_c = Maps.<String, IScoreObjectiveCriteria.EnumRenderType>newHashMap();
        private final String field_178798_d;

        private EnumRenderType(String p_i45548_3_) {
            this.field_178798_d = p_i45548_3_;
        }

        public String func_178796_a() {
            return this.field_178798_d;
        }

        public static IScoreObjectiveCriteria.EnumRenderType func_178795_a(String p_178795_0_) {
            IScoreObjectiveCriteria.EnumRenderType iscoreobjectivecriteria$enumrendertype = field_178801_c.get(p_178795_0_);
            return iscoreobjectivecriteria$enumrendertype == null ? INTEGER : iscoreobjectivecriteria$enumrendertype;
        }

        static {
            for (IScoreObjectiveCriteria.EnumRenderType iscoreobjectivecriteria$enumrendertype : values()) {
                field_178801_c.put(iscoreobjectivecriteria$enumrendertype.func_178796_a(), iscoreobjectivecriteria$enumrendertype);
            }
        }
    }
}
