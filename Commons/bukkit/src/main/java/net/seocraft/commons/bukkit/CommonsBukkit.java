package net.seocraft.commons.bukkit;

import com.google.inject.Inject;
import me.fixeddev.bcm.basic.NoOpPermissionMessageProvider;
import me.fixeddev.bcm.bukkit.BukkitCommandHandler;
import me.fixeddev.bcm.bukkit.CommandSenderAuthorizer;
import me.fixeddev.bcm.parametric.ParametricCommandHandler;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.commons.bukkit.authentication.AuthenticationCommandsListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationEnvironmentEventsListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationLanguageMenuListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationLanguageSelectListener;
import net.seocraft.commons.bukkit.command.*;
import net.seocraft.commons.bukkit.game.GameModule;
import net.seocraft.commons.bukkit.listeners.DisabledPluginsCommandListener;
import net.seocraft.commons.bukkit.listeners.UserLogoutListener;
import net.seocraft.commons.bukkit.punishment.PunishmentModule;
import net.seocraft.commons.bukkit.user.UserAccessResponse;
import net.seocraft.commons.bukkit.user.UserChatListener;
import net.seocraft.commons.bukkit.user.UserModule;
import net.seocraft.commons.core.CoreModule;
import net.seocraft.commons.core.server.ServerModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

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
    @Inject private ServerLoad serverLoad;

    public List<UUID> unregisteredPlayers = new ArrayList<>();
    public Map<UUID, Integer> loginAttempts = new HashMap<>();
    public ParametricCommandHandler parametricCommandHandler;
    public Server serverRecord;

    @Override
    public void onEnable() {
        parametricCommandHandler = new ParametricCommandHandler(commandSenderAuthorizer, new NoOpPermissionMessageProvider(), getLogger());
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(getLogger(), new NoOpPermissionMessageProvider());
        loadConfig();

        try {
            this.serverRecord = this.serverLoad.setupServer();
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
            Bukkit.getLogger().log(Level.SEVERE, "[Bukkit-API] Error when authorizing server load.");
            Bukkit.shutdown();
        }

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
        binder.install(new CoreModule());
        binder.install(new GameModule());
        binder.install(new PunishmentModule());
        binder.install(new ServerModule());
        binder.install(new UserModule());
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

    public Server getServerRecord() {
        return this.serverRecord;
    }

}