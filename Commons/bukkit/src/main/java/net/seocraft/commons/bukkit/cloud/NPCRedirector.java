package net.seocraft.commons.bukkit.cloud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.cloud.ServerRedirector;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.management.MatchFinder;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.redis.RedisClient;
import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.event.CustomSelectionEvent;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class NPCRedirector implements ServerRedirector {

    @Inject private GameSessionManager gameSessionManager;
    @Inject private CommonsBukkit instance;
    @Inject private CloudManager cloudManager;
    @Inject private ObjectMapper mapper;
    @Inject private MatchFinder matchFinder;
    @Inject private RedisClient client;

    public void redirectPlayer(@NotNull Gamemode gamemode, @Nullable SubGamemode subGamemode, @NotNull Player player, boolean perk) {
        if (this.instance.hasCloudDeploy()) {
            if (subGamemode == null) {
                this.cloudManager.sendPlayerToGroup(
                        player,
                        gamemode.getLobbyGroup()
                );
            } else {
                if (subGamemode.canSelectMap()) {
                    Bukkit.getPluginManager().callEvent(new CustomSelectionEvent(player, gamemode, subGamemode, perk));
                } else {
                    FinderResult result;
                    try {
                        result = this.matchFinder.findAvailableMatch(gamemode.getId(), subGamemode.getId(), subGamemode.getServerGroup(), false);
                        String finderResult = this.mapper.writeValueAsString(result);
                        this.client.setString(
                                "pairing:" + player.getDatabaseIdentifier(),
                                finderResult
                        );
                        this.client.setExpiration("pairing" + player.getDatabaseIdentifier(), 60);
                        this.cloudManager.sendPlayerToServer(player, result.getServer().getSlug());
                    } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException ex) {
                        ChatAlertLibrary.errorChatAlert(player, "Error pairing game, please try again.");
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "This server is not deployed in a cloud. Request denied.");
        }
    }
}
