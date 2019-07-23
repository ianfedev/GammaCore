package net.seocraft.api.core.group.partial;

import org.jetbrains.annotations.NotNull;

public interface Flair {

    @NotNull String getRealm();

    @NotNull String getColor();

    @NotNull String getSymbol();
}
