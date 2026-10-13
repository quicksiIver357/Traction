package main.java.io.quicksiiver.traction.core.tiles.types;

public class Checkpoint extends SpecialTile {
    private final int GROUP;
    public Checkpoint(int group) {
        super("checkpoint.png", 0.5, 1, 1);
        GROUP = group;
    }
    public int getGroup() { return GROUP; }
}
