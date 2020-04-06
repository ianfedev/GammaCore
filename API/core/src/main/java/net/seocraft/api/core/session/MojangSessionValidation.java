package net.seocraft.api.core.session;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public interface MojangSessionValidation {

    boolean hasValidUUID(@NotNull String username, @NotNull String UUID) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    @NotNull UUID generateOfflineUUID(@NotNull String username);

}
