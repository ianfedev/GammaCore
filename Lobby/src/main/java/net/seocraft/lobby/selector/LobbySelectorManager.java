package net.seocraft.lobby.selector;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.selector.SelectorManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.lobby.Lobby;
import org.bukkit.configuration.ConfigurationSection;

public class LobbySelectorManager implements SelectorManager {

    @Inject private Lobby lobby;

    @Override
    public void setupSelectorNPC() {
        ConfigurationSection NPCConfiguration = this.lobby.getConfig().getConfigurationSection("selector");
        System.out.println(NPCConfiguration);
        NPCConfiguration.getKeys(true).forEach(System.out::println);
        NPCConfiguration.getKeys(true).forEach((key) -> {
            ConfigurationSection selector = this.lobby.getConfig().getConfigurationSection("selector." + key);

            System.out.println(selector.getString("gamemode"));

            /*SelectorNPC selectorNPC = new LobbySelectorNPC(
                    ,
                    selector.getString("subGamemode"),
                    selector.getString("skin"),
                    selector.getFloat("x"),
                    selector.getFloat("y"),
                    selector.getFloat("z"),
                    selector.getFloat("yaw"),
                    selector.getFloat("pitch")
            );*/
        });

    }

}
