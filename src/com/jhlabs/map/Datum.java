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
package com.jhlabs.map;

import java.util.Hashtable;

/**
 * A geodetic datum.
 */
public class Datum {

    private static Hashtable<String, Datum> registry = new Hashtable<String, Datum>();
    String name;
    Ellipsoid ellipsoid;
    double deltaX, deltaY, deltaZ;
    // it's made static because it's widely used everywhere
    public static Datum WGS_1984 = new Datum("WGS84", Ellipsoid.WGS_1984, 0, 0, 0, new String[]{"WGS 84"});
    // TODO factory pattern should be used instead to preserve memory?
    public static Datum[] datums = {
        new Datum("Adindan", Ellipsoid.CLARKE_1880, -166, -15, 204),
        new Datum("ADINDAN", Ellipsoid.CLARKE_1880, -162, -12, -206),
        new Datum("AFG", Ellipsoid.KRASOVSKY, -43, -163, 45),
        new Datum("Ain-El-Abd", Ellipsoid.INTERNATIONAL_1924, -150, -251, -2),
        new Datum("Alaska-NAD27", Ellipsoid.CLARKE_1866, -5, 135, 172),
        new Datum("Alaska-Canada", Ellipsoid.CLARKE_1880, -9, 151, 185),
        new Datum("Anna-1-Astro", Ellipsoid.AUSTRALIAN, -491, -22, 435),
        new Datum("ARC1950", Ellipsoid.CLARKE_1880, -143, -90, -294, new String[]{"ARC 1950 Mean"}),
        new Datum("ARC1960", Ellipsoid.CLARKE_1880, -160, -8, -300, new String[]{"ARC 1960 Mean"}),
        new Datum("Asc Island 58", Ellipsoid.INTERNATIONAL_1924, -207, 107, 52),
        new Datum("Astro B4", Ellipsoid.INTERNATIONAL_1924, 114, -116, -333),
        new Datum("Astro Beacon E", Ellipsoid.INTERNATIONAL_1924, 145, 75, -272),
        new Datum("Astro pos 71/4", Ellipsoid.INTERNATIONAL_1924, -320, 550, -494),
        new Datum("Astro stn 52", Ellipsoid.INTERNATIONAL_1924, 124, -234, -25),
        new Datum("Australian Geodetic 1966", Ellipsoid.AUSTRALIAN, -133, -48, 148),
        new Datum("Australian Geodetic 1984", Ellipsoid.AUSTRALIAN, -134, -48, 149, new String[]{"Australia Geo 1984"}),
        new Datum("Bahamas NAD27", Ellipsoid.CLARKE_1880, -4, 154, 178),
        new Datum("Bellevue IGN", Ellipsoid.INTERNATIONAL_1924, -127, -769, 472),
        new Datum("Bermuda 1957", Ellipsoid.CLARKE_1880, -73, 213, 296),
        new Datum("Bukit Rimpah", Ellipsoid.BESSEL, -384, 664, -48),
        // different sources state different ellipsoids
        new Datum("Camp_Area_Astro", Ellipsoid.INTERNATIONAL_1924, -104, -129, 239),
        new Datum("CAMP_AREA_ASTRO", Ellipsoid.INTERNATIONAL_1967, -104, -129, 239),
        new Datum("Campo_Inchauspe", Ellipsoid.INTERNATIONAL_1924, -148, 136, 90),
        new Datum("Canada_Mean(NAD27)", Ellipsoid.CLARKE_1866, -10, 158, 187),
        new Datum("Canal_Zone_(NAD27)", Ellipsoid.CLARKE_1866, 0, 125, 201),
        new Datum("Canton_Island_1966", Ellipsoid.INTERNATIONAL_1924, 298, -304, -375),
        new Datum("Cape", Ellipsoid.CLARKE_1880, -136, -108, -292),
        new Datum("Cape_Canaveral_mean", Ellipsoid.CLARKE_1866, -2, 150, 181),
        new Datum("Carribean NAD27", Ellipsoid.CLARKE_1866, -7, 152, 178),
        new Datum("Carthage", Ellipsoid.CLARKE_1880, -263, 6, 431),
        new Datum("Cent America NAD27", Ellipsoid.CLARKE_1866, 0, 125, 194),
        new Datum("CH-1903", Ellipsoid.BESSEL, 674, 15, 405),
        new Datum("Chatham 1971", Ellipsoid.INTERNATIONAL_1924, 175, -38, 113),
        new Datum("Chua Astro", Ellipsoid.INTERNATIONAL_1924, -134, 229, -29),
        new Datum("Corrego Alegre", Ellipsoid.INTERNATIONAL_1924, -206, 172, -6),
        new Datum("Cuba NAD27", Ellipsoid.CLARKE_1866, -9, 152, 178),
        new Datum("Cyprus", Ellipsoid.INTERNATIONAL_1924, -104, -101, -140),
        new Datum("Djakarta(Batavia)", Ellipsoid.BESSEL, -377, 681, -50),
        new Datum("DOS 1968", Ellipsoid.INTERNATIONAL_1924, 230, -199, -752),
        new Datum("Easter lsland 1967", Ellipsoid.INTERNATIONAL_1924, 211, 147, 111),
        new Datum("Egypt", Ellipsoid.INTERNATIONAL_1924, -130, -117, -151),
        // different sources state different ellipsoids
        new Datum("European 1950", Ellipsoid.INTERNATIONAL_1924, -87, -98, -121),
        new Datum("European Datum 1950", Ellipsoid.INTERNATIONAL_1967, -87, -98, -121),
        new Datum("European 1950 (Mean France)", Ellipsoid.INTERNATIONAL_1924, -87, -96, -120),
        new Datum("European 1950 (Spain and Portugal)", Ellipsoid.INTERNATIONAL_1924, -84, -107, -120),
        // different sources state different ellipsoids
        new Datum("European 1979", Ellipsoid.INTERNATIONAL_1924, -86, -98, -119),
        new Datum("European Datum 1979", Ellipsoid.INTERNATIONAL_1967, -86, -98, -119),
        new Datum("Finnish Nautical", Ellipsoid.INTERNATIONAL_1924, -78, -231, -97, new String[]{"Finland Hayford"}),
        new Datum("Gandajika Base", Ellipsoid.INTERNATIONAL_1924, -133, -321, 50),
        new Datum("GDA 94", Ellipsoid.GRS_1980, 0, 0, 0, new String[]{"Australian Geocentric 1994 (GDA94)"}),
        new Datum("Geodetic Datum 1949", Ellipsoid.INTERNATIONAL_1967, 84, -22, 209, new String[]{"Geodetic Datum 49"}),
        new Datum("Ghana", Ellipsoid.WGS_1984, 0, 0, 0), // alias?
        new Datum("Greenland NAD27", Ellipsoid.CLARKE_1866, 11, 114, 195),
        new Datum("Guam 1963", Ellipsoid.CLARKE_1866, -100, -248, 259),
        new Datum("Gunung Segara", Ellipsoid.BESSEL, -403, 684, 41),
        new Datum("Gunung Serindung 1962", Ellipsoid.WGS_1984, 0, 0, 0),
        new Datum("GUX1 Astro", Ellipsoid.INTERNATIONAL_1924, 252, -209, -751),
        new Datum("Herat North", Ellipsoid.INTERNATIONAL_1924, -333, -222, 114),
        new Datum("Hjorsey 1955", Ellipsoid.INTERNATIONAL_1924, -73, 46, 86),
        // different sources state different ellipsoids
        //new Datum("Hong Kong 1963", Ellipsoid.INTERNATIONAL_1924, -156, -271, -189),
        new Datum("Hong Kong 1963", Ellipsoid.INTERNATIONAL_1967, -156, -271, -189),
        // different sources state different ellipsoids
        new Datum("Hu-Tzu-Shan", Ellipsoid.INTERNATIONAL_1924, -634, -549, -201),
        new Datum("Hu Tzu from.han", Ellipsoid.INTERNATIONAL_1967, -634, -549, -201),
        new Datum("Indian", Ellipsoid.EVEREST_56, 289, 734, 257),
        new Datum("Iran", Ellipsoid.INTERNATIONAL_1924, -117, -132, -164),
        new Datum("Ireland 1965", Ellipsoid.AIRY_MOD, 506, -122, 611),
        new Datum("Israeli", Ellipsoid.CLARKE_1880_PAL, -235, -85, 264),
        new Datum("ISTS 073 Astro 69", Ellipsoid.INTERNATIONAL_1924, 208, -435, -229),
        new Datum("Johnston Island 61", Ellipsoid.INTERNATIONAL_1924, 191, -77, -204),
        new Datum("Kandawala", Ellipsoid.EVEREST_30, -97, 787, 86),
        new Datum("Kerguelen Island", Ellipsoid.INTERNATIONAL_1924, 145, -187, 103),
        new Datum("Kertau 48", Ellipsoid.EVEREST_48, -11, 851, 5),
        new Datum("L.C. 5 Astro", Ellipsoid.CLARKE_1866, 42, 124, 147),
        new Datum("La Reunion", Ellipsoid.INTERNATIONAL_1924, 94, -948, -1262),
        new Datum("Liberia 1964", Ellipsoid.CLARKE_1880, -90, 40, 88),
        new Datum("Luzon", Ellipsoid.CLARKE_1866, -133, -77, -51),
        new Datum("Mahe 1971", Ellipsoid.CLARKE_1880, 41, -220, -134),
        new Datum("Marco Astro", Ellipsoid.INTERNATIONAL_1924, -289, -124, 60),
        new Datum("Masirah Is. Nahrwan", Ellipsoid.CLARKE_1880, -247, -148, 369),
        new Datum("Massawa", Ellipsoid.BESSEL, 639, 405, 60),
        new Datum("Merchich", Ellipsoid.CLARKE_1880, 31, 146, 47),
        new Datum("Mexico NAD27", Ellipsoid.CLARKE_1866, -12, 130, 190),
        new Datum("Midway Astro 61", Ellipsoid.INTERNATIONAL_1924, 912, -58, 1227),
        new Datum("Mindanao", Ellipsoid.CLARKE_1866, -133, -79, -72),
        new Datum("Minna", Ellipsoid.CLARKE_1880, -92, -93, 122),
        new Datum("Montjong Lowe", Ellipsoid.WGS_1984, 0, 0, 0),
        new Datum("Nahrwan", Ellipsoid.CLARKE_1880, -231, -196, 482),
        new Datum("Naparima BWI", Ellipsoid.INTERNATIONAL_1924, -2, 374, 172),
        new Datum("NAD27", Ellipsoid.CLARKE_1866, -8, 160, 176, new String[]{"NAD27 CONUS"}),
        new Datum("NAD83", Ellipsoid.GRS_1980, 0, 0, 0),
        new Datum("NTF France", Ellipsoid.CLARKE_1880_IGN, -168, -60, 320),
        new Datum("North America 83", Ellipsoid.GRS_1980, 0, 0, 0),
        new Datum("N. America 1927 mean", Ellipsoid.CLARKE_1866, -8, 160, 176),
        new Datum("Observatorio 1966", Ellipsoid.INTERNATIONAL_1924, -425, -169, 81),
        new Datum("Old Egyptian", Ellipsoid.HELMET, -130, 110, -13),
        new Datum("Old Hawaiian mean", Ellipsoid.CLARKE_1866, 89, -279, -183, new String[]{"Old Hawaiian_mean"}),
        new Datum("Old Hawaiian Kauai", Ellipsoid.CLARKE_1866, 45, -290, -172),
        new Datum("Old Hawaiian Maui", Ellipsoid.CLARKE_1866, 65, -290, -190),
        new Datum("Old Hawaiian Oahu", Ellipsoid.CLARKE_1866, 56, -284, -181),
        // TODO should use lower case keys in registry?
        new Datum("Oman", Ellipsoid.CLARKE_1880, -346, -1, 224, new String[]{"OMAN"}),
        new Datum("Ordnance Survey 1936", Ellipsoid.AIRY, 375, -111, 431, new String[]{"OSGB36"}),
        new Datum("Pico De Las Nieves", Ellipsoid.INTERNATIONAL_1924, -307, -92, 127),
        new Datum("Pitcairn Astro 67", Ellipsoid.INTERNATIONAL_1924, 185, 165, 42),
        // different sources state different ellipsoids
        new Datum("PROVISIONAL_S_AMERICAN_1956", Ellipsoid.INTERNATIONAL_1967, -288, 175, -376),
        new Datum("S. Am. 1956 mean(P)", Ellipsoid.INTERNATIONAL_1924, -288, 175, -376),
        new Datum("Puerto Rico", Ellipsoid.CLARKE_1866, 11, 72, -101),
        new Datum("Pulkovo 1942", Ellipsoid.KRASOVSKY, 27, -135, -89),
        new Datum("Pulkovo 1942 (1)", Ellipsoid.KRASOVSKY, 28, -130, -95, new String[]{"Pulkovo 1942 (2)"}),
        new Datum("Pulkovo 1942 (2)", Ellipsoid.KRASOVSKY, 28, -130, -95),
        new Datum("Qornoq", Ellipsoid.INTERNATIONAL_1924, 164, 138, -189),
        new Datum("Quatar National", Ellipsoid.INTERNATIONAL_1924, -128, -283, 22),
        new Datum("Rijksdriehoeksmeting", Ellipsoid.BESSEL, 593, 26, 478),
        new Datum("Rome 1940", Ellipsoid.INTERNATIONAL_1924, -225, -65, 9),
        new Datum("RT 90", Ellipsoid.BESSEL, 498, -36, 568),
        new Datum("S42", Ellipsoid.KRASOVSKY, 28, -121, -77),
        new Datum("S. Am. 1969 mean", Ellipsoid.SA_1969, -57, 1, -41, new String[]{"South American 1969"}),
        new Datum("S. Chilean 1963 (P)", Ellipsoid.INTERNATIONAL_1924, 16, 196, 93),
        new Datum("S.E.Asia_(Indian)", Ellipsoid.EVEREST_30, 173, 750, 264),
        new Datum("SAD-69/Brazil", Ellipsoid.SA_1969, -60, -2, -41),
        new Datum("Santa Braz", Ellipsoid.INTERNATIONAL_1924, -203, 141, 53),
        new Datum("Santo (DOS)", Ellipsoid.INTERNATIONAL_1924, 170, 42, 84),
        new Datum("Sapper Hill 43", Ellipsoid.INTERNATIONAL_1924, -355, 16, 74),
        new Datum("Schwarzeck", Ellipsoid.BESSEL_NAM, 616, 97, -251),
        new Datum("Sicily", Ellipsoid.INTERNATIONAL_1924, -97, -88, -135),
        new Datum("Sierra Leone 1960", Ellipsoid.WGS_1984, 0, 0, 0),
        new Datum("South Asia", Ellipsoid.FISCHER_MOD, 7, -10, -26),
        new Datum("Southeast Base", Ellipsoid.INTERNATIONAL_1924, -499, -249, 314),
        new Datum("Southwest Base", Ellipsoid.INTERNATIONAL_1924, -104, 167, -38),
        new Datum("Sweden", Ellipsoid.BESSEL, 424.300000, -80.500000, 613.100000),
        new Datum("Tananarive Obs 25", Ellipsoid.INTERNATIONAL_1924, -189, -242, -91),
        new Datum("Thai/Viet (Indian)", Ellipsoid.EVEREST_30, 214, 836, 303),
        new Datum("Timbalai 1948", Ellipsoid.EVEREST_30, -689, 691, -45),
        new Datum("Tokyo", Ellipsoid.BESSEL, -128, 481, 664, new String[]{"Tokyo mean"}),
        new Datum("Tristan Astro 1968", Ellipsoid.INTERNATIONAL_1924, -632, 438, -609),
        new Datum("United Arab Emirates", Ellipsoid.CLARKE_1880, -249, -156, 381),
        new Datum("Viti Levu 1916", Ellipsoid.CLARKE_1880, 51, 391, -36),
        new Datum("Wake Eniwetok 60", Ellipsoid.HOUGH, 101, 52, -39, new String[]{"Wake-Eniwetok 1960"}),
        new Datum("WGS 72", Ellipsoid.WGS_1972, 0, 0, 5),
        new Datum("Yacare", Ellipsoid.INTERNATIONAL_1924, -155, 171, 37),
        new Datum("Zanderij", Ellipsoid.INTERNATIONAL_1924, -265, 120, -358),
        WGS_1984
    };

