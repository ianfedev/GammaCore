package net.seocraft.commons.bukkit.game;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameLoginManager;
import net.seocraft.api.bukkit.game.management.MapFileManager;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.bukkit.game.map.MapProvider;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.party.PartyProvider;
import net.seocraft.commons.bukkit.game.gamemode.CoreGamemodeProvider;
import net.seocraft.commons.bukkit.game.management.CraftCoreGameManagement;
import net.seocraft.commons.bukkit.game.management.CraftGameLoginManager;
import net.seocraft.commons.bukkit.game.management.CraftMapFileManager;
import net.seocraft.commons.bukkit.game.management.GameMatchFinder;
import net.seocraft.commons.bukkit.game.map.CoreMapProvider;
import net.seocraft.commons.bukkit.game.match.GameMatchProvider;
import net.seocraft.commons.bukkit.game.party.GamePartyProvider;

public class GameModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(GamemodeProvider.class).to(CoreGamemodeProvider.class);
        bind(MapProvider.class).to(CoreMapProvider.class);
        bind(MapFileManager.class).to(CraftMapFileManager.class);
        bind(MatchProvider.class).to(GameMatchProvider.class);
        bind(PartyProvider.class).to(GamePartyProvider.class);
        bind(GameLoginManager.class).to(CraftGameLoginManager.class);
        bind(CoreGameManagement.class).to(CraftCoreGameManagement.class);
        bind(MatchFinder.class).to(GameMatchFinder.class);
        expose(CoreGameManagement.class);
        expose(MatchFinder.class);
        expose(MapFileManager.class);
        expose(GamemodeProvider.class);
        expose(MapProvider.class);
        expose(MatchProvider.class);
        expose(PartyProvider.class);
    }
}
