package client.java.io.quicksiiver.traction.panels.menu;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

// this class handles all of the things related to the menus for the client.
public class MenuManager extends JPanel {
    private StartGamePanel startGamePanel = new StartGamePanel(() -> { setActivePanel(MAIN_MENU); });
    private MainMenuPanel mainMenuPanel = new MainMenuPanel();
    private TrackDisplayPanel whiteTracksPanel = new TrackDisplayPanel(generateTrackSetNames("A"), false);
    private SettingsPanel settingsPanel = new SettingsPanel();

    // strings for layout
    private static final String START_GAME = "startGame";
    private static final String MAIN_MENU = "mainMenu";
    private static final String WHITE_TRACKS = "whiteTracks";
    private static final String SETTINGS = "settings";

    public MenuManager() {
        CardLayout cl = new CardLayout();
        setLayout(cl);
        setBackground(Color.BLACK);

        // action listeners for tabbing between pages and such
        mainMenuPanel.addPlayButtonActionListener(e -> { setActivePanel(WHITE_TRACKS); });
        mainMenuPanel.addSettingsButtonActionListener(e -> { setActivePanel(SETTINGS); });

        // key listeners for tabbing between pages and such
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // these are to prevent typos
        final String ESC = "escape";

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESC);
        actionMap.put(ESC, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel activePanel = getActivePanel();

                // TODO: or any of the other track panels
                if (activePanel == whiteTracksPanel) { setActivePanel(MAIN_MENU); }
            }
        });

        // drawing
        // add all of them but only set the start game one to be active
        add(startGamePanel, START_GAME);
        add(mainMenuPanel, MAIN_MENU);
        add(whiteTracksPanel, WHITE_TRACKS);
        add(settingsPanel, SETTINGS);

        cl.show(this, START_GAME);
    }

    public void tick(double dt) {
        JPanel activePanel = getActivePanel();
        if (activePanel == startGamePanel) { startGamePanel.tick(dt); }
    }

    // getters
    private JPanel getActivePanel() {
        for (Component component : getComponents()) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;

                if (panel.isVisible()) { return panel; }
            }
        }

        return null;
    }
    public String toString() { return super.toString() + getActivePanel().toString(); }

    // setters
    private void setActivePanel(String name) {
        CardLayout cl = (CardLayout) getLayout();
        cl.show(this, name);
    }

    // helpers
    private static String[] generateTrackSetNames(String modifier) {
        String[] values = new String[10]; // create an array of the values
        // for each value fill it with a new String of modifier + number
        for (int i = 0; i < values.length; i++) { values[i] = modifier + (i + 1); }

        return values; // return it
    }
}
