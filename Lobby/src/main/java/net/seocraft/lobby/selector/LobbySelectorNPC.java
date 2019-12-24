package net.seocraft.lobby.selector;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.creator.npc.NPC;
import net.seocraft.creator.npc.NPCManager;
import net.seocraft.creator.npc.action.ClickType;
import net.seocraft.creator.npc.event.NPCInteractEvent;
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
    private float x;
    private float y;
    private float z;
    private float yaw;
    private float pitch;

    public LobbySelectorNPC(@NotNull Gamemode gamemode, @Nullable SubGamemode subGamemode, @NotNull SkinProperty skin, float x, float y, float z, float yaw, float pitch) {
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.skin = skin;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void create(@NotNull Plugin plugin, @NotNull String name, @NotNull NPCManager manager) {

        World world = Bukkit.getWorld(plugin.getConfig().getString("spawn.world"));

        if (world != null) {
            NPC npcPlayer = manager.createPlayerNPC(
                    plugin,
                    new Location(
                            world,
                            this.x,
                            this.y,
                            this.z,
                            this.yaw,
                            this.pitch
                    ),
                    name,
                    this.skin
            ).addActionHandler((npc, npcEvent) -> {
                if (npcEvent instanceof NPCInteractEvent) {
                    NPCInteractEvent interactEvent = (NPCInteractEvent) npcEvent;
                    if (interactEvent.getClickType() == ClickType.RIGHT_CLICK) {
                        npc.setPassenger(interactEvent.getPlayer());
                    }
                }
            });
            npcPlayer.setFrozen(false);
            npcPlayer.setControllable(true);
            Bukkit.getLogger().log(Level.INFO, "[Lobby] The NPC {0} was created successfully.", name);
        } else {
            Bukkit.getLogger().log(Level.WARNING, "[Lobby] The world {0} could not be found for NPC creation.", plugin.getConfig().getString("spawn.world"));
        }
    }
}
