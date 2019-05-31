package net.seocraft.commons.bukkit.user;

import com.google.gson.JsonObject;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserChat;
import net.seocraft.api.bukkit.server.ServerTokenQuery;
import net.seocraft.api.bukkit.user.UserPermissions;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.serialization.JsonUtils;
import net.seocraft.api.shared.user.UserAccessRequest;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.authentication.AuthenticationAttemptsHandler;
import net.seocraft.commons.bukkit.authentication.AuthenticationLoginListener;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

public class UserAccessResponse implements Listener {

    @Inject private CommonsBukkit instance;
    @Inject private UserChat chatManager;
    @Inject private AuthenticationAttemptsHandler authenticationAttemptsHandler;
    @Inject private AuthenticationLoginListener loginListener;
    @Inject private JsonUtils parser;
    @Inject private UserStore userStorage;
    @Inject private UserAccessRequest request;
    @Inject private ServerTokenQuery tokenHandler;
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
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String ip = player.getAddress()
                .toString()
                .split(":")[0]
                .replace("/", "");
        JsonObject request = new JsonObject();
        request.addProperty("username", player.getName());
        request.addProperty("ip", ip);
        try {
            String response = this.request.executeRequest(request, tokenHandler.getToken());
            User user = this.userStorage.getUserObjectSync(player.getName());
            if (parser.parseJson(response, "multi").getAsBoolean()) {
                // TODO: Handle multi-account issue
            } else {
                this.userStorage.storeUser(player.getName(), parser.parseObject(response, "user").toString());
                if (instance.getConfig().getBoolean("authentication.enabled")) {
                    if (this.authenticationAttemptsHandler.getAttemptStatus(playerId.toString())) {
                        this.loginListener.authenticationLoginListener(
                                player,
                                parser.parseJson(response, "registered").getAsBoolean(),
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
                    playerField.set(player, new UserPermissions(player));
                }

            }
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IllegalAccessException error) {
            player.kickPlayer(ChatColor.RED + "Error when logging in, please try again. \n\n" + ChatColor.GRAY + "Error Type: " + error.getClass().getSimpleName());
            Bukkit.getLogger().log(Level.SEVERE, "[Commons] Something went wrong when logging player {0} ({1}): {2}",
                    new Object[]{player.getName(), error.getClass().getSimpleName(), error.getMessage()});
        }
    }

}