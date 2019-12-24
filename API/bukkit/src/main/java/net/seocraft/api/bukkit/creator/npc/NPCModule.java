package net.seocraft.api.bukkit.creator.npc;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.creator.npc.navigation.Navigator;
import net.seocraft.api.bukkit.creator.skin.CraftSkinHandler;
import net.seocraft.api.bukkit.creator.skin.SkinHandler;
import net.seocraft.api.bukkit.creator.v_1_8_R3.npc.navigation.CraftNavigator_v1_8_R3;

public class NPCModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(SkinHandler.class).to(CraftSkinHandler.class).in(Scopes.SINGLETON);
        bind(Navigator.class).to(CraftNavigator_v1_8_R3.class).in(Scopes.SINGLETON);
        bind(NPCManager.class).to(SimpleNPCManager.class).in(Scopes.SINGLETON);
        expose(NPCManager.class);
    }

}