package net.seocraft.api.bukkit.game.gamemode;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface GamemodeCache {

    void cacheMatch(@NotNull Gamemode gamemode) throws JsonProcessingException;

    @NotNull Set<Gamemode> getCachedGamemodes();

    void invalidateCache(@NotNull String gamemode);

}
