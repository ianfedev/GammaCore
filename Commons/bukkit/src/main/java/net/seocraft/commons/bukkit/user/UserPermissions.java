package net.seocraft.commons.bukkit.user;

import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.model.Group;
import net.seocraft.api.shared.model.User;
import net.seocraft.api.shared.session.GameSession;
import net.seocraft.api.shared.session.SessionHandler;
import net.seocraft.commons.bukkit.util.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;


public class UserPermissions extends PermissibleBase {

    private UserStoreHandler userStoreHandler;
    private SessionHandler sessionHandler;
    private TranslatableField translatableField;
    private Player player;
    private User user;


    UserPermissions(Player player, User user, UserStoreHandler userStoreHandler, SessionHandler sessionHandler, TranslatableField translatableField) {
        super(player);
        this.player = player;
        this.userStoreHandler = userStoreHandler;
        this.sessionHandler = sessionHandler;
        this.translatableField = translatableField;
        this.user = user;
    }

    public boolean hasPermission(String s) {
        GameSession session = this.sessionHandler.getCachedSession(player.getName());
        try {
            if (session != null) {
                User newUser = this.userStoreHandler.getCachedUserSync(session.getPlayerId());
                for (Group group: newUser.getGroups()) {
                    for (String permission: group.getPermissions()) {
                        if (permission.equalsIgnoreCase(s)) return true;
                        String[] requestedTree = s.split("\\.");
                        int scanningLength = requestedTree.length;
                        String[] permissionTree = permission.split("\\.");
                        if (permissionTree.length < scanningLength) scanningLength = permissionTree.length;
                        for (int i = 0; i < scanningLength; i++) {
                            if (permissionTree[i].equalsIgnoreCase("*")) return true;
                            if (!requestedTree[i].equalsIgnoreCase(permissionTree[i])) break;
                        }
                    }
                }
            }
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    this.translatableField.getField(user.getLanguage(), "commons_permissions_error") + "."
            );
        }
        return false;
    }

}
