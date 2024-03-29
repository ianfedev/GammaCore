package net.seocraft.lobby.selector;

import net.seocraft.api.bukkit.creator.npc.NPC;
import net.seocraft.api.bukkit.creator.npc.NPCManager;
import net.seocraft.api.bukkit.creator.skin.SkinProperty;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class LobbySelectorNPC implements SelectorNPC {

    @NotNull private final Gamemode gamemode;
    @Nullable private final SubGamemode subGamemode;
    @NotNull private final SkinProperty skin;
    private final double x;
    private final double y;
    private final double z;
    private final double yaw;
    private final double pitch;
    private final boolean perk;

    public LobbySelectorNPC(@NotNull Gamemode gamemode, @Nullable SubGamemode subGamemode,
                            @NotNull SkinProperty skin, double x, double y, double z, double yaw,
                            double pitch, boolean perk) {
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.skin = skin;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.perk = perk;
    }

    public @NotNull Gamemode getGamemode() {
        return this.gamemode;
    }

    public @Nullable SubGamemode getSubGamemode() {
        return this.subGamemode;
    }

    public @NotNull SkinProperty getSkin() {
        return this.skin;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public double getYaw() {
        return this.yaw;
    }

    public double getPitch() {
        return this.pitch;
    }

    @Override
    public NPC create(@NotNull Plugin plugin, @NotNull String name, @NotNull NPCManager manager) {

        World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.world"));
        if (world != null) {

            Location location = new Location(
                    world,
                    this.x,
                    this.y,
                    this.z,
                    (float) this.yaw,
                    (float) this.pitch
            );

            NPC npcPlayer = manager.createPlayerNPC(
                    plugin,
                    location,
                    " ",
                    this.skin
            );
            npcPlayer.setFrozen(true);
            npcPlayer.setInvulnerable(true);
            Bukkit.getLogger().log(Level.INFO, "[Lobby] NPC {0} created successfully.", name);
            return npcPlayer;
        } else {
            Bukkit.getLogger().log(Level.WARNING, "[Lobby] Error creating NPC {0}.", name);
        }
        return null;
    }

    @Override
    public boolean isPerk() {
        return perk;
    }
}
