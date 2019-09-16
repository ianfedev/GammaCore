package net.seocraft.api.core.user;

import org.jetbrains.annotations.NotNull;

public interface UserExpulsion {

    @NotNull User getUser();

    @NotNull String getReason();

    long getExpiration();

    boolean isPermanent();

}
