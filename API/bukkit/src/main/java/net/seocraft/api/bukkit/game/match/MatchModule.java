package net.seocraft.api.bukkit.game.match;

import com.google.inject.AbstractModule;

public class MatchModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MatchHandler.class).to(MatchHandlerImp.class);
    }

}
