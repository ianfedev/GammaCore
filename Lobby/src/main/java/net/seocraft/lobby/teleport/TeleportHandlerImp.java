package net.seocraft.lobby.teleport;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserChat;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import net.seocraft.lobby.Lobby;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportHandlerImp implements TeleportHandler {


    @Inject private TranslatableField translatableField;
    @Inject private UserStoreHandler userStoreHandler;
    @Inject private UserChat userChatHandler;
    @Inject private SessionHandler sessionHandler;
    @Inject private BukkitAPI bukkitAPI;
    @Inject private Lobby instance;

    @Override
    public void spawnTeleport(@NotNull Player player, @Nullable OfflinePlayer offlineTarget, boolean silent) {

        GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());

        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {

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

                CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse -> {
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
                                        this.userChatHandler.getUserFormat(
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
                ChatAlertLibrary.errorChatAlert(player, null);
            }
        });
    }

    @Override
    public void playerTeleport(@NotNull Player sender, @Nullable Player target, boolean silent) {
        GameSession playerSession = this.sessionHandler.getCachedSession(sender.getName());

        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                if (target == null) {
                    sendOfflineTarget(sender, user);
                    return;
                }

                CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse -> {
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
                                        this.userChatHandler.getUserFormat(
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
                                        this.userChatHandler.getUserFormat(
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
                ChatAlertLibrary.errorChatAlert(sender, null);
            }
        });
    }

    @Override
    public void playerTeleportOwn(@NotNull Player sender, @Nullable Player target, boolean silent) {
        GameSession playerSession = this.sessionHandler.getCachedSession(sender.getName());

        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                if (target == null) {
                    sendOfflineTarget(sender, user);
                    return;
                }

                CallbackWrapper.addCallback(this.userStoreHandler.findUserByName(target.getName()), targetAsyncResponse -> {
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
                                        this.userChatHandler.getUserFormat(
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
                                        this.userChatHandler.getUserFormat(
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
                ChatAlertLibrary.errorChatAlert(sender, null);
            }
        });
    }

    @Override
    public void playerTeleportAll(@NotNull Player player, boolean silent) {
        GameSession playerSession = this.sessionHandler.getCachedSession(player.getName());

        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {

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
                    GameSession session = this.sessionHandler.getCachedSession(player.getName());
                    try {
                        User playerRecord = this.userStoreHandler.getCachedUserSync(session.getPlayerId());
                        onlinePlayer.teleport(player);

                        if (!silent) ChatAlertLibrary.infoAlert(
                                onlinePlayer,
                                this.translatableField.getUnspacedField(
                                        playerRecord.getLanguage(),
                                        "commons_lobby_teleport_all_target"
                                ).replace(
                                        "%%player%%",
                                        this.userChatHandler.getUserFormat(
                                                user,
                                                this.bukkitAPI.getConfig().getString("realm")
                                        )
                                )
                        );
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError ignore) {}
                });
            } else {
                ChatAlertLibrary.errorChatAlert(player, null);
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
