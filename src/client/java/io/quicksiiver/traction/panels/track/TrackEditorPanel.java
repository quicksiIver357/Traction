package client.java.io.quicksiiver.traction.panels.track;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import main.java.io.quicksiiver.traction.core.Track;
import main.java.io.quicksiiver.traction.core.tiles.Tile;
import main.java.io.quicksiiver.traction.core.tiles.specific.DriveableTile;
import main.java.io.quicksiiver.traction.core.tiles.specific.Wall;

public class TrackEditorPanel extends GenericTrackPanel {
    // information about the panel
    private double x;
    private double y;
    private double oldX;
    private double oldY;

    private Tile drawing = DriveableTile.DEFAULT;
    private boolean dragging = false;
    private boolean draggable = true;

    // drawing
    JTabbedPane sideBar = new JTabbedPane();

    // constants (adjustable)
    private static final int SIDEBAR_WIDTH = 150;

    public TrackEditorPanel(Track t) {
        super(t);

        // storage about the functinoality
        x = oldX = t.getStartX() * TILE_SIZE;
        y = oldY = t.getStartY() * TILE_SIZE;

        // drawing the ui and such
        setLayout(new BorderLayout());

        sideBar.add("Tiles", createJScrollPane(DriveableTile.getAllDriveableTiles().toArray(new Tile[0])));
        sideBar.add("Walls", createJScrollPane(Wall.getAllWalls().toArray(new Wall[0])));
        sideBar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, sideBar.getPreferredSize().height));

        add(sideBar, BorderLayout.EAST);

        // input hanlding
        // mouse
        MouseList m = new MouseList();
        addMouseListener(m);
        addMouseMotionListener(m);

        // keyboard
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // prevent typos
        final String LOCK_VIEWPOINT = "lockViewpoint";

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), LOCK_VIEWPOINT);
        
        actionMap.put(LOCK_VIEWPOINT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { draggable = !draggable; }
        });
    }

    // fulfill superclass requirements (DONT CHANGE)
    protected double getScreenX() { return x; }
    protected double getScreenY() { return y; }

    // helpers
    private JScrollPane createJScrollPane(Tile[] tiles) {
        // create an images arrray to store the tiles' images in
        ImageIcon[] images = new ImageIcon[tiles.length];

        // iterate over tiles and store the images
        for (int i = 0; i < tiles.length; i++) { images[i] = new ImageIcon(tiles[i].getPath()); }

        // start creation of the JScrollPane after Tiles array has been processed
        JScrollPane scrollPane = new JScrollPane();
        JPanel imagePanel = new JPanel();

        // scrollpane settings (makes them appear in a vertical fashion)
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        imagePanel.setLayout(new GridBagLayout());


        // iterate over the images and add them to the JScrollPane
        for (int i = 0; i < tiles.length; i++) {
            // wrap into a JButton for functionality before adding them to the JPanel
            ImageIcon image = images[i];
            // resize the image
            int buttonScale = SIDEBAR_WIDTH / 2 - 20;
            image.setImage(image.getImage().getScaledInstance(buttonScale, buttonScale, 0));
            // add it to the JButton
            JButton imageButton = new JButton(image);

            int ii = i; // prevents effectively final problems with lambda expression below
            imageButton.addActionListener(e -> { drawing = tiles[ii]; });

            // make it look nice ^_^
            imageButton.setBorderPainted(false);
            imageButton.setFocusPainted(false);
            imageButton.setContentAreaFilled(false);
            imageButton.setOpaque(false);
            imageButton.setMargin(new Insets(0, 0, 0, 0));

            
            // layout settings
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = i % 2;
            gbc.gridy = i / 2;
            gbc.anchor = GridBagConstraints.NORTH;

            imagePanel.add(imageButton, gbc);
        }

        // pushes all of the stuff to the top
        GridBagConstraints spacer = new GridBagConstraints();
        spacer.gridx = 0;
        spacer.gridy = (int) Math.ceil(tiles.length / 2.0);
        spacer.gridwidth = 2; // span both columns
        spacer.weighty = 1; // take all remaining vertical space
        spacer.fill = GridBagConstraints.BOTH;

        imagePanel.add(new JLabel(" "), spacer);

        scrollPane.setViewportView(imagePanel); // add the image panel
        return scrollPane; // and return the finished JScrollPane
    }

    // input handling
    private class MouseList implements MouseListener, MouseMotionListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point mousePos = e.getPoint();

            putTile(mousePos);
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            // preliminary calculations
            Point mousePos = e.getPoint();
            
            // drag logic
            if (draggable) {
                if (!dragging) {
                    oldX += mousePos.x / zoom;
                    oldY += mousePos.y / zoom;

                    dragging = true; 
                }

                x = oldX - mousePos.x / zoom;
                y = oldY - mousePos.y / zoom;   
            } else { putTile(mousePos); } // drawing tiles logic
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // end dragging logic
            if (dragging && draggable) {
                oldX = x;
                oldY = y;

                dragging = false;
            }
        }

        // helper methods
        private void putTile(Point mousePos) {
            // dont ask how it got off by 0.5 tiles
            int xIndex = (int) ( ( ( mousePos.x - getWidth() / 2 ) / zoom + x ) / TILE_SIZE + 0.5 );
            int yIndex = (int) ( ( ( mousePos.y - getHeight() / 2 ) / zoom + y ) / TILE_SIZE + 0.5 );

            // Tile oldTile = track.getTile(xIndex, yIndex);
            track.setElement(xIndex, yIndex, drawing);

            updateImages();

            // // update the images array
            // // if the old tile is no longer used in the Track remove it from the images
            // if (!track.contains(oldTile)) { images.remove((oldTile)); }
            // // if the new tile is not already in the array add it
            // boolean notAlreadyThere = true;
            // for (Tile tile : images.keySet()) {
            //     if (tile == drawing) {
            //         notAlreadyThere = false;
            //         break;
            //     }
            // }

            // if (notAlreadyThere) { images.put(drawing, new ImageIcon(drawing.getPath())); }
        }

        // satisfy implementations
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mousePressed(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}

        // helpers
        // private static boolean near(Point p1, Point p2, double r) {
        //     if (r*r >= Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2)) { return true; }
        //     else { return false; }
        // }
    }
}
