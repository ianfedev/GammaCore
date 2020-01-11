package net.seocraft.lobby.board;

import net.seocraft.api.bukkit.BukkitAPI;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.creator.board.Scoreboard;
import net.seocraft.api.bukkit.creator.board.ScoreboardManager;
import net.seocraft.api.core.group.partial.Flair;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.core.group.partial.MinecraftFlair;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class LobbyScoreboardTask extends BukkitRunnable {

    @NotNull private ScoreboardManager scoreboardManager;
    @NotNull private CloudManager cloudManager;
    @NotNull private TranslatableField translatableField;
    @NotNull private BukkitAPI bukkitAPI;

    @NotNull private String player;
    @NotNull private User user;

    public LobbyScoreboardTask(@NotNull ScoreboardManager scoreboardManager, @NotNull CloudManager cloudManager, @NotNull TranslatableField translatableField, @NotNull BukkitAPI bukkitAPI, @NotNull String player, @NotNull User user) {
        this.scoreboardManager = scoreboardManager;
        this.cloudManager = cloudManager;
        this.translatableField = translatableField;
        this.bukkitAPI = bukkitAPI;
        this.player = player;
        this.user = user;
    }

    @Override
    public void run() {
        Player scoreboardPlayer = Bukkit.getPlayer(this.player);
        if (scoreboardPlayer != null) {

            Flair primaryFlair = new MinecraftFlair("","gray","");
            for (Flair flair : this.user.getPrimaryGroup().getMinecraftFlairs()) {
                if (flair.getRealm().equalsIgnoreCase(this.bukkitAPI.getConfig().getString("realm"))) {
                    primaryFlair = flair;
                    break;
                }
            }

            Scoreboard lobbyBoard = this.scoreboardManager.createScoreboard(
                    ChatColor.GOLD + "" + ChatColor.BOLD + "Seocraft Network"
            );

            lobbyBoard.setLine(10, ChatColor.YELLOW +
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_lobby_scoreboard_rank"
                    )
            );

            lobbyBoard.setLine(9, ChatColor.YELLOW + "\u00BB "
                    + ChatColor.valueOf(primaryFlair.getColor().toUpperCase()) + user.getPrimaryGroup().getName()
            );

            lobbyBoard.setLine(8, ChatColor.YELLOW +
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_lobby_scoreboard_level"
                    )
            );

            lobbyBoard.setLine(11, " ");

            lobbyBoard.setLine(7, ChatColor.ITALIC + "" + ChatColor.YELLOW + "\u00BB "
                    + ChatColor.WHITE + user.getLevel()
            );

            lobbyBoard.setLine(6, ChatColor.YELLOW +
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_lobby_scoreboard_lobby"
                    )
            );

            lobbyBoard.setLine(5, ChatColor.RED + "" + ChatColor.YELLOW + "\u00BB "
                    + ChatColor.WHITE + "#" + Bukkit.getServerName().split("-")[1]
            );

            lobbyBoard.setLine(4, ChatColor.YELLOW +
                    this.translatableField.getUnspacedField(
                            user.getLanguage(),
                            "commons_lobby_scoreboard_players"
                    )
            );

            lobbyBoard.setLine(3, ChatColor.MAGIC + "" + ChatColor.YELLOW + "\u00BB "
                    + ChatColor.WHITE + this.cloudManager.getOnlinePlayers()
            );

            lobbyBoard.setLine(2, ChatColor.BOLD + "");
            lobbyBoard.setLine(1, ChatColor.YELLOW + "www.seocraft.net");
            lobbyBoard.apply(scoreboardPlayer);
        } else {
            this.cancel();
        }
    }
}
