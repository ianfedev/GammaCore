package net.seocraft.lobby;

import com.google.inject.Inject;
import com.google.inject.Scopes;
import me.fixeddev.ebcm.CommandManager;
import me.fixeddev.ebcm.SimpleCommandManager;
import me.fixeddev.ebcm.bukkit.BukkitAuthorizer;
import me.fixeddev.ebcm.bukkit.BukkitCommandManager;
import me.fixeddev.ebcm.bukkit.BukkitMessager;
import me.fixeddev.ebcm.bukkit.parameter.provider.BukkitModule;
import me.fixeddev.ebcm.parameter.provider.ParameterProviderRegistry;
import me.fixeddev.ebcm.parametric.ParametricCommandBuilder;
import me.fixeddev.ebcm.parametric.ReflectionParametricCommandBuilder;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.lobby.HidingGadgetManager;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorHologramManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorHologramUpdater;
import net.seocraft.api.bukkit.lobby.selector.SelectorManager;
import net.seocraft.api.bukkit.lobby.selector.SelectorNPC;
import net.seocraft.api.bukkit.profile.ProfileManager;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.lobby.command.HidingGadgetCommand;
import net.seocraft.lobby.command.TeleportCommand;
import net.seocraft.lobby.hiding.HidingGadgetListener;
import net.seocraft.lobby.hiding.LobbyHidingGadget;
import net.seocraft.lobby.hotbar.GameSelectorListener;
import net.seocraft.lobby.hotbar.HotbarListener;
import net.seocraft.lobby.listener.*;
import net.seocraft.lobby.profile.GammaProfileManager;
import net.seocraft.lobby.profile.listener.FriendsMenuListener;
import net.seocraft.lobby.profile.listener.ProfileMenuListener;
import net.seocraft.lobby.selector.LobbySelectorHologramManager;
import net.seocraft.lobby.selector.LobbySelectorHologramUpdater;
import net.seocraft.lobby.selector.LobbySelectorListener;
import net.seocraft.lobby.selector.LobbySelectorManager;
import net.seocraft.lobby.teleport.LobbyTeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class Lobby extends JavaPlugin {

    @NotNull private final Set<SelectorNPC> lobbyNPC = new HashSet<>();
    @NotNull private final Map<Player, Integer> lobbyMenuClose = new HashMap<>();

    private CommandManager commandManager;

    @Inject private CommonsBukkit instance;

    @Inject private HidingGadgetCommand hidingGadgetCommand;
    @Inject private SelectorManager selectorManager;
    @Inject private SelectorHologramUpdater hologramUpdater;
    @Inject private TeleportCommand teleportCommand;

    @Inject private InventoryCloseListener inventoryCloseListener;
    @Inject private GameSelectorListener gameSelectorListener;
    @Inject private HidingGadgetListener hidingGadgetListener;
    @Inject private PlayerBlockInteractionListener playerBlockInteractionListener;
    @Inject private HotbarListener hotbarListener;
    @Inject private LobbyConnectionListener lobbyConnectionListener;
    @Inject private LobbySelectorListener lobbySelectorListener;
    @Inject private PlayerDamageListener playerDamageListener;
    @Inject private InventoryInteractionListener inventoryInteractionEvent;
    @Inject private InventoryDropListener inventoryDropEvent;

    // --- Profile Listeners --- //
    @Inject private ProfileMenuListener profileMenuListener;
    @Inject private FriendsMenuListener friendsMenuListener;

    @Override
    public void onEnable() {

        if (this.instance.getServerRecord().getServerType() != ServerType.LOBBY) {
            Bukkit.getLogger().log(Level.SEVERE, "[Lobby] Server type was not set to LOBBY.");
            Bukkit.shutdown();
        }

        saveDefaultConfig();
        setupLobbyWorld();

        ParameterProviderRegistry registry = ParameterProviderRegistry.createRegistry();
        registry.installModule(new BukkitModule());

        CommandManager commandManager = new SimpleCommandManager(new BukkitAuthorizer(),
                new BukkitMessager(),
                registry);
        this.commandManager = new BukkitCommandManager(commandManager, this.getName());

        ParametricCommandBuilder commandBuilder = new ReflectionParametricCommandBuilder();

        commandManager.registerCommands(commandBuilder.fromClass(this.hidingGadgetCommand));
        commandManager.registerCommands(commandBuilder.fromClass(this.teleportCommand));

        getServer().getPluginManager().registerEvents(this.inventoryCloseListener, this);
        getServer().getPluginManager().registerEvents(this.gameSelectorListener, this);
        getServer().getPluginManager().registerEvents(this.lobbyConnectionListener, this);
        getServer().getPluginManager().registerEvents(this.hidingGadgetListener, this);
        getServer().getPluginManager().registerEvents(this.lobbySelectorListener, this);
        getServer().getPluginManager().registerEvents(this.playerBlockInteractionListener, this);
        getServer().getPluginManager().registerEvents(this.inventoryInteractionEvent, this);
        getServer().getPluginManager().registerEvents(this.hotbarListener, this);
        getServer().getPluginManager().registerEvents(this.playerDamageListener, this);
        getServer().getPluginManager().registerEvents(this.inventoryDropEvent, this);

        getServer().getPluginManager().registerEvents(this.profileMenuListener, this);
        getServer().getPluginManager().registerEvents(this.friendsMenuListener, this);

        this.selectorManager.setupSelectorNPC();
        this.hologramUpdater.scheduleUpdater();
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.bind(HidingGadgetManager.class).to(LobbyHidingGadget.class);
        binder.bind(SelectorManager.class).to(LobbySelectorManager.class);
        binder.bind(SelectorHologramUpdater.class).to(LobbySelectorHologramUpdater.class).in(Scopes.SINGLETON);
        binder.bind(SelectorHologramManager.class).to(LobbySelectorHologramManager.class);
        binder.bind(TeleportManager.class).to(LobbyTeleportManager.class);
        binder.bind(ProfileManager.class).to(GammaProfileManager.class).in(Scopes.SINGLETON);
        binder.bind(Lobby.class).toInstance(this);
    }

    public @NotNull Set<SelectorNPC> getLobbyNPC() {
        return lobbyNPC;
    }

    private void setupLobbyWorld() {
        World world = Bukkit.getWorld(getConfig().getString("spawn.world"));
        if (world != null) {
            world.setGameRuleValue("doWeatherCycle", "false");
            world.setStorm(false);
        }
    }

    public @NotNull Map<Player, Integer> getLobbyMenuClose() {
        return lobbyMenuClose;
    }
}
