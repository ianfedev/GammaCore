package net.seocraft.commons.bukkit.old.user;

import net.seocraft.api.core.session.GameSessionManager;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.session.GameSession;
import net.seocraft.api.core.user.User;
import net.seocraft.commons.bukkit.old.util.ChatAlertLibrary;
import net.seocraft.commons.core.translation.TranslatableField;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class UserPermissions extends PermissibleBase {

    private UserStorageProvider userStorageProvider;
    private GameSessionManager gameSessionManager;
    private TranslatableField translatableField;
    private Player player;
    private User user;
    private Logger logger;

    UserPermissions(Player player, User user, UserStorageProvider userStorageProvider, GameSessionManager gameSessionManager, TranslatableField translatableField, Logger logger) {
        super(player);
        this.player = player;
        this.userStorageProvider = userStorageProvider;
        this.gameSessionManager = gameSessionManager;
        this.translatableField = translatableField;
        this.user = user;
        this.logger = logger;
    }

    @Override
    public boolean hasPermission(String s) {
        GameSession session = this.gameSessionManager.getCachedSession(player.getName());
        try {
            User newUser = this.userStorageProvider.getCachedUserSync(session.getPlayerId());

            Set<String> userPermissions = getFlattenPermissions(newUser);

            if (userPermissions.contains(s)) {
                return true;
            }

            String[] requestedPermissionTree = s.split("\\.");

            for (String permission : userPermissions) {
                if (permission.equalsIgnoreCase(s)) {
                    return true;
                }

                int scanningLength = requestedPermissionTree.length;
                String[] permissionTree = permission.split("\\.");

                if (permissionTree.length < scanningLength) {
                    scanningLength = permissionTree.length;
                }

                for (int i = 0; i < scanningLength; i++) {
                    if (permissionTree[i].equalsIgnoreCase("*")) {
                        return true;
                    }

                    if (!requestedPermissionTree[i].equalsIgnoreCase(permissionTree[i])) {
                        break;
                    }
                }
            }

        } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    this.translatableField.getField(user.getLanguage(), "commons_permissions_error") + "."
            );

            logger.log(Level.SEVERE, "An exception ocurred while getting player " + player.getName() + " permissions", unauthorized);
        }
        return false;
    }

    private Set<String> getFlattenPermissions(User user) {
        return user.getGroups().stream().flatMap(group -> group.getPermissions().stream()).collect(Collectors.toSet());
    }

}
