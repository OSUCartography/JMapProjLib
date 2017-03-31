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
/**
 * Removed isRectilinear (duplicate of super class), changed class name from 
 * PlateCarreeProjection to EquidistantCylindricalProjection, changed 
 * name to "Equidistant Cylindrical (Plate Carr\u00e9e)", added support for
 * latitude of true scale.
 * by Bernhard Jenny, June 26, 2008 and July 13, 2010.
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;
import java.time.Year;

public class EquidistantCylindricalProjection extends CylindricalProjection {

    private double cosTrueScaleLatitude;

    @Override
    public Point2D.Double project(double x, double y, Point2D.Double dst) {
        dst.x = x * cosTrueScaleLatitude;
        dst.y = y;
        return dst;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
        lp.y = y;
        lp.x = x / cosTrueScaleLatitude;
        return lp;
    }

    @Override
    public void initialize() {
        super.initialize();
        cosTrueScaleLatitude = Math.cos(trueScaleLatitude);
        es = 0.;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public String toString() {
        return "Equirectangular";
    }
    
    public String getDescription() {
        return super.getDescription() + " Plate Carr\u00e9e if the equator is the parallel of true scale.";
    }
    
    @Override
    public Year getYear() {
        return Year.of(100);
    }
    
    @Override
    public String getAuthor() {
        return "Marinus of Tyre";
    }
    
    @Override
    public String getHistoryDescription() {
        return super.getHistoryDescription() + " Used in the 13th century and later for maps of Ptolemy's Geography.";
    }
}