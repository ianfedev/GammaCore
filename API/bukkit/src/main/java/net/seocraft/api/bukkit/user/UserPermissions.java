package net.seocraft.api.bukkit.user;

import com.google.inject.Inject;
import net.seocraft.api.shared.models.Group;
import net.seocraft.api.shared.models.User;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;


public class UserPermissions extends PermissibleBase {

    private UserStore userStore;
    private Player player;


    public UserPermissions(Player player, UserStore userStore) {
        super(player);
        this.userStore = userStore;
        this.player = player;
    }

    public boolean hasPermission(String s) {
        User user = this.userStore.getUserObjectSync(player.getUniqueId());
        for (Group group: user.getGroups()) {
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
        return false;
    }

    public boolean hasPermission(Permission p) {
        return hasPermission(p.getName());
    }

}
