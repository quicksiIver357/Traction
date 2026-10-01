package main.java.io.quicksiiver.traction.core;

public class Car {
    private double vx; // x velocity
    private double vy; // y velocity

    private double x;
    private double y;

    private double rotation; // out of 2pi

    private double acceleration = 20;
    private double maxSpeed = 1000;

    // keys pressed
    private boolean w;
    private boolean a;
    private boolean s;
    private boolean d;

    private static final double MAX_STEERING_ANGLE = 0.4;
    private static final double STEERING_STRENGTH = 0.5;

    public void processInputs(double grip, double dt) { // dt is the timestep
        // System.out.println("w: " + w); // ok this was me being a dumbass and forgetting to add the part where it moves, please save it so you can laugh about it later

        // calculations
        double steering = MAX_STEERING_ANGLE * ( ( a ? -1 : 0 ) + ( d ? 1 : 0 ) );

        double angleOffset = Math.atan2(vy, vx) - rotation;
        double speed = Math.sqrt(vx * vx + vy * vy);
        double speedMagnitude = ( ( w ? 1 : 0 ) + ( s ? -1 : 0 ) ) * acceleration * dt;
        double deaccelerate = 1 + grip / ( maxSpeed - speed );

        double sideMomentum = speed * Math.sin(angleOffset) * ( 1 - grip );
        double frontMomentum = speed * Math.cos(angleOffset);

        // modify car variables
        // calculate new velocity based on grip
        vx = frontMomentum * Math.cos(rotation) - sideMomentum * Math.sin(rotation);
        vy = frontMomentum * Math.sin(rotation) + sideMomentum * Math.cos(rotation);

        // then modify that if they are accelerating or deaccelerating
        vx += Math.cos(rotation) * speedMagnitude;
        vx += Math.sin(rotation) * speedMagnitude;

        // then modify that to deaccelerate over time
        vx /= deaccelerate;
        vy /= deaccelerate;

        // and finally adjust it to 0 if it is very close to 0 (prevents drift over long periods of time)
        if (Math.abs(vx) < 0.0001) { vx = 0; }
        if (Math.abs(vy) < 0.0001) { vy = 0; }

        x += vx * dt;
        y += vy * dt;

        rotation += steering * frontMomentum * STEERING_STRENGTH * dt;
    }

    // setters
    public void setWPressed(boolean pressed) { w = pressed; }
    public void setAPressed(boolean pressed) { a = pressed; }
    public void setSPressed(boolean pressed) { s = pressed; }
    public void setDPressed(boolean pressed) { d = pressed; }

    // getters
    public double getX() { return x; }
    public double getY() { return y; }
}
