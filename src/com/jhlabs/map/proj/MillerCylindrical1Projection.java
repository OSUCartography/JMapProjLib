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
 * Changed name from Miller Cylindrical to Miller Cylindrical I
 * Bernhard Jenny, May 20 2010.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class MillerCylindrical1Projection extends CylindricalProjection {

	public MillerCylindrical1Projection() {
	}
	
	public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
		out.x = lplam;
		out.y = Math.log(Math.tan(MapMath.QUARTERPI + lpphi * .4)) * 1.25;
		return out;
	}

	public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
		out.x = xyx;
		out.y = 2.5 * (Math.atan(Math.exp(.8 * xyy)) - MapMath.QUARTERPI);
		return out;
	}

	public boolean hasInverse() {
		return true;
	}

	public String toString() {
		return "Miller Cylindrical I";
	}

}
