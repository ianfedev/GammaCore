package net.seocraft.api.bukkit.creator.skin;

import lombok.Getter;

@Getter
public enum SkinLayer {

    CAPE(0),
    JACKET(1),
    LEFT_SLEEVE(2),
    RIGHT_SLEEVE(3),
    LEFT_PANTS(4),
    RIGHT_PANTS(5),
    HAT(6);

    private final int id;
    private final int shifted;

    SkinLayer(int id) {
        this.id = id;
        this.shifted = 1 << id;
    }
}