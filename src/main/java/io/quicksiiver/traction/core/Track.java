package main.java.io.quicksiiver.traction.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.google.gson.Gson;

import main.java.io.quicksiiver.traction.core.tiles.Tile;
import main.java.io.quicksiiver.traction.core.tiles.types.Checkpoint;
import main.java.io.quicksiiver.traction.core.tiles.types.DriveableTile;
import main.java.io.quicksiiver.traction.core.tiles.types.Wall;

public class Track {
    // instance variables
    private Tile[][] map; // stores the rows and columns

    // stores what tile the car starts on
    private int startX; 
    private int startY;
    private double startRotation; // radians
    private boolean playable; // contains start and end tiles
    private String name; // the name of the map, to be written to [name].json

    // classifiers
    public static final String DEFAULT_PATH = "src/main/resources/data/tracks/";

    // constructors
    public Track() { this(100, 100); }
    public Track(int width, int height) { this(createBlankMap(width, height)); } 
    public Track(Tile[][] map) { // main constructor
        this.map = map.clone(); 

        this.name = "default";
        this.startRotation = Math.PI;

        // map verification
        int numberOfStartTiles = 0;
        int numberOfEndTiles = 0;
        int startTileX = 0;
        int startTileY = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == DriveableTile.START) { 
                    numberOfStartTiles++; 

                    startTileX = i;
                    startTileY = j;
                } 
                else if (map[i][j] == DriveableTile.END) { numberOfEndTiles++; }
            }
        }

        if (numberOfStartTiles != 1 || numberOfEndTiles == 0) { playable = false; }
        else { playable = true; }

        if (numberOfStartTiles > 0) {
            startX = startTileX;
            startY = startTileY;
        }
    }

    // helpers
    private static Tile[][] createBlankMap(int w, int h) {
        Tile[][] blankMap = new Tile[h][w];

        for (int i = 0; i < blankMap.length; i++) {
            for (int j = 0; j < blankMap[0].length; j++) {
                // set each tile to be the same thing, the default tile
                // but make sure that the edges are walls
                blankMap[i][j] = i == 0 || i == blankMap.length - 1 || j == 0 || j == blankMap[0].length - 1
                ? Wall.BASIC : DriveableTile.DEFAULT;
            }
        }

        return blankMap;
    }

    // getters
    public Tile getTile(int x, int y) { return map[y][x]; }
    public String getName() { return name; }
    public int getWidth() { return map.length; }
    public int getHeight() { return map[0].length; }
    public Tile[][] getMap() { return map.clone(); }
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public double getStartRotation() { return startRotation; }
    public boolean contains(Tile t) {
        // check every tile
        for (Tile[] tiles : map) {
            for (Tile tile : tiles) {
                if (tile == t) { return true; }
            }
        }

        return false;
    }
    public boolean playable() { return playable; }
    public String toString() {
        return Arrays.deepToString(map) + name;
    }
    public int getNumberOfCheckpoints() {
        int numberOfCheckpoints = 0;

        for (Tile[] tiles : map) {
            for (Tile tile : tiles) {
                if (tile instanceof Checkpoint) {
                    Checkpoint checkpoint = (Checkpoint) tile;
                    int group = checkpoint.getGroup();

                    if (group > numberOfCheckpoints) {
                        numberOfCheckpoints = group;
                    }
                }
            }
        }

        return numberOfCheckpoints;
    }

    // setters
    public void setElement(int x, int y, Tile value) { map[y][x] = value; } // row y, column x
    public void setName(String name) { this.name = name; }

    // SAVE-LOAD
    /**
     * Loads a Map from a filepath.
     * @param gson : The Gson object to use to read from the file
     * @param path : The path of the file
     * @param printInfo : Whether or not the information should be printed
     * @return The Map from the filepath, or, if there is not a Map 
     * at that location, create a new one.
     */
    public static Track loadTrack(Gson gson, Path path, boolean printInfo) {
        // load Map
        Track track;

        // try to load
        try {
            String json = Files.readString(path);
            track = gson.fromJson(json, Track.class);

            if (printInfo) { System.out.println("Map loaded successfully!"); } // print that it loaded fine

        } catch (IOException e) { // in case of no Map, create a new one
            if (printInfo) { System.out.println("Could not load Map. Creating a new one. . ."); }

            track = new Track(); // create a new Map

            if (printInfo) { System.out.println("Map created successfully!"); }
        }

        return track;
    }
    /**
     * Loads a Map from a filepath. 
     * @param gson : The Gson object to use to read from the file
     * @param path : The path of the file
     * @return The Map from the filepath, or, if there is not a Map 
     * at that location, create a new one.
     */
    public static Track loadTrack(Gson gson, Path path) { return loadTrack(gson, path, false); } // default to no info printed

    // SAVE Track
    /**
     * Saves a Track to a file.
     * @param gson : The Gson object to use for saving
     * @param printInfo Whether or not to print information about the saving
     * (used for debug)
     */
    public void save(Gson gson, boolean printInfo) {
        Path path = Path.of(DEFAULT_PATH + name + ".json");

        try {
            // convert to String
            String json = gson.toJson(this);
            
            // write to file
            Files.writeString(path, json);

            // print info
            if (printInfo) { System.out.println("Map saved successfully!"); }
        } catch (IOException e) {
            if (printInfo) { System.out.println("Map failed to save. "); }
        }
    }
    /**
     * Saves a Track to its respective file in the resources/data/Tracks folder,
     * with the filename [name].json where [name] is the Track's name.
     * @param gson : The Gson object to use for saving
     */
    public void save(Gson gson) { save(gson, false); }

    // misc and useful stuff
    public boolean equals(Object o) {
        if (this == o) { return true; } // if they are the same reference return true
        if (!(o instanceof Track)) { return false; } // if o is not even a Track return false

        Track t = (Track) o; // cast it
        // if they don't have the same attributes return false
        if (t.getWidth() != this.getWidth() ||
            t.getHeight() != this.getHeight() ||
            t.startX != this.startX ||
            t.startY != this.startY ||
            t.startRotation != this.startRotation) 
        { return false; }

        // check all of the tile individually, return false on an inconsistency
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                if (this.map[i][j] != t.map[i][j]) { return false; }
            }
        }

        // otherwise return true if it's all the same
        return true;
    }
    public Track clone() {
        Track clone = new Track();

        // copy values into the map
        clone.map = this.map.clone(); 
        clone.name = new String(this.name);

        return clone;
    }
}
