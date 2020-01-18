package net.seocraft.lobby.lobby;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class FriendsMenuIconsUtil {

    private UserFormatter userFormatter;
    private TranslatableField translatableField;
    private String realm;

    @Inject
    FriendsMenuIconsUtil(
            TranslatableField translatableField, UserStorageProvider userStorageProvider,
            BukkitAPI bukkitAPI, UserFormatter formatter
    ) {
        this.translatableField = translatableField;
        this.userFormatter = formatter;
        this.realm = bukkitAPI.getConfig().getString("realm");
    }

    public @NotNull ItemStack addFriendIcon(@NotNull User user) {
        ItemStack addIcon = NBTTagHandler.addString(
                new ItemStack(Material.BOOK_AND_QUILL),
                "lobby_accessor",
                "add_friends"
        );
        ItemMeta addMeta = addIcon.getItemMeta();
        addMeta.setDisplayName(ChatColor.YELLOW + this.translatableField.getField(user.getLanguage(), "commons_profile_friends_add")
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        addIcon.setItemMeta(addMeta);
        return addIcon;
    }

    public @NotNull ItemStack removeFriendIcon(@NotNull User user) {
        ItemStack removeIcon = NBTTagHandler.addString(
                new ItemStack(Material.BARRIER),
                "lobby_accessor",
                "remove_friends"
        );
        ItemMeta removeMeta = removeIcon.getItemMeta();
        removeMeta.setDisplayName(ChatColor.RED + this.translatableField.getField(user.getLanguage(), "commons_profile_friends_remove")
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        removeIcon.setItemMeta(removeMeta);
        return removeIcon;
    }

}
