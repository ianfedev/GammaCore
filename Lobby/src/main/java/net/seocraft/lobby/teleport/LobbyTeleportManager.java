package net.seocraft.lobby.teleport;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.Lobby;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.logging.Level;

public class LobbyTeleportManager implements TeleportManager {


    @Inject private TranslatableField translatableField;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private UserFormatter userFormatter;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private Lobby instance;

    @Override
    public void spawnTeleport(@NotNull Player player, @Nullable OfflinePlayer offlineTarget, boolean silent) {

        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                FileConfiguration c = this.instance.getConfig();
                World world = Bukkit.getWorld(c.getString("spawn.world"));

                if (world == null) {
                    ChatAlertLibrary.errorChatAlert(
                            player,
                            this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_lobby_error_world"
                            )
                    );
                    return;
                }

                if (offlineTarget != null && player.getName().equalsIgnoreCase(offlineTarget.getName())) {
                    ChatAlertLibrary.errorChatAlert(
                            player,
                            this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_lobby_teleport_same"
                            )
                    );
                    return;
                }


                Location spawnLocation = new Location(
                        world,
                        c.getDouble("spawn.x"),
                        c.getDouble("spawn.y"),
                        c.getDouble("spawn.z"),
                        c.getInt("spawn.yaw"),
                        c.getInt("spawn.pitch")
                );

                if (offlineTarget == null) {
                    player.teleport(spawnLocation);
                    if (!silent) ChatAlertLibrary.infoAlert(
                            player,
                            this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_lobby_spawn_teleport"
                            ) + "."
                    );
                    return;
                }

                Player target = Bukkit.getPlayer(offlineTarget.getName());

                CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                    if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User targetRecord = targetAsyncResponse.getResponse();
                        target.teleport(spawnLocation);
                        if (!silent) ChatAlertLibrary.infoAlert(
                                player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_lobby_spawn_teleport_other"
                                ) + ".".replace(
                                        "%%player%%",
                                        this.userFormatter.getUserFormat(
                                                targetRecord,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.AQUA
                                )
                        );
                    } else {
                        ChatAlertLibrary.errorChatAlert(
                                player,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_lobby_teleport_error"
                                )
                        );
                    }
                });
            } else {
                Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error teleporting player to spawn.", userAsyncResponse.getThrowedException());
                ChatAlertLibrary.errorChatAlert(player);
            }
        });
    }

    @Override
    public void playerTeleport(@NotNull Player sender, @Nullable Player target, boolean silent) {

        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(sender.getDatabaseIdentifier()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                if (target == null) {
                    sendOfflineTarget(sender, user);
                    return;
                }

                CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                    if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User targetRecord = targetAsyncResponse.getResponse();

                        sender.teleport(target);
                        ChatAlertLibrary.infoAlert(
                                sender,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_lobby_teleport_success"
                                ).replace(
                                        "%%player%%",
                                        this.userFormatter.getUserFormat(
                                                targetRecord,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.AQUA
                                )
                        );

                        if (!silent) ChatAlertLibrary.infoAlert(
                                target,
                                this.translatableField.getUnspacedField(
                                        targetRecord.getLanguage(),
                                        "commons_lobby_teleport_target"
                                ).replace(
                                        "%%player%%",
                                        this.userFormatter.getUserFormat(
                                                user,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.AQUA
                                )
                        );

                    } else {
                        ChatAlertLibrary.errorChatAlert(
                                sender,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_lobby_teleport_error"
                                )
                        );
                    }
                });
            } else {
                Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error teleporting player.", userAsyncResponse.getThrowedException());
                ChatAlertLibrary.errorChatAlert(sender);
            }
        });
    }

    @Override
    public void playerTeleportOwn(@NotNull Player sender, @Nullable Player target, boolean silent) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(sender.getDatabaseIdentifier()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                if (target == null) {
                    sendOfflineTarget(sender, user);
                    return;
                }

                CallbackWrapper.addCallback(this.userStorageProvider.findUserByName(target.getName()), targetAsyncResponse -> {
                    if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User targetRecord = targetAsyncResponse.getResponse();

                        target.teleport(sender);
                        ChatAlertLibrary.infoAlert(
                                sender,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_lobby_teleport_own_success"
                                ).replace(
                                        "%%player%%",
                                        this.userFormatter.getUserFormat(
                                                targetRecord,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.AQUA
                                )
                        );

                        if (!silent) ChatAlertLibrary.infoAlert(
                                target,
                                this.translatableField.getUnspacedField(
                                        targetRecord.getLanguage(),
                                        "commons_lobby_teleport_own_target"
                                ).replace(
                                        "%%player%%",
                                        this.userFormatter.getUserFormat(
                                                user,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        ) + ChatColor.AQUA
                                )
                        );

                    } else {
                        ChatAlertLibrary.errorChatAlert(
                                sender,
                                this.translatableField.getUnspacedField(
                                        user.getLanguage(),
                                        "commons_lobby_teleport_error"
                                )
                        );
                    }
                });
            } else {
                Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error teleporting player.", userAsyncResponse.getThrowedException());
                ChatAlertLibrary.errorChatAlert(sender);
            }
        });
    }

    @Override
    public void playerTeleportAll(@NotNull Player player, boolean silent) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                ChatAlertLibrary.infoAlert(
                        player,
                        this.translatableField.getUnspacedField(
                                user.getLanguage(),
                                "commons_lobby_teleport_all_success"
                        )
                );

                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                    try {
                        User playerRecord = this.userStorageProvider.getCachedUserSync(onlinePlayer.getDatabaseIdentifier());
                        onlinePlayer.teleport(player);

                        if (!silent) ChatAlertLibrary.infoAlert(
                                onlinePlayer,
                                this.translatableField.getUnspacedField(
                                        playerRecord.getLanguage(),
                                        "commons_lobby_teleport_all_target"
                                ).replace(
                                        "%%player%%",
                                        this.userFormatter.getUserFormat(
                                                user,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        )
                                )
                        );
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}
                });
            } else {
                Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error teleporting all the players.", userAsyncResponse.getThrowedException());
                ChatAlertLibrary.errorChatAlert(player);
            }
        });
    }

    private void sendOfflineTarget(Player player, User user) {
        ChatAlertLibrary.errorChatAlert(
                player,
                this.translatableField.getUnspacedField(
                        user.getLanguage(),
                        "commons_offline_target"
                )
        );
    }

}
