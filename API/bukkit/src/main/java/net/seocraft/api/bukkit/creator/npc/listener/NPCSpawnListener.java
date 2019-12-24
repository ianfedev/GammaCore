package net.seocraft.api.bukkit.creator.npc.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.seocraft.api.bukkit.creator.intercept.PacketAdapter;
import net.seocraft.api.bukkit.creator.npc.NPCManager;
import net.seocraft.api.bukkit.creator.npc.entity.NPCEntity;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Singleton
public class NPCSpawnListener extends PacketAdapter {

    @Inject private NPCManager npcManager;

    @Inject
    NPCSpawnListener() {
        super("PacketPlayOutNamedEntitySpawn");
    }

    @Override
    public Object onPacketReceiving(@NotNull Player target, @NotNull Channel channel, @NotNull Object packet) {
        PacketPlayOutNamedEntitySpawn entityPacket = (PacketPlayOutNamedEntitySpawn) packet;
        try {
            Field idField = entityPacket.getClass().getDeclaredField("b");
            idField.setAccessible(true);
            UUID id = (UUID) idField.get(entityPacket);
            getPlayersInWorld(target.getWorld()).forEach(player -> {
                if (player.getUniqueId().equals(id) && npcManager.isNpc(player)) {
                    npcManager.getNPC(player).ifPresent(npc -> ((NPCEntity) npc).updateToPlayer(target));
                }
            });
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return entityPacket;
    }

    protected Collection<Player> getPlayersInWorld(World world) {
        List<Player> list = new ArrayList<>();
        if (world == null) { return list; }
        List<Entity> entities = Bukkit.getServer().getWorld(world.getUID()).getEntities();
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                list.add((Player) entity);
            }
        }
        return list;
    }
}