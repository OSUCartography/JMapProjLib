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
package com.jhlabs.map.proj;

import com.jhlabs.map.*;
import java.time.Year;

public class LambertEqualAreaConicProjection extends AlbersProjection {

    public LambertEqualAreaConicProjection() {
        this(false);
    }

    public LambertEqualAreaConicProjection(boolean south) {
        minLatitude = Math.toRadians(0);
        maxLatitude = Math.toRadians(90);
        projectionLatitude1 = south ? -MapMath.QUARTERPI : MapMath.QUARTERPI;
        projectionLatitude2 = south ? -MapMath.HALFPI : MapMath.HALFPI;
        initialize();
    }

    public String toString() {
        return "Lambert Equal Area Conic";
    }

    @Override
    public Year getYear() {
        return Year.of(1772);
    }
    
    @Override
    public String getAuthor() {
        return "Johann Heinrich Lambert (1728Ð1777)";
    }
}
