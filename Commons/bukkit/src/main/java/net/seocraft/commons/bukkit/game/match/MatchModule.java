package net.seocraft.commons.bukkit.game.match;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.bukkit.game.match.MatchAssignationProvider;
import net.seocraft.api.bukkit.game.match.MatchCacheManager;
import net.seocraft.api.bukkit.game.match.MatchDataProvider;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.commons.bukkit.game.management.GameMatchFinder;

public class MatchModule extends ProtectedModule {

    @Override
    public void configure() {
        bind(MatchProvider.class).to(GameMatchProvider.class);
        bind(MatchAssignationProvider.class).to(GameAssignationProvider.class);
        bind(MatchCacheManager.class).to(GameMatchCache.class);
        bind(MatchDataProvider.class).to(GameDataProvider.class);
        bind(MatchFinder.class).to(GameMatchFinder.class);
        expose(MatchProvider.class);
        expose(MatchAssignationProvider.class);
        expose(MatchCacheManager.class);
        expose(MatchDataProvider.class);
        expose(MatchFinder.class);
    }

}
