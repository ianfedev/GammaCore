package net.seocraft.lobby.lobby;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.profile.ProfileManager;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.util.InventoryUtils;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GammaProfileManager implements ProfileManager {

    @Inject private ProfileMenuIconsUtil profileMenuIconsUtil;
    @Inject private TranslatableField translatableField;

    @Override
    public void openMainMenu(@NotNull User user) {

        Map<Integer, ItemStack> items = new HashMap<>();

        items.put(1, this.profileMenuIconsUtil.getYouTubeInfo(user));
        items.put(4, this.profileMenuIconsUtil.getProfileInfo(user));
        items.put(7, this.profileMenuIconsUtil.getTwitterInfo(user));
        items.put(12, this.profileMenuIconsUtil.getStatsInfo(user));
        items.put(13, this.profileMenuIconsUtil.getLanguageInfo(user));
        items.put(14, this.profileMenuIconsUtil.getFriendsInfo(user));
        items.put(19, this.profileMenuIconsUtil.getInstagramInfo(user));
        items.put(25, this.profileMenuIconsUtil.getFacbookInfo(user));

        InventoryUtils.createInventory(
                this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_menu"),
                27,
                items
        );

    }

    @Override
    public void openFriendsMenu(@NotNull User user, int page) {

        Map<Integer, ItemStack> items = new HashMap<>();



    }

    @Override
    public void openFriendsMenu(@NotNull User user) {

    }

    @Override
    public void openLanguageMenu(@NotNull User user) {

    }

    @Override
    public void openStatsMenu(@NotNull User user) {

    }
}
