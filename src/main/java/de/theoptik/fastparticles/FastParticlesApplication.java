package de.theoptik.fastparticles;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.theoptik.fastparticles.Launcher.HEIGHT;
import static de.theoptik.fastparticles.Launcher.WIDTH;
import static javafx.scene.image.PixelFormat.getIntArgbInstance;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_32S;


public class FastParticlesApplication extends Application {

    private static int mouseX = Integer.MAX_VALUE;
    private static int mouseY = Integer.MAX_VALUE;
    private static double drag = 0;
    private static boolean showGravityField = false;

    public static void start(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        final var particles = IntStream.range(0, 500000)
                .mapToObj((i) -> new Particle(Math.cos(i) * 300 + WIDTH / 2, Math.sin(i) * 300 + HEIGHT / 2))
                .collect(Collectors.toList());

        final var canvas = new Canvas(WIDTH, HEIGHT);
        final var root = new VBox(canvas);
        root.setStyle("-fx-background-color: black");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

        primaryStage.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            mouseX = (int) e.getX();
            mouseY = (int) e.getY();
        });

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.SPACE) {
                mouseX = Integer.MAX_VALUE;
                mouseY = Integer.MAX_VALUE;
            }
            if (e.getCode() == KeyCode.UP) {
                drag += 0.01;
            }
            if (e.getCode() == KeyCode.DOWN) {
                drag -= 0.01;
            }
            if (e.getCode() == KeyCode.G) {
                showGravityField = !showGravityField;
            }
        });


        final var animation = new AnimationTimer() {

            final float[] gravityBuffer = new float[WIDTH * HEIGHT];

            @Override
            public void handle(long now) {
                final var buffer = new int[WIDTH * HEIGHT];
                final var newGravityBuffer = new int[WIDTH * HEIGHT];
                particles.parallelStream().forEach(p -> {
                    p.update(mouseX, mouseY, drag, gravityBuffer);
                    p.draw(buffer, newGravityBuffer);
                });
                canvas.getGraphicsContext2D()
                        .getPixelWriter()
                        .setPixels(0, 0, WIDTH, HEIGHT, getIntArgbInstance(), buffer, 0, WIDTH);

                final var src = new Mat(HEIGHT, WIDTH, CV_32S);
                final var src2 = new Mat(HEIGHT, WIDTH, CV_32F);
                src.put(0, 0, newGravityBuffer);
                src.convertTo(src2, CV_32F);

                final var dest = new Mat(HEIGHT, WIDTH, CV_32F);

                Imgproc.GaussianBlur(src2, dest, new Size(71, 71), 0);

                if (showGravityField) {
                    drawGravityField(dest, canvas);
                }
                dest.get(0, 0, gravityBuffer);
            }
        };
        animation.start();
    }


    private void drawGravityField(Mat gravityField, Canvas canvas) {

        final var imageMat = new Mat(HEIGHT, WIDTH, CV_32S);
        gravityField.convertTo(imageMat, CV_32S);
        final var buffer = new int[WIDTH * HEIGHT];
        imageMat.get(0, 0, buffer);

        for (int i = 0; i < buffer.length; i++) {
            buffer[i] *= 100;
            buffer[i] |= 0xFF000000;
        }

        canvas.getGraphicsContext2D()
                .getPixelWriter()
                .setPixels(0, 0, WIDTH, HEIGHT, PixelFormat.getIntArgbInstance(), buffer, 0, WIDTH);
    }
}
