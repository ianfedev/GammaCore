package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.redis.messager.ChannelListener;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserExpulsion;
import net.seocraft.api.core.user.UserStorageProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishmentListener implements ChannelListener<Punishment> {

    private PunishmentActions punishmentActions;
    private Channel<UserExpulsion> expulsionChannel;

    PunishmentListener(PunishmentActions punishmentActions, Channel<UserExpulsion> expulsionChannel) {
        this.punishmentActions = punishmentActions;
        this.expulsionChannel = expulsionChannel;
    }

    @Override
    public void receiveMessage(Punishment punishment) {
        User targetData = punishment.getPunished();
        Player target = Bukkit.getPlayer(targetData.getUsername());
        switch (punishment.getType()) {
            case BAN: {
                this.punishmentActions.broadcastBan(punishment);
                BridgedUserBan.banPlayer(expulsionChannel, punishment, targetData);
                break;
            }
            case KICK: {
                this.punishmentActions.kickPlayer(target.getPlayer(), targetData, punishment);
                break;
            }
            case WARN: {
                this.punishmentActions.warnPlayer(target.getPlayer(), targetData, punishment);
                break;
            }
            default: break;
        }
    }
}
