package client.java.io.quicksiiver.traction.panels.track;

import java.awt.Color;
import java.awt.Graphics;

import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import main.java.io.quicksiiver.traction.core.Car;
import main.java.io.quicksiiver.traction.core.Track;
import main.java.io.quicksiiver.traction.core.tiles.Tile;

public class TrackPanel extends JPanel {
    private Track track;
    private Car car; // only used for drawing
    private double zoom;

    private HashMap<Tile, ImageIcon> images;

    // prevent magic numbers
    private static final int TILE_SIZE = 80;

    public TrackPanel(Track t, Car c) { 
        setBackground(Color.BLACK);

        setTrack(t);
        car = c;
        zoom = 1; // 100%
    }

    // setters
    public void setTrack(Track t) { 
        track = t.copy(); 

        // load all of the images
        images = new HashMap<>();

        for (Tile[] column : track.getMap()) {
            for (Tile tile : column) {
                if (!images.containsKey(tile)) { images.put(tile, new ImageIcon(tile.getPath())); } // if not already loaded, load it
            }
        }
    }
    // public void setCarPos(Point p) { car.setPos(p); }
    public void setZoom(double d) { zoom = d; }

    // getters
    public Track getTrack() { return track.copy(); }
    public Car getCar() { return car; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // g.setColor(Color.RED);
        // g.fillRect(0, 0, 100, 100);

        // System.out.println("Panel: " + getWidth() + "x" + getHeight());
        // System.out.println("Track: " + track.getWidth() + "x" + track.getHeight());

        for (int i = 0; i < track.getWidth(); i++) {
            for (int j = 0; j < track.getHeight(); j++) {
                Tile currentTile = track.getTile(i, j);
                ImageIcon toDraw = images.get(currentTile);


                double xCenter = getWidth() / 2.0;
                double xRelative = TILE_SIZE * zoom * ( i - track.getWidth() / 2.0 );
                double yCenter = getHeight() / 2.0;
                double yRelative = TILE_SIZE * zoom * ( j - track.getHeight() / 2.0 );

                int imageScale = (int) (zoom * TILE_SIZE);

                int x = (int) (xCenter + xRelative - imageScale / 2.0);
                int y = (int) (yCenter + yRelative - imageScale / 2.0);

                g.drawImage(toDraw.getImage(), x, y, imageScale, imageScale, null);
            }
        }
    }
}
