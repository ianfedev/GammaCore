package net.seocraft.commons.bukkit.listener.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.event.GameStartedEvent;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.Set;

public class GameStartedListener implements Listener {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private CommonsBukkit instance;
    @Inject private MatchProvider matchProvider;
    @Inject private GameStartManager gameStartManager;

    @EventHandler
    public void gameStartedListener(GameStartedEvent event) {
        try {
            Match match = event.getMatch();
            match.setTeams(event.getUpdatableTeam());
            match.setStatus(MatchStatus.STARTING);
            this.coreGameManagement.updateMatch(match);

            Set<Match> serverMatches = this.matchProvider.getServerMatches();

            if (
                    this.instance.getServerRecord().getMatches().size() < this.instance.getServerRecord().getMaxTotal() &&
                    serverMatches.size() < this.instance.getServerRecord().getMaxRunning()
            ) {
                this.coreGameManagement.initializeMatch();
            }
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
            this.gameStartManager.kickErrorPlayers(event.getMatch());
        }
    }
}
