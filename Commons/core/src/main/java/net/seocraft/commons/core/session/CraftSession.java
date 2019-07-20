package net.seocraft.commons.core.session;

import net.seocraft.api.core.session.GameSession;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class CraftSession implements GameSession {

    @NotNull private String playerId;
    @NotNull private String address;
    @NotNull private String version;

    @ConstructorProperties({"playerId", "address", "version"})
    public CraftSession(@NotNull String playerId, @NotNull String address, @NotNull String version) {
        this.playerId = playerId;
        this.address = address;
        this.version = version;
    }

    @Override
    public @NotNull String getPlayerId() {
        return this.playerId;
    }

    @Override
    public @NotNull String getAddress() {
        return this.address;
    }

    @Override
    public @NotNull String getVersion() {
        return this.version;
    }

}
