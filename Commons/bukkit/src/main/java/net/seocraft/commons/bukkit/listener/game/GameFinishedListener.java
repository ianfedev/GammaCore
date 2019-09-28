package net.seocraft.commons.bukkit.listener.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.event.GameFinishedEvent;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.bukkit.game.match.partial.TeamMember;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class GameFinishedListener implements Listener {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private CloudManager cloudManager;
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

            this.coreGameManagement.updateMatch(gameMatch);
            this.coreGameManagement.finishMatch(gameMatch);
            this.coreGameManagement.getMatchPlayers(gameMatch.getId()).forEach(player -> this.cloudManager.sendPlayerToGroup(player, this.coreGameManagement.getGamemode().getLobbyGroup()));

            if (
                    this.instance.getServerRecord().getMatches().size() < this.instance.getServerRecord().getMaxTotal() &&
                    this.coreGameManagement.getActualMatches().size() < this.instance.getServerRecord().getMaxRunning()
            ) {
                this.coreGameManagement.initializeMatch();
            }
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "[Game API] There was an error updating the match status. ({0})", e.getMessage());
        }
    }
}
