package net.seocraft.commons.bukkit.friend;

import com.google.inject.PrivateModule;
import net.seocraft.api.core.friend.FriendshipProvider;

public class FriendshipModule extends PrivateModule {
    @Override
    protected void configure() {
        bind(FriendshipProvider.class).to(UserFriendshipProvider.class);
    }
}
