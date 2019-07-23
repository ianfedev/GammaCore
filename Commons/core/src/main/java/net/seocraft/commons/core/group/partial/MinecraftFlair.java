package net.seocraft.commons.core.group.partial;

import net.seocraft.api.core.group.partial.Flair;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class MinecraftFlair implements Flair {

    @NotNull private String realm;
    @NotNull private String color;
    @NotNull private String symbol;

    @ConstructorProperties({"realm", "color", "symbol"})
    public MinecraftFlair(@NotNull String realm, @NotNull String color, @NotNull String symbol) {
        this.realm = realm;
        this.color = color;
        this.symbol = symbol;
    }

    public @NotNull String getRealm() {
        return realm;
    }

    public @NotNull String getColor() {
        return color;
    }

    public @NotNull String getSymbol() {
        return symbol;
    }
}