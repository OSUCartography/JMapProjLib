/*
 Copyright 2015 Bojan Savric, Bernhard Jenny, Daniel Strebe

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
 * The Hufnagel projection family was introduced by Herbert Hufnagel in
 * "Hufnagel, H. 1989. Ein System unecht-zylindrischer Kartennetze für
 * Erdkarten. Kartographische Nachrichten, 39(3), 89–96." All projections are
 * equal-area. Implementation by Bernhard Jenny, Oregon State University, Bojan
 * Savric, Oregon State University, with substantial contributions by Daniel
 * "daan" Strebe, Mapthematics. November 2014 to October 2015.
 *
 * @author Bojan Savric
 * @author Bernhard Jenny
 */
public class HufnagelProjection extends PseudoCylindricalProjection {

    private static final double EPS = 1e-14;
    private static final int MAX_ITER = 100;
    private static final int LUT_SIZE = 101;

    private double A, B, psiMax, aspectRatio;
    private double[] latLUT, yLUT, psiLUT;
    private double K2, K, C;

    public HufnagelProjection() {
        this(2. / 3., 1. / 3., Math.PI / 4., 2.0);
    }

    public HufnagelProjection(double A, double B, double psiMax, double aspectRatio) {
        this.A = A;
        this.B = B;
        this.aspectRatio = aspectRatio;
        this.psiMax = psiMax;
        initializeHufnagel();
    }

    private void initializeHufnagel() {
        if (psiMax == 0) {
            K = Math.sqrt(aspectRatio / Math.PI);
        } else {
            K2 = (4. * Math.PI) / (2. * psiMax + (1. + A - B / 2.) * Math.sin(2. * psiMax)
                    + ((A + B) / 2.) * Math.sin(4. * psiMax) + (B / 2.) * Math.sin(6. * psiMax));
            K = Math.sqrt(K2);
            C = Math.sqrt(aspectRatio * Math.sin(psiMax)
                    * Math.sqrt((1 + A * Math.cos(2 * psiMax) + B * Math.cos(4 * psiMax)) / (1 + A + B)));
            initializeLookUpTables();
        }
    }

