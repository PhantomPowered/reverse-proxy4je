package com.github.derrop.proxy.connection.cache.handler;

import com.github.derrop.proxy.connection.PacketConstants;
import com.github.derrop.proxy.connection.cache.CachedPacket;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.util.scoreboard.*;
import com.github.derrop.proxy.util.scoreboard.criteria.IScoreObjectiveCriteria;
import com.github.derrop.proxy.api.connection.PacketSender;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.Team;

public class ScoreboardCache implements PacketCacheHandler {

    private Scoreboard scoreboard = new Scoreboard();

    @Override
    public int[] getPacketIDs() {
        return new int[]{PacketConstants.SCOREBOARD_DISPLAY, PacketConstants.SCOREBOARD_OBJECTIVE, PacketConstants.SCOREBOARD_SCORE, PacketConstants.SCOREBOARD_TEAM};
    }

    @Override
    public void cachePacket(PacketCache packetCache, CachedPacket newPacket) {
        DefinedPacket packet = newPacket.getDeserializedPacket();

        if (packet instanceof ScoreboardObjective) {
            ScoreboardObjective objective = (ScoreboardObjective) packet;

            if (objective.getAction() == 0) {
                ScoreObjective scoreobjective = scoreboard.addScoreObjective(objective.getName(), IScoreObjectiveCriteria.DUMMY);
                scoreobjective.setDisplayName(objective.getValue());
                scoreobjective.setRenderType(objective.getType());
            } else {
                ScoreObjective scoreobjective1 = scoreboard.getObjective(objective.getName());

                if (objective.getAction() == 1) {
                    scoreboard.removeObjective(scoreobjective1);
                } else if (objective.getAction() == 2) {
                    scoreobjective1.setDisplayName(objective.getValue());
                    scoreobjective1.setRenderType(objective.getType());
                }
            }
        } else if (packet instanceof ScoreboardScore) {
            ScoreboardScore scorePacket = (ScoreboardScore) packet;

            ScoreObjective scoreobjective = scoreboard.getObjective(scorePacket.getObjectiveName());

            if (scorePacket.getAction() == 0) {
                Score score = scoreboard.getValueFromObjective(scorePacket.getItemName(), scoreobjective);
                score.setScorePoints(scorePacket.getValue());
            } else if (scorePacket.getAction() == 1) {
                if (scorePacket.getObjectiveName().isEmpty()) {
                    scoreboard.removeObjectiveFromEntity(scorePacket.getItemName(), null);
                } else if (scoreobjective != null) {
                    scoreboard.removeObjectiveFromEntity(scorePacket.getItemName(), scoreobjective);
                }
            }
        } else if (packet instanceof ScoreboardDisplay) {
            ScoreboardDisplay display = (ScoreboardDisplay) packet;

            if (display.getName().isEmpty()) {
                scoreboard.setObjectiveInDisplaySlot(display.getPosition(), null);
            } else {
                ScoreObjective scoreobjective = scoreboard.getObjective(display.getName());
                scoreboard.setObjectiveInDisplaySlot(display.getPosition(), scoreobjective);
            }
        } else if (packet instanceof Team) {
            Team team = (Team) packet;

            ScorePlayerTeam scoreplayerteam;

            if (team.getMode() == 0) {
                scoreplayerteam = scoreboard.createTeam(team.getName());
            } else {
                scoreplayerteam = scoreboard.getTeam(team.getName());
            }

            if (team.getMode() == 0 || team.getMode() == 2) {
                scoreplayerteam.setTeamName(team.getDisplayName());
                scoreplayerteam.setNamePrefix(team.getPrefix());
                scoreplayerteam.setNameSuffix(team.getSuffix());
                scoreplayerteam.setChatFormat(team.getColor());
                scoreplayerteam.func_98298_a(team.getFriendlyFire());
                EnumVisible team$enumvisible = EnumVisible.func_178824_a(team.getNameTagVisibility());

                if (team$enumvisible != null) {
                    scoreplayerteam.setNameTagVisibility(team$enumvisible);
                }
            }

            if (team.getMode() == 0 || team.getMode() == 3) {
                for (String s : team.getPlayers()) {
                    scoreboard.addPlayerToTeam(s, team.getName());
                }
            }

            if (team.getMode() == 4) {
                for (String s1 : team.getPlayers()) {
                    scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
                }
            }

            if (team.getMode() == 1) {
                scoreboard.removeTeam(scoreplayerteam);
            }
        }
    }

    @Override
    public void sendCached(PacketSender con) {
        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            con.sendPacket(new ScoreboardObjective(objective.getName(), objective.getDisplayName(), objective.getRenderType(), (byte) 0));
        }

        for (Score score : this.scoreboard.getScores()) {
            con.sendPacket(new ScoreboardScore(score.getPlayerName(), (byte) 0, score.getObjective().getName(), score.getScorePoints()));
        }

        for (ScorePlayerTeam team : this.scoreboard.getTeams()) {
            con.sendPacket(new Team(
                    team.getRegisteredName(), (byte) 0, team.getTeamName(), team.getColorPrefix(), team.getColorSuffix(),
                    team.getNameTagVisibility().toString().toLowerCase(), null, team.getChatFormat(),
                    (byte) 0, team.getMembershipCollection().toArray(new String[0])
            ));
        }

        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            con.sendPacket(new ScoreboardDisplay((byte) objective.getDisplaySlot(), objective.getName()));
        }
    }

    @Override
    public void onClientSwitch(UserConnection con) {
        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            con.sendPacket(new ScoreboardObjective(objective.getName()));
        }

        for (Score score : this.scoreboard.getScores()) {
            con.sendPacket(new ScoreboardScore(score.getPlayerName(), score.getObjective().getName()));
        }

        for (ScorePlayerTeam team : this.scoreboard.getTeams()) {
            con.sendPacket(new Team(team.getRegisteredName()));
        }

        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            con.sendPacket(new ScoreboardDisplay((byte) objective.getDisplaySlot(), ""));
        }
    }
}
