package net.seocraft.commons.core.user.partial;

import net.seocraft.api.core.user.partial.PublicInfo;
import org.jetbrains.annotations.Nullable;

import java.beans.ConstructorProperties;

public class UserPublicInfo implements PublicInfo {

    @Nullable private String email;
    @Nullable private String twitter;
    @Nullable private String reddit;
    @Nullable private String steam;
    @Nullable private String twitch;

    @ConstructorProperties({
            "email",
            "twitter",
            "reddit",
            "steam",
            "twitch"
    })
    public UserPublicInfo(@Nullable String email, @Nullable String twitter, @Nullable String reddit, @Nullable String steam, @Nullable String twitch) {
        this.email = email;
        this.twitter = twitter;
        this.reddit = reddit;
        this.steam = steam;
        this.twitch = twitch;
    }

    @Override
    public @Nullable String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Override
    public @Nullable String getTwitter() {
        return this.twitter;
    }

    @Override
    public void setTwitter(@Nullable String twitter) {
        this.twitter = twitter;
    }

    @Override
    public @Nullable String getReddit() {
        return this.reddit;
    }

    @Override
    public void setReddit(@Nullable String reddit) {
        this.reddit = reddit;
    }

    @Override
    public @Nullable String getSteam() {
        return this.steam;
    }

    @Override
    public void setSteam(@Nullable String steam) {
        this.steam = steam;
    }

    @Override
    public @Nullable String getTwitch() {
        return this.twitch;
    }

    @Override
    public void setTwitch(@Nullable String twitch) {
        this.twitch = twitch;
    }
}
