package net.seocraft.api.shared.http.exceptions;

public class Unauthorized extends Exception implements HTTPException {
    private String reason;

    public Unauthorized(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return this.reason;
    }

    @Override
    public int statusCode() {
        return 403;
    }
}
