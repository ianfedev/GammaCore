package net.seocraft.api.core.user.partial.settings.partial;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ACSettings {

    boolean isActive();

    void setActive(boolean active);

    @JsonProperty("logs")
    boolean hasActiveLogs();

    @JsonProperty("logs")
    void setActiveLogs(boolean activeLogs);

    @JsonProperty("punishments")
    boolean hasActivePunishments();

    @JsonProperty("punishments")
    void setActivePunishments(boolean activePunishments);

}
