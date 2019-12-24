package net.seocraft.api.bukkit.creator.skin;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

@Getter
public class CraftSkinProperty implements SkinProperty {

    @NotNull private String signature;
    @NotNull private String value;

    @ConstructorProperties({"signature", "value"})
    public CraftSkinProperty(@NotNull String signature, @NotNull String value) {
        this.signature = signature;
        this.value = value;
    }
}