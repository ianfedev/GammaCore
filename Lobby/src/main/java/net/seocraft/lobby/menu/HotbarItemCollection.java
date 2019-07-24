package net.seocraft.lobby.menu;

import com.google.inject.Inject;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.util.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import static net.seocraft.commons.bukkit.minecraft.NBTTagHandler.addString;

public class HotbarItemCollection {

    @Inject private TranslatableField translatableField;

    private ItemStack getGameMenu(String l) {
        ItemStack gameMenuBase = addString(
                new ItemStack(Material.COMPASS, 1),
                "accessor",
                "game_menu"
        );

        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
        ItemMeta gameMenuMeta = gameMenuBase.getItemMeta();
        gameMenuMeta.setDisplayName(
                ChatColor.YELLOW +
                        this.translatableField.getField(
                                l,
                                "commons_lobby_game_menu"
                        ) +
                ChatColor.GRAY
                        + "(" +
                        this.translatableField.getUnspacedField(l, "commons_right_click")
                        + ")"
        );
        loreDisplayArray.add(
                this.translatableField.getUnspacedField(
                        l,
                        "commons_lobby_game_description"
                ) + ".",
                ChatColor.GRAY
        );
        gameMenuMeta.setLore(loreDisplayArray);
        gameMenuBase.setItemMeta(gameMenuMeta);
        return gameMenuBase;
    }

    public ItemStack getHidingGadget(String l, boolean isHiding) {
        ItemStack hidingMenuBase;
        if (isHiding) {
            hidingMenuBase = NBTTagHandler.addString(
                    new ItemStack(Material.ENDER_PEARL, 1),
                    "accessor",
                    "show_players"
            );
        } else {
            hidingMenuBase = NBTTagHandler.addString(
                    new ItemStack(Material.EYE_OF_ENDER, 1),
                    "accessor",
                    "hide_players"
            );
        }

        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
        ItemMeta hidingMenuMeta = hidingMenuBase.getItemMeta();
        if (isHiding) {
            hidingMenuMeta.setDisplayName(
                    ChatColor.LIGHT_PURPLE +
                            this.translatableField.getField(
                                    l,
                                    "commons_lobby_hiding_gadget_show"
                            ) +
                            ChatColor.GRAY
                            + "(" +
                            this.translatableField.getUnspacedField(l, "commons_right_click")
                            + ")"
            );
        } else {
            hidingMenuMeta.setDisplayName(
                    ChatColor.YELLOW +
                            this.translatableField.getField(
                                    l,
                                    "commons_lobby_hiding_gadget"
                            ) +
                            ChatColor.GRAY
                            + "(" +
                            this.translatableField.getUnspacedField(l, "commons_right_click")
                            + ")"
            );
        }
        loreDisplayArray.add(
                this.translatableField.getUnspacedField(
                        l,
                        "commons_lobby_hiding_description"
                ) + ".",
                ChatColor.GRAY
        );
        loreDisplayArray.add("");
        loreDisplayArray.add(
                ChatColor.AQUA +
                        this.translatableField.getUnspacedField(
                                l,
                                "commons_note"
                        ).toUpperCase() + ": " +
                        this.translatableField.getUnspacedField(
                            l,
                                "commons_lobby_hiding_note"
                        ),
                ChatColor.AQUA
        );
        hidingMenuMeta.setLore(loreDisplayArray);
        hidingMenuBase.setItemMeta(hidingMenuMeta);
        return hidingMenuBase;
    }

    private ItemStack getElementalLoot(String l) {
        ItemStack elementalLootBase = addString(
                new ItemStack(Material.ENDER_CHEST, 1),
                "accessor",
                "elemental_loot"
        );

        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
        ItemMeta elementalLootMeta = elementalLootBase.getItemMeta();
        elementalLootMeta.setDisplayName(
                ChatColor.YELLOW +
                        this.translatableField.getField(
                                l,
                                "commons_lobby_elemental_loot"
                        ) +
                        ChatColor.RED
                        + "(" +
                        this.translatableField.getUnspacedField(l, "commons_coming_soon")
                        + ")"
        );
        loreDisplayArray.add(
                this.translatableField.getUnspacedField(
                        l,
                        "commons_lobby_elemental_description"
                ) + ".",
                ChatColor.GRAY
        );
        elementalLootMeta.setLore(loreDisplayArray);
        elementalLootBase.setItemMeta(elementalLootMeta);
        return elementalLootBase;
    }

    private ItemStack getProfileMenu(User user) {
        ItemStack profileBase = addString(
                new ItemStack(Material.SKULL_ITEM, 1, (byte) 3),
                "profile_accessor",
                user.getId()
        );
        SkullMeta profileMeta = (SkullMeta) profileBase.getItemMeta();
        profileMeta.setOwner(user.getSkin());
        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
        profileMeta.setDisplayName(
                ChatColor.YELLOW +
                        this.translatableField.getField(
                                user.getLanguage(),
                                "commons_lobby_profile"
                        ) +
                        ChatColor.RED
                        + "(" +
                        this.translatableField.getUnspacedField(user.getLanguage(), "commons_coming_soon")
                        + ")"
        );
        loreDisplayArray.add(
                this.translatableField.getUnspacedField(
                        user.getLanguage(),
                        "commons_lobby_profile_description"
                ) + ".",
                ChatColor.GRAY
        );
        profileMeta.setLore(loreDisplayArray);
        profileBase.setItemMeta(profileMeta);
        return profileBase;
    }

    private ItemStack getLobbySelector(String l) {
        ItemStack lobbySelectorBase = addString(
                new ItemStack(Material.NETHER_STAR, 1),
                "accessor",
                "lobby_selector"
        );
        ItemMeta lobbySelectorMeta = lobbySelectorBase.getItemMeta();
        lobbySelectorMeta.setDisplayName(
                ChatColor.YELLOW +
                        this.translatableField.getField(
                                l,
                                "commons_lobby_hiding_gadget_show"
                        ) +
                        ChatColor.GRAY
                        + "(" +
                        this.translatableField.getUnspacedField(l, "commons_right_click")
                        + ")"
        );
        LoreDisplayArray<String> loreDisplayArray = new LoreDisplayArray<>();
        loreDisplayArray.add(
                this.translatableField.getUnspacedField(
                        l,
                        "commons_lobby_selector_description"
                ) + ".",
                ChatColor.GRAY
        );
        lobbySelectorMeta.setLore(loreDisplayArray);
        lobbySelectorBase.setItemMeta(lobbySelectorMeta);
        return lobbySelectorBase;
    }

    public void setupPlayerHotbar(Player player, User user) {
        String l = user.getLanguage();
        player.getInventory().clear();
        player.getInventory().setItem(0, getGameMenu(l));
        player.getInventory().setItem(1, getHidingGadget(l, user.isHiding()));
        player.getInventory().setItem(4, getElementalLoot(l));
        player.getInventory().setItem(7, getProfileMenu(user));
        player.getInventory().setItem(8, getLobbySelector(l));
        player.getInventory().setHeldItemSlot(0);
    }

}
