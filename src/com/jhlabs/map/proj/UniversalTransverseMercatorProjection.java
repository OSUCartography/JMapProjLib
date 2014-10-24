/**
Contributed by Andrey Novikov, September 2011.

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

import com.jhlabs.map.MapMath;

public class UniversalTransverseMercatorProjection extends TransverseMercatorProjection {

    protected int utmzone = -1;

    public void initialize() {
        // TODO
//    	if (!P->es) E_ERROR(-34); FIXME
        if (utmzone < 0) {
            int zone = (int) getZoneFromNearestMeridian(projectionLongitude * RTD);
            setUTMZone(zone);
        }
        super.initialize();
    }

    public int getRowFromNearestParallel(double latitude) {
        int degrees = (int)MapMath.radToDeg(MapMath.normalizeLatitude(latitude));
        if (degrees < -80 || degrees > 84) {
            return 0;
        }
        if (degrees > 80) {
            return 24;
        }
        return (degrees + 80) / 8 + 3;
    }

    public int getZoneFromNearestMeridian(double longitude) {
        
        // int zone = (int) (Math.floor(((180.0 + longitude) / 6)) + 1); FIXME
        int zone = (int) Math.floor((MapMath.normalizeLongitude(longitude) + Math.PI) * 30.0 / Math.PI) + 1;
        if (zone < 1) {
            zone = 1;
        } else if (zone > 60) {
            zone = 60;
        }
        
        /*
        if( Lat >= 56.0 && Lat < 64.0 && LongTemp >= 3.0 && LongTemp < 12.0 )
            ZoneNumber = 32;

		// Special zones for Svalbard
    	if( Lat >= 72.0 && Lat < 84.0 ) 
    	{
    	  if(      LongTemp >= 0.0  && LongTemp <  9.0 ) ZoneNumber = 31;
    	  else if( LongTemp >= 9.0  && LongTemp < 21.0 ) ZoneNumber = 33;
    	  else if( LongTemp >= 21.0 && LongTemp < 33.0 ) ZoneNumber = 35;
    	  else if( LongTemp >= 33.0 && LongTemp < 42.0 ) ZoneNumber = 37;
    	 }
    	 */

        
        return zone;
    }

    public void setIsSouth(boolean south) {
        falseNorthing = south ? 10000000. : 0.;
    }

    public void clearUTMZone() {
        utmzone = -1;
    }

    public void setUTMZone(int zone) {
        utmzone = zone - 1;

        projectionLongitude = (utmzone * 6 - 180 + 3) * DTR;  //+3 puts origin in middle of zone
        projectionLatitude = 0.0;
        scaleFactor = 0.9996;
        falseNorthing = 0.;
        falseEasting = 500000;
        //initialize(); FIXME
    }

    public String toString() {
        return "Universal Transverse Mercator";
    }
}
