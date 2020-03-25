package net.seocraft.commons.core.user.partial.settings.partial;

import net.seocraft.api.core.user.partial.settings.partial.ACSettings;

import java.beans.ConstructorProperties;

public class UserACSettings implements ACSettings {

    private boolean active;
    private boolean activeLogs;
    private boolean activePunishments;

    @ConstructorProperties({
            "active",
            "logs",
            "punishments"
    })
    public UserACSettings(boolean active, boolean activeLogs, boolean activePunishments) {
        this.active = active;
        this.activeLogs = activeLogs;
        this.activePunishments = activePunishments;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean hasActiveLogs() {
        return activeLogs;
    }

    @Override
    public void setActiveLogs(boolean activeLogs) {
        this.activeLogs = activeLogs;
    }

    @Override
    public boolean hasActivePunishments() {
        return activePunishments;
    }

    @Override
    public void setActivePunishments(boolean activePunishments) {
        this.activePunishments = activePunishments;
    }
}
