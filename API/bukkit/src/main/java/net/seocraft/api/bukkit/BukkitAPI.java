package net.seocraft.api.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.game.GameModule;
import net.seocraft.api.bukkit.game.gamemode.model.Gamemode;
import net.seocraft.api.bukkit.game.map.model.Map;
import net.seocraft.api.bukkit.game.match.model.Match;
import net.seocraft.api.bukkit.game.party.model.Party;
import net.seocraft.api.bukkit.game.subgame.SubGamemode;
import net.seocraft.api.bukkit.server.ServerModule;
import net.seocraft.api.bukkit.server.management.ServerLoad;
import net.seocraft.api.bukkit.server.management.ServerManager;
import net.seocraft.api.bukkit.server.model.Server;
import net.seocraft.api.bukkit.user.UserStoreHandlerImp;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.SharedModule;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.serialization.model.ExtendedGsonBuilder;
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
            Bukkit.getLogger().log(Level.INFO, "[API-Bukkit] ServerImp connected to the API. (ID: {0})",
                    getServerRecord().id());
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
        binder.publicBinder().bind(Gson.class).toProvider(() -> {
            ExtendedGsonBuilder builder = new ExtendedGsonBuilder();
            return builder.registerModelSerializer(Gamemode.class)
                    .registerModelSerializer(Map.class)
                    .registerModelSerializer(Match.class)
                    .registerModelSerializer(Party.class)
                    .registerModelSerializer(SubGamemode.class)
                    .registerModelSerializer(Server.class)
                    .serializeNulls()
                    .enableComplexMapKeySerialization()
                    .setPrettyPrinting()
                    .create();
        }).in(Scopes.SINGLETON);
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
