package net.seocraft.api.core.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface MinecraftSessionManager {

    /**
     * Send authorization to login at the backend
     * @param username to be logged
     * @param address of the connected address
     * @return validation with sensitive user data to be processed
     */
    @NotNull AuthValidation verifyAuthenticationSession(@NotNull String username, @NotNull String address) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

    /**
     * Send server switch to the backend
     * @param id of the user who changed server
     * @param server that has been switched by the user
     * @return confirmation
     */
    boolean serverSwitch(@NotNull String id, @NotNull String server) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

    /**
     * Send lobby switch to the backend
     * @param id of the user who changed server
     * @param lobby that has been switched by the user
     * @return confirmation
     */
    boolean lobbySwitch(@NotNull String id, @NotNull String lobby) throws IOException, Unauthorized, BadRequest, NotFound, InternalServerError;

    /**
     * Send disconnect request to the user
     * @param id of the user to disconnect
     */
    void disconnectSession(@NotNull String id) throws Unauthorized, BadRequest, NotFound, InternalServerError;

}
