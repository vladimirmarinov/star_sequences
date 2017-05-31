package edu.unisofia.vmarinov.edge;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EdgeDetector {
    public static BufferedImage detectEdges(BufferedImage image) {
        for (int i = 1; i < image.getWidth() - 1; i++) {
            for (int j = 1; j < image.getHeight() - 1; j++) {
                if (image.getRGB(i, j) == Color.white.getRGB()) {
                    if (
                            image.getRGB(i + 1, j) != Color.black.getRGB() &&
                            image.getRGB(i - 1, j) != Color.black.getRGB() &&
                            image.getRGB(i, j + 1) != Color.black.getRGB() &&
                            image.getRGB(i, j - 1) != Color.black.getRGB()) {
                        image.setRGB(i, j, Color.red.getRGB());
                    }
                }
            }
        }

        return image;
    }
}
