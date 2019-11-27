package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.Flag;
import net.md_5.bungee.api.ChatColor;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.management.GameStartManager;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.MatchStatus;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.api.bukkit.utils.ChatGlyphs;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

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
    public boolean startCommand(CommandSender commandSender, int time, @Flag(value = 's') boolean silent) {
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
                            System.out.println("Match which player obtains: " + playerMatch.getId());

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

    @Command(names = {"match debug"}, permission = "commons.staff.match.debug")
    public boolean debugCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {

            Player player = (Player) commandSender;
            player.sendMessage(ChatColor.GOLD + ChatGlyphs.SEPARATOR.getContent());

            player.sendMessage(ChatColor.GREEN + "Gamemode: " + ChatColor.YELLOW + this.coreGameManagement.getGamemode().getName());
            player.sendMessage(ChatColor.GREEN + "SubGamemode: " + ChatColor.YELLOW + this.coreGameManagement.getSubGamemode().getName());

            StringBuilder waitingPlayers = new StringBuilder();
            for (Player wp : this.coreGameManagement.getWaitingPlayers()) {
                waitingPlayers.append(wp.getName()).append(", ");
            }
            if (waitingPlayers.toString().length() >= 2) waitingPlayers.substring(0, waitingPlayers.length() - 2);
            player.sendMessage(ChatColor.GREEN + "Waiting Players: " + ChatColor.YELLOW + waitingPlayers.toString());

            StringBuilder spectatingPlayers = new StringBuilder();
            for (Player sp : this.coreGameManagement.getSpectatingPlayers()) {
                spectatingPlayers.append(sp.getName()).append(", ");
            }
            if (spectatingPlayers.toString().length() >= 2) spectatingPlayers.substring(0, spectatingPlayers.length() - 2);
            player.sendMessage(ChatColor.GREEN + "Spectating Players: " + ChatColor.YELLOW + spectatingPlayers.toString());

            player.sendMessage(ChatColor.GREEN + "Actual matches:");
            for (Match match : this.coreGameManagement.getActualMatches()) {
                player.sendMessage(ChatColor.YELLOW + match.getId().substring(0, 5) + " - " + match.getStatus());
            }

            player.sendMessage(ChatColor.GOLD + ChatGlyphs.SEPARATOR.getContent());
        }
        return true;
    }

    @Command(names = {"match assignation"}, permission = "commons.staff.match.debug")
    public boolean assignationCommand(CommandSender commandSender) {
        if (commandSender instanceof Player) {

            Player player = (Player) commandSender;
            player.sendMessage(ChatColor.GOLD + ChatGlyphs.SEPARATOR.getContent());
            player.sendMessage(ChatColor.GREEN + "Match assignation: ");
            for (Map.Entry<String, User> entry : this.coreGameManagement.getMatchAssignations().entries()) {
                player.sendMessage(ChatColor.YELLOW + entry.getValue().getUsername() + " - " + entry.getKey());
            }

            player.sendMessage(ChatColor.GOLD + ChatGlyphs.SEPARATOR.getContent());
            player.sendMessage(ChatColor.GREEN + "Spectator assignation: ");
            for (Map.Entry<String, User> entry : this.coreGameManagement.getSpectatorAssignations().entries()) {
                player.sendMessage(ChatColor.YELLOW + entry.getValue().getUsername() + " - " + entry.getKey());
            }
            player.sendMessage(ChatColor.GOLD + ChatGlyphs.SEPARATOR.getContent());
        }
        return true;
    }

    @Command(names = {"match cancel"}, permission = "commons.staff.match.cancel", usage = "/<command> [-s]")
    public boolean cancelCommand(CommandSender commandSender, @Flag(value = 's') boolean silent) {
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

    @Command(names = {"match invalidate"}, permission = "commons.staff.match.invalidate")
    public boolean invalidateCommand(CommandSender commandSender) {
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

                                if (playerMatch.getStatus() != MatchStatus.INGAME) {
                                    ChatAlertLibrary.errorChatAlert(
                                            player,
                                            this.translatableField.getField(
                                                    user.getLanguage(),
                                                    "commons_invalidation_notingame"
                                            )
                                    );
                                }

                                try {
                                    this.coreGameManagement.invalidateMatch(playerMatch);
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
