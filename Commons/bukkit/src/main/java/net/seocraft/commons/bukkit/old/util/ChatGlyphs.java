package net.seocraft.commons.bukkit.old.util;

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
