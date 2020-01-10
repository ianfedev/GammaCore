package net.seocraft.api.bukkit.announcement;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AnnouncementHandler {

    @NotNull List<String> getConfiguredAnnouncementList();

    void startAnnouncementDisplaying();

}
