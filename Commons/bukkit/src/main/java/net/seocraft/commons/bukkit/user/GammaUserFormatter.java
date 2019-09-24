package net.seocraft.commons.bukkit.user;

import net.seocraft.api.bukkit.user.UserFormatter;
import net.seocraft.api.core.group.Group;
import net.seocraft.api.core.group.partial.Flair;
import net.seocraft.api.core.user.User;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class GammaUserFormatter implements UserFormatter {

    @Override
    public @NotNull String getUserFormat(@NotNull User user, @NotNull String realm) {
        String userFormat = ChatColor.GRAY + user.getUsername();
        Group primaryGroup = user.getPrimaryGroup();
        for (Flair flair: primaryGroup.getMinecraftFlairs()) {
            if (flair.getRealm().equalsIgnoreCase(realm) && !flair.getSymbol().equalsIgnoreCase("")) {
                String symbol = flair.getSymbol();
                for (int i = 0; i < symbol.length(); i++) {
                    String character = "" + flair.getSymbol().charAt(0);
                    if (character.equalsIgnoreCase(" ")) {
                        symbol = symbol.substring(i);
                    } else {
                        break;
                    }
                }
                userFormat = ChatColor.valueOf(flair.getColor().toUpperCase()) + symbol + " " + user.getUsername() + ChatColor.WHITE;
                break;
            }
        }

        return userFormat;
    }

    @Override
    public @NotNull String getUserColor(@NotNull User user, @NotNull String realm) {
        String userFormat = ChatColor.GRAY + user.getUsername();
        Group primaryGroup = user.getPrimaryGroup();
        for (Flair flair: primaryGroup.getMinecraftFlairs()) {
            if (flair.getRealm().equalsIgnoreCase(realm) && !flair.getSymbol().equalsIgnoreCase("")) {
                String symbol = flair.getSymbol();
                for (int i = 0; i < symbol.length(); i++) {
                    String character = "" + flair.getSymbol().charAt(0);
                    if (character.equalsIgnoreCase(" ")) {
                        symbol = symbol.substring(i);
                    } else {
                        break;
                    }
                }
                userFormat = ChatColor.valueOf(flair.getColor().toUpperCase()) + user.getUsername() + ChatColor.WHITE;
                break;
            }
        }

        return userFormat;
    }

}