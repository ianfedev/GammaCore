package net.seocraft.lobby.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.GamemodeHandler;
import net.seocraft.commons.bukkit.util.InventoryUtils;
import net.seocraft.commons.bukkit.util.LoreDisplayArray;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GameMenuHandlerImp implements GameMenuHandler {

    @Inject private GamemodeHandler gamemodeHandler;
    @Inject private TranslatableField translatableField;

    @Override
    public void loadGameMenu(@NotNull Player player, @NotNull String l) {

        Map<Integer, ItemStack> inventoryItems = new HashMap<>();

        this.gamemodeHandler.listGamemodes().forEach(gamemode -> {
            ItemStack gamemodeBase = gamemode.getNavigatorIcon();
            ItemMeta gamemodeMeta = gamemodeBase.getItemMeta();

            gamemodeMeta.setDisplayName(
                    this.translatableField.getUnspacedField(
                            l,
                            "game_" + gamemode.id() + "_title"
                    )
            );

            LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();

            loreDisplayArray.add(
                    this.translatableField.getUnspacedField(
                            l,
                            "game_" + gamemode.id() + "_description"
                    )
            );

            gamemodeMeta.setLore(loreDisplayArray);
            gamemodeBase.setItemMeta(gamemodeMeta);
            inventoryItems.put(gamemode.getNavigatorSlot(), gamemodeBase);
        });

        InventoryUtils.createInventory(
                this.translatableField.getUnspacedField(
                        l,
                        "commons_lobby_game_menu"
                ),
                9,
                inventoryItems
        );


    }

}
