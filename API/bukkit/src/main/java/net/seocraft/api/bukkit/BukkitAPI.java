package net.seocraft.api.bukkit;

import com.google.inject.Scopes;
import me.fixeddev.inject.ProtectedBinder;
import net.seocraft.api.bukkit.creator.intercept.CraftPacketManager;
import net.seocraft.api.bukkit.creator.intercept.PacketManager;
import net.seocraft.api.bukkit.creator.npc.NPCModule;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
    }

    @Override
    public void configure(ProtectedBinder binder) {
        binder.install(new NPCModule());
        binder.bind(PacketManager.class).to(CraftPacketManager.class).in(Scopes.SINGLETON);
        binder.expose(PacketManager.class);
        binder.publicBinder().bind(BukkitAPI.class).toInstance(this);
    }

}
