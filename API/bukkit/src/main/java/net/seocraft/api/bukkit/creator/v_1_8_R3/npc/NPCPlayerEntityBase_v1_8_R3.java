package net.seocraft.api.bukkit.creator.v_1_8_R3.npc;

import com.mojang.authlib.GameProfile;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.server.v1_8_R3.*;
import net.seocraft.api.bukkit.creator.npc.animation.Animation;
import net.seocraft.api.bukkit.creator.npc.entity.player.NPCPlayer;
import net.seocraft.api.bukkit.creator.npc.navigation.Navigator;
import net.seocraft.api.bukkit.creator.skin.SkinLayer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

public class NPCPlayerEntityBase_v1_8_R3 extends NPCEntityBase_v1_8_R3 implements NPCPlayer {

    private Plugin plugin;
    protected GameProfile profile;
    private SkinLayer[] skinLayers;

    private boolean shownInList = false;
    private int ping = 1;
    private boolean lying = false;

    public NPCPlayerEntityBase_v1_8_R3(@NotNull Plugin plugin, @NotNull Navigator navigator, @NotNull World world, @NotNull GameProfile gameProfile) {
        super(navigator, world);
        this.plugin = plugin;
        this.profile = gameProfile;
        this.name = gameProfile.getName();
        this.interactManager = new PlayerInteractManager(((CraftWorld) world).getHandle());
        this.skinLayers = SkinLayer.values();
    }

    @Override
    protected void initialize(@NotNull World world, @NotNull Location location) throws Exception {
        ((EntityPlayer) this.entity).playerInteractManager.setGameMode(WorldSettings.EnumGamemode.CREATIVE);

        NetworkManager networkManager = new NetworkManager(EnumProtocolDirection.CLIENTBOUND);
        networkManager.channel = new NpcChannel_v1_8_R3();
        networkManager.l = new SocketAddress() {
            private static final long serialVersionUID = 1108301788933825435L;
        };

        PlayerConnection playerConnection = new PlayerConnection((MinecraftServer) this.minecraftServer, networkManager, ((EntityPlayer) this.entity));
        networkManager.a(playerConnection);

        ((EntityPlayer) this.entity).playerConnection = playerConnection;

        ((EntityHuman) this.entity).fauxSleeping = true;

        PathfinderNormal pathfinderNormal = new PathfinderNormal();
        pathfinderNormal.a(true);
        this.pathFinder = new Pathfinder(pathfinderNormal);

        ((Entity) this.entity).setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        Bukkit.getOnlinePlayers().forEach(this::updatePlayerList);

        ((CraftWorld) world).getHandle().addEntity((Entity) this.entity);

        ((CraftWorld) world).getHandle().players.remove((EntityHuman) this.entity);

        this.setSkinLayers(this.skinLayers);
    }

    @Override
    public @NotNull Player getBukkitEntity() {
        return (Player) super.getBukkitEntity();
    }

    @Override
    public @NotNull GameMode getGamemode() {
        return GameMode.values()[((EntityPlayer) this.entity).playerInteractManager.getGameMode().ordinal()];
    }

    @Override
    public @NotNull SkinLayer[] getActiveSkinLayers() {
        return this.skinLayers;
    }

    @Override
    public int getPing() {
        return this.ping;
    }

    @Override
    public boolean isLying() {
        return this.lying;
    }

    @Override
    public boolean isShownInList() {
        return this.shownInList;
    }

