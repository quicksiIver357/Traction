package main.java.io.quicksiiver.traction.core.tiles.specific;

import java.util.ArrayList;

import main.java.io.quicksiiver.traction.core.tiles.Tile;

public class DriveableTile extends Tile {
    private final double FRICTION;

    // list of all of them
    private static ArrayList<DriveableTile> tiles = new ArrayList<>();

    // types of DriveableTile s
    public static final DriveableTile START = new DriveableTile("start.png", 1);
    public static final DriveableTile END = new DriveableTile("end.png", 0);
    public static final DriveableTile DEFAULT = new DriveableTile("default.png", 0.8);
    public static final DriveableTile ICE = new DriveableTile("ice.png", 0.1);

    protected DriveableTile(String filename, double friction) {
        super(Tile.TILES_FOLDER + filename);

        this.FRICTION = friction;

        tiles.add(this);
    }

    // getters
    public double getFriction() { return FRICTION; }
    @SuppressWarnings("unchecked")
    public static ArrayList<DriveableTile> getAllDriveableTiles() { 
        return (ArrayList<DriveableTile>) tiles.clone();
    }
}
