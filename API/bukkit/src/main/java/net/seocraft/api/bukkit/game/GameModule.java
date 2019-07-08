package net.seocraft.api.bukkit.game;

import com.google.inject.AbstractModule;
import net.seocraft.api.bukkit.game.gamemode.GamemodeModule;
import net.seocraft.api.bukkit.game.map.MapModule;
import net.seocraft.api.bukkit.game.match.MatchModule;
import net.seocraft.api.bukkit.game.party.PartyModule;

public class GameModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GamemodeModule());
        install(new MapModule());
        install(new MatchModule());
        install(new PartyModule());
    }

}
