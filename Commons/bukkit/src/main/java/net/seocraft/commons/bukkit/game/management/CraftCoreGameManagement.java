package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class CraftCoreGameManagement implements CoreGameManagement {

    @Inject private MatchProvider matchProvider;

    private Gamemode gamemode;
    private SubGamemode subGamemode;
    private Set<Player> waitingPlayers;
    private Set<Player> spectatingPlayers;
    private Map<Match, Set<User>> matchAssignation;

    @Override
    public void initializeGameCore(@NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode) {
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.waitingPlayers = new HashSet<>();
        this.spectatingPlayers = new HashSet<>();
        this.matchAssignation = new HashMap<>();
    }

    @Override
    public @NotNull Gamemode getGamemode() {
        return this.gamemode;
    }

    @Override
    public @NotNull SubGamemode getSubGamemode() {
        return this.subGamemode;
    }

    @Override
    public @NotNull Set<Player> getWaitingPlayers() {
        return this.waitingPlayers;
    }

    @Override
    public void addWaitingPlayer(Player player) {
        this.waitingPlayers.add(player);
    }

    @Override
    public void removeWaitingPlayer(Player player) {
        this.waitingPlayers.remove(player);
    }

    @Override
    public @NotNull Set<Player> getSpectatingPlayers() {
        return this.spectatingPlayers;
    }

    @Override
    public void addSpectatingPlayer(Player player) {
        this.spectatingPlayers.add(player);
    }

    @Override
    public void removeSpectatingPlayer(Player player) {
        this.spectatingPlayers.remove(player);
    }

    @Override
    public void initializeMatch(Match match) {
        this.matchAssignation.put(match, new HashSet<>());
    }

    @Override
    public void updateMatch(Match match) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {

        Match updatedMatch = this.matchProvider.updateMatch(match);

        this.matchAssignation.forEach((processMatch, list) -> {
            if (processMatch.getId().equalsIgnoreCase(match.getId())) {
                Set<User> userSet = this.matchAssignation.get(match);
                this.matchAssignation.remove(match);
                this.matchAssignation.put(updatedMatch, userSet);
            }
        });
    }

    @Override
    public void addMatchPlayer(String match, User player) {
        this.matchAssignation.forEach((processMatch, list) -> {
            if (processMatch.getId().equalsIgnoreCase(match)) list.add(player);
        });
    }

    @Override
    public void removeMatchPlayer(String match, User player) {
        this.matchAssignation.forEach((processMatch, list) -> {
            if (processMatch.getId().equalsIgnoreCase(match)) list.remove(player);
        });
    }
}
