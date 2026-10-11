package main.java.io.quicksiiver.traction.core.tiles.types;

import java.util.ArrayList;

import main.java.io.quicksiiver.traction.core.tiles.Tile;

public class Wall extends Tile {
    // instance variables
    private final double BOUNCYNESS;

    // all Walls
    private static ArrayList<Wall> walls = new ArrayList<>();

    // types of walls
    public static final Wall BASIC = new Wall("basic.png", 0.5);
    public static final Wall PLASTIC = new Wall("plastic.png", 0.8);


    // constructor
    private Wall(String filename, double bouncyness) { 
        super(filename);

        // verify bouncyness and set it if its ok else throw an error
        if (!verifyBouncyness(bouncyness)) { throw new IllegalArgumentException("bouncyness of a Wall must be between 0 and 1!"); }
        BOUNCYNESS = bouncyness;

        walls.add(this);
    }

    // helpers
    private boolean verifyBouncyness(double bouncyness) { return bouncyness < 1 && bouncyness > 0; }

    // getters
    public double getBouncyness() { return BOUNCYNESS; }
    @SuppressWarnings("unchecked")
    public static ArrayList<Wall> getAllWalls() { return (ArrayList<Wall>) walls.clone(); }
}
