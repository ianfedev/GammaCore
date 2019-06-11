package net.seocraft.commons.bukkit.whisper;

import net.seocraft.api.shared.user.model.User;

import java.util.Objects;

public class WhisperImpl implements Whisper {
    private User from;
    private User to;
    private String content;

    WhisperImpl(User from, User to, String content) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.content = Objects.requireNonNull(content);
    }

    @Override
    public User from() {
        return from;
    }

    @Override
    public User to() {
        return to;
    }

    @Override
    public String content() {
        return content;
    }
}
