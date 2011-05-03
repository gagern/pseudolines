package de.tum.ma.gagern.pseudolines;

abstract class EndPoint extends PointOnLine {

    PseudoLine pseudoline;

    PointOnLine neighbour;

    void addCrossing(PointOnLine prev, PseudoLine line, PointOnLine next) {
        if (prev == null) {
            if (next == null)
                throw new NullPointerException();
            else
                neighbour = next;
        }
        else {
            if (next == null)
                neighbour = prev;
            else
                throw new IllegalArgumentException("Not an endpoint");
        }
        pseudoline = line;
    }

}
