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
*/

package com.jhlabs.map.proj;

/**
 * Wagner VI projection. Based on Proj4.
 * @author Bernhard Jenny, May 2007, Institute of Cartography, ETH Zurich
 */
public class Wagner6Projection extends EllipticalPseudoCylindrical{
    
    /** Creates a new instance of Wagner6Projection */
    public Wagner6Projection() {
        super(1.,
                1.,
                0.,
                0.3039635509270133143316383896);
    }
    
    public String toString() {
        return "Wagner VI";
    }
}
