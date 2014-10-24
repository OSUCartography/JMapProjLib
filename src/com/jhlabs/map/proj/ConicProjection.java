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
package com.jhlabs.map.proj;

/**
 * The superclass for all Conic projections.
 *
 * Bernhard Jenny, 17 September 2010:
 * Moved projectionLatitude1 and projectionLatitude2 from super class to
 * ConicProjection, as these are specific to conics.
 * 25 October 2010:  Constructors initializes projectionLatitude1 and
 * projectionLatitude2 to the value of projectionLatitude.
 */
public class ConicProjection extends Projection {

    /**
     * Standard parallel 1 (for projections which use it).
     */
    protected double projectionLatitude1;
    /**
     * Standard parallel 2 (for projections which use it)
     */
    protected double projectionLatitude2;

    protected ConicProjection() {
        projectionLatitude1 = projectionLatitude;
        projectionLatitude2 = projectionLatitude;
    }

    public void setProjectionLatitude1(double projectionLatitude1) {
        this.projectionLatitude1 = projectionLatitude1;
    }

    public double getProjectionLatitude1() {
        return projectionLatitude1;
    }

    public final void setProjectionLatitude1Degrees(double projectionLatitude1) {
        setProjectionLatitude1(DTR * projectionLatitude1);
    }

    public final double getProjectionLatitude1Degrees() {
        return getProjectionLatitude1() * RTD;
    }

    public void setProjectionLatitude2(double projectionLatitude2) {
        this.projectionLatitude2 = projectionLatitude2;
    }

    public double getProjectionLatitude2() {
        return projectionLatitude2;
    }

    public final void setProjectionLatitude2Degrees(double projectionLatitude2) {
        setProjectionLatitude2(DTR * projectionLatitude2);
    }

    public final double getProjectionLatitude2Degrees() {
        return getProjectionLatitude2() * RTD;
    }

    public String toString() {
        return "Conic";
    }
}
