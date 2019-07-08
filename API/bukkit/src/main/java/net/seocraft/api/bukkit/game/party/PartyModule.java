package net.seocraft.api.bukkit.game.party;

import com.google.inject.AbstractModule;

public class PartyModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PartyHandler.class).to(PartyHandlerImp.class);
    }

}
