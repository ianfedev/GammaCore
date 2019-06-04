package net.seocraft.commons.bukkit.punishment;

import com.google.inject.Inject;
import net.seocraft.api.bukkit.user.UserStore;

public class TestPunishment {

    @Inject private UserStore userStorage;
    @Inject private PunishmentHandler punishmentHandler;

    public void testPunishment() {
    }
}
