package net.seocraft.api.core.concurrent;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface Callback<T> {

    void call(T object) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError;

    default void handleException(Throwable throwable){
        Logger.getGlobal().log(Level.SEVERE, "Error executing callback.", throwable);
    }
}
