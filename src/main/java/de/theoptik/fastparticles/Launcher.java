package de.theoptik.fastparticles;


import nu.pattern.OpenCV;
import org.opencv.imgproc.Imgproc;

public class Launcher {

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;

    public static void main(String[] args) {
        OpenCV.loadLocally();
        FastParticlesApplication.start(args);
    }

}
