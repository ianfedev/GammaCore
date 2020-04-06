package net.seocraft.api.core.concurrent;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface Callback<T> {

    void call(T object) throws Unauthorized, InternalServerError, BadRequest, NotFound, JsonProcessingException;

    default void handleException(Throwable throwable){
        Logger.getGlobal().log(Level.SEVERE, "Error executing callback.", throwable);
    }
}
