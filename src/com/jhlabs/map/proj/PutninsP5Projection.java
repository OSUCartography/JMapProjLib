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

/*
 * This file was semi-automatically converted from the public-domain USGS PROJ source.
 */

/**
 * Changed superclass to PseudoCylindricalProjection.
 * Bernhard Jenny, May 25 2010.
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

public class PutninsP5Projection extends PseudoCylindricalProjection {

	protected double A;
	protected double B;

	private final static double C = 1.01346;
	private final static double D = 1.2158542;

	public PutninsP5Projection() {
		A = 2;
		B = 1;
	}

	public Point2D.Double project(double lplam, double lpphi, Point2D.Double xy) {
		xy.x = C * lplam * (A - B * Math.sqrt(1. + D * lpphi * lpphi));
		xy.y = C * lpphi;
		return xy;
	}

	public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double lp) {
		lp.y = xyy / C;
		lp.x = xyx / (C * (A - B * Math.sqrt(1. + D * lp.y * lp.y)));
		return lp;
	}

	public boolean hasInverse() {
		return true;
	}

	public String toString() {
		return "Putnins P5";
	}

}