    public Datum(String name, Ellipsoid ellipsoid, double deltaX, double deltaY, double deltaZ) {
        this(name, ellipsoid, deltaX, deltaY, deltaZ, null);
    }

    public Datum(String name, Ellipsoid ellipsoid, double deltaX, double deltaY, double deltaZ, String[] aliases) {
        this.name = name;
        this.ellipsoid = ellipsoid;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
        registry.put(name, this);
        if (aliases != null) {
            for (String alias : aliases) {
                registry.put(alias, this);
            }
        }
    }

    public static final Datum get(String name) {
        return registry.get(name);
    }

    public String toString() {
        return name;
    }

    public GeodeticPosition transformToWGS84(GeodeticPosition from) {
        return transform(from, this, Datum.WGS_1984);
    }

    public static final GeodeticPosition transform(GeodeticPosition from, Datum fromDatum, Datum toDatum) {
        return transform(from, fromDatum.ellipsoid.equatorRadius,
                fromDatum.ellipsoid.flattening, toDatum.ellipsoid.equatorRadius,
                toDatum.ellipsoid.flattening, fromDatum.deltaX, fromDatum.deltaY,
                fromDatum.deltaZ);
    }

    /**
     ** Transforms coordinates in source datum to coordinates in destination datum using Molodensky algorithm.
     ** Contributed by Andrey Novikov, September 2011,
     ** @param from source GeodeticPosition (degrees, meters)
     ** @param Sa source semi-major axis (meters)
     ** @param Sf source flattening
     ** @param Da destination semi-major axis (meters)
     ** @param Df destination flattening
     ** @param dx delta x
     ** @param dy delta y
     ** @param dz delta z
     **
     ** @return destination GeodeticPosition (degrees, meters)
     */
    public static final GeodeticPosition transform(GeodeticPosition from, double Sa,
            double Sf, double Da, double Df, double dx,
            double dy, double dz) {
        double frlat;
        double frlon;
        double esq;
        double bda;
        double da;
        double df;
        double N;
        double M;
        double tmp;
        double tmp2;
        double dlambda;
        double dheight;
        double phis;
        double phic;
        double lams;
        double lamc;

        GeodeticPosition to = new GeodeticPosition();

        esq = 2.0 * Sf - Math.pow(Sf, 2.0);
        bda = 1.0 - Sf;
        frlat = Math.toRadians(from.lat);
        frlon = Math.toRadians(from.lon);

        da = Da - Sa;
        df = Df - Sf;

        phis = Math.sin(frlat);
        phic = Math.cos(frlat);
        lams = Math.sin(frlon);
        lamc = Math.cos(frlon);

        N = Sa / Math.sqrt(1.0 - esq * Math.pow(phis, 2.0));

        tmp = (1.0 - esq) / Math.pow((1.0 - esq * Math.pow(phis, 2.0)), 1.5);
        M = Sa * tmp;

        tmp = df * ((M / bda) + N * bda) * phis * phic;
        tmp2 = da * N * esq * phis * phic / Sa;
        tmp2 += ((-dx * phis * lamc - dy * phis * lams) + dz * phic);
        to.lat = (tmp2 + tmp) / (M + from.h);

        dlambda = (-dx * lams + dy * lamc) / ((N + from.h) * phic);

        dheight = dx * phic * lamc + dy * phic * lams + dz * phis - da * (Sa / N)
                + df * bda * N * phis * phis;

        to.lat = Math.toDegrees(frlat + to.lat);
        to.lon = Math.toDegrees(frlon + dlambda);
        to.h = from.h + dheight;

        return to;
    }
}
