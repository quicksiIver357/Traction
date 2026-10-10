package client.java.io.quicksiiver.traction;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;

import client.java.io.quicksiiver.traction.misc.MainWindowListener;
import client.java.io.quicksiiver.traction.panels.main.MainMenuPanel;
import client.java.io.quicksiiver.traction.panels.track.TrackEditorPanel;
import client.java.io.quicksiiver.traction.panels.track.TrackPanel;
import main.java.io.quicksiiver.traction.core.Car;
import main.java.io.quicksiiver.traction.core.Track;

public class Client {
    public static final Client instance = new Client();   

    // drawing
    private JFrame frame = new JFrame();
    private MainMenuPanel mainPanel = new MainMenuPanel(); 
    private TrackPanel trackPanel = new TrackPanel(new Track(), new Car());
    private TrackEditorPanel trackEditorPanel = new TrackEditorPanel(new Track());

    private Client() { // hides constructor
        // set up the window
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new MainWindowListener());

        // drawing
        // add all of them but make them invisible
        frame.add(mainPanel);
        frame.add(trackPanel);
        frame.add(trackEditorPanel);

        // set all of them to invisible
        for (Component component : frame.getComponents()) {
            if (component instanceof JPanel) { ((JPanel) component).setVisible(false); }
        }

        setActivePanel(trackEditorPanel);
        
        frame.setSize(1024, 576);
        frame.setVisible(true);
    } 

    // main stuff (do every tick)
    public void tick(double dt) {
        if (frame.getContentPane() == trackPanel) { trackPanel.tick(dt); }
    }

    // setters
    private void setActivePanel(JPanel panel) {
        // remove the old one, if there is one
        JPanel activePanel = getActivePanel();
        if (activePanel != null) { activePanel.setVisible(false); }

        // add the new one
        panel.setVisible(true);
    }
    public void setTrack(Track t) {
        trackEditorPanel.setTrack(t);
        trackPanel.setTrack(t);
    }

    // getters
    public Track getTrack() { return trackPanel.getTrack(); }
    public Car getCar() { return trackPanel.getCar(); }
    private JPanel getActivePanel() {
        for (Component component : frame.getContentPane().getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;

                if (panel.isVisible()) { return panel; }
            }
        }

        return null;
    }

    // accessors
    public void repaint() { frame.repaint(); }
    public void dispose() { 
        frame.dispose();
        // track.save(Main.gson);
        
        // System.out.println(track);
    } 
}
