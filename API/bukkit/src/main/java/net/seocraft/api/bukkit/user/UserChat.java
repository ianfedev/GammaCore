package net.seocraft.api.bukkit.user;

import net.seocraft.api.shared.model.Group;
import net.seocraft.api.shared.model.MinecraftFlair;
import net.seocraft.api.shared.model.User;
import org.bukkit.ChatColor;

public class UserChat {

    public String getUserFormat(User user, String realm) {
        String userFormat = ChatColor.GRAY + user.getUsername();
        Group primaryGroup = user.getPrimaryGroup();
        for (MinecraftFlair flair: primaryGroup.getMinecraftFlairs()) {
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

}