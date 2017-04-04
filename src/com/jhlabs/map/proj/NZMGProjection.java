/**
Contributed by Andrey Novikov, September 2011.

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

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class NZMGProjection extends Projection {

    private static final double SEC5_TO_RAD = 0.4848136811095359935899141023;
    private static final double RAD_TO_SEC5 = 2.062648062470963551564733573;
    private static final double bf[][] = {
        {.7557853228, 0.0},
        {.249204646, .003371507},
        {-.001541739, .041058560},
        {-.10162907, .01727609},
        {-.26623489, -.36249218},
        {-.6870983, -1.1651967}};
    private static final double tphi[] = {1.5627014243, .5185406398, -.03333098, -.1052906, -.0368594,
        .007317, .01220, .00394, -.0013};
    private static final double tpsi[] = {.6399175073, -.1358797613, .063294409, -.02526853, .0117879,
        -.0055161, .0026906, -.001333, .00067, -.00034};

    public void initialize() {
        // force to International major axis
        a = 6378388.0;
        super.initialize();
        //ra = 1. / a;
        projectionLongitude = DTR * 173.;
        projectionLatitude = DTR * -41.;
        falseEasting = 2510000.;
        falseNorthing = 6023150.;
    }

    public Point2D.Double project(double lam, double phi, Point2D.Double xy) {
        double[] p = new double[2];

        phi = (phi - projectionLatitude) * RAD_TO_SEC5;
        p[0] = tpsi[tpsi.length - 1];
        for (int i = tpsi.length - 2; i >= 0; i--) {
            p[0] = tpsi[i] + phi * p[0];
        }
        p[0] *= phi;
        p[1] = lam;
        p = MapMath.zpoly1(p, bf, bf.length);
        xy.x = p[1];
        xy.y = p[0];
        return xy;
    }

    public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
        int nn, i;
        double[] p = new double[2], f, fp = new double[2], dp = new double[2];
        double den;

        p[0] = y;
        p[1] = x;
        for (nn = 20; nn > 0; --nn) {
            f = MapMath.zpolyd1(p, bf, bf.length, fp);
            f[0] -= y;
            f[1] -= x;
            den = fp[0] * fp[0] + fp[1] * fp[1];
            p[0] += dp[0] = -(f[0] * fp[0] + f[1] * fp[1]) / den;
            p[1] += dp[1] = -(f[1] * fp[0] - f[0] * fp[1]) / den;
            if ((Math.abs(dp[0]) + Math.abs(dp[1])) <= EPS10) {
                break;
            }
        }
        lp.x = p[1];
        lp.y = tphi[tphi.length - 1];
        for (i = tphi.length - 2; i >= 0; --i) {
            lp.y = tphi[i] + p[0] * lp.y;
        }
        lp.y = projectionLatitude + p[0] * lp.y * SEC5_TO_RAD;
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "New Zealand Map Grid";
    }
}
