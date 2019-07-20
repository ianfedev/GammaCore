package net.seocraft.commons.bukkit;

import com.google.inject.Inject;
import me.fixeddev.bcm.basic.NoOpPermissionMessageProvider;
import me.fixeddev.bcm.bukkit.BukkitCommandHandler;
import me.fixeddev.bcm.bukkit.CommandSenderAuthorizer;
import me.fixeddev.bcm.parametric.ParametricCommandHandler;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.commons.bukkit.old.authentication.*;
import net.seocraft.commons.bukkit.old.command.*;
import net.seocraft.commons.bukkit.old.friend.FriendshipHandler;
import net.seocraft.commons.bukkit.old.friend.FriendshipHandlerImpl;
import net.seocraft.commons.bukkit.old.listeners.DisabledPluginsCommandListener;
import net.seocraft.commons.bukkit.old.listeners.UserLogoutListener;
import net.seocraft.commons.bukkit.old.punishment.IPunishmentHandler;
import net.seocraft.commons.bukkit.old.punishment.PunishmentHandler;
import net.seocraft.commons.bukkit.old.user.UserAccessResponse;
import net.seocraft.commons.bukkit.old.user.UserChatListener;
import net.seocraft.commons.bukkit.old.whisper.WhisperManager;
import net.seocraft.commons.bukkit.old.whisper.WhisperManagerImpl;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommonsBukkit extends JavaPlugin {

    // --- Authentication mode related listeners //
    @Inject private AuthenticationEnvironmentEventsListener authenticationMovementListener;
    @Inject private AuthenticationLanguageMenuListener authenticationLanguageMenuListener;
    @Inject private AuthenticationLanguageSelectListener authenticationLanguageSelectListener;
    @Inject private AuthenticationCommandsListener authenticationCommandsListener;

    @Inject private DisabledPluginsCommandListener disabledPluginsCommandListener;
    @Inject private UserLogoutListener userLogoutListener;

    @Inject private UserChatListener userChatListener;
    @Inject private UserAccessResponse userAccessResponse;

    @Inject private LoginCommand loginCommand;
    @Inject private RegisterCommand registerCommand;

    @Inject private WhisperCommand whisperCommand;
    @Inject private PunishmentCommand punishmentCommand;
    @Inject private FriendCommand friendCommand;

    @Inject private CommandSenderAuthorizer commandSenderAuthorizer;

    public List<UUID> unregisteredPlayers = new ArrayList<>();
    public Map<UUID, Integer> loginAttempts = new HashMap<>();
    public ParametricCommandHandler parametricCommandHandler;

    @Override
    public void onEnable() {
        parametricCommandHandler = new ParametricCommandHandler(commandSenderAuthorizer, new NoOpPermissionMessageProvider(), getLogger());
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(getLogger(), new NoOpPermissionMessageProvider());
        loadConfig();

        dispatcher.registerCommandClass(whisperCommand);
        dispatcher.registerCommandClass(punishmentCommand);
        dispatcher.registerCommandClass(friendCommand);

        getServer().getPluginManager().registerEvents(userChatListener, this);
        getServer().getPluginManager().registerEvents(userAccessResponse, this);
        getServer().getPluginManager().registerEvents(disabledPluginsCommandListener, this);
        getServer().getPluginManager().registerEvents(userLogoutListener, this);


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
        binder.expose(FriendshipHandler.class);
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