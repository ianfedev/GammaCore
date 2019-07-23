package net.seocraft.commons.bukkit.punishment;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.punishment.PunishmentProvider;

public class PunishmentModule extends ProtectedModule {
    @Override
    protected void configure() {
        bind(PunishmentProvider.class).to(UserPunishmentProvider.class);
    }
}
