package net.seocraft.commons.bukkit;

import com.google.inject.Inject;
import me.fixeddev.inject.ProtectedBinder;
import me.ggamer55.bcm.bukkit.BukkitCommandHandler;
import me.ggamer55.bcm.bukkit.CommandSenderAuthorizer;
import me.ggamer55.bcm.parametric.ParametricCommandHandler;
import net.seocraft.commons.bukkit.authentication.*;
import net.seocraft.commons.bukkit.commands.LoginCommand;
import net.seocraft.commons.bukkit.commands.RegisterCommand;
import net.seocraft.commons.bukkit.commands.WhisperCommand;
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
    @Inject private AuthenticationEnvironmentEventsListener authenticationMovementListener;
    @Inject private AuthenticationLanguageMenuListener authenticationLanguageMenuListener;
    @Inject private AuthenticationLanguageSelectListener authenticationLanguageSelectListener;
    @Inject private AuthenticationCommandsListener authenticationCommandsListener;

    @Inject private UserChatListener userChatListener;
    @Inject private UserAccessResponse userAccessResponse;

    @Inject private LoginCommand loginCommand;
    @Inject private RegisterCommand registerCommand;

    @Inject private WhisperCommand whisperCommand;

    @Inject private CommandSenderAuthorizer commandSenderAuthorizer;

    public List<UUID> unregisteredPlayers = new ArrayList<>();
    public Map<UUID, Integer> loginAttempts = new HashMap<>();
    public Map<UUID, String> playerIdentifier = new HashMap<>();
    public ParametricCommandHandler parametricCommandHandler;

    @Override
    public void onEnable() {
        parametricCommandHandler = new ParametricCommandHandler(this.commandSenderAuthorizer, this.getLogger());
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(this.getLogger());
        loadConfig();
        parametricCommandHandler.registerCommand(this.loginCommand);
        parametricCommandHandler.registerCommand(this.registerCommand);
        dispatcher.registerCommandClass(this.whisperCommand);
        // --- Authentication mode related listeners //
        getServer().getPluginManager().registerEvents(this.authenticationCommandsListener, this);
        getServer().getPluginManager().registerEvents(this.authenticationLanguageMenuListener, this);
        getServer().getPluginManager().registerEvents(this.authenticationLanguageSelectListener, this);
        getServer().getPluginManager().registerEvents(this.authenticationMovementListener, this);

        getServer().getPluginManager().registerEvents(this.userChatListener, this);
        getServer().getPluginManager().registerEvents(this.userAccessResponse, this);
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.bind(CommonsBukkit.class).toInstance(this);
        binder.bind(WhisperManager.class).to(WhisperManagerImpl.class);
        binder.bind(PunishmentHandler.class).to(IPunishmentHandler.class);
        binder.expose(CommonsBukkit.class);
        binder.expose(WhisperManager.class);
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}