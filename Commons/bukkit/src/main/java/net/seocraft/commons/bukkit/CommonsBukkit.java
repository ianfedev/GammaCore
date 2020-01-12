package net.seocraft.commons.bukkit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import me.fixeddev.bcm.bukkit.BukkitCommandHandler;
import me.fixeddev.bcm.bukkit.CommandSenderAuthorizer;
import me.fixeddev.bcm.parametric.ParametricCommandHandler;
import me.fixeddev.bcm.parametric.providers.ParameterProviderRegistry;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.announcement.AnnouncementHandler;
import net.seocraft.api.bukkit.creator.intercept.PacketManager;
import net.seocraft.api.bukkit.creator.npc.listener.NPCSpawnListener;
import net.seocraft.api.bukkit.creator.npc.listener.NPCUseListener;
import net.seocraft.api.bukkit.event.GameProcessingReadyEvent;
import net.seocraft.api.bukkit.game.management.MapFileManager;
import net.seocraft.api.bukkit.punishment.PunishmentProvider;
import net.seocraft.api.bukkit.stats.StatsProvider;
import net.seocraft.api.bukkit.user.UserLoginManagement;
import net.seocraft.api.bukkit.whisper.WhisperManager;
import net.seocraft.api.core.friend.FriendshipProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.Server;
import net.seocraft.api.core.server.ServerLoad;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.bukkit.announcement.GammaAnnouncementHandler;
import net.seocraft.commons.bukkit.authentication.*;
import net.seocraft.commons.bukkit.channel.admin.ACModule;
import net.seocraft.commons.bukkit.cloud.CloudModule;
import net.seocraft.commons.bukkit.command.*;
import net.seocraft.commons.bukkit.creator.board.ScoreboardModule;
import net.seocraft.commons.bukkit.friend.UserFriendshipProvider;
import net.seocraft.commons.bukkit.game.GameModule;
import net.seocraft.commons.bukkit.listener.DisabledPluginsCommandListener;
import net.seocraft.commons.bukkit.listener.game.*;
import net.seocraft.commons.bukkit.punishment.UserPunishmentProvider;
import net.seocraft.commons.bukkit.serializer.InterfaceDeserializer;
import net.seocraft.commons.bukkit.server.ServerModule;
import net.seocraft.commons.bukkit.stats.GameStatsProvider;
import net.seocraft.commons.bukkit.user.*;
import net.seocraft.commons.bukkit.whisper.CraftWhisperManager;
import net.seocraft.commons.core.CoreModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class CommonsBukkit extends JavaPlugin {

    @Inject private AnnouncementHandler announcementHandler;

    // --- Authentication mode related listeners --- //
    @Inject private AuthenticationEnvironmentEventsListener authenticationMovementListener;
    @Inject private AuthenticationLanguageMenuListener authenticationLanguageMenuListener;
    @Inject private AuthenticationLanguageSelectListener authenticationLanguageSelectListener;
    @Inject private AuthenticationInventoryDropListener authenticationInventoryDropListener;
    @Inject private AuthenticationCommandsListener authenticationCommandsListener;

    // --- Game API related listeners --- //
    @Inject private PlayerDamageListener playerDamageListener;
    @Inject private PlayerSpectatorListener playerSpectatorListener;

    @Inject private GamePairingListener gamePairingListener;
    @Inject private GameFinishedListener gameFinishedListener;
    @Inject private GameStartedListener gameStartedListener;
    @Inject private DisabledPluginsCommandListener disabledPluginsCommandListener;
    @Inject private UserDisconnectListener userDisconnectListener;

    @Inject private UserChatListener userChatListener;
    @Inject private UserJoinListener userJoinListener;

    @Inject private LoginCommand loginCommand;
    @Inject private RegisterCommand registerCommand;
    @Inject private AdminChatCommand adminChatCommand;

    @Inject private VerificationCommand verificationCommand;
    @Inject private WhisperCommand whisperCommand;
    @Inject private PunishmentCommand punishmentCommand;
    @Inject private FriendCommand friendCommand;
    @Inject private MatchCommand matchCommand;

    @Inject private CommandSenderAuthorizer commandSenderAuthorizer;
    @Inject private ServerLoad serverLoad;
    @Inject private MapFileManager mapFileManager;

    @Inject private PacketManager packetManager;
    @Inject private NPCSpawnListener npcSpawnListener;
    @Inject private NPCUseListener npcUseListener;

    public List<UUID> unregisteredPlayers = new ArrayList<>();
    public Map<UUID, Integer> loginAttempts = new HashMap<>();
    public ParametricCommandHandler parametricCommandHandler;
    public boolean pairedGame = false;
    private boolean cloudDeploy = false;
    public int pairingRunnable;
    @NotNull public Server serverRecord;

    @Override
    public void onEnable() {
        parametricCommandHandler = new ParametricCommandHandler(commandSenderAuthorizer, null, ParameterProviderRegistry.createRegistry(), getLogger());
        BukkitCommandHandler dispatcher = new BukkitCommandHandler(getLogger(), null, ParameterProviderRegistry.createRegistry());
        saveDefaultConfig();

        try {
            this.serverRecord = this.serverLoad.setupServer();

            if (this.serverRecord.getServerType() == ServerType.GAME) {
                Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(
                        new GameProcessingReadyEvent(
                                Objects.requireNonNull(this.serverRecord.getGamemode()),
                                Objects.requireNonNull(this.serverRecord.getSubGamemode())
                        )
                ));
                this.mapFileManager.configureMapFolder();
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

        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException ex) {
            ex.printStackTrace();
            Bukkit.getLogger().log(Level.SEVERE, "[Bukkit-API] Error when authorizing server load.");
            Bukkit.shutdown();
        }

        parametricCommandHandler.registerCommand(adminChatCommand);

        this.packetManager.addPacketListener(this.npcSpawnListener);
        this.packetManager.addPacketListener(this.npcUseListener);

        dispatcher.registerCommandClass(whisperCommand);
        dispatcher.registerCommandClass(punishmentCommand);
        dispatcher.registerCommandClass(friendCommand);
        dispatcher.registerCommandClass(matchCommand);
        dispatcher.registerCommandClass(verificationCommand);

        getServer().getPluginManager().registerEvents(gamePairingListener, this);
        getServer().getPluginManager().registerEvents(userChatListener, this);
        getServer().getPluginManager().registerEvents(userJoinListener, this);
        getServer().getPluginManager().registerEvents(disabledPluginsCommandListener, this);
        getServer().getPluginManager().registerEvents(userDisconnectListener, this);

        if (getConfig().getBoolean("announcement.enabled")) announcementHandler.startAnnouncementDisplaying();


        if (getConfig().getBoolean("authentication.enabled", false)) {
            enableAuthentication();
        }

        if (this.getServerRecord().getServerType().equals(ServerType.GAME)) {
            enableGameEvents();
        }
    }

    @Override
    public void onDisable() {
        try {
            this.serverLoad.disconnectServer();
        } catch (Unauthorized | InternalServerError | NotFound | BadRequest unauthorized) {
            Bukkit.getLogger().log(Level.SEVERE, "[Bukkit API] Error while shutting down server.");
        }
    }

    @Override
    public void configure(ProtectedBinder binder) {

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase("CloudNet-Bridge")) {
                Bukkit.getLogger().log(Level.INFO, "[BukkitAPI] This server has been deployed in a cloud.");
                this.cloudDeploy = true;
            }
        }

        if (!this.cloudDeploy)
            Bukkit.getLogger().log(Level.INFO, "[BukkitAPI] There was not found a cloud plugin. Starting standalone implementation. (This could restrict some functinos like server teleport or lobby icons)");

        binder.bind(FriendshipProvider.class).to(UserFriendshipProvider.class).in(Scopes.SINGLETON);
        binder.bind(PunishmentProvider.class).to(UserPunishmentProvider.class).in(Scopes.SINGLETON);
        binder.bind(WhisperManager.class).to(CraftWhisperManager.class).in(Scopes.SINGLETON);
        binder.bind(StatsProvider.class).to(GameStatsProvider.class).in(Scopes.SINGLETON);
        binder.bind(AnnouncementHandler.class).to(GammaAnnouncementHandler.class).in(Scopes.SINGLETON);
        binder.bind(UserLoginManagement.class).to(GammaUserLoginManagement.class).in(Scopes.SINGLETON);
        binder.publicBinder().bind(CommonsBukkit.class).toInstance(this);
        binder.bind(ObjectMapper.class).toProvider(() -> {
            ObjectMapper mapper = new ObjectMapper().registerModule(InterfaceDeserializer.getAbstractTypes());
            mapper.setVisibility(mapper.getSerializationConfig()
                    .getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withIsGetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC));
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper;
        }).in(Scopes.SINGLETON);
        binder.install(new CoreModule());
        binder.install(new GameModule());
        binder.install(new ServerModule());
        binder.install(new UserModule());
        binder.install(new CloudModule(this.cloudDeploy));
        binder.install(new ScoreboardModule());
        binder.install(new ACModule());
        binder.expose(FriendshipProvider.class);
        binder.expose(PunishmentProvider.class);
        binder.expose(WhisperManager.class);
        binder.expose(StatsProvider.class);
    }

    private void enableAuthentication() {
        parametricCommandHandler.registerCommand(loginCommand);
        parametricCommandHandler.registerCommand(registerCommand);

        getServer().getPluginManager().registerEvents(authenticationCommandsListener, this);
        getServer().getPluginManager().registerEvents(authenticationLanguageMenuListener, this);
        getServer().getPluginManager().registerEvents(authenticationLanguageSelectListener, this);
        getServer().getPluginManager().registerEvents(authenticationMovementListener, this);
        getServer().getPluginManager().registerEvents(authenticationInventoryDropListener, this);

    }

    private void enableGameEvents() {
        getServer().getPluginManager().registerEvents(playerDamageListener, this);
        getServer().getPluginManager().registerEvents(playerSpectatorListener, this);
        getServer().getPluginManager().registerEvents(gameFinishedListener, this);
        getServer().getPluginManager().registerEvents(gameStartedListener, this);
    }

    public @NotNull Server getServerRecord() {
        return this.serverRecord;
    }

    public void setServerRecord(Server server) {
        this.serverRecord = server;
    }

    public boolean hasCloudDeploy() {
        return this.cloudDeploy;
    }

}