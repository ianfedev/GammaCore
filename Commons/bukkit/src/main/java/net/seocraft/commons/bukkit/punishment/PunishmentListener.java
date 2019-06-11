package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.redis.ChannelListener;
import net.seocraft.api.shared.user.model.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishmentListener implements ChannelListener<Punishment> {

    private CommonsBukkit instance;
    private UserStoreHandler userStoreHandler;
    private PunishmentActions punishmentActions;

    PunishmentListener(CommonsBukkit instance, UserStoreHandler userStoreHandler, PunishmentActions punishmentActions) {
        this.instance = instance;
        this.userStoreHandler = userStoreHandler;
        this.punishmentActions = punishmentActions;
    }

    @Override
    public void receiveMessage(Punishment punishment) {
        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(punishment.getPunishedId()), targetAsyncResponse -> {
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
