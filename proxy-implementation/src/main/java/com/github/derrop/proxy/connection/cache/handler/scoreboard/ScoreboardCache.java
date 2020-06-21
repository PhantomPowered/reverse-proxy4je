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
package com.github.derrop.proxy.connection.cache.handler.scoreboard;

import com.github.derrop.proxy.api.connection.player.Player;
import com.github.derrop.proxy.api.network.Packet;
import com.github.derrop.proxy.api.network.PacketSender;
import com.github.derrop.proxy.connection.ConnectedProxyClient;
import com.github.derrop.proxy.connection.cache.PacketCache;
import com.github.derrop.proxy.connection.cache.PacketCacheHandler;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.*;
import com.github.derrop.proxy.connection.player.scoreboard.minecraft.criteria.IScoreObjectiveCriteria;
import com.github.derrop.proxy.protocol.ProtocolIds;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardDisplay;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardObjective;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardScore;
import com.github.derrop.proxy.protocol.play.server.scoreboard.PacketPlayServerScoreboardTeam;

public class ScoreboardCache implements PacketCacheHandler {

    private final Scoreboard scoreboard = new Scoreboard();

    private ScoreboardHandler handler;

    private Player connectedPlayer;

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public int[] getPacketIDs() {
        return new int[]{
                ProtocolIds.ToClient.Play.SCOREBOARD_DISPLAY_OBJECTIVE,
                ProtocolIds.ToClient.Play.SCOREBOARD_OBJECTIVE,
                ProtocolIds.ToClient.Play.SCOREBOARD_SCORE,
                ProtocolIds.ToClient.Play.SCOREBOARD_TEAM
        };
    }

    @Override
    public void cachePacket(PacketCache packetCache, Packet newPacket) {
        if (newPacket instanceof PacketPlayServerScoreboardObjective) {
            PacketPlayServerScoreboardObjective objective = (PacketPlayServerScoreboardObjective) newPacket;

            if (objective.getAction() == 0) {
                ScoreObjective scoreobjective = scoreboard.addScoreObjective(objective.getName(), IScoreObjectiveCriteria.DUMMY);
                scoreobjective.setDisplayName(objective.getValue());
                scoreobjective.setRenderType(objective.getType());

                if (this.handler != null) {
                    this.handler.handleObjectiveCreated(scoreobjective);
                }
            } else {
                ScoreObjective scoreobjective = scoreboard.getObjective(objective.getName());

                if (objective.getAction() == 1) {
                    scoreboard.removeObjective(scoreobjective);
                    if (this.handler != null) {
                        this.handler.handleObjectiveUnregistered(scoreobjective);
                    }
                } else if (objective.getAction() == 2) {
                    scoreobjective.setDisplayName(objective.getValue());
                    scoreobjective.setRenderType(objective.getType());

                    if (this.handler != null) {
                        this.handler.handleObjectiveUpdated(scoreobjective);
                    }
                }
            }
        } else if (newPacket instanceof PacketPlayServerScoreboardScore) {
            PacketPlayServerScoreboardScore scorePacket = (PacketPlayServerScoreboardScore) newPacket;

            ScoreObjective scoreobjective = scoreboard.getObjective(scorePacket.getObjectiveName());

            if (scorePacket.getAction() == 0) {
                Score score = scoreboard.getValueFromObjective(scorePacket.getItemName(), scoreobjective);
                score.setScorePoints(scorePacket.getValue());

                if (this.handler != null) {
                    this.handler.handleScoreUpdated(score);
                }
            } else if (scorePacket.getAction() == 1) {
                if (scorePacket.getObjectiveName().isEmpty()) {
                    scoreboard.removeObjectiveFromEntity(scorePacket.getItemName(), null);
                } else if (scoreobjective != null) {
                    scoreboard.removeObjectiveFromEntity(scorePacket.getItemName(), scoreobjective);
                }

                if (this.handler != null) {
                    this.handler.handleScoreRemoved(scorePacket.getItemName(), scoreobjective);
                }
            }
        } else if (newPacket instanceof PacketPlayServerScoreboardDisplay) {
            PacketPlayServerScoreboardDisplay display = (PacketPlayServerScoreboardDisplay) newPacket;

            if (display.getName().isEmpty()) {
                scoreboard.setObjectiveInDisplaySlot(display.getPosition(), null);
            } else {
                ScoreObjective scoreobjective = scoreboard.getObjective(display.getName());
                scoreboard.setObjectiveInDisplaySlot(display.getPosition(), scoreobjective);
            }
        } else if (newPacket instanceof PacketPlayServerScoreboardTeam) {
            PacketPlayServerScoreboardTeam team = (PacketPlayServerScoreboardTeam) newPacket;

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
                    if (this.handler != null) {
                        this.handler.handleTeamEntryAdded(scoreplayerteam, s);
                    }
                }
            }

            if (team.getMode() == 4) {
                for (String s1 : team.getPlayers()) {
                    scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
                    if (this.handler != null) {
                        this.handler.handleTeamEntryRemoved(scoreplayerteam, s1);
                    }
                }
            }

