package net.seocraft.lobby.profile.icon;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.utils.StringUtils;
import net.seocraft.api.core.utils.TimeUtils;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.HeadLibrary;
import net.seocraft.commons.bukkit.util.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class FriendsMenuIconsUtil {

    private UserFormatter userFormatter;
    private TranslatableField translatableField;
    private String realm;

    @Inject
    FriendsMenuIconsUtil(TranslatableField translatableField, BukkitAPI bukkitAPI, UserFormatter formatter) {
        this.translatableField = translatableField;
        this.userFormatter = formatter;
        this.realm = bukkitAPI.getConfig().getString("realm");
    }

    public @NotNull ItemStack addFriendIcon(@NotNull User user) {
        ItemStack addIcon = NBTTagHandler.addString(
                new ItemStack(Material.BOOK_AND_QUILL),
                "friends_accessor",
                "add"
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
                "friends_accessor",
                "remove"
        );
        ItemMeta removeMeta = removeIcon.getItemMeta();
        removeMeta.setDisplayName(ChatColor.RED + this.translatableField.getField(user.getLanguage(), "commons_profile_friends_remove")
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        removeIcon.setItemMeta(removeMeta);
        return removeIcon;
    }

    public @NotNull ItemStack getPreviousPage(@NotNull User user, int page) {
        ItemStack previousPage = NBTTagHandler.addString(
                HeadLibrary.previousProfile(),
                "friends_page",
                page + ""
        );
        ItemMeta previousMeta = previousPage.getItemMeta();
        previousMeta.setDisplayName(ChatColor.YELLOW +
                this.translatableField.getUnspacedField(user.getLanguage(), "commons_pagination_previous").replace("%%page%%", page + "")
        );
        previousPage.setItemMeta(previousMeta);
        return previousPage;
    }

    public @NotNull ItemStack getNextPage(@NotNull User user, int page) {
        ItemStack nextPage = NBTTagHandler.addString(
                HeadLibrary.nextProfile(),
                "friends_page",
                page + ""
        );
        ItemMeta nextMeta = nextPage.getItemMeta();
        nextMeta.setDisplayName(ChatColor.YELLOW +
                this.translatableField.getUnspacedField(user.getLanguage(), "commons_pagination_next").replace("%%page%%", page + "")
        );
        nextPage.setItemMeta(nextMeta);
        return nextPage;
    }

    public @NotNull ItemStack noFriendsItem(@NotNull User user) {
        ItemStack goBack = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backMeta = goBack.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_friends_no_friends"));
        goBack.setItemMeta(backMeta);
        return goBack;
    }

    public @NotNull ItemStack getFriend(@NotNull User user, @NotNull User friend) {
        ItemStack profileInfo = NBTTagHandler.addString(
                new ItemStack(Material.SKULL_ITEM, 1, (short) 3),
                "friends_selector",
                friend.getUsername()
        );
        SkullMeta profileMeta = (SkullMeta) profileInfo.getItemMeta();
        profileMeta.setOwner(friend.getUsername());
        profileMeta.setDisplayName(this.userFormatter.getUserFormat(friend, realm));
        LoreDisplayArray<String> profileDisplay = new LoreDisplayArray<>();
        profileDisplay.add(" ");
        profileDisplay.add(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_rank") + ": " +
                        this.userFormatter.getUserColor(friend, realm).replace(friend.getUsername(), "") + friend.getPrimaryGroup().getName()
        );
        profileDisplay.add(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_level") + ": " +
                        ChatColor.GOLD + friend.getLevel()
        );

        if (friend.getSessionInfo().getLastSeen() == 0) {
            if (friend.getGameSettings().getGeneral().isShowingStatus()) {
                profileDisplay.add(
                        ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_status") + ": " +
                                ChatColor.GREEN + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_logged")
                                .replace("%%server%%", StringUtils.capitalizeString(friend.getSessionInfo().getLastGame().replace("_", " ")))
                );
            } else {
                profileDisplay.add(
                        ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_status") + ": " +
                                ChatColor.GREEN + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_logged_hidden")
                );
            }
        } else {
            String base = ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_status") + ": " +
                    ChatColor.RED + TimeUtils.formatAgoTimeInt(friend.getSessionInfo().getLastSeen(), user.getLanguage());
            if (friend.getGameSettings().getGeneral().isShowingStatus()) base = " "
                    + this.translatableField.getUnspacedField(
                            user.getLanguage(),
                    "commons_profile_friend_status"
            ).replace(
                    "%%game%%",
                    friend.getSessionInfo().getLastGame().replace("_", " ")
            ).toLowerCase();
            profileDisplay.add(base);
        }
        profileMeta.setLore(profileDisplay);
        profileInfo.setItemMeta(profileMeta);
        return profileInfo;
    }

}
