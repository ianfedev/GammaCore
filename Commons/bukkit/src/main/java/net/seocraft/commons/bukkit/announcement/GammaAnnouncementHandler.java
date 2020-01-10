package net.seocraft.commons.bukkit.announcement;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.announcement.AnnouncementHandler;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class GammaAnnouncementHandler implements AnnouncementHandler {

    @Inject private Plugin instance;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;

    @Override
    public @NotNull List<String> getConfiguredAnnouncementList() {
        return this.instance.getConfig().getStringList("announcement.announcements");
    }

    @Override
    public void startAnnouncementDisplaying() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.instance, () -> {
            String field = getRandomAnnouncement();
            Bukkit.getOnlinePlayers().forEach((player) -> CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();
                    player.sendMessage(
                            ChatAlertLibrary.transformChat(
                                    translatableField.getUnspacedField(
                                            user.getLanguage(),
                                            field
                                    )
                            )
                    );
                    player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1f, 1f);
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "[Commons] Error sending announce to player {0}.", player.getName());
                }
            }));
        }, 0, this.instance.getConfig().getInt("announcement.delay") * 20L);
    }

    private @NotNull String getRandomAnnouncement() {
        Random rand = new Random();
        return getConfiguredAnnouncementList().get(rand.nextInt(getConfiguredAnnouncementList().size()));
    }

}
