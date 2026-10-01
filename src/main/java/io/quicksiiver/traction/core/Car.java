package main.java.io.quicksiiver.traction.core;

public class Car {
    private double vx; // x velocity
    private double vy; // y velocity

    private double x;
    private double y;

    private double rotation; // out of 2pi

    private double acceleration;
    private double maxSpeed = 1000;

    private static final double MAX_STEERING_ANGLE = 0.4;
    private static final double STEERING_STRENGTH = 0.5;

    public void processInputs(boolean w, boolean a, boolean s, boolean d, double grip, double dt) { // dt is the timestep
        // calculations
        double steering = MAX_STEERING_ANGLE * ( ( a ? -1 : 0 ) + ( d ? 1 : 0 ) );

        double angleOffset = Math.atan2(vy, vx) - rotation;
        double speed = Math.sqrt(vx * vx + vy * vy);

        double sideMomentum = speed * Math.sin(angleOffset) * ( 1 - grip );
        double frontMomentum = speed * Math.cos(angleOffset);

        // modify car variables
        vx = frontMomentum * Math.cos(rotation) - sideMomentum * Math.sin(rotation);
        vy = frontMomentum * Math.sin(rotation) + sideMomentum * Math.cos(rotation);

        x += vx * dt;
        y += vy * dt;

        rotation += steering * frontMomentum * STEERING_STRENGTH * dt;
    }
}
