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
    private static final double FPS = 60;

    public static void main(String[] args) {
        Track map = Track.loadMap(gson, Track.DEFAULT_PATH.resolve("testing"));

        Client.instance.setMap(map);

        // stuff to do with consistent framerate
        long previous = System.nanoTime();
        double accumulator = 0;

        while (true) {
            Client.instance.repaint();

            // consistent framerate
            long current = System.nanoTime();
            double frameTime = ( current - previous ) / 1000000000.0;
            previous = current;
            accumulator += frameTime;

            while (accumulator >= 1 / FPS) {
                Client.instance.tick(1 / FPS);
                accumulator -= 1 / FPS;
            }
        }
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
