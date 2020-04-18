package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.NPCManager;
import net.seocraft.api.bukkit.creator.npc.action.ClickType;
import net.seocraft.api.bukkit.creator.npc.event.NPCInteractEvent;
import net.seocraft.api.bukkit.creator.skin.CraftSkinProperty;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.commons.bukkit.cloud.NPCRedirector;
import net.seocraft.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.logging.Level;

public class LobbySelectorManager implements SelectorManager {

    @Inject private Lobby lobby;
    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private NPCRedirector npcRedirector;
    @Inject private NPCManager npcManager;

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
                        SubGamemode subGamemode = null;

                        if (gamemode != null) {
                            if (section.getString("subGamemode") != null) {
                                Optional<SubGamemode> subOptional = gamemode.getSubGamemodes().stream().filter(
                                        sub -> sub.getId().equalsIgnoreCase(section.getString("subGamemode"))
                                ).findFirst();

                                if (!subOptional.isPresent()) throw new NotFound("The Sub Gamemode selected was not found");
                                subGamemode = subOptional.get();
                            }

                            SelectorNPC selectorNPC = new LobbySelectorNPC(
                                    gamemode,
                                    subGamemode,
                                    new CraftSkinProperty(
                                            section.getString("signature"),
                                            section.getString("skin")
                                    ),
                                    Double.parseDouble(section.getString("x")),
                                    Double.parseDouble(section.getString("y")),
                                    Double.parseDouble(section.getString("z")),
                                    Double.parseDouble(section.getString("yaw")),
                                    Double.parseDouble(section.getString("pitch")),
                                    section.getBoolean("perk")
                            );

                            this.lobby.getLobbyNPC().add(selectorNPC);

                            NPC gameNPC =  selectorNPC.create(this.lobby, key, npcManager);
                            if (gameNPC != null) {
                                SubGamemode finalSubGamemode = subGamemode;
                                gameNPC.addActionHandler((npc, npcEvent) -> {
                                    if (npcEvent instanceof NPCInteractEvent) {
                                        NPCInteractEvent interactEvent = (NPCInteractEvent) npcEvent;
                                        if (interactEvent.getClickType() == ClickType.RIGHT_CLICK) {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.lobby, () -> {
                                                this.npcRedirector.redirectPlayer(
                                                        gamemode,
                                                        finalSubGamemode,
                                                        interactEvent.getPlayer(),
                                                        selectorNPC.isPerk()
                                                );
                                            });
                                        }
                                    }
                                });
                            } else {
                                throw new InternalServerError("There was an error creating the NPC.");
                            }
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
