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

 * Eckert3Projection.java
 *
 * Created  March 2007
 *
 */

package com.jhlabs.map.proj;

/**
 * Eckert 3 Projection. Based on Proj4.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich
 */
public class Eckert3Projection extends EllipticalPseudoCylindrical{
    
    /** Creates a new instance of Eckert3Projection */
    public Eckert3Projection() {
        super(0.42223820031577120149,
                0.84447640063154240298,
                1.,
                0.4052847345693510857755);
    }
    
    public String toString() {
        return "Eckert III";
    }
}