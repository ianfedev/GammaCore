package net.seocraft.commons.bukkit.friend;

import net.seocraft.api.bukkit.user.UserStoreHandler;
import net.seocraft.api.shared.concurrent.CallbackWrapper;
import net.seocraft.api.shared.http.AsyncResponse;
import net.seocraft.api.shared.redis.ChannelListener;

public class FriendshipListener implements ChannelListener<Friendship> {

    private FriendshipUserActions friendshipActions;
    private UserStoreHandler userStoreHandler;

    FriendshipListener (UserStoreHandler userStoreHandler, FriendshipUserActions friendshipActions) {
        this.userStoreHandler = userStoreHandler;
        this.friendshipActions = friendshipActions;
    }

    @Override
    public void receiveMessage(Friendship object) {
        CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(object.getReceiver()), senderRecord -> {
            if (senderRecord.getStatus() == AsyncResponse.Status.SUCCESS) {
                CallbackWrapper.addCallback(this.userStoreHandler.getCachedUser(object.getReceiver()), receiverRecord -> {
                    if (receiverRecord.getStatus() == AsyncResponse.Status.SUCCESS) {
                        this.friendshipActions.receiverAction(
                                senderRecord.getResponse(),
                                receiverRecord.getResponse(),
                                object.getAction()
                        );
                    }
                });
            }
        });

    }

}
