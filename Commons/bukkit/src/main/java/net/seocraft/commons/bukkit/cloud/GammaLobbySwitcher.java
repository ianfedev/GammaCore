package net.seocraft.commons.bukkit.cloud;

import com.google.inject.Inject;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GammaLobbySwitcher implements CloudManager {

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
        CloudNetDriver.getInstance().getCloudServicesAsync(group).addListener(new ITaskListener<Collection<ServiceInfoSnapshot>>() {
            @Override
            public void onComplete(ITask<Collection<ServiceInfoSnapshot>> task, Collection<ServiceInfoSnapshot> serviceInfoSnapshots) {
                if (!serviceInfoSnapshots.isEmpty()) {
                    serviceInfoSnapshots.stream().findAny().ifPresent(serviceInfoSnapshot -> sendPlayerToServer(player, serviceInfoSnapshot.getServiceId().getName()));
                }
            }
        });
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
    public void getOnlinePlayers(@NotNull Gamemode gamemode) {
        int counter;
        for (SubGamemode sub : gamemode.getSubGamemodes()) {
            for (IPermissionUser user : CloudNetDriver.getInstance().getUsers()) {

            }
        }
    }

}
