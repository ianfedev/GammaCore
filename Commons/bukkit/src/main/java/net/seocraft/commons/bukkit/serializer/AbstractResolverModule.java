package net.seocraft.commons.bukkit.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.seocraft.api.bukkit.game.gamemode.Gamemode;
import net.seocraft.api.bukkit.game.gamemode.SubGamemode;
import net.seocraft.api.bukkit.game.management.FinderResult;
import net.seocraft.api.bukkit.game.map.BaseMapConfiguration;
import net.seocraft.api.bukkit.game.map.GameMap;
import net.seocraft.api.bukkit.game.map.partial.Contribution;
import net.seocraft.api.bukkit.game.map.partial.MapCoordinates;
import net.seocraft.api.bukkit.game.map.partial.Rating;
import net.seocraft.api.bukkit.game.match.Match;
import net.seocraft.api.bukkit.game.match.partial.GameTeam;
import net.seocraft.api.bukkit.game.match.partial.GameTeamMember;
import net.seocraft.api.bukkit.game.match.partial.Team;
import net.seocraft.api.bukkit.game.match.partial.TeamMember;
import net.seocraft.api.bukkit.game.party.Party;
import net.seocraft.api.bukkit.punishment.Punishment;
import net.seocraft.api.bukkit.stats.Stats;
import net.seocraft.api.bukkit.stats.games.SkyWarsStats;
import net.seocraft.api.bukkit.stats.games.TNTGamesStats;
import net.seocraft.api.bukkit.stats.games.dungeon.DungeonKit;
import net.seocraft.api.bukkit.stats.games.dungeon.DungeonStats;
import net.seocraft.api.bukkit.stats.games.dungeon.partial.DungeonEnchantment;
import net.seocraft.api.bukkit.stats.games.dungeon.partial.GameDungeonEnchantment;
import net.seocraft.api.core.friend.Friendship;
import net.seocraft.commons.bukkit.friend.UserFriendship;
import net.seocraft.commons.bukkit.game.gamemode.CoreGamemode;
import net.seocraft.commons.bukkit.game.gamemode.CoreSubGamemode;
import net.seocraft.commons.bukkit.game.management.GameResult;
import net.seocraft.commons.bukkit.game.map.CoreMap;
import net.seocraft.commons.bukkit.game.map.CraftMapConfiguration;
import net.seocraft.commons.bukkit.game.map.partial.MapContribution;
import net.seocraft.commons.bukkit.game.map.partial.MapRating;
import net.seocraft.commons.bukkit.game.map.partial.PartialCoordinates;
import net.seocraft.commons.bukkit.game.match.GameMatch;
import net.seocraft.commons.bukkit.game.party.GameParty;
import net.seocraft.commons.bukkit.punishment.UserPunishment;
import net.seocraft.commons.bukkit.stats.GameStats;
import net.seocraft.commons.bukkit.stats.games.GameSkyWarsStats;
import net.seocraft.commons.bukkit.stats.games.GameTNTGamesStats;
import net.seocraft.commons.bukkit.stats.games.dungeon.GameDungeonKit;
import net.seocraft.commons.bukkit.stats.games.dungeon.GameDungeonStats;
import net.seocraft.commons.core.serializer.CoreResolver;

public class AbstractResolverModule {

    public static SimpleModule getAbstractTypes() {
        SimpleModule module = new SimpleModule("AbstractResolverModule", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = CoreResolver.getCoreResolver();
        resolver.addMapping(Friendship.class, UserFriendship.class)
                .addMapping(Gamemode.class, CoreGamemode.class)
                .addMapping(SubGamemode.class, CoreSubGamemode.class)
                .addMapping(Contribution.class, MapContribution.class)
                .addMapping(Rating.class, MapRating.class)
                .addMapping(MapCoordinates.class, PartialCoordinates.class)
                .addMapping(GameMap.class, CoreMap.class)
                .addMapping(BaseMapConfiguration.class, CraftMapConfiguration.class)
                .addMapping(Team.class, GameTeam.class)
                .addMapping(TeamMember.class, GameTeamMember.class)
                .addMapping(FinderResult.class, GameResult.class)
                .addMapping(Match.class, GameMatch.class)
                .addMapping(Party.class, GameParty.class)
                .addMapping(Stats.class, GameStats.class)
                .addMapping(DungeonEnchantment.class, GameDungeonEnchantment.class)
                .addMapping(DungeonKit.class, GameDungeonKit.class)
                .addMapping(DungeonStats.class, GameDungeonStats.class)
                .addMapping(SkyWarsStats.class, GameSkyWarsStats.class)
                .addMapping(TNTGamesStats.class, GameTNTGamesStats.class)
                .addMapping(Punishment.class, UserPunishment.class);
        module.setAbstractTypes(resolver);
        return module;
    }

}
