package client.java.io.quicksiiver.traction.panels.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
    private static final int TILE_SIZE = 80;

    public TrackPanel(Track t, Car c) { 
        setBackground(Color.BLACK);

        setTrack(t);
        car = c;
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
        
        // TODO: add actions listeners to the ActionMap
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
        int x = (int) ( car.getX() / TILE_SIZE ) - track.getMap().length / 2;
        int y = (int) ( car.getY() / TILE_SIZE ); // TODO: SAme for the y please fix

        Tile current = track.getMap()[y][x];

        if (current instanceof DriveableTile) {
            double friction = ((DriveableTile) current).getFriction();

            car.processInputs(friction, dt);
        }

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


                double xCenter = getWidth() / 2.0 - car.getX();
                double xRelative = TILE_SIZE * zoom * ( i - track.getWidth() / 2.0 );
                double yCenter = getHeight() / 2.0 - car.getY();
                double yRelative = TILE_SIZE * zoom * ( j - track.getHeight() / 2.0 );

                int imageScale = (int) (zoom * TILE_SIZE);

                int x = (int) (xCenter + xRelative - imageScale / 2.0);
                int y = (int) (yCenter + yRelative - imageScale / 2.0);

                g.drawImage(toDraw.getImage(), x, y, imageScale, imageScale, null);
            }
        }
    }
}
