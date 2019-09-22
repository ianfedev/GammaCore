package net.seocraft.commons.bukkit.game.map.partial;

import net.seocraft.api.bukkit.game.map.partial.MapCoordinates;

import java.beans.ConstructorProperties;

public class PartialCoordinates implements MapCoordinates {

    private float x;
    private float y;
    private float z;

    @ConstructorProperties({"x", "y", "z"})
    public PartialCoordinates(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public float getZ() {
        return this.z;
    }
}
