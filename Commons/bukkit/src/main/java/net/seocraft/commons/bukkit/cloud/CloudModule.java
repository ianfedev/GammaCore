package net.seocraft.commons.bukkit.cloud;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.cloud.CloudManager;

public class CloudModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(CloudManager.class).to(GammaLobbySwitcher.class);
    }

}
