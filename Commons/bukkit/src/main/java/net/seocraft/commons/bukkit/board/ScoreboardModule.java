package net.seocraft.commons.bukkit.board;

import me.fixeddev.inject.ProtectedModule;
import net.seocraft.api.bukkit.creator.board.ScoreboardApplier;
import net.seocraft.api.bukkit.creator.board.ScoreboardManager;
import net.seocraft.api.bukkit.creator.board.ScoreboardRemover;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineCreator;
import net.seocraft.api.bukkit.creator.board.line.ScoreboardLineRemover;
import net.seocraft.commons.bukkit.board.line.SimpleScoreboardLineCreator;
import net.seocraft.commons.bukkit.board.line.SimpleScoreboardLineRemover;

public class ScoreboardModule extends ProtectedModule {

    @Override
    protected void configure() {
        bind(ScoreboardManager.class).to(CraftScoreboardManager.class);
        bind(ScoreboardApplier.class).to(SimpleScoreboardApplier.class);
        bind(ScoreboardRemover.class).to(SimpleScoreboardRemover.class);
        bind(ScoreboardLineCreator.class).to(SimpleScoreboardLineCreator.class);
        bind(ScoreboardLineRemover.class).to(SimpleScoreboardLineRemover.class);
        expose(ScoreboardManager.class);
    }

}