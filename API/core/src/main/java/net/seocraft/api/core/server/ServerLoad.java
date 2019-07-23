package net.seocraft.api.core.server;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;

public interface ServerLoad {

    Server setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError;

    void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError;

}
