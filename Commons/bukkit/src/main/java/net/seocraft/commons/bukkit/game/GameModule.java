package net.seocraft.commons.bukkit.game;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.map.MapProvider;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.party.PartyProvider;
import net.seocraft.commons.bukkit.game.gamemode.CoreGamemodeProvider;
import net.seocraft.commons.bukkit.game.map.CoreMapProvider;
import net.seocraft.commons.bukkit.game.match.GameMatchProvider;
import net.seocraft.commons.bukkit.game.party.GamePartyProvider;

public class GameModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(GamemodeProvider.class).to(CoreGamemodeProvider.class);
        bind(MapProvider.class).to(CoreMapProvider.class);
        bind(MatchProvider.class).to(GameMatchProvider.class);
        bind(PartyProvider.class).to(GamePartyProvider.class);
        expose(GamemodeProvider.class);
        expose(MapProvider.class);
        expose(MatchProvider.class);
        expose(PartyProvider.class);
    }
}
