package net.seocraft.commons.bukkit.punishment;

import com.google.inject.AbstractModule;
import net.seocraft.api.bukkit.punishment.PunishmentProvider;

public class PunishmentModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PunishmentProvider.class).to(UserPunishmentProvider.class);
    }
}
