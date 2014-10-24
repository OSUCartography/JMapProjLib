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
 * Renamed from WerenskioldProjection to Werenskiold1Projection as there are
 * more projections by Werenskiold. Change by Bernhard Jenny, May 19 2010.
 */
package com.jhlabs.map.proj;

/**
 * Werenskiold I Projection, identical to Putnins P4', just a bit larger.
 */
public class Werenskiold1Projection extends PutninsP4PProjection {

    public Werenskiold1Projection() {
        C_x = 1;
        C_y = 4.442882938;
    }

    public String toString() {
        return "Werenskiold I";
    }
}
