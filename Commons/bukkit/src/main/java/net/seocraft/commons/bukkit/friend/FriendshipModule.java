package net.seocraft.commons.bukkit.friend;

import com.google.inject.AbstractModule;
import net.seocraft.api.core.friend.FriendshipProvider;

public class FriendshipModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FriendshipProvider.class).to(UserFriendshipProvider.class);
    }
}
