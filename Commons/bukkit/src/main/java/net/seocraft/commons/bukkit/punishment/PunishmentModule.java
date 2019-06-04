package net.seocraft.commons.bukkit.punishment;

import com.google.inject.AbstractModule;

public class PunishmentModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PunishmentHandler.class).to(IPunishmentHandler.class);
    }
}
