package net.seocraft.commons.core.user.partial;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.seocraft.api.core.user.partial.IPRecord;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

@JsonSerialize(as = IPRecord.class)
public class PlayerIP implements IPRecord {

    @NotNull private String number;
    @NotNull private String country;
    @NotNull private boolean primary;

    @ConstructorProperties({"number", "country", "primary"})
    public PlayerIP(@NotNull String number, @NotNull String country, @NotNull boolean primary) {
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
