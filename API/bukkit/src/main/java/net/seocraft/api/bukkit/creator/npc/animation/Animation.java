package net.seocraft.api.bukkit.creator.npc.animation;

import lombok.Getter;

@Getter
public enum Animation {

    SWING_ARM(0),
    TAKE_DAMAGE(1),
    LEAVE_BED(2),
    EAT_FOOD(3),
    CRITICAL_EFFECT(4),
    MAGIC_CRITICAL_EFFECT(5),
    CROUCH(104),
    UNCROUCH(105);

    private int id;

    Animation(int id) {
        this.id = id;
    }

}