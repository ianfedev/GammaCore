package net.seocraft.commons.bukkit.game.scoreboard;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.creator.board.Scoreboard;
import net.seocraft.api.bukkit.creator.board.ScoreboardManager;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.scoreboard.LobbyScoreboardManager;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.utils.StringUtils;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GammaLobbyScoreboardManager implements LobbyScoreboardManager {

    @Inject private ScoreboardManager scoreboardManager;
    @Inject private CoreGameManagement coreGameManagement;
    @Inject private TranslatableField translatableField;

    private Map<String, Scoreboard> scoreboardMap = new ConcurrentHashMap<>();
    private Map<String, Integer> taskMap = new ConcurrentHashMap<>();

    public void clearScoreboard(@NotNull String userName) {
        taskMap.remove(userName);
    }

    public void setScoreboardTask(@NotNull String userName, int task) {
        taskMap.put(userName, task);
    }

    public void setLobbyScoreboard(@NotNull Match match) {

        Set<User> matchUsers = this.coreGameManagement.getMatchUsers(match.getId());
        matchUsers.addAll(this.coreGameManagement.getMatchSpectatorsUsers(match.getId()));

        matchUsers.forEach(user -> {
            Player player = Bukkit.getPlayer(user.getUsername());

            if (player != null) {

                Scoreboard scoreboard = scoreboardMap.get(user.getId());
                if (!taskMap.containsKey(user.getId())) {
                    scoreboard = this.scoreboardManager.createScoreboard(
                            ChatColor.GOLD + "" + ChatColor.BOLD + this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    this.coreGameManagement.getGamemode().getName().toUpperCase()
                            )
                    );
                    scoreboard.apply(player);
                    scoreboardMap.put(user.getId(), scoreboard);
                } else {
                    this.scoreboardManager.getDefaultRemover().remove(scoreboardMap.get(player.getName()), player);
                    this.scoreboardMap.remove(player.getName());
                }

                if (scoreboard != null) {
                    scoreboard.setLine(
                            9,
                            ChatColor.YELLOW + this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_scoreboard_players"
                            )
                    );
                    scoreboard.setLine(
                            8,
                            ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE +
                                   this.coreGameManagement.getMatchPlayers(match.getId()).size() + "/" + this.coreGameManagement.getSubGamemode().getMaxPlayers()
                    );
                    scoreboard.setLine(
                            7,
                            ChatColor.YELLOW + this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_scoreboard_starting"
                            )
                    );
                    scoreboard.setLine(
                            6,
                            ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE +
                                    this.translatableField.getUnspacedField(user.getLanguage(), "commons_scoreboard_insufficent")
                    );
                    scoreboard.setLine(
                            5,
                            ChatColor.YELLOW + this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_scoreboard_map"
                            )
                    );
                    scoreboard.setLine(
                            4,
                            ChatColor.YELLOW + "\u00BB " + ChatColor.GREEN + ChatColor.WHITE + this.coreGameManagement.getMatchMap(match).getName()
                    );
                    scoreboard.setLine(
                            3,
                            ChatColor.YELLOW + this.translatableField.getUnspacedField(
                                    user.getLanguage(),
                                    "commons_scoreboard_mode"
                            )
                    );
                    scoreboard.setLine(2, ChatColor.YELLOW + "\u00BB " + ChatColor.ITALIC + ChatColor.WHITE +
                            StringUtils.capitalizeString(this.coreGameManagement.getSubGamemode().getName().toLowerCase().replace("_", " "))
                    );
                    scoreboard.setLine(1, ChatColor.AQUA + " ");
                    scoreboard.setLine(0, ChatColor.YELLOW + "www.seocraft.net");
                }

            }
        });

    }

}
