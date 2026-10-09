package main.java.io.quicksiiver.traction.core;

import javax.swing.ImageIcon;

import client.java.io.quicksiiver.traction.panels.track.TrackPanel;
import main.java.io.quicksiiver.traction.core.tiles.Tile;
import main.java.io.quicksiiver.traction.core.tiles.Wall;

public class Car {
    private double vx; // x velocity
    private double vy; // y velocity

    private double x;
    private double y;

    private double r; // rotation out of 2pi
    private double vr; // rotational momentum

    // keys pressed
    private boolean w;
    private boolean a;
    private boolean s;
    private boolean d;

    // constants among the car
    private static final double STEERING_STRENGTH = 1;
    private static final double ACCELERATION = 500;
    private static final double MAX_SPEED = 1000;
    private static final double GRIP_STRENGTH = 1;
    private static final double DRAG_RATE = 0.2;
    
    private static final int WIDTH = TrackPanel.CAR_SIZE / 2;
    private static final int HEIGHT = TrackPanel.CAR_SIZE;

    // classifiers
    private static final String IMG_FILEPATH = "src/main/resources/assets/other/car.png";

    // useful things to save performance
    public static final ImageIcon IMAGE = new ImageIcon(IMG_FILEPATH);

    public void processInputs(final Track t, double grip, final double dt) {
        // do calculations
        grip *= GRIP_STRENGTH;
        boolean drifting = w && s;
        if (drifting) { grip /= 2; }

        int steerInput = (a ? 1 : 0) - (d ? 1 : 0);
        double speed = getSpeed();

        r -= (drifting ? 2.5 : 1) * // more powerful turning while drifting
        steerInput * Math.sin( Math.PI / 3 * speed / MAX_SPEED ) * STEERING_STRENGTH * dt; // calculate turn amount

        double ax = calculateAcceleration(grip, vx, true);
        double ay = calculateAcceleration(grip, vy, false);

        // velocity
        vx += ax * dt;
        vy += ay * dt;

        // recompute speed
        speed = getSpeed();

        // add a little sideways drag if not drifting
        if (speed != 0 && !drifting) { applyGrip(grip); }
        applyCollisionLogic(t, dt);

        // update position
        x += vx * dt;
        y += vy * dt;
        r += vr * dt;
    }


