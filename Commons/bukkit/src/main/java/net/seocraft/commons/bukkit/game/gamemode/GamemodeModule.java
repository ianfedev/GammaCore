package net.seocraft.commons.bukkit.game.gamemode;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.game.gamemode.GamemodeCache;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;

public class GamemodeModule extends ProtectedModule {

    @Override
    public void configure() {
        bind(GamemodeCache.class).to(CoreGamemodeCache.class);
        bind(GamemodeProvider.class).to(CoreGamemodeProvider.class);
        expose(GamemodeCache.class);
        expose(GamemodeProvider.class);
    }

}
