package de.theoptik.fastparticles;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.openjdk.jmh.annotations.Mode.Throughput;

public class ParticleDrawPerformanceTest {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final GraphicsContext context = new Canvas(WIDTH, HEIGHT).getGraphicsContext2D();

    private static final List<Particle> particles = IntStream.range(0, 500000)
            .mapToObj((i) -> new Particle(Math.random() * Launcher.WIDTH, Math.random() * Launcher.HEIGHT))
            .collect(Collectors.toList());


    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    @BenchmarkMode(Throughput)
    public void context() {

        context.clearRect(0, 0, WIDTH, HEIGHT);
        particles.forEach(p -> p.draw(context));

    }

    @Benchmark
    @BenchmarkMode(Throughput)
    public void buffer() {

        final var buffer = new int[WIDTH * HEIGHT];
        particles.forEach(p -> p.draw(buffer));

    }

    @Benchmark
    @BenchmarkMode(Throughput)
    public void bufferStream() {

        final var buffer = new int[WIDTH * HEIGHT];
        particles.stream().forEach(p -> p.draw(buffer));

    }

    @Benchmark
    @BenchmarkMode(Throughput)
    public void bufferParallelStream() {

        final var buffer = new int[WIDTH * HEIGHT];
        particles.parallelStream().forEach(p -> p.draw(buffer));

    }
}
