package net.seocraft.commons.core.cooldown;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.core.cooldown.CooldownManager;

public class CooldownModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(CooldownManager.class).to(CoreCooldownManager.class);
    }

}