    @Override
    public void despawn() {
        if (this.getBukkitEntity().isDead()) return;
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            WorldServer worldServer = ((CraftWorld) getBukkitEntity().getWorld()).getHandle();
            worldServer.kill((Entity) entity);
            Bukkit.getOnlinePlayers().forEach(this::updatePlayerList);
        });
        super.despawn();
    }

    @Override
    public void setGamemode(@NotNull GameMode gamemode) {
        ((EntityPlayer) this.entity).playerInteractManager.setGameMode(WorldSettings.EnumGamemode.values()[gamemode.ordinal()]);
    }

    @Override
    public void setSkinLayers(@NotNull SkinLayer... layers) throws NoSuchFieldException, IllegalAccessException {
        this.skinLayers = layers;

        int i = 0;
        for (SkinLayer layer : layers) {
            i |= layer.getShifted();
        }

        Field dataValuesField = ((CraftEntity) this.getBukkitEntity()).getHandle().getDataWatcher().getClass().getDeclaredField("dataValues");
        dataValuesField.setAccessible(true);
        TIntObjectHashMap dataValues = (TIntObjectHashMap) dataValuesField.get(((CraftEntity) this.getBukkitEntity()).getHandle().getDataWatcher());
        dataValues.put(10, new DataWatcher.WatchableObject(0, 10, (byte) i));
    }

    @Override
    public void setPing(int ping) {
        if (this.ping == ping) return;
        this.ping = ping;
        Bukkit.getOnlinePlayers().forEach(this::updatePlayerList);
    }

    @Override
    public void teleport(@NotNull Location location) {
        this.getBukkitEntity().teleport(location);
        this.setYaw(location.getYaw());
        this.setPitch(location.getPitch());
    }

    @Override
    public void setLying(boolean lying) {
        if (this.lying == lying) return;
        if (lying) {
            this.setYaw(0);
            this.setPing(0);
            this.getLocation().getWorld().getPlayers().forEach(this::sendBedPacket);
            this.lying = true;
        } else {
            this.playAnimation(Animation.LEAVE_BED);
            this.lying = false;
        }
    }

    @Override
    public void setShownInList(boolean shownInList) {
        if (this.shownInList == shownInList) return;
        this.shownInList = shownInList;
        Bukkit.getOnlinePlayers().forEach(this::updatePlayerList);
    }

    @Override
    public @NotNull UUID getUUID() {
        return this.profile.getId();
    }

    @Override
    public void updateToPlayer(@NotNull Player player) {
        this.updatePlayerList(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isLying()) {
                    sendBedPacket(player);
                }
            }
        }.runTaskLater(this.plugin, 10L);
    }



    private void updatePlayerList(@NotNull Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(this.buildPlayerInfoPacket(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                if (!isShownInList() || getBukkitEntity().isDead()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(buildPlayerInfoPacket(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER));
                }
            }
        }.runTaskLater(this.plugin, 10L);

    }

    private void sendBedPacket(@NotNull Player player) {
        Location bedLocation = this.getLocation();
        bedLocation.setY(1);
        player.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte) 0);

        PacketPlayOutBed bedPacket = new PacketPlayOutBed();

        try {
            Field entityIdField = bedPacket.getClass().getDeclaredField("a");
            entityIdField.setAccessible(true);
            entityIdField.set(bedPacket, this.getEntityId());

            Field blockPositionField = bedPacket.getClass().getDeclaredField("b");
            blockPositionField.setAccessible(true);
            blockPositionField.set(bedPacket, new BlockPosition(bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ()));
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bedPacket);

        double height = 0.25;
        this.getBukkitEntity().teleport(new Location(
                this.getLocation().getWorld(),
                bedLocation.getBlockX(),
                this.getLocation().getBlockY(),
                this.getLocation().getBlockZ(),
                this.getBukkitEntity().getLocation().getYaw(),
                this.getBukkitEntity().getLocation().getPitch())
        );

        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(
                this.getEntityId(),
                MathHelper.floor(bedLocation.getBlockX() * 32.0D),
                MathHelper.floor((this.getLocation().getBlockY() + height) * 32.0D),
                MathHelper.floor(this.getLocation().getBlockZ() * 32.0D),
                (byte) (int) (this.getBukkitEntity().getLocation().getYaw() * 256F / 360F),
                (byte) (int) (this.getBukkitEntity().getLocation().getPitch() * 256F / 360F),
                true
        );

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(teleportPacket);
    }

    private PacketPlayOutPlayerInfo buildPlayerInfoPacket(PacketPlayOutPlayerInfo.EnumPlayerInfoAction enumPlayerInfoAction) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();

        try {
            Field enumField = packet.getClass().getDeclaredField("a");
            enumField.setAccessible(true);
            enumField.set(packet, enumPlayerInfoAction);

            Field dataField = packet.getClass().getDeclaredField("b");
            dataField.setAccessible(true);
            List dataList = (List) dataField.get(packet);

            PacketPlayOutPlayerInfo.PlayerInfoData playerInfoData = packet.new PlayerInfoData(
                    this.profile,
                    ping,
                    WorldSettings.EnumGamemode.values()[this.getGamemode().ordinal()],
                    CraftChatMessage.fromString(this.getName())[0]
            );

            dataList.add(playerInfoData);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return packet;
    }
}