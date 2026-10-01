package main.java.io.quicksiiver.traction.core.tiles;

public class Wall extends Tile {
    private final double BOUNCYNESS;

    // types of walls
    public static final Wall BASIC = new Wall("basic.png", 0.2);
    public static final Wall PLASTIC = new Wall("plastic.png", 0.8);

    // constructor
    private Wall(String filename, double bouncyness) { 
        super(Tile.TILES_FOLDER + filename);

        // verify bouncyness and set it if its ok else throw an error
        if (!verifyBouncyness(bouncyness)) { throw new IllegalArgumentException("bouncyness of a Wall must be between 0 and 1!"); }
        BOUNCYNESS = bouncyness;
    }

    // helpers
    private boolean verifyBouncyness(double bouncyness) { return bouncyness < 1 && bouncyness > 0; }

    // implement the copy method
    public Wall copy() { return new Wall(getPath().toString(), BOUNCYNESS); }
}
