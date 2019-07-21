package net.seocraft.commons.bukkit.whisper;

import net.seocraft.api.bukkit.whisper.Whisper;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class WhisperMessage implements Whisper {

    @NotNull private User from;
    @NotNull private User to;
    @NotNull private String content;

    @ConstructorProperties({"from", "to", "content"})
    WhisperMessage(@NotNull User from, @NotNull User to, @NotNull String content) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.content = Objects.requireNonNull(content);
    }

    @Override
    public @NotNull User from() {
        return from;
    }

    @Override
    public @NotNull User to() {
        return to;
    }

    @Override
    public @NotNull String content() {
        return content;
    }
}
