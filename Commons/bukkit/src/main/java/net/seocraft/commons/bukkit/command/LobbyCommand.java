package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand implements CommandClass {

    @Inject private CommonsBukkit instance;
    @Inject private CloudManager cloudManager;
    @Inject private CoreGameManagement coreGameManagement;

    @Command(names = {"lobby", "l", "hub"})
    public boolean mainCommand(CommandSender commandSender) {
        String game = "main_lobby";
        if (this.instance.getServerRecord().getServerType() == ServerType.GAME)
            game = this.coreGameManagement.getGamemode().getLobbyGroup();
        this.cloudManager.sendPlayerToGroup((Player) commandSender, game);
        return true;
    }
}
