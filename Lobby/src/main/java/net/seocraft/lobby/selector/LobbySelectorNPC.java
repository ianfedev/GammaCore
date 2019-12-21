package net.seocraft.lobby.selector;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import org.jetbrains.annotations.NotNull;

public class LobbySelectorNPC implements SelectorNPC {

    @NotNull private Gamemode gamemode;
    @NotNull private SubGamemode subGamemode;
    @NotNull private String skin;
    private float x;
    private float y;
    private float z;
    private float yaw;
    private float pitch;

    public LobbySelectorNPC(@NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode, @NotNull String skin, float x, float y, float z, float yaw, float pitch) {
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

    public @NotNull SubGamemode getSubGamemode() {
        return this.subGamemode;
    }

    public @NotNull String getSkin() {
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
}
