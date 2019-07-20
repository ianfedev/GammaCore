package net.seocraft.api.core.http.exceptions;

public class InternalServerError extends Exception implements HTTPException {
    private String reason;

    public InternalServerError(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return this.reason;
    }

    @Override
    public int statusCode() {
        return 500;
    }
}
