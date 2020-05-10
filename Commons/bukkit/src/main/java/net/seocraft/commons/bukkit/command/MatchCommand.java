package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
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
import net.seocraft.api.bukkit.game.match.MatchProvider;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.bukkit.utils.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

@ACommand(names = {"match"}, permission = "commons.staff.match.help")
public class MatchCommand implements CommandClass {

    @Inject private GameStartManager gameStartManager;
    @Inject private MatchDataProvider matchDataProvider;
    @Inject private CoreGameManagement coreGameManagement;
    @Inject private MatchProvider matchProvider;
    @Inject private CommonsBukkit instance;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;

    @ACommand(names = {"", "help"})
    public boolean mainCommand(@Injected(true) @Sender Player commandSender) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(commandSender.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();
                // TODO: Show help

            } else {
                ChatAlertLibrary.errorChatAlert(commandSender);
            }
        });
        return true;
    }

    @ACommand(names = {"debug"}, permission = "commons.staff.math.debug")
    public boolean debugCommand(@Injected(true) @Sender Player player) {
        player.sendMessage(ChatColor.YELLOW + ChatGlyphs.SEPARATOR.getContent());
        this.instance.getServerRecord().getMatches().forEach(match -> {
            try {
                Match queriedMatch = this.matchProvider.getCachedMatchSync(match);
                player.sendMessage(ChatColor.GRAY + "Match " + ChatColor.GREEN + "#" + queriedMatch.getId());
                player.sendMessage(ChatColor.GRAY + "Assignations:");
                this.matchDataProvider.getMatchParticipants(queriedMatch).forEach(assignation ->
                        player.sendMessage(ChatColor.GREEN + assignation.getUsername() + " - " + queriedMatch.getMatchRecord().get(assignation.getId()))
                );
            } catch (IOException | Unauthorized | BadRequest | NotFound | InternalServerError ex) {
                Bukkit.getLogger().log(Level.WARNING, "[GameAPI] Error while querying match.", ex);
            }
        });
        player.sendMessage(ChatColor.YELLOW + ChatGlyphs.SEPARATOR.getContent());
        return true;
    }

    @ACommand(names = {"start"}, permission = "commons.staff.match.start")
    public boolean startCommand(@Injected(true) @Sender Player commandSender, @Named("time") Integer time, @Flag(value = 's') boolean silent) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(commandSender.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                MatchAssignation playerMatch = this.matchDataProvider.getPlayerMatch(user.getId());

                if (alertNotGameServer(commandSender, user)) return;

                if (playerMatch != null) {
                    if (time < 5 || time > 300) {
                        ChatAlertLibrary.errorChatAlert(
                                commandSender,
                                this.translatableField.getField(
                                        user.getLanguage(),
                                        "commons_countdown_forced_invalid"
                                )
                        );
                        return;
                    }

                    if (!playerMatch.getMatch().getStatus().equals(MatchStatus.WAITING)) {
                        ChatAlertLibrary.errorChatAlert(
                                commandSender,
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
                            commandSender,
                            this.translatableField.getField(
                                    user.getLanguage(),
                                    "commons_not_ingame"
                            )
                    );
                }

            } else {
                ChatAlertLibrary.errorChatAlert(commandSender);
            }
        });
        return true;
    }

    @ACommand(names = {"cancel"}, permission = "commons.staff.match.cancel")
    public boolean cancelCommand(@Injected(true) @Sender Player commandSender, @Flag(value = 's') boolean silent) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(commandSender.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                MatchAssignation playerMatch = this.matchDataProvider.getPlayerMatch(user.getId());

                if (alertNotGameServer(commandSender, user)) return;

                if (playerMatch != null) {

                    if (!playerMatch.getMatch().getStatus().equals(MatchStatus.WAITING)) {
                        ChatAlertLibrary.errorChatAlert(
                                commandSender,
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
                            commandSender,
                            this.translatableField.getField(
                                    user.getLanguage(),
                                    "commons_not_ingame"
                            )
                    );
                }

            } else {
                ChatAlertLibrary.errorChatAlert(commandSender);
            }
        });
        return true;
    }

    @ACommand(names = {"invalidate"}, permission = "commons.staff.match.invalidate")
    public boolean invalidateCommand(@Injected(true) @Sender Player commandSender) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(commandSender.getDatabaseIdentifier()), userAsyncResponse -> {
            if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                User user = userAsyncResponse.getResponse();

                MatchAssignation playerMatch = this.matchDataProvider.getPlayerMatch(user.getId());

                if (alertNotGameServer(commandSender, user)) return;

                if (playerMatch != null) {

                    if (playerMatch.getMatch().getStatus() != MatchStatus.INGAME) {
                        ChatAlertLibrary.errorChatAlert(
                                commandSender,
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
                            commandSender,
                            this.translatableField.getField(
                                    user.getLanguage(),
                                    "commons_not_ingame"
                            )
                    );
                }

            } else {
                ChatAlertLibrary.errorChatAlert(commandSender);
            }
        });
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
