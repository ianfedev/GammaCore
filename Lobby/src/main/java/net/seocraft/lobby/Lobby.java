package net.seocraft.lobby;

import com.google.inject.Inject;
import me.fixeddev.inject.ProtectedBinder;
import me.ggamer55.bcm.bukkit.BukkitCommandHandler;
import net.seocraft.lobby.command.HidingGadgetCommand;
import net.seocraft.lobby.command.TeleportCommand;
import net.seocraft.lobby.hiding.HidingGadgetHandler;
import net.seocraft.lobby.hiding.HidingGadgetHandlerImp;
import net.seocraft.lobby.listener.HidingGadgetListener;
import net.seocraft.lobby.listener.InventoryDropListener;
import net.seocraft.lobby.listener.InventoryInteractionListener;
import net.seocraft.lobby.listener.PlayerDeathListener;
import net.seocraft.lobby.management.CooldownManager;
import net.seocraft.lobby.management.CooldownManagerImp;
import net.seocraft.lobby.player.LobbyConnectionListener;
import net.seocraft.lobby.teleport.TeleportHandler;
import net.seocraft.lobby.teleport.TeleportHandlerImp;
import org.bukkit.plugin.java.JavaPlugin;

public class Lobby extends JavaPlugin {

    @Inject private HidingGadgetCommand hidingGadgetCommand;
    @Inject private TeleportCommand teleportCommand;

    @Inject private HidingGadgetListener hidingGadgetListener;
    @Inject private LobbyConnectionListener lobbyConnectionListener;
    @Inject private PlayerDeathListener playerDeathListener;
    @Inject private InventoryInteractionListener inventoryInteractionEvent;
    @Inject private InventoryDropListener inventoryDropEvent;

    @Override
    public void onEnable() {
        loadConfig();
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(this.getLogger());

        dispatcher.registerCommandClass(this.hidingGadgetCommand);
        dispatcher.registerCommandClass(this.teleportCommand);

        getServer().getPluginManager().registerEvents(this.lobbyConnectionListener, this);
        getServer().getPluginManager().registerEvents(this.hidingGadgetListener, this);
        getServer().getPluginManager().registerEvents(this.inventoryInteractionEvent, this);
        getServer().getPluginManager().registerEvents(this.playerDeathListener, this);
        getServer().getPluginManager().registerEvents(this.inventoryDropEvent, this);
    }

    //@Override
    public void configure(ProtectedBinder binder) {
        binder.bind(HidingGadgetHandler.class).to(HidingGadgetHandlerImp.class);
        binder.bind(CooldownManager.class).to(CooldownManagerImp.class);
        binder.bind(TeleportHandler.class).to(TeleportHandlerImp.class);
        binder.bind(Lobby.class).toInstance(this);
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}