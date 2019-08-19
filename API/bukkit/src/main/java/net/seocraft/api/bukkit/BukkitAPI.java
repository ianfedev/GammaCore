package net.seocraft.api.bukkit;

import me.fixeddev.inject.ProtectedBinder;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        loadConfig();
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.publicBinder().bind(BukkitAPI.class).toInstance(this);
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
