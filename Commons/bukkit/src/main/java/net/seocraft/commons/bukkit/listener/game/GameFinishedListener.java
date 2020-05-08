package net.seocraft.commons.bukkit.listener.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.event.GameFinishedEvent;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchDataProvider;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.bukkit.game.match.partial.TeamMember;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class GameFinishedListener implements Listener {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private MatchDataProvider matchDataProvider;
    @Inject private CloudManager cloudManager;
    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private MatchProvider matchProvider;
    @Inject private CommonsBukkit instance;

    @EventHandler
    public void gameFinishedListener(GameFinishedEvent event) {
        try {
            Match gameMatch = event.getMatch();
            gameMatch.setStatus(MatchStatus.FINISHED);

            Set<String> winnerList;
            if (event.getWinnerTeam() !=  null) {
                winnerList = new HashSet<>();
                Team winnerTeam = event.getWinnerTeam();
                for (TeamMember member : winnerTeam.getMembers()) {
                    winnerList.add(member.getUser());
                }
                gameMatch.setWinner(winnerList);
            }

            this.matchProvider.updateMatch(gameMatch);
            this.coreGameManagement.finishMatch(gameMatch);
            String game = "main_lobby";
            Gamemode gamemode = this.gamemodeProvider.getServerGamemode();
            if (gamemode != null) game = gamemode.getLobbyGroup();
            String finalGame = game;
            this.matchDataProvider.getMatchParticipants(gameMatch).forEach(user -> {
                Player player = Bukkit.getPlayer(user.getUsername());
                if (player != null) this.cloudManager.sendPlayerToGroup(player, finalGame);
            });
            Set<Match> matches = this.matchProvider.getServerMatches();

            if (
                    this.instance.getServerRecord().getMatches().size() < this.instance.getServerRecord().getMaxTotal() &&
                    matches.size() < this.instance.getServerRecord().getMaxRunning()
            ) {
                this.coreGameManagement.initializeMatch();
            }
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "[Game API] There was an error updating the match status. ({0})", e.getMessage());
        }
    }
}
