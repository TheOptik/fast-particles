package de.theoptik.fastparticles;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private static double drag = 0;

    public static void start(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        final var particles = IntStream.range(0, 1000000)
                .mapToObj((i) -> new Particle(Math.random() * WIDTH, Math.random() * HEIGHT))
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
            if(e.getCode() == KeyCode.SPACE){
                mouseX = Integer.MAX_VALUE;
                mouseY = Integer.MAX_VALUE;
            }
            if(e.getCode() == KeyCode.UP){
                drag += 0.01;
            }
            if(e.getCode() == KeyCode.DOWN){
                drag -= 0.01;
            }
        });


        final var animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                final var buffer = new int[WIDTH * HEIGHT];
                particles.parallelStream().forEach(p -> {
                    p.update(mouseX, mouseY, drag);
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
