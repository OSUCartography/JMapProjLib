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

public class OtherProjections {
	public Object createPlugin() {
		return new Object[] {
			//new SimpleConicProjection(SimpleConicProjection.TISSOT),
			new SimpleConicProjection(SimpleConicProjection.MURD1),
			new SimpleConicProjection(SimpleConicProjection.MURD2),
			new SimpleConicProjection(SimpleConicProjection.MURD3),
			new SimpleConicProjection(SimpleConicProjection.PCONIC),
			new SimpleConicProjection(SimpleConicProjection.VITK1),
			new MollweideProjection(MollweideProjection.WAGNER4),
			new MollweideProjection(MollweideProjection.WAGNER5),
		};
	}
}
