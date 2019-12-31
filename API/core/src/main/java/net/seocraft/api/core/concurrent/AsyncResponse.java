package net.seocraft.api.core.concurrent;

import net.seocraft.api.core.http.exceptions.HTTPException;
import org.jetbrains.annotations.NotNull;

/**
 * This class will wrap an async response with a success status or an exception
 * @param <T> Interface/Class that will be wrapped
 */
public class AsyncResponse<T> {

    private Exception throwedException;
    @NotNull private Status status;
    private T response;

    /**
     * Constructor of async resposne
     * @param throwedException shouldn't be null when an exception was thrown during the async block
     * @param status should be SUCCESS when throwedException is null or ERROR when response is null
     * @param response shouldn't be null when an exception was never whrown during the async block
     */
    public AsyncResponse(Exception throwedException, @NotNull Status status, T response) {
        this.throwedException = throwedException;
        this.status = status;
        this.response = response;
    }

    /**
     * @return thrown exception when the async block fails
     */
    public Exception getThrowedException() {
        return throwedException;
    }

    /**
     * Will throw exception code when is an HTTP Exception
     * @return server status code.
     */
    public int getStatusCode() {
        if (throwedException instanceof HTTPException) {
            HTTPException exception = (HTTPException) throwedException;
            return exception.statusCode();
        }
        return 0;
    }

    /**
     * @return Async block status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return Response block status
     */
    public T getResponse() {
        return response;
    }

    /**
     * Enum of the reponse status
     */
    public enum Status {
        SUCCESS, ERROR
    }
}