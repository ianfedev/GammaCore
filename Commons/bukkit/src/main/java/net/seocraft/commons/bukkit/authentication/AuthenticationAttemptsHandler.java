package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.commons.core.utils.TimeUtils;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;

import java.util.Date;

@Singleton
public class AuthenticationAttemptsHandler {

    @Inject private CommonsBukkit instance;
    @Inject private RedisClient client;

    public void setAttemptLock(String uuid, String date) {
        client.setHash("authentication_locks", uuid, date);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.instance, () -> this.client.deleteHash("authentication_locks", "uuid"), 1000*60*3);
    }

    public boolean getAttemptStatus(String uuid) {
        if (this.client.existsKey("authentication_locks") && this.client.getHashFields("authentication_locks").containsKey(uuid)) {
            return TimeUtils.parseUnixStamp(
                    Integer.parseInt(
                            this.client.getHashFields("authentication_locks").get(uuid))
            ).before(new Date());
        } else {
            return true;
        }
    }

    public String getAttemptLockDelay(String uuid) {
        if (this.client.existsKey("authentication_locks") && this.client.getHashFields("authentication_locks").containsKey(uuid)) {
            return getRemainingTime(
                    TimeUtils.parseUnixStamp(
                            Integer.parseInt(this.client.getHashFields("authentication_locks").get(uuid))
                    )
            );
        } else {
            return getRemainingTime(new Date());
        }
    }

    private String getRemainingTime(Date date) {
        double diff = date.getTime() - new Date().getTime();
        int finalMinutes = (int) diff / (60 * 1000) % 60;
        if (finalMinutes > 1) {
            return (finalMinutes + " minutes");
        } else if (diff == 1) {
            return (finalMinutes + " minute");
        } else {
            return "Less than a minute";
        }
    }
}
