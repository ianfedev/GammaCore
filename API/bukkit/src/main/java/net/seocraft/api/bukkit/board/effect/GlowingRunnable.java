package net.seocraft.api.bukkit.board.effect;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

public class GlowingRunnable implements Runnable {

    private @NotNull Objective objective;
    private @NotNull String title;
    private int counter;
    private int tilting;

    public GlowingRunnable(@NotNull Objective objective, @NotNull String title) {
        this.objective = objective;
        this.title = title;
        this.counter = 0;
        this.tilting  = 0;
    }

    @Override
    public void run() {
        if (counter < this.title.length()) {
            String whitePart = ChatColor.WHITE + "" + ChatColor.BOLD + this.title.substring(0, counter);
            String yellowPart = ChatColor.YELLOW + "" + ChatColor.BOLD + this.title.charAt(counter);
            String orangePart = counter < (this.title.length() - 1) ? ChatColor.GOLD + "" + ChatColor.BOLD + this.title.substring((counter + 1)) : "";
            this.objective.setDisplayName(whitePart + yellowPart + orangePart);
            counter++;
        }  else {

            if (tilting % 2 == 0) {
                this.objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + this.title);
            } else {
                this.objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + this.title);
            }

            if (this.tilting == 4) {
                this.tilting = 0;
                this.counter = 0;
            } else {
                this.tilting++;
            }
        }
    }

}
