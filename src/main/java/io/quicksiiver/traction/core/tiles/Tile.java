package main.java.io.quicksiiver.traction.core.tiles;

import java.util.ArrayList;

public abstract class Tile { 
    // instance variables
    private final String ID;

    // helper stuff for the paths of subclasses
    protected static final String TILES_FOLDER = "src/main/resources/assets/tile/";

    // store all of the created tiles
    private static ArrayList<Tile> tiles = new ArrayList<>();

    // constructors
    protected Tile(final String id) { 
        // verify path is not taken
        for (Tile tile : tiles) {
            if (tile.ID.equals(id)) { 
                throw new IllegalArgumentException("ERROR: duplicate path id for tile."); 
            }
        }

        // if its fine just set it
        ID = id; 

        tiles.add(this); // add it to the tiles array
    }

    // getters
    public String getPath() { return TILES_FOLDER + ID; }
    public String getName() { return ID; }
    public String toString() { return getName(); }
    public static Tile getTileFromName(String name) {
        for (Tile tile : tiles) {
            if (tile.ID.equals(name)) { return tile; }
        }

        // fallback to null
        return null;
    }


    // .equals method and such
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Tile)) { return false; }


        Tile t = (Tile) o;
        return this.ID.equals(t.ID); // or x,y or whatever uniquely identifies a tile
    }
}
