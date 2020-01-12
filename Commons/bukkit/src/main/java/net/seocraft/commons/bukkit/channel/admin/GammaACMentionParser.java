package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.channel.admin.ACMentionParser;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GammaACMentionParser implements ACMentionParser {

    @Inject private UserStorageProvider userStorageProvider;

    @Override
    public @NotNull Set<User> getMentionedUsers(@NotNull String rawMessage) throws Unauthorized, IOException, BadRequest, InternalServerError {

        Pattern mentionedPattern = Pattern.compile("@(\\S+)");
        Matcher matchedMentions = mentionedPattern.matcher(rawMessage);
        Set<User> users = new HashSet<>();
        while (matchedMentions.find()) {
            try {
                User user = this.userStorageProvider.findUserByNameSync(matchedMentions.group(1));
                users.add(user);
            } catch (NotFound ignore) {}
        }

        return users;
    }

}
