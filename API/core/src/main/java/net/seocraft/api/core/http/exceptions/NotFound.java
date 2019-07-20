package net.seocraft.api.core.http.exceptions;

public class NotFound extends Exception implements HTTPException {
    private String reason;
    public NotFound(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return this.reason;
    }

    @Override
    public int statusCode() {
        return 404;
    }
}
