package net.seocraft.api.core.session;

import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

public interface AuthValidation {

    @NotNull User getValidatedUser();

    boolean isRegistered();

    boolean hasMultipleAccounts();

}
