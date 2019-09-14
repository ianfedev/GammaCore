package net.seocraft.api.core.server;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

import java.io.IOException;

public interface ServerLoad {

    Server setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError, IOException;

    void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError;

}
