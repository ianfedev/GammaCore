package net.seocraft.commons.bukkit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.creator.intercept.PacketManager;
import net.seocraft.api.bukkit.event.GamePlayerJoinEvent;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.GameLoginManager;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.session.AuthValidation;
import net.seocraft.api.core.session.MinecraftSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserPermissionChecker;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.authentication.AuthenticationAttemptsHandler;
import net.seocraft.commons.bukkit.authentication.AuthenticationLoginListener;
import net.seocraft.commons.bukkit.punishment.PunishmentActions;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class UserJoinListener implements Listener {

    @Inject
    private ObjectMapper mapper;
    @Inject
    private PunishmentActions punishmentActions;
    @Inject
    private RedisClient redisClient;
    @Inject
    private AuthenticationAttemptsHandler authenticationAttemptsHandler;
    @Inject
    private AuthenticationLoginListener authenticationLoginListener;
    @Inject
    private GameLoginManager gameLoginManager;
    @Inject
    private UserPermissionChecker userPermissionChecker;
    @Inject
    private ServerManager serverManager;
    @Inject
    private CloudManager cloudManager;
    @Inject
    private TranslatableField translatableField;
    @Inject
    private OnlineStatusManager onlineStatusManager;
    @Inject
    private MinecraftSessionManager minecraftSessionManager;
    @Inject
    private CommonsBukkit instance;
    @Inject
    private PacketManager packetManager;
    @Inject
    private UserStorageProvider userStorageProvider;
    private static Field playerField;

    static {
        try {
            playerField = CraftHumanEntity.class.getDeclaredField("perm");
            playerField.setAccessible(true);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Internal error where obtaining reflection field.");
        }
    }

    private Cache<UUID, AuthValidation> usersBeingValidated = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(50)
            .weakValues()
            .build();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            AuthValidation validation = this.minecraftSessionManager.verifyAuthenticationSession(
                    event.getName(),
                    event.getAddress().getHostAddress()
            );

            if (validation.hasMultipleAccounts()) {
                event.setKickMessage(ChatColor.RED + "Sorry, you can not have multiple accounts. Att: Seocraft :)");
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);

                return;
            }

            User user = validation.getValidatedUser();
            this.punishmentActions.checkBan(user);

            usersBeingValidated.put(event.getUniqueId(), validation);
        } catch (IOException | InternalServerError | NotFound | Unauthorized | BadRequest error) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName());
            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Something went wrong when logging player {0} ({1}): {2}",
                    new Object[]{event.getName(), error.getClass().getSimpleName(), error.getMessage()});
            error.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        // should be loaded
        AuthValidation validation = usersBeingValidated.getIfPresent(event.getPlayer().getUniqueId());

        if (validation == null) {
            event.setKickMessage(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Data could not be loaded on the login");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);

            return;
        }

        Player player = event.getPlayer();

        User validatedUser = validation.getValidatedUser();
        player.setDatabaseIdentifier(validatedUser.getId());

        // Cache the user for 2 minutes after it was initially validated
        try {
            userStorageProvider.cacheStoreUser(validatedUser);
        } catch (JsonProcessingException error) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName());
            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Something went wrong when logging player {0} ({1}): {2}",
                    new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
            error.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void userAccessResponse(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.packetManager.injectPlayer(event.getPlayer());
        try {
            // should be loaded
            AuthValidation validation = usersBeingValidated.getIfPresent(event.getPlayer().getUniqueId());

            if (validation == null) {
                player.kickPlayer(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Data could not be loaded on the login");
                return;
            }

            User validatedUser = validation.getValidatedUser();

            this.onlineStatusManager.setPlayerOnlineStatus(validatedUser.getId(), true);
            playerField.set(player, new UserPermissions(player, validatedUser, userPermissionChecker, translatableField));

            if (instance.getConfig().getBoolean("authentication.enabled")) {

                if (validatedUser.getSessionInfo().isPremium()) {
                    this.cloudManager.sendPlayerToGroup(player, validatedUser.getSessionInfo().getLastLobby());
                    event.setJoinMessage("");
                    return;
                }

                executeAuthenticationProcess(
                        player,
                        validatedUser.getLanguage(),
                        validation.isRegistered()
                );
                event.setJoinMessage("");
            }

            executeLobbyCheck(validatedUser, player, event);

            if (this.instance.getServerRecord().getServerType() == ServerType.GAME) {
                executeGameCheck(validatedUser, player);
                event.setJoinMessage("");
            }

            updateServerRecord(validatedUser.getId());
            this.minecraftSessionManager.serverSwitch(validatedUser.getId(), Bukkit.getServerName().split("-")[0]);


        } catch (IOException | InternalServerError | NotFound | Unauthorized | BadRequest | IllegalAccessException error) {
            player.kickPlayer(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName());
            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Something went wrong when logging player {0} ({1}): {2}",
                    new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
            error.printStackTrace();
        }
    }

    private void executeAuthenticationProcess(@NotNull Player player, @NotNull String language, boolean registered) {
        if (!this.authenticationAttemptsHandler.getAttemptStatus(player.getUniqueId().toString())) {
            this.authenticationLoginListener.authenticationLoginListener(
                    player,
                    registered,
                    language
            );

            Bukkit.getScheduler().runTaskLater(this.instance, () -> {
                Player dp = Bukkit.getPlayer(player.getName());
                if (dp != null)
                    player.kickPlayer(ChatColor.RED + this.translatableField.getUnspacedField(language, "authentication_delay_exceeded"));
            }, 20 * 30);

        } else {
            player.kickPlayer(ChatColor.RED +
                    this.translatableField.getUnspacedField(language, "authentication_too_many_attempts") + "\n\n" + ChatColor.GRAY +
                    this.translatableField.getUnspacedField(language, "authentication_try_again_delay") +
                    ": " + this.authenticationAttemptsHandler.getAttemptLockDelay(player.getUniqueId().toString())
            );
        }
    }

    private void executeLobbyCheck(@NotNull User user, @NotNull Player player, @NotNull PlayerJoinEvent event) throws Unauthorized, InternalServerError, BadRequest, NotFound, IOException {
        if (
                this.instance.getServerRecord().getServerType() == ServerType.LOBBY &&
                        Bukkit.getPluginManager().getPlugin("Lobby") != null
        ) {
            event.setJoinMessage("");
            this.minecraftSessionManager.serverSwitch(user.getId(), Bukkit.getServerName().split("-")[0]);
            Bukkit.getPluginManager().callEvent(new LobbyConnectionEvent(user, player));
        }
    }

    private void updateServerRecord(@NotNull String id) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError {
        Server updatableServer = this.instance.getServerRecord();
        updatableServer.getOnlinePlayers().add(id);
        this.serverManager.updateServer(
                updatableServer
        );
        this.instance.setServerRecord(updatableServer);
    }

    private void executeGameCheck(@NotNull User user, @NotNull Player player) throws IOException {
        if (this.instance.pairedGame) {
            if (this.redisClient.existsKey("pairing:" + user.getId())) {
                String pairing = this.redisClient.getString("pairing:" + user.getId());
                FinderResult result = this.mapper.readValue(
                        pairing,
                        FinderResult.class
                );
                this.gameLoginManager.matchPlayerJoin(result, user, player);
                this.redisClient.deleteString("pairing:" + user.getId());
                Bukkit.getPluginManager().callEvent(new GamePlayerJoinEvent(user));
            } else {
                player.kickPlayer(ChatColor.RED + "You were not paired to this server, please try again.");
            }
        } else {
            player.kickPlayer(ChatColor.RED + "This game is being initialized, please try again.");
        }

    }
}
