package net.seocraft.api.shared.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class GameSessionImp implements GameSession {
    @NotNull String playerId;
    @NotNull String address;
    @NotNull String version;
}
