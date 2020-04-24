package net.seocraft.api.bukkit.creator.npc.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.seocraft.api.bukkit.creator.intercept.PacketAdapter;
import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.NPCManager;
import net.seocraft.api.bukkit.creator.npc.action.ClickType;
import net.seocraft.api.bukkit.creator.v_1_8_R3.npc.NPCEntityBase_v1_8_R3;
import net.seocraft.lib.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@Singleton
public class NPCUseListener extends PacketAdapter {

    @Inject
    private NPCManager npcManager;

    @Inject NPCUseListener() {
        super("PacketPlayInUseEntity");
    }

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

            for (NPC npc : npcManager.getNpcs()) {
                if (npc.getEntityId() == entityId && npc instanceof NPCEntityBase_v1_8_R3) {
                    ((NPCEntityBase_v1_8_R3) npc).onInteract(target, nmsAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK ? ClickType.LEFT_CLICK : ClickType.RIGHT_CLICK);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return usePacket;
    }
}