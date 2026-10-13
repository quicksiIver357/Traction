package client.java.io.quicksiiver.traction.panels.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import client.java.io.quicksiiver.traction.Client;
import client.java.io.quicksiiver.traction.misc.JGradientButton;

public class MainMenuPanel extends MenuPanel {
    private JGradientButton playButton = new JGradientButton("PLAY", BUTTON_GRADIENT_1, BUTTON_GRADIENT_2);
    private JGradientButton createButton = new JGradientButton("CREATE", BUTTON_GRADIENT_1, BUTTON_GRADIENT_2);
    private JGradientButton settingsButton = new JGradientButton("SETTINGS", BUTTON_GRADIENT_1, BUTTON_GRADIENT_2);

    private static final Color BUTTON_GRADIENT_1 = new Color(200, 200, 200);
    private static final Color BUTTON_GRADIENT_2 = new Color(160, 160, 160);

    public MainMenuPanel() {
        // various parts of the following constructor were taken from
        // https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/BoxLayoutDemoProject/src/layout/BoxLayoutDemo.java
        // wow thats a really long link
        // not sure if this is relevant anymore as I changed a lot but
        // I am too lasy to go check if it still applies so the link is staying

        // visuals
        setBackground(Color.BLACK);

        // create a layout
        BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(bl);

        // button settings
        Color buttonBorderColor = new Color(0, 0, 100);
        LineBorder buttonBorder = new LineBorder(buttonBorderColor, 3);
        int buttonSpacing = 20;

        // apply things to all of the buttons
        // Source - https://stackoverflow.com/a/3094980
        // Posted by EMurnane
        // Retrieved 2026-10-11, License - CC BY-SA 2.5
        playButton.setBorder(buttonBorder);
        resizeButton(playButton);
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        createButton.setBorder(buttonBorder);
        resizeButton(createButton);
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        settingsButton.setBorder(buttonBorder);
        resizeButton(settingsButton);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        // make the buttons do things when clicked
        // https://stackoverflow.com/questions/8852560/how-to-make-popup-window-in-java
        createButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, """
                Enter the name of the track you want to edit 
                (will create a new one if necessary):
            """, null);

            // validate the name
            boolean valid = true;
            String okCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-";
            for (char c : name.toCharArray()) {
                // if a char is not in the ok characters, its not ok
                if (!okCharacters.contains(String.valueOf(c))) {
                    valid = false;
                    break;
                }
            }

            // load the track, if the filename is valid
            if (valid) { Client.instance.openTrackFromName("custom/" + name, false); }
            else { // otherwise reprimand them for putting an invalid name
                JOptionPane.showMessageDialog(this, """
                    Please enter a valid name!

                    Can only contain:
                    - Letters
                    - Numbers
                    - Hyphens (-)
                """);
            }
        });

        // add the buttons
        add(Box.createVerticalGlue());
        add(Box.createRigidArea(new Dimension(0, buttonSpacing * 3)));
        add(playButton);
        add(Box.createRigidArea(new Dimension(0, buttonSpacing)));
        add(createButton);
        add(Box.createRigidArea(new Dimension(0, buttonSpacing)));
        add(settingsButton);
        add(Box.createVerticalGlue());
    }

    // setters
    public void addPlayButtonActionListener(ActionListener l) { playButton.addActionListener(l); }
    public void addSettingsButtonActionListener(ActionListener l) { settingsButton.addActionListener(l); }

    // helpers
    private void resizeButton(JButton b) {
        Dimension d = new Dimension(80, 60);

        b.setMaximumSize(d);
        b.setMinimumSize(d);
        b.setPreferredSize(d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTitle(g);
    }
}
