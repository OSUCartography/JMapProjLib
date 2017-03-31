package com.jhlabs.map.proj;

import java.time.Year;

/**
 *
 * @author Bernhard Jenny, Faculty of Information Technology, Monash University,
 * Melbourne, Australia
 */
public class GallPetersProjection extends CylindricalEqualAreaProjection {
    public GallPetersProjection() {
        super(0, 0, Math.PI / 4);
    }
    
    @Override
    public String toString() {
        return "Gall-Peters";
    }
    
    @Override
    public String getAuthor() {
        return "James Gall";
    }
    
    @Override
    public Year getYear() {
        return Year.of(1855);
    }
    
    @Override
    public String getHistoryDescription() {
        return super.getHistoryDescription() + " Popularized by Arno Peters in the early 1970s.";
    }
}
