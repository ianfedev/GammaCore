package net.seocraft.api.bukkit.utils;

public enum ChatGlyphs {
    SEPARATOR("-----------------------------------------------------");

    private String content;

    ChatGlyphs(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
