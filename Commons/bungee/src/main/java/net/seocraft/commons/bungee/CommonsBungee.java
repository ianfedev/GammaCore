package net.seocraft.commons.bungee;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedBinder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.online.OnlineStatusManager;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserExpulsion;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bungee.punishment.PunishmentListener;
import net.seocraft.commons.bungee.serializer.InterfaceDeserializer;
import net.seocraft.commons.bungee.server.ServerModule;
import net.seocraft.commons.bungee.user.PlayerDisconnectListener;
import net.seocraft.commons.bungee.user.PlayerJoinListener;
import net.seocraft.commons.core.CoreModule;
import net.seocraft.commons.core.translation.TranslatableField;

import java.io.*;
import java.util.logging.Level;


public class CommonsBungee extends Plugin {

    @Inject private PlayerJoinListener playerJoinListener;
    @Inject private PlayerDisconnectListener playerDisconnectListener;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private GameSessionManager gameSessionManager;
    @Inject private OnlineStatusManager onlineStatusManager;
    @Inject private TranslatableField translatableField;
    @Inject private Messager messager;

    @Inject private ServerLoad serverLoad;
    private Server serverRecord;
    private Channel<UserExpulsion> punishmentChannel;


    @Override
    public void onEnable() {
        loadConfig();
        try {
            this.serverRecord = this.serverLoad.setupServer();
            this.registerBanListener();

            getProxy().getPluginManager().registerListener(this, playerJoinListener);
            getProxy().getPluginManager().registerListener(this, playerDisconnectListener);

        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
            this.getLogger().log(Level.SEVERE, "[Bungee-API] There was an error initializating server.");
            this.getProxy().stop();
        }
    }

    @Override
    public void onDisable() {
        System.out.println("Server disconnecting");
        try {
            this.serverLoad.disconnectServer();
            System.out.println("Server disconnected");
            for (ProxiedPlayer player : getProxy().getPlayers()) {
                try {
                    User user = this.userStorageProvider.findUserByNameSync(player.getName());
                    this.gameSessionManager.removeGameSession(player.getName());
                    this.onlineStatusManager.setPlayerOnlineStatus(user.getId(), false);
                } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}
            }

        } catch (Unauthorized | BadRequest | NotFound | InternalServerError ex) {
            this.getLogger().log(Level.SEVERE, "[Bungee-API] There was an error shutting down the server. ({0})", ex.getMessage());
        }
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml"); OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.bind(CommonsBungee.class).toInstance(this);
        binder.bind(ObjectMapper.class).toProvider(() -> {
            ObjectMapper mapper = new ObjectMapper().registerModule(InterfaceDeserializer.getAbstractTypes());
            mapper.setVisibility(mapper.getSerializationConfig()
                    .getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withIsGetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC));
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper;
        }).in(Scopes.SINGLETON);
        binder.install(new CoreModule());
        binder.install(new ServerModule());
    }

    public Server getServerRecord() {
        return this.serverRecord;
    }

    private void registerBanListener() {
        punishmentChannel = this.messager.getChannel("proxyBan", UserExpulsion.class);
        this.punishmentChannel.registerListener(new PunishmentListener(this, translatableField));

    }

}
