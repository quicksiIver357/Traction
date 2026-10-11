package client.java.io.quicksiiver.traction.panels.track;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import main.java.io.quicksiiver.traction.core.Car;
import main.java.io.quicksiiver.traction.core.Track;
import main.java.io.quicksiiver.traction.core.tiles.Tile;
import main.java.io.quicksiiver.traction.core.tiles.types.DriveableTile;

public class TrackPanel extends GenericTrackPanel {
    private Car car; // only used for drawing
    private double timer = 0;

    // prevent magic numbers
    public static final int TILE_SIZE = 80;
    public static final int CAR_SIZE = 80; // needs to be accessed by Car.java

    public TrackPanel(Track t, Car c) { 
        super(t);

        // set the track and car and move it to the start position
        car = c;
        System.out.println(c);
        reset();
        
        // keyboard input handling
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // prevents typos
        final String ACCELERATE = "accelerate";
        final String LEFT = "left";
        final String BRAKE = "brake";
        final String RIGHT = "right";
        final String RESET = "reset";
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
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), RESET);

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
        actionMap.put(RESET, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { reset(); }
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
    private void reset() {
        // reset the car's variables
        car.reset(track.getStartX() * TILE_SIZE, track.getStartY() * TILE_SIZE, track.getStartRotation());

        // reset the timer to the countdown
        timer = -3;
    }

    // setters
    public void setTrack(Track t) { 
        super.setTrack(t);
        // System.out.println("help");
        reset();
    }
    // public void setCarPos(Point p) { car.setPos(p); }
    public void setZoom(double d) { zoom = d; }

    // getters
    public Track getTrack() { return track; }
    public Car getCar() { return car; }
    protected double getScreenX() { return car.getX(); }
    protected double getScreenY() { return car.getY(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        drawCar(g2d);
        drawTimer(g2d);
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
    private String format(double a) { 
        return (a < 10 ? "0" + String.format("%.2f", a) : "" + String.format("%.2f", a)); 
    }

}
