package net.seocraft.api.bukkit.profile;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface ProfileManager {

    void openMainMenu(@NotNull User user);

    void openFriendsMenu(@NotNull User user, int page);

    void openFriendsMenu(@NotNull User user);

    void openLanguageMenu(@NotNull User user);

    void openStatsMenu(@NotNull User user);

}