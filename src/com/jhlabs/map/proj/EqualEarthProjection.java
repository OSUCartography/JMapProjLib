/**
 * Copyright 2018 Bernie Jenny, Monash University, Melbourne, Australia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Equal Earth is a projection inspired by the Robinson projection, but unlike
 * the Robinson projection retains the relative size of areas. The projection
 * was designed in 2018 by Bojan Savric, Tom Patterson and Bernhard Jenny.
 *
 * Publication:
 * Bojan Savric, Tom Patterson & Bernhard Jenny (2018). The Equal Earth map
 * projection, International Journal of Geographical Information Science,
 * DOI: 10.1080/13658816.2018.1504949
 *
 * Code released August 2018
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;
import java.time.Year;

public class EqualEarthProjection extends PseudoCylindricalProjection {

    private static final double A1 = 1.340264,
            A2 = -0.081106,
            A3 = 0.000893,
            A4 = 0.003796;

    private static final double M = Math.sqrt(3) / 2.0;

    @Override
    public Point2D.Double project(double lon, double lat, Point2D.Double xy) {
        double paramLat = Math.asin(M * Math.sin(lat));
        double paramLatSq = paramLat * paramLat;
        double paramLatPow6 = paramLatSq * paramLatSq * paramLatSq;
        double x = lon * Math.cos(paramLat)
                / (M * (A1 + 3 * A2 * paramLatSq + paramLatPow6 * (7 * A3 + 9 * A4 * paramLatSq)));
        double y = paramLat * (A1 + A2 * paramLatSq + paramLatPow6 * (A3 + A4 * paramLatSq));
        
        xy.x = x;
        xy.y = y;
        return xy;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double lonLat) {
        final double EPS = 1.0e-9;
        final int NITER = 10;
        
        double paramLat = y, paramLatSq, paramLatPow6, fy, fpy, dlat;
        int i;
        for (i = 0; i < NITER; ++i) {
            paramLatSq = paramLat * paramLat;
            paramLatPow6 = paramLatSq * paramLatSq * paramLatSq;

            fy = paramLat * (A1 + A2 * paramLatSq + paramLatPow6 * (A3 + A4 * paramLatSq)) - y;
            fpy = A1 + 3 * A2 * paramLatSq + paramLatPow6 * (7 * A3 + 9 * A4 * paramLatSq);
            paramLat -= dlat = fy / fpy;
            if (Math.abs(dlat) < EPS) {
                break;
            }
        }

        paramLatSq = paramLat * paramLat;
        paramLatPow6 = paramLatSq * paramLatSq * paramLatSq;
        double lon = M * x * (A1 + 3 * A2 * paramLatSq + paramLatPow6 * (7 * A3 + 9 * A4 * paramLatSq))
                / Math.cos(paramLat);
        double lat = Math.asin(Math.sin(paramLat) / M);
        
        lonLat.x = lon;
        lonLat.y = lat;
        return lonLat;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }
    
    @Override
    public boolean isEqualArea() {
        return true;
    }

    @Override
    public String toString() {
        return "Equal Earth";
    }
    
    @Override
    public Year getYear() {
        return Year.of(2018);
    }

    @Override
    public String getAuthor() {
        return "Bojan Savric, Tom Patterson, Bernhard Jenny";
    }
}
