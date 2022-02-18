package de.theoptik.fastparticles;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static de.theoptik.fastparticles.Launcher.HEIGHT;
import static de.theoptik.fastparticles.Launcher.WIDTH;

record Vector(double x, double y) {
};

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

    public void update(int gravX, int gravY, double drag, float[] gravityBuffer) {
        updateForce(gravX, gravY, gravityBuffer);
        updateVelocity(drag);
        updatePosition();
        keepParticlesInBoundry();
    }

    private void updateForce(int gravX, int gravY, float[] gravityBuffer) {
        final var force = calculateGravity(new Point2D(gravX, gravY));
        xForce = force.x();
        yForce = force.y();
        final var index = (int) x + (int) y * WIDTH;
        if (index - 1 >= 0) {
            xForce -= Math.sqrt(gravityBuffer[index - 1]);
        }
        if (index + 1 < gravityBuffer.length) {
            xForce += Math.sqrt(gravityBuffer[index + 1]);
        }
        if (index - WIDTH >= 0) {
            yForce -= Math.sqrt(gravityBuffer[index - WIDTH]);
        }
        if (index + WIDTH < gravityBuffer.length) {
            yForce += Math.sqrt(gravityBuffer[index + WIDTH]);
        }
    }

    private Vector calculateGravity(Point2D gravityCenter) {
        final var dx = gravityCenter.getX() - x;
        final var dy = gravityCenter.getY() - y;
        final var m = Math.max(10, Math.sqrt(dx * dx + dy * dy));
        return new Vector(dx / (m * m), dy / (m * m));
    }

    private void updatePosition() {
        x += xVel;
        y += yVel;
    }

    private void updateVelocity(double drag) {
        xVel += xForce;
        yVel += yForce;
        xForce = 0;
        yForce = 0;
        xVel -= xVel * drag;
        yVel -= yVel * drag;
    }

    private void keepParticlesInBoundry() {
        if (x < 0) {
            xVel *= -1;
            x = Math.abs(x);
            xVel *= 0.9;
        } else if (x >= WIDTH) {
            xVel *= -1;
            x = WIDTH - (x - WIDTH);
            xVel *= 0.9;
        }
        if (y < 0) {
            yVel *= -1;
            y = Math.abs(y);
            yVel *= 0.9;
        } else if (y >= HEIGHT) {
            yVel *= -1;
            y = HEIGHT - (y - HEIGHT);
            yVel *= 0.9;
        }
    }

    public void draw(int[] buffer, int[] gravityBuffer) {
        final var index = (int) x + WIDTH * (int) y;
        buffer[index] = 0xFF000000 | (int) (Math.min(1, Math.abs((yVel + xVel) / 2)) * 0xFF) << 16 | (int) (Math.min(1, Math.abs(yVel)) * 0xFF) << 8 | (int) (Math.min(1, Math.abs(xVel)) * 0xFF);
//        buffer[index] = 0xFF00FF00;

        gravityBuffer[index] += 1;
//        gravityBuffer[index] += 10<<16 | 10<<8 | 10;
//        gravityBuffer[index] |=  0xFF000000;
    }

    public void draw(GraphicsContext graphicsContext) {
        graphicsContext.setStroke(Color.LIMEGREEN);
        graphicsContext.strokeLine(x, y, x, y);
    }

    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                ", xVel=" + xVel +
                ", yVel=" + yVel +
                ", xForce=" + xForce +
                ", yForce=" + yForce +
                '}';
    }
}
