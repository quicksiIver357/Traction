package main.java.io.quicksiiver.traction.core.tiles;

import java.nio.file.Path;

public class DriveableTile extends Tile {
    private final double FRICTION;

    // types of DriveableTile s
    public static final DriveableTile DEFAULT = new DriveableTile("default.png", 0.8);

    protected DriveableTile(String filename, double friction) {
        super(Tile.TILES_FOLDER + filename);

        this.FRICTION = friction;
    }

    public DriveableTile copy() { return new DriveableTile(TILES_FOLDER, FRICTION); }
}
