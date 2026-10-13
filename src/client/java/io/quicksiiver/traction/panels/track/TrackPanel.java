package client.java.io.quicksiiver.traction.panels.track;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.nio.file.Path;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import main.java.io.quicksiiver.traction.Main;
import main.java.io.quicksiiver.traction.core.Car;
import main.java.io.quicksiiver.traction.core.Replay;
import main.java.io.quicksiiver.traction.core.Track;

public class TrackPanel extends GenericTrackPanel {
    private Car car; // only used for drawing
    private double timer = 0;
    private boolean racing = true;

    // pb stuff
    private Car pbCar = new Car();
    private Replay pb;

    // prevent magic numbers
    public static final int TILE_SIZE = 80;
    public static final int CAR_SIZE = 80; // needs to be accessed by Car.java

    public TrackPanel(Track t, Car c) { 
        super(t);

        // set the track and car and move it to the start position
        car = c;
        // System.out.println(c);
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
            if (racing) {
                if (timer >= 0) { 
                    if (car.processInputs(track, dt)) { 
                        racing = false; 

                        // save it if better
                        if (pb == null || pb.getTime() > timer) {
                            // save the replay file if it's a personal best
                            pb = car.getReplay();

                            // process the track name
                            String[] split = track.getName().split("/");
                            String processedName = split[0] + "/" + Main.getProps().getProperty("ghost") + "/" + split[1];

                            pb.save(Main.gson, processedName, true);
                        }
                    }

                    // pb ghost
                    if (pb != null) { 
                        // force the inputs for the replay car
                        if (timer < pb.getTime() - dt) {
                            pbCar.setWPressed(pb.getW(timer, dt));
                            pbCar.setAPressed(pb.getA(timer, dt));
                            pbCar.setSPressed(pb.getS(timer, dt));
                            pbCar.setDPressed(pb.getD(timer, dt));
                        }
                        
                        pbCar.processInputs(track, dt); 
                    }
                }

                timer += dt; // update the timer
            } else if (pb == null || pb.getTime() > car.getReplay().getTime()) {
                // save the replay file if it's a personal best
                // pb = car.getReplay();
                // pb.save(Main.gson, track.getName(), true);
            }   
        } else {
            // display debug info and such
        }
    }

    // helpers
    private void reset() {
        // reset the car's variables
        int startX = track.getStartX() * TILE_SIZE;
        int startY = track.getStartY() * TILE_SIZE;
        double startRotation = track.getStartRotation();

        car.reset(startX, startY, startRotation);
        if (pbCar != null) { pbCar.reset(startX, startY, startRotation); }
        

        // reset the timer to the countdown
        timer = -3;
        racing = true;
    }

    // setters
    public void setTrack(Track t) { 
        super.setTrack(t);

        // if its not no track and not the default track do stuff with loading pbs and reset the car
        if (t != null && t.getName() != "default") { 
            reset(); // reset the car to start pos
            
            // process the track name
            String[] split = t.getName().split("/");
            String processedName = split[0] + "/" + Main.getProps().getProperty("ghost") + "/" + split[1] + ".json";

            Path pbPath = Path.of(Replay.DEFAULT_PATH + processedName);
            pb = Replay.loadReplay(Main.gson, pbPath, true);
        }
    }
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

        drawCars(g2d);
        drawTimer(g2d);
    }

    private void drawCars(Graphics2D g2d) {
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
        Image carImage = Car.IMAGE.getImage();

        // draw it
        g2d.drawImage(carImage, screenCenter.x - carImageScale / 2, screenCenter.y - carImageScale / 2, carImageScale, carImageScale, null);

        // draw the replay car
        AffineTransform replayTrans = new AffineTransform();  

        int x = (int) (screenCenter.x + (pbCar.getX() - car.getX()) / zoom) - carImageScale / 2;
        int y = (int) (screenCenter.y + (pbCar.getY() - car.getY()) / zoom) - carImageScale / 2;

        float opacity = (float) Math.clamp(Math.sqrt(Math.pow(x - screenCenter.x, 2) + Math.pow(y - screenCenter.y, 2)) / 100, 0, 1) ;

        Composite c = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity);         
        g2d.setComposite(c);

        replayTrans.rotate(pbCar.getRotation() + Math.PI, x, y);
        g2d.setTransform(backup);
        g2d.transform(replayTrans);
        g2d.drawImage(carImage, x, y, carImageScale, carImageScale, null);

        // finally, restore the original AffineTransform
        g2d.setTransform(backup);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1));
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
