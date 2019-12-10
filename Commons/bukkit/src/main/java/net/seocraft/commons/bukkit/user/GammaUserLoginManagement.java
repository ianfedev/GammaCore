package net.seocraft.commons.bukkit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.user.UserLoginManagement;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.utils.TimeUtils;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.authentication.AuthenticationAttemptsHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.backend.user.UserLoginRequest;
import net.seocraft.commons.core.backend.user.UserRegisterRequest;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

public class GammaUserLoginManagement implements UserLoginManagement {

    @Inject private CommonsBukkit instance;
    @Inject private ObjectMapper mapper;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;
    @Inject private AuthenticationAttemptsHandler authenticationAttemptsHandler;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private CloudManager cloudManager;

    @Inject private UserLoginRequest userLoginRequest;
    @Inject private UserRegisterRequest userRegisterRequest;

    @Override
    public void loginUser(@NotNull Player player, @NotNull String password) throws IOException {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {

                User user = userAsyncResponse.getResponse();

                if (!isRegistered(player)) {
                    ChatAlertLibrary.errorChatAlert(
                            player,
                            this.translatableField.getField(user.getLanguage(),"authentication_not_registered") +
                                    ChatColor.YELLOW + "/register <" +
                                    this.translatableField.getUnspacedField(user.getLanguage(),"commons_password")
                                    + ">"
                    );
                    return;
                }

                try {
                    this.userLoginRequest.executeRequest(
                            generateRequestJSON(user.getUsername(), password, getPlayerIP(player)),
                            this.serverTokenQuery.getToken()
                    );
                    this.movePlayerToLobby(player, user);
                } catch (Unauthorized unauthorized) {
                    checkUserJoinAttempts(player, user);
                } catch (InternalServerError | NotFound | BadRequest | JsonProcessingException error) {
                    Bukkit.getLogger().log(Level.WARNING,
                            "[Commons Auth] Something went wrong when authenticating player {0} ({1}): {2}",
                            new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
                    error.printStackTrace();
                    Bukkit.getScheduler().runTask(this.instance, () -> player.kickPlayer(ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "authentication_login_error") + ". \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName()));
                }

            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }

        });
    }

    @Override
    public void registerUser(@NotNull Player player, @NotNull String password) throws IOException {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(this.gameSessionManager.getCachedSession(player.getName()).getPlayerId()), userAsyncResponse -> {

            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {

                User user = userAsyncResponse.getResponse();

                if (isRegistered(player)) {
                    ChatAlertLibrary.errorChatAlert(player,
                            this.translatableField.getField(user.getLanguage(),"authentication_already_registered") +
                                    ChatColor.YELLOW + "/login <" +
                                    this.translatableField.getUnspacedField(user.getLanguage(),"commons_password")
                                    + ">"
                    );
                }

                if (password.length() > 7) {
                    try {

                        this.userRegisterRequest.executeRequest(
                                generateRequestJSON(user.getUsername(), password, getPlayerIP(player)),
                                this.serverTokenQuery.getToken()
                        );

                        ChatAlertLibrary.infoAlert(player,
                                ChatColor.AQUA +
                                        this.translatableField.getUnspacedField(user.getLanguage(), "authentication_welcome_new")
                                                .replace("%%server%%", ChatColor.YELLOW + "Seocraft Network" + ChatColor.AQUA)
                        );
                        this.cloudManager.sendPlayerToGroup(player, user.getLastLobby());

                    } catch (InternalServerError | Unauthorized | NotFound | BadRequest | JsonProcessingException error) {
                        Bukkit.getLogger().log(Level.WARNING,
                                "[Commons Auth] Something went wrong when authenticating player {0} ({1}): {2}",
                                new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
                        Bukkit.getScheduler().runTask(this.instance, () -> player.kickPlayer(ChatColor.RED +
                                this.translatableField.getUnspacedField(user.getLanguage(), "authentication_register_error") +
                                ". \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName()
                        ));
                    }
                } else {
                    ChatAlertLibrary.errorChatAlert(player,
                            this.translatableField.getUnspacedField(user.getLanguage(),"authentication_password_weak")
                    );
                }
            } else {
                ChatAlertLibrary.errorChatAlert(player);
            }

        });
    }

    @Override
    public void checkUserJoinAttempts(@NotNull Player player, @NotNull User user) {

        this.instance.loginAttempts.put(
                player.getUniqueId(),
                this.instance.loginAttempts.get(player.getUniqueId()) + 1
        );

        if (this.instance.loginAttempts.get(player.getUniqueId()) >= 3) {

            Date remainingDate = TimeUtils.addMinutes(new Date(), 3);

            this.authenticationAttemptsHandler.setAttemptLock(
                    player.getUniqueId().toString(),
                    TimeUtils.getUnixStamp(remainingDate).toString()
            );

            Bukkit.getScheduler().runTask(this.instance, () -> player.kickPlayer(ChatColor.RED +
                    this.translatableField.getUnspacedField(user.getLanguage(),"authentication_too_many_attempts") + "\n\n" + ChatColor.GRAY +
                    this.translatableField.getUnspacedField(user.getLanguage(), "authentication_try_again_delay") +
                    ": " + this.authenticationAttemptsHandler.getAttemptLockDelay(player.getUniqueId().toString())
            ));
            this.instance.loginAttempts.remove(player.getUniqueId());

        } else {
            ChatAlertLibrary.errorChatAlert(player,
                    this.translatableField.getField(user.getLanguage(), "authentication_incorrect_password") +
                            ChatColor.GRAY + "[" + this.instance.loginAttempts.get(player.getUniqueId()) + "/3]"
            );
        }
    }

    private boolean isRegistered(@NotNull Player player) {
        return !this.instance.unregisteredPlayers.contains(player.getUniqueId());
    }

    private @NotNull String generateRequestJSON(@NotNull String user, @NotNull String password, @NotNull String ip) throws JsonProcessingException {
        ObjectNode node = mapper.createObjectNode();
        node.put("username", user);
        node.put("password", password);
        node.put("ip", ip);
        return mapper.writeValueAsString(node);
    }

    private @NotNull String getPlayerIP(@NotNull Player player) {
        return player.getAddress().toString().split(":")[0].replace("/", "");
    }

    private void movePlayerToLobby(@NotNull Player player, @NotNull User user) {
        if (user.getLastGame().equalsIgnoreCase("registrandose")) {
            ChatAlertLibrary.infoAlert(
                    player,
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "authentication_logged_main"
                    ).replace(
                            "%%main_lobby%%",
                            ChatColor.YELLOW +
                            this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_main_lobby"
                            )
                            + ChatColor.AQUA
                    )
            );
            this.cloudManager.sendPlayerToGroup(player, "main_lobby");
        } else {
            ChatAlertLibrary.infoAlert(
                    player,
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "authentication_logged_secondary"
                    ).replace(
                            "%%game%%",
                            ChatColor.YELLOW +
                            user.getLastLobby()
                            + ChatColor.AQUA
                    )
            );
            this.cloudManager.sendPlayerToGroup(player, user.getLastLobby());
        }
    }

}
