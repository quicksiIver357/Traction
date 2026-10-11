package client.java.io.quicksiiver.traction;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import client.java.io.quicksiiver.traction.misc.MainWindowListener;
import client.java.io.quicksiiver.traction.panels.menu.MenuManager;
import client.java.io.quicksiiver.traction.panels.track.TrackEditorPanel;
import client.java.io.quicksiiver.traction.panels.track.TrackPanel;
import main.java.io.quicksiiver.traction.Main;
import main.java.io.quicksiiver.traction.core.Car;
import main.java.io.quicksiiver.traction.core.Track;

public class Client {
    public static final Client instance = new Client();  

    // global assets and such
    public static final String FONTS_FOLDER = "src/main/resources/fonts/";
    public static final String PIXEL_FONT_NAME = "Home Video";

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(loadFont(FONTS_FOLDER + "pixelFont.ttf"));
    }
    
    // for CardLayout labels
    private static final String MENU_MANAGER = "menuManager";
    private static final String TRACK_PANEL = "trackPanel";
    private static final String TRACK_EDITOR_PANEL = "trackEditorPanel";

    // drawing
    private JFrame frame = new JFrame();
    private TrackPanel trackPanel = new TrackPanel(new Track(), new Car());
    private TrackEditorPanel trackEditorPanel = new TrackEditorPanel(new Track());
    private MenuManager menuManager = new MenuManager();
    CardLayout cl;

    private Client() { // hides constructor
        // set up the window
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new MainWindowListener());
        frame.setBackground(Color.BLACK);

        cl = new CardLayout();
        frame.setLayout(cl);

        // input map for moving between track/editor/menu
        final String ESCAPE = "escape";

        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW) // get the input map
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE); // put the input
        frame.getRootPane().getActionMap().put(ESCAPE, new AbstractAction() { // add a function to do after
            @Override
            public void actionPerformed(ActionEvent e) { cl.show(frame.getContentPane(), MENU_MANAGER); }
        });

        // drawing
        // add all of them but make them invisible
        frame.add(menuManager, MENU_MANAGER);
        frame.add(trackPanel, TRACK_PANEL);
        frame.add(trackEditorPanel, TRACK_EDITOR_PANEL);
        cl.show(frame.getContentPane(), MENU_MANAGER);
        
        frame.setSize(1024, 576);
        frame.setVisible(true);
    } 

    // main stuff (do every tick)
    public void tick(double dt) {
        if (getActivePanel() == trackPanel) { trackPanel.tick(dt); }
        else if (getActivePanel() == menuManager) { menuManager.tick(dt); }

        // System.out.println(getActivePanel());
    }

    // helpers
    private static Font loadFont(String filepath) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(filepath));
        } catch (FontFormatException e) {
            e.printStackTrace();
            System.exit(0);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        return null;
    }

    // setters
    public void setTrack(Track t) {
        trackEditorPanel.setTrack(t);
        trackPanel.setTrack(t);
    }
    public void openTrackFromName(String name, boolean playing) {
        Track t = Main.loadTrack(name);

        // determine whether or not to use the editor
        if (t.playable() && playing) { cl.show(frame.getContentPane(), TRACK_PANEL); } 
        else if (!playing) { cl.show(frame.getContentPane(), TRACK_EDITOR_PANEL); }
        else { JOptionPane.showMessageDialog(frame.getContentPane(), "Error loading track!"); }

        // set the track
        setTrack(t);
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
