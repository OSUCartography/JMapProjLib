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

/**
Changes:
 * Added private static Hashtable nameMap which maps between human-readable
 * projection names and proj4 names.
 * Changed ProjectionFactoryregister() to fill the new nameMap.
 * Changed initialize() to instantiate nameMap.
 * Added method getNamedProjection() to first map from a human-readable name to a
 * proj4 name and then to a Projection class.
 * Changed register(): the human-readable name of a projection is not passed anymore
 * to register(), but the name is retrieved from an instance of the projection. This
 * avoids inconsistencies between names returned by the projection and names passed
 * to register().
 * modified readProjectionFile such that opened file with
 * coordinate system specification is always closed when the method exits.
 * Bernhard Jenny, 2010
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.*;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectionFactory {

    private final static double SIXTH = .1666666666666666667; /* 1/6 */

    private final static double RA4 = .04722222222222222222; /* 17/360 */

    private final static double RA6 = .02215608465608465608; /* 67/3024 */

    private final static double RV4 = .06944444444444444444; /* 5/72 */

    private final static double RV6 = .04243827160493827160; /* 55/1296 */

    private static final AngleFormat format = new AngleFormat(AngleFormat.ddmmssPattern, true);

    /**
     * Return a projection initialized with a PROJ.4 argument list.
     */
    public static Projection fromPROJ4Specification(String[] args) {
        Projection projection = null;
        Ellipsoid ellipsoid = null;
        double a = 0, b = 0, es = 0;

        Hashtable params = new Hashtable();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("+")) {
                int index = arg.indexOf('=');
                if (index != -1) {
                    String key = arg.substring(1, index);
                    String value = arg.substring(index + 1);
                    params.put(key, value);
                }
            }
        }

        String s;
        s = (String) params.get("proj");
        if (s != null) {
            projection = getNamedPROJ4Projection(s);
            if (projection == null) {
                throw new ProjectionException("Unknown projection: " + s);
            }
        }

        s = (String) params.get("init");
        if (s != null) {
            projection = getNamedPROJ4CoordinateSystem(s);
            if (projection == null) {
                throw new ProjectionException("Unknown projection: " + s);
            }
            a = projection.getEquatorRadius();
            es = projection.getEllipsoid().getEccentricitySquared();
        }

        // Set the ellipsoid
        String ellipsoidName = "";
        s = (String) params.get("R");
        if (s != null) {
            a = Double.parseDouble(s);
        } else {
            s = (String) params.get("ellps");
            if (s == null) {
                s = (String) params.get("datum");
            }
            if (s != null) {
                Ellipsoid[] ellipsoids = Ellipsoid.ellipsoids;
                for (int i = 0; i < ellipsoids.length; i++) {
                    if (ellipsoids[i].shortName.equals(s)) {
                        ellipsoid = ellipsoids[i];
                        break;
                    }
                }
                if (ellipsoid == null) {
                    throw new ProjectionException("Unknown ellipsoid: " + s);
                }
                es = ellipsoid.eccentricity2;
                a = ellipsoid.equatorRadius;
                ellipsoidName = s;
            } else {
                s = (String) params.get("a");
                if (s != null) {
                    a = Double.parseDouble(s);
                }
                s = (String) params.get("es");
                if (s != null) {
                    es = Double.parseDouble(s);
                } else {
                    s = (String) params.get("rf");
                    if (s != null) {
                        es = Double.parseDouble(s);
                        es = es * (2. - es);
                    } else {
                        s = (String) params.get("f");
                        if (s != null) {
                            es = Double.parseDouble(s);
                            es = 1.0 / es;
                            es = es * (2. - es);
                        } else {
                            s = (String) params.get("b");
                            if (s != null) {
                                b = Double.parseDouble(s);
                                es = 1. - (b * b) / (a * a);
                            }
                        }
                    }
                }
                if (b == 0) {
                    b = a * Math.sqrt(1. - es);
                }
            }

            s = (String) params.get("R_A");
            if (s != null && Boolean.getBoolean(s)) {
                a *= 1. - es * (SIXTH + es * (RA4 + es * RA6));
            } else {
                s = (String) params.get("R_V");
                if (s != null && Boolean.getBoolean(s)) {
                    a *= 1. - es * (SIXTH + es * (RV4 + es * RV6));
                } else {
                    s = (String) params.get("R_a");
                    if (s != null && Boolean.getBoolean(s)) {
                        a = .5 * (a + b);
                    } else {
                        s = (String) params.get("R_g");
                        if (s != null && Boolean.getBoolean(s)) {
                            a = Math.sqrt(a * b);
                        } else {
                            s = (String) params.get("R_h");
                            if (s != null && Boolean.getBoolean(s)) {
                                a = 2. * a * b / (a + b);
                                es = 0.;
                            } else {
                                s = (String) params.get("R_lat_a");
                                if (s != null) {
                                    double tmp = Math.sin(parseAngle(s));
                                    if (Math.abs(tmp) > MapMath.HALFPI) {
                                        throw new ProjectionException("-11");
                                    }
                                    tmp = 1. - es * tmp * tmp;
                                    a *= .5 * (1. - es + tmp) / (tmp * Math.sqrt(tmp));
                                    es = 0.;
                                } else {
                                    s = (String) params.get("R_lat_g");
                                    if (s != null) {
                                        double tmp = Math.sin(parseAngle(s));
                                        if (Math.abs(tmp) > MapMath.HALFPI) {
                                            throw new ProjectionException("-11");
                                        }
                                        tmp = 1. - es * tmp * tmp;
                                        a *= Math.sqrt(1. - es) / tmp;
                                        es = 0.;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        projection.setEllipsoid(new Ellipsoid(ellipsoidName, a, es, ellipsoidName));

        // Other arguments
//		projection.setProjectionLatitudeDegrees( 0 );
//		projection.setProjectionLatitude1Degrees( 0 );
//		projection.setProjectionLatitude2Degrees( 0 );
        s = (String) params.get("lat_0");
        if (s != null) {
            projection.setProjectionLatitudeDegrees(parseAngle(s));
        }
        s = (String) params.get("lon_0");
        if (s != null) {
            projection.setProjectionLongitudeDegrees(parseAngle(s));
        }
        s = (String) params.get("lat_1");
        if (s != null && projection instanceof ConicProjection) {
            ConicProjection conic = (ConicProjection) projection;
            conic.setProjectionLatitude1Degrees(parseAngle(s));
        }
        s = (String) params.get("lat_2");
        if (s != null && projection instanceof ConicProjection) {
            ConicProjection conic = (ConicProjection) projection;
            conic.setProjectionLatitude2Degrees(parseAngle(s));
        }
        s = (String) params.get("lat_ts");
        if (s != null) {
            projection.setTrueScaleLatitudeDegrees(parseAngle(s));
        }
        s = (String) params.get("x_0");
        if (s != null) {
            projection.setFalseEasting(Double.parseDouble(s));
        }
        s = (String) params.get("y_0");
        if (s != null) {
            projection.setFalseNorthing(Double.parseDouble(s));
        }

        s = (String) params.get("k_0");
        if (s == null) {
            s = (String) params.get("k");
        }
        if (s != null) {
            projection.setScaleFactor(Double.parseDouble(s));
        }

        s = (String) params.get("units");
        if (s != null) {
            Unit unit = Units.findUnits(s);
            if (unit != null) {
                projection.setFromMetres(1.0 / unit.value);
            }
        }
        s = (String) params.get("to_meter");
        if (s != null) {
            projection.setFromMetres(1.0 / Double.parseDouble(s));
        }

        if (projection instanceof UniversalTransverseMercatorProjection) {
            s = (String) params.get("zone");
            if (s != null) {
                ((UniversalTransverseMercatorProjection) projection).setUTMZone(Integer.parseInt(s));
            }

            s = (String) params.get("bsouth");
            if (s != null) {
                ((UniversalTransverseMercatorProjection) projection).setIsSouth(true);
            }
        }

//zone
//towgs84 - see Datum
//alpha
//datum
//lat_ts
//azi
//lonc
//rf
//pm
        // FIXME !!!
        // projection.initialize();

        return projection;
    }

    private static double parseAngle(String s) {
        return format.parse(s, null).doubleValue();
    }
    private static Hashtable registry;
    private static Hashtable nameMap;

    private static void register(String proj4Name, Class cls) throws InstantiationException, IllegalAccessException {
        try {
            registry.put(proj4Name, cls);
            Projection projection = (Projection) cls.newInstance();
            String readableName = projection.getName();
            nameMap.put(readableName, proj4Name);
        } catch (InstantiationException e) {
            System.err.println("unable to register " + proj4Name);
            throw e;
        }
    }

    public static Projection getNamedProjection(String name) {
        if (registry == null) {
            initialize();
        }
        String proj4Name = (String) nameMap.get(name);
        return getNamedPROJ4Projection(proj4Name);
    }

    public static Projection getNamedPROJ4Projection(String name) {
        if (registry == null) {
            initialize();
        }
        Class cls = (Class) registry.get(name);
        if (cls != null) {
            try {
                return (Projection) cls.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object[] getOrderedProjectionNames() {
        if (registry == null) {
            initialize();
        }
        Object[] names = nameMap.keySet().toArray();
        Arrays.sort(names);
        return names;
    }

    private static void initialize() {
        try {
            registry = new Hashtable();
            nameMap = new Hashtable();

            register("aea", AlbersProjection.class);
            register("aeqd", EquidistantAzimuthalProjection.class);
            register("airy", AiryProjection.class);
            register("aitoff", AitoffProjection.class);
//            register("alsk", Projection.class);
            register("apian1", Apian1Projection.class);
            register("apian2", Apian2Projection.class);
            register("ardn_cls", ArdenCloseProjection.class);
            register("aspect", AspectAdaptiveProjection.class);
            register("august", AugustProjection.class);
            register("bacon", BaconProjection.class);
            register("bipc", BipolarProjection.class);
            register("boggs", BoggsProjection.class);
            register("bonne", BonneProjection.class);
            register("cass", CassiniProjection.class);
            register("cc", CentralCylindricalProjection.class);
            register("cea", CylindricalEqualAreaProjection.class);
            register("compmill", CompactMillerProjection.class);
//		register( "chamb", Projection.class);
            register("collg", CollignonProjection.class);
            register("crast", CrasterProjection.class);
            register("denoy", DenoyerProjection.class);
            register("eck1", Eckert1Projection.class);
            register("eck2", Eckert2Projection.class);
            register("eck3", Eckert3Projection.class);
            register("eck4", Eckert4Projection.class);
            register("eck5", Eckert5Projection.class);
            register("eck6", Eckert6Projection.class);
            register("eckgreif", EckertGreifendorffProjection.class);
            register("eqc", EquidistantCylindricalProjection.class);
            register("eqdc", EquidistantConicProjection.class);
            register("euler", EulerProjection.class);
            register("fahey", FaheyProjection.class);
            register("fouc", FoucautProjection.class);
            register("fouc_s", FoucautSinusoidalProjection.class);
            register("four2", Fournier2Projection.class);
            register("gall", GallProjection.class);
            register("gallpeters", GallPetersProjection.class);
            register("gins8", Ginzburg8Projection.class);
//		register( "gn_sinu", Projection.class);
            register("gnom", GnomonicAzimuthalProjection.class);
            register("goode", GoodeProjection.class);
//		register( "gs48", Projection.class, "Mod. Stererographics of 48 U.S." );
//		register( "gs50", Projection.class, "Mod. Stererographics of 50 U.S." );
            register("hammer", HammerProjection.class); // Eckert-Greifendorff is in own class
            register("hatano", HatanoProjection.class);
            register("holzel", HolzelProjection.class);
            register("hufnagel", HufnagelProjection.class);
//		register( "imw_p", Projection.class, "Internation Map of the World Polyconic" );
            register("kav1", Kavrayskiy1Projection.class);
            register("kav5", Kavrayskiy5Projection.class);
            register("kav7", Kavrayskiy7Projection.class);
//		register( "labrd", Projection.class, "Laborde" );
            register("laea", LambertAzimuthalEqualAreaProjection.class);
            register("lagrng", LagrangeProjection.class);
            register("larr", LarriveeProjection.class);
            register("lask", LaskowskiProjection.class);
            register("lcc", LambertConformalConicProjection.class);
            register("leac", LambertEqualAreaConicProjection.class);
//		register( "lee_os", Projection.class, "Lee Oblated Stereographic" );
            register("longlat", LinearProjection.class);
            register("loxim", LoximuthalProjection.class);
            register("lsat", LandsatProjection.class);
            register("mbt_s", McBrydeThomasSine1Projection.class);
            register("mbt_fps", McBrydeThomasFlatPolarSine2Projection.class);
            register("mbtfpp", McBrydeThomasFlatPolarParabolicProjection.class);
            register("mbtfpq", McBrydeThomasFlatPolarQuarticProjection.class);
            // register("mbtfps", .class);
            register("merc", MercatorProjection.class);
//		register( "mil_os", Projection.class, "Miller Oblated Stereographic" );
            register("mill", MillerCylindrical1Projection.class);
//		register( "mpoly", Projection.class, "Modified Polyconic" );
            register("moll", MollweideProjection.class);
            register("murd1", Murdoch1Projection.class);
            register("murd2", Murdoch2Projection.class);
            register("murd3", Murdoch3Projection.class);
            register("natearth", NaturalEarthProjection.class);
            register("natearth2", NaturalEarth2Projection.class);
            register("nell", NellProjection.class);
            register("nell_h", NellHammerProjection.class);
            register("nicol", NicolosiProjection.class);
            register("nsper", PerspectiveProjection.class);
            register("nzmg", NZMGProjection.class);
//		register( "ob_tran", Projection.class, "General Oblique Transformation" );
//		register( "ocea", Projection.class, "Oblique Cylindrical Equal Area" );
//		register( "oea", Projection.class, "Oblated Equal Area" );
            register("omerc", ObliqueMercatorProjection.class);
            register("ortel", OrteliusProjection.class);
            register("ortho", OrthographicAzimuthalProjection.class);
            register("patt", PattersonProjection.class);
            register("pconic", PerspectiveConicProjection.class);
            register("poly", PolyconicProjection.class);
            register("putp1", PutninsP1Projection.class);
            register("putp2", PutninsP2Projection.class);
//		register( "putp3", Projection.class, "Putnins P3" );            
            register("putp4p", PutninsP4PProjection.class);
            register("putp5", PutninsP5Projection.class);
            register("putp5p", PutninsP5PProjection.class);
//		register( "putp6", Projection.class, "Putnins P6" );
//		register( "putp6p", Projection.class, "Putnins P6'" );
            register("qua_aut", QuarticAuthalicProjection.class);
            register("robin", RobinsonProjection.class); // RobinsonProjectionOriginal_Proj4_JHL has vertical shift at latitude +/-40 degrees
            register("rpoly", RectangularPolyconicProjection.class);
            register("sinu", SinusoidalProjection.class);
            register("somerc", SwissObliqueMercatorProjection.class);
            register("stere", StereographicAzimuthalProjection.class);
            register("tcc", TCCProjection.class);
            register("tcea", TCEAProjection.class);
            register("tissot", TissotProjection.class);
            register("tmerc", TransverseMercatorProjection.class);
            register("tlat", TransformedLambertAzimuthalTransverse.class);
            
//		register( "tpeqd", Projection.class, "Two Point Equidistant" );
//		register( "tpers", Projection.class, "Tilted perspective" );
//		register( "ups", Projection.class, "Universal Polar Stereographic" );
//		register( "urm5", Projection.class, "Urmaev V" );
            register("urmfps", URMFPSProjection.class); // Urmaev Flat-Polar Sinusoidal
            register("utm", UniversalTransverseMercatorProjection.class);
            register("vandg", VanDerGrintenProjection.class);
//		register( "vandg2", Projection.class, "van der Grinten II" );
//		register( "vandg3", Projection.class, "van der Grinten III" );
//		register( "vandg4", Projection.class, "van der Grinten IV" );
            register("vitk1", VitkovskyProjection.class);
            register("wag1", Wagner1Projection.class);
            register("wag2", Wagner2Projection.class);
            register("wag3", Wagner3Projection.class);
            register("wag4", Wagner4Projection.class);
            register("wag5", Wagner5Projection.class);
            register("wag6", Wagner6Projection.class);
            register("wag7", Wagner7Projection.class);
            register("weren", Werenskiold1Projection.class);
            register("wink1", Winkel1Projection.class);
            register("wink2", Winkel2Projection.class);
            register("wintri", WinkelTripelProjection.class);
        } catch (InstantiationException ex) {
            Logger.getLogger(ProjectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ProjectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Projection readProjectionFile(String file, String name) throws IOException {

        BufferedReader reader = null;
        try {
            String filePath = "/coordsys/" + file;
            InputStream is = ProjectionFactory.class.getResourceAsStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is));
            StreamTokenizer t = new StreamTokenizer(reader);
            t.commentChar('#');
            t.ordinaryChars('0', '9');
            t.ordinaryChars('.', '.');
            t.ordinaryChars('-', '-');
            t.ordinaryChars('+', '+');
            t.wordChars('0', '9');
            t.wordChars('\'', '\'');
            t.wordChars('"', '"');
            t.wordChars('_', '_');
            t.wordChars('.', '.');
            t.wordChars('-', '-');
            t.wordChars('+', '+');
            t.wordChars(',', ',');
            t.nextToken();

            while (t.ttype == '<') {
                t.nextToken();
                if (t.ttype != StreamTokenizer.TT_WORD) {
                    throw new IOException(t.lineno() + ": Word expected after '<'");
                }

                String cname = t.sval;
                t.nextToken();
                if (t.ttype != '>') {
                    throw new IOException(t.lineno() + ": '>' expected");
                }
                t.nextToken();
                Vector v = new Vector();
                while (t.ttype != '<') {
                    if (t.ttype == '+') {
                        t.nextToken();
                    }
                    if (t.ttype != StreamTokenizer.TT_WORD) {
                        throw new IOException(t.lineno() + ": Word expected after '+'");
                    }
                    String key = t.sval;
                    t.nextToken();
                    if (t.ttype == '=') {
                        t.nextToken();
                        //Removed check to allow for proj4 hack +nadgrids=@null
                        //if ( t.ttype != StreamTokenizer.TT_WORD )
                        //	throw new IOException( t.lineno()+": Value expected after '='" );
                        String value = t.sval;
                        t.nextToken();
                        if (key.startsWith("+")) {
                            v.add(key + "=" + value);
                        } else {
                            v.add("+" + key + "=" + value);
                        }
                    }
                }
                t.nextToken();
                if (t.ttype != '>') {
                    throw new IOException(t.lineno() + ": '<>' expected");
                }
                t.nextToken();
                if (cname.equals(name)) {
                    String[] args = new String[v.size()];
                    v.copyInto(args);
                    return fromPROJ4Specification(args);
                }
            }
            return null;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    public static Projection getNamedPROJ4CoordinateSystem(String name) {
        String[] files = {
            "world",
            "nad83",
            "nad27",
            "esri",
            "epsg",};

        try {
            int p = name.indexOf(':');
            if (p >= 0) {
                return readProjectionFile(name.substring(0, p), name.substring(p + 1));
            }

            for (int i = 0; i < files.length; i++) {
                Projection projection = readProjectionFile(files[i], name);
                if (projection != null) {
                    return projection;
                }
            }
        } catch (IOException e) {
        }
        return null;
    }

    public static void main(String[] args) {
        Projection projection = ProjectionFactory.fromPROJ4Specification(args);


        if (projection != null) {
            System.out.println(projection.getPROJ4Description());

            for (int i = 0; i
                    < args.length; i++) {
                String arg = args[i];


                if (!arg.startsWith("+") && !arg.startsWith("-")) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(new File(args[i])));
                        Point2D.Double p = new Point2D.Double();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            StringTokenizer t = new StringTokenizer(line, " ");
                            String slon = t.nextToken();
                            String slat = t.nextToken();
                            p.x = format.parse(slon, null).doubleValue();
                            p.y = format.parse(slat, null).doubleValue();
                            projection.transform(p, p);
                            System.out.println(p.x + " " + p.y);
                        }
                    } catch (IOException e) {
                        System.out.println("IOException: " + args[i] + ": " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("Can't find projection " + args[0]);
        }

    }
}
