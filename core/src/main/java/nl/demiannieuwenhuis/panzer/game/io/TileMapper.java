package nl.demiannieuwenhuis.panzer.game.io;

import nl.demiannieuwenhuis.panzer.game.model.world.ContentType;
import nl.demiannieuwenhuis.panzer.game.model.world.SurfaceType;
import nl.demiannieuwenhuis.panzer.game.model.world.Tile;

public class TileMapper {

    private TileMapper() {}

    public static TileData toData(Tile tile) {
        TileData data = new TileData();
        data.x = tile.getX();
        data.y = tile.getY();
        data.surfaceType = tile.getSurfaceType().getId();
        data.contentType = tile.getContentType().getId();
        data.subTilesTrackMarks = tile.subTilesTrackMarks;
        data.selected = tile.isSelected();
        return data;
    }

    public static Tile fromData(TileData data) {
        Tile tile = new Tile(
            data.x,
            data.y,
            SurfaceType.fromId(data.surfaceType),
            ContentType.fromId(data.contentType)
        );

        tile.subTilesTrackMarks = data.subTilesTrackMarks;
        tile.setSelected(data.selected);

        return tile;
    }
}
