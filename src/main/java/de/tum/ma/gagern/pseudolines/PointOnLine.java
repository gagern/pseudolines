package de.tum.ma.gagern.pseudolines;

abstract class PointOnLine {

    IllegalArgumentException notANeighbour() {
        return new IllegalArgumentException("Argument is not a neighbour");
    }

    abstract LinVec2 getLocation();

    abstract LinVec2 getControlTowards(PointOnLine neighbour);

    abstract void addCrossing(PointOnLine prev, PseudoLine line,
                              PointOnLine next);

    void addEquations(LinearSystem ls) {
        // Do nothing by default
    }

}
