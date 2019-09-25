package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Parameter;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MatchCommand implements CommandClass {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private GameStartManager gameStartManager;
    @Inject private CoreGameManagement coreGameManagement;
    @Inject private CommonsBukkit instance;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translatableField;

    @Command(names = {"match help"}, permission = "commons.staff.match.help")
    public boolean mainCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                if (playerSession != null) {
                    CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                        if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User user = userAsyncResponse.getResponse();
                            // TODO: Show help

                        } else {
                            ChatAlertLibrary.errorChatAlert(player);
                        }
                    });
                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player);
            }
        }
        return true;
    }

    @Command(names = {"match start"}, permission = "commons.staff.match.start", min = 1, usage = "/<command> <time> [-s]")
    public boolean startCommand(CommandSender commandSender, int time, @Parameter(value = "s", isFlag = true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                if (playerSession != null) {
                    CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                        if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User user = userAsyncResponse.getResponse();

                            Match playerMatch = this.coreGameManagement.getPlayerMatch(user);

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

                                if (!playerMatch.getStatus().equals(MatchStatus.WAITING)) {
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
                                        playerMatch,
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
                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player);
            }
        }
        return true;
    }

    @Command(names = {"match cancel"}, permission = "commons.staff.match.cancel", usage = "/<command> [-s]")
    public boolean cancelCommand(CommandSender commandSender, @Parameter(value = "s", isFlag = true) boolean silent) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            GameSession playerSession;
            try {
                playerSession = this.gameSessionManager.getCachedSession(player.getName());
                if (playerSession != null) {
                    CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(playerSession.getPlayerId()), userAsyncResponse -> {
                        if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User user = userAsyncResponse.getResponse();

                            Match playerMatch = this.coreGameManagement.getPlayerMatch(user);

                            if (alertNotGameServer(player, user)) return;

                            if (playerMatch != null) {

                                if (!playerMatch.getStatus().equals(MatchStatus.WAITING)) {
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
                                        playerMatch,
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
                } else {
                    ChatAlertLibrary.errorChatAlert(player);
                }
            } catch (IOException e) {
                ChatAlertLibrary.errorChatAlert(player);
            }
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
