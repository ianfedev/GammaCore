package net.seocraft.commons.bukkit.friend;

import net.seocraft.api.core.friend.Friendship;
import net.seocraft.api.core.user.UserStorageProvider;
import net.seocraft.api.core.concurrent.CallbackWrapper;
import net.seocraft.api.core.concurrent.AsyncResponse;
import net.seocraft.api.core.redis.messager.ChannelListener;

public class FriendshipListener implements ChannelListener<Friendship> {

    private FriendshipUserActions friendshipActions;
    private UserStorageProvider userStorageProvider;

    FriendshipListener (UserStorageProvider userStorageProvider, FriendshipUserActions friendshipActions) {
        this.userStorageProvider = userStorageProvider;
        this.friendshipActions = friendshipActions;
    }

    @Override
    public void receiveMessage(Friendship object) {
        CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(object.getReceiver()), senderRecord -> {
            if (senderRecord.getStatus() == AsyncResponse.Status.SUCCESS) {
                CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(object.getReceiver()), receiverRecord -> {
                    if (receiverRecord.getStatus() == AsyncResponse.Status.SUCCESS) {
                        if (object.getIssuer() != null) {
                            CallbackWrapper.addCallback(this.userStorageProvider.getCachedUser(object.getReceiver()), issuerRecord -> {
                                if (issuerRecord.getStatus() == AsyncResponse.Status.SUCCESS) {
                                    this.friendshipActions.receiverAction(
                                            senderRecord.getResponse(),
                                            receiverRecord.getResponse(),
                                            object.getAction(),
                                            issuerRecord.getResponse()
                                    );
                                }
                            });
                        } else {
                            this.friendshipActions.receiverAction(
                                    senderRecord.getResponse(),
                                    receiverRecord.getResponse(),
                                    object.getAction(),
                                    null
                            );
                        }

                    }
                });
            }
        });

    }

}
