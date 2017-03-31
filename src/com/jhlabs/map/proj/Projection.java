/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.AngleFormat;
import com.jhlabs.map.Ellipsoid;
import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.time.Year;

/**
 * The superclass for all map projections
 *
 * Changes: Added Serializable interface, added abstract keyword, added
 * binarySearchInverse, added transform and inverseTransform with lon/lat as
 * doubles. Bernhard Jenny, 18 May 2010
 *
 * Made all setters and getters dealing with degrees final and calling the
 * versions using radians. Derived classes now only need to overwrite a single
 * getter or setter (if overwriting is at all necessary). Bernhard Jenny, 25
 * October 2010
 *
 * Added getYear(), getAuthor(), getDescription(), getHistory() methods by
 * Bernhard Jenny, February 2017.
 */
public abstract class Projection implements Cloneable, Serializable {

    /**
     * The minimum latitude of the bounds of this projection
     */
    protected double minLatitude = -Math.PI / 2;
    /**
     * The minimum longitude of the bounds of this projection. This is relative
     * to the projection centre.
     */
    protected double minLongitude = -Math.PI;
    /**
     * The maximum latitude of the bounds of this projection
     */
    protected double maxLatitude = Math.PI / 2;
    /**
     * The maximum longitude of the bounds of this projection. This is relative
     * to the projection centre.
     */
    protected double maxLongitude = Math.PI;
    /**
     * The latitude of the centre of projection
     */
    protected double projectionLatitude = 0.0;
    /**
     * The longitude of the centre of projection
     */
    protected double projectionLongitude = 0.0;

    /**
     * The projection scale factor
     */
    protected double scaleFactor = 1.0;
    /**
     * The false Easting of this projection
     */
    protected double falseEasting = 0;
    /**
     * The false Northing of this projection
     */
    protected double falseNorthing = 0;
    /**
     * The latitude of true scale. Only used by specific projections.
     */
    protected double trueScaleLatitude = 0.0;
    /**
     * The equator radius
     */
    protected double a = 0;
    /**
     * The eccentricity
     */
    protected double e = 0;
    /**
     * The eccentricity squared
     */
    protected double es = 0;
    /**
     * 1-(eccentricity squared)
     */
    protected double one_es = 0;
    /**
     * 1/(1-(eccentricity squared))
     */
    protected double rone_es = 0;
    /**
     * The ellipsoid used by this projection
     */
    protected Ellipsoid ellipsoid;
    /**
     * True if this projection is using a sphere (es == 0)
     */
    protected boolean spherical;
    /**
     * True if this projection is geocentric
     */
    protected boolean geocentric;
    /**
     * The name of this projection
     */
    protected String name = null;
    /**
     * Conversion factor from metres to whatever units the projection uses.
     */
    protected double fromMetres = 1;
    /**
     * The total scale factor = Earth radius * units
     */
    private double totalScale = 0;
    /**
     * falseEasting, adjusted to the appropriate units using fromMetres
     */
    private double totalFalseEasting = 0;
    /**
     * falseNorthing, adjusted to the appropriate units using fromMetres
     */
    private double totalFalseNorthing = 0;
    // Some useful constants
    protected final static double EPS10 = 1e-10;
    protected final static double RTD = 180.0 / Math.PI;
    protected final static double DTR = Math.PI / 180.0;

    protected Projection() {
        setEllipsoid(Ellipsoid.SPHERE);
    }

