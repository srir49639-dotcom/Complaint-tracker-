package model;

public enum EscalationLevel {
    LEVEL_0("Level 0 - Standard", 0),
    LEVEL_1("Level 1 - Department Lead", 1),
    LEVEL_2("Level 2 - Operations Head", 2),
    LEVEL_3("Level 3 - Executive Director", 3);

    private final String description;
    private final int level;

    EscalationLevel(String description, int level) {
        this.description = description;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public static EscalationLevel fromInt(int lvl) {
        for (EscalationLevel e : values()) {
            if (e.level == lvl) return e;
        }
        return LEVEL_0;
    }
}
