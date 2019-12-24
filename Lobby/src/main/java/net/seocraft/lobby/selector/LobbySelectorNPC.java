package net.seocraft.lobby.selector;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.creator.npc.NPC;
import net.seocraft.creator.npc.NPCManager;
import net.seocraft.creator.skin.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class LobbySelectorNPC implements SelectorNPC {

    @NotNull private Gamemode gamemode;
    @Nullable private SubGamemode subGamemode;
    @NotNull private SkinProperty skin;
    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;
    private boolean perk;

    public LobbySelectorNPC(@NotNull Gamemode gamemode, @Nullable SubGamemode subGamemode, @NotNull SkinProperty skin, double x, double y, double z, double yaw, double pitch, boolean perk) {
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
            NPC npcPlayer = manager.createPlayerNPC(
                    plugin,
                    new Location(
                            world,
                            this.x,
                            this.y,
                            this.z,
                            (float) this.yaw,
                            (float) this.pitch
                    ),
                    name,
                    this.skin
            );
            npcPlayer.setFrozen(true);
            npcPlayer.setInvulnerable(true);
            Bukkit.getLogger().log(Level.INFO, "[Lobby] The NPC {0} was created successfully.", name);
            return npcPlayer;
        } else {
            Bukkit.getLogger().log(Level.WARNING, "[Lobby] The world {0} could not be found for NPC creation.", plugin.getConfig().getString("spawn.world"));
        }
        return null;
    }

    @Override
    public boolean isPerk() {
        return perk;
    }
}
