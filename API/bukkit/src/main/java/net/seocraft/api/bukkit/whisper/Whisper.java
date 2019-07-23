package net.seocraft.api.bukkit.whisper;


import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface Whisper {

    @NotNull User from();
    @NotNull User to();

    @NotNull String content();
}
