package net.seocraft.commons.bukkit.user;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.TextComponent;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserChat;
import net.seocraft.api.bukkit.user.UserStore;
import net.seocraft.api.shared.models.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UserChatListener implements Listener {

    @Inject private UserStore userStore;
    @Inject private UserChat chatManager;
    @Inject private BukkitAPI bukkitAPI;

    @EventHandler(priority = EventPriority.LOW)
    public void userChatListener(AsyncPlayerChatEvent event) {
        User userData = this.userStore.getUserObjectSync(event.getPlayer().getUniqueId());
        if (event.isCancelled()) { return; }
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
    }
}
