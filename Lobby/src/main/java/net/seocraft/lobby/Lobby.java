package net.seocraft.lobby;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import me.fixeddev.bcm.basic.NoOpPermissionMessageProvider;
import me.fixeddev.bcm.bukkit.BukkitCommandHandler;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.friend.UserFriendshipProvider;
import net.seocraft.commons.bukkit.game.GameModule;
import net.seocraft.commons.bukkit.serializer.InterfaceDeserializer;
import net.seocraft.commons.bukkit.server.ServerModule;
import net.seocraft.commons.bukkit.user.UserModule;
import net.seocraft.commons.core.CoreModule;
import net.seocraft.lobby.command.HidingGadgetCommand;
import net.seocraft.lobby.command.TeleportCommand;
import net.seocraft.api.bukkit.lobby.HidingGadgetManager;
import net.seocraft.lobby.hiding.LobbyHidingGadget;
import net.seocraft.lobby.listener.*;
import net.seocraft.api.core.cooldown.CooldownManager;
import net.seocraft.commons.core.cooldown.CoreCooldownManager;
import net.seocraft.api.bukkit.lobby.TeleportManager;
import net.seocraft.lobby.teleport.LobbyTeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Lobby extends JavaPlugin {

    @Inject private CommonsBukkit instance;

    @Inject private HidingGadgetCommand hidingGadgetCommand;
    @Inject private TeleportCommand teleportCommand;

    @Inject private HidingGadgetListener hidingGadgetListener;
    @Inject private PlayerBlockInteractionListener playerBlockInteractionListener;
    @Inject private GameMenuListener gameMenuListener;
    @Inject private LobbyConnectionListener lobbyConnectionListener;
    @Inject private PlayerDeathListener playerDeathListener;
    @Inject private InventoryInteractionListener inventoryInteractionEvent;
    @Inject private InventoryDropListener inventoryDropEvent;

    @Override
    public void onEnable() {

        if (this.instance.getServerRecord().getServerType() != ServerType.LOBBY) {
            Bukkit.getLogger().log(Level.SEVERE, "[Lobby] Server type was not set to LOBBY.");
            Bukkit.shutdown();
        }

        loadConfig();
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(this.getLogger(), new NoOpPermissionMessageProvider());

        dispatcher.registerCommandClass(this.hidingGadgetCommand);
        dispatcher.registerCommandClass(this.teleportCommand);

        getServer().getPluginManager().registerEvents(this.lobbyConnectionListener, this);
        getServer().getPluginManager().registerEvents(this.hidingGadgetListener, this);
        getServer().getPluginManager().registerEvents(this.playerBlockInteractionListener, this);
        getServer().getPluginManager().registerEvents(this.inventoryInteractionEvent, this);
        getServer().getPluginManager().registerEvents(this.gameMenuListener, this);
        getServer().getPluginManager().registerEvents(this.playerDeathListener, this);
        getServer().getPluginManager().registerEvents(this.inventoryDropEvent, this);
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new CoreModule());
        binder.install(new UserModule());
        binder.install(new ServerModule());
        binder.install(new GameModule());
        binder.bind(FriendshipProvider.class).to(UserFriendshipProvider.class);
        binder.bind(HidingGadgetManager.class).to(LobbyHidingGadget.class);
        binder.bind(ObjectMapper.class).toProvider(() -> {
            ObjectMapper mapper = new ObjectMapper().registerModule(InterfaceDeserializer.getAbstractTypes());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper;
        }).in(Scopes.SINGLETON);
        binder.bind(TeleportManager.class).to(LobbyTeleportManager.class);
        binder.bind(Lobby.class).toInstance(this);
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
