package net.seocraft.commons.core.backend.http;

import net.seocraft.api.core.http.exceptions.HTTPException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AsyncResponse<T> {

    private Exception throwedException;
    private Status status;
    private T response;

    public AsyncResponse(Exception throwedException, @NotNull Status status, @Nullable T response) {
        this.throwedException = throwedException;
        this.status = status;
        this.response = response;
    }

    @NotNull public Exception getThrowedException() {
        return throwedException;
    }

    public int getStatusCode() {
        if (throwedException instanceof HTTPException) {
            HTTPException exception = (HTTPException) throwedException;
            return exception.statusCode();
        }
        return 0;
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