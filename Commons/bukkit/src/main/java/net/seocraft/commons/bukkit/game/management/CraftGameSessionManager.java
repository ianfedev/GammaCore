package net.seocraft.commons.bukkit.game.management;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.event.GamePlayerLeaveEvent;
import net.seocraft.api.bukkit.event.GameSpectatorSetEvent;
import net.seocraft.api.bukkit.game.management.*;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

public class CraftGameSessionManager implements GameLoginManager {

    @Inject private CoreGameManagement coreGameManagement;
    @Inject private GameStartManager gameStartManager;
    @Inject private TranslatableField translatableField;
    @Inject private UserFormatter userFormatter;
    @Inject private BukkitAPI bukkitAPI;

    @Override
    public void matchPlayerJoin(@NotNull FinderResult match, @NotNull User user, @NotNull Player player) {
        if (match.isSpectable()) {
            Bukkit.getPluginManager().callEvent(new GameSpectatorSetEvent(match.getMatch(), user, player, false, true));
        } else {
            player.setHealth(20);
            player.setFoodLevel(20);
            player.getInventory().clear();
            try {
                player.teleport(this.coreGameManagement.getLobbyLocation(match.getMatch()));
                player.setGameMode(GameMode.ADVENTURE);
                this.coreGameManagement.addMatchPlayer(
                        match.getMatch().getId(),
                        user
                );
                this.coreGameManagement.addWaitingPlayer(player);

                Bukkit.getOnlinePlayers().forEach(online -> {
                    online.hidePlayer(player);
                    player.hidePlayer(online);
                });

                Set<Player> matchPlayers = this.coreGameManagement.getMatchPlayers(match.getMatch().getId());
                int actualPlayers = matchPlayers.size();
                matchPlayers.addAll(this.coreGameManagement.getMatchSpectators(match.getMatch().getId()));

                for (Player matchPlayer : matchPlayers) {
                    player.showPlayer(matchPlayer);
                    matchPlayer.showPlayer(player);
                    matchPlayer.sendMessage(
                            ChatColor.YELLOW +
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
                                                    ChatColor.AQUA + "" + actualPlayers + ChatColor.YELLOW
                                            )
                                            .replace(
                                                    "%%max%%",
                                                    ChatColor.AQUA + "" + this.coreGameManagement.getSubGamemode().getMaxPlayers() + ChatColor.YELLOW
                                            )

                    );

                    if (actualPlayers >= this.coreGameManagement.getSubGamemode().getMinPlayers()) {
                        this.gameStartManager.startMatchCountdown(match.getMatch());
                    }

                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "[Game API] There was an error paring user {0} to a match. ({1})",
                        new Object[]{user.getUsername(), e.getMessage()});
                player.kickPlayer(ChatColor.RED +  this.translatableField.getUnspacedField(user.getLanguage(), "commons_pairing_error"));
                e.printStackTrace();
            }
        }
    }

    @Override
    public void matchPlayerLeave(@NotNull Match match, @NotNull User user, @NotNull Player player) {

        if (this.coreGameManagement.getSpectatingPlayers().contains(player)) {

            Set<User> matchPlayers = this.coreGameManagement.getMatchUsers(match.getId());
            matchPlayers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));

            matchPlayers.forEach(onlinePlayer -> {
                Player playerRecord = Bukkit.getPlayer(onlinePlayer.getUsername());
                if (playerRecord != null) {
                    ChatAlertLibrary.infoAlert(
                            playerRecord,
                            this.translatableField.getUnspacedField(
                                    onlinePlayer.getLanguage(),
                                    "commons_spectator_leave"
                            ).replace("%%player%%", this.userFormatter.getUserColor(user, this.bukkitAPI.getConfig().getString("realm")) + ChatColor.AQUA)
                    );
                }
            });

            this.coreGameManagement.removeSpectatingPlayer(player);
            this.coreGameManagement.removeMatchPlayer(match.getId(), user);
            return;
        }

        if (this.coreGameManagement.getWaitingPlayers().contains(player)) {
            Set<Player> matchPlayers = this.coreGameManagement.getMatchPlayers(match.getId());
            int actualPlayers = matchPlayers.size();
            matchPlayers.addAll(this.coreGameManagement.getMatchSpectators(match.getId()));

            for (Player matchPlayer : matchPlayers) {
                player.showPlayer(matchPlayer);
                matchPlayer.showPlayer(player);
                matchPlayer.sendMessage(
                        ChatColor.YELLOW +
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
                                                ChatColor.AQUA + "" + actualPlayers + ChatColor.YELLOW
                                        )
                                        .replace(
                                                "%%max%%",
                                                ChatColor.AQUA + "" + this.coreGameManagement.getSubGamemode().getMaxPlayers() + ChatColor.YELLOW
                                        )

                );

            }
            if (actualPlayers < this.coreGameManagement.getSubGamemode().getMinPlayers()) {
                this.gameStartManager.cancelMatchCountdown(match);
            }
        }

        this.coreGameManagement.removeWaitingPlayer(player);
        this.coreGameManagement.removeMatchPlayer(match.getId(), user);
        Bukkit.getPluginManager().callEvent(new GamePlayerLeaveEvent(user));

    }
}
