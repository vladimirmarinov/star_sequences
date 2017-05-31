package edu.unisofia.vmarinov;

import edu.unisofia.vmarinov.binarization.Binarization;
import edu.unisofia.vmarinov.edge.EdgeDetector;
import edu.unisofia.vmarinov.hough.HoughCircle;
import edu.unisofia.vmarinov.hough.HoughCircleTransform;
import edu.unisofia.vmarinov.processing.StarSequence;
import edu.unisofia.vmarinov.processing.StarSequencesImageProcessor;
import edu.unisofia.vmarinov.processing.StarSequencesProcessor;
import edu.unisofia.vmarinov.skew.ImageDeskew;
import edu.unisofia.vmarinov.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedImage img = null;
        String inputFile = args.length > 0 ? args[0] : "input.bmp";
        int binarizationThreshold = args.length > 1 ? Integer.parseInt(args[1]) : 107;

        img = ImageIO.read(new File(inputFile));

        BufferedImage binarized = Binarization.GetBmp(img, binarizationThreshold);
        EdgeDetector.detectEdges(binarized);

        ImageDeskew deskew = new ImageDeskew(binarized);
        BufferedImage rotated = ImageUtil.rotate(binarized, -deskew.getSkewAngle(), 0, 0);
        BufferedImage rotatedOriginal = ImageUtil.rotate(img, -deskew.getSkewAngle(), 0, 0);

        int maxRadius = 15;
        int[][][] A = HoughCircleTransform.getVotingMatrix(rotated, maxRadius);
        int[] maxValues = HoughCircleTransform.getMaxValues(A);
        List<HoughCircle>[] houghCircles = HoughCircleTransform.detectCircles(A, maxValues);

        HoughCircle currentCircle;
        for (int r = 0; r < maxRadius; r++) {
            Iterator<HoughCircle> iterator = houghCircles[r].iterator();

            while(iterator.hasNext()) {
                currentCircle = iterator.next();

                if (!ImageUtil.isRed(rotated.getRGB(currentCircle.x, currentCircle.y))) {
                    iterator.remove();
                }
            }
        }

        StarSequencesImageProcessor imageProcessor = new StarSequencesImageProcessor(rotated, A);
        List<List<int[]>> regions = imageProcessor.getAllRegionsWithoutCircleDetected();

        for (List<int[]> region : regions) {
            HoughCircle circle = imageProcessor.selectBestCircleCandidateInRegion(region);

            houghCircles[circle.radius].add(circle);
        }

        for (int r = 0; r < maxRadius; r++) {
            for (HoughCircle circle : houghCircles[r]) {
                rotated.setRGB(circle.x, circle.y, Color.blue.getRGB());
            }
        }

        StarSequencesProcessor processor = new StarSequencesProcessor(A, houghCircles);
        int distanceMode = processor.getDistancesMode();
        List<StarSequence> starSequences = processor.findStarSequences(distanceMode);

        Graphics2D graphics2D = rotatedOriginal.createGraphics();
        graphics2D.setStroke(new BasicStroke());

        for (StarSequence starSequence : starSequences) {
            if (starSequence.type == StarSequence.StarSequenceType.NORMAL) {
                graphics2D.setColor(ImageUtil.neighbourhoodSizeColors[starSequence.neighbourhoodSize]);
            } else {
                graphics2D.setColor(Color.green);
            }
            graphics2D.drawRect(starSequence.x, starSequence.y, starSequence.width, starSequence.height);
        }

        graphics2D.dispose();
        ImageIO.write(rotatedOriginal, "png", new File("output.png"));
    }
}
