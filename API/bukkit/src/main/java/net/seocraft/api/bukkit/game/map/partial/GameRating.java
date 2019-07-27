package net.seocraft.api.bukkit.game.map.partial;

public enum GameRating {
    ONE, TWO, THREE, FOUR, FIVE;

    public static int getNumber(GameRating rating) {
        switch (rating) {
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            default:
                return 1;
        }
    }
}
