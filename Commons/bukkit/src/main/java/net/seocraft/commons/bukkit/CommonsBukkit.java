package net.seocraft.commons.bukkit;

import com.google.inject.Inject;
import me.fixeddev.inject.ProtectedBinder;
import me.ggamer55.bcm.bukkit.BukkitCommandHandler;
import me.ggamer55.bcm.bukkit.CommandSenderAuthorizer;
import me.ggamer55.bcm.parametric.ParametricCommandHandler;
import net.seocraft.commons.bukkit.authentication.*;
import net.seocraft.commons.bukkit.command.*;
import net.seocraft.commons.bukkit.friend.FriendshipHandler;
import net.seocraft.commons.bukkit.friend.FriendshipHandlerImpl;
import net.seocraft.commons.bukkit.listeners.DisabledPluginsCommandListener;
import net.seocraft.commons.bukkit.punishment.IPunishmentHandler;
import net.seocraft.commons.bukkit.punishment.PunishmentHandler;
import net.seocraft.commons.bukkit.user.UserAccessResponse;
import net.seocraft.commons.bukkit.user.UserChatListener;
import net.seocraft.commons.bukkit.whisper.WhisperManager;
import net.seocraft.commons.bukkit.whisper.WhisperManagerImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommonsBukkit extends JavaPlugin {

    // --- Authentication mode related listeners //
    @Inject
    private AuthenticationEnvironmentEventsListener authenticationMovementListener;
    @Inject
    private AuthenticationLanguageMenuListener authenticationLanguageMenuListener;
    @Inject
    private AuthenticationLanguageSelectListener authenticationLanguageSelectListener;
    @Inject
    private AuthenticationCommandsListener authenticationCommandsListener;

    @Inject
    private DisabledPluginsCommandListener disabledPluginsCommandListener;

    @Inject
    private UserChatListener userChatListener;
    @Inject
    private UserAccessResponse userAccessResponse;

    @Inject
    private LoginCommand loginCommand;
    @Inject
    private RegisterCommand registerCommand;

    @Inject
    private WhisperCommand whisperCommand;
    @Inject
    private PunishmentCommand punishmentCommand;
    @Inject
    private FriendCommand friendCommand;

    @Inject
    private CommandSenderAuthorizer commandSenderAuthorizer;

    public List<UUID> unregisteredPlayers = new ArrayList<>();
    public Map<UUID, Integer> loginAttempts = new HashMap<>();
    public ParametricCommandHandler parametricCommandHandler;

    @Override
    public void onEnable() {
        parametricCommandHandler = new ParametricCommandHandler(commandSenderAuthorizer, getLogger());
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(getLogger());
        loadConfig();

        dispatcher.registerCommandClass(whisperCommand);
        dispatcher.registerCommandClass(punishmentCommand);
        dispatcher.registerCommandClass(friendCommand);

        getServer().getPluginManager().registerEvents(userChatListener, this);
        getServer().getPluginManager().registerEvents(userAccessResponse, this);
        getServer().getPluginManager().registerEvents(disabledPluginsCommandListener, this);

        if (getConfig().getBoolean("authentication.enabled", false)) {
            enableAuthentication();
        }
    }


    @Override
    public void configure(ProtectedBinder binder) {
        binder.bind(CommonsBukkit.class).toInstance(this);
        binder.bind(WhisperManager.class).to(WhisperManagerImpl.class);
        binder.bind(PunishmentHandler.class).to(IPunishmentHandler.class);
        binder.bind(FriendshipHandler.class).to(FriendshipHandlerImpl.class);
        binder.expose(CommonsBukkit.class);
        binder.expose(WhisperManager.class);
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void enableAuthentication() {
        parametricCommandHandler.registerCommand(loginCommand);
        parametricCommandHandler.registerCommand(registerCommand);

        getServer().getPluginManager().registerEvents(authenticationCommandsListener, this);
        getServer().getPluginManager().registerEvents(authenticationLanguageMenuListener, this);
        getServer().getPluginManager().registerEvents(authenticationLanguageSelectListener, this);
        getServer().getPluginManager().registerEvents(authenticationMovementListener, this);

    }

}