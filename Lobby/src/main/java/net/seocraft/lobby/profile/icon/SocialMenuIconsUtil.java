package net.seocraft.lobby.profile.icon;

import com.google.inject.Inject;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.HeadLibrary;
import net.seocraft.commons.bukkit.util.LoreDisplayArray;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class SocialMenuIconsUtil {

    @Inject private TranslatableField translatableField;

    public @NotNull ItemStack getPublicEmail(@NotNull User user) {
        ItemStack publicEmail = NBTTagHandler.addString(
                HeadLibrary.mailHead(),
                "social_accessor",
                "email"
        );
        ItemMeta emailMeta = publicEmail.getItemMeta();

        String social = this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_none");
        if (user.getPublicEmail() != null) social = user.getPublicEmail();

        LoreDisplayArray<String> lore = new LoreDisplayArray<>();
        lore.add(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change"), ChatColor.GOLD);
        emailMeta.setLore(lore);

        emailMeta.setDisplayName(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_mail") + ": " +
                ChatColor.GRAY + social
        );
        publicEmail.setItemMeta(emailMeta);
        return publicEmail;
    }

    public @NotNull ItemStack getTwitter(@NotNull User user) {
        ItemStack publicTwitter = NBTTagHandler.addString(
                HeadLibrary.twitterHead(),
                "social_accessor",
                "twitter"
        );
        ItemMeta twitterMeta = publicTwitter.getItemMeta();

        String social = this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_none");
        if (user.getTwitter() != null) social = user.getTwitter();

        LoreDisplayArray<String> lore = new LoreDisplayArray<>();
        lore.add(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change"), ChatColor.GOLD);
        twitterMeta.setLore(lore);

        twitterMeta.setDisplayName(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_twitter") + ": " +
                        ChatColor.GRAY + social +
                        ChatColor.GOLD + " (" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change") + ")"
        );
        publicTwitter.setItemMeta(twitterMeta);
        return publicTwitter;
    }

    public @NotNull ItemStack getReddit(@NotNull User user) {
        ItemStack publicReddit = NBTTagHandler.addString(
                HeadLibrary.redditHead(),
                "social_accessor",
                "reddit"
        );
        ItemMeta redditMeta = publicReddit.getItemMeta();

        String social = this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_none");
        if (user.getReddit() != null) social = user.getReddit();

        LoreDisplayArray<String> lore = new LoreDisplayArray<>();
        lore.add(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change"), ChatColor.GOLD);
        redditMeta.setLore(lore);

        redditMeta.setDisplayName(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_reddit") + ": " +
                        ChatColor.GRAY + social +
                        ChatColor.GOLD + " (" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change") + ")"
        );
        publicReddit.setItemMeta(redditMeta);
        return publicReddit;
    }

    public @NotNull ItemStack getSteam(@NotNull User user) {
        ItemStack publicSteam = NBTTagHandler.addString(
                HeadLibrary.steamHead(),
                "social_accessor",
                "steam"
        );
        ItemMeta steamMeta = publicSteam.getItemMeta();

        String social = this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_none");
        if (user.getSteam() != null) social = user.getSteam();

        LoreDisplayArray<String> lore = new LoreDisplayArray<>();
        lore.add(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change"), ChatColor.GOLD);
        steamMeta.setLore(lore);

        steamMeta.setDisplayName(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_steam") + ": " +
                        ChatColor.GRAY + social +
                        ChatColor.GOLD + " (" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change") + ")"
        );
        publicSteam.setItemMeta(steamMeta);
        return publicSteam;
    }

    public @NotNull ItemStack getTwitch(@NotNull User user) {
        ItemStack publicTwitch = NBTTagHandler.addString(
                HeadLibrary.twitchHead(),
                "social_accessor",
                "twitch"
        );
        ItemMeta twitchMeta = publicTwitch.getItemMeta();

        String social = this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_none");
        if (user.getTwitch() != null) social = user.getTwitch();

        LoreDisplayArray<String> lore = new LoreDisplayArray<>();
        lore.add(this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change"), ChatColor.GOLD);
        twitchMeta.setLore(lore);

        twitchMeta.setDisplayName(
                ChatColor.YELLOW + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_twitch") + ": " +
                        ChatColor.GRAY + social +
                        ChatColor.GOLD + " (" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_profile_social_change") + ")"
        );
        publicTwitch.setItemMeta(twitchMeta);
        return publicTwitch;
    }

}
