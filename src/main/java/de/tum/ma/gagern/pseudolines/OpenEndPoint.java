package de.tum.ma.gagern.pseudolines;

import java.util.Collection;
import java.util.Iterator;

class OpenEndPoint extends EndPoint {

    Collection<PointOnLine> surrounding;

    public OpenEndPoint() {
        super(1);
    }

    LinVec2 getLocation() {
        Iterator<PointOnLine> iter = surrounding.iterator();
        LinVec2 loc = iter.next().getLocation();
        while (iter.hasNext())
            loc = loc.add(iter.next().getLocation());
        return loc.scale(1./surrounding.size());
    }

}
