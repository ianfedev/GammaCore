package net.seocraft.lobby.profile.icon;

import com.google.inject.Inject;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.HeadLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class LanguageMenuIconsUtil {

    @Inject private TranslatableField translatableField;

    public @NotNull ItemStack spanishIcon(@NotNull User user) {
        ItemStack spanishIcon = NBTTagHandler.addString(
                HeadLibrary.spanishHead(),
                "language_accessor",
                "es"
        );
        ItemMeta spanishMeta = spanishIcon.getItemMeta();
        spanishMeta.setDisplayName(ChatColor.YELLOW +
                this.translatableField.getField(user.getLanguage(), "commons_profile_language_change")
                        .replace("%%language%%", this.translatableField.getUnspacedField(user.getLanguage(), "commons_es").toLowerCase())
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        spanishIcon.setItemMeta(spanishMeta);
        return spanishIcon;
    }

    public @NotNull ItemStack englishIcon(@NotNull User user) {
        ItemStack englishIcon = NBTTagHandler.addString(
                HeadLibrary.englishHead(),
                "language_accessor",
                "en"
        );
        ItemMeta englishMeta = englishIcon.getItemMeta();
        englishMeta.setDisplayName(ChatColor.YELLOW +
                this.translatableField.getField(user.getLanguage(), "commons_profile_language_change")
                        .replace("%%language%%", this.translatableField.getUnspacedField(user.getLanguage(), "commons_en").toLowerCase())
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        englishIcon.setItemMeta(englishMeta);
        return englishIcon;
    }

    public @NotNull ItemStack frenchIcon(@NotNull User user) {
        ItemStack frenchIcon = NBTTagHandler.addString(
                HeadLibrary.frenchHead(),
                "language_accessor",
                "fr"
        );
        ItemMeta frenchMeta = frenchIcon.getItemMeta();
        frenchMeta.setDisplayName(ChatColor.YELLOW +
                this.translatableField.getField(user.getLanguage(), "commons_profile_language_change")
                        .replace("%%language%%", this.translatableField.getUnspacedField(user.getLanguage(), "commons_fr").toLowerCase())
                + ChatColor.GOLD + "(" + this.translatableField.getUnspacedField(user.getLanguage(), "commons_left_click") + ")");
        frenchIcon.setItemMeta(frenchMeta);
        return frenchIcon;
    }

}
