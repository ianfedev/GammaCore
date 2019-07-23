package net.seocraft.api.bukkit.user;

import net.seocraft.api.core.user.User;

public interface UserFormatter {
    String getUserFormat(User user, String realm);
}