    // helpers
    private static int getIndexFromScreenValue(double v) { return (int) ( v / TrackPanel.TILE_SIZE ); }
    private void applyGrip(double grip) {
        double speed = getSpeed();

        // calculate the difference between the movement of the car
        // and the direction that it is facing, same as rotating the 
        // xy plane to match that of the car's momentum
        double oldAngle = Math.atan2(vy, vx);
        double angleOffset = r - oldAngle + Math.PI / 2;
        angleOffset = Math.atan2(Math.sin(angleOffset), Math.cos(angleOffset)); // normalize [-pi, pi]
        
        // calculate how much the car is sliding sideways and how
        // much is going forward
        double sideSpeed = speed * Math.sin(angleOffset);
        double frontSpeed = speed * Math.cos(angleOffset);

        // apply some extra drag to the car on the sideways axis
        sideSpeed -= sideSpeed * DRAG_RATE * grip * 0.3; // DONT multiply by dt
        speed = Math.sqrt(frontSpeed*frontSpeed + sideSpeed*sideSpeed);

        // replace r with -r to rotate the xy plane in the opposite direction,
        // back to the world space
        angleOffset = -r - Math.atan2(frontSpeed, sideSpeed) + Math.PI / 2;

        // recompute vx and vy after the application
        vx = speed * Math.sin(angleOffset);
        vy = speed * Math.cos(angleOffset);

        // recalculate speed
        speed = getSpeed();
    }
    private boolean collidesAt(Track t, double x, double y) {
        return t.getTile(getIndexFromScreenValue(x), getIndexFromScreenValue(y)) instanceof Wall;
    }
    private double getBouncyness(Track t, double x, double y) {
        int xIndex = getIndexFromScreenValue(x);
        int yIndex = getIndexFromScreenValue(y);
        Tile tile = t.getTile(xIndex, yIndex);

        if (tile instanceof Wall) { return ((Wall) tile).getBouncyness(); }
        else { return -1; } // incase not a wall this will keep the momentum in the correct direction
    }
    private void applyCollisionLogic(Track t, double dt) {
        // oh god i was somehow off by half a tile so this is a bad fix
        double x = this.x + TrackPanel.TILE_SIZE / 2;
        double y = this.y + TrackPanel.TILE_SIZE / 2;

        double xOffset1 = Math.sin(r) * HEIGHT / 2;
        double xOffset2 = Math.cos(r) * WIDTH / 2;
        double yOffset1 = Math.cos(r) * HEIGHT / 2;
        double yOffset2 = Math.sin(r) * WIDTH / 2;

        double topLeftCornerX = x + xOffset1 - xOffset2;
        double topRightCornerX = x + xOffset1 + xOffset2;
        double bottomLeftCornerX = x - xOffset1 - xOffset2;
        double bottomRightCornerX = x - xOffset1 + xOffset2;
        double topLeftCornerY = y + yOffset1 + yOffset2;
        double topRightCornerY = y + yOffset1 - yOffset2;
        double bottomLeftCornerY = y - yOffset1 + yOffset2;
        double bottomRightCornerY = y - yOffset1 - yOffset2;
;
        double[] cornerXs = {topLeftCornerX, topRightCornerX, bottomLeftCornerX, bottomRightCornerX};
        double[] cornerYs = {topLeftCornerY, topRightCornerY, bottomLeftCornerY, bottomRightCornerY};
        boolean colliding = false;

        // NOTE: collision logic was mostly taken from 2026
        // Graphics final project 2nd place winner DRIFT GAME.
        // apply the collisions
        for (int i = 0; i < cornerXs.length; i++) {
            double futureCornerX = cornerXs[i] + vx * dt;
            double futureCornerY = cornerYs[i] + vy * dt;

            if (collidesAt(t, futureCornerX, cornerYs[i])) {
                double bouncyness = getBouncyness(t, futureCornerX, cornerYs[i]);

                vx *= -bouncyness; // bounce off
                vy *= bouncyness * ( 1 - dt ); // grind against wall
                colliding = true;
            } if (collidesAt(t, cornerXs[i], futureCornerY)) { // should NOT be else if
                double bouncyness = getBouncyness(t, cornerXs[i], futureCornerY);

                vx *= bouncyness * ( 1 - dt );
                vy *= -bouncyness;
                colliding = true;
            }

            // then nudge the car out of the wall
            if (!colliding) { continue; } // yo continue is goated
            for (int attempts = 0; collidesAt(t, x, y) && attempts < 10; attempts++) {
                double leniency = 0.1;
                double speed = getSpeed();
                double shiftAmount = speed * leniency;

                // Try nudging the car slightly to the right
                if (!collidesAt(t, x + shiftAmount, y)) { x += shiftAmount; }
                // left 
                else if (!collidesAt(t, x - shiftAmount, y)) { x -= shiftAmount; }
                // down 
                else if (!collidesAt(t, x, y + shiftAmount)) { y += shiftAmount; } 
                // up
                else {
                    if (collidesAt(t, x, y - shiftAmount)) { break; } // No escape possible
                    y -= shiftAmount; // otherwise nudge
                }
            }

        }
    }
    private double calculateAcceleration(double grip, double directionalVelocity, boolean sin) {
        int accelerateInput = (w ? 1 : 0) - (s ? 1 : 0);

        // calculate acceleration
        double rawAcceleration = accelerateInput * ACCELERATION * (sin ? Math.sin(-r) : Math.cos(-r));
        // make it easier to slow down than to speed up
        double slowdownFactor = accelerateInput != 0 && Math.signum(accelerateInput) == Math.signum(directionalVelocity) ? 1.5 : 1;
        // add drag
        double drag = directionalVelocity * DRAG_RATE * grip * (accelerateInput != 0 ? 1 : 3);

        return (rawAcceleration * slowdownFactor - drag) * grip; // multiply it all by grip to get accleration
    }

    // setters
    public void setWPressed(boolean pressed) { w = pressed; }
    public void setAPressed(boolean pressed) { a = pressed; }
    public void setSPressed(boolean pressed) { s = pressed; }
    public void setDPressed(boolean pressed) { d = pressed; }
    public void setRotation(double rotation) { r = rotation; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void resetVelocity() { vx = vy = 0; }

    // getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getRotation() { return r; }
    public double getSpeed() { return Math.sqrt(vx*vx + vy*vy); }
}
