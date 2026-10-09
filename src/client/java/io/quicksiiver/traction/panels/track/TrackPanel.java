package client.java.io.quicksiiver.traction.panels.track;

import java.awt.Color;
import java.awt.Font;
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
    private double zoom = 1; // 100%
    private double timer = 0;

    private HashMap<Tile, ImageIcon> images;

    // debug
    private boolean paused = false;

    // prevent magic numbers
    public static final int TILE_SIZE = 80;
    public static final int CAR_SIZE = 80; // needs to be accessed by Car.java

    public TrackPanel(Track t, Car c) { 
        setBackground(Color.BLACK);

        // set the track and car and move it to the start position
        // (setTrack(Track) calls reset())
        car = c;
        setTrack(t);
        
        // keyboard input handling
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // prevents typos
        final String ACCELERATE = "accelerate";
        final String LEFT = "left";
        final String BRAKE = "brake";
        final String RIGHT = "right";
        final String RESET = "reset";
        final String DEBUG_PAUSE = "pause";
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
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), DEBUG_PAUSE);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), ACCELERATE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), LEFT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), BRAKE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), RIGHT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), ACCELERATE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), BRAKE + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), LEFT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), RIGHT + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), BRAKE + RELEASE);
        // inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), RESET + RELEASE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), DEBUG_PAUSE + RELEASE);
        
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
        actionMap.put(RESET, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { reset(); }
        });
        actionMap.put(DEBUG_PAUSE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { paused = true; }
        });
        actionMap.put(DEBUG_PAUSE + RELEASE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { paused = false; }
        });
    }

    public void tick(double dt) {
        // put here everything that should happen each frame (besides rendering)
        if (!paused) { 
            // process inputs only if the game has started
            if (timer >= 0) { car.processInputs(track, getTileGrip(), dt); }
            
            timer += dt; // update the timer
        } else {
            // display debug info and such
        }
    }

    // helpers
    private double getTileGrip() {
        Tile current = track.getMap()[getTileY()][getTileX()];

        if (current instanceof DriveableTile) { return ((DriveableTile) current).getFriction(); }
        // else { throw new IllegalStateException("The car was stuck within a wall. "); }
        else return 0;
    }
    private int getTileX() { return (int) ( car.getX() / TILE_SIZE ); }
    private int getTileY() { return (int) ( car.getY() / TILE_SIZE ); }
    private void reset() {
        // reset the car's variables
        car.setX(track.getStartX() * TILE_SIZE);
        car.setY(track.getStartY() * TILE_SIZE);
        car.setRotation(track.getStartRotation());

        car.resetVelocity();

        // reset the timer to the countdown
        timer = -3;
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

        reset();
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

        drawTiles(g2d);
        drawCar(g2d);
        drawTimer(g2d);
    }

    // drawing helpers
    private void drawTiles(Graphics2D g2d) {
        Point screenCenter = new Point(getWidth() / 2, getHeight() / 2);
        int tileImageScale = (int) (zoom * TILE_SIZE);

        // draw each tile
        for (int i = 0; i < track.getWidth(); i++) {            
            double xCenter = screenCenter.x - car.getX() * zoom;
            double xRelative = TILE_SIZE * zoom * i;
            int x = (int) ((xCenter + xRelative - tileImageScale / 2.0));

            // don't draw it becuase it's off the screen
            if (x + TILE_SIZE < 0 || x > getWidth()) { continue; }

            // otherwise move on
            for (int j = 0; j < track.getHeight(); j++) {       
                double yCenter = screenCenter.y - car.getY() * zoom;
                double yRelative = TILE_SIZE * zoom * j;
                int y = (int) ((yCenter + yRelative - tileImageScale / 2.0));

                // don't draw it because it's off the screen
                if (y + TILE_SIZE < 0 || y > getHeight()) { continue; }

                Tile currentTile = track.getTile(i, j);
                ImageIcon toDraw = images.get(currentTile);
                g2d.drawImage(toDraw.getImage(), x, y, tileImageScale, tileImageScale, null);
            }
        }
    }
    private void drawCar(Graphics2D g2d) {
        Point screenCenter = new Point(getWidth() / 2, getHeight() / 2);

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
        g2d.setTransform(backup);
    }
    private void drawTimer(Graphics2D g2d) {
        // screen pos
        int x = (getWidth() - 146) / 2;
        int y = (int) (getHeight() * 0.95);

        // draw it
        Color oldColor = g2d.getColor(); // preserve the old color to restore later
        Font oldFont = g2d.getFont(); // do the same for font

        // set the color of the drawer based on the timer time
        if (timer < 0) { g2d.setColor(Color.RED); }
        else { g2d.setColor(Color.GREEN); }

        // set font
        g2d.setFont(new Font("Times New Roman", Font.BOLD, 40));

        // draw it
        double absTime = Math.abs(timer);
        int minutes = (int) (absTime / 60);
        double seconds = Math.round(Math.abs(timer) * 100) / 100.0 - minutes * 60;

        // format it like XX:XX.XX (.substring is to remove .0 from minutes as it is implicitly cast to double)
        String toDraw = format(minutes).substring(0, 2) + ":" + format(seconds);
        g2d.drawString(toDraw, x, y); // draw it to the screen

        // restore
        g2d.setColor(oldColor); // re-set the color to the previous to not break anything else
        g2d.setFont(oldFont); // do the same for font
    }
    private String format(double a) { return (a < 10 ? "0" + String.format("%.2f", a) : "" + String.format("%.2f", a)); }
}
