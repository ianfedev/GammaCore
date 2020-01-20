package net.seocraft.lobby.profile.icon;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.utils.StringUtils;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.HeadLibrary;
import net.seocraft.commons.bukkit.util.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class ProfileMenuIconsUtil {

    private UserFormatter userFormatter;
    private TranslatableField translatableField;
    private String realm;

    @Inject
    ProfileMenuIconsUtil(
            TranslatableField translatableField, UserStorageProvider userStorageProvider,
            BukkitAPI bukkitAPI, UserFormatter formatter
    ) {
        this.translatableField = translatableField;
        this.userFormatter = formatter;
        this.realm = bukkitAPI.getConfig().getString("realm");
    }

    public @NotNull ItemStack getProfileInfo(@NotNull User user) {
        ItemStack profileInfo = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta profileMeta = (SkullMeta) profileInfo.getItemMeta();
        profileMeta.setOwner(user.getSkin());
        profileMeta.setDisplayName(this.userFormatter.getUserFormat(user, realm));

        LoreDisplayArray<String> profileDisplay = new LoreDisplayArray<>();
        profileDisplay.add(" ");
        profileDisplay.add(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_rank") + ": " +
                        this.userFormatter.getUserColor(user, realm).replace(user.getUsername(), "") + user.getPrimaryGroup().getName()
        );
        profileDisplay.add(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_level") + ": " +
                        ChatColor.GOLD + user.getLevel()
        );

        String[] name = Bukkit.getServerName().split("-");
        profileDisplay.add(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_status") + ": " +
                        ChatColor.GOLD + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_logged")
                        .replace("%%server%%", StringUtils.capitalizeString(name[0].replace("_", " ")))
        );
        profileMeta.setLore(profileDisplay);
        profileInfo.setItemMeta(profileMeta);
        return profileInfo;
    }

    public @NotNull ItemStack getStatsInfo(@NotNull User user) {
        ItemStack statsHead = NBTTagHandler.addString(
                HeadLibrary.statsHead(),
                "lobby_accessor",
                "stats"
        );
        ItemMeta statsMeta = statsHead.getItemMeta();
        statsMeta.setDisplayName(ChatColor.YELLOW + this.translatableField.getField(user.getLanguage(), "commons_profile_stats")
        + ChatColor.RED + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_coming_soon") + ")");
        statsHead.setItemMeta(statsMeta);
        return statsHead;
    }

    public @NotNull ItemStack getLanguageInfo(@NotNull User user) {
        ItemStack languageHead = NBTTagHandler.addString(
                HeadLibrary.selectHead(),
                "lobby_accessor",
                "language"
        );
        ItemMeta languageMeta = languageHead.getItemMeta();
        languageMeta.setDisplayName(ChatColor.YELLOW + this.translatableField.getField(user.getLanguage(), "commons_profile_language"));
        LoreDisplayArray<String> languageLore = new LoreDisplayArray<>();
        languageLore.add(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_language_lore"), ChatColor.GRAY);
        languageMeta.setLore(languageLore);
        languageHead.setItemMeta(languageMeta);
        return languageHead;
    }

    public @NotNull ItemStack getFriendsInfo(@NotNull User user) {
        ItemStack friendsHead = NBTTagHandler.addString(
                HeadLibrary.friendsHead(),
                "lobby_accessor",
                "friends"
        );
        ItemMeta statsMeta = friendsHead.getItemMeta();
        statsMeta.setDisplayName(ChatColor.YELLOW + this.translatableField.getField(user.getLanguage(), "commons_profile_friends")
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        friendsHead.setItemMeta(statsMeta);
        return friendsHead;
    }

    public @NotNull ItemStack getSocialInfo(@NotNull User user) {
        ItemStack socialHead = NBTTagHandler.addString(
                HeadLibrary.socialHead(),
                "lobby_accessor",
                "social"
        );
        ItemMeta socialMeta = socialHead.getItemMeta();
        socialMeta.setDisplayName(ChatColor.YELLOW + this.translatableField.getField(user.getLanguage(), "commons_profile_social")
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        socialHead.setItemMeta(socialMeta);
        return socialHead;
    }

    public @NotNull ItemStack getYouTubeInfo(@NotNull User user) {
        ItemStack youTubeHead = HeadLibrary.youTubeHead();
        ItemMeta youTubeMeta = youTubeHead.getItemMeta();
        youTubeMeta.setDisplayName(
                ChatColor.RED + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_youtube") + ": " +
                        ChatColor.YELLOW + "SeocraftNetwork"
        );
        youTubeHead.setItemMeta(youTubeMeta);
        return youTubeHead;
    }

    public @NotNull ItemStack getTwitterInfo(@NotNull User user) {
        ItemStack twitterHead = HeadLibrary.twitterHead();
        ItemMeta twitterMeta = twitterHead.getItemMeta();
        twitterMeta.setDisplayName(
                ChatColor.AQUA + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_twitter") + ": " +
                        ChatColor.YELLOW + "SeocraftNetwork"
        );
        twitterHead.setItemMeta(twitterMeta);
        return twitterHead;
    }

    public @NotNull ItemStack getInstagramInfo(@NotNull User user) {
        ItemStack instagramHead = HeadLibrary.instagramHead();
        ItemMeta instagramMeta = instagramHead.getItemMeta();
        instagramMeta.setDisplayName(
                ChatColor.LIGHT_PURPLE + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_instagram") + ": " +
                        ChatColor.YELLOW + "SeocraftNetwork"
        );
        instagramHead.setItemMeta(instagramMeta);
        return instagramHead;
    }

    public @NotNull ItemStack getFacbookInfo(@NotNull User user) {
        ItemStack facebookHead = HeadLibrary.facebookHead();
        ItemMeta facebookMeta = facebookHead.getItemMeta();
        facebookMeta.setDisplayName(
                ChatColor.BLUE + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_facebook") + ": " +
                        ChatColor.YELLOW + "SeocraftNetwork"
        );
        facebookHead.setItemMeta(facebookMeta);
        return facebookHead;
    }
}
