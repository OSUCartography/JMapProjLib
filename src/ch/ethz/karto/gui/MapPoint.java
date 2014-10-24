package ch.ethz.karto.gui;

public final class MapPoint {

    public MapPoint() {
    }

    public MapPoint (double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        MapPoint p = (MapPoint)obj;
        return x == p.x && y == p.y;
    }

    public double x;
    public double y;
}
