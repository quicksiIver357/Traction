package client.java.io.quicksiiver.traction;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import client.java.io.quicksiiver.traction.misc.MainWindowListener;
import client.java.io.quicksiiver.traction.panels.main.MainMenuPanel;
import client.java.io.quicksiiver.traction.panels.track.TrackPanel;
import main.java.io.quicksiiver.traction.core.Car;
import main.java.io.quicksiiver.traction.core.Track;

public class Client {
    public static final Client instance = new Client();

    // storage
    private Track map;

    // drawing
    private JFrame frame = new JFrame();
    private MainMenuPanel mainPanel = new MainMenuPanel(); 
    private TrackPanel trackPanel = new TrackPanel(new Track(), new Car());

    private Client() { // hides constructor
        // set up the window
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new MainWindowListener());

        // mouse input handling
        MouseList m = new MouseList();
        frame.addMouseListener(m);
        frame.addMouseMotionListener(m);

        // Display the window.
        frame.setContentPane(trackPanel);
        frame.setSize(1024, 576);
        frame.setVisible(true);
    } 

    // main stuff (do every tick)
    public void tick(double dt) {
        if (frame.getContentPane() == trackPanel) { trackPanel.tick(dt); }
    }

    // setters
    public void setMap(Track m) { map = m.copy(); }

    // getters
    public Track getMap() { return map.copy(); }

    // accessors
    public void repaint() { frame.repaint(); }
    public void dispose() { frame.dispose(); }

    // input handling
    private class MouseList implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point mousePos = e.getPoint();

            mainPanel.clicked(mousePos);
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {}

        // satisfy implementations
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}

        // helpers
        // private static boolean near(Point p1, Point p2, double r) {
        //     if (r*r >= Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2)) { return true; }
        //     else { return false; }
        // }
    }
}
