package net.seocraft.commons.bukkit.user;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserChat;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UserChatListener implements Listener {

    @Inject private UserStoreHandler userStoreHandler;
    @Inject private UserChat chatManager;
    @Inject private BukkitAPI bukkitAPI;

    @EventHandler(priority = EventPriority.LOW)
    public void userChatListener(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) { return; }
        try {
            User userData = this.userStoreHandler.findUserByNameSync(event.getPlayer().getName());
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
