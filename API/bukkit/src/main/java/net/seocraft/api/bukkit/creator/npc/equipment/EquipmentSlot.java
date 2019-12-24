package net.seocraft.api.bukkit.creator.npc.equipment;

import lombok.Getter;

@Getter
public enum EquipmentSlot {

    HAND(0),
    FEET(1),
    LEGS(2),
    CHEST(3),
    HEAD(4);

    private int id;

    EquipmentSlot(int id) {
        this.id = id;
    }

}