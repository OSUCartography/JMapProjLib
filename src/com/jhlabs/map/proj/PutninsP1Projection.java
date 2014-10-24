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

 * PutninsP1Projection.java
 *
 * Created on May 19, 2007, 7:23 PM
 *
 */

package com.jhlabs.map.proj;

/**
 * Putnins P1 projection. Based on Proj4.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich
 */
public class PutninsP1Projection extends EllipticalPseudoCylindrical{
    
    /** Creates a new instance of PutninP1Projection */
    public PutninsP1Projection() {
        super(1.89490,
                0.94745,
                -0.5,
                0.30396355092701331433);
    }
    
    public String toString() {
        return "Putnins P1";
    }
}