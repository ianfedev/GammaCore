package net.seocraft.commons.bukkit.old.user;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.old.util.ChatAlertLibrary;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UserChatListener implements Listener {

    @Inject private UserStorageProvider userStorageProvider;
    @Inject private UserFormatter chatManager;
    @Inject private BukkitAPI bukkitAPI;

    @EventHandler(priority = EventPriority.LOW)
    public void userChatListener(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) { return; }
        try {
            User userData = this.userStorageProvider.findUserByNameSync(event.getPlayer().getName());
            event.setCancelled(true);
            Bukkit.getOnlinePlayers().forEach( player ->
                    player.spigot().sendMessage(
                            new TextComponent(
                                    this.chatManager.getUserFormat(
                                            userData,
                                            this.bukkitAPI.getConfig().getString("realm")
                                    ) + ": "
                                            + event.getMessage())
                    )
            );
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
            ChatAlertLibrary.errorChatAlert(
                    event.getPlayer(),
                    null
            );
        }
    }
}
