package edu.unisofia.vmarinov.hough;

public class HoughCircle {

    public HoughCircle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius= radius;
    }

    public int x;
    public int y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HoughCircle circle = (HoughCircle) o;

        if (x != circle.x) return false;
        if (y != circle.y) return false;
        return radius == circle.radius;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + radius;
        return result;
    }

    public int radius;
}
