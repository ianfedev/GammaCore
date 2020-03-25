package net.seocraft.api.core.user.partial.settings;


import net.seocraft.api.core.user.partial.settings.partial.ACSettings;
import net.seocraft.api.core.user.partial.settings.partial.GeneralSettings;
import org.jetbrains.annotations.NotNull;

public interface GameSettings {

    @NotNull ACSettings getAdminChat();

    @NotNull GeneralSettings getGeneral();

}
