package net.seocraft.api.core.user.partial.settings.partial;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface GeneralSettings {

    @JsonProperty("gifts")
    boolean isReceivingGifts();

    @JsonProperty("gifts")
    void setReceivingGifts(boolean receivingGifts);

    @JsonProperty("friends")
    boolean isAcceptingFriends();

    @JsonProperty("friends")
    void setAcceptingFriends(boolean acceptingFriends);

    @JsonProperty("parties")
    boolean isAcceptingParties();

    @JsonProperty("parties")
    void setAcceptingParties(boolean acceptingParties);

    @JsonProperty("status")
    boolean isShowingStatus();

    @JsonProperty("status")
    void setShowingStatus(boolean showingStatus);

    @JsonProperty("hiding")
    boolean isHidingPlayers();

    @JsonProperty("hiding")
    void setHidingPlayers(boolean hidingPlayers);

}
