package net.seocraft.api.core.user;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface UserPermissionChecker {

    boolean hasPermission(@NotNull String id, @NotNull String s) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError;

}
