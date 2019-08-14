package net.seocraft.commons.bukkit.cloud;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.cloud.CloudLobbySwitcher;

public class CloudModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(CloudLobbySwitcher.class).to(GammaLobbySwitcher.class);
    }

}
