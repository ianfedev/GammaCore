package net.seocraft.api.bukkit;

import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.creator.CreatorModule;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new CreatorModule());
        binder.publicBinder().bind(BukkitAPI.class).toInstance(this);
    }

}
