package net.seocraft.commons.core.user;

import com.google.inject.Inject;
import net.seocraft.api.core.http.exceptions.BadRequest;
import net.seocraft.api.core.http.exceptions.InternalServerError;
import net.seocraft.api.core.http.exceptions.NotFound;
import net.seocraft.api.core.http.exceptions.Unauthorized;
import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserPermissionChecker;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.user.partial.GroupAssignation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GammaUserPermissionChecker implements UserPermissionChecker {

    @Inject private UserStorageProvider userStorageProvider;

    @Override
    public boolean hasPermission(@NotNull String id, @NotNull String s) throws Unauthorized, IOException, BadRequest, NotFound, InternalServerError {

        User user = this.userStorageProvider.getCachedUserSync(id);

        Set<String> userPermissions = getFlattenPermissions(user);

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
        return false;
    }

    private Set<String> getFlattenPermissions(User user) {

        Set<String> permission = new HashSet<>();

        for (GroupAssignation group : user.getGroupAssignation()) {
            permission.addAll(group.getGroup().getPermissions());
        }

        return permission;
    }

}
