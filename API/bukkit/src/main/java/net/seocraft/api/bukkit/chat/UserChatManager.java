package net.seocraft.api.bukkit.chat;

import net.seocraft.api.shared.models.Group;
import net.seocraft.api.shared.models.MinecraftFlair;
import net.seocraft.api.shared.models.User;
import org.bukkit.ChatColor;

public class UserChatManager {

    public String getUserFormat(User user, String realm) {
        String userFormat = ChatColor.GRAY + user.getUsername();
        Group primaryGroup = new Group();
        primaryGroup.setPriority(0);
        for (Group group: user.getGroups()) {
            System.out.println(group.getName() + ": " + group.getPriority());
            if (group.getPriority() > primaryGroup.getPriority()) primaryGroup = group;
        }
        for (MinecraftFlair flair: primaryGroup.getMinecraftFlairs()) {
            if (flair.getRealm().equalsIgnoreCase(realm)) {
                userFormat = ChatColor.valueOf(flair.getColor().toUpperCase()) + flair.getSymbol() + " " + user.getUsername() + ChatColor.WHITE;
            }
        }
        for (int i = 0; i < userFormat.length(); i++) {
            String character = "" + userFormat.charAt(i);
            if (character.equalsIgnoreCase(" ")) {
                userFormat = userFormat.substring(i);
            } else {
                break;
            }
        }
        return userFormat;
    }

}