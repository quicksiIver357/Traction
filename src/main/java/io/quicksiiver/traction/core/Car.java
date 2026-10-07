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

        int accelerateInput = (w ? 1 : 0) - (s ? 1 : 0);
        int steerInput = (a ? 1 : 0) - (d ? 1 : 0);
        double speed = getSpeed();

        r -= (drifting ? 2 : 1) * steerInput * Math.sin( Math.PI / 3 * speed / MAX_SPEED ) * STEERING_STRENGTH * dt;
        r += vr * dt;

        double ax = accelerateInput * ACCELERATION * Math.sin(-r) * // calculate acceleration
        (accelerateInput != 0 && Math.signum(accelerateInput) == Math.signum(vx) ? 1.5 : 1) - // make it easier to slow down than to speed up
        vx * DRAG_RATE * grip * (accelerateInput != 0 ? 1 : 5); // add drag

        double ay = accelerateInput * ACCELERATION * Math.cos(-r) * 
        (accelerateInput != 0 && Math.signum(accelerateInput) == Math.signum(vy) ? 1.5 : 1) - 
        vy * DRAG_RATE * grip * (accelerateInput != 0 ? 1 : 5);

        // velocity
        vx += ax * dt;
        vy += ay * dt;

        // recompute speed
        speed = getSpeed();

        // add a little sideways drag if not drifting
        if (speed != 0 && !drifting) {
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
            sideSpeed -= sideSpeed * DRAG_RATE * grip * (accelerateInput != 0 ? 1 : 3) / 10;

            // recompute vx and vy after the application
            vx = frontSpeed * -Math.sin(r) - sideSpeed * Math.cos(r);
            vy = frontSpeed * Math.cos(r) + sideSpeed * -Math.sin(r);

            // recalculate speed
            speed = getSpeed();
        }
        
        // ------------------------------------------
        // do calculations for collision with walls
        // ------------------------------------------

        double futureX = x + vx * dt; // future x (after 1 frame of movement)
        double futureY = y + vy * dt; // future y

        double xOffset1 = Math.sin(r) * HEIGHT / 2;
        double xOffset2 = Math.cos(r) * WIDTH / 2;
        double yOffset1 = Math.cos(r) * HEIGHT / 2;
        double yOffset2 = Math.sin(r) * WIDTH / 2;

        double topLeftCornerX = futureX + xOffset1 - xOffset2;
        double topRightCornerX = futureX + xOffset1 + xOffset2;
        double bottomLeftCornerX = futureX - xOffset1 - xOffset2;
        double bottomRightCornerX = futureX - xOffset1 + xOffset2;
        double topLeftCornerY = futureY + yOffset1 + yOffset2;
        double topRightCornerY = futureY + yOffset1 - yOffset2;
        double bottomLeftCornerY = futureY - yOffset1 + yOffset2;
        double bottomRightCornerY = futureY - yOffset1 - yOffset2;

        Tile topLeftCornerTile = getTileFromScreenCoords(t, topLeftCornerX, topLeftCornerY);
        Tile topRightCornerTile = getTileFromScreenCoords(t, topRightCornerX, topRightCornerY);
        Tile bottomLeftCornerTile = getTileFromScreenCoords(t, bottomLeftCornerX, bottomLeftCornerY);
        Tile bottomRightCornerTile = getTileFromScreenCoords(t, bottomRightCornerX, bottomRightCornerY);

        // TODO: add wall collision


        // -----------------------------------------
        // end calculations
        // ----------------------------------------

        // update position
        x += vx * dt;
        y += vy * dt;
        r += vr * dt;
    }

    // public void processInputs(Track t, double grip, double dt) {
    //     // do calculations
    //     double speed = Math.sqrt(vx * vx + vy * vy);

    //     double futureX = x + vx * dt; // future x (after 1 frame of movement)
    //     double futureY = y + vy * dt; // future y

    //     int xIndex = (int) ( x / TrackPanel.TILE_SIZE );
    //     int yIndex = (int) ( y / TrackPanel.TILE_SIZE );

    //     Tile currentTile = t.getTile(xIndex, yIndex);

    //     double xOffset1 = Math.sin(r) * HEIGHT / 2;
    //     double xOffset2 = Math.cos(r) * WIDTH / 2;
    //     double yOffset1 = Math.cos(r) * HEIGHT / 2;
    //     double yOffset2 = Math.sin(r) * WIDTH / 2;

    //     double topLeftCornerX = futureX + xOffset1 - xOffset2;
    //     double topRightCornerX = futureX + xOffset1 + xOffset2;
    //     double bottomLeftCornerX = futureX - xOffset1 - xOffset2;
    //     double bottomRightCornerX = futureX - xOffset1 + xOffset2;
    //     double topLeftCornerY = futureY + yOffset1 + yOffset2;
    //     double topRightCornerY = futureY + yOffset1 - yOffset2;
    //     double bottomLeftCornerY = futureY - yOffset1 + yOffset2;
    //     double bottomRightCornerY = futureY - yOffset1 - yOffset2;

    //     Tile topLeftCornerTile = getTileFromScreenCoords(t, topLeftCornerX, topLeftCornerY);
    //     Tile topRightCornerTile = getTileFromScreenCoords(t, topRightCornerX, topRightCornerY);
    //     Tile bottomLeftCornerTile = getTileFromScreenCoords(t, bottomLeftCornerX, bottomLeftCornerY);
    //     Tile bottomRightCornerTile = getTileFromScreenCoords(t, bottomRightCornerX, bottomRightCornerY);

    //     // handle collisions with walls
    //     boolean clockwise = true;
    //     for (Tile tile : new Tile[] {
    //         // loop through each tile IN A SPECIFIC ORDER for the clockwise stuff
    //         topLeftCornerTile,
    //         topRightCornerTile,
    //         bottomLeftCornerTile,
    //         bottomRightCornerTile
    //     }) {
    //         // hit the wall if necessary
    //         if (tile instanceof Wall) { hitWall(tile, speed, dt, clockwise); }

    //         // flip between clockwise and counterclockwise rotation on collision
    //         clockwise = !clockwise;
    //     }

    //     // acceleration
    //     int accelerationMultiplier = (w ? 1 : 0) - (s ? 1 : 0);
    //     double ax = accelerationMultiplier * ACCELERATION * Math.sin(r);
    //     double ay = accelerationMultiplier * ACCELERATION * Math.cos(r);

    //     // velocity
    //     vx += ax * dt;
    //     vy += ay * dt;

    //     // apply drag
    //     vx *= (1 - DRAG_RATE * dt);
    //     vy *= (1 - DRAG_RATE * dt);

    //     // update position
    //     x += vx * dt;
    //     y += vy * dt;
    //     r += vr * dt;
    // }

    // helpers
    private static Tile getTileFromScreenCoords(Track t, double x, double y) {
        int xIndex = (int) ( x / TrackPanel.TILE_SIZE );
        int yIndex = (int) ( y / TrackPanel.TILE_SIZE );

        return t.getTile(xIndex, yIndex);
    }
    private void hitWall(Wall wall, double speed, double dt, boolean clockwiseRotation) {
        // calculations
        double bouncyness = (wall).getBouncyness();
        // double bounceCoefficient = bouncyness * speed * dt;

        // TODO: rotation after hitting wall 
        // vr += (clockwiseRotation ? -1 : 1) * bounceCoefficient * ROTATION_IMPULSE;

        // x -= bounceCoefficient * vx;
        // y -= bounceCoefficient * vy;

        // DONT just multiply by bouncyness, do it relative to the wall colision,
        // and then decrease the speed of the car paralell to the wall anyways
        // because if you didnt then you could drive against the wall just fine

        vx *= bouncyness;
        vy *= bouncyness;
    }
    // private double getBouncyness();

    // public void processInputs(double grip, double dt) { // dt is the timestep
    //     // System.out.println("w: " + w); // ok this was me being a dumbass and forgetting to add the part where it moves, please save it so you can laugh about it later

    //     // calculations
    //     int steerInput = ( a ? 1 : 0 ) + ( d ? -1 : 0 );
    //     int movementInput = ( w ? -1 : 0 ) + ( s ? 1 : 0 );

    //     if (s && w) { steerInput *= 2; }

    //     double speed = Math.sqrt(vx * vx + vy * vy);
    //     double angleOffset = Math.atan2(vy, vx) - rotation;
    //     double preSideMomentum = speed * Math.sin(angleOffset);
    //     double frontMomentum = speed * Math.cos(angleOffset);

    //     double speedMagnitude = movementInput * ACCELERATION * dt * Math.pow( 1 - speed / MAX_SPEED, 1 / ( speed * speed + 1 ) );
    //     double drag = dt * grip * GRIP_STRENGTH * DRAG_RATE * speed / MAX_SPEED * Math.signum(frontMomentum);
    //     // double drag = Math.exp( ( 1 + grip / ( MAX_SPEED - speed ) ) * dt );

    //     frontMomentum -= drag;
    //     System.out.println("test: " + Math.pow( 1 - speed / MAX_SPEED, 1 / ( speed + 1 ) ));
    //     double sideMomentum = Math.abs(preSideMomentum) > 50 * grip ? preSideMomentum -  preSideMomentum * grip * GRIP_STRENGTH * dt : 0;
    //     double steering = Math.sin( Math.PI / 2 * speed / MAX_SPEED ) * steerInput * STEERING_STRENGTH * dt;
    //     // modify car variables
    //     // calculate new velocity based on grip
    //     vx = frontMomentum * Math.cos(rotation) - sideMomentum * Math.sin(rotation);
    //     vy = frontMomentum * Math.sin(rotation) + sideMomentum * Math.cos(rotation);

    //     // then modify that if they are accelerating or deaccelerating
    //     vx += Math.cos(rotation) * speedMagnitude;
    //     vy += Math.sin(rotation) * speedMagnitude;

    //     // then modify that to deaccelerate over time
    //     // vx /= drag;
    //     // vy /= drag;

    //     // and finally adjust it to 0 if it is very close to 0 (prevents drift over long periods of time)
    //     if (Math.abs(vx) < 0.0001) { vx = 0; }
    //     if (Math.abs(vy) < 0.0001) { vy = 0; }

    //     x += vx * dt;
    //     y += vy * dt;

    //     rotation += steering * Math.signum(frontMomentum);
    // }

    

    // setters
    public void setWPressed(boolean pressed) { w = pressed; }
    public void setAPressed(boolean pressed) { a = pressed; }
    public void setSPressed(boolean pressed) { s = pressed; }
    public void setDPressed(boolean pressed) { d = pressed; }
    public void setRotation(double rotation) { r = rotation; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getRotation() { return r; }
    public double getSpeed() { return Math.sqrt(vx*vx + vy*vy); }
}
