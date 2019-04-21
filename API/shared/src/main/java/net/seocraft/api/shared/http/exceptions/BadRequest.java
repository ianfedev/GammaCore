package net.seocraft.api.shared.http.exceptions;

public class BadRequest extends Exception {

    private String reason;

    public BadRequest(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return this.reason;
    }
}
