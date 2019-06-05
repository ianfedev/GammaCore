package net.seocraft.api.bukkit;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.server.ServerLoadManager;
import net.seocraft.api.bukkit.user.IUserStoreHandler;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.SharedModule;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.Server;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BukkitAPI extends JavaPlugin {

    @Inject private ServerLoadManager loadManager;
    @Getter private String preSelectedMap;
    @Getter @Setter private Server serverRecord;

    @Override
    public void onEnable() {
        loadConfig();
        loadServer();
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.publicBinder().install(new SharedModule()); // This should be changed when bungee also has the same ProtectedModule
        binder.bind(BukkitAPI.class).toInstance(this);
        binder.bind(UserStoreHandler.class).to(IUserStoreHandler.class);
        binder.expose(UserStoreHandler.class);
        binder.expose(BukkitAPI.class);
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

}
