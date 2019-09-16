package net.seocraft.commons.core.user;

import net.seocraft.api.core.user.User;
import net.seocraft.api.core.user.UserExpulsion;
import org.jetbrains.annotations.NotNull;

import java.beans.ConstructorProperties;

public class PlayerExpulsion implements UserExpulsion {

     @NotNull private User user;
     @NotNull private String reason;
     private long expiration;
     private boolean permanent;

     @ConstructorProperties({"user", "reason", "expiration", "permanent"})
     public PlayerExpulsion(@NotNull User user, @NotNull String reason, long expiration, boolean permanent) {
          this.user = user;
          this.reason = reason;
          this.expiration = expiration;
          this.permanent = permanent;
     }

     @Override
     public @NotNull User getUser() {
          return this.user;
     }

     @Override
     public @NotNull String getReason() {
          return this.reason;
     }

     @Override
     public long getExpiration() {
          return this.expiration;
     }

     @Override
     public boolean isPermanent() {
          return this.permanent;
     }
}
