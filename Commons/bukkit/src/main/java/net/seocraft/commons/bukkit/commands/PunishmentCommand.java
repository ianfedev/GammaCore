package net.seocraft.commons.bukkit.commands;

import com.google.inject.Inject;
import me.ggamer55.bcm.CommandContext;
import me.ggamer55.bcm.parametric.CommandClass;
import me.ggamer55.bcm.parametric.annotation.Command;
import me.ggamer55.bcm.parametric.annotation.Parameter;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.models.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishmentCommand implements CommandClass {

    @Inject private CommonsBukkit instance;
    @Inject private TranslatableField translator;
    @Inject private UserStoreHandler userStoreHandler;

    @Command(names = {"ban", "tempban"}, permission = "commons.staff.ban", min = 1, usage = "/<command> <target> [duration] [reason] [-s]")
    public boolean banCommand(CommandSender sender, CommandContext context, OfflinePlayer target, @Parameter(value = "s", isFlag =  true) boolean silent) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(player.getUniqueId())), userAsyncResponse -> {
                if (userAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                    User user = userAsyncResponse.getResponse();

                    // Detecting if player is online
                    if (!target.isOnline()) {
                        ChatAlertLibrary.errorChatAlert(player, this.translator.getUnspacedField(
                                        user.getLanguage(), "commons_punish_offline".replace("%%url%%",
                                        ChatColor.YELLOW + "https://www.seocraft.net" + ChatColor.RED + "."
                                )));
                        return;
                    }

                    // Get online player data
                    CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(this.instance.playerIdentifier.get(target.getUniqueId())), targetAsyncResponse -> {
                        if (targetAsyncResponse.getStatus() == AsyncResponse.Status.SUCCESS) {
                            User targetRecord = targetAsyncResponse.getResponse();

                            // Check if user has lower priority
                            if (user.getPrimaryGroup().getPriority() < targetRecord.getPrimaryGroup().getPriority()) {

                            }
                        } else {
                            ChatAlertLibrary.errorChatAlert(
                                    player,
                                    this.translator.getUnspacedField(
                                            user.getLanguage(),
                                            "commons_punish_error" + "."
                                    )
                            );
                            return;
                        }
                    });

                } else {
                    ChatAlertLibrary.errorChatAlert(
                            player,
                            null
                    );
                    return;
                }
            });
        }
        return true;
    }
}
