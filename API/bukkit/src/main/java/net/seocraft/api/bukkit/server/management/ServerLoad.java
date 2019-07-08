package net.seocraft.api.bukkit.server.management;

import net.seocraft.api.bukkit.server.model.Server;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;

public interface ServerLoad {

    Server setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError;

    void disconnectServer() throws Unauthorized, BadRequest, NotFound, InternalServerError;

}
