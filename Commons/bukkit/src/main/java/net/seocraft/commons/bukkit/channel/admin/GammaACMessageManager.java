package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.channel.admin.*;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
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
            ACParticipantsProvider participantsProvider, ACBroadcaster broadcaster
    ) {
        this.mentionParser = mentionParser;
        this.participantsProvider = participantsProvider;
        this.broadcaster = broadcaster;
        this.messageChannel = messager.getChannel("ac_messages", ACMessage.class);
        this.messageChannel.registerListener(new ACMessageListener(broadcaster));
    }

    @Override
    public void sendMessage(@NotNull String message, @NotNull User sender, boolean important) throws Unauthorized, InternalServerError, BadRequest, IOException {
        Set<User> mentionedUsers = this.mentionParser.getMentionedUsers(message)
                .stream()
                .filter((user) -> {
                    for (User participant : this.participantsProvider.getChannelParticipants())
                        if (participant.getId().equalsIgnoreCase(user.getId())) return true;
                    return false;
                }).collect(Collectors.toSet());
        ACMessage formattedMessage = new GammaACMessage(message, sender, mentionedUsers, important);
        this.messageChannel.sendMessage(formattedMessage);
        this.broadcaster.deliveryMessage(formattedMessage);
    }

}
