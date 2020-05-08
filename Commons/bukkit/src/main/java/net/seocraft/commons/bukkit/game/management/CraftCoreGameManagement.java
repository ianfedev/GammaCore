package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.event.GameInvalidationEvent;
import net.seocraft.api.bukkit.event.GameSpectatorSetEvent;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.MapFileManager;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.match.*;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.CountdownTimer;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

public class CraftCoreGameManagement implements CoreGameManagement {

    @Inject private MatchProvider matchProvider;
    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private MapFileManager mapFileManager;
    @Inject private TranslatableField translatableField;
    @Inject private MatchDataProvider matchDataProvider;
    @Inject private MatchTimerProvider matchTimerProvider;
    @Inject private MatchAssignationProvider matchAssignationProvider;
    @Inject private ServerManager serverManager;
    @Inject private CloudManager cloudManager;
    @Inject private CommonsBukkit instance;
    @Inject private Random random;

    @Override
    public void initializeMatch() throws IOException, Unauthorized, NotFound, BadRequest, InternalServerError {

        List<GameMap> playableMaps = new ArrayList<>(this.mapFileManager.getPlayableMaps().keySet());
        Gamemode gamemode = this.gamemodeProvider.getServerGamemode();
        SubGamemode subGamemode = this.gamemodeProvider.getServerSubgamemode();

        if (gamemode != null && subGamemode != null) {
            if (playableMaps.size() <= 0)
                throw new InternalServerError("No playable maps could be found");

            int index = this.random.nextInt(playableMaps.size());
            GameMap map = playableMaps.get(index);
            Match createdMatch = this.matchProvider.createMatch(
                    map.getId(),
                    new HashSet<>(),
                    gamemode.getId(),
                    subGamemode.getId()
            );

            this.mapFileManager.loadMatchWorld(createdMatch);
            this.instance.getServerRecord().addMatch(createdMatch.getId());
            this.serverManager.updateServer(
                    this.instance.getServerRecord()
            );
            this.matchTimerProvider.updateMatchRemaingTime(createdMatch, -1);
        } else {
            throw new InternalServerError("Gamemode or SubGamemode not paired successfuly");
        }
    }

    @Override
    public void finishMatch(@NotNull Match match) {

        String game = "main_lobby";
        try {
            Gamemode gamemode = this.gamemodeProvider.getServerGamemode();
            if (gamemode != null) game = gamemode.getLobbyGroup();
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "[GameAPI] Gamemode could not be parsed at match finish", ex);
        }

        Set<User> players = this.matchDataProvider.getMatchParticipants(match, PlayerType.ACTIVE);
        players.forEach(
                assignation -> this.matchAssignationProvider.assignPlayer(assignation.getId(), match, PlayerType.SPECTATOR)
        );

        this.matchAssignationProvider.clearMatchAssignations(match);
        String finalGame = game;
        this.instance.getServer().getScheduler().runTaskLaterAsynchronously(this.instance, () ->
                players.forEach(user -> {
                    Player p = Bukkit.getPlayer(user.getUsername());
                    if (p != null) this.cloudManager.sendPlayerToGroup(p, finalGame);
                }), 60L);
    }

    @Override
    public void invalidateMatch(@NotNull Match match) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError {

        String game = "main_lobby";
        try {
            Gamemode gamemode = this.gamemodeProvider.getServerGamemode();
            if (gamemode != null) game = gamemode.getLobbyGroup();
        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
            Bukkit.getLogger().log(Level.WARNING, "[GameAPI] Gamemode could not be parsed at match finish", ex);
        }

        match.setStatus(MatchStatus.INVALIDATED);
        this.matchProvider.updateMatch(match);

        this.matchDataProvider.getMatchParticipants(match).forEach(user -> {
            Player player = Bukkit.getPlayer(user.getUsername());
            if (player == null) {
                return;
            }
            Bukkit.getScheduler().runTask(this.instance, () ->
                    Bukkit.getPluginManager().callEvent(new GameSpectatorSetEvent(match, user, player, false))
            );
            ChatAlertLibrary.infoAlert(
                    player,
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_invalidation_success"
                    ) + "."
            );
        });

        String finalGame = game;
        CountdownTimer timer = new CountdownTimer(
                this.instance,
                120,
                () -> Bukkit.getScheduler().runTask(this.instance, () -> Bukkit.getPluginManager().callEvent(new GameInvalidationEvent(match))),
                (time) -> {
                    if (time.isImportantSecond()) {
                        this.matchDataProvider.getMatchParticipants(match).forEach(user -> {
                            Player player = Bukkit.getPlayer(user.getUsername());
                            if (player == null) {
                                return;
                            }
                            ChatAlertLibrary.infoAlert(
                                    player,
                                    this.translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_invalidation_expulse"
                                    ).replace(
                                            "%%seconds%%",
                                            ChatColor.RED + "" + time.getSecondsLeft() + " " + ChatColor.AQUA
                                    )
                            );
                        });
                    }
                },
                () -> {
                    this.matchDataProvider.getMatchParticipants(match).forEach(user -> {
                        Player player = Bukkit.getPlayer(user.getUsername());
                        if (player == null) {
                            return;
                        }
                        this.cloudManager.sendPlayerToGroup(player, finalGame);
                        this.matchAssignationProvider.unassignPlayer(match, player.getDatabaseIdentifier());
                    });
                    this.finishMatch(match);
                }
        );

        timer.scheduleTimer();
    }
}