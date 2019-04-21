package net.seocraft.api.shared.http.exceptions;

public class InternalServerError extends Throwable {
    private String reason;

    public InternalServerError(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return this.reason;
    }
}
