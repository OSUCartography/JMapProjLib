Changes to JMapProjLib Java Map Projection Library
--------------------------------------------------

Below are changes made to Jerry Huxtable's original release at
http://www.jhlabs.com/java/maps/proj/index.html

1. Structural changes

Made Projection and Ellipsoid class serializable by adding Serializable interface.

Moved Datum and Ellipsoid from com.jhlabs.map.proj to com.jhlabs.map

Changed base class of PseudoCylindricalProjection from CylindricalProjection to
Projection.

Projection base class: added binarySearchInverse, added transform and
inverseTransform with lon/lat as doubles.

ProjectionFactory:
Added private static Hashtable nameMap which maps between human-readable
projection names and proj4 names.
Changed ProjectionFactoryregister() to fill the new nameMap.
Changed initialize() to instantiate nameMap.
Added method getNamedProjection() to first map from a human-readable name to a
proj4 name and then to a Projection class.
Changed register(): the human-readable name of a projection is not passed anymore
to register(), but the name is retreived from an instance of the projection. This
avoids inconsistencies between names returned by the projection and names passed
to register().

SimpleConicProjection (base class for a series of conic projections): Changed
initialize() to use projectionLatitude1 and projectionLatitude2 instead of
hard-coded values. This class should be entirely removed.

2. Changes to individual projections, in alphabetical order

Aitoff: separated the code of the Aitoff and the Winkel Tripel projections.
Changed superclass from PseudoCylindricalProjection to Projection.

Albers: Changed base class to ConicProjection.

Apian Globular I and II: added.

Bacon: added.

Bonne: fixed inverse spherical.

Collignon: fixed inverse.

CrasterProjection: changed superclass from Projection to PseudocylindricalProjection.

CylindricalEqualAreaProjection: added missing toString() and isEqualArea(),
changed superclass to CylindricalProjection.

Eckert 1: changed superclass from Projection to PseudocylindricalProjection.

Eckert 3: added. Derives from abstract EllipticalPseudoCylindrical.

Eckert 5: changed superclass from Projection to PseudocylindricalProjection.

Eckert 4: removed two unused static variables. Added missing isEqualArea method.

Eckert 6: added.

Eckert-Greifendorff: split from Hammer to new class.

EllipticalPseudoCylindrical: added. This abstract class is the base for the new
Eckert 3, Kavraisky 7, Putnins P1 and Wagner 6 projections.

Equidistant Conical: fixed bugs, was not functional.

Equidistant Cylindrical: changed class name from PlateCarreeProjection,
changed name to "Equidistant Cylindrical (Plate Carr\u00e9e)", removed
isRectilinear (duplicate of super class), added support for latitude of true scale.

Euler: now derives from ConicProjection instead of intermediate base class. Fixed
bugs in inverse projection.

Fahey: changed superclass from Projection to PseudocylindricalProjection, fixed
inverse.

Foucaut Sinusoidal: added setter for parameter n of Foucaut Sinusoidal, and
added missing initialization of n to 0.5, as is done by proj4.

Fournier 2: added.

Gall: change superclass from Projection to CylindricalProjection.

Goode: changed superclass from Projection to PseudoCylindricalProjection.

Ginzburg VIII: Changed name from Ginsburg to Ginzburg. Changed "Ginsburg VIII
(TsNIIGAiK)" to "Ginzburg VIII (TsNIIGAiK 1944)". Changed superclass from
Projection to PseudocylindricalProjection.

Hammer: split from Eckert-Greifendorff.

Holzel: added.

Kavraisky 5: added missing isEqualArea method, renamed from KavraiskyVProjection
to Kavraisky5Projection.

Kavraisky 7: added. Derives from abstract EllipticalPseudoCylindrical.

Larrivee: added acute on e in string returned by toString

Loximuthal: Fixed forward and inverse projections, removed unused constants,
added validity test for parameter phi1.

McBride-Thomas Flat-Polar Parabolic: fixed forward.

Mercator: added missing isConformal method.

Miller Cylindrical I: changed name from Miller to be consistent with the name
of the new Miller Cylindrical II.

Miller Cylindrical II: added.

Mollweide: Changed class name from Molleweide to Mollweide, added isEqualArea.

Nell: fixed forward.

NellHammer: changed name from NellHProjection, changed superclass
from Projection to PseudoCylindricalProjection. Fixed inverse.

Nicolosi: Added initialization for minLongitude and maxLongitude.

Ortelius: added.

Polyconic (American): fixed spherical inverse.

Putnins P1: added. Derives from abstract EllipticalPseudoCylindrical.

Putnins P2: fixed forward projection.

Putnins P4': Was confounded with Putnins P4. Corrected name of projection and
changed name of class from PutninsP4Projection to PutninsP4PProjection. Changed
superclass from Projection to PseudoCylindricalProjection.

Putnins P5': Changed name string fom "Putnins P5P" to "Putnins P5'". Changed
superclass from Projection to PseudoCylindricalProjection.

Quartic Authalic: Addes missing isEqualArea

Robinson: Fixed bug in inverse computation, no exception is thrown from the
inverse projection if the input is out of bounds, added comments.

Sinusoidal: Added isEqualArea.

STSProjection: super class was ConicProjection, changed to
PseudocylindricalProjection and made abstract. STSProjection is the superclass
for Kavraisky5Projection, MBTFPS1Projection, FoucautProjection, and
QuarticAuthalicProjection, which are all pseudocylindricals. Fixed inverse.

TCEA: Added isEqualArea and changed base class to CylindricalProjection.

Tissot: now derives from ConicProjection instead of intermediate base class. Fixed
bugs in inverse projection.

Transverse Mercator: corrected spherical code in projectInverse, added isConformal.

Urmaev Flat-Polar Sinusoidal: fixed forward.

Wagner 2: fixed bug in forward projection, increased precisions of constants,
changed superclass to PseudoCylindricalProjection.

Wagner 6: added. Derives from abstract EllipticalPseudoCylindrical.

Werenskiold 1: Renamed from WerenskioldProjection to Werenskiold1Projection as
there are more projections by Werenskiold.

Winkel 1: added. Winkel I in Java uses a different default value (50 28') for
the latitude of true scale than proj4 (0).

Winkel 2: added.





Open questions / suggestion:

Change getName() to getProj4Name(), make getName() return toString()

Way to return altnerative names? and description?

What happens if longitude / latitude is out of bounds in forward projection?
What happens if X / Y is out of bounds in inverse projection?

Mercator: conflict with fix?
16 Aug 2007: Version 1.0.4: Fixed another bug in inverse tmerc projection.
11 Aug 2007: Version 1.0.3: Fixed a bug in inverse tmerc projection and added radian versions of projection methods.