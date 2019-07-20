package net.seocraft.lobby.game;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.commons.core.backend.http.AsyncResponse;
import net.seocraft.commons.bukkit.old.util.ChatAlertLibrary;
import net.seocraft.commons.bukkit.old.util.InventoryUtils;
import net.seocraft.commons.bukkit.old.util.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMenuHandlerImp implements GameMenuHandler {

    @Inject private GamemodeProvider gamemodeHandler;
    @Inject private TranslatableField translatableField;

    @Override
    public void loadGameMenu(@NotNull Player player, @NotNull String l) {

        Map<Integer, ItemStack> inventoryItems = new HashMap<>();

        CallbackWrapper.addCallback(this.gamemodeHandler.listGamemodes(), gamemodes -> {
            if (gamemodes.getStatus() == AsyncResponse.Status.SUCCESS) {
                List<Gamemode> gamemodeList = gamemodes.getResponse();

                if (!gamemodeList.isEmpty()) {
                    gamemodeList.forEach(gamemode -> {
                        ItemStack gamemodeBase = gamemode.obtainStackItem();
                        ItemMeta gamemodeMeta = gamemodeBase.getItemMeta();

                        gamemodeMeta.setDisplayName(
                                ChatColor.GREEN +
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
                                ) + ".",
                                ChatColor.GRAY
                        );

                        gamemodeMeta.setLore(loreDisplayArray);
                        gamemodeBase.setItemMeta(gamemodeMeta);
                        inventoryItems.put(gamemode.getNavigatorSlot(), gamemodeBase);
                    });
                } else {
                    ItemStack emptyBase = new ItemStack(Material.BARRIER, 1);
                    ItemMeta emptyMeta = emptyBase.getItemMeta();
                    emptyMeta.setDisplayName(
                            ChatColor.RED +
                            this.translatableField.getUnspacedField(l, "commons_lobby_selector_empty")
                    );
                    LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
                    loreDisplayArray.add(
                            this.translatableField.getUnspacedField(
                                    l,
                                    "commons_lobby_selector_empty_lore"
                            ) + ".",
                            ChatColor.GRAY
                    );
                    loreDisplayArray.add("");
                    loreDisplayArray.add(
                            this.translatableField.getUnspacedField(
                                    l,
                                    "commons_lobby_selector_empty_note"
                            ) + ":",
                            ChatColor.GRAY
                    );
                    loreDisplayArray.add(ChatColor.AQUA + "@SeocraftNetwork");
                    emptyMeta.setLore(loreDisplayArray);
                    emptyBase.setItemMeta(emptyMeta);
                    inventoryItems.put(4, emptyBase);
                }

                player.openInventory(InventoryUtils.createInventory(
                        this.translatableField.getUnspacedField(
                                l,
                                "commons_lobby_game_menu"
                        ),
                        9,
                        inventoryItems
                ));

            } else {
                ChatAlertLibrary.errorChatAlert(
                        player,
                        this.translatableField.getUnspacedField(
                                l,
                                "commons_lobby_selector_error"
                        ) + "."
                );
            }
        });
    }

}
