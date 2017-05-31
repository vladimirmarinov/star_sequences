package edu.unisofia.vmarinov.skew;

import edu.unisofia.vmarinov.util.ImageUtil;

import java.awt.image.BufferedImage;

public class ImageDeskew {

    public class HoughLine {
        public int count = 0;
        public int index = 0;
        public double alpha;
        public double d;
    }

    private BufferedImage cImage;
    private double cAlphaStart = -20;
    private double cAlphaStep = 0.2;
    private int cSteps = 40 * 5;
    private double[] cSinA;
    private double[] cCosA;
    private double cDMin;
    private double cDStep = 1.0;
    private int cDCount;
    private int[] cHMatrix;

    public ImageDeskew(BufferedImage image) {
        this.cImage = image;
    }

    public double getSkewAngle() {
        ImageDeskew.HoughLine[] hl;
        double sum = 0.0;
        int count = 0;

        calc();
        hl = getTop(20);

        if (hl.length >= 20) {
            for (int i = 0; i < 19; i++) {
                sum += hl[i].alpha;
                count++;
            }
            return (sum / count);
        } else {
            return 0.0d;
        }
    }

    private ImageDeskew.HoughLine[] getTop(int count) {

        ImageDeskew.HoughLine[] hl = new ImageDeskew.HoughLine[count];
        for (int i = 0; i < count; i++) {
            hl[i] = new ImageDeskew.HoughLine();
        }

        ImageDeskew.HoughLine tmp;

        for (int i = 0; i < (this.cHMatrix.length - 1); i++) {
            if (this.cHMatrix[i] > hl[count - 1].count) {
                hl[count - 1].count = this.cHMatrix[i];
                hl[count - 1].index = i;
                int j = count - 1;
                while ((j > 0) && (hl[j].count > hl[j - 1].count)) {
                    tmp = hl[j];
                    hl[j] = hl[j - 1];
                    hl[j - 1] = tmp;
                    j--;
                }
            }
        }

        int alphaIndex;
        int dIndex;

        for (int i = 0; i < count; i++) {
            dIndex = hl[i].index / cSteps;
            alphaIndex = hl[i].index - dIndex * cSteps;
            hl[i].alpha = getAlpha(alphaIndex);
            hl[i].d = dIndex + cDMin;
        }

        return hl;
    }

    private void calc() {
        int hMin = (int) ((this.cImage.getHeight()) / 4.0);
        int hMax = (int) ((this.cImage.getHeight()) * 3.0 / 4.0);
        init();

        for (int y = hMin; y < hMax; y++) {
            for (int x = 1; x < (this.cImage.getWidth() - 2); x++) {
                if (ImageUtil.isBlack(this.cImage, x, y)) {
                    if (!ImageUtil.isBlack(this.cImage, x, y + 1)) {
                        calc(x, y);
                    }
                }
            }
        }

    }

    private void calc(int x, int y) {
        double d;
        int dIndex;
        int index;

        for (int alpha = 0; alpha < (this.cSteps - 1); alpha++) {
            d = y * this.cCosA[alpha] - x * this.cSinA[alpha];
            dIndex = (int) (d - this.cDMin);
            index = dIndex * this.cSteps + alpha;
            try {
                this.cHMatrix[index] += 1;
            } catch (Exception ex) {

            }
        }
    }

    private void init() {

        double angle;

        this.cSinA = new double[this.cSteps - 1];
        this.cCosA = new double[this.cSteps - 1];

        for (int i = 0; i < (this.cSteps - 1); i++) {
            angle = getAlpha(i) * Math.PI / 180.0;
            this.cSinA[i] = Math.sin(angle);
            this.cCosA[i] = Math.cos(angle);
        }

        this.cDMin = -this.cImage.getWidth();
        this.cDCount = (int) (2.0 * ((this.cImage.getWidth() + this.cImage.getHeight())) / this.cDStep);
        this.cHMatrix = new int[this.cDCount * this.cSteps];

    }

    public double getAlpha(int index) {
        return this.cAlphaStart + (index * this.cAlphaStep);
    }
}
