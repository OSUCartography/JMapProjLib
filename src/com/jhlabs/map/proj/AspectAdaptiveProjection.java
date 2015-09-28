/*
 Copyright 2015 Bojan Savric

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

import java.awt.geom.Point2D;

/**
 * 
 * The Aspect-Adaptive compromise cylindrical map projection adjusts the 
 * distribution of parallels to the aspect ratio of a canvas. Polynomial 
 * expressions define a cylindrical projection for any height-to-width ratio 
 * between 0.3:1 and 1:1. The Aspect-Adaptive was developed by Bernhard Jenny
 * and Bojan Savric, Oregon State University, and by Tom Patterson, US National
 * Park Service, in 2014. 
 * 
 * @author Bojan Savric
 * 
 */

public class AspectAdaptiveProjection extends CylindricalProjection {

    private static final double A1 = 9.684;
    private static final double A2 = -33.44;
    private static final double A3 = 43.13;
    private static final double A4 = -19.77;
    private static final double A5 = -0.569;
    private static final double A6 = -0.875;
    private static final double A7 = 7.002;
    private static final double A8 = -5.948;
    private static final double A9 = -0.509;
    private static final double A10 = 3.333;
    private static final double A11 = -6.705;
    private static final double A12 = 4.148;
    private static final double B1 = 0.0186;
    private static final double B2 = -0.0215;
    private static final double B3 = -1.179;
    private static final double B4 = 1.837;
    
    private static final double ASPECT_LIMIT = 0.7;
    private static final double LAT_LIMIT = Math.PI / 4.;

    private double aspectRatio;
    private double k11, k12, k13, k21, k22;

    public AspectAdaptiveProjection() {
        setAspectRatio(0.6);
    }

    public void setAspectRatio(double aspect) {
        aspectRatio = Math.max(Math.min(aspect, 1.), 0.3);

        double PI_HALF = Math.PI / 2.;
        double PI_HALF_2 = PI_HALF * PI_HALF;

        double k11x, k12x, k13x, k21x, k22x, n, n_2;

        if (aspectRatio > ASPECT_LIMIT) {
            k11x = A1 + ASPECT_LIMIT * (A2  + ASPECT_LIMIT * (A3  + ASPECT_LIMIT * A4));
            k12x = A5 + ASPECT_LIMIT * (A6  + ASPECT_LIMIT * (A7  + ASPECT_LIMIT * A8));
            k13x = A9 + ASPECT_LIMIT * (A10 + ASPECT_LIMIT * (A11 + ASPECT_LIMIT * A12));

            n = PI_HALF * (k11x + PI_HALF_2 * (k12x + PI_HALF_2 * k13x));

            k11 = k11x * ASPECT_LIMIT * Math.PI / n;
            k12 = k12x * ASPECT_LIMIT * Math.PI / n;
            k13 = k13x * ASPECT_LIMIT * Math.PI / n;

            k21x = B1 + aspectRatio * B2;
            k22x = B3 + aspectRatio * B4;

            n_2 = LAT_LIMIT * (k21x + LAT_LIMIT * LAT_LIMIT * k22x);

            double aspectDiff = aspectRatio - ASPECT_LIMIT;
            k21 = k21x * Math.PI * aspectDiff / n_2;
            k22 = k22x * Math.PI * aspectDiff / n_2;

        } else {
            k11x = A1 + aspectRatio * (A2  + aspectRatio * (A3  + aspectRatio * A4));
            k12x = A5 + aspectRatio * (A6  + aspectRatio * (A7  + aspectRatio * A8));
            k13x = A9 + aspectRatio * (A10 + aspectRatio * (A11 + aspectRatio * A12));

            n = PI_HALF * (k11x + PI_HALF_2 * (k12x + PI_HALF_2 * k13x));

            k11 = k11x * aspectRatio * Math.PI / n;
            k12 = k12x * aspectRatio * Math.PI / n;
            k13 = k13x * aspectRatio * Math.PI / n;

            k21 = k22 = 0.0;
        }

    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public Point2D.Double project(double lon, double lat, Point2D.Double dst) {
		final double lat_abs = Math.abs(lat);
		final double latsq = lat * lat;
		double lat_diff = 0.0, y;

        y = lat * (k11 + latsq * (k12 + latsq * k13));
        
		if ((aspectRatio > ASPECT_LIMIT) && (lat_abs > LAT_LIMIT)) {
            lat_diff = Math.signum(lat) * (lat_abs - LAT_LIMIT);
            y += lat_diff * (k21 + lat_diff * lat_diff * k22);
        }

        dst.x = lon;
        dst.y = y;
        return dst;
    }

    public boolean hasInverse() {
        return false;
    }

    public String toString() {
        return "Aspect-Adaptive";
    }
}
