package net.seocraft.commons.bukkit.user;

import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserPermissionChecker;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class UserPermissions extends PermissibleBase {

    private UserPermissionChecker userPermissionChecker;
    private TranslatableField translatableField;
    private Player player;
    private User user;

    UserPermissions(Player player, User user, UserPermissionChecker userPermissionChecker, TranslatableField translatableField) {
        super(player);
        this.player = player;
        this.userPermissionChecker = userPermissionChecker;
        this.translatableField = translatableField;
    }

    @Override
    public boolean hasPermission(String s) {
        try {
            return userPermissionChecker.hasPermission(player.getDatabaseIdentifier(), s);
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError | IOException unauthorized) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    this.translatableField.getField(user.getLanguage(), "commons_permissions_error") + "."
            );
            Bukkit.getLogger().log(Level.SEVERE, "An exception ocurred while getting player " + player.getName() + " permissions", unauthorized);
        }
        return false;
    }

}
