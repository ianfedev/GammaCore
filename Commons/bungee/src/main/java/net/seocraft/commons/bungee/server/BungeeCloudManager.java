package net.seocraft.commons.bungee.server;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import org.jetbrains.annotations.NotNull;

public class BungeeCloudManager {

    public static void sendPlayerToServer(@NotNull String player, @NotNull String UUID, @NotNull String server) {
        CloudNetDriver.getInstance().sendChannelMessage(
                "cloudnet-bridge-channel-player-api",
                "send_on_proxy_player_to_server",
                new JsonDocument()
                        .append("uniqueId", UUID)
                        .append("name", player)
                        .append("serviceName", server)
        );
    }

    public static void sendPlayerToGroup(@NotNull String player, String UUID, @NotNull String group) {
        sendPlayerToServer(player, UUID, group);
    }

}
