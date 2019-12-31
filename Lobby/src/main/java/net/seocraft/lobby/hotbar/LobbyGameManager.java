package net.seocraft.lobby.hotbar;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.lobby.GameMenuManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.bukkit.util.InventoryUtils;
import net.seocraft.commons.bukkit.util.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LobbyGameManager implements GameMenuManager {

    @Inject private GamemodeProvider gamemodeHandler;
    @Inject private CloudManager cloudManager;
    @Inject private TranslatableField translatableField;

    @Override
    public void loadGameMenu(@NotNull Player player, @NotNull String l) {

        Map<Integer, ItemStack> inventoryItems = new HashMap<>();

        CallbackWrapper.addCallback(this.gamemodeHandler.listGamemodes(), gamemodes -> {
            if (gamemodes.getStatus() == AsyncResponse.Status.SUCCESS) {
                Set<Gamemode> gamemodeList = gamemodes.getResponse();

                if (!gamemodeList.isEmpty()) {
                    gamemodeList.forEach(gamemode -> {
                        ItemStack gamemodeBase = gamemode.obtainStackItem();
                        ItemMeta gamemodeMeta = gamemodeBase.getItemMeta();

                        gamemodeMeta.setDisplayName(
                                ChatColor.GREEN +
                                this.translatableField.getUnspacedField(
                                        l,
                                        "game_" + gamemode.getId() + "_title"
                                )
                        );

                        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();

                        loreDisplayArray.add(
                                this.translatableField.getUnspacedField(
                                        l,
                                        "game_" + gamemode.getId() + "_description"
                                ) + ".",
                                ChatColor.GRAY
                        );

                        gamemodeMeta.setLore(loreDisplayArray);
                        gamemodeBase.setItemMeta(gamemodeMeta);
                        gamemodeBase = NBTTagHandler.addString(gamemodeBase, "game_selector_opt", gamemode.getLobbyGroup());
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
