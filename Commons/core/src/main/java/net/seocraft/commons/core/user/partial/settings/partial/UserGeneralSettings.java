package net.seocraft.commons.core.user.partial.settings.partial;

import net.seocraft.api.core.user.partial.settings.partial.GeneralSettings;

import java.beans.ConstructorProperties;

public class UserGeneralSettings implements GeneralSettings {

    private boolean receivingGifts;
    private boolean acceptingFriends;
    private boolean acceptingParties;
    private boolean showingStatus;
    private boolean hidingPlayers;

    @ConstructorProperties({
            "gifts",
            "friends",
            "parties",
            "status",
            "hiding"
    })
    public UserGeneralSettings(boolean receivingGifts, boolean acceptingFriends, boolean acceptingParties, boolean showingStatus, boolean hidingPlayers) {
        this.receivingGifts = receivingGifts;
        this.acceptingFriends = acceptingFriends;
        this.acceptingParties = acceptingParties;
        this.showingStatus = showingStatus;
        this.hidingPlayers = hidingPlayers;
    }

    @Override
    public boolean isReceivingGifts() {
        return receivingGifts;
    }

    @Override
    public void setReceivingGifts(boolean receivingGifts) {
        this.receivingGifts = receivingGifts;
    }

    @Override
    public boolean isAcceptingFriends() {
        return acceptingFriends;
    }

    @Override
    public void setAcceptingFriends(boolean acceptingFriends) {
        this.acceptingFriends = acceptingFriends;
    }

    @Override
    public boolean isAcceptingParties() {
        return acceptingParties;
    }

    @Override
    public void setAcceptingParties(boolean acceptingParties) {
        this.acceptingParties = acceptingParties;
    }

    @Override
    public boolean isShowingStatus() {
        return this.showingStatus;
    }

    @Override
    public void setShowingStatus(boolean showingStatus) {
        this.showingStatus = showingStatus;
    }

    @Override
    public boolean isHidingPlayers() {
        return hidingPlayers;
    }

    @Override
    public void setHidingPlayers(boolean hidingPlayers) {
        this.hidingPlayers = hidingPlayers;
    }

}
