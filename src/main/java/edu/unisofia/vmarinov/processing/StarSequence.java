package edu.unisofia.vmarinov.processing;

public class StarSequence {
    public int x;
    public int y;
    public int width;
    public int height;
    public int radius;
    public int neighbourhoodSize;
    public StarSequenceType type;

    public StarSequence(int x, int y, int width, int height, int radius, StarSequenceType type, int neighbourhoodSize) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.neighbourhoodSize = neighbourhoodSize;
        this.type = type;
    }

    public enum StarSequenceType {
        NORMAL,
        INTERESTING
    }
}
