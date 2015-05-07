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
 * The Hufnagel projection family was introduced by Herbert Hufnagel in the
 * "Hufnagel, H. 1989. Ein System unecht-zylindrischer Kartennetze für 
 * Erdkarten. Kartographische Nachrichten, 39(3), 89–96." All projections are 
 * equal-area. Some parameter combinations break the projection into pieces. 
 * This is due to the difficulty of finding good initial guesses for the 
 * iterative computation of coordinates. Implementation by Bernhard Jenny and 
 * Bojan Savric, Oregon State University, with a hint by Daniel Strebe, 
 * Mapthematics. Nov 30, 2014.
 *
 * @author Bojan Savric
 */

public class HufnagelProjection extends PseudoCylindricalProjection {

    private static final double EPS = 1e-6;
    private static final int MAX_ITER = 100;
    private static final int LUT_SIZE = 101;

    private double A, B, psiMAX, aspectRatio;
    private double[] latLUT, yLUT, psiLUT;
    private double K2, K, C;

    public HufnagelProjection() {
        this(0.0, 0.0, Math.PI / 2., 2.0);
    }

    public HufnagelProjection(double A, double B, double psiMAX, double aspectRatio) {
        this.A = A;
        this.B = B;
        this.psiMAX = psiMAX;
        this.aspectRatio = aspectRatio;
        initialize();
    }

    @Override
    public void initialize() {
        Point2D.Double outPoint = new Point2D.Double();
        double width;

        K2 = (4. * Math.PI) / (2. * psiMAX + (1. + A - B / 2.) * Math.sin(2. * psiMAX) + ((A + B) / 2.) * Math.sin(4. * psiMAX) + (B / 2.) * Math.sin(6. * psiMAX));
        K = Math.sqrt(K2);
        C = 1;

        initializeLookUpTables();

        this.project(Math.PI, 0, outPoint);
        width = outPoint.x;
        this.project(0, Math.PI / 2., outPoint);
        C = Math.sqrt(aspectRatio / (width / outPoint.y));

        initializeLookUpTables();
    }

    public void initializeLookUpTables() {
        latLUT = new double[LUT_SIZE];
        yLUT = new double[LUT_SIZE];
        psiLUT = new double[LUT_SIZE];

        double psi, sin2Psi, sin4Psi, sin6Psi, phi, r, y;

        for (int i = 0; i < LUT_SIZE; i++) {
            psi = psiMAX * i / (LUT_SIZE - 1.);

            if (i == 0) {
                phi = 0.0;
            } else if (i == LUT_SIZE - 1) {
                phi = Math.PI / 2.;
            } else {
                sin2Psi = Math.sin(2. * psi);
                sin4Psi = Math.sin(4. * psi);
                sin6Psi = Math.sin(6. * psi);
                phi = Math.asin(0.25 / Math.PI * K2 * (2 * psi + (1 + A - 0.5 * B) * sin2Psi + 0.5 * (A + B) * sin4Psi + 0.5 * B * sin6Psi));
            }

            r = Math.sqrt(1 + A * Math.cos(2 * psi) + B * Math.cos(4 * psi));
            y = r * Math.sin(psi);

            if (i > 0) {
                if (y < yLUT[i - 1] || phi < latLUT[i - 1]) {
                    y = yLUT[i - 1];
                    phi = latLUT[i - 1];
                }
            }

            latLUT[i] = phi;
            yLUT[i] = y;
            psiLUT[i] = psi;
        }
    }

    public double approximatePsi(double lat) {
        double lat0, lat1, d0, dif, w, psi0, psi1, psi, lat_abs = Math.abs(lat);
        int imid, imin = 0, imax = LUT_SIZE - 1;

        while (imax >= imin) {
            imid = (imin + imax) / 2;
            lat0 = latLUT[Math.max(0, imid - 1)];
            lat1 = latLUT[imid];
            if (lat0 < lat_abs && lat1 >= lat_abs) {
                d0 = lat_abs - lat0;
                dif = lat1 - lat0;
                w = d0 / dif;
                psi0 = psiLUT[Math.max(0, imid - 1)];
                psi1 = psiLUT[imid];
                psi = w * (psi1 - psi0) + psi0;
                return lat < 0. ? -psi : psi;
            }

            if (lat1 < lat_abs) {
                imin = imid + 1;
            } else {
                imax = imid - 1;
            }
        }
        return 0.0;
    }

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        double r, deltaPsi, deltaPsi_I, deltaPsi_II, psi0 = 0., PI_x_sinLat = Math.PI * Math.sin(lpphi), psi0_x_2;
        int i = 0;

        if (lpphi != 0.0) {
            psi0 = approximatePsi(lpphi);
            if (Math.abs(psi0) < Math.PI / 2.) {
                do {
                    psi0_x_2 = psi0 * 2.;

                    deltaPsi_I = (.25 * K2) * (psi0_x_2 + (1. + A - .5 * B) * Math.sin(psi0_x_2) + (.5 * (A + B)) * Math.sin(2. * psi0_x_2) + (.5 * B) * Math.sin(3. * psi0_x_2)) - PI_x_sinLat;
                    deltaPsi_II = (.5 * K2) * (1. + (1. + A - .5 * B) * Math.cos(psi0_x_2) + (A + B) * Math.cos(2. * psi0_x_2) + (3. * .5 * B) * Math.cos(3. * psi0_x_2));
                    deltaPsi = deltaPsi_I / deltaPsi_II;

                    i++;

                    if (Double.isNaN(deltaPsi) || Double.isInfinite(deltaPsi) || i > MAX_ITER) {
                        out.x = Double.NaN;
                        out.y = Double.NaN;
                        return out;
                    }
                    psi0 = psi0 - deltaPsi;
                } while (Math.abs(deltaPsi) > EPS);
            }
        }

        r = Math.sqrt(1. + A * Math.cos(2. * psi0) + B * Math.cos(4. * psi0));
        out.x = K * r * C * lplam / Math.PI * Math.cos(psi0);
        out.y = K * r / C * Math.sin(psi0);

        return out;
    }

    public double getA() {
        return A;
    }

    public void setA(double A) {
        this.A = A;
        initialize();
    }

    public double getB() {
        return B;
    }

    public void setB(double B) {
        this.B = B;
        initialize();
    }

    public double getPsiMAX() {
        return psiMAX;
    }

    public void setPsiMAX(double psiMAX) {
        this.psiMAX = psiMAX;
        initialize();
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
        initialize();
    }

    @Override
    public boolean hasInverse() {
        return false;
    }

    public boolean isEqualArea() {
        return true;
    }

    @Override
    public String toString() {
        return "Hufnagel";
    }

}
