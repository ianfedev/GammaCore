package net.seocraft.commons.core.user.partial;

import net.seocraft.api.core.user.partial.IPRecord;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class PlayerIP implements IPRecord {

    @NotNull private final String number;
    @NotNull private final String country;
    private final boolean primary;

    @ConstructorProperties({"number", "country", "primary"})
    public PlayerIP(@NotNull String number, @NotNull String country, boolean primary) {
        this.number = number;
        this.country = country;
        this.primary = primary;
    }

    @Override
    public @NotNull String getNumber() {
        return number;
    }

    @Override
    public @NotNull String getCountry() {
        return country;
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }
}
