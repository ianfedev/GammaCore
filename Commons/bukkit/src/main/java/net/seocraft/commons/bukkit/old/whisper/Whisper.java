package net.seocraft.commons.bukkit.old.whisper;


import net.seocraft.api.core.user.User;

public interface Whisper {
    User from();
    User to();

    String content();
}
