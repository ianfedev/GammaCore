package net.seocraft.commons.bukkit.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import net.seocraft.commons.bukkit.server.BukkitTokenQuery;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.commons.core.backend.user.UserAccessRequest;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.authentication.AuthenticationAttemptsHandler;
import net.seocraft.commons.bukkit.authentication.AuthenticationLoginListener;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.commons.bukkit.punishment.PunishmentActions;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

public class UserAccessResponse implements Listener {

    @Inject private CommonsBukkit instance;
    @Inject private AuthenticationAttemptsHandler authenticationAttemptsHandler;
    @Inject private AuthenticationLoginListener loginListener;
    @Inject private ObjectMapper mapper;
    @Inject private UserStorageProvider userStorage;
    @Inject private CommonsBukkit bukkit;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private FriendshipProvider friendshipProvider;
    @Inject private UserFormatter userFormatter;
    @Inject private ServerManager serverManager;
    @Inject private UserAccessRequest request;
    @Inject private OnlineStatusManager onlineStatusManager;
    @Inject private BukkitTokenQuery tokenHandler;
    @Inject private PunishmentActions punishmentActions;
    @Inject private TranslatableField translator;
    private static Field playerField;

    static {
        try {
            playerField = CraftHumanEntity.class.getDeclaredField("perm");
            playerField.setAccessible(true);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Internal error where obtaining reflection field.");
        }
    }

    @EventHandler
    public void userAccessResponse(PlayerJoinEvent event) {

        // Define needed player variables
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        String ip = player.getAddress().getAddress().getHostAddress();

        // Create
        ObjectNode node = mapper.createObjectNode();
        node.put("username", player.getName());
        node.put("ip", ip);
        try {

            JsonNode response = this.mapper.readTree(
                    this.request.executeRequest(this.mapper.writeValueAsString(node), tokenHandler.getToken())
            );

            String playerIdentifier = response.get("user").asText();

            User user = this.userStorage.getCachedUserSync(playerIdentifier);
            if (response.get("multi").asBoolean()) {
                // TODO: Handle multi-account issue
            } else {
                // Detect if player has a punishment
                this.punishmentActions.checkBan(user);

                // Execute authentication handler
                if (instance.getConfig().getBoolean("authentication.enabled")) {
                    if (this.authenticationAttemptsHandler.getAttemptStatus(playerId.toString())) {
                        this.loginListener.authenticationLoginListener(
                                player,
                                response.get("registered").asBoolean(),
                                user.getLanguage()
                        );
                    } else {
                        player.kickPlayer(ChatColor.RED +
                                this.translator.getUnspacedField(user.getLanguage(),"authentication_too_many_attempts") + "\n\n" + ChatColor.GRAY +
                                this.translator.getUnspacedField(user.getLanguage(), "authentication_try_again_delay") +
                                ": " + this.authenticationAttemptsHandler.getAttemptLockDelay(player.getUniqueId().toString())
                        );
                    }
                    event.setJoinMessage("");
                }

                playerField.set(player, new UserPermissions(player, user, userStorage, gameSessionManager, translator));

                if (this.instance.getServerRecord().getServerType() == ServerType.LOBBY &&
                Bukkit.getPluginManager().getPlugin("Lobby") != null) {
                    Bukkit.getPluginManager().callEvent(new LobbyConnectionEvent(user, player));
                }

                Server updatableServer = this.instance.getServerRecord();
                updatableServer.getOnlinePlayers().add(playerIdentifier);
                this.serverManager.updateServer(
                        updatableServer
                );
                this.instance.setServerRecord(updatableServer);

            }
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IllegalAccessException | IOException error) {
            error.printStackTrace();
            player.kickPlayer(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName());
            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Something went wrong when logging player {0} ({1}): {2}",
                    new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
        }
    }

}