    public Object clone() {
        try {
            Projection e = (Projection) super.clone();
            return e;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Project a lat/long point (in degrees), producing a result in metres
     */
    public Point2D.Double transform(Point2D.Double src, Point2D.Double dst) {
        double x = src.x * DTR;
        if (projectionLongitude != 0) {
            x = MapMath.normalizeLongitude(x - projectionLongitude);
        }
        project(x, src.y * DTR, dst);
        dst.x = totalScale * dst.x + totalFalseEasting;
        dst.y = totalScale * dst.y + totalFalseNorthing;
        return dst;
    }

    /**
     * Project a lon/lat point (in degrees), producing a result in metres.
     * Important: unlike the other variants of transform, this implementation
     * always normalizes the longitude, even if projectionLongitude is 0. This
     * is useful for projecting line features crossing +/180 degree of
     * longitude. Bernhard Jenny, May 2010.
     */
    public final Point2D.Double transform(double lon, double lat, Point2D.Double dst) {
        lon = MapMath.normalizeLongitude(lon * DTR - projectionLongitude);
        project(lon, lat * DTR, dst);
        dst.x = totalScale * dst.x + totalFalseEasting;
        dst.y = totalScale * dst.y + totalFalseNorthing;
        return dst;
    }

    /**
     * Project a lat/long point, producing a result in metres
     */
    public Point2D.Double transformRadians(Point2D.Double src, Point2D.Double dst) {
        double x = src.x;
        if (projectionLongitude != 0) {
            x = MapMath.normalizeLongitude(x - projectionLongitude);
        }
        project(x, src.y, dst);
        dst.x = totalScale * dst.x + totalFalseEasting;
        dst.y = totalScale * dst.y + totalFalseNorthing;
        return dst;
    }

    /**
     * Project a lon/lat point (in degrees), producing a result in metres.
     * Important: unlike the other variations of tansform, this implementation
     * always normalizes the longitude, even if projectionLongitude is 0. This
     * is useful for projecting line features crossing +/180 degree of
     * longitude. Bernhard Jenny, May 2010.
     */
    public final Point2D.Double transformRadians(double lon, double lat, Point2D.Double dst) {
        lon = MapMath.normalizeLongitude(lon - projectionLongitude);
        project(lon, lat, dst);
        dst.x = totalScale * dst.x + totalFalseEasting;
        dst.y = totalScale * dst.y + totalFalseNorthing;
        return dst;
    }

    /**
     * The method which actually does the projection. This should be overridden
     * for all projections.
     *
     * @param x Longitude in radians.
     * @param y Latitude in radians.
     * @param dst The projected point.
     * @return The projected point, identical to parameter dst.
     */
    public Point2D.Double project(double x, double y, Point2D.Double dst) {
        dst.x = x;
        dst.y = y;
        return dst;
    }

    /**
     * Project a number of lat/long points (in degrees), producing a result in
     * metres
     */
    public void transform(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Point2D.Double in = new Point2D.Double();
        Point2D.Double out = new Point2D.Double();
        for (int i = 0; i < numPoints; i++) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            transform(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }
    }

    /**
     * Project a number of lat/long points (in radians), producing a result in
     * metres
     */
    public void transformRadians(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Point2D.Double in = new Point2D.Double();
        Point2D.Double out = new Point2D.Double();
        for (int i = 0; i < numPoints; i++) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            transform(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }
    }

    /**
     * Inverse-project a point (in metres), producing a lat/long result in
     * degrees
     */
    public Point2D.Double inverseTransform(Point2D.Double src, Point2D.Double dst) {
        double x = (src.x - totalFalseEasting) / totalScale;
        double y = (src.y - totalFalseNorthing) / totalScale;
        projectInverse(x, y, dst);
        if (dst.x < -Math.PI) {
            dst.x = -Math.PI;
        } else if (dst.x > Math.PI) {
            dst.x = Math.PI;
        }
        if (projectionLongitude != 0) {
            dst.x = MapMath.normalizeLongitude(dst.x + projectionLongitude);
        }
        dst.x *= RTD;
        dst.y *= RTD;
        return dst;
    }

    /**
     * Inverse-project a point (in metres), producing a lat/long result in
     * radians
     */
    public Point2D.Double inverseTransformRadians(Point2D.Double src, Point2D.Double dst) {
        double x = (src.x - totalFalseEasting) / totalScale;
        double y = (src.y - totalFalseNorthing) / totalScale;
        projectInverse(x, y, dst);
        if (dst.x < -Math.PI) {
            dst.x = -Math.PI;
        } else if (dst.x > Math.PI) {
            dst.x = Math.PI;
        }
        if (projectionLongitude != 0) {
            dst.x = MapMath.normalizeLongitude(dst.x + projectionLongitude);
        }
        return dst;
    }

    /**
     * Inverse-project a point (in meters), producing a lat/long result in
     * radians. Added by Bernhard Jenny, May 2007.
     */
    public void inverseTransformRadians(double srcX, double srcY, Point2D.Double dst) {
        double x = (srcX - totalFalseEasting) / totalScale;
        double y = (srcY - totalFalseNorthing) / totalScale;
        projectInverse(x, y, dst);
        if (dst.x < -Math.PI) {
            dst.x = -Math.PI;
        } else if (dst.x > Math.PI) {
            dst.x = Math.PI;
        }
        if (projectionLongitude != 0) {
            dst.x = MapMath.normalizeLongitude(dst.x + projectionLongitude);
        }
    }

    /**
     * The method which actually does the inverse projection. This should be
     * overridden for all projections.
     */
    public Point2D.Double projectInverse(double x, double y, Point2D.Double dst) {
        dst.x = x;
        dst.y = y;
        return dst;
    }

    /**
     * Inverse-project a number of points (in metres), producing a lat/long
     * result in degrees
     */
    public void inverseTransform(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Point2D.Double in = new Point2D.Double();
        Point2D.Double out = new Point2D.Double();
        for (int i = 0; i < numPoints; i++) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            inverseTransform(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }
    }

    /**
     * Inverse-project a number of points (in metres), producing a lat/long
     * result in radians
     */
    public void inverseTransformRadians(double[] srcPoints, int srcOffset, double[] dstPoints, int dstOffset, int numPoints) {
        Point2D.Double in = new Point2D.Double();
        Point2D.Double out = new Point2D.Double();
        for (int i = 0; i < numPoints; i++) {
            in.x = srcPoints[srcOffset++];
            in.y = srcPoints[srcOffset++];
            inverseTransformRadians(in, out);
            dstPoints[dstOffset++] = out.x;
            dstPoints[dstOffset++] = out.y;
        }
    }

    /**
     * Finds the smallest lat/long rectangle wholly inside the given view
     * rectangle. This is only a rough estimate.
     */
    public Rectangle2D inverseTransform(Rectangle2D r) {
        Point2D.Double in = new Point2D.Double();
        Point2D.Double out = new Point2D.Double();
        Rectangle2D bounds = null;
        if (isRectilinear()) {
            for (int ix = 0; ix < 2; ix++) {
                double x = r.getX() + r.getWidth() * ix;
                for (int iy = 0; iy < 2; iy++) {
                    double y = r.getY() + r.getHeight() * iy;
                    in.x = x;
                    in.y = y;
                    inverseTransform(in, out);
                    if (ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0, 0);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        } else {
            for (int ix = 0; ix < 7; ix++) {
                double x = r.getX() + r.getWidth() * ix / 6;
                for (int iy = 0; iy < 7; iy++) {
                    double y = r.getY() + r.getHeight() * iy / 6;
                    in.x = x;
                    in.y = y;
                    inverseTransform(in, out);
                    if (ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0, 0);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        }
        return bounds;
    }

    public static void main(String[] args) {
        /*Projection p = new OrteliusProjection();
        Ellipsoid unarySphere = new Ellipsoid(null, 1, 0, null);
        p.setEllipsoid(unarySphere);
        p.initialize();
        p.testBinarySearchInverse();
        
         */
        Projection p = new RobinsonProjection();
        p.initialize();
        double lon = Math.PI / 2;
        double lat = Math.PI / 4;
        Point2D.Double xy = new Point2D.Double();
        p.project(lon, lat, xy);
        System.out.println(xy);
    }

    public void testBinarySearchInverse() {
        Point2D.Double pt1 = new Point2D.Double();
        Point2D.Double pt2 = new Point2D.Double();

        for (int lon = -180; lon <= 180; lon++) {
            for (int lat = -89; lat < 90; lat++) {
                if (!inside(lon, lat)) {
                    continue;
                }
                transform(lon, lat, pt1);
                binarySearchInverse(pt1.x, pt1.y,
                        Math.toRadians(0),
                        Math.toRadians(0),
                        pt2);
                double lon2 = Math.toDegrees(pt2.x);
                double lat2 = Math.toDegrees(pt2.y);
                double d = Math.hypot(lon - lon2, lat - lat2);
                if (d > 1e-6) {
                    System.out.println(lon + "/" + lat + ": " + d);
                }
            }
        }
    }

    /**
     * Compute the inverse projection by a binary search. Use this method
     * carefully! It is slow and only an approximation for projections that do
     * not provide their own projectInverse method. Added by Bernhard Jenny, 18
     * May 2010.
     *
     * @param x The projected x coordinate relative to the unary sphere.
     * @param y The projected y coordinate relative to the unary sphere.
     * @param lp A point that will receive the result.
     */
    protected void binarySearchInverse(double x, double y, Point2D.Double lp) {
        binarySearchInverse(x, y, 0, 0, lp);
    }

    /**
     * Compute the inverse projection by a binary search. Use this method
     * carefully! It is slow and only an approximation for projections that do
     * not provide their own projectInverse method. Added by Bernhard Jenny, 18
     * May 2010.
     *
     * @param x The projected x coordinate relative to the unary sphere.
     * @param y The projected y coordinate relative to the unary sphere.
     * @param lon An approximation of the longitude in radians.
     * @param lat An approximation of the latitude in radians.
     * @param lp A point that will receive the result.
     */
    protected void binarySearchInverse(double x, double y,
            double lon, double lat, Point2D.Double lp) {

        // tolerance for approximating longitude and latitude
        final double TOL = 1e-9; // less than a hundreth of a second

        // maximum number of loops
        final int MAX_LOOP = 1000;

        int counter = 0;
        double dx, dy;
        do {
            // forward projection
            this.project(lon, lat, lp);
            dx = x - lp.x; // horizontal difference in projected coordinates
            dy = y - lp.y; // vertical difference in projected coordinates
            lon += dx * 0.5; // add half of the horizontal difference to the longitude
            lat += dy * 0.5; // add half of the vertical difference to the latitude

            // stop if it is not converging
            if (counter++ == MAX_LOOP) {
                lon = Double.NaN;
                lat = Double.NaN;
                break;
            }

            // stop when difference is small enough
        } while (dx > TOL || dx < -TOL || dy > TOL || dy < -TOL);

        lp.x = lon;
        lp.y = lat;
    }

    /**
     * Transform a bounding box. This is only a rough estimate.
     */
    public Rectangle2D transform(Rectangle2D r) {
        Point2D.Double in = new Point2D.Double();
        Point2D.Double out = new Point2D.Double();
        Rectangle2D bounds = null;
        if (isRectilinear()) {
            for (int ix = 0; ix < 2; ix++) {
                double x = r.getX() + r.getWidth() * ix;
                for (int iy = 0; iy < 2; iy++) {
                    double y = r.getY() + r.getHeight() * iy;
                    in.x = x;
                    in.y = y;
                    transform(in, out);
                    if (ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0, 0);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        } else {
            for (int ix = 0; ix < 7; ix++) {
                double x = r.getX() + r.getWidth() * ix / 6;
                for (int iy = 0; iy < 7; iy++) {
                    double y = r.getY() + r.getHeight() * iy / 6;
                    in.x = x;
                    in.y = y;
                    transform(in, out);
                    if (ix == 0 && iy == 0) {
                        bounds = new Rectangle2D.Double(out.x, out.y, 0, 0);
                    } else {
                        bounds.add(out.x, out.y);
                    }
                }
            }
        }
        return bounds;
    }

    /**
     * Returns true if this projection is conformal
     *
     * @return true if this projection is conformal, false otherwise.
     */
    public boolean isConformal() {
        return false;
    }

    /**
     * Returns true if this projection is equal area
     *
     * @return true if this projection is equal area, false otherwise.
     */
    public boolean isEqualArea() {
        return false;
    }

    /**
     * Returns true if this projection has an inverse
     *
     * @return true if an inverse projection is available, false otherwise.
     */
    public boolean hasInverse() {
        return false;
    }

    /**
     * Returns true if lat/long lines form a rectangular grid for this
     * projection. This is generally only the case for cylindrical projections,
     * but not for oblique cylindrical projections.
     */
    public boolean isRectilinear() {
        return false;
    }

    /**
     * Returns true if parallels (lines of constant latitude) are parallel. This
     * is the case for cylindric, pseudocylindric and some other projections
     * that use an equatorial aspect.
     *
     * @return true if lines of constant latitude are parallel.
     */
    public boolean parallelsAreParallel() {
        return isRectilinear();
    }

    /**
     * Returns true if the given lat/lon point is visible in this projection.
     *
     * @param lon longitude in degrees.
     * @param lat latitude in degrees.
     * @return
     */
    public boolean inside(double lon, double lat) {
        lon = MapMath.normalizeLongitude(lon * DTR - projectionLongitude);
        lat *= DTR;
        return minLongitude <= lon && lon <= maxLongitude && minLatitude <= lat && lat <= maxLatitude;
    }

    /**
     * Returns the name of the projection.
     *
     * @return the name
     */
    public String getName() {
        return toString();
    }

    /**
     * Returns year of invention or first use. The year may be approximate for
     * old projections.
     *
     * @return year of invention or first use, or null if not known.
     */
    public Year getYear() {
        return null;
    }

    /**
     * Returns the name of the author of the projection.
     *
     * @return the name
     */
    public String getAuthor() {
        return null;
    }

    /**
     * Returns a human readable description of the main characteristics of this
     * projection in telegram style.
     *
     * @return a summary of the main characteristics
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();

        // projection type
        if (this instanceof CylindricalProjection) {
            sb.append("Cylindric");
        } else if (this instanceof ConicProjection) {
            sb.append("Conic");
        } else if (this instanceof PseudoCylindricalProjection) {
            sb.append("Pseudocylindric");
        } else if (this instanceof AzimuthalProjection) {
            sb.append("Azimuthal");
        } else {
            sb.append("Miscellaneous");
        }

        // distortion
        if (isConformal()) {
            sb.append(", conformal.");
        } else if (isEqualArea()) {
            sb.append(", equal-area.");
        } else {
            sb.append(", not equal-area, not conformal.");
        }

        return sb.toString();
    }

    /**
     * Returns short description of the development of this projection,
     * consisting of the year of invention and author if available, and possibly
     * related information.
     *
     * @return historical summary
     */
    public String getHistoryDescription() {
        StringBuilder sb = new StringBuilder();

        Year year = getYear();
        String author = getAuthor();

        if (year != null) {
            if (year.isBefore(Year.of(0))) {
                sb.append(Math.abs(year.getValue())).append(" B.C.");
            } else if (year.isBefore(Year.of(1000))) {
                sb.append("A.D. ").append(year.getValue());
            } else {
                sb.append(year);
            }
        }

        if (author != null) {
            if (year == null) {
                sb.append("By ");
            } else {
                sb.append(" by ");
            }
            sb.append(author);
        }

        if (sb.length() > 0) {
            boolean endsWithPeriod = sb.toString().endsWith(".");
            if (endsWithPeriod == false) {
                sb.append(".");
            }
        }

        return sb.toString();
    }

    /**
     * Get a string which describes this projection in PROJ.4 format.
     */
    public String getPROJ4Description() {
        AngleFormat format = new AngleFormat(AngleFormat.ddmmssPattern, false);
        StringBuffer sb = new StringBuffer();
        sb.append(
                "+proj=" + getName()
                + " +a=" + a);
        if (es != 0) {
            sb.append(" +es=" + es);
        }
        sb.append(" +lon_0=");
        format.format(projectionLongitude, sb, null);
        sb.append(" +lat_0=");
        format.format(projectionLatitude, sb, null);
        if (falseEasting != 1) {
            sb.append(" +x_0=" + falseEasting);
        }
        if (falseNorthing != 1) {
            sb.append(" +y_0=" + falseNorthing);
        }
        if (scaleFactor != 1) {
            sb.append(" +k=" + scaleFactor);
        }
        if (fromMetres != 1) {
            sb.append(" +fr_meters=" + fromMetres);
        }
        return sb.toString();
    }

    public String toString() {
        return "None";
    }

    /**
     * Set the minimum latitude. This is only used for Shape clipping and
     * doesn't affect projection.
     */
    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }

    public double getMinLatitude() {
        return minLatitude;
    }

    /**
     * Set the maximum latitude. This is only used for Shape clipping and
     * doesn't affect projection.
     */
    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public double getMaxLatitude() {
        return maxLatitude;
    }

    public final double getMaxLatitudeDegrees() {
        return getMaxLatitude() * RTD;
    }

    public final double getMinLatitudeDegrees() {
        return getMinLatitude() * RTD;
    }

    public void setMinLongitude(double minLongitude) {
        this.minLongitude = minLongitude;
    }

    public double getMinLongitude() {
        return minLongitude;
    }

    public final void setMinLongitudeDegrees(double minLongitude) {
        setMinLongitude(DTR * minLongitude);
    }

    public final double getMinLongitudeDegrees() {
        return getMinLongitude() * RTD;
    }

    public void setMaxLongitude(double maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public double getMaxLongitude() {
        return maxLongitude;
    }

    public final void setMaxLongitudeDegrees(double maxLongitude) {
        setMaxLongitude(DTR * maxLongitude);
    }

    public final double getMaxLongitudeDegrees() {
        return getMaxLongitude() * RTD;
    }

    /**
     * Set the projection latitude in radians.
     */
    public void setProjectionLatitude(double projectionLatitude) {
        this.projectionLatitude = projectionLatitude;
    }

    public double getProjectionLatitude() {
        return projectionLatitude;
    }

    /**
     * Set the projection latitude in degrees.
     */
    public final void setProjectionLatitudeDegrees(double projectionLatitude) {
        setProjectionLatitude(DTR * projectionLatitude);
    }

    public final double getProjectionLatitudeDegrees() {
        return getProjectionLatitude() * RTD;
    }

    /**
     * Set the projection longitude in radians.
     */
    public void setProjectionLongitude(double projectionLongitude) {
        this.projectionLongitude = MapMath.normalizeLongitude(projectionLongitude);
    }

    public double getProjectionLongitude() {
        return projectionLongitude;
    }

    /**
     * Set the projection longitude in degrees.
     */
    public final void setProjectionLongitudeDegrees(double projectionLongitude) {
        setProjectionLongitude(DTR * projectionLongitude);
    }

    public double getProjectionLongitudeDegrees() {
        return getProjectionLongitude() * RTD;
    }

    /**
     * Set the latitude of true scale in radians. This is only used by certain
     * projections.
     */
    public void setTrueScaleLatitude(double trueScaleLatitude) {
        this.trueScaleLatitude = trueScaleLatitude;
    }

    public double getTrueScaleLatitude() {
        return trueScaleLatitude;
    }

    /**
     * Set the latitude of true scale in degrees. This is only used by certain
     * projections.
     */
    public final void setTrueScaleLatitudeDegrees(double trueScaleLatitude) {
        setTrueScaleLatitude(DTR * trueScaleLatitude);
    }

    public final double getTrueScaleLatitudeDegrees() {
        return getTrueScaleLatitude() * RTD;
    }

    /**
     * Set the false Northing in projected units.
     */
    public void setFalseNorthing(double falseNorthing) {
        this.falseNorthing = falseNorthing;
    }

    public double getFalseNorthing() {
        return falseNorthing;
    }

    /**
     * Set the false Easting in projected units.
     */
    public void setFalseEasting(double falseEasting) {
        this.falseEasting = falseEasting;
    }

    public double getFalseEasting() {
        return falseEasting;
    }

    /**
     * Set the projection scale factor. This is set to 1 by default.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getEquatorRadius() {
        return a;
    }

    /**
     * Set the conversion factor from metres to projected units. This is set to
     * 1 by default.
     */
    public void setFromMetres(double fromMetres) {
        this.fromMetres = fromMetres;
    }

    public double getFromMetres() {
        return fromMetres;
    }

    public void setEllipsoid(Ellipsoid ellipsoid) {
        this.ellipsoid = ellipsoid;
        a = ellipsoid.equatorRadius;
        e = ellipsoid.eccentricity;
        es = ellipsoid.eccentricity2;
    }

    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /**
     * Returns the ESPG code for this projection, or 0 if unknown.
     */
    public int getEPSGCode() {
        return 0;
    }

    /**
     * Initialize the projection. This should be called after setting parameters
     * and before using the projection. This is for performance reasons as
     * initialization may be expensive.
     */
    public void initialize() {
        spherical = e == 0.0;
        one_es = 1 - es;
        rone_es = 1.0 / one_es;
        totalScale = a * fromMetres;
        totalFalseEasting = falseEasting * fromMetres;
        totalFalseNorthing = falseNorthing * fromMetres;
    }

}
