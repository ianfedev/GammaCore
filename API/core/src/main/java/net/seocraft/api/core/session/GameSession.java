package net.seocraft.api.core.session;

import org.jetbrains.annotations.NotNull;

public interface GameSession {

    @NotNull String getPlayerId();

    @NotNull String getAddress();

    @NotNull String getVersion();

}