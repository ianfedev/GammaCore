package net.seocraft.api.bukkit.whisper;

import javax.annotation.Nullable;
import java.util.Objects;

public class WhisperResponse {

    private Exception throwedException;
    private Response response;
    private Whisper whisper;

    WhisperResponse(Exception throwedException, Response response, Whisper whisper) {
        this.throwedException = throwedException;
        this.response = Objects.requireNonNull(response);
        this.whisper = Objects.requireNonNull(whisper);
    }

    static WhisperResponse getSucessResponse(Whisper whisper) {
        return new WhisperResponse(null, Response.SUCCESS, whisper);
    }

    @Nullable
    public Exception getThrowedException() {
        return throwedException;
    }

    public Response getResponse() {
        return response;
    }

    public Whisper getWhisper() {
        return whisper;
    }

    public enum Response {
        SUCCESS, PLAYER_OFFLINE, ERROR;
    }
}
