package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import me.fixeddev.ebcm.exception.CommandException;
import me.fixeddev.ebcm.exception.CommandUsageException;
import me.fixeddev.ebcm.internal.namespace.Namespace;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.IOException;
import java.util.logging.Level;

public class AuthenticationCommandsListener implements Listener {

    @Inject
    private CommonsBukkit instance;
    @Inject
    private TranslatableField translator;
    @Inject
    private UserStorageProvider userStorageProvider;

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        try {
            String userLanguage = this.userStorageProvider.getCachedUserSync(player.getDatabaseIdentifier()).getLanguage();

            if (!event.getMessage().contains("/register") && !event.getMessage().contains("/login")) {
                ChatAlertLibrary.errorChatAlert(
                        player,
                        this.translator.getUnspacedField(userLanguage, "commons_commands_disabled") + "."
                );

                event.setCancelled(true);
                return;
            }

            Namespace namespace = new Namespace();
            namespace.setObject(CommandSender.class, "sender", event.getPlayer());

            try {
                if (this.instance.getDispatcher().execute(namespace, event.getMessage().substring(1))) {
                    event.setCancelled(true);
                }

                return;
            } catch (CommandException ex) {
                event.getPlayer().sendMessage(ex.getMessage());
                Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);

            } catch (NoPermissionsException ex) {
                event.getPlayer().sendMessage(ChatColor.RED + ex.getMessage());
            } catch (CommandUsageException ex) {
                String message = ChatColor.RED + ChatColor.translateAlternateColorCodes('&', ex.getMessage());
                String[] splitMessage = message.split("\n");

                splitMessage[0] = ChatColor.RED + "Usage: " + splitMessage[0];

                for (String s : splitMessage) {
                    event.getPlayer().sendMessage(s);
                }
            }
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException exception) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    null
            );
        }
        event.setCancelled(true);
    }

}
