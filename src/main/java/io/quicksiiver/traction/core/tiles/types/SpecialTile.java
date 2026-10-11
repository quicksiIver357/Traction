package main.java.io.quicksiiver.traction.core.tiles.types;

import java.util.ArrayList;

public abstract class SpecialTile extends DriveableTile {
    private static ArrayList<SpecialTile> tiles = new ArrayList<>();
    protected SpecialTile(String id, double friction) { 
        super(id, 0);
        tiles.add(this); 
    }
    @SuppressWarnings("unchecked")
    public ArrayList<SpecialTile> getAllSpecialTiles() { return (ArrayList<SpecialTile>) tiles.clone(); }
}
