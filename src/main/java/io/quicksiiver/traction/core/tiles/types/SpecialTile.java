package main.java.io.quicksiiver.traction.core.tiles.types;

import java.util.ArrayList;

public class SpecialTile extends DriveableTile {
    private static ArrayList<SpecialTile> tiles = new ArrayList<>();

    // types of SpecialTile s
    public static final SpecialTile START = new SpecialTile("start.png", 1, 2, 1);
    public static final SpecialTile END = new SpecialTile("end.png", 0, 0, 0);

    protected SpecialTile(String id, double friction, double acceleration, double turning) { 
        super(id, friction, acceleration, turning);
        if (!(this instanceof Checkpoint)) { tiles.add(this); } 
    }
    public ArrayList<SpecialTile> getAllSpecialTiles() { return new ArrayList<>(tiles); }
}
