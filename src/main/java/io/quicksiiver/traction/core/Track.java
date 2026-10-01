package main.java.io.quicksiiver.traction.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

import main.java.io.quicksiiver.traction.core.tiles.DriveableTile;
import main.java.io.quicksiiver.traction.core.tiles.Tile;

public class Track {
    // instance variables
    private Tile[][] map; // stores the rows and columns
    private String name; // the name of the map, to be written to [name].json

    // classifiers
    static {
        
    }

    public static final Path DEFAULT_PATH = Path.of("src", "main", "resources", "data", "maps");

    // constructors
    public Track() { this(10, 10); }
    public Track(int width, int height) { 
        Tile[][] tempMap = new Tile[height][width]; // rows and then columns

        // fill all of them
        for (int i = 0; i < tempMap.length; i++) {
            for (int j = 0; j < tempMap[i].length; j++) {
                tempMap[i][j] = DriveableTile.DEFAULT;
            }
        }

        this(tempMap);
    } 
    public Track(Tile[][] map) { this.map = map.clone(); }

    // getters
    public Tile getTile(int x, int y) { return map[y][x]; }
    public String getName() { return name; }
    public int getWidth() { return map.length; }
    public int getHeight() { return map[0].length; }
    public Tile[][] getMap() { return map.clone(); }

    // setters
    public void setElement(int x, int y, Tile value) { map[y][x] = value.copy(); } // row y, column x

    // SAVE-LOAD
    /**
     * Loads a Map from a filepath.
     * @param gson : The Gson object to use to read from the file
     * @param path : The path of the file
     * @param printInfo : Whether or not the information should be printed
     * @return The Map from the filepath, or, if there is not a Map 
     * at that location, create a new one.
     */
    public static Track loadMap(Gson gson, Path path, boolean printInfo) {
        // load Map
        Track map;

        // try to load
        try {
            String json = Files.readString(path);
            map = gson.fromJson(json, Track.class);

            if (printInfo) { System.out.println("Map loaded successfully!"); } // print that it loaded fine

        } catch (IOException e) { // in case of no Map, create a new one
            if (printInfo) { System.out.println("Could not load Map. Creating a new one. . ."); }

            map = new Track(); // create a new Map

            if (printInfo) { System.out.println("Map created successfully!"); }
        }

        return map;
    }
    /**
     * Loads a Map from a filepath. 
     * @param gson : The Gson object to use to read from the file
     * @param path : The path of the file
     * @return The Map from the filepath, or, if there is not a Map 
     * at that location, create a new one.
     */
    public static Track loadMap(Gson gson, Path path) { return loadMap(gson, path, false); } // default to no info printed

    // SAVE Map
    /**
     * Saves a Map to a file.
     * @param gson : The Gson object to use for saving
     * @param path : The path to be saved to
     * @param printInfo Whether or not to print information about the saving
     * (used for debug)
     */
    public void save(Gson gson, Path path, boolean printInfo) {
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
     * Saves a Map to a file.
     * @param gson : The Gson object to use for saving
     * @param path : The path to be saved to
     */
    public void save(Gson gson, Path path) { save(gson, path, false); } // default no info printed
    /**
     * Saves a Map to its respective file in the resources/data/Maps folder,
     * with the filename [name].json where [name] is the Map's name.
     * @param gson : The Gson object to use for saving
     */
    public void save(Gson gson) { save(gson, DEFAULT_PATH.resolve(name + ".json")); } // append the [name].json to the end of the Path

    public Track copy() {
        Track copy = new Track();

        // copy values into the map
        copy.map = this.map.clone(); 
        copy.name = this.name;

        return copy;
    }
}
