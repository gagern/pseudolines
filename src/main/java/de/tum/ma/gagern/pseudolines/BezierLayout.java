package de.tum.ma.gagern.pseudolines;

abstract class BezierLayout extends Layout {

    LinVec2 control(PointOnLine from, PointOnLine to) {
        if (from instanceof Intersection)
            return control((Intersection)from, to);
        if (from instanceof RimPoint)
            return control((RimPoint)from, to);
        return from.getLocation();
    }

    abstract LinVec2 control(Intersection from, PointOnLine to);

    LinVec2 control(RimPoint from, PointOnLine to) {
        if (to instanceof RimPoint) {
            // avoid infinite recursion
            return new LinVec2(LinComb.constant(from.x/2),
                               LinComb.constant(from.y/2));
        }
        LinVec2 cp = control(to, from);
        LinComb len = cp.getXTerm().mul(from.x);
        len = len.add(cp.getYTerm().mul(from.y));
        // Now len is the scalar product, i.e. the length of the next
        // control point, projected onto the spoke. We want the center
        // between that projection and the rim.
        len = len.add(LinComb.constant(1)).div(2);
        return new LinVec2(len.mul(from.x), len.mul(from.y));
    }

}
