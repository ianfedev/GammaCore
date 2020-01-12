package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.bukkit.channel.admin.ACBroadcaster;
import net.seocraft.api.bukkit.channel.admin.ACMentionParser;
import net.seocraft.api.bukkit.channel.admin.ACMessage;
import net.seocraft.api.bukkit.channel.admin.ACMessageManager;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.Messager;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Singleton
public class GammaACMessageManager implements ACMessageManager {

    private ACMentionParser mentionParser;
    private ACBroadcaster broadcaster;
    private Channel<ACMessage> messageChannel;

    @Inject
    GammaACMessageManager(ACMentionParser mentionParser, Messager messager) {
        this.mentionParser = mentionParser;
        this.broadcaster = new GammaACBroadcaster(getChannelParticipants());
        this.messageChannel = messager.getChannel("ac_messages", ACMessage.class);
    }

    @Override
    public @NotNull Set<User> getChannelParticipants() {
        //TODO: Get all users participants
        return new HashSet<>();
    }

    @Override
    public void sendMessage(@NotNull String message, boolean important) {
        Set<User> mentionedUsers = this.mentionParser.getMentionedUsers(message);
        ACMessage formattedMessage = new GammaACMessage(message, mentionedUsers, important);
        this.messageChannel.sendMessage(formattedMessage);
        this.broadcaster.deliveryMessage(formattedMessage, getChannelParticipants());
    }

}
