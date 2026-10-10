package main.java.io.quicksiiver.traction.core.tiles;

import java.util.ArrayList;

public abstract class Tile { 
    // instance variables
    private final String PATH;

    // helper stuff for the paths of subclasses
    protected static final String TILES_FOLDER = "src/main/resources/assets/tile/";

    // store all of the created tiles
    private static ArrayList<Tile> tiles = new ArrayList<>();

    // constructors
    protected Tile(final String path) { 
        // verify path is not taken
        for (Tile tile : tiles) {
            if (tile.PATH.equals(path)) { 
                throw new IllegalArgumentException("ERROR: duplicate path name for tile."); 
            }
        }

        // if its fine just set it
        PATH = path; 

        tiles.add(this); // add it to the tiles array
    }

    // getters
    public String getPath() { return PATH; }
    public String getName() { return PATH.substring(TILES_FOLDER.length(), PATH.length() - 4); }
    public String toString() { return getName(); }
    public static Tile getTileFromName(String name) {
        for (Tile tile : tiles) {
            if (tile.getName().equals(name)) { return tile; }
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
        return this.PATH.equals(t.PATH); // or x,y or whatever uniquely identifies a tile
    }
}
