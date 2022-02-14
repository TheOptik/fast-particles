package de.theoptik.fastparticles;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static de.theoptik.fastparticles.Launcher.HEIGHT;
import static de.theoptik.fastparticles.Launcher.WIDTH;

public class Particle {

    private double x;
    private double y;
    private double xVel;
    private double yVel;
    private double xForce;
    private double yForce;

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
        xVel = 0;//getRandomVelocity() * 3;
        yVel = 0;//getRandomVelocity() * 3;
    }

    private double getRandomVelocity() {
        return Math.random() - 0.5;
    }

    public void update(int gravX, int gravY) {
        updateForce(gravX, gravY);
        updateVelocity();
        updatePosition();
        keepParticlesInBoundry();
    }

    private void updateForce(int gravX, int gravY) {
        final var force = calculateGravity(new Point2D(gravX, gravY));
        xForce = force[0];
        yForce = force[1];
    }

    private double[] calculateGravity(Point2D gravityCenter) {
        final var dx = gravityCenter.getX() - x;
        final var dy = gravityCenter.getY() - y;
        final var m = Math.max(1, Math.sqrt(dx * dx + dy * dy));
        return new double[]{dx / (m * m), dy / (m * m)};
    }

    private void updatePosition() {
        x += xVel;
        y += yVel;
    }

    private void updateVelocity() {
        xVel += xForce;
        yVel += yForce;
        xForce = 0;
        yForce = 0;
    }

    private void keepParticlesInBoundry() {
        if (x < 0 || x >= WIDTH) {
            xVel *= -1;
            x += xVel;
            xVel *= 0.9;
        }
        if (y < 0 || y >= HEIGHT) {
            yVel *= -1;
            y += yVel;
            yVel *= 0.9;
        }
    }

    public void draw(int[] buffer) {
        buffer[(int) x + WIDTH * (int) y] = 0xFF00FF00;
    }

    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.LIMEGREEN);
        graphicsContext.strokeLine(x, y, x, y);
    }

}
