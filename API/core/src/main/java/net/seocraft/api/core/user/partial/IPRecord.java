package net.seocraft.api.core.user.partial;

import org.jetbrains.annotations.NotNull;

public interface IPRecord {

    @NotNull String getNumber();

    @NotNull String getCountry();

    boolean isPrimary();

}
