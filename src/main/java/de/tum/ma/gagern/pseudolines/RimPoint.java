package de.tum.ma.gagern.pseudolines;

class RimPoint extends EndPoint {

    RimPoint prev, next;

    double x, y;

    RimPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    LinVec2 getLocation() {
        return new LinVec2(LinComb.constant(x), LinComb.constant(y));
    }

    LinVec2 getControlTowards(PointOnLine neighbour) {
        if (neighbour == this.neighbour) {
            if (neighbour instanceof RimPoint) {
                // avoid infinite recursion
                return new LinVec2(LinComb.constant(x/2),
                                   LinComb.constant(y/2));
            }
            LinVec2 cp = neighbour.getControlTowards(this);
            LinComb len = cp.getXTerm().mul(x);
            len = len.add(cp.getYTerm().mul(y));
            // Now len is the scalar product, i.e. the length of the
            // next control point, projected onto the spoke. We want
            // the center between that projection and the rim.
            len = len.add(LinComb.constant(1)).div(2);
            return new LinVec2(len.mul(x), len.mul(y));
        }
        throw notANeighbour();
    }

}
