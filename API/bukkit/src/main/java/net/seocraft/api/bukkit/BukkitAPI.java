package net.seocraft.api.bukkit;

import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.bukkit.server.ServerLoadManager;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.Server;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BukkitAPI extends JavaPlugin {

    private static BukkitAPI instance;
    @Inject private ServerLoadManager loadManager;
    @Getter private String preSelectedMap;
    @Getter @Setter private Server serverRecord;

    @Override
    public void onEnable() {
        instance = this;
        loadConfig();
        moduleInjector();
        loadServer();
    }

    private void moduleInjector() {
        BukkitModule module = new BukkitModule(this);
        Injector injector = module.createInjector();
        injector.injectMembers(this);
    }

    private void loadServer() {
        try {
            this.loadManager.setupServer();
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError error) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Error starting server ({0}): {1}",
                    new Object[]{error.getClass().getSimpleName(), error.getMessage()});
            Bukkit.getServer().shutdown();
        }
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static BukkitAPI getInstance() {
        return instance;
    }

}
