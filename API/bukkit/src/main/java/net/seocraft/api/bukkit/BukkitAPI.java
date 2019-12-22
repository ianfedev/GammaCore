package net.seocraft.api.bukkit;

import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.creator.CreatorModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitAPI extends JavaPlugin {

    private boolean cloudDeploy = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.cloudDeploy = true;
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new CreatorModule());
        binder.publicBinder().bind(BukkitAPI.class).toInstance(this);

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            System.out.println("Plugin: " + plugin.getName());
        }
    }

    public boolean hasCloudDeploy() {
        return this.cloudDeploy;
    }

}
