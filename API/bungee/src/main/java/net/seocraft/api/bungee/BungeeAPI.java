package net.seocraft.api.bungee;

import com.google.common.io.ByteStreams;
import me.fixeddev.inject.ProtectedBinder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class BungeeAPI extends Plugin {

    @Override
    public void onEnable() {
        loadConfig();
    }

    void loadConfig() {
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
        binder.publicBinder().bind(BungeeAPI.class).toInstance(this);
    }

}
