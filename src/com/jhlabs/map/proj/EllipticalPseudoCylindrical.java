/*
Copyright 2007 Bernhard Jenny

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 * EllipticalPseudoCylindrical.java
 *
 * Created on March 11, 2007, 11:21 PM
 *
 */

package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

/**
 * Abstract base class for Eckert 3, Putnins P1, Wagner VI (Putnin P'1), and
 * Kavraisky VII. Based on Proj4.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich
 */
public abstract class EllipticalPseudoCylindrical extends PseudoCylindricalProjection {
    
    private final double C_x;
    private final double C_y;
    private final double A;
    private final double B;
    
    /** Creates a new instance of EllipticalPseudocylindrical */
    protected EllipticalPseudoCylindrical(double C_x, double C_y, 
            double A, double B) {
            this.C_x = C_x;
            this.C_y = C_y;
            this.A = A;
            this.B = B;
    }

    public Point2D.Double project(double x, double y, Point2D.Double dst) {      
        dst.y = C_y * y;
	dst.x = C_x * x * (A + this.asqrt(1. - B * y * y));
        return dst;
    }
    
    public boolean hasInverse() {
        return true;
    }
    
    public Point2D.Double projectInverse(double x, double y, Point2D.Double dst) {
        dst.y = y / C_y;
	dst.x = x / (C_x * (A + this.asqrt(1. - B * y * y)));
        return dst;
    }
    
    private double asqrt(double v) {
        return ((v <= 0) ? 0. : Math.sqrt(v));
    }
    
}
