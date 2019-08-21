package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.redis.messager.ChannelListener;
import net.seocraft.api.core.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishmentListener implements ChannelListener<Punishment> {

    private UserStorageProvider userStorageProvider;
    private PunishmentActions punishmentActions;

    PunishmentListener(UserStorageProvider userStorageProvider, PunishmentActions punishmentActions) {
        this.userStorageProvider = userStorageProvider;
        this.punishmentActions = punishmentActions;
    }

    @Override
    public void receiveMessage(Punishment punishment) {
        System.out.println(punishment.getPunishedId());
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(punishment.getPunishedId()), targetAsyncResponse -> {
            User targetData = targetAsyncResponse.getResponse();
            Player target = Bukkit.getPlayer(targetData.getUsername());

            if (target != null && targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                switch (punishment.getPunishmentType()) {
                    case BAN: {
                        this.punishmentActions.banPlayer(target.getPlayer(), targetData, punishment);
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
        });
    }
}
