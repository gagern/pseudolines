package de.tum.ma.gagern.pseudolines;

class RegularBezierLayout extends BezierLayout {

    LinVec2 direction(Intersection from, PointOnLine to) {
        return from.dir.rot(from.neighbourIndex(to), from.numNeighbours());
    }

    LinVec2 direction(RimPoint from, PointOnLine to) {
        int idx = from.neighbourIndex(to);
        /* a is the angle of the control point direction, relative to
         * the tangent to the circle.
         */
        double a = Math.PI*2.*(idx + 1)/from.numNeighbours();
        double s = Math.sin(a), c = Math.cos(a);
        /* At the rim point, there is a local coordinate system with
         * the counterclockwise tangent unit vector (-y, x) as first
         * unit vector, and the vector (-x, -y) towards the center as
         * the second unit vector. It is with respect to that
         * coordinate system that we interpret the angle above to get
         * our absolute direction vector for the control point.
         */
        double x = -c*from.y -s*from.x;
        double y =  c*from.x -s*from.y;
        if (to instanceof RimPoint) {
            // avoid infinite recursion, as we otherwise call control
            return new LinVec2(LinComb.constant(x/2.),
                               LinComb.constant(y/2.));
        }
        LinVec2 cp = control(to, from).sub(from.getLocation());
        LinComb len = cp.getXTerm().mul(x);
        len = len.add(cp.getYTerm().mul(y));
        /* Now len is the scalar product, i.e. the distance of the
         * next control point, projected onto the control direction
         * vector. We want the center between that projection and the
         * rim.
         */
        len = len.div(2);
        return new LinVec2(len.mul(x), len.mul(y));
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
