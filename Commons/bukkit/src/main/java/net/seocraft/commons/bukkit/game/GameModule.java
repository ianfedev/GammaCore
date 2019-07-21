package net.seocraft.commons.bukkit.game;

import com.google.inject.AbstractModule;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.map.MapProvider;
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.party.PartyProvider;
import net.seocraft.commons.bukkit.game.gamemode.CoreGamemodeProvider;
import net.seocraft.commons.bukkit.game.map.CoreMapProvider;
import net.seocraft.commons.bukkit.game.match.GameMatchProvider;
import net.seocraft.commons.bukkit.game.party.GamePartyProvider;

public class GameModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GamemodeProvider.class).to(CoreGamemodeProvider.class);
        bind(MapProvider.class).to(CoreMapProvider.class);
        bind(MatchProvider.class).to(GameMatchProvider.class);
        bind(PartyProvider.class).to(GamePartyProvider.class);
    }
}
