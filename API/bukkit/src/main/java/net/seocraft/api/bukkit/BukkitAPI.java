package net.seocraft.api.bukkit;

import com.google.inject.Inject;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.game.GameModule;
import net.seocraft.api.bukkit.server.ServerModule;
import net.seocraft.api.bukkit.server.management.ServerLoad;
import net.seocraft.api.bukkit.server.model.Server;
import net.seocraft.api.bukkit.user.UserStoreHandlerImp;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.SharedModule;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BukkitAPI extends JavaPlugin {

    @Inject private ServerLoad loadManager;
    private Server serverRecord;

    @Override
    public void onEnable() {
        loadConfig();
        try {
            setServerRecord(this.loadManager.setupServer());
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Error starting server ({0}): {1}",
                    new Object[]{exception.getClass().getSimpleName(), exception.getMessage()});
            Bukkit.getServer().shutdown();
        }
    }

    @Override
    public void onDisable() {
        try {
            this.loadManager.disconnectServer();
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError exception) {
            Bukkit.getLogger().log(Level.SEVERE, "[API-Bukkit] Error disconnecting server ({0}): {1}",
                    new Object[]{exception.getClass().getSimpleName(), exception.getMessage()});
            Bukkit.getServer().shutdown();
        }
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.publicBinder().install(new SharedModule()); // This should be changed when bungee also has the same ProtectedModule
        binder.publicBinder().install(new ServerModule());
        binder.publicBinder().install(new GameModule());
        binder.publicBinder().bind(UserStoreHandler.class).to(UserStoreHandlerImp.class);
        binder.publicBinder().bind(BukkitAPI.class).toInstance(this);
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public Server getServerRecord() {
        return serverRecord;
    }

    private void setServerRecord(Server serverRecord) {
        this.serverRecord = serverRecord;
    }
}
