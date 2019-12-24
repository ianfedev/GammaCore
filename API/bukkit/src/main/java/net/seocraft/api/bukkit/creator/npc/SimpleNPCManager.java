package net.seocraft.api.bukkit.creator.npc;

import com.google.inject.Inject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.seocraft.api.bukkit.creator.npc.entity.NPCEntityNMS;
import net.seocraft.api.bukkit.creator.npc.entity.player.NPCPlayer;
import net.seocraft.api.bukkit.creator.npc.navigation.Navigator;
import net.seocraft.api.bukkit.creator.skin.SkinProperty;
import net.seocraft.api.bukkit.creator.v_1_8_R3.npc.NPCPlayerEntity_v1_8_R3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Getter
public class SimpleNPCManager implements NPCManager {

    @Inject private Navigator navigator;
    @Inject private ArrayList<NPC> npcs;
    private String dataName = "CreatorNPC";

    @Override
    public @NotNull NPCPlayer createPlayerNPC(@NotNull Plugin plugin, @NotNull Location location, @NotNull String name, @NotNull SkinProperty skinProperty) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", new Property("textures", skinProperty.getValue(), skinProperty.getSignature()));

        try {
            NPCPlayer npcPlayer = new NPCPlayerEntity_v1_8_R3(plugin, this.navigator, location.getWorld(), profile, location);
            npcPlayer.getBukkitEntity().setMetadata(this.dataName, new FixedMetadataValue(plugin, true));
            this.npcs.add(npcPlayer);
            return npcPlayer;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public @NotNull Optional<NPC> getNPC(@NotNull String name) {
        return this.npcs.stream().filter(npc -> npc.getName().equals(name)).findFirst();
    }

    @Override
    public @NotNull Optional<NPC> getNPC(@NotNull Entity entity) {
        if (!this.isNpc(entity)) return Optional.empty();
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        if (nmsEntity instanceof NPC) return Optional.of((NPC) nmsEntity);
        if (nmsEntity instanceof NPCEntityNMS) return Optional.of(((NPCEntityNMS) nmsEntity).getNPC());

        return Optional.empty();
    }

    @Override
    public boolean isNpc(@NotNull Entity entity) {
        return entity.hasMetadata(this.dataName);
    }
}