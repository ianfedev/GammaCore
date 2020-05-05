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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LobbySelectorHologramUpdater implements SelectorHologramUpdater {

    // "gamemodeId" -> ( "language" -> "hologram" )
    private final Map<String, Map<String, Hologram>> holograms = new ConcurrentHashMap<>();
    @Inject private TranslatableField translations;
    @Inject private CloudManager cloudManager;
    @Inject private GamemodeProvider gamemodeProvider;

    @Override
    public @NotNull Optional<Hologram> getHologram(@NotNull String gamemodeId, @NotNull String language) {
        Map<String, Hologram> hologramsByLang = holograms.get(gamemodeId);
        if (hologramsByLang == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(hologramsByLang.get(language));
    }

    @Override
    public void scheduleNewHologramUpdater(@NotNull String gamemodeId, @NotNull String language, @NotNull Hologram hologram) {
        Map<String, Hologram> hologramByLang = holograms.getOrDefault(gamemodeId, new ConcurrentHashMap<>());
        hologramByLang.put(language, hologram);
        holograms.put(gamemodeId, hologramByLang);
    }

    @Override
    public void updateAll() {
        holograms.forEach((gamemodeId, hologramsByLang) ->
            hologramsByLang.forEach((lang, hologram) -> {
                Gamemode gamemode;

                try {
                    gamemode = gamemodeProvider.getGamemodeSync(gamemodeId);
                    if (gamemode == null) {
                        return;
                    }
                } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException exception) {
                    Bukkit.getLogger().log(Level.WARNING, "[Lobby] Couldn't update selector holograms", exception);
                    return;
                }

                hologram.setLine(
                        1,
                        ChatColor.YELLOW + "" +
                                cloudManager.getGamemodeOnlinePlayers(gamemode) + " " +
                                translations.getUnspacedField(lang, "commons_lobby_scoreboard_players")
                );
            })
        );
    }

}
