package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.creator.skin.CraftSkinProperty;
import net.seocraft.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.logging.Level;

public class LobbySelectorManager implements SelectorManager {

    @Inject private Lobby lobby;
    @Inject private GamemodeProvider gamemodeProvider;

    @Override
    public void setupSelectorNPC() {
        ConfigurationSection NPCConfiguration = this.lobby.getConfig().getConfigurationSection("selector");

        if (NPCConfiguration != null)
            NPCConfiguration.getKeys(false).forEach((key) -> {
                ConfigurationSection section = NPCConfiguration.getConfigurationSection(key);
                String gameString = section.getString("gamemode");

                if (gameString != null) {

                    try {
                        Gamemode gamemode = this.gamemodeProvider.getGamemodeSync(gameString);
                        SubGamemode subGamemode;

                        if (gamemode != null) {
                            if (section.getString("subGamemode") != null) {
                                Optional<SubGamemode> subOptional = gamemode.getSubGamemodes().stream().filter(
                                        sub -> sub.getId().equalsIgnoreCase(section.getString("subGamemode"))
                                ).findFirst();

                                if (!subOptional.isPresent()) throw new NotFound("The Sub Gamemode selected was not found");
                                subGamemode = subOptional.get();
                            } else {
                                throw new NotFound("The Sub Gamemode selected was not found");
                            }

                            SelectorNPC selectorNPC = new LobbySelectorNPC(
                                    gamemode,
                                    subGamemode,
                                    new CraftSkinProperty(
                                            section.getString("skin"),
                                            section.getString("signature")
                                    ),
                                    section.getFloat("x"),
                                    section.getFloat("sy"),
                                    section.getFloat("z"),
                                    section.getFloat("yaw"),
                                    section.getFloat("pitch")
                            );

                            selectorNPC.create(this.lobby, key);


                        } else {
                            throw new NotFound("The gamemode selected is null");
                        }
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.WARNING, "[Lobby] There was an error loading the {0} NPC. ({1})", new Object[]{key, ex.getMessage()});
                        ex.printStackTrace();
                    }

                }
            });

    }

}
