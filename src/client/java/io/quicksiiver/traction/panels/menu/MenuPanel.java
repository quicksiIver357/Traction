package client.java.io.quicksiiver.traction.panels.menu;

import java.awt.Graphics;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

// includes a bunch of helper methods that might need to be used across panels
public abstract class MenuPanel extends JPanel {
    // images that need to be loaded
    public static final String ASSETS_FOLDER = "src/main/resources/assets/menu/";
    public static final String SHARED_FOLDER = ASSETS_FOLDER + "shared/";

    private static final ImageIcon TITLE = new ImageIcon(SHARED_FOLDER + "title.gif");

    protected void drawTitle(Graphics g) {
        Point screenCenter = new Point(getWidth() / 2, getHeight() / 2);

        // relative to the top left of the screen, if it was placed there
        Point titleCenter = new Point(TITLE.getIconWidth() / 2, TITLE.getIconHeight() / 2);

        int x = screenCenter.x - titleCenter.x;
        int y = screenCenter.y / 3 - titleCenter.y;

        g.drawImage(TITLE.getImage(), x, y, null);
    }
}
