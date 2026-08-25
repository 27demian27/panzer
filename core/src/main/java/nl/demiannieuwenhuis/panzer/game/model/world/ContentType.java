package nl.demiannieuwenhuis.panzer.game.model.world;

import lombok.Getter;

public enum ContentType {
    WALL("wall"),
    BUSH("bush"),
    HEDGEHOG("hedgehog"),
    EXPLOSIVE_BARREL("explosive_barrel"),
    SPAWN_POINT_A("spawn_point_a"),
    SPAWN_POINT_B("spawn_point_b"),
    SPAWN_POINT_C("spawn_point_c"),
    SPAWN_POINT_D("spawn_point_d"),
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

    public boolean isSpawnPoint() {
        return (
                this.equals(SPAWN_POINT_A) ||
                this.equals(SPAWN_POINT_B) ||
                this.equals(SPAWN_POINT_C) ||
                this.equals(SPAWN_POINT_D)
            );
    }
}
