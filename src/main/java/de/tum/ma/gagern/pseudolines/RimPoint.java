package de.tum.ma.gagern.pseudolines;

class RimPoint extends EndPoint {

    double x, y;

    RimPoint(double x, double y) {
        super(2);
        this.x = x;
        this.y = y;
    }

    LinVec2 getLocation() {
        return new LinVec2(LinComb.constant(x), LinComb.constant(y));
    }

}
