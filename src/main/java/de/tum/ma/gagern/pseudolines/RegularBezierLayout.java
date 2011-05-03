package de.tum.ma.gagern.pseudolines;

class RegularBezierLayout extends BezierLayout {

    LinVec2 control(Intersection from, PointOnLine to) {
        return from.dir
            .rot(from.neighbourIndex(to), from.numNeighbours())
            .add(from.pos);
    }

    void addEquations(Intersection pt) {
        int n = pt.numNeighbours();
        LinVec2 eqPos = pt.pos.scale(-n);
        LinVec2 eqDir = pt.dir.scale(-2*n);
        for (int i = 0; i < n; ++i) {
            LinVec2 nc = control(pt.neighbour(i), pt);
            eqPos = eqPos.add(nc);
            eqDir = eqDir.add(nc.sub(pt.pos).rot(-i, n));
        }
        ls.eqZero(eqPos);
        ls.eqZero(eqDir);
    }

}
