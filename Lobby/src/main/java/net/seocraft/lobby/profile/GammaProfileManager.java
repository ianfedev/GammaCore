package net.seocraft.lobby.profile;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.profile.ProfileManager;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.storage.Pagination;
import net.seocraft.api.core.user.User;
import net.seocraft.api.bukkit.minecraft.NBTTagHandler;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.InventoryUtils;
import net.seocraft.commons.core.model.GammaPagination;
import net.seocraft.commons.core.translation.TranslatableField;
import net.seocraft.lobby.profile.icon.FriendsMenuIconsUtil;
import net.seocraft.lobby.profile.icon.LanguageMenuIconsUtil;
import net.seocraft.lobby.profile.icon.ProfileMenuIconsUtil;
import net.seocraft.lobby.profile.icon.SocialMenuIconsUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GammaProfileManager implements ProfileManager {

    @Inject private ProfileMenuIconsUtil profileMenuIconsUtil;
    @Inject private FriendshipProvider friendshipProvider;
    @Inject private FriendsMenuIconsUtil friendsMenuIconsUtil;
    @Inject private LanguageMenuIconsUtil languageMenuIconsUtil;
    @Inject private SocialMenuIconsUtil socialMenuIconsUtil;
    @Inject private Plugin plugin;
    @Inject private TranslatableField translatableField;

    @Override
    public void openMainMenu(@NotNull User user) {

        Player player = Bukkit.getPlayer(user.getUsername());

        if (player != null) {
            Map<Integer, ItemStack> items = new HashMap<>();

            items.put(1, this.profileMenuIconsUtil.getYouTubeInfo(user));
            items.put(4, this.profileMenuIconsUtil.getProfileInfo(user));
            items.put(7, this.profileMenuIconsUtil.getTwitterInfo(user));
            items.put(12, this.profileMenuIconsUtil.getStatsInfo(user));
            items.put(13, this.profileMenuIconsUtil.getLanguageInfo(user));
            items.put(14, this.profileMenuIconsUtil.getFriendsInfo(user));
            items.put(19, this.profileMenuIconsUtil.getInstagramInfo(user));
            items.put(22, this.profileMenuIconsUtil.getSocialInfo(user));
            items.put(25, this.profileMenuIconsUtil.getFacbookInfo(user));

            Bukkit.getScheduler().runTask(plugin, () -> player.openInventory(
                    InventoryUtils.createInventory(
                            this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_menu"),
                            27,
                            items
                    )
            ));
        }

    }

    @Override
    public void openFriendsMenu(@NotNull User user, int page) {

        Player player = Bukkit.getPlayer(user.getUsername());

        if (player != null) {
            CallbackWrapper.addCallback(this.friendshipProvider.listFriends(user.getId()), friendsResponse -> {
                if (friendsResponse.getStatus() == AsyncResponse.Status.SUCCESS) {

                    Set<User> friends = friendsResponse.getResponse();
                    Map<Integer, ItemStack> items = new HashMap<>();
                    Pagination<User> friendPagination = null;
                    if (friends.size() > 0) friendPagination = new GammaPagination<>(9, friends);

                    items.put(0, this.friendsMenuIconsUtil.addFriendIcon(user));
                    items.put(1, this.friendsMenuIconsUtil.removeFriendIcon(user));
                    items.put(22, this.goBackItem(user));

                    if (friendPagination == null) {
                        items.put(13, this.friendsMenuIconsUtil.noFriendsItem(user));
                    }

                    if (friendPagination != null) {
                        for (int i = 9; i < 18; i++)
                            items.put(i, this.friendsMenuIconsUtil.getFriend(
                                    user,
                                    friendPagination.getPage(page).get(i))
                            );

                        if (page != 1 && friendPagination.getPageSize() != 1) {
                            items.put(18, this.friendsMenuIconsUtil.getPreviousPage(user, page - 1));
                        }

                        if (page != 1 && friendPagination.getPageSize() != page) {
                            items.put(26, this.friendsMenuIconsUtil.getNextPage(user, page + 1));
                        }
                    }

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.openInventory(InventoryUtils.createInventory(
                                this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_friends"),
                                27,
                                items
                        ));
                        player.updateInventory();
                    });

                } else {
                    ChatAlertLibrary.errorChatAlert(player, this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_error"));
                    player.closeInventory();
                    player.updateInventory();
                }
            });
        }
    }

    @Override
    public void openFriendsMenu(@NotNull User user) {
        openFriendsMenu(user, 1);
    }

    @Override
    public void openLanguageMenu(@NotNull User user) {
        Player player = Bukkit.getPlayer(user.getUsername());

        if (player != null) {
            Map<Integer, ItemStack> items = new HashMap<>();

            items.put(11, this.languageMenuIconsUtil.spanishIcon(user));
            items.put(13, this.languageMenuIconsUtil.englishIcon(user));
            items.put(15, this.languageMenuIconsUtil.frenchIcon(user));
            items.put(22, this.goBackItem(user));

            Bukkit.getScheduler().runTask(plugin, () -> {
                player.openInventory(
                        InventoryUtils.createInventory(
                                this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_language"),
                                27,
                                items
                        )
                );
                player.updateInventory();
            });
        }
    }

    @Override
    public void openStatsMenu(@NotNull User user) {
        //TODO: Create social menu
    }

    @Override
    public void openSocialMenu(@NotNull User user) {
        Player player = Bukkit.getPlayer(user.getUsername());

        if (player != null) {
            Map<Integer, ItemStack> items = new HashMap<>();

            items.put(11, this.socialMenuIconsUtil.getPublicEmail(user));
            items.put(12, this.socialMenuIconsUtil.getTwitter(user));
            items.put(13, this.socialMenuIconsUtil.getReddit(user));
            items.put(14, this.socialMenuIconsUtil.getSteam(user));
            items.put(15, this.socialMenuIconsUtil.getTwitch(user));

            Bukkit.getScheduler().runTask(plugin, () -> {
                player.openInventory(
                        InventoryUtils.createInventory(
                                this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social"),
                                27,
                                items
                        )
                );
                player.updateInventory();
            });
        }
    }

    private  @NotNull ItemStack goBackItem(@NotNull User user) {
        ItemStack goBack = NBTTagHandler.addString(
                new ItemStack(Material.ARROW),
                "lobby_accessor",
                "back"
        );
        ItemMeta backMeta = goBack.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_back"));
        goBack.setItemMeta(backMeta);
        return goBack;
    }
}
