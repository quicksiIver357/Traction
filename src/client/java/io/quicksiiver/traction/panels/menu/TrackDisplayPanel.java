package client.java.io.quicksiiver.traction.panels.menu;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import client.java.io.quicksiiver.traction.Client;

public class TrackDisplayPanel extends JPanel {
    public TrackDisplayPanel(String[] trackNames, boolean custom) {
        setBackground(Color.BLACK);
        setLayout(new GridLayout(2, 5));

        // put in all of the tracks
        for (int i = 0; i < trackNames.length; i++) {
            JButton button = new JButton(trackNames[i]);

            final int j = i; // for lambda function (must be final or effectively final)
            button.addActionListener(e -> {
                Client.instance.openTrackFromName((custom ? "custom" : "built-in") + "/" + trackNames[j], true);
            });

            // TODO: style them
            add(button);
        }
    }
}
