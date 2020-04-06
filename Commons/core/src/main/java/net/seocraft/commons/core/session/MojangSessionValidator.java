package net.seocraft.commons.core.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.session.MojangSessionValidation;
import net.seocraft.commons.core.backend.mojang.MojangUUIDRequest;
import org.apache.commons.codec.Charsets;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class MojangSessionValidator implements MojangSessionValidation {

    @Inject private MojangUUIDRequest request;
    @Inject private ObjectMapper mapper;

    @Override
    public boolean hasValidUUID(@NotNull String username, @NotNull String UUID) throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException {
        String validation = this.request.executeRequest(UUID);
        JsonNode node = this.mapper.readTree(validation);
        return node.get("name").toString().equalsIgnoreCase(username) &&
                node.get("status").toString().equalsIgnoreCase("OK");
    }
}
