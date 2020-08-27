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
package com.github.phantompowered.proxy.connection.player.scoreboard.minecraft;

import com.github.phantompowered.proxy.connection.player.scoreboard.minecraft.criteria.ScoreObjectiveCriteria;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Scoreboard {

    private static String[] displaySlots = null;
    private final Map<String, ScoreObjective> scoreObjectives = new ConcurrentHashMap<>();
    private final Map<ScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = new ConcurrentHashMap<>();
    private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = new ConcurrentHashMap<>();
    /* 0 = tab menu, 1 = sidebar, 2 = below name */
    private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
    private final Map<String, ScorePlayerTeam> teams = new ConcurrentHashMap<>();
    private final Map<String, ScorePlayerTeam> teamMemberships = new ConcurrentHashMap<>();

    public static String getObjectiveDisplaySlot(int id) {
        switch (id) {
            case 0:
                return "list";

            case 1:
                return "sidebar";

            case 2:
                return "belowName";

            default:
                return null;
        }
    }

    public static int getObjectiveDisplaySlotNumber(String name) {
        if (name.equalsIgnoreCase("list")) {
            return 0;
        } else if (name.equalsIgnoreCase("sidebar")) {
            return 1;
        } else if (name.equalsIgnoreCase("belowName")) {
            return 2;
        } else {
            return -1;
        }
    }

    public static String[] getDisplaySlotStrings() {
        if (displaySlots == null) {
            displaySlots = new String[19];

            for (int i = 0; i < 19; ++i) {
                displaySlots[i] = getObjectiveDisplaySlot(i);
            }
        }

        return displaySlots;
    }

    public ScoreObjective getObjective(String name) {
        return this.scoreObjectives.get(name);
    }

    public ScoreObjective addScoreObjective(String name, ScoreObjectiveCriteria criteria) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
        } else {
            ScoreObjective scoreobjective = this.getObjective(name);

            if (scoreobjective != null) {
                throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
            } else {
                scoreobjective = new ScoreObjective(this, name, criteria);
                List<ScoreObjective> list = this.scoreObjectiveCriterias.computeIfAbsent(criteria, k -> new ArrayList<>());

                list.add(scoreobjective);
                this.scoreObjectives.put(name, scoreobjective);
                return scoreobjective;
            }
        }
    }

    public Score getValueFromObjective(String name, ScoreObjective objective) {
        if (name.length() > 40) {
            throw new IllegalArgumentException("The player name '" + name + "' is too long!");
        } else {
            Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.computeIfAbsent(name, k -> Maps.newHashMap());
            Score score = map.get(objective);

            if (score == null) {
                score = new Score(this, objective, name);
                map.put(objective, score);
            }

            return score;
        }
    }

    public Collection<Score> getSortedScores(ScoreObjective objective) {
        List<Score> list = new ArrayList<>();
        for (Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
            Score score = map.get(objective);

            if (score != null) {
                list.add(score);
            }
        }

        list.sort(Score.SCORE_COMPARATOR);
        return list;
    }

    public Collection<ScoreObjective> getScoreObjectives() {
        return this.scoreObjectives.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.entitiesScoreObjectives.keySet();
    }

    public void removeObjectiveFromEntity(String name, ScoreObjective objective) {
        if (objective != null) {
            Map<ScoreObjective, Score> map2 = this.entitiesScoreObjectives.get(name);

            if (map2 != null) {
                map2.remove(objective);
            }
        }
    }

    public Collection<Score> getScores() {
        Collection<Map<ScoreObjective, Score>> collection = this.entitiesScoreObjectives.values();
        List<Score> list = new ArrayList<>();
        for (Map<ScoreObjective, Score> map : collection) {
            list.addAll(map.values());
        }

        return list;
    }

    public void removeObjective(ScoreObjective scoreObjective) {
        this.scoreObjectives.remove(scoreObjective.getName());

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveInDisplaySlot(i) == scoreObjective) {
                this.setObjectiveInDisplaySlot(i, null);
            }
        }

        List<ScoreObjective> list = this.scoreObjectiveCriterias.get(scoreObjective.getCriteria());

        if (list != null) {
            list.remove(scoreObjective);
        }

        for (Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
            map.remove(scoreObjective);
        }
    }

    public void setObjectiveInDisplaySlot(int id, ScoreObjective scoreObjective) {
        this.objectiveDisplaySlots[id] = scoreObjective;
        if (scoreObjective != null) {
            scoreObjective.setDisplaySlot(id);
        }
    }

    public ScoreObjective getObjectiveInDisplaySlot(int id) {
        return this.objectiveDisplaySlots[id];
    }

    public ScorePlayerTeam getTeam(String name) {
        return this.teams.get(name);
    }

    public ScorePlayerTeam createTeam(String name) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The team name '" + name + "' is too long!");
        } else {
            ScorePlayerTeam scoreplayerteam = this.getTeam(name);

            if (scoreplayerteam != null) {
                throw new IllegalArgumentException("A team with the name '" + name + "' already exists!");
            } else {
                scoreplayerteam = new ScorePlayerTeam(name);
                this.teams.put(name, scoreplayerteam);
                return scoreplayerteam;
            }
        }
    }

    public void removeTeam(ScorePlayerTeam scorePlayerTeam) {
        this.teams.remove(scorePlayerTeam.getRegisteredName());
        for (String s : scorePlayerTeam.getMembershipCollection()) {
            this.teamMemberships.remove(s);
        }
    }

    public void addPlayerToTeam(String player, String newTeam) {
        if (player.length() > 40) {
            throw new IllegalArgumentException("The player name '" + player + "' is too long!");
        } else if (this.teams.containsKey(newTeam)) {
            ScorePlayerTeam scoreplayerteam = this.getTeam(newTeam);
            if (this.getPlayersTeam(player) != null) {
                this.removePlayerFromTeams(player);
            }

            this.teamMemberships.put(player, scoreplayerteam);
            scoreplayerteam.getMembershipCollection().add(player);
        }
    }

    public boolean removePlayerFromTeams(String player) {
        ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(player);
        if (scoreplayerteam != null) {
            this.removePlayerFromTeam(player, scoreplayerteam);
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String player, ScorePlayerTeam scorePlayerTeam) {
        if (this.getPlayersTeam(player) != scorePlayerTeam) {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team '" + scorePlayerTeam.getRegisteredName() + "'.");
        } else {
            this.teamMemberships.remove(player);
            scorePlayerTeam.getMembershipCollection().remove(player);
        }
    }

    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }

    public Collection<ScorePlayerTeam> getTeams() {
        return this.teams.values();
    }

    public ScorePlayerTeam getPlayersTeam(String player) {
        return this.teamMemberships.get(player);
    }
}
