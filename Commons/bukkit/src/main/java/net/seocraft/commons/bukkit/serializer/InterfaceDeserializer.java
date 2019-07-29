package net.seocraft.commons.bukkit.serializer;


import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.map.partial.Contribution;
import net.seocraft.api.bukkit.game.map.partial.Rating;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.party.Party;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.core.friend.Friendship;
import net.seocraft.commons.bukkit.friend.UserFriendship;
import net.seocraft.commons.bukkit.game.gamemode.CoreGamemode;
import net.seocraft.commons.bukkit.game.gamemode.CoreSubGamemode;
import net.seocraft.commons.bukkit.game.map.CoreMap;
import net.seocraft.commons.bukkit.game.map.CraftMapConfiguration;
import net.seocraft.commons.bukkit.game.map.partial.MapContribution;
import net.seocraft.commons.bukkit.game.map.partial.MapRating;
import net.seocraft.commons.bukkit.game.match.GameMatch;
import net.seocraft.commons.bukkit.game.party.GameParty;
import net.seocraft.commons.bukkit.punishment.UserPunishment;
import net.seocraft.commons.core.serializer.CoreResolver;

public class InterfaceDeserializer {

    public static SimpleModule getAbstractTypes() {
        SimpleModule module = new SimpleModule("InterfaceDeserializerModule", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = CoreResolver.getCoreResolver();
        resolver.addMapping(Friendship.class, UserFriendship.class)
                .addMapping(Gamemode.class, CoreGamemode.class)
                .addMapping(SubGamemode.class, CoreSubGamemode.class)
                .addMapping(Contribution.class, MapContribution.class)
                .addMapping(Rating.class, MapRating.class)
                .addMapping(GameMap.class, CoreMap.class)
                .addMapping(BaseMapConfiguration.class, CraftMapConfiguration.class)
                .addMapping(Match.class, GameMatch.class)
                .addMapping(Party.class, GameParty.class)
                .addMapping(Punishment.class, UserPunishment.class);
        module.setAbstractTypes(resolver);
        return module;
    }

}
