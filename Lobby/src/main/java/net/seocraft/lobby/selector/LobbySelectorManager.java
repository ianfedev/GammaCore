package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.api.core.http.exceptions.NotFound;
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
            NPCConfiguration.getKeys(true).forEach((key) -> {
                String gameString = this.lobby.getConfig().getString("selector." + key + ".gamemode");
                System.out.println(gameString);

                if (gameString != null) {

                    try {
                        Gamemode gamemode = this.gamemodeProvider.getGamemodeSync(gameString);
                        SubGamemode subGamemode;

                        if (gamemode != null) {
                            if (this.lobby.getConfig().getString("selector." + key + ".subGamemode") != null) {
                                Optional<SubGamemode> subOptional = gamemode.getSubGamemodes().stream().filter(
                                        sub -> sub.getId().equalsIgnoreCase(this.lobby.getConfig().getString("selector." + key + ".gubGamemode"))
                                ).findFirst();

                                if (!subOptional.isPresent()) throw new NotFound("The Sub Gamemode selected was not found");

                                subGamemode = subOptional.get();

                                SelectorNPC selectorNPC = new LobbySelectorNPC(
                                        gamemode,
                                        subGamemode,
                                        this.lobby.getConfig().getString("selector." + key + ".skin"),
                                        this.lobby.getConfig().getFloat("selector." + key + ".x"),
                                        this.lobby.getConfig().getFloat("selector." + key + ".y"),
                                        this.lobby.getConfig().getFloat("selector." + key + ".z"),
                                        this.lobby.getConfig().getFloat("selector." + key + ".yaw"),
                                        this.lobby.getConfig().getFloat("selector." + key + ".pitch")
                                );

                                System.out.println(selectorNPC.getGamemode().getId());

                            } else {
                                throw new NotFound("The Sub Gamemode selected was not found");
                            }

                        } else {
                            throw new NotFound("The gamemode selected is null");
                        }
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.WARNING, "[Lobby] There was an error loading the '{0}' NPC. ({1})", new Object[]{key, ex.getMessage()});
                    }

                }
            });

    }

}
