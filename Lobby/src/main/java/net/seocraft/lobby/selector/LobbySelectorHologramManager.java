package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorHologramManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorHologramUpdater;
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

import java.util.Optional;

public class LobbySelectorHologramManager implements SelectorHologramManager {

    @Inject private Lobby lobby;
    @Inject private TranslatableField translatableField;
    @Inject private CloudManager cloudManager;
    @Inject private SelectorHologramUpdater hologramUpdater;

    @Override
    public void showSelectorHologram(@NotNull User user) {

        Player player = Bukkit.getPlayer(user.getUsername());

        if (player == null) {
            return;
        }

        lobby.getLobbyNPC().forEach(selector -> {
            World world = Bukkit.getWorld(lobby.getConfig().getString("spawn.world"));

            if (world == null) {
                return;
            }

            Gamemode gamemode = selector.getGamemode();
            String language = user.getLanguage();

            Optional<Hologram> optionalSelectorHologram = hologramUpdater.getHologram(gamemode.getId(), language);
            optionalSelectorHologram.ifPresent(player.getLinkedHolograms()::add);

            if (!optionalSelectorHologram.isPresent()) {

                Location location = new Location(
                        world,
                        selector.getX(),
                        selector.getY() + 1.5,
                        selector.getZ(),
                        (float) selector.getYaw(),
                        (float) selector.getPitch()
                );

                Hologram hologram = new CraftHologram(location, player);
                hologram.addLine(ChatColor.YELLOW + "" + this.cloudManager.getGamemodeOnlinePlayers(gamemode) + " " + this.translatableField.getUnspacedField(language, "commons_lobby_scoreboard_players"));
                hologram.addLine(ChatColor.GREEN + this.translatableField.getUnspacedField(language, "game_" + gamemode.getId() + "_title"));
                hologram.addLine(ChatColor.YELLOW + "" + ChatColor.BOLD + this.translatableField.getUnspacedField(language, "commons_lobby_click_play").toUpperCase());

                hologramUpdater.scheduleNewHologramUpdater(gamemode.getId(), language, hologram);

            }

        });
    }

}
