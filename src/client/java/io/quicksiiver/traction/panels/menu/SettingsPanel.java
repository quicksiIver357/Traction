package client.java.io.quicksiiver.traction.panels.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import main.java.io.quicksiiver.traction.Main;

public class SettingsPanel extends MenuPanel {
    // classifiers
    private static final String PB = "pb";
    private static final String BRONZE = "bronze";
    private static final String SILVER = "silver";
    private static final String GOLD = "gold";
    private static final String AUTHOR = "author";

    public SettingsPanel() {
        setBackground(Color.BLACK);

        // create all of the layout stuff
        GridLayout layout = new GridLayout(1, 2);
        setLayout(layout);

        setBorder(new EmptyBorder(200, 50, 100, 50)); // add padding from sides

        // create all of the buttons
        JButton toggleGhostTypeButton = new JButton(PB.toUpperCase());

        // make the buttons functional
        toggleGhostTypeButton.addActionListener(e -> {
            switch (toggleGhostTypeButton.getText()) {
                case PB -> { toggleGhostTypeButtonActionHelper(toggleGhostTypeButton, BRONZE); } 
                case BRONZE -> { toggleGhostTypeButtonActionHelper(toggleGhostTypeButton, SILVER); }
                case SILVER -> { toggleGhostTypeButtonActionHelper(toggleGhostTypeButton, GOLD); }
                case GOLD -> { toggleGhostTypeButtonActionHelper(toggleGhostTypeButton, AUTHOR); }
                case AUTHOR -> { toggleGhostTypeButtonActionHelper(toggleGhostTypeButton, PB); }
            }
        });

        // add all of the buttons
        add(toggleGhostTypeButton);   
    }

    // helpers
    private void toggleGhostTypeButtonActionHelper(JButton b, String s) {
        // TODO: fix bug of not working AT ALL
        
        final String GHOST = "ghost";

        b.setText(s.toUpperCase());
        Main.modifySettings(GHOST, s);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle(g);
    }
}
