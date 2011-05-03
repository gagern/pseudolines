package de.tum.ma.gagern.pseudolines;

abstract class Layout {

    final LinearSystem ls;

    public Layout() {
        ls = new LinearSystem();
    }

    void addEquations(PointOnLine pt) {
        if (pt instanceof Intersection)
            addEquations((Intersection)pt);
    }

    abstract void addEquations(Intersection pt);

}
