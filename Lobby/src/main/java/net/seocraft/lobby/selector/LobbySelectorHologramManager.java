package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorHologramManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.creator.hologram.CraftHologram;
import org.bukkit.creator.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LobbySelectorHologramManager implements SelectorHologramManager {

    @Inject private Lobby lobby;
    @Inject private TranslatableField translatableField;
    @Inject private CloudManager cloudManager;

    @Override
    public void showSelectorHologram(@NotNull User user) {

        Player player = Bukkit.getPlayer(user.getUsername());

        if (player != null) {
            lobby.getLobbyNPC().forEach(selector -> {
                World world = Bukkit.getWorld(lobby.getConfig().getString("spawn.world"));
                if (world != null) {

                    Location location = new Location(
                            world,
                            selector.getX(),
                            selector.getY() + 1.5,
                            selector.getZ(),
                            (float) selector.getYaw(),
                            (float) selector.getPitch()
                    );

                    Hologram selectorHologram = new CraftHologram(location, player);
                    selectorHologram.addLine(ChatColor.YELLOW + "" + this.cloudManager.getGamemodeOnlinePlayers(selector.getGamemode()) + " " + this.translatableField.getUnspacedField(user.getLanguage(), "commons_lobby_scoreboard_players"));
                    selectorHologram.addLine(ChatColor.GREEN + this.translatableField.getUnspacedField(user.getLanguage(), "game_" + selector.getGamemode().getId() + "_title"));
                    selectorHologram.addLine(ChatColor.YELLOW + "" + ChatColor.BOLD + this.translatableField.getUnspacedField(user.getLanguage(), "commons_lobby_click_play").toUpperCase());
                    // TODO: hmm, creating per player repeating task
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(lobby, () -> selectorHologram.setLine(
                            1,
                            ChatColor.YELLOW + "" + this.cloudManager.getGamemodeOnlinePlayers(selector.getGamemode()) + " " + this.translatableField.getUnspacedField(user.getLanguage(), "commons_lobby_scoreboard_players")
                    ), 0,1200L);
                }
            });
        }
    }

}
