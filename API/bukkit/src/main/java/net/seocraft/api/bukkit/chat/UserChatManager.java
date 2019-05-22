package net.seocraft.api.bukkit.chat;

import net.seocraft.api.shared.models.Group;
import net.seocraft.api.shared.models.MinecraftFlair;
import net.seocraft.api.shared.models.User;
import org.bukkit.ChatColor;

public class UserChatManager {

    public String getUserFormat(User user, String realm) {
        String userFormat = ChatColor.GRAY + user.getUsername();
        Group primaryGroup = new Group();
        primaryGroup.setPriority(999999999); // Change priority if needed deeper groups
        for (Group group: user.getGroups()) {
            if (group.getPriority() < primaryGroup.getPriority()) primaryGroup = group;
        }
        System.out.println(primaryGroup.getName());
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