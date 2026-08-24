package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.Getter;

public enum ContentType {
    WALL("wall"),
    BUSH("bush"),
    HEDGEHOG("hedgehog"),
    EXPLOSIVE_BARREL("explosive_barrel"),
    EMPTY("empty"),
    UNKNOWN("unknown");

    private @Getter String id;

    ContentType(String id) {
        this.id = id;
    }

    public static ContentType fromId(String id) {
        for (ContentType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
