package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.GamemodeProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.logging.Level;

public class LobbyCommand implements CommandClass {

    @Inject private CommonsBukkit instance;
    @Inject private CloudManager cloudManager;
    @Inject private GamemodeProvider gamemodeProvider;

    @ACommand(names = {"lobby", "l", "hub"})
    public boolean mainCommand(@Injected(true) @Sender Player commandSender) {
        String game = "main_lobby";
        if (this.instance.getServerRecord().getServerType() == ServerType.GAME) {
            try {
                Gamemode gamemode = this.gamemodeProvider.getServerGamemode();
                if (gamemode != null) game = gamemode.getLobbyGroup();
            } catch (Unauthorized | InternalServerError | BadRequest | NotFound | IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "[Lobby] There was an error while redirecting to lobby", e);
            }
        }
        this.cloudManager.sendPlayerToGroup((Player) commandSender, game);
        return true;
    }
}
