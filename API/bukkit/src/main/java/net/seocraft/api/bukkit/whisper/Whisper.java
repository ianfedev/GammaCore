package net.seocraft.api.bukkit.whisper;

import net.seocraft.api.shared.models.User;

public interface Whisper {
    User from();
    User to();

    String content();
}