            if (team.getMode() == 1) {
                scoreboard.removeTeam(scoreplayerteam);
            }

            if (team.getMode() == 0) {
                if (this.handler != null) {
                    this.handler.handleTeamRegistered(scoreplayerteam);
                }
            } else if (team.getMode() == 1) {
                if (this.handler != null) {
                    this.handler.handleTeamUnregistered(scoreplayerteam);
                }
            } else {
                if (this.handler != null) {
                    this.handler.handleTeamUpdated(scoreplayerteam);
                }
            }
        }

        if (this.handler != null) {
            this.handler.handleScoreboardPacket(newPacket);
        }
    }

    @Override
    public void sendCached(PacketSender con, ConnectedProxyClient targetProxyClient) {
        if (con instanceof Player) {
            this.connectedPlayer = (Player) con;
        }

        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            this.sendCreatedObjective(con, objective);
        }

        for (Score score : this.scoreboard.getScores()) {
            this.sendScoreUpdate(con, score.getPlayerName(), score.getObjective().getName(), score.getScorePoints());
        }

        for (ScorePlayerTeam team : this.scoreboard.getTeams()) {
            this.sendTeamCreation(con, team);
        }

        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            con.sendPacket(new PacketPlayServerScoreboardDisplay((byte) objective.getDisplaySlot(), objective.getName()));
        }
    }

    @Override
    public void onClientSwitch(Player con) {
        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            this.sendDeletedObjective(con, objective.getName());
        }

        for (Score score : this.scoreboard.getScores()) {
            this.sendScoreDestroy(con, score.getPlayerName(), score.getObjective().getName());
        }

        for (ScorePlayerTeam team : this.scoreboard.getTeams()) {
            this.sendTeamUpdate(con, new PacketPlayServerScoreboardTeam(team.getRegisteredName()));
        }

        for (ScoreObjective objective : this.scoreboard.getScoreObjectives()) {
            con.sendPacket(new PacketPlayServerScoreboardDisplay((byte) objective.getDisplaySlot(), ""));
        }
    }

    public void setHandler(ScoreboardHandler handler) {
        this.handler = handler;
    }

    public void sendScoreUpdate(String score, String objective, int value) {
        if (this.connectedPlayer != null) {
            this.sendScoreUpdate(this.connectedPlayer, score, objective, value);
        }
    }

    private void sendScoreUpdate(PacketSender sender, String score, String objective, int value) {
        sender.sendPacket(new PacketPlayServerScoreboardScore(score, (byte) 0, objective, value));
    }

    public void sendScoreDestroy(String score, String objective) {
        if (this.connectedPlayer != null) {
            this.sendScoreDestroy(this.connectedPlayer, score, objective);
        }
    }

    private void sendScoreDestroy(PacketSender sender, String score, String objective) {
        sender.sendPacket(new PacketPlayServerScoreboardScore(score, objective));
    }

    public void sendCreatedObjective(ScoreObjective objective) {
        if (this.connectedPlayer != null) {
            this.sendCreatedObjective(this.connectedPlayer, objective);
            this.connectedPlayer.sendPacket(new PacketPlayServerScoreboardDisplay((byte) objective.getDisplaySlot(), ""));
        }
    }

    private void sendCreatedObjective(PacketSender sender, ScoreObjective objective) {
        sender.sendPacket(new PacketPlayServerScoreboardObjective(objective.getName(), objective.getDisplayName(), objective.getRenderType(), (byte) 0));
    }

    public void sendDeletedObjective(String name) {
        if (this.connectedPlayer != null) {
            this.sendDeletedObjective(this.connectedPlayer, name);
        }
    }

    private void sendDeletedObjective(PacketSender sender, String name) {
        sender.sendPacket(new PacketPlayServerScoreboardObjective(name));
    }

    public void sendTeamCreation(ScorePlayerTeam team) {
        if (this.connectedPlayer != null) {
            this.sendTeamCreation(this.connectedPlayer, team);
        }
    }

    private void sendTeamCreation(PacketSender sender, ScorePlayerTeam team) {
        this.sendTeamUpdate((byte) 0, sender, team);
    }

    public void sendTeamUpdate(byte mode, ScorePlayerTeam team) {
        if (this.connectedPlayer != null) {
            this.sendTeamUpdate(mode, this.connectedPlayer, team);
        }
    }

    private void sendTeamUpdate(byte mode, PacketSender sender, ScorePlayerTeam team) {
        this.sendTeamUpdate(sender, new PacketPlayServerScoreboardTeam(
                team.getRegisteredName(), mode, team.getTeamName(), team.getColorPrefix(), team.getColorSuffix(),
                team.getNameTagVisibility().toString().toLowerCase(), null, team.getChatFormat(),
                (byte) 0, team.getMembershipCollection().toArray(new String[0])
        ));
    }

    public void sendTeamUpdate(PacketPlayServerScoreboardTeam team) {
        if (this.connectedPlayer != null) {
            this.sendTeamUpdate(this.connectedPlayer, team);
        }
    }

    private void sendTeamUpdate(PacketSender sender, PacketPlayServerScoreboardTeam team) {
        sender.sendPacket(team);
    }

}
