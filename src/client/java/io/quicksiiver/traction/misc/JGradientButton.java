package client.java.io.quicksiiver.traction.misc;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// Source - https://stackoverflow.com/a/42007820
// Posted by Mr. Polywhirl
// Retrieved 2026-10-11, License - CC BY-SA 3.0
public class JGradientButton extends JButton {
    private static final long serialVersionUID = 1L;
    private Color stopTop;
    private Color stopBottom;
    private Paint colorGradient;
    private Point[] stopPoints = new Point[2];

    public JGradientButton(Color stopTop, Color stopBottom) {
        this("", stopTop, stopBottom);
    }

    public JGradientButton(String text, Color stopTop, Color stopBottom) {
        this(text, null, stopTop, stopBottom);
    }

    public JGradientButton(String text, Icon icon, Color stopTop, Color stopBottom) {
        super(text, icon);

        setContentAreaFilled(false);
        setFocusPainted(false);

        this.stopTop = stopTop;
        this.stopBottom = stopBottom;

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                invalidate();
            }
        });
    }

    public void invalidate() {
        super.invalidate();

        stopPoints[0] = new Point(0, 0);
        stopPoints[1] = new Point(0, getHeight());

        if (getModel().isPressed()) {
            colorGradient = new GradientPaint(stopPoints[0], stopBottom, stopPoints[1], stopTop);
        } else {
            colorGradient = new GradientPaint(stopPoints[0], stopTop, stopPoints[1], stopBottom);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setPaint(colorGradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }
}
