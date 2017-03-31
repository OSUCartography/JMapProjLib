/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.karto.gui;

import com.jhlabs.map.MapMath;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Projects open lines.
 *
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class LineProjector {

    private int graticuleDensity = 15; // FIXME
    private double curveTolerance = 500; // FIXME
    private boolean addIntermediatePointsAlongCurves = true; // FIXME
    private boolean prevPointOutOfRange = false;

    private void projectMoveTo(double x, double y, MapLine projectedLine, Projection projection) {

        // test if the point is outside of lon0 +/- 180deg
        final double lon0 = projection.getProjectionLongitudeDegrees();
        final double xlon0 = x - lon0;
        final boolean pointOutOfRange = xlon0 < -180 || xlon0 > 180;

        // project the point
        if (projection.inside(x, y)) {
            Point2D.Double dst = new Point2D.Double();
            try {
                projection.transform(x, y, dst);
            } catch (ProjectionException exc) {
                return;
            }
            if (Double.isNaN(dst.x) || Double.isNaN(dst.y)) {
                return;
            }
            projectedLine.addPoint(dst.x, dst.y);
        }
        prevPointOutOfRange = pointOutOfRange;

    }

    /**
     * Project the end point of a straight line segment.
     *
     * @param lonEnd The x coordinate of the end point of the straight line
     * segment.
     * @param latEnd The y coordinate of the end point of the straight line
     * segment.
     * @param lonStart The x coordinate of the start point of the straight line
     * segment. This is only used when the line segment intersects the bounding
     * meridian of graticule to compute the intersection point.
     * @param latStart The y coordinate of the start point of the straight line
     * segment. This is only used when the line segment intersects the bounding
     * meridian of graticule to compute the intersection point.
     * @param projPath The path that will receive the projected point(s).
     */
    private MapLine projectLineTo(double lonEnd, double latEnd,
            double lonStart, double latStart,
            MapLine projPath,
            ArrayList<MapLine> lines,
            Projection projection) {

        // test if the point is outside of lon0 +/- 180deg
        final double lon0 = projection.getProjectionLongitudeDegrees();
        final double xlon0 = lonEnd - lon0;
        final boolean pointOutOfRange = xlon0 < -180 || xlon0 > 180;

        if (prevPointOutOfRange != pointOutOfRange) {
            prevPointOutOfRange = pointOutOfRange;
            projPath = projectIntersectingLineTo(lonEnd, latEnd,
                    lonStart, latStart,
                    projPath, lines, projection);
        } else {
            lineTo(lonStart, latStart, lonEnd, latEnd, projPath, projection);
        }

        return projPath;
    }

    /**
     * Computes two intersection points for a straight line segment that crosses
     * the bounding meridian at +/-180 degrees from the central meridian.
     * Projects and adds the two intersection points and the next end point to a
     * path.
     *
     * @param lonEnd The longitude of the end point of the line segment.
     * @param latEnd The latitude of the end point of the line segment.
     * @param lonStart The longitude of the start point of the line segment.
     * @param latStart The latitude of the start point of the line segment.
     * @param projPath This path will receive three new projected points.
     */
    private MapLine projectIntersectingLineTo(double lonEnd, double latEnd,
            double lonStart, double latStart,
            MapLine projPath,
            ArrayList<MapLine> lines,
            Projection projection) {

        final double dLon = lonEnd - lonStart;
        final double dLat = latEnd - latStart;

        // compute intersection point in geographic coordinates
        final double lon0 = projection.getProjectionLongitudeDegrees();
        final double maxLon = 180 + lon0;
        final double minLon = -180 + lon0;

        // FIXME: intersections are not detected for projections that have not
        // a graticule covering 360 degrees in longitude
        // FIXME: intersections with min latitude and max latitude missing.
        final double lon1; // the longitude of the intermediate end point
        final double lon2; // the longitude of the intermediate start point
        final double lat; // the latitude of both intermediate points
        if (lonEnd > maxLon) {   // leaving graticule towards east
            lon1 = maxLon;
            lat = latStart + dLat * (maxLon - lonStart) / dLon;
            lon2 = minLon;
        } else if (lonStart > maxLon) { // entering graticule from east
            lon1 = minLon;
            lat = latStart + dLat * (maxLon - lonStart) / dLon;
            lon2 = maxLon;
        } else if (lonEnd < minLon) { // leaving graticule towards west
            lon1 = minLon;
            lat = latStart + dLat * (minLon - lonStart) / dLon;
            lon2 = maxLon;
        } else if (lonStart < minLon) { // entering graticule from west
            lon1 = maxLon;
            lat = latStart + dLat * (minLon - lonStart) / dLon;
            lon2 = minLon;
        } else {
            return projPath;  // project the intermediate end point
        }

        // add line from start of line to intersection
        lineTo(lonStart, latStart, lon1, lat, projPath, projection);

        // store the line and create a new one
        lines.add(projPath);
        projPath = new MapLine();

        // add start point to new line, which is the intersection point
        MapPoint xy = projectPoint(lon2, lat, projection);
        if (xy != null) {
            projPath.addPoint(xy);
        }

        // add line to end of line
        lineTo(lon2, lat, lonEnd, latEnd, projPath, projection);

        return projPath;
    }

    private void lineTo(double lonStart, double latStart,
            double lonEnd, double latEnd,
            MapLine projPath,
            Projection projection) {

        if (lonStart == lonEnd && latStart == latEnd) {
            return;
        }

        if (addIntermediatePointsAlongCurves) {
            curvedLineTo(lonStart, latStart, lonEnd, latEnd, projPath, projection);
        } else {
            straightLineTo(lonEnd, latEnd, projPath, projection);
        }

    }

    private void straightLineTo(double lonEnd, double latEnd,
            MapLine projPath,
            Projection projection) {

        MapPoint xy = projectPoint(lonEnd, latEnd, projection);
        if (xy == null) {
            return;
        }

        // don't add the same coordinates twice
        if (projPath.size() > 0) {
            MapPoint endPoint = projPath.getPoint(projPath.size() - 1);
            if (endPoint != null && endPoint.equals(xy)) {
                return;
            }
        }

        projPath.addPoint(xy);
    }

    /**
     * Normalizes a longitude in degrees.
     */
    private double normalizeLongitude(double lon, Projection projection) {
        lon *= MapMath.DTR;
        final double lon0Rad = projection.getProjectionLongitude();
        return MapMath.normalizeLongitude(lon - lon0Rad) * MapMath.RTD;
    }

    private void curvedLineTo(double lonStart, double latStart, double lonEnd, double latEnd, MapLine projPath, Projection projection) {
        MapPoint xyEnd = projectPoint(lonEnd, latEnd, projection);
        if (xyEnd == null) {
            return;
        }
        double lonStartNorm = normalizeLongitude(lonStart, projection);
        double lonEndNorm = normalizeLongitude(lonEnd, projection);
        final double lon0Deg = projection.getProjectionLongitudeDegrees();

        // project the intermediate point between the start and the end point
        double lonMean = (lonStartNorm + lonEndNorm) * 0.5 + lon0Deg;
        double latMean = (latStart + latEnd) * 0.5;
        MapPoint xyMean = projectPoint(lonMean, latMean, projection);
        if (xyMean == null) {
            return;
        }

        if (projPath.size() == 0) {
            return;
        }
        final MapPoint xyStart = projPath.getPoint(projPath.size() - 1);

        // compute the orthogonal distance of the mean point to the line
        // between the start and the end point
        double dsq = pointLineDistanceSquare(xyMean, xyStart, xyEnd);
        if (dsq > curveTolerance * curveTolerance) {
            curvedLineTo(lonStart, latStart, lonMean, latMean, projPath, projection);
            projPath.addPoint(xyMean);
            curvedLineTo(lonMean, latMean, lonEnd, latEnd, projPath, projection);
        }
        projPath.addPoint(xyEnd);
    }

    /**
     * Square distance between a point and a line defined by two other points.
     * See http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
     *
     * @param x0 The point not on the line.
     * @param y0 The point not on the line.
     * @param x1 A point on the line.
     * @param y1 A point on the line.
     * @param x2 Another point on the line.
     * @param y2 Another point on the line.
     */
    private static double pointLineDistanceSquare(
            double x0, double y0,
            double x1, double y1,
            double x2, double y2) {

        final double x2_x1 = x2 - x1;
        final double y2_y1 = y2 - y1;

        final double d = (x2_x1) * (y1 - y0) - (x1 - x0) * (y2_y1);
        final double denominator = x2_x1 * x2_x1 + y2_y1 * y2_y1;
        return d * d / denominator;
    }

    /**
     * Square distance between a point and a line defined by two other points.
     * See http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
     *
     * @param p0 The point not on the line.
     * @param p1 A point on the line.
     * @param p2 Another point on the line.
     */
    private static double pointLineDistanceSquare(MapPoint p0, MapPoint p1, MapPoint p2) {
        return pointLineDistanceSquare(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y);
    }

    private MapPoint projectPoint(double lon, double lat, Projection projection) {
        if (!projection.inside(lon, lat)) {
            return null;
        }

        // project the point
        Point2D.Double dst = new Point2D.Double();
        try {
            projection.transform(lon, lat, dst);
        } catch (ProjectionException exc) {
            return null;
        }
        if (Double.isNaN(dst.x) || Double.isNaN(dst.y)) {
            return null;
        }
        return new MapPoint(dst.x, dst.y);
    }

    public ArrayList<MapLine> inverse(ArrayList<MapLine> lines, Projection projection) {

        if (lines == null) {
            return null;
        }

        // create a new array to store the new lines.
        ArrayList<MapLine> projectedLines = new ArrayList<MapLine>();

        // loop over all lines to inverse-project
        for (MapLine line : lines) {
            MapLine projectedLine = new MapLine();

            // loop over all points of the line
            for (MapPoint p : line.getPoints()) {
                Point2D.Double point = new Point2D.Double(p.x, p.y);
                projection.inverseTransform(point, point);
                projectedLine.addPoint(new MapPoint(point.x, point.y));
            }

            // add the projected line to the array of projected lines
            if (projectedLine.size() > 1) {
                projectedLines.add(projectedLine);
            }
        }

        // return the vector with the projected lines.
        return projectedLines;

    }

    public void project(MapLine line, Projection projection, ArrayList<MapLine> projectedLines) {

        if (line.size() < 1) {
            return;
        }

        // create a new line for the projected coordinates.
        MapLine projectedLine = new MapLine();

        // loop over all points of the line
        int nbrPoints = line.size();

        prevPointOutOfRange = false;
        MapPoint point = line.getPoint(0);
        double prevLon = point.x;
        double prevLat = point.y;
        projectMoveTo(point.x, point.y, projectedLine, projection);

        for (int pointID = 1; pointID < nbrPoints; pointID++) {
            point = line.getPoint(pointID);
            final double lon = point.x;
            final double lat = point.y;
            projectedLine = projectLineTo(lon, lat, prevLon, prevLat,
                    projectedLine, projectedLines, projection);
            prevLon = lon;
            prevLat = lat;
        }

        // add the projected line to the array of projected lines
        if (projectedLine.size() > 1) {
            projectedLines.add(projectedLine);
        }

    }

    /**
     * Projects a vector of lines.
     *
     * @param lines The lines to project.
     * @return A vector with the projected lines.
     */
    public void projectLines(ArrayList<MapLine> src, ArrayList<MapLine> dst, Projection projection) {

        if (src == null || dst == null) {
            return;
        }
        
        this.addIntermediatePointsAlongCurves = false;

        // loop over all lines to project
        int nbrLines = src.size();
        for (int lineID = 0; lineID < nbrLines; lineID++) {
            MapLine line = (MapLine) src.get(lineID);
            project(line, projection, dst);
        }

    }

    /**
     * Construct a graticule (a grid of regularly spaced longitude and latitude
     * lines). The graticule is projected.
     */
    public void constructGraticule(ArrayList<MapLine> projectedLines, Projection projection) {

        this.addIntermediatePointsAlongCurves = true;

        final double minLon = projection.getMinLongitudeDegrees();
        final double maxLon = projection.getMaxLongitudeDegrees();
        final double minLat = projection.getMinLatitudeDegrees();
        final double maxLat = projection.getMaxLatitudeDegrees();

        projection = (Projection) projection.clone();

        final int linesPerHemisphere = (int) (180 / graticuleDensity);

        // vertical meridian lines
        for (int i = -linesPerHemisphere; i <= linesPerHemisphere; i++) {
            final double lon = i * graticuleDensity;
            MapLine line = new MapLine();
            line.addPoint(lon, maxLat);
            // Add an intermediat point at the equator. Othewrwise the projected
            // graticule will be a wrong straight line for pseudocylindrical
            // projections that have a pole line with the same length as the
            // equator. This is caused by the way intermediate points are added
            // by the LineProjector. It tests the middle point for each line
            // segment. If its distance from the line connecting the start and
            // the end line is large enough, an intermediate point is recursevly
            // added. Problems arise when this intermediate point is on this line,
            // as in the case above.
            if (!projection.isRectilinear()) {
                line.addPoint(lon, 0);
            }
            line.addPoint(lon, minLat);

            project(line, projection, projectedLines);
        }

        // horizontal parallels
        projection.setProjectionLongitudeDegrees(0);
        for (int j = -linesPerHemisphere / 2; j <= linesPerHemisphere / 2; j++) {
            MapLine line = new MapLine();
            final double lat = j * graticuleDensity;
            if (lat > maxLat || lat < minLat) {
                continue;
            }
            line.addPoint(minLon, (float) lat);
            line.addPoint(maxLon, (float) lat);
            project(line, projection, projectedLines);
        }
    }

    /**
     * Returns the outline of the valid area of a projection in the projected
     * coordinate system.
     */
    public MapLine constructOutline(Projection projection,
            ArrayList<MapLine> projectedLines) {

        this.addIntermediatePointsAlongCurves = true;

        projection = (Projection) projection.clone();
        projection.setProjectionLongitudeDegrees(0);
        projection.initialize();

        final double minLon = projection.getMinLongitudeDegrees();
        final double maxLon = projection.getMaxLongitudeDegrees();
        final double minLat = projection.getMinLatitudeDegrees();
        final double maxLat = projection.getMaxLatitudeDegrees();

        MapLine outline = new MapLine();

        // bottom line
        outline.addPoint(minLon, minLat);

        // right line
        outline.addPoint(maxLon, minLat);

        // top line
        outline.addPoint(maxLon, maxLat);

        // left line
        outline.addPoint(minLon, maxLat);

        // close the line
        outline.addPoint(minLon, minLat);

        // project the outline
        project(outline, projection, projectedLines);

        return outline;
    }
}
