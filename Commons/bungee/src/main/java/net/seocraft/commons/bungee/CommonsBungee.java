package net.seocraft.commons.bungee;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedBinder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.commons.bungee.serializer.InterfaceDeserializer;
import net.seocraft.commons.bungee.server.ServerModule;
import net.seocraft.commons.core.CoreModule;

import java.io.*;
import java.util.logging.Level;


public class CommonsBungee extends Plugin {

    @Inject private ServerLoad serverLoad;
    private Server serverRecord;

    @Override
    public void onEnable() {
        loadConfig();
        try {
            this.serverRecord = this.serverLoad.setupServer();

        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
            this.getLogger().log(Level.SEVERE, "[Bungee-API] There was an error initializating server.");
            this.getProxy().stop();
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

    public Configuration getConfig() throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
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

}
