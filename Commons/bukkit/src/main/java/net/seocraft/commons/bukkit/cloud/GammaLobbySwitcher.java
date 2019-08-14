package net.seocraft.commons.bukkit.cloud;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import net.seocraft.api.bukkit.cloud.CloudLobbySwitcher;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GammaLobbySwitcher implements CloudLobbySwitcher {

    @Override
    public void sendPlayerToServer(@NotNull Player player, @NotNull String server) {
        CloudNetDriver.getInstance().sendChannelMessage(
                "cloudnet-bridge-channel-player-api",
                "send_on_proxy_player_to_server",
                new JsonDocument()
                        .append("uniqueId", player.getUniqueId())
                        .append("name", player.getName())
                        .append("serviceName", server)
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendPlayerToGroup(@NotNull Player player, @NotNull String group) {
        CloudNetDriver.getInstance().getCloudServicesAsync(group).addListener(new ITaskListener<Collection<ServiceInfoSnapshot>>() {
            @Override
            public void onComplete(ITask<Collection<ServiceInfoSnapshot>> task, Collection<ServiceInfoSnapshot> serviceInfoSnapshots) {
                if (!serviceInfoSnapshots.isEmpty()) {
                    serviceInfoSnapshots.stream().findAny().ifPresent(serviceInfoSnapshot -> {
                        sendPlayerToServer(player, serviceInfoSnapshot.getServiceId().getName());
                    });
                }
            }
        });
    }

}
