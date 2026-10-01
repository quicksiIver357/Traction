package main.java.io.quicksiiver.traction.core.tiles;

public abstract class Tile { 
    // instance variables
    private final String PATH;

    // helper stuff for the paths of subclasses
    protected static final String TILES_FOLDER = "src/main/resources/assets/tile/";

    // constructors
    protected Tile(final String path) { PATH = path; }

    // getters
    public String getPath() { return PATH; }

    // subclasses need to implement a copy in order to prevent problems
    public abstract Tile copy();
}