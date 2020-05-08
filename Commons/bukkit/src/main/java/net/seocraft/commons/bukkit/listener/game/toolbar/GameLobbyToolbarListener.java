package net.seocraft.commons.bukkit.listener.game.toolbar;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.logging.Level;

public class GameLobbyToolbarListener implements Listener {

    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private CloudManager cloudManager;

    @EventHandler
    public void gameClickInventoryListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getItemInHand();
        if (
                (event.getAction() == Action.RIGHT_CLICK_AIR ||  event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                 handItem != null && handItem.getType() != Material.AIR && NBTTagHandler.hasString(handItem, "hotbar_accessor")
        ) {
            if (NBTTagHandler.getString(handItem, "hotbar_accessor").equalsIgnoreCase("back_lobby")) {
                String game = "main_lobby";
                try {
                    Gamemode gamemode = this.gamemodeProvider.getServerGamemode();
                    if (gamemode != null) game = gamemode.getLobbyGroup();
                } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
                    Bukkit.getLogger().log(Level.WARNING, "[GameAPI] There was an error retreiving base gamemode.", ex);
                }
                this.cloudManager.sendPlayerToGroup(player, game);
            }
        }
    }

}
