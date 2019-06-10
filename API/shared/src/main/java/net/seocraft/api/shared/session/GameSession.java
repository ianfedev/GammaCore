package net.seocraft.api.shared.session;

import org.jetbrains.annotations.NotNull;

public interface GameSession {

    @NotNull String getPlayerId();

    @NotNull String getAddress();

    @NotNull String getVersion();
}