package net.seocraft.commons.bukkit.user;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.http.exceptions.BadRequest;
import net.seocraft.api.shared.http.exceptions.InternalServerError;
import net.seocraft.api.shared.http.exceptions.NotFound;
import net.seocraft.api.shared.http.exceptions.Unauthorized;
import net.seocraft.api.shared.models.Group;
import net.seocraft.api.shared.models.User;
import net.seocraft.commons.bukkit.CommonsBukkit;
import net.seocraft.commons.bukkit.utils.ChatAlertLibrary;
import net.seocraft.commons.core.translations.TranslatableField;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;


public class UserPermissions extends PermissibleBase {

    @Inject private UserStoreHandler userStoreHandler;
    @Inject private CommonsBukkit instance;
    @Inject private TranslatableField translatableField;
    private Player player;
    private User user;


    UserPermissions(Player player, User user) {
        super(player);
        this.player = player;
        this.user = user;
    }

    public boolean hasPermission(String s) {
        try {
            User newUser = this.userStoreHandler.getCachedUserSync(this.instance.playerIdentifier.get(player.getUniqueId()));
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
        } catch (Unauthorized | BadRequest | NotFound | InternalServerError unauthorized) {
            ChatAlertLibrary.errorChatAlert(
                    player,
                    this.translatableField.getField(user.getLanguage(), "commons_permissions_error")
            );
        }
        return false;
    }

    public boolean hasPermission(Permission p) {
        return hasPermission(p.getName());
    }

}
