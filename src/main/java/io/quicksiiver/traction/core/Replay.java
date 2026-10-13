package main.java.io.quicksiiver.traction.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Replay {
    private ArrayList<Boolean> w = new ArrayList<>();
    private ArrayList<Boolean> a = new ArrayList<>();
    private ArrayList<Boolean> s = new ArrayList<>();
    private ArrayList<Boolean> d = new ArrayList<>();

    private double time = 0;

    // classifiers
    public static final String DEFAULT_PATH = "src/main/resources/data/replays/";

    // getters
    public boolean getW(double time, double dt) { return w.get(getIndex(time, dt)); }
    public boolean getA(double time, double dt) { return a.get(getIndex(time, dt)); }
    public boolean getS(double time, double dt) { return s.get(getIndex(time, dt)); }
    public boolean getD(double time, double dt) { return d.get(getIndex(time, dt)); }
    public double getTime() { return time; }

    // helpers
    private int getIndex(double time, double dt) { return (int) Math.round(time / dt); }

    // modifiers
    public void add(double dt, boolean w, boolean a, boolean s, boolean d) {
        time += dt;

        this.w.add(w);
        this.a.add(a);
        this.s.add(s);
        this.d.add(d);
    }

    // SAVE-LOAD
    public void save(Gson gson, String name, boolean printInfo) {
        Path path = Path.of(DEFAULT_PATH + name + ".json");

        try {
            // convert to String
            String json = gson.toJson(this);
            
            // write to file
            Files.writeString(path, json);

            // print info
            if (printInfo) { System.out.println("Replay saved successfully! Filepath: " + path); }
        } catch (IOException e) {
            if (printInfo) { System.out.println("Replay failed to save. Filepath: " + path); }
        }
    }
    public static Replay loadReplay(Gson gson, Path path, boolean printInfo) {
        // load Replay
        Replay track;

        // try to load
        try {
            String json = Files.readString(path);
            track = gson.fromJson(json, Replay.class);

            if (printInfo) { System.out.println("Replay loaded successfully!"); } // print that it loaded fine

            return track;
        } catch (IOException e) { // in case of no Replay, return null
            if (printInfo) { System.out.println("Could not load Replay. Filename: " + path); }

            return null;
        }

        
    }

}
