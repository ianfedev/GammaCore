package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserExpulsion;
import net.seocraft.commons.core.user.PlayerExpulsion;

public class BridgedUserBan {

    public static void banPlayer(Channel<UserExpulsion> expulsionChannel, Punishment punishment, User targetData) {
        expulsionChannel.sendMessage(new PlayerExpulsion(targetData, punishment.getReason(), punishment.getExpiration(), punishment.isPermanent()));
    }
}
