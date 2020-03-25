package net.seocraft.api.core.user.partial;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PublicInfo {

    @Nullable String getEmail();

    void setEmail(@NotNull String email);

    @Nullable String getTwitter();

    void setTwitter(@NotNull String twitter);

    @Nullable String getReddit();

    void setReddit(@NotNull String reddit);

    @Nullable String getSteam();

    void setSteam(@NotNull String steam);

    @Nullable String getTwitch();

    void setTwitch(@NotNull String twitch);

}
