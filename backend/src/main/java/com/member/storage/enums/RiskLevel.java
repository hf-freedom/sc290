package com.member.storage.enums;

public enum RiskLevel {
    NORMAL("正常", 0),
    ATTENTION("关注", 1),
    RESTRICTED("限制", 2);

    private final String description;
    private final int level;

    RiskLevel(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }
}
