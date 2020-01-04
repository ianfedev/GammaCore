package net.seocraft.commons.bukkit.event;

import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomSelectionEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    @NotNull private Player player;
    @NotNull private Gamemode gamemode;
    @NotNull private SubGamemode subGamemode;
    private boolean perk;

    public CustomSelectionEvent(@NotNull Player player, @NotNull Gamemode gamemode, @NotNull SubGamemode subGamemode, boolean perk) {
        this.player = player;
        this.gamemode = gamemode;
        this.subGamemode = subGamemode;
        this.perk = perk;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Gamemode getGamemode() {
        return gamemode;
    }

    public @NotNull SubGamemode getSubGamemode() {
        return subGamemode;
    }

    public boolean isPerk() {
        return perk;
    }
}