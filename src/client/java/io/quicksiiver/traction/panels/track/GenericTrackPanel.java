package client.java.io.quicksiiver.traction.panels.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import main.java.io.quicksiiver.traction.core.Track;
import main.java.io.quicksiiver.traction.core.tiles.Tile;

public abstract class GenericTrackPanel extends JPanel {
    protected Track track;
    protected double zoom = 1; // 100%

    // improve performance by only loading the images of the tiles
    // once a new track is loaded.
    protected HashMap<Tile, ImageIcon> images;

    // debug
    protected boolean paused = false;

    // prevent magic numbers
    public static final int TILE_SIZE = 80;
    private static final double MIN_ZOOM = 0.3;
    private static final double MAX_ZOOM = 2;
    private static final double SCROLL_SPEED = 0.05;

    public GenericTrackPanel(Track t) { 
        setBackground(Color.BLACK);
        
        track = t.clone(); 

        // load all of the images
        images = new HashMap<>();
        updateImages();

        // mouse input handling
        MouseWheelList mouseWheelList = new MouseWheelList();
        addMouseWheelListener(mouseWheelList);
        
        // keyboard input handling
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // prevents typos
        final String DEBUG_PAUSE = "pause";
        final String RELEASE = "_released";

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), DEBUG_PAUSE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), DEBUG_PAUSE + RELEASE);
        
        actionMap.put(DEBUG_PAUSE, new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) { paused = true; }
        });
        actionMap.put(DEBUG_PAUSE + RELEASE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { paused = false; }
        });
    }

    public void tick(double dt) {
        // put here everything that should happen each frame (besides rendering)
        if (!paused) { 
            // process inputs only if the game has started
        } else {
            // display debug info and such
        }
    }

    // helpers
    protected int getTileX() { return (int) ( getScreenX() / TILE_SIZE ); }
    protected int getTileY() { return (int) ( getScreenY() / TILE_SIZE ); }
    protected abstract double getScreenX();
    protected abstract double getScreenY();

    // setters
    public void setTrack(Track t) { 
        track = t;

        // load all of the images
        images = new HashMap<>();
        updateImages();
    }
    protected void updateImages() {
        for (Tile[] column : track.getMap()) {
            for (Tile tile : column) {
                if (!images.containsKey(tile)) { images.put(tile, new ImageIcon(tile.getPath())); } // if not already loaded, load it
            }
        }
    }
    public void setZoom(double d) { 
        zoom = d; 

        // clamp to max and min zoom
        if (zoom < MIN_ZOOM) { zoom = MIN_ZOOM; }
        else if (zoom > MAX_ZOOM) { zoom = MAX_ZOOM; }
    }


    // getters
    public Track getTrack() { return track; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        drawTiles(g2d);
    }

    // drawing helpers
    private void drawTiles(Graphics2D g2d) {
        Point screenCenter = new Point(getWidth() / 2, getHeight() / 2);
        int tileImageScale = (int) (zoom * TILE_SIZE) + 1;

        // draw each tile
        for (int i = 0; i < track.getWidth(); i++) {            
            double xCenter = screenCenter.x - getScreenX() * zoom;
            double xRelative = TILE_SIZE * zoom * i;
            int x = (int) ((xCenter + xRelative - tileImageScale / 2.0));

            // don't draw it becuase it's off the screen
            if (x + TILE_SIZE * zoom + 1 < 0 || x > getWidth()) { continue; }

            // otherwise move on
            for (int j = 0; j < track.getHeight(); j++) {       
                double yCenter = screenCenter.y - getScreenY() * zoom;
                double yRelative = TILE_SIZE * zoom * j;
                int y = (int) ((yCenter + yRelative - tileImageScale / 2.0));

                // don't draw it because it's off the screen
                if (y + TILE_SIZE * zoom + 1 < 0 || y > getHeight()) { continue; }

                Tile currentTile = track.getTile(i, j);
                ImageIcon toDraw = images.get(currentTile);
                g2d.drawImage(toDraw.getImage(), x, y, tileImageScale, tileImageScale, null);
            }
        }
    }

    // input
    protected class MouseWheelList implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double scrollAmount = e.getPreciseWheelRotation();

            setZoom(zoom - scrollAmount * SCROLL_SPEED);
        }
        
    }
}
