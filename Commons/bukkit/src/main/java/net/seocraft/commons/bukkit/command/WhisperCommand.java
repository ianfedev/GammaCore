package net.seocraft.commons.bukkit.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.parametric.CommandClass;
import me.fixeddev.bcm.parametric.annotation.Command;
import me.fixeddev.bcm.parametric.annotation.JoinedString;
import net.seocraft.api.bukkit.whisper.WhisperManager;
import net.seocraft.api.bukkit.whisper.WhisperResponse;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.redis.messager.Channel;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

public class WhisperCommand implements CommandClass {

    @Inject private WhisperManager whisperManager;
    @Inject private ObjectMapper mapper;
    @Inject private UserStorageProvider userStorageProvider;
    @Inject private TranslatableField translator;

    private Channel<String> messager;

    @Command(names = {"msg", "whisper", "tell", "w", "m", "t"}, min = 2, usage = "/<command> <target> <message>")
    public boolean whisperCommand(CommandSender commandSender, OfflinePlayer target, @JoinedString String message) {

        if (commandSender instanceof Player) {
            Player sender = (Player) commandSender;

            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(sender.getDatabaseIdentifier()), asyncUserSender -> {

                if (asyncUserSender.getStatus() != AsyncResponse.Status.SUCCESS) {
                    ChatAlertLibrary.errorChatAlert(sender);
                    return;
                }

                User userSender = asyncUserSender.getResponse();

                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(((Player) target).getDatabaseIdentifier()), asyncTargetUser -> {
                    if (asyncTargetUser.getStatus() == AsyncResponse.Status.SUCCESS) {
                        User targetUser = asyncTargetUser.getResponse();
                        CallbackWrapper.addCallback(whisperManager.sendMessage(userSender, targetUser, message), response -> {
                            if (response.getResponse() == null) {
                                ChatAlertLibrary.infoAlert(sender,
                                        this.translator.getUnspacedField(
                                                userSender.getLanguage(),
                                                "commons_message_nulled"
                                        ));
                                return;
                            }

                            if (response.getResponse() == WhisperResponse.Response.PLAYER_OFFLINE) {
                                ChatAlertLibrary.infoAlert(sender,
                                        this.translator.getUnspacedField(
                                                userSender.getLanguage(),
                                                "commons_player_offline"
                                        ));
                                return;
                            }

                            if (response.getResponse() == WhisperResponse.Response.ERROR) {
                                ChatAlertLibrary.errorChatAlert(sender,
                                        this.translator.getUnspacedField(
                                                userSender.getLanguage(),
                                                "commons_system_error"
                                        ));
                                Bukkit.getLogger().log(Level.SEVERE, "An error ocurred while executing the whisper command", response.getThrowedException());
                            }
                        });
                    } else {
                        ChatAlertLibrary.errorChatAlert(sender);
                    }
                });
            });
        }
        return true;
    }

    private @NotNull String getPlayerIP(@NotNull Player player) {
        return player.getAddress().toString().split(":")[0].replace("/", "");
    }

    @Command(names = {"testMessager"}, max = 0)
    public boolean testMessager() {
        messager.sendMessage("test");
        Bukkit.broadcastMessage("Send message test");
        return true;
    }

    @Command(names = {"test"})
    public boolean testCommand(CommandSender commandSender, CommandContext context) {
        try {
            ItemStack stack = new ItemStack(Material.DIAMOND_AXE, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Poppper");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
            stack.setItemMeta(meta);
            ItemStack readed = this.mapper.readValue(this.mapper.writeValueAsString(stack), ItemStack.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}