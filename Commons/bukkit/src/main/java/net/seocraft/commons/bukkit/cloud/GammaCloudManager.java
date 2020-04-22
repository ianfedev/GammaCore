package net.seocraft.commons.bukkit.cloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.LobbyIcon;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GammaCloudManager implements CloudManager {

    @Inject private CommonsBukkit instance;
    @Inject private ObjectMapper mapper;

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
    public void sendPlayerToGroup(@NotNull Player player, @NotNull String group) {
        sendPlayerToGroupSecured(player, group, 0);
    }

    @SuppressWarnings("unchecked")
    private void sendPlayerToGroupSecured(@NotNull Player player, @NotNull String group, int time) {
        final int finalTimer = time + 1;
        if (finalTimer < 3) {
            CloudNetDriver.getInstance().getCloudServicesAsync(group).addListener(new ITaskListener<Collection<ServiceInfoSnapshot>>() {
                @Override
                public void onComplete(ITask<Collection<ServiceInfoSnapshot>> task, Collection<ServiceInfoSnapshot> serviceInfoSnapshots) {
                    if (!serviceInfoSnapshots.isEmpty()) {
                        serviceInfoSnapshots.stream().findAny().ifPresent(serviceInfoSnapshot -> sendPlayerToServer(player, serviceInfoSnapshot.getServiceId().getName()));
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(instance, () -> sendPlayerToGroupSecured(player, group, finalTimer));
                }
            });
        } else {
            player.sendMessage(ChatColor.RED + "There was an error trying to connecting you to this server, please try again. Att: Seocraft Network ;)");
        }
    }

    @Override
    public @NotNull Set<LobbyIcon> getGroupLobbies(@NotNull String group) {
        return CloudNetDriver.getInstance()
                .getCloudServiceByGroup(group)
                .stream()
                .map(s -> new GammaLobbyIcon(
                        s.getServiceId().getName(),
                        getServiceOnlinePlayers(s),
                        Integer.parseInt(s.getServiceId().getName().split("-")[1]),
                        s.getProperties().getInt("Max-Players")
                )).collect(Collectors.toSet());
    }

    @Override
    public int getGroupOnlinePlayers(@NotNull String name) {
        int counter = 0;
        for (ServiceInfoSnapshot snapshot : CloudNetDriver.getInstance().getCloudServiceByGroup(name)) {
            counter += getServiceOnlinePlayers(snapshot);
        }
        return counter;
    }

    @Override
    public int getGamemodeOnlinePlayers(@NotNull Gamemode gamemode) {
        int counter = 0;
        counter += getGroupOnlinePlayers(gamemode.getLobbyGroup());
        for (SubGamemode subGamemode : gamemode.getSubGamemodes()) counter += getGroupOnlinePlayers(subGamemode.getServerGroup());
        return counter;
    }

    @Override
    public int getOnlinePlayers() {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Integer> task = () -> BridgePlayerManager.getInstance().getOnlinePlayers().size();
        Future<Integer> future = executor.submit(task);
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {

            future.cancel(true);
            return 0;
        }
    }

    @NotNull
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

    private int getServiceOnlinePlayers(@NotNull ServiceInfoSnapshot snapshot) {
        int size = 0;
        try {
            JsonNode node =  this.mapper.readTree(
                    snapshot.getProperties().toJson()
            ).get("Players");
            if (node != null && node.isArray()) size = node.size();
        } catch (IOException ignore) {}
        return size;
    }


}
