package net.md_5.bungee.api.score;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.md_5.bungee.connection.UserConnection;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;

import java.util.*;

@Data
@NoArgsConstructor
public class Scoreboard
{

    /**
     * Unique name for this scoreboard.
     */
    private String name;
    /**
     * Position of this scoreboard.
     */
    private Position position;
    /**
     * Objectives for this scoreboard.
     */
    private final Map<String, Objective> objectives = new HashMap<>();
    /**
     * Scores for this scoreboard.
     */
    private final Map<String, Score> scores = new HashMap<>();
    /**
     * Teams on this board.
     */
    private final Map<String, Team> teams = new HashMap<>();

    public Collection<Objective> getObjectives()
    {
        return Collections.unmodifiableCollection( objectives.values() );
    }

    public Collection<Score> getScores()
    {
        return Collections.unmodifiableCollection( scores.values() );
    }

    public Collection<Team> getTeams()
    {
        return Collections.unmodifiableCollection( teams.values() );
    }

    public void addObjective(Objective objective)
    {
        Preconditions.checkNotNull( objective, "objective" );
        Preconditions.checkArgument( !objectives.containsKey( objective.getName() ), "Objective %s already exists in this scoreboard", objective.getName() );
        objectives.put( objective.getName(), objective );
    }

    public void addScore(Score score)
    {
        Preconditions.checkNotNull( score, "score" );
        scores.put( score.getItemName(), score );
    }

    public Score getScore(String name)
    {
        return scores.get( name );
    }

    public void addTeam(Team team)
    {
        Preconditions.checkNotNull( team, "team" );
        Preconditions.checkArgument( !teams.containsKey( team.getName() ), "Team %s already exists in this scoreboard", team.getName() );
        teams.put( team.getName(), team );
    }

    public Team getTeam(String name)
    {
        return teams.get( name );
    }

    public Objective getObjective(String name)
    {
        return objectives.get( name );
    }

    public void removeObjective(String objectiveName)
    {
        objectives.remove( objectiveName );
    }

    public void removeScore(String scoreName)
    {
        scores.remove( scoreName );
    }

    public void removeTeam(String teamName)
    {
        teams.remove( teamName );
    }

    public void write(UserConnection con) {
        for (Objective objective : new ArrayList<>(this.objectives.values())) {
            con.unsafe().sendPacket(new ScoreboardObjective(objective.getName(), objective.getValue(), ScoreboardObjective.HealthDisplay.fromString(objective.getType()), (byte) 0)); // 0 -> add; 1 -> remove; 2 -> update value and type
        }
        con.unsafe().sendPacket(new ScoreboardDisplay((byte) this.position.ordinal(), this.name));
        for (Score score : new ArrayList<>(this.scores.values())) {
            con.unsafe().sendPacket(new ScoreboardScore(score.getItemName(), (byte) 0, score.getScoreName(), score.getValue())); // 0 -> add; 1 -> remove
        }
        for (Team team : new ArrayList<>(this.teams.values())) {
            con.unsafe().sendPacket(new net.md_5.bungee.protocol.packet.Team(
                    team.getName(), (byte) 0, team.getDisplayName(),
                    team.getPrefix(), team.getSuffix(), team.getNameTagVisibility(),
                    team.getCollisionRule(), team.getColor(), team.getFriendlyFire(),
                    team.getPlayers().toArray(new String[0])
            )); // 0 -> add whole team; 1 -> remove; 2 -> update value and type; 3 -> add only players; 4 -> remove only players
        }
    }

    public void writeClear(UserConnection con) {
        for (Objective objective : new ArrayList<>(this.objectives.values())) {
            con.unsafe().sendPacket(new ScoreboardObjective(objective.getName(), objective.getValue(), ScoreboardObjective.HealthDisplay.fromString(objective.getType()), (byte) 1));
        }
        for (Score score : new ArrayList<>(this.scores.values())) {
            con.unsafe().sendPacket(new ScoreboardScore(score.getItemName(), (byte) 1, score.getScoreName(), score.getValue()));
        }
        for (Team team : new ArrayList<>(this.teams.values())) {
            con.unsafe().sendPacket(new net.md_5.bungee.protocol.packet.Team(team.getName()));
        }
    }

    public void clear()
    {
        name = null;
        position = null;
        objectives.clear();
        scores.clear();
        teams.clear();
    }
}
