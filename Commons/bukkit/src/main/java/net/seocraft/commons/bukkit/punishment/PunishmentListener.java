package net.seocraft.commons.bukkit.punishment;

import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.models.User;
import net.seocraft.api.shared.redis.ChannelListener;
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
        if (!this.instance.playerIdentifier.containsValue(punishment.getPunishedId())) return;
        Player target = Bukkit.getPlayer(
            this.instance.playerIdentifier.inverse().get(punishment.getPunishedId())
        );

        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(punishment.getPunishedId()), targetAsyncResponse -> {
            User targetData = targetAsyncResponse.getResponse();

            if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                switch (punishment.getPunishmentType()) {
                    case BAN: {
                        Bukkit.getScheduler().runTask(instance, () -> this.punishmentActions.banPlayer(target.getPlayer(), targetData, punishment));
                        break;
                    }
                    case KICK: {
                        Bukkit.getScheduler().runTask(instance, () -> this.punishmentActions.kickPlayer(target.getPlayer(), targetData, punishment));
                        break;
                    }
                    case WARN: {
                        Bukkit.getScheduler().runTask(instance, () -> this.punishmentActions.warnPlayer(target.getPlayer(), targetData, punishment));
                        break;
                    }
                    default: break;
                }
            }
        });
    }
}
