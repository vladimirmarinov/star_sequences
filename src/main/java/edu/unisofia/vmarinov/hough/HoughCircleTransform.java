package edu.unisofia.vmarinov.hough;

import edu.unisofia.vmarinov.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class HoughCircleTransform {
    private static final double THRESHOLD = 0.65;

    public static int[][][] getVotingMatrix(BufferedImage image, int maxRadius) {
        final int width = image.getWidth(), height = image.getHeight();
        int a, b;
        int A[][][] = new int[width][height][maxRadius];
        final int minRadius = 2;
        int step = 5;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (image.getRGB(i, j) == Color.black.getRGB() ||
                        ImageUtil.isRed(image.getRGB(i, j))) {
                    continue;
                }
                for (int r = minRadius; r < maxRadius; r++) {
                    for (int theta = 0; theta < 360; theta += step) {
                        a = (int) Math.round(i - r * Math.cos(theta * Math.PI / 180));
                        b = (int) Math.round(j - r * Math.sin(theta * Math.PI / 180));

                        if (a >= 0 && b >= 0 && a < width && b < height) {
                            A[a][b][r]++;
                        }
                    }
                }
            }
        }

        return A;
    }

    public static int[] getMaxValues(int[][][] A) {
        int width = A.length;
        int height = A[0].length;
        int maxRadius = A[0][0].length;
        int minRadius = 2;

        int[] maxValues = new int[maxRadius];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int r = minRadius; r < maxRadius; r++) {
                    if (A[i][j][r] > maxValues[r]) {
                        maxValues[r] = A[i][j][r];
                    }
                }
            }
        }

        return maxValues;
    }

    public static List<HoughCircle>[] detectCircles(int A[][][], int[] maxValues) {
        List<HoughCircle>[] result = new List[maxValues.length];

        int width = A.length;
        int height = A[0].length;
        int maxRadius = A[0][0].length;
        int minRadius = 2;

        for (int r = 0; r < maxRadius; r++) {
            result[r] = new ArrayList<HoughCircle>();
        }

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                for (int r = minRadius; r < maxRadius; r++) {
                    if (
                            A[i][j][r] > A[i - 1][j][r] &&
                                    A[i][j][r] > A[i + 1][j][r] &&
                                    A[i][j][r] > A[i][j - 1][r] &&
                                    A[i][j][r] > A[i][j + 1][r] &&
                                    A[i][j][r] > A[i - 1][j - 1][r] &&
                                    A[i][j][r] > A[i + 1][j + 1][r] &&
                                    A[i][j][r] > A[i + 1][j - 1][r] &&
                                    A[i][j][r] > A[i - 1][j + 1][r] &&
                                    (1.0 * A[i][j][r]) / maxValues[r] > THRESHOLD
                            ) {
                        result[r].add(new HoughCircle(i, j, r));
                    }
                }
            }
        }

        return result;
    }
}
