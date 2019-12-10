package net.seocraft.commons.bukkit.authentication;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.utils.TimeUtils;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;

import java.util.Date;

@Singleton
public class AuthenticationAttemptsHandler {

    @Inject private CommonsBukkit instance;
    @Inject private RedisClient client;

    public void setAttemptLock(String uuid, String date) {
        client.setString("authlock:" + uuid, date);
        client.setExpiration("authlock:" + uuid, 180);
    }

    public boolean getAttemptStatus(String uuid) {
        return this.client.existsKey("authlock:" + uuid);
    }

    public String getAttemptLockDelay(String uuid) {

        if (this.client.existsKey("authlock:" + uuid)) {
            return getRemainingTime(
                    TimeUtils.parseUnixStamp(
                            Integer.parseInt(this.client.getString("authlock:" + uuid))
                    )
            );
        }

        return getRemainingTime(new Date());
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
