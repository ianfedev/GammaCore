package net.seocraft.api.bukkit.creator.npc;

import com.google.inject.Inject;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.seocraft.api.bukkit.creator.intercept.PacketAdapter;
import net.seocraft.api.bukkit.creator.intercept.PacketManager;
import net.seocraft.api.bukkit.creator.npc.action.ClickType;
import net.seocraft.api.bukkit.creator.npc.entity.NPCEntity;
import net.seocraft.api.bukkit.creator.v_1_8_R3.npc.NPCEntityBase_v1_8_R3;
import net.seocraft.lib.netty.channel.Channel;
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

public class NPCListenerSetup {

    @Inject private PacketManager packetManager;
    @Inject private NPCManager npcManager;

    public void setup() {
        this.packetManager.addPacketListener(new PacketAdapter("PacketPlayInUseEntity") {
            @Override
            public Object onPacketReceiving(@NotNull Player target, @NotNull Channel channel, @NotNull Object packet) {
                PacketPlayInUseEntity usePacket = (PacketPlayInUseEntity) packet;
                try {
                    Field entityIdField = usePacket.getClass().getDeclaredField("a");
                    entityIdField.setAccessible(true);
                    int entityId = (int) entityIdField.get(usePacket);

                    Field actionField = usePacket.getClass().getDeclaredField("action");
                    actionField.setAccessible(true);
                    PacketPlayInUseEntity.EnumEntityUseAction nmsAction = (PacketPlayInUseEntity.EnumEntityUseAction) actionField.get(usePacket);

                    npcManager.getNpcs().forEach(npc -> {
                        if (npc.getEntityId() == entityId && npc instanceof NPCEntityBase_v1_8_R3) {
                            ((NPCEntityBase_v1_8_R3) npc).onInteract(target, nmsAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK ? ClickType.LEFT_CLICK : ClickType.RIGHT_CLICK);
                        }
                    });

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                return usePacket;
            }
        });

        this.packetManager.addPacketListener(new PacketAdapter("PacketPlayOutNamedEntitySpawn") {
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
        });
    }

    private Collection<Player> getPlayersInWorld(World world) {
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
