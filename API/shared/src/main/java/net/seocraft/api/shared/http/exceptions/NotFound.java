package net.seocraft.api.shared.http.exceptions;

public class NotFound extends Throwable implements HTTPException {
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
