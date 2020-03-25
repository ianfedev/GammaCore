package net.seocraft.commons.core.user.partial.settings;

import net.seocraft.api.core.user.partial.settings.GameSettings;
import net.seocraft.api.core.user.partial.settings.partial.ACSettings;
import net.seocraft.api.core.user.partial.settings.partial.GeneralSettings;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class UserGameSettings implements GameSettings {

    @NotNull private ACSettings adminChat;
    @NotNull private GeneralSettings general;

    @ConstructorProperties({
            "adminChat",
            "general"
    })
    public UserGameSettings(@NotNull ACSettings adminChat, @NotNull GeneralSettings general) {
        this.adminChat = adminChat;
        this.general = general;
    }

    @Override
    public @NotNull ACSettings getAdminChat() {
        return this.adminChat;
    }

    @Override
    public @NotNull GeneralSettings getGeneral() {
        return this.general;
    }
}
