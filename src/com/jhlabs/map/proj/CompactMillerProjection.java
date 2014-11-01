/*
 Copyright 2014 Bojan Savric

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

import java.awt.geom.*;

public class CompactMillerProjection extends CylindricalProjection {
    
    private final static double K1 = 0.9902;
    private final static double K2 = 0.1604;
    private final static double K3 = -0.03054;
    
    @Override
    public Point2D.Double project(double lon, double lat, Point2D.Double out) {
        final double lat_sq = lat * lat;
        out.x = lon;
        out.y = lat * (K1 + lat_sq * (K2 + K3 * lat_sq));
        return out;
    }

    @Override
    public boolean hasInverse() {
        return false;
    }

    @Override
    public String toString() {
        return "Compact Miller";
    }
}
