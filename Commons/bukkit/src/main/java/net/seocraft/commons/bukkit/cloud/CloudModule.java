package net.seocraft.commons.bukkit.cloud;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.cloud.CloudManager;

public class CloudModule extends ProtectedModule {

    private boolean cloud;

    public CloudModule(boolean cloud) {
        this.cloud = cloud;
    }

    @Override
    protected void configure() {
        if (this.cloud) {
            bind(CloudManager.class).to(GammaCloudManager.class);
        } else {
            bind(CloudManager.class).to(CloudStandaloneManager.class);
        }
        expose(CloudManager.class);
    }

}
