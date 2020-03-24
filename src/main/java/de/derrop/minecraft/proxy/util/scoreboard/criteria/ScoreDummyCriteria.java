package de.derrop.minecraft.proxy.util.scoreboard.criteria;

import java.util.List;
import java.util.UUID;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria {
    private final String dummyName;

    public ScoreDummyCriteria(String name) {
        this.dummyName = name;
        IScoreObjectiveCriteria.INSTANCES.put(name, this);
    }

    public String getName() {
        return this.dummyName;
    }

    public int getScoreForPlayers(List<UUID> p_96635_1_) {
        return 0;
    }

    public boolean isReadOnly() {
        return false;
    }

    public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
        return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
    }
}
