package net.seocraft.commons.core.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerTokenQuery;
import net.seocraft.api.core.session.AuthValidation;
import net.seocraft.api.core.session.MinecraftSessionManager;
import net.seocraft.commons.core.backend.user.AuthenticationSessionRequest;
import net.seocraft.commons.core.backend.user.UserDisconnectRequest;
import net.seocraft.commons.core.backend.user.UserSwitchRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GammaSessionManager implements MinecraftSessionManager {

    @Inject private ServerTokenQuery serverTokenQuery;
    @Inject private UserSwitchRequest userSwitchRequest;
    @Inject private AuthenticationSessionRequest authenticationSessionRequest;
    @Inject private UserDisconnectRequest userDisconnectRequest;
    @Inject private ObjectMapper mapper;

    @Override
    public @NotNull AuthValidation verifyAuthenticationSession(@NotNull String username, @NotNull String address) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        ObjectNode mapper = this.mapper.createObjectNode();
        mapper.put("username", username);
        mapper.put("address", address);
        return this.mapper.readValue(
                this.authenticationSessionRequest.executeRequest(
                        this.mapper.writeValueAsString(mapper),
                        this.serverTokenQuery.getToken()
                ),
                AuthValidation.class
        );
    }

    @Override
    public boolean serverSwitch(@NotNull String id, @NotNull String server) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        ObjectNode mapper = this.mapper.createObjectNode();
        mapper.put("user", id);
        mapper.put("server", server);
        return this.getSwitcherRequest(mapper);
    }

    @Override
    public boolean lobbySwitch(@NotNull String id, @NotNull String lobby) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        ObjectNode mapper = this.mapper.createObjectNode();
        mapper.put("user", id);
        mapper.put("lobby", lobby);
        return this.getSwitcherRequest(mapper);
    }

    @Override
    public void disconnectSession(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError {
        this.userDisconnectRequest.executeRequest(id, this.serverTokenQuery.getToken());
    }

    private boolean getSwitcherRequest(@NotNull ObjectNode node) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError {
        return this.mapper.readValue(
                this.userSwitchRequest.executeRequest(
                        this.mapper.writeValueAsString(node),
                        this.serverTokenQuery.getToken()
                ),
                Boolean.class
        );
    }
}
