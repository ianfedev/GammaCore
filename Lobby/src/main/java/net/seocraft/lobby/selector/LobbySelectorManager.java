package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
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

                ConfigurationSection selector = NPCConfiguration.getConfigurationSection(key);

                if (selector.getString("gamemode") != null) {

                    try {
                        Gamemode gamemode = this.gamemodeProvider.getGamemodeSync("gamemode");
                        SubGamemode subGamemode;

                        if (gamemode != null) {
                            if (selector.getString("subGamemode") != null) {
                                Optional<SubGamemode> subOptional = gamemode.getSubGamemodes().stream().filter(
                                        sub -> sub.getId().equalsIgnoreCase(selector.getString("subGamemode"))
                                ).findFirst();

                                if (!subOptional.isPresent()) throw new NotFound("The Sub Gamemode selected was not found");

                                subGamemode = subOptional.get();

                                SelectorNPC selectorNPC = new LobbySelectorNPC(
                                        gamemode,
                                        subGamemode,
                                        selector.getString("skin"),
                                        selector.getFloat("x"),
                                        selector.getFloat("y"),
                                        selector.getFloat("z"),
                                        selector.getFloat("yaw"),
                                        selector.getFloat("pitch")
                                );

                                System.out.println(selectorNPC.getGamemode().getId());

                            } else {
                                throw new NotFound("The Sub Gamemode selected was not found");
                            }

                        } else {
                            throw new NotFound("The gamemode selected is null");
                        }
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
                        Bukkit.getLogger().log(Level.WARNING, "[Lobby] There was an error loading the '{0}' NPC. ({1})", new Object[]{key, ex.getMessage()});
                    }

                    System.out.println(selector.getString("gamemode"));

                }
            });

    }

}
