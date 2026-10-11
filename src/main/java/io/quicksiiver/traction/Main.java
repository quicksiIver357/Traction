package main.java.io.quicksiiver.traction;

import java.nio.file.Path;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.java.io.quicksiiver.traction.Client;
import main.java.io.quicksiiver.traction.core.Track;
import main.java.io.quicksiiver.traction.core.tiles.Tile;
import main.java.io.quicksiiver.traction.core.tiles.TileAdapter;

public abstract class Main {
    // important stuff
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Tile.class, new TileAdapter()).create();
    private static final Scanner scanner = new Scanner(System.in);

    // no magic numbers
    // private static final double FPS = 60;
    private static final double PHYSICS_TICK_RATE = 60;
    private static final double FPS = 120;

    // global stuff that should be shared across all methods
    public static int frames = 0;

    public static void main(String[] args) {
        // set it to anything but 0, 0 or 100, 100
        // 0, 0 will break the getHeight() method becuase it does map[0].length,
        // and if the length is 0 then map[0] does not exist
        // 100, 100 will break it because that is the default contsructor and it checks against that
        // 1, 1 is the most efficient as it creates the smallest possible 2d array.
        Client.instance.setTrack(new Track(1, 1));

        // stuff to do with consistent framerate and physics rate
        long previousPhysics = System.nanoTime();
        long previousFrame = System.nanoTime();

        // main game loop
        while (true) {
            long current = System.nanoTime();

            // fixed physics rate
            while (( current - previousPhysics ) / 1000000000.0 >= 1 / PHYSICS_TICK_RATE) {
                tick();
                previousPhysics = current;
            }
            // fixed framerate
            while ((current - previousFrame) / 1000000000.0 >= 1 / FPS) {
                Client.instance.repaint(); // update frame
                previousFrame = current;
            }

            current = System.nanoTime();
        }
    }

    // this runs PHYSICS_TICK_RATE times per second (should be 60)
    private static void tick() {
        Client.instance.tick(1 / PHYSICS_TICK_RATE);
        Client.instance.repaint();

        frames++;
        // System.out.println("Frames: " + frames);
    }

    // this runs when the game stops
    public static void stop() {
        Track track = Client.instance.getTrack(); // get track
        track.save(gson); // save track

        // close things
        Client.instance.dispose();
        scanner.close();
        System.exit(0);
    }

    // utility
    public static Track loadTrack(String name) {
        Track track = Track.loadTrack(gson, Path.of(Track.DEFAULT_PATH + name + ".json"));
        if (track == null) { 
            track = new Track(); 
            track.setName("testing");
        }

        return track;
    }
}
