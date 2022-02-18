package de.theoptik.fastparticles;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.theoptik.fastparticles.Launcher.HEIGHT;
import static de.theoptik.fastparticles.Launcher.WIDTH;
import static javafx.scene.image.PixelFormat.getIntArgbInstance;


public class FastParticlesApplication extends Application {

    private static int mouseX = WIDTH / 2;
    private static int mouseY = HEIGHT / 2;

    public static void start(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var particles = IntStream.range(0, 50000)
                .mapToObj((i) -> new Particle(Math.random() * WIDTH, Math.random() * HEIGHT))
                .collect(Collectors.toList());

        final var canvas = new Canvas(WIDTH, HEIGHT);
        final var root = new VBox(canvas);
        root.setStyle("-fx-background-color: black");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();


        primaryStage.addEventHandler(MouseEvent.MOUSE_MOVED, (e) -> {
            mouseX = (int) e.getX();
            mouseY = (int) e.getY();
        });


        final var animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                final var buffer = new int[WIDTH * HEIGHT];
                particles.parallelStream().forEach(p -> {
                    p.update(mouseX, mouseY);
                    p.draw(buffer);
                });
                canvas.getGraphicsContext2D()
                        .getPixelWriter()
                        .setPixels(0, 0, WIDTH, HEIGHT, getIntArgbInstance(), buffer, 0, WIDTH);
            }
        };
        animation.start();
    }
}
