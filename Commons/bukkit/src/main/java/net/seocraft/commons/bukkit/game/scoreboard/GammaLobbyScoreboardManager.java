package net.seocraft.commons.bukkit.game.scoreboard;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.board.LightingAnimatedBoard;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.*;
import net.seocraft.api.bukkit.game.scoreboard.LobbyScoreboardManager;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.utils.StringUtils;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.GameBoard;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GammaLobbyScoreboardManager implements LobbyScoreboardManager {

    @Inject private Plugin plugin;
    @Inject private GamemodeProvider gamemodeProvider;
    @Inject private MatchMapProvider matchMapProvider;
    @Inject private MatchTimerProvider matchTimerProvider;
    @Inject private MatchDataProvider matchDataProvider;
    @Inject private TranslatableField translatableField;

    @Override
    public void retrieveGameBoard(@NotNull Match match, @NotNull Player player, @NotNull User user) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError {

        Gamemode gamemode = this.gamemodeProvider.getServerGamemode();
        SubGamemode subGamemode = this.gamemodeProvider.getServerSubgamemode();

        if (gamemode != null && subGamemode != null) {
            LightingAnimatedBoard board = new LightingAnimatedBoard(this.translatableField.getUnspacedField(
                    user.getLanguage(),
                    gamemode.getName().toUpperCase()
            ), this.plugin);

            board.addStaticLine(10,ChatColor.RED + " ");
            board.addStaticLine(
                    9,
                    ChatColor.YELLOW + this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_scoreboard_players"
                    )
            );
            board.addLine(
                    8,
                    ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE +
                            this.matchDataProvider.getMatchParticipants(match, PlayerType.HOLDING).size() + "/" + subGamemode.getMaxPlayers()
            );
            board.addStaticLine(
                    7,
                    ChatColor.YELLOW + this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_scoreboard_starting"
                    )
            );

            if (this.matchTimerProvider.hasRemainingTime(match) && this.matchTimerProvider.getRemainingTime(match) != -1) {
                board.addLine(
                        6,
                        ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE +
                                this.matchTimerProvider.getRemainingTime(match) + "s"
                );
            } else {
                board.addLine(
                        6,
                        ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE +
                                this.translatableField.getUnspacedField(user.getLanguage(), "commons_scoreboard_insufficent")
                );
            }

            board.addStaticLine(
                    5,
                    ChatColor.YELLOW + this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_scoreboard_map"
                    )
            );
            board.addStaticLine(
                    4,
                    ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE + this.matchMapProvider.getMatchMap(match).getName()
            );
            board.addStaticLine(
                    3,
                    ChatColor.YELLOW + this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_scoreboard_mode"
                    )
            );
            board.addStaticLine(2, ChatColor.YELLOW + "\u00BB " + ChatColor.ITALIC + ChatColor.WHITE +
                    StringUtils.capitalizeString(subGamemode.getName().toLowerCase().replace("_", " "))
            );
            board.addStaticLine(1, ChatColor.AQUA + " ");
            board.addStaticLine(0, ChatColor.YELLOW + "www.seocraft.net");

            if (player.hasAttachedBoard()) player.removeScoreboard();
            player.setAttachedBoard(board);
        }

    }

    @Override
    public void updateBoardCountDown(@NotNull Match match, int count) {
        this.matchDataProvider.getMatchParticipants(match).forEach(user -> {

            Player player = Bukkit.getPlayer(user.getUsername());

            if (player != null && player.hasAttachedBoard() && player.getAttachedBoard() != null) {
                GameBoard board = player.getAttachedBoard();
                if (board.getLine(6).isPresent()) {
                    if (count == -1) {
                        board.setLine(
                                6,
                                ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE +
                                        this.translatableField.getUnspacedField(user.getLanguage(), "commons_scoreboard_insufficent")
                        );
                    } else {
                        board.setLine(
                                6,
                                ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE +
                                        this.matchTimerProvider.getRemainingTime(match) + "s"
                        );
                    }
                }
            }

        });
    }

}