    public void initializeLookUpTables() {
        latLUT = new double[LUT_SIZE];
        yLUT = new double[LUT_SIZE];
        psiLUT = new double[LUT_SIZE];

        for (int i = 0; i < LUT_SIZE; i++) {
            double psi = psiMax * i / (LUT_SIZE - 1.);
            double phi;
            if (i == 0) {
                phi = 0.0;
            } else if (i == LUT_SIZE - 1) {
                phi = Math.PI / 2.;
            } else {
                double sin2Psi = Math.sin(2. * psi);
                double sin4Psi = Math.sin(4. * psi);
                double sin6Psi = Math.sin(6. * psi);
                double sinPhi = 0.25 / Math.PI * K2 * (2 * psi + (1 + A - 0.5 * B) * sin2Psi
                        + 0.5 * (A + B) * sin4Psi + 0.5 * B * sin6Psi);
                if (Math.abs(sinPhi) > 1) {
                    phi = sinPhi > 0 ? Math.PI / 2 : -Math.PI / 2;
                } else {
                    phi = Math.asin(sinPhi);
                }
            }

            double r = Math.sqrt(1 + A * Math.cos(2 * psi) + B * Math.cos(4 * psi));
            double y = K / C * r * Math.sin(psi);
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

    private double approximatePsiFromTable(double v, double[] table) {
        double w, psi, vAbs = Math.abs(v);
        int imid, imin = 0, imax = table.length;

        while (true) {
            imid = (int) Math.floor((imin + imax) / 2);
            if (imid == imin) {
                // This also handles abs(phi) == latitudeTable[0] because mid must == min.
                break;
            } else if (vAbs > table[imid]) {
                imin = imid;
            } else {
                // abs(phi) < latitudeTable[mid], or abs(phi) == latitudeTable[mid] and mid ≠ 0
                imax = imid;
            }
        }

        w = (vAbs - table[imin]) / (table[imin + 1] - table[imin]);
        psi = w * (psiLUT[imin + 1] - psiLUT[imin]) + psiLUT[imin];

        return v < 0 ? -psi : psi;
    }

    private double psiFromPhi(double phi) {
        double psi = approximatePsiFromTable(phi, latLUT);
        int i = 0;
        while (true) {
            double psi_x_2 = psi * 2.;
            double deltaPsiNumerator = (.25 * K2) * (psi_x_2 + (1. + A - .5 * B)
                    * Math.sin(psi_x_2) + (0.5 * (A + B)) * Math.sin(2. * psi_x_2)
                    + (0.5 * B) * Math.sin(3. * psi_x_2)) - Math.PI * Math.sin(phi);
            if (Math.abs(deltaPsiNumerator) < EPS) {
                break;
            }
            double deltaPsiDenominator = (.5 * K2) * (1. + (1. + A - .5 * B)
                    * Math.cos(psi_x_2) + (A + B) * Math.cos(2. * psi_x_2)
                    + (3. * .5 * B) * Math.cos(3. * psi_x_2));
            double deltaPsi = deltaPsiNumerator / deltaPsiDenominator;
            if (Double.isNaN(deltaPsi) || Double.isInfinite(deltaPsi) || i++ > MAX_ITER) {
                return Double.NaN;
            }
            psi = psi - deltaPsi;
        }
        return psi;
    }

    private double psiFromY(double y) {
        double psi = approximatePsiFromTable(y, yLUT);
        int i = 0;
        while (true) {
            double sinPsi = Math.sin(psi);
            double sin2Psi = Math.sin(2 * psi);
            double cos2Psi = Math.cos(2 * psi);
            double cos4Psi = Math.cos(4 * psi);
            double r = Math.sqrt(1 + A * cos2Psi + B * cos4Psi);
            double r_sinPsi = r * sinPsi;
            double scaledY = y * C / K;
            double deltaPsiNumerator = r_sinPsi * r_sinPsi - scaledY * scaledY;
            if (Math.abs(deltaPsiNumerator) < EPS) {
                break;
            }
            double deltaPsiDenominator = sin2Psi * (1 - A + 2 * B
                    + (2 * A - 4 * B) * cos2Psi + 3 * B * cos4Psi);
                    
            double deltaPsi = deltaPsiNumerator / deltaPsiDenominator;
            if (Double.isNaN(deltaPsi) || Double.isInfinite(deltaPsi) || i++ > MAX_ITER) {
                return Double.NaN;
            }
            psi = psi - deltaPsi;
        }
        return psi;
    }

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        if (psiMax == 0) {
            // cylindrical equal-area projection
            out.x = lplam * K;
            out.y = Math.sin(lpphi) / K;
        } else {
            double psi = psiFromPhi(lpphi);
            if (Double.isNaN(psi) || Math.abs(psi) > psiMax) {
                out.x = out.y = Double.NaN;
                return out;
            }
            double r = Math.sqrt(1. + A * Math.cos(2. * psi) + B * Math.cos(4. * psi));
            out.x = K * r * C * lplam / Math.PI * Math.cos(psi);
            out.y = K * r / C * Math.sin(psi);
        }
        return out;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double dst) {

        if (psiMax == 0) { // cylindric
            if (Math.abs(x) > K * Math.PI || Math.abs(y) > 1.0 / K) {
                dst.x = dst.y = Double.NaN;
                return dst;
            }
            dst.x = x / K;
            dst.y = Math.asin(y * K);
        } else {

            double psi = psiFromY(y); // using interpolation table
            if (Double.isNaN(psi) || Math.abs(psi) > psiMax) {
                dst.x = dst.y = Double.NaN;
                return dst;
            }

            double sin2Psi = Math.sin(2 * psi);
            double sin4Psi = Math.sin(4 * psi);
            double sin6Psi = Math.sin(6 * psi);
            double sinPhi = 0.25 * K * K * (2 * psi + (1.0 + A - 0.5 * B) * sin2Psi
                    + 0.5 * (A + B) * sin4Psi + 0.5 * B * sin6Psi) / Math.PI;
            if (Math.abs(sinPhi) > 1.0) {
                dst.x = dst.y = Double.NaN;
                return dst;
            }
            dst.y = Math.asin(sinPhi);

            double cosPsi = Math.cos(psi);
            double cos2Psi = Math.cos(2 * psi);
            double cos4Psi = Math.cos(4 * psi);
            double r = Math.sqrt(1.0 + A * cos2Psi + B * cos4Psi);
            double lambda = x * Math.PI / (K * C * r * cosPsi);
            if (Math.abs(lambda) > Math.PI) {
                dst.x = dst.y = Double.NaN;
                return dst;
            }
            dst.x = lambda;
        }
        return dst;
    }

    public double getA() {
        return A;
    }

    public void setA(double A) {
        this.A = A;
        initializeHufnagel();
    }

    public double getB() {
        return B;
    }

    public void setB(double B) {
        this.B = B;
        initializeHufnagel();
    }

    public double getPsiMAX() {
        return psiMax;
    }

    public void setPsiMAX(double psiMAX) {
        this.psiMax = psiMAX;
        initializeHufnagel();
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
        initializeHufnagel();
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
        return "Hufnagel";
    }

}
