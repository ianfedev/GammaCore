package net.seocraft.commons.bukkit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import me.fixeddev.bcm.basic.NoOpPermissionMessageProvider;
import me.fixeddev.bcm.bukkit.BukkitCommandHandler;
import me.fixeddev.bcm.bukkit.CommandSenderAuthorizer;
import me.fixeddev.bcm.parametric.ParametricCommandHandler;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.whisper.WhisperManager;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.bukkit.authentication.AuthenticationCommandsListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationEnvironmentEventsListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationLanguageMenuListener;
import net.seocraft.commons.bukkit.authentication.AuthenticationLanguageSelectListener;
import net.seocraft.commons.bukkit.cloud.CloudModule;
import net.seocraft.commons.bukkit.command.*;
import net.seocraft.api.bukkit.event.GameProcessingReadyEvent;
import net.seocraft.commons.bukkit.friend.UserFriendshipProvider;
import net.seocraft.commons.bukkit.game.GameModule;
import net.seocraft.commons.bukkit.listener.DisabledPluginsCommandListener;
import net.seocraft.commons.bukkit.listener.GamePairingListener;
import net.seocraft.commons.bukkit.game.management.CraftMapFileManager;
import net.seocraft.commons.bukkit.punishment.PunishmentModule;
import net.seocraft.commons.bukkit.serializer.InterfaceDeserializer;
import net.seocraft.commons.bukkit.server.ServerModule;
import net.seocraft.commons.bukkit.user.UserAccessResponse;
import net.seocraft.commons.bukkit.user.UserChatListener;
import net.seocraft.commons.bukkit.user.UserDisconnectListener;
import net.seocraft.commons.bukkit.user.UserModule;
import net.seocraft.commons.bukkit.whisper.CraftWhisperManager;
import net.seocraft.commons.core.CoreModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class CommonsBukkit extends JavaPlugin {

    // --- Authentication mode related listeners //
    @Inject private AuthenticationEnvironmentEventsListener authenticationMovementListener;
    @Inject private AuthenticationLanguageMenuListener authenticationLanguageMenuListener;
    @Inject private AuthenticationLanguageSelectListener authenticationLanguageSelectListener;
    @Inject private AuthenticationCommandsListener authenticationCommandsListener;

    @Inject private GamePairingListener gamePairingListener;
    @Inject private DisabledPluginsCommandListener disabledPluginsCommandListener;
    @Inject private UserDisconnectListener userDisconnectListener;

    @Inject private UserChatListener userChatListener;
    @Inject private UserAccessResponse userAccessResponse;

    @Inject private LoginCommand loginCommand;
    @Inject private RegisterCommand registerCommand;

    @Inject private WhisperCommand whisperCommand;
    @Inject private PunishmentCommand punishmentCommand;
    @Inject private FriendCommand friendCommand;

    @Inject private CommandSenderAuthorizer commandSenderAuthorizer;
    @Inject private ServerLoad serverLoad;
    @Inject private CraftMapFileManager craftMapFileManager;

    public List<UUID> unregisteredPlayers = new ArrayList<>();
    public Map<UUID, Integer> loginAttempts = new HashMap<>();
    public ParametricCommandHandler parametricCommandHandler;
    public int pairingRunnable;
    @NotNull public Server serverRecord;

    @Override
    public void onEnable() {
        parametricCommandHandler = new ParametricCommandHandler(commandSenderAuthorizer, new NoOpPermissionMessageProvider(), getLogger());
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(getLogger(), new NoOpPermissionMessageProvider());
        loadConfig();

        try {
            this.serverRecord = this.serverLoad.setupServer();

            if (this.serverRecord.getServerType() == ServerType.GAME) {
                this.craftMapFileManager.configureMapFolder();
                // The punishment event should be called in the main thread only
                Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(
                        new GameProcessingReadyEvent(
                                Objects.requireNonNull(this.serverRecord.getGamemode()),
                                Objects.requireNonNull(this.serverRecord.getSubGamemode())
                        )
                ));
                pairingRunnable = Bukkit.getScheduler().scheduleSyncDelayedTask(
                        this,
                        ()  -> {
                            Bukkit.getLogger().log(
                                Level.SEVERE,
                                "[GameAPI] No game was paired during last 2 minutes, shutting down server."
                            );
                            Bukkit.shutdown();
                        },
                        600L
                );
            }

        } catch (Unauthorized | BadRequest | NotFound | InternalServerError ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "[Bukkit-API] Error when authorizing server load.");
            Bukkit.shutdown();
        }

        dispatcher.registerCommandClass(whisperCommand);
        dispatcher.registerCommandClass(punishmentCommand);
        dispatcher.registerCommandClass(friendCommand);

        getServer().getPluginManager().registerEvents(gamePairingListener, this);
        getServer().getPluginManager().registerEvents(userChatListener, this);
        getServer().getPluginManager().registerEvents(userAccessResponse, this);
        getServer().getPluginManager().registerEvents(disabledPluginsCommandListener, this);
        getServer().getPluginManager().registerEvents(userDisconnectListener, this);


        if (getConfig().getBoolean("authentication.enabled", false)) {
            enableAuthentication();
        }
    }


    @Override
    public void configure(ProtectedBinder binder) {
        binder.publicBinder().bind(CommonsBukkit.class).toInstance(this);
        binder.bind(WhisperManager.class).to(CraftWhisperManager.class);
        binder.bind(FriendshipProvider.class).to(UserFriendshipProvider.class);
        binder.bind(ObjectMapper.class).toProvider(() -> {
            ObjectMapper mapper = new ObjectMapper().registerModule(InterfaceDeserializer.getAbstractTypes());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper;
        }).in(Scopes.SINGLETON);
        binder.install(new CoreModule());
        binder.install(new GameModule());
        binder.install(new PunishmentModule());
        binder.install(new ServerModule());
        binder.install(new UserModule());
        binder.install(new CloudModule());
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

    public void setServerRecord(Server server) {
        this.serverRecord = server;
    }

}