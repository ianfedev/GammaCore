package net.seocraft.commons.bukkit.whisper;


import net.seocraft.api.shared.user.model.User;

public interface Whisper {
    User from();
    User to();

    String content();
}
