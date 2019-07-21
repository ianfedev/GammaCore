package net.seocraft.commons.core.cooldown;

import com.google.inject.AbstractModule;
import net.seocraft.api.core.cooldown.CooldownManager;

public class CooldownModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CooldownManager.class).to(CoreCooldownManager.class);
    }

}
