package net.seocraft.commons.bukkit.game.match;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchTimerProvider;
import net.seocraft.api.core.redis.RedisClient;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.logging.Level;

public class GameMatchTimerProvider implements MatchTimerProvider {

    @Inject private RedisClient redisClient;

    @Override
    public void updateMatchRemaingTime(@NotNull Match match, int time) {
        this.redisClient.setString("matchTimer:" + match.getId(), time + "");
    }

    @Override
    public int getRemainingTime(@NotNull Match match) {
        int number = 0;
        try {
            number = Integer.parseInt(this.redisClient.getString("matchTimer:" + match.getId()));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "[GameAPI] Error while parsing remaining time from match.", e);
        }
        return number;
    }

    @Override
    public boolean hasRemainingTime(@NotNull Match match) {
        return this.redisClient.existsKey("matchTimer:" + match.getId());
    }

    @Override
    public void removeMatchTime(@NotNull Match match) {
        this.redisClient.deleteString("matchTimer:" + match.getId());
    }
}
