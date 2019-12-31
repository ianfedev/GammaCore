package net.seocraft.lobby.selector;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.lobby.LobbyIcon;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.storage.Pagination;
import net.seocraft.api.core.utils.StringUtils;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.minecraft.NBTTagHandler;
import net.seocraft.commons.bukkit.util.HeadLibrary;
import net.seocraft.commons.bukkit.util.InventoryUtils;
import net.seocraft.commons.core.model.GammaPagination;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class LobbySelectorMenu {

    @Inject private TranslatableField translatableField;
    @Inject private ListeningExecutorService listeningExecutorService;
    @Inject private CommonsBukkit instance;

    public ListenableFuture<AsyncResponse<Inventory>> getLobbyMenu(String l, Set<LobbyIcon> lobbyIcons, String actualServer, int page) {
        return this.listeningExecutorService.submit(() -> new AsyncResponse<>(null, AsyncResponse.Status.SUCCESS, getLobbyMenuSync(l, lobbyIcons, actualServer, page)));
    }

    public Inventory getLobbyMenuSync(String l, Set<LobbyIcon> lobbyIcons, String actualServer, int page) {
        if (this.instance.getServerRecord().getGamemode() != null && this.instance.getServerRecord().getServerType() == ServerType.LOBBY) {
            Map<Integer, ItemStack> menuStack = new HashMap<>();
            List<ItemStack> iconList = lobbyIcons.stream().sorted(Comparator.comparingInt(LobbyIcon::getNumber)).map(s -> {
                int n = s.getNumber(); if (n > 64) n = 64;
                ItemStack baseStack;
                if (s.getMaxPlayers() == s.getOnlinePlayers()) {
                    baseStack = NBTTagHandler.addString(new ItemStack(Material.STAINED_CLAY, n, (short) 14), "lobby_selector_opt", "FULL");
                    ItemMeta baseMeta = baseStack.getItemMeta();
                    baseMeta.setDisplayName(
                            ChatColor.RED +
                                    this.translatableField.getUnspacedField(
                                            l,
                                            "commons_lobby_selector_title"
                                    ).replace(
                                            "%%number%%",
                                            "" + s.getNumber()
                                    )
                    );
                    List<String> baseLore = new ArrayList<>();
                    baseLore.add(
                            ChatColor.GRAY +
                                    StringUtils.capitalizeString(
                                            this.translatableField.getUnspacedField(
                                                    l,
                                                    "commons_players"
                                            )
                                    ) + ": " + s.getOnlinePlayers() + "/" + s.getMaxPlayers()
                    );
                    baseLore.add("");
                    baseLore.add(
                            ChatColor.RED + this.translatableField.getUnspacedField(l, "commons_lobby_selector_full")
                    );

                    baseMeta.setLore(baseLore);
                    baseStack.setItemMeta(baseMeta);
                } else if (s.getName().equalsIgnoreCase(actualServer)) {
                    baseStack = NBTTagHandler.addString(new ItemStack(Material.STAINED_CLAY, n, (short) 13), "lobby_selector_opt", "ACTUAL");
                    ItemMeta baseMeta = baseStack.getItemMeta();
                    baseMeta.setDisplayName(
                            ChatColor.GREEN + "" + ChatColor.BOLD +
                                    this.translatableField.getUnspacedField(
                                            l,
                                            "commons_lobby_selector_title"
                                    ).replace(
                                            "%%number%%",
                                            "" + s.getNumber()
                                    )
                    );
                    List<String> baseLore = new ArrayList<>();
                    baseLore.add(
                            ChatColor.GRAY +
                                    StringUtils.capitalizeString(
                                            this.translatableField.getUnspacedField(
                                                    l,
                                                    "commons_players"
                                            )
                                    ) + ": " + s.getOnlinePlayers() + "/" + s.getMaxPlayers()
                    );
                    baseLore.add("");
                    baseLore.add(
                            ChatColor.GREEN + this.translatableField.getUnspacedField(l, "commons_lobby_selector_already")
                    );

                    baseMeta.setLore(baseLore);
                    baseStack.setItemMeta(baseMeta);
                } else {
                    baseStack = NBTTagHandler.addString(new ItemStack(Material.QUARTZ_BLOCK, n), "lobby_selector_opt", s.getName());
                    ItemMeta baseMeta = baseStack.getItemMeta();
                    baseMeta.setDisplayName(
                            ChatColor.GREEN +
                                    this.translatableField.getUnspacedField(
                                            l,
                                            "commons_lobby_selector_title"
                                    ).replace(
                                            "%%number%%",
                                            "" + s.getNumber()
                                    )
                    );
                    List<String> baseLore = new ArrayList<>();
                    baseLore.add(
                            ChatColor.GRAY +
                                    StringUtils.capitalizeString(
                                            this.translatableField.getUnspacedField(
                                                    l,
                                                    "commons_players"
                                            )
                                    ) + ": " + s.getOnlinePlayers() + "/" + s.getMaxPlayers()
                    );
                    baseLore.add("");
                    baseLore.add(
                            ChatColor.YELLOW + this.translatableField.getUnspacedField(l, "commons_lobby_selector_connect")
                    );

                    baseMeta.setLore(baseLore);
                    baseStack.setItemMeta(baseMeta);
                }
                return baseStack;
            }).collect(Collectors.toList());
            Pagination<ItemStack> pagination = new GammaPagination<>(21, iconList);

            int externalCount = 0;
            for (int i = 10; i < iconList.size() + 10; i++) {
                if (i != 17 && i != 18 && i != 26 && i != 27) {
                    menuStack.put(i, pagination.getPage(page).get(externalCount));
                    externalCount++;
                }
            }

            if (page != 1) {
                ItemStack leftArrow = NBTTagHandler.addString(
                        HeadLibrary.leftArrowBlack(),
                        "lobby_page",
                        "" + (page - 1)
                );
                ItemMeta arrowMeta = leftArrow.getItemMeta();
                arrowMeta.setDisplayName(ChatColor.GREEN + "\u00AB " +
                        this.translatableField.getUnspacedField(l, "commons_pagination_previous").replace("%%page%%", "" + (page - 1))
                );
                leftArrow.setItemMeta(arrowMeta);
                menuStack.put(47, leftArrow);
            }

            ItemStack closeButton = NBTTagHandler.addString(
                    new ItemStack(Material.ARROW, 1),
                    "lobby_selector_opt",
                    "CLOSE"
            );
            ItemMeta closeMeta = closeButton.getItemMeta();
            closeMeta.setDisplayName(ChatColor.RED + this.translatableField.getUnspacedField(l, "commons_close"));
            closeButton.setItemMeta(closeMeta);
            menuStack.put(49, closeButton);

            if (pagination.totalPages() != 1 && page != pagination.totalPages()) {
                ItemStack rightArrow = NBTTagHandler.addString(
                        HeadLibrary.rightArrowBlack(),
                        "lobby_page",
                        "" + (page + 1)
                );
                ItemMeta arrowMeta = rightArrow.getItemMeta();
                arrowMeta.setDisplayName(ChatColor.GREEN +
                        this.translatableField.getUnspacedField(l, "commons_pagination_next").replace("%%page%%", "" + (page + 1))  + " \u00BB"
                );
                rightArrow.setItemMeta(arrowMeta);
                menuStack.put(51, rightArrow);
            }

            return InventoryUtils.createInventory(
                    this.translatableField.getUnspacedField(l, "commons_lobby_selector"),
                    54,
                    menuStack
            );

        } else {
            throw new IllegalStateException("Called method when not a lobby.");
        }
    }

}
