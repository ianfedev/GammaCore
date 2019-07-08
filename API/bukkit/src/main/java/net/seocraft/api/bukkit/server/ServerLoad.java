package net.seocraft.api.bukkit.server;

import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;

public interface ServerLoad {

    void setupServer() throws Unauthorized, BadRequest, NotFound, InternalServerError;

}
