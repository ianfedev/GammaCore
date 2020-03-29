package net.seocraft.commons.core.session;

import net.seocraft.api.core.session.AuthValidation;
import net.seocraft.api.core.user.User;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class MinecraftAuthValidation implements AuthValidation {

    @NotNull private User validatedUser;
    private boolean registered;
    private boolean multipleAccounts;

    @ConstructorProperties({"user", "registered", "multiAccount"})
    public MinecraftAuthValidation(@NotNull User validatedUser, boolean registered, boolean multipleAccounts) {
        this.validatedUser = validatedUser;
        this.registered = registered;
        this.multipleAccounts = multipleAccounts;
    }

    @Override
    public @NotNull User getValidatedUser() {
        return this.validatedUser;
    }

    @Override
    public boolean isRegistered() {
        return this.registered;
    }

    @Override
    public boolean hasMultipleAccounts() {
        return this.multipleAccounts;
    }
}
