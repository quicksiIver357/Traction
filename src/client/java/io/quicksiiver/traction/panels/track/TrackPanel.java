package client.java.io.quicksiiver.traction.panels.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import main.java.io.quicksiiver.traction.core.Car;
import main.java.io.quicksiiver.traction.core.Track;
import main.java.io.quicksiiver.traction.core.tiles.DriveableTile;
import main.java.io.quicksiiver.traction.core.tiles.Tile;

public class TrackPanel extends JPanel {
    private Track track;
    private Car car; // only used for drawing
    private double zoom;

    private HashMap<Tile, ImageIcon> images;

    // prevent magic numbers
    public static final int TILE_SIZE = 80;
    public static final int CAR_SIZE = 80; // needs to be accessed by Car.java

    public TrackPanel(Track t, Car c) { 
        setBackground(Color.BLACK);

        // set the track and car and move it to the start position
        setTrack(t);
        car = c;
        reset();

        zoom = 1; // 100%
        
        // keyboard input handling
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // prevents typos
        final String ACCELERATE = "accelerate";
        final String LEFT = "left";
        final String BRAKE = "brake";
        final String RIGHT = "right";
        final String RELEASE = "_released";

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), ACCELERATE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), LEFT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), BRAKE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), RIGHT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), ACCELERATE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), BRAKE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), LEFT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), RIGHT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, false), BRAKE);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), ACCELERATE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), LEFT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), BRAKE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), RIGHT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), ACCELERATE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), BRAKE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), LEFT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), RIGHT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), BRAKE + RELEASE);
        
        actionMap.put(ACCELERATE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setWPressed(true); }
        });
        actionMap.put(ACCELERATE + RELEASE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setWPressed(false); }
        });
        actionMap.put(LEFT, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setAPressed(true); }
        });
        actionMap.put(LEFT + RELEASE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setAPressed(false); }
        });
        actionMap.put(RIGHT, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setDPressed(true); }
        });
        actionMap.put(RIGHT + RELEASE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setDPressed(false); }
        });
        actionMap.put(BRAKE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setSPressed(true); }
        });
        actionMap.put(BRAKE + RELEASE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { car.setSPressed(false); }
        });
    }

    public void tick(double dt) {
        // put here everything that should happen each frame (besides rendering or physics)
        car.processInputs(track, getTileGrip(), dt);
    }

    // helpers
    private double getTileGrip() {
        Tile current = track.getMap()[getTileY()][getTileX()];

        if (current instanceof DriveableTile) { return ((DriveableTile) current).getFriction(); }
        else { throw new IllegalStateException("The car was stuck within a wall. "); }
    }
    private int getTileX() { return (int) ( car.getX() / TILE_SIZE ); }
    private int getTileY() { return (int) ( car.getY() / TILE_SIZE ); }
    private void reset() {
        car.setX(track.getStartX() * TILE_SIZE);
        car.setY(track.getStartY() * TILE_SIZE);
        car.setRotation(track.getStartRotation());
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

        Graphics2D g2d = (Graphics2D) g;

        // g.setColor(Color.RED);
        // g.fillRect(0, 0, 100, 100);

        // System.out.println("Panel: " + getWidth() + "x" + getHeight());
        // System.out.println("Track: " + track.getWidth() + "x" + track.getHeight());


        // things you only need to calculate once
        Point screenCenter = new Point(getWidth() / 2, getHeight() / 2);

        int trackWidth = track.getWidth();
        int trackHeight = track.getHeight();
        int tileImageScale = (int) (zoom * TILE_SIZE);

        // draw each tile
        for (int i = 0; i < trackWidth; i++) {            
            double xCenter = screenCenter.x - car.getX();
            double xRelative = TILE_SIZE * zoom * i;
            int x = (int) (xCenter + xRelative - tileImageScale / 2.0);

            // don't draw it becuase it's off the screen
            if (x + TILE_SIZE < 0 || x > getWidth()) { continue; }

            // otherwise move on
            for (int j = 0; j < trackHeight; j++) {                
                double yCenter = screenCenter.y - car.getY();
                double yRelative = TILE_SIZE * zoom * j;
                int y = (int) (yCenter + yRelative - tileImageScale / 2.0);

                // don't draw it because it's off the screen
                if (y + TILE_SIZE < 0 || y > getHeight()) { continue; }

                Tile currentTile = track.getTile(i, j);
                ImageIcon toDraw = images.get(currentTile);

                g2d.drawImage(toDraw.getImage(), x, y, tileImageScale, tileImageScale, null);
            }
        }

        // draw the car
        // the goal of this is to preserve the old AffineTransform so it doesn't disrupt anything that was relying on it not changing.
        AffineTransform backup = g2d.getTransform(); 

        // then make a new one and rotate the car based on its rotation
        AffineTransform trans = new AffineTransform();
        // rotate it around the center of the car (which is also the screen center)
        // you gotta add pi bc im an idiot and made the car texture upside down
        trans.rotate(car.getRotation() + Math.PI, screenCenter.x, screenCenter.y);  
        g2d.transform(trans); // rotate the graphics component

        // do some calculations for drawing the car
        int carImageScale = (int) (zoom * CAR_SIZE);

        // draw it
        g2d.drawImage(Car.IMAGE.getImage(), screenCenter.x - carImageScale / 2, screenCenter.y - carImageScale / 2, carImageScale, carImageScale, null);

        // finally, restore the original AffineTransform
        g2d.transform(backup);
    }
}
