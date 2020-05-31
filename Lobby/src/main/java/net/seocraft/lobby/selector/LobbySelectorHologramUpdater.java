package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.lobby.selector.SelectorHologramUpdater;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.creator.hologram.Hologram;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LobbySelectorHologramUpdater implements SelectorHologramUpdater {

    // "gamemodeId" -> ( "language" -> ["holograms"] )
    private final Map<String, Map<String, Set<Hologram>>> holograms = new ConcurrentHashMap<>();
    @Inject private TranslatableField translations;
    @Inject private CloudManager cloudManager;
    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private Plugin plugin;

    @Override
    public void scheduleNewHologramUpdater(@NotNull String gamemodeId, @NotNull String language, @NotNull Hologram hologram) {
        Map<String, Set<Hologram>> hologramsByLang = holograms.getOrDefault(gamemodeId, new ConcurrentHashMap<>());
        Set<Hologram> hologramSet = hologramsByLang.getOrDefault(language, new HashSet<>());
        hologramSet.add(hologram);
        hologramsByLang.put(language, hologramSet);
        holograms.put(gamemodeId, hologramsByLang);
    }

    @Override
    public void updateAll() {
        holograms.forEach((gamemodeId, hologramsByLang) -> {

            Gamemode gamemode;

            try {
                gamemode = gamemodeProvider.findGamemodeByIdSync(gamemodeId);
            } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException exception) {
                Bukkit.getLogger().log(Level.WARNING, "[Lobby] Couldn't update selector holograms", exception);
                return;
            }

            int gamemodeOnlinePlayers = cloudManager.getGamemodeOnlinePlayers(gamemode);

            hologramsByLang.forEach((lang, holograms) -> {

                for (Hologram hologram : holograms) {
                    hologram.setLine(
                            1,
                            ChatColor.YELLOW + "" +
                                    gamemodeOnlinePlayers + " " +
                                    translations.getUnspacedField(lang, "commons_lobby_scoreboard_players")
                    );
                }

            });
        });
    }

    @Override
    public void scheduleUpdater() {

        // updates holograms every 10 seconds (if server is running 20 tps)
        Bukkit.getScheduler().runTaskTimer(plugin, this::updateAll, 0L, 200L);

    }

}
