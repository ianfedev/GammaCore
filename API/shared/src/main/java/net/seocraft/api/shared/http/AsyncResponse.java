package net.seocraft.api.shared.http;

import net.seocraft.api.shared.http.exceptions.HTTPException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AsyncResponse<T> {

    private HTTPException throwedException;
    private Status status;
    private T response;

    public AsyncResponse(HTTPException throwedException, @NotNull Status status, @Nullable T response) {
        this.throwedException = throwedException;
        this.status = status;
        this.response = response;
    }

    @Nullable
    public HTTPException getThrowedException() {
        return throwedException;
    }

    public Status getStatus() {
        return status;
    }

    public T getResponse() {
        return response;
    }

    public enum Status {
        SUCCESS, ERROR
    }
}