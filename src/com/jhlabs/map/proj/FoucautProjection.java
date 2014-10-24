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
 *
 * Bernhard Jenny, 23 September 2010: fixed wrong parameter in call to super,
 * added isEqualArea.
 */
package com.jhlabs.map.proj;

public class FoucautProjection extends STSProjection {

    public FoucautProjection() {
        super(1.772453850905516027298167483, 2., true);
    }

    public boolean isEqualArea() {
        return true;
    }

    public String toString() {
        return "Foucaut";
    }
}
