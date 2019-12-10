package net.seocraft.commons.bukkit.cloud;

import com.google.inject.Inject;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.LobbyIcon;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GammaCloudManager implements CloudManager {

    @Inject private ServerManager serverManager;

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
        while (Bukkit.getPlayer(player.getName()) == null) {
            CloudNetDriver.getInstance().getCloudServicesAsync(group).addListener(new ITaskListener<Collection<ServiceInfoSnapshot>>() {
                @Override
                public void onComplete(ITask<Collection<ServiceInfoSnapshot>> task, Collection<ServiceInfoSnapshot> serviceInfoSnapshots) {
                    if (!serviceInfoSnapshots.isEmpty()) {
                        serviceInfoSnapshots.stream().findAny().ifPresent(serviceInfoSnapshot -> sendPlayerToServer(player, serviceInfoSnapshot.getServiceId().getName()));
                    }
                }
            });
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ignore) {}
        }
    }

    @Override
    public Set<LobbyIcon> getGroupLobbies(@NotNull String group) {
        return CloudNetDriver.getInstance()
                .getCloudServiceByGroup(group)
                .stream()
                .map(s -> {
                    s.getConfiguration();
                    try {
                        Optional<Server> server = this.serverManager.getServerByQuerySync(null, null, null,  null, s.getServiceId().getName()).stream().findFirst();
                        return server.map(value -> new GammaLobbyIcon(
                                s.getServiceId().getName(),
                                value.getOnlinePlayers().size(),
                                Integer.parseInt(s.getServiceId().getName().split("-")[1]),
                                server.get().getMaxPlayers()
                        )).orElse(null);
                    } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ignore) {}
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public UUID createCloudService(@NotNull String taskName) {
        ServiceTask task = CloudNetDriver.getInstance().getServiceTask(taskName);
        ServiceInfoSnapshot snapshot = CloudNetDriver.getInstance().createCloudService(task);
        CloudNetDriver.getInstance().startCloudService(snapshot);
        return snapshot.getServiceId().getUniqueId();
    }

    @Override
    public boolean isConnected(@NotNull UUID service) {
        return CloudNetDriver.getInstance().getCloudService(service).isConnected();
    }

    @Override
    public @NotNull String getSlug(@NotNull UUID service) {
        return CloudNetDriver.getInstance().getCloudService(service).getServiceId().getName();
    }


}
