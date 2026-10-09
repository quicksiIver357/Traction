package main.java.io.quicksiiver.traction;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import client.java.io.quicksiiver.traction.Client;
import main.java.io.quicksiiver.traction.core.Track;

public abstract class Main {
    // important stuff
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Scanner scanner = new Scanner(System.in);

    // no magic numbers
    // private static final double FPS = 60;
    private static final double PHYSICS_TICK_RATE = 60;
    private static final double FPS = 120;

    // global stuff that should be shared across all methods
    public static int frames = 0;

    public static void main(String[] args) {
        Track map = Track.loadMap(gson, Track.DEFAULT_PATH.resolve("testing"));

        Client.instance.setMap(map);

        // stuff to do with consistent framerate
        long previousPhysics = System.nanoTime();
        long previousFrame = System.nanoTime();

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
        System.out.println("Frames: " + frames);
    }

    // this runs when the game stops
    public static void stop() {
        Client.instance.getMap().save(gson); // save map

        // close things
        Client.instance.dispose();
        scanner.close();
        System.exit(0);
    }
}
