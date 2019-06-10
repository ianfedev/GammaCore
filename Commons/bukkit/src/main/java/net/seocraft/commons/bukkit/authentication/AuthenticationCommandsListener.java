package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import me.ggamer55.bcm.basic.Namespace;
import me.ggamer55.bcm.basic.exceptions.ArgumentsParseException;
import me.ggamer55.bcm.basic.exceptions.CommandException;
import me.ggamer55.bcm.basic.exceptions.CommandUsageException;
import me.ggamer55.bcm.basic.exceptions.NoPermissionsException;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.logging.Level;

public class AuthenticationCommandsListener implements Listener {

    @Inject private CommonsBukkit instance;
    @Inject private TranslatableField translator;
    @Inject private SessionHandler sessionHandler;
    @Inject private UserStoreHandler userStoreHandler;

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)  {
        Namespace namespace = new Namespace();
        namespace.setObject(CommandSender.class, "sender", event.getPlayer());
        Player player = event.getPlayer();
        if (this.instance.getConfig().getBoolean("authentication.enabled")) {
            try {
                String userLanguage = this.userStoreHandler.getCachedUserSync(this.sessionHandler.getCachedSession(player.getName()).getPlayerId()).getLanguage();
                if((event.getMessage().equals("/pl")) || (event.getMessage().equals("/plugins"))) {
                    ChatAlertLibrary.infoAlert(
                            player,
                            this.translator.getField(userLanguage,"commons_plugin_developer") + ChatColor.YELLOW + "www.seocraft.net/staff"
                    );
                    event.setCancelled(true);
                } else if(event.getMessage().contains("/register") || event.getMessage().contains("/login")) {
                    try {
                        if (this.instance.parametricCommandHandler.dispatchCommand(namespace, event.getMessage().substring(1))) {
                            event.setCancelled(true);
                        }
                    } catch (CommandException ex) {
                        event.getPlayer().sendMessage(ex.getMessage());
                        Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
                        event.setCancelled(true);
                    } catch (NoPermissionsException ex) {
                        event.getPlayer().sendMessage(ChatColor.RED + ex.getMessage());
                        event.setCancelled(true);
                    } catch (CommandUsageException | ArgumentsParseException ex) {
                        String message = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', ex.getMessage());
                        String[] splittedMessage = message.split("\n");
                        splittedMessage[0] = ChatColor.RED + "Usage: " + splittedMessage[0];
                        for (String s : splittedMessage) {
                            event.getPlayer().sendMessage(s);
                        }
                        event.setCancelled(true);
                    }
                } else {
                    ChatAlertLibrary.errorChatAlert(
                            player,
                            this.translator.getUnspacedField(userLanguage,"commons_commands_disabled") + "."
                    );
                    event.setCancelled(true);
                }
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
                ChatAlertLibrary.errorChatAlert(
                        player,
                        null
                );
                event.setCancelled(true);
            }
        }

    }

}
