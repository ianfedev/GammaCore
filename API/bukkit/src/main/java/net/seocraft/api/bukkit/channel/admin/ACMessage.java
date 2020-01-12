package net.seocraft.api.bukkit.channel.admin;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ACMessage {

    @NotNull String getMessage();

    @NotNull Set<User> getMentionUsers();

    boolean isImportant();

}
