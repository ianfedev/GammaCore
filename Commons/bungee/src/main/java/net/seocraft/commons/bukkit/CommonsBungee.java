package net.seocraft.commons.bukkit;

import net.md_5.bungee.api.plugin.Plugin;


public class CommonsBungee extends Plugin {

    private static CommonsBungee instance;

    public static CommonsBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
    }


}
