package main.java.io.quicksiiver.traction.core.tiles.types;

import java.util.ArrayList;

import main.java.io.quicksiiver.traction.core.tiles.Tile;

public class DriveableTile extends Tile {
    private final double FRICTION;
    private final double ACCELERATION;
    private final double TURNING;

    // list of all of them
    private static ArrayList<DriveableTile> tiles = new ArrayList<>();

    // types of DriveableTile s
    public static final DriveableTile DEFAULT = new DriveableTile("default.png", 0.8, 1, 1);
    public static final DriveableTile ICE = new DriveableTile("ice.png", 0.2, 1, 5);

    protected DriveableTile(String id, double friction, double acceleration, double turning) {
        super(id);

        FRICTION = friction;
        ACCELERATION = acceleration;
        TURNING = turning;

        tiles.add(this);
    }

    // getters
    public double getFriction() { return FRICTION; }
    public double getAcceleration() { return ACCELERATION; }
    public double getTurning() { return TURNING; }
    public static DriveableTile[] getAllDriveableTiles() { return tiles.toArray(new DriveableTile[0]); }
    public static ArrayList<DriveableTile> getAllRegularDriveableTiles() {
        ArrayList<DriveableTile> allTiles = new ArrayList<>(tiles); // get all of them (cloning)

        // remove special tiles
        for (DriveableTile tile : allTiles) {
            if (tile instanceof Checkpoint) { allTiles.remove(tile); }
        }

        // return it
        return allTiles;
    }

}
