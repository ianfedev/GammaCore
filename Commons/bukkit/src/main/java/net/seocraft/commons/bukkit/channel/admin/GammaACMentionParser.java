package net.seocraft.commons.bukkit.channel.admin;

import net.seocraft.api.bukkit.channel.admin.ACMentionParser;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class GammaACMentionParser implements ACMentionParser {

    @Override
    public @NotNull Set<User> getMentionedUsers(@NotNull String rawMessage) {
        //TODO: Parse mentioned users and check if they're participants
        return new HashSet<>();
    }

}
