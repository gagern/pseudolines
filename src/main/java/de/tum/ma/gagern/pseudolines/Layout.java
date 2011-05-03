package de.tum.ma.gagern.pseudolines;

abstract class Layout {

    Arrangement arrangement;

    final LinearSystem ls;

    public Layout() {
        ls = new LinearSystem();
    }

    void addEquations(PointOnLine pt) {
        if (pt instanceof Intersection)
            addEquations((Intersection)pt);
    }

    abstract void addEquations(Intersection pt);

    abstract PseudoLinePath getPath(PseudoLine pl);

    void performLayout(Arrangement arr) throws LinearSystemException {
        arrangement = arr;
        ls.clear();
        for (PointOnLine pol: arr.pols)
            addEquations(pol);
        ls.solve();
    }

}
