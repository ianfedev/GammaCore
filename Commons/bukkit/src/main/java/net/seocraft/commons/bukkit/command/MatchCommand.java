package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Flag;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import me.fixeddev.ebcm.parametric.annotation.Named;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.MatchAssignation;
import net.seocraft.api.bukkit.game.match.MatchDataProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

@ACommand(names = {"match help"}, permission = "commons.staff.match.help")
public class MatchCommand implements CommandClass {

    @Inject private GameStartManager gameStartManager;
    @Inject private MatchDataProvider matchDataProvider;
    @Inject private CoreGameManagement coreGameManagement;
    @Inject private CommonsBukkit instance;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;

    @ACommand(names = {"", "help"})
    public boolean mainCommand(@Injected(true) @Named("SENDER") CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();
                    // TODO: Show help

                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            });
        }
        return true;
    }

    @ACommand(names = {"start"}, permission = "commons.staff.match.start")
    public boolean startCommand(@Injected(true) @Named("SENDER") CommandSender commandSender, @Named("time") Integer time, @Flag(value = 's') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    MatchAssignation playerMatch = this.matchDataProvider.getPlayerMatch(user.getId());

                    if (alertNotGameServer(player, user)) return;

                    if (playerMatch != null) {
                        if (time < 5 || time > 300) {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getField(
                                            user.getLanguage(),
                                            "commons_countdown_forced_invalid"
                                    )
                            );
                            return;
                        }

                        if (!playerMatch.getMatch().getStatus().equals(MatchStatus.WAITING)) {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getField(
                                            user.getLanguage(),
                                            "commons_countdown_already_started"
                                    )
                            );
                            return;
                        }

                        this.gameStartManager.forceMatchCountdown(
                                playerMatch.getMatch(),
                                time,
                                user,
                                silent
                        );
                    } else {
                        ChatAlertLibrary.errorChatAlert(
                                player,
                                this.translatableField.getField(
                                        user.getLanguage(),
                                        "commons_not_ingame"
                                )
                        );
                    }

                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            });
        }
        return true;
    }

    @ACommand(names = {"cancel"}, permission = "commons.staff.match.cancel")
    public boolean cancelCommand(@Injected(true) @Named("SENDER") CommandSender commandSender, @Flag(value = 's') boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    MatchAssignation playerMatch = this.matchDataProvider.getPlayerMatch(user.getId());

                    if (alertNotGameServer(player, user)) return;

                    if (playerMatch != null) {

                        if (!playerMatch.getMatch().getStatus().equals(MatchStatus.WAITING)) {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getField(
                                            user.getLanguage(),
                                            "commons_countdown_already_started"
                                    )
                            );
                            return;
                        }

                        this.gameStartManager.cancelMatchCountdown(
                                playerMatch.getMatch(),
                                user,
                                silent
                        );
                    } else {
                        ChatAlertLibrary.errorChatAlert(
                                player,
                                this.translatableField.getField(
                                        user.getLanguage(),
                                        "commons_not_ingame"
                                )
                        );
                    }

                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            });
        }
        return true;
    }

    @ACommand(names = {"invalidate"}, permission = "commons.staff.match.invalidate")
    public boolean invalidateCommand(@Injected(true) @Named("SENDER") CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(player.getDatabaseIdentifier()), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    MatchAssignation playerMatch = this.matchDataProvider.getPlayerMatch(user.getId());

                    if (alertNotGameServer(player, user)) return;

                    if (playerMatch != null) {

                        if (playerMatch.getMatch().getStatus() != MatchStatus.INGAME) {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translatableField.getField(
                                            user.getLanguage(),
                                            "commons_invalidation_notingame"
                                    )
                            );
                        }

                        try {
                            this.coreGameManagement.invalidateMatch(playerMatch.getMatch());
                        } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException e) {
                            Bukkit.getLogger().log(Level.SEVERE, "[Game API] There was an error invalidating the match. ({0})", e.getMessage());
                        }

                    } else {
                        ChatAlertLibrary.errorChatAlert(
                                player,
                                this.translatableField.getField(
                                        user.getLanguage(),
                                        "commons_not_ingame"
                                )
                        );
                    }

                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            });
        }
        return true;
    }

    private boolean alertNotGameServer(Player player, User user) {
        if (!this.instance.getServerRecord().getServerType().equals(ServerType.GAME)) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    this.translatableField.getField(
                            user.getLanguage(),
                            "commons_not_gameserver"
                    )
            );
            return true;
        }
        return false;
    }
}
