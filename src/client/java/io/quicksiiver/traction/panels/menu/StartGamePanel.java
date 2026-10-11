package client.java.io.quicksiiver.traction.panels.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;

import client.java.io.quicksiiver.traction.Client;

public class StartGamePanel extends MenuPanel {
    private double frames = 0;

    public StartGamePanel(Runnable startGameAction) {
        setBackground(Color.BLACK);

        // Source - https://stackoverflow.com/a/18037609
        // Posted by Elist, modified by community. See post 'Timeline' for change history
        // Retrieved 2026-10-11, License - CC BY-SA 3.0
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                if (frames > 3 && ke.getID() == KeyEvent.KEY_PRESSED) { startGameAction.run(); }
                return false;
            }
        });
    }

    public void tick(double dt) {
        // everything here is physics and the like that should occur
        // at a constant rate
        frames += dt;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Point screenCenter = new Point(getWidth() / 2, getHeight() / 2);

        // draw title
        if (frames > 1.5) { drawTitle(g); } 
        // draw the press any key to start thing
        if (frames > 3) { // NOT else if, surprisingly
            Font font = new Font(Client.PIXEL_FONT_NAME, Font.PLAIN, 24);

            int x = screenCenter.x / 3;
            int y = (int) (screenCenter.y * 1.5);

            g.setFont(font);
            g.setColor(Color.LIGHT_GRAY);
            
            g.drawString("Press any key to start. . .", x, y);
        }
    }
}
