package net.seocraft.lobby.menu;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.lobby.LobbyIcon;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LobbySelectorMenu {

    @Inject private TranslatableField translatableField;
    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private CommonsBukkit instance;

    private Inventory getLobbyMenu(String l, Set<LobbyIcon> lobbyIcons, String actualServer) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError {
        if (this.instance.getServerRecord().getGamemode() != null) {
            Gamemode lobbyGamemode = this.gamemodeProvider.getGamemodeSync(this.instance.getServerRecord().getGamemode());
            Map<ItemStack, Integer> itemMap = new HashMap<>();
            lobbyIcons.forEach(s -> {
                int n = s.getNumber(); if (n > 64) n = 64;
                ItemStack baseStack = null;
                if (s.getMaxPlayers() == s.getOnlinePlayers()) {
                    baseStack = new ItemStack(Material.STAINED_CLAY, n, (short) 0);

                } else if (s.getName().equalsIgnoreCase(actualServer)) {
                    baseStack = new ItemStack(Material.STAINED_CLAY, n, (short) 0);
                } else {
                    baseStack = new ItemStack(Material.STAINED_CLAY, n, (short) 0);
                    ItemMeta baseMeta = baseStack.getItemMeta();
                    baseMeta.setDisplayName(
                            this.translatableField.getUnspacedField(
                                    l,
                                    "commons_lobby_selector_title"
                            ).replace(
                                    "%%game%%",
                                    this.translatableField.getUnspacedField(
                                            l,
                                            "game_" + this.instance.getServerRecord().getGamemode() + "_title"
                                    )
                            )
                    );
                }

            });
        } else {
            throw new IllegalStateException("Called method when not a lobby.");
        }
    }
}
