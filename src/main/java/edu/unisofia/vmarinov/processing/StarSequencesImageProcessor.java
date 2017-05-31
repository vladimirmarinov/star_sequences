package edu.unisofia.vmarinov.processing;

import edu.unisofia.vmarinov.hough.HoughCircle;
import edu.unisofia.vmarinov.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class StarSequencesImageProcessor {
    public BufferedImage image;
    public int[][][] A;

    public StarSequencesImageProcessor(BufferedImage image, int A[][][]) {
        this.image = image;
        this.A = A;
    }

    public List<List<int[]>> getAllRegionsWithoutCircleDetected() {
        List<List<int[]>> result = new ArrayList<List<int[]>>();

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (ImageUtil.isRed(image.getRGB(i, j))) {
                    List<int[]> region = new ArrayList<int[]>();

                    boolean hasCircleDetected = findRegionAroundPoint(i, j, region);
                    if (!hasCircleDetected) {
                        result.add(region);
                    }
                }
            }
        }

        return result;
    }

    boolean findRegionAroundPoint(int x, int y, List<int[]> regionPoints) {
        if (!ImageUtil.isRed(image.getRGB(x, y))) {
            if (image.getRGB(x, y) == Color.blue.getRGB()) {
                return true;
            }

            return false;
        }

        image.setRGB(x, y, Color.black.getRGB());
        regionPoints.add(new int[]{x, y});

        return findRegionAroundPoint(x - 1, y, regionPoints) ||
                findRegionAroundPoint(x + 1, y, regionPoints) ||
                findRegionAroundPoint(x, y - 1, regionPoints) ||
                findRegionAroundPoint(x, y + 1, regionPoints) ||
                findRegionAroundPoint(x - 1, y - 1, regionPoints) ||
                findRegionAroundPoint(x + 1, y + 1, regionPoints) ||
                findRegionAroundPoint(x + 1, y - 1, regionPoints) ||
                findRegionAroundPoint(x - 1, y + 1, regionPoints);
    }

    public HoughCircle selectBestCircleCandidateInRegion(List<int[]> region) {
        int maxRadius = A[0][0].length;
        int minRadius = (int) Math.round(Math.sqrt(region.size() / Math.PI));
        double currentMax = 0;
        int maxX = 0, maxY = 0, maxR = 0;

        for (int[] point : region) {
            for (int r = minRadius; r < maxRadius; r++) {
                if (A[point[0]][point[1]][r] > currentMax) {
                    currentMax = A[point[0]][point[1]][r];
                    maxX = point[0];
                    maxY = point[1];
                    maxR = r;
                }
            }
        }

        return new HoughCircle(maxX, maxY, maxR);
    }
}
