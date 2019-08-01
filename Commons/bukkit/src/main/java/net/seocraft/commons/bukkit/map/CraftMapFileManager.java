package net.seocraft.commons.bukkit.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.map.MapProvider;
import net.seocraft.api.bukkit.map.MapFileManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.utils.FileManagerUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.seocraft.commons.core.utils.FileManagerUtils.compressFile;
import static net.seocraft.commons.core.utils.FileManagerUtils.fileToBase64StringConversion;

public class CraftMapFileManager implements MapFileManager {

    @Inject private CommonsBukkit instance;
    @Inject private MapProvider mapProvider;
    @Inject private ObjectMapper mapper;

    @Override
    public void configureMapFolder() {

        File serverPath = new File("./maps");
        if (serverPath.exists() && serverPath.isDirectory()) {

            Set<File> folderList = Arrays.stream(Objects.requireNonNull(serverPath.listFiles())).filter(
                    File::isDirectory
            ).collect(
                    Collectors.toSet()
            );

            folderList.forEach(folder -> {
                boolean level = false, region = false, image = false;
                BaseMapConfiguration configuration = null;
                for (File file: Objects.requireNonNull(folder.listFiles())) {
                    if (file.getName().equalsIgnoreCase("level.dat") && !file.isDirectory()) level = true;
                    if (file.getName().equalsIgnoreCase("region") && file.isDirectory()) region = true;
                    if (file.getName().equalsIgnoreCase("image.png") && !file.isDirectory()) image = true;

                    if ((file.getName().equalsIgnoreCase("config.json") && !file.isDirectory())) {
                        try {
                            configuration = this.mapper.readValue(
                                    file,
                                    BaseMapConfiguration.class
                            );
                        } catch (IOException e) {
                            Bukkit.getLogger().log(
                                    Level.SEVERE,
                                    "[GameAPI] Unable to get configuration from folder {0} ({1}).",
                                    new Object[]{file.getName(), e.getMessage()}
                            );
                        }
                    }
                }

                //TODO: Transform files to base64, execute provider action

                if (level && region && image && configuration != null) {

                    try {
                        File mapFile = new File(folder, "map.zip");
                        compressFile(folder, mapFile);
                        BaseMapConfiguration finalConfiguration = configuration;
                        CallbackWrapper.addCallback(this.mapProvider.loadMap(
                                configuration.getName(),
                                fileToBase64StringConversion(
                                        new File(folder, "map.zip")
                                ),
                                fileToBase64StringConversion(
                                        new File(folder, "config.json")
                                ),
                                fileToBase64StringConversion(
                                        new File(folder, "image.png")
                                ),
                                configuration.getAuthor(),
                                configuration.getVersion(),
                                configuration.getContributors(),
                                configuration.getGamemode(),
                                configuration.getSubGamemode(),
                                configuration.getDescription()
                        ), mapAsyncResponse -> {
                            if (mapFile.exists()) mapFile.delete();
                            if (mapAsyncResponse.getStatus().equals(AsyncResponse.Status.SUCCESS)) {
                                System.out.println(mapAsyncResponse.getResponse().getName());
                            } else {
                                Bukkit.getLogger().log(
                                        Level.SEVERE,
                                        "[GameAPI] Unable to get configuration from folder {0} ({1}).",
                                        new Object[]{finalConfiguration.getName(), mapAsyncResponse.getThrowedException().getMessage()}
                                );
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Bukkit.getLogger().log(
                            Level.SEVERE,
                            "[GameAPI] Current map has not the correctly configuration.",
                            new Object[]{folder.getName()}
                    );
                }
            });
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "[GameAPI] There is not a folder called 'maps' inside your server director.");
            Bukkit.shutdown();
        }
    }

}
