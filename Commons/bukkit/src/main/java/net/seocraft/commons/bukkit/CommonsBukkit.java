package net.seocraft.commons.bukkit;

import com.google.inject.Inject;
import me.ggamer55.bcm.bukkit.BukkitCommandHandler;
import me.ggamer55.bcm.bukkit.CommandSenderAuthorizer;
import me.ggamer55.bcm.parametric.ParametricCommandHandler;
import net.seocraft.commons.bukkit.authentication.AuthenticationCommandsListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationEnvironmentEventsListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationLanguageMenuListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationLanguageSelectListener;
import net.seocraft.commons.bukkit.commands.LoginCommand;
import net.seocraft.commons.bukkit.commands.RegisterCommand;
import net.seocraft.commons.bukkit.user.UserAccessResponse;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommonsBukkit extends JavaPlugin {

    // --- Authentication mode related listeners //
    @Inject private AuthenticationEnvironmentEventsListener authenticationMovementListener;
    @Inject private AuthenticationLanguageMenuListener authenticationLanguageMenuListener;
    @Inject private AuthenticationLanguageSelectListener authenticationLanguageSelectListener;
    @Inject private AuthenticationCommandsListener authenticationCommandsListener;
    @Inject private UserAccessResponse userAccessResponse;

    @Inject private LoginCommand loginCommand;
    @Inject private RegisterCommand registerCommand;

    @Inject private CommandSenderAuthorizer commandSenderAuthorizer;

    private static CommonsBukkit instance;
    public List<UUID> unregisteredPlayers = new ArrayList<>();
    public Map<UUID, Integer> loginAttempts = new HashMap<>();
    public ParametricCommandHandler parametricCommandHandler;

    @Override
    public void onEnable() {
        instance = this;
        parametricCommandHandler = new ParametricCommandHandler(this.commandSenderAuthorizer, this.getLogger());
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(this.getLogger());
        loadConfig();
        parametricCommandHandler.registerCommand(this.loginCommand);
        parametricCommandHandler.registerCommand(this.registerCommand);
        // --- Authentication mode related listeners //
        getServer().getPluginManager().registerEvents(this.authenticationCommandsListener, this);
        getServer().getPluginManager().registerEvents(this.authenticationLanguageMenuListener, this);
        getServer().getPluginManager().registerEvents(this.authenticationLanguageSelectListener, this);
        getServer().getPluginManager().registerEvents(this.authenticationMovementListener, this);

        getServer().getPluginManager().registerEvents(this.userAccessResponse, this);
    }

    @Override
    public void configure() {
        this.bind(CommonsBukkit.class).toInstance(this);
    }

    private void loadConfig(){
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static CommonsBukkit getInstance() {
        return instance;
    }

}