package net.seocraft.commons.bukkit.channel.admin;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.channel.admin.ACMentionParser;
import net.seocraft.api.bukkit.channel.admin.ACMessageManager;

public class ACModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(ACMentionParser.class).to(GammaACMentionParser.class);
        bind(ACMessageManager.class).to(GammaACMessageManager.class);
        expose(ACMentionParser.class);
        expose(ACMessageManager.class);
    }

}
