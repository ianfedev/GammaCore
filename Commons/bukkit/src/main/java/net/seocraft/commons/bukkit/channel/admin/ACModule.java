package net.seocraft.commons.bukkit.channel.admin;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.channel.admin.*;

public class ACModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(ACMentionParser.class).to(GammaACMentionParser.class).in(Scopes.SINGLETON);
        bind(ACMessageManager.class).to(GammaACMessageManager.class).in(Scopes.SINGLETON);
        bind(ACParticipantsProvider.class).to(GammaACParticipantsProvider.class).in(Scopes.SINGLETON);
        bind(ACBroadcaster.class).to(GammaACBroadcaster.class).in(Scopes.SINGLETON);
        bind(ACPunishmentBroadcaster.class).to(GammaACPunishmentBroadcaster.class).in(Scopes.SINGLETON);
        bind(ACLoginBroadcaster.class).to(GammaACLoginBroadcaster.class).in(Scopes.SINGLETON);
        expose(ACMentionParser.class);
        expose(ACMessageManager.class);
        expose(ACParticipantsProvider.class);
        expose(ACBroadcaster.class);
        expose(ACLoginBroadcaster.class);
    }

}
