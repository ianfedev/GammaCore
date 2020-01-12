package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.channel.admin.*;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserPermissionChecker;
import net.seocraft.commons.bukkit.channel.admin.listener.ACLoginListener;
import net.seocraft.commons.bukkit.channel.admin.listener.ACLogoutListener;
import net.seocraft.commons.bukkit.channel.admin.listener.ACMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class GammaACMessageManager implements ACMessageManager {

    private ACMentionParser mentionParser;
    private ACParticipantsProvider participantsProvider;
    private ACBroadcaster broadcaster;
    private Channel<ACMessage> messageChannel;

    @Inject
    GammaACMessageManager(
            ACMentionParser mentionParser, Messager messager,
            ACParticipantsProvider participantsProvider, ACBroadcaster broadcaster,
            ACLoginBroadcaster loginBroadcaster, UserPermissionChecker permissionChecker
    ) {
        this.mentionParser = mentionParser;
        this.participantsProvider = participantsProvider;
        this.broadcaster = broadcaster;
        this.messageChannel = messager.getChannel("ac_messages", ACMessage.class);
        Channel<User> loginChannel = messager.getChannel("ac_login", User.class);
        Channel<User> logoutChannel = messager.getChannel("ac_logout", User.class);
        this.messageChannel.registerListener(new ACMessageListener(broadcaster));
        loginChannel.registerListener(new ACLoginListener(loginBroadcaster, permissionChecker));
        logoutChannel.registerListener(new ACLogoutListener(loginBroadcaster, permissionChecker));
    }

    @Override
    public void sendMessage(@NotNull String message, @NotNull User sender, boolean important) throws Unauthorized, InternalServerError, BadRequest, IOException {
        Set<User> mentionedUsers = this.mentionParser.getMentionedUsers(message)
                .stream()
                .filter((user) -> {
                    for (User participant : this.participantsProvider.getChannelParticipants())
                        if (
                                participant.getId().equalsIgnoreCase(user.getId()) &&
                                !participant.getId().equalsIgnoreCase(sender.getId()) // Comment this line to allow own-mention
                        ) return true;
                    return false;
                }).collect(Collectors.toSet());
        ACMessage formattedMessage = new GammaACMessage(message, sender, mentionedUsers, important);
        this.messageChannel.sendMessage(formattedMessage);
        this.broadcaster.deliveryMessage(formattedMessage);
    }

}
