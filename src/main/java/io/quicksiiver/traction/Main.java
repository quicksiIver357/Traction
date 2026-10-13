package main.java.io.quicksiiver.traction;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
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

    // filepaths
    public static final String GLOBAL_SETTINGS_FILEPATH = "src/main/resources/data/settings.properties";

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
        if (track != null) { track.save(gson); } // save track

        // close things
        Client.instance.dispose();
        scanner.close();
        System.exit(0);
    }

    public static void modifySettings(String key, String value) {
        // Source - https://stackoverflow.com/a/15337487
        // Posted by Vasyl Keretsman, modified by community. See post 'Timeline' for change history
        // Retrieved 2026-10-12, License - CC BY-SA 4.0
        // gosh whoever wrote this had the right idea idea but 
        // oh GOD they screwed up the syntax really badly
        // also I split it up into two methods
        Properties props = getProps();
                
        try (FileOutputStream out = new FileOutputStream(GLOBAL_SETTINGS_FILEPATH)){
            props.setProperty(key, value);
            props.store(out, null);
        } catch (IOException e) {}
    }
    public static Properties getProps() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(GLOBAL_SETTINGS_FILEPATH)) { props.load(in); }
        catch (IOException e) {}

        return props;
    }
}
