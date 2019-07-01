package net.seocraft.api.shared.session;

import net.seocraft.api.shared.serialization.model.ImplementedBy;
import org.jetbrains.annotations.NotNull;

@ImplementedBy(GameSessionImp.class)
public interface GameSession {

    @NotNull String getPlayerId();

    @NotNull String getAddress();

    @NotNull String getVersion();

}