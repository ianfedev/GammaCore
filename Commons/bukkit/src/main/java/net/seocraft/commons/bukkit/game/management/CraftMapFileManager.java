package net.seocraft.commons.bukkit.game.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.map.MapProvider;
import net.seocraft.api.bukkit.game.management.MapFileManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.seocraft.commons.core.utils.FileManagerUtils.compressFile;
import static net.seocraft.commons.core.utils.FileManagerUtils.fileToBase64StringConversion;

@Singleton
public class CraftMapFileManager implements MapFileManager {

    @NotNull private Map<GameMap, File> playableMaps =  new HashMap<>();
    @Inject private MapProvider mapProvider;
    @Inject private ObjectMapper mapper;
    @Inject private CommonsBukkit instance;

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

                if (level && region && image && configuration != null) {
                    try {
                        File mapFile = new File(folder, "map.zip");
                        compressFile(folder, mapFile);
                        GameMap map = this.mapProvider.loadMapSync(
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
                        );
                        if (mapFile.exists()) mapFile.delete();
                        map.setMapJSON(String.join("\n", Files.readAllLines( new File(folder, "config.json").toPath())));
                        this.playableMaps.put(map, folder);
                        Bukkit.getLogger().log(
                                Level.INFO,
                                "[GameAPI] Loaded successfully map {0}.",
                                map.getName()
                        );
                    } catch (IOException | InternalServerError | NotFound | Unauthorized | BadRequest e) {
                        Bukkit.getLogger().log(
                                Level.SEVERE,
                                "[GameAPI] Error loading map {0}. ({1})",
                                new Object[]{folder.getName(), e.getMessage()}
                        );
                    }
                } else {
                    Bukkit.getLogger().log(
                            Level.SEVERE,
                            "[GameAPI] Current map has not the correctly configuration.",
                            folder.getName()
                    );
                }
            });

            if (this.playableMaps.isEmpty()) {
                Bukkit.getLogger().log(Level.SEVERE, "[GameAPI] There was not maps available for load ad 'maps' folder.");
                Bukkit.shutdown();
            } else {
                Bukkit.getLogger().log(
                        Level.INFO,
                        "[GameAPI] Loaded successfully {0} maps.",
                        this.playableMaps.size()
                );
            }
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "[GameAPI] There is not a folder called 'maps' inside your server directory.");
            Bukkit.shutdown();
        }
    }

    @Override
    public @NotNull Map<GameMap, File> getPlayableMaps() {
        return this.playableMaps;
    }

    @Override
    public @NotNull World loadMatchWorld(@NotNull Match match) throws IOException {
        File serverPath = new File("./match_" + match.getId());
        serverPath.mkdir();
        for (Map.Entry entry : this.playableMaps.entrySet()) {
            GameMap map = (GameMap) entry.getKey();
            if (map.getId().equalsIgnoreCase(match.getMap())) {
                FileUtils.copyDirectory(
                        (File) entry.getValue(),
                        serverPath
                );
            }
        }

        WorldCreator worldCreator = new WorldCreator("match_" + match.getId());
        return worldCreator.createWorld();
    }

}