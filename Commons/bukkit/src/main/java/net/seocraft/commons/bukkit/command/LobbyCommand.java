package net.seocraft.commons.bukkit.command;

import com.google.inject.Inject;
import me.fixeddev.ebcm.bukkit.parameter.provider.annotation.Sender;
import me.fixeddev.ebcm.parametric.CommandClass;
import me.fixeddev.ebcm.parametric.annotation.ACommand;
import me.fixeddev.ebcm.parametric.annotation.Injected;
import net.seocraft.api.bukkit.cloud.CloudManager;
import net.seocraft.api.bukkit.game.management.CoreGameManagement;
import net.seocraft.api.core.server.ServerType;
import net.seocraft.commons.bukkit.CommonsBukkit;
import org.bukkit.entity.Player;

public class LobbyCommand implements CommandClass {

    @Inject private CommonsBukkit instance;
    @Inject private CloudManager cloudManager;
    @Inject private CoreGameManagement coreGameManagement;

    @ACommand(names = {"lobby", "l", "hub"})
    public boolean mainCommand(@Injected(true) @Sender Player commandSender) {
        String game = "main_lobby";
        if (this.instance.getServerRecord().getServerType() == ServerType.GAME)
            game = this.coreGameManagement.getGamemode().getLobbyGroup();
        this.cloudManager.sendPlayerToGroup(commandSender, game);
        return true;
    }
}
