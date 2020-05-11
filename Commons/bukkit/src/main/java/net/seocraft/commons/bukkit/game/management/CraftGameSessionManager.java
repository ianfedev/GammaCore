package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.event.GamePlayerLeaveEvent;
import net.seocraft.api.bukkit.event.GameSpectatorSetEvent;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.GameLoginManager;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.*;
import net.seocraft.api.bukkit.game.scoreboard.LobbyScoreboardManager;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.bukkit.game.management.menu.SpectatorToolbar;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

public class CraftGameSessionManager implements GameLoginManager {

    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private MatchDataProvider matchDataProvider;
    @Inject private MatchAssignationProvider gameAssignationProvider;
    @Inject private MatchMapProvider matchMapProvider;
    @Inject private GameStartManager gameStartManager;
    @Inject private TranslatableField translatableField;
    @Inject private LobbyScoreboardManager lobbyScoreboardManager;
    @Inject private UserFormatter userFormatter;
    @Inject private BukkitAPI bukkitAPI;

    @Override
    public void matchPlayerJoin(@NotNull FinderResult match, @NotNull User user, @NotNull Player player) {

        if (match.isSpectable()) {
            Bukkit.getPluginManager().callEvent(new GameSpectatorSetEvent(match.getMatch(), user, player, false, true));
            return;
        }

        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        try {
            player.teleport(this.matchMapProvider.getLobbyLocation(match.getMatch()));
            player.setGameMode(GameMode.ADVENTURE);
            this.gameAssignationProvider.assignPlayer(user.getId(), match.getMatch(), PlayerType.HOLDING);

            Bukkit.getOnlinePlayers().forEach(online -> {
                online.hidePlayer(player);
                player.hidePlayer(online);
            });

            int players = 0;
            SubGamemode gamemode = this.gamemodeProvider.getServerSubgamemode();
            if (gamemode != null) players = gamemode.getMaxPlayers();

            Set<User> matchUsers = this.matchDataProvider.getMatchParticipants(match.getMatch(), PlayerType.HOLDING);
            Set<User> matchPlayers = this.matchDataProvider.getMatchParticipants(match.getMatch());

            String joinMessage = ChatColor.YELLOW +
                    this.translatableField.getUnspacedField(user.getLanguage(), "commons_joined_dynamic")
                            .replace(
                                    "%%player%%",
                                    this.userFormatter.getUserColor(
                                            user,
                                            this.bukkitAPI.getConfig().getString("realm")
                                    ) + ChatColor.YELLOW
                            )
                            .replace(
                                    "%%actual%%",
                                    ChatColor.AQUA + "" + matchUsers.size() + ChatColor.YELLOW
                            )
                            .replace(
                                    "%%max%%",
                                    ChatColor.AQUA + "" + players + ChatColor.YELLOW
                            );

            for (User matchPlayer : matchPlayers) {
                Player uPlayer = Bukkit.getPlayer(matchPlayer.getUsername());
                if (uPlayer != null) {
                    player.showPlayer(uPlayer);
                    uPlayer.showPlayer(player);
                    uPlayer.sendMessage(joinMessage);
                }
            }

            this.lobbyScoreboardManager.retrieveGameBoard(match.getMatch(), player, user);
            player.getInventory().setItem(
                    8,
                    SpectatorToolbar.getLobbyReturnItem(
                            user.getLanguage(),
                            translatableField
                    )
            );

            if (gamemode != null && matchUsers.size() >= gamemode.getMinPlayers()) {
                this.gameStartManager.startMatchCountdown(match.getMatch());
            }

        } catch (IOException | InternalServerError | NotFound | Unauthorized | BadRequest e) {
            Bukkit.getLogger().log(Level.WARNING, "[Game API] There was an error paring user {0} to a match. ({1})",
                    new Object[]{user.getUsername(), e.getMessage()});
            player.kickPlayer(ChatColor.RED +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_pairing_error"));
            e.printStackTrace();
        }
    }

    @Override
    public void matchPlayerLeave(@NotNull Match match, @NotNull User user, @NotNull Player player) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {

        MatchAssignation assignation = this.matchDataProvider.getPlayerMatch(player.getDatabaseIdentifier());

        if (assignation != null) {
            Set<User> matchUsers = this.matchDataProvider.getMatchParticipants(match, PlayerType.HOLDING);
            Set<User> matchPlayers = this.matchDataProvider.getMatchParticipants(match);


            int players = 0;
            SubGamemode gamemode = null;
            try {
                gamemode = this.gamemodeProvider.getServerSubgamemode();
            } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
                Bukkit.getLogger().log(Level.WARNING, "[GameAPI] There was an error retreiving Gamemode for stats.", ex);
            }
            if (gamemode != null) players = gamemode.getMaxPlayers();

            if (assignation.getPlayerType() == PlayerType.SPECTATOR) {

                matchPlayers.forEach(onlinePlayer -> {
                    Player playerRecord = Bukkit.getPlayer(onlinePlayer.getUsername());
                    if (playerRecord == null) {
                        return;
                    }
                    ChatAlertLibrary.infoAlert(
                            playerRecord,
                            this.translatableField.getUnspacedField(
                                    onlinePlayer.getLanguage(),
                                    "commons_spectator_leave"
                            ).replace("%%player%%", this.userFormatter.getUserColor(user, this.bukkitAPI.getConfig().getString("realm")) + ChatColor.AQUA)
                    );
                });

                this.gameAssignationProvider.unassignPlayer(match, user.getId());
                return;
            }

            if (assignation.getPlayerType() == PlayerType.HOLDING) {
                String leaveMessage = ChatColor.YELLOW +
                        this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_dynamic")
                                .replace(
                                        "%%player%%",
                                        this.userFormatter.getUserColor(
                                                user,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.YELLOW
                                )
                                .replace(
                                        "%%actual%%",
                                        ChatColor.AQUA + "" + (matchUsers.size() - 1) + ChatColor.YELLOW
                                )
                                .replace(
                                        "%%max%%",
                                        ChatColor.AQUA + "" + players + ChatColor.YELLOW
                                );

                for (User matchPlayer : matchPlayers) {
                    Player uPlayer = Bukkit.getPlayer(matchPlayer.getUsername());
                    if (uPlayer != null) {
                        player.showPlayer(uPlayer);
                        uPlayer.showPlayer(player);
                        uPlayer.sendMessage(leaveMessage);
                    }
                }

                this.lobbyScoreboardManager.retrieveGameBoard(match, player, user);

            }

            if (gamemode != null && matchUsers.size() < gamemode.getMinPlayers()) {
                this.gameStartManager.cancelMatchCountdown(match);
            }

            this.gameAssignationProvider.unassignPlayer(match, user.getId());
            player.getInventory().clear();
            Bukkit.getPluginManager().callEvent(new GamePlayerLeaveEvent(user));
        }

    }
}
