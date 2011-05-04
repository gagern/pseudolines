package de.tum.ma.gagern.pseudolines;

abstract class BezierLayout extends Layout {

    LinVec2 control(PointOnLine from, PointOnLine to) {
        return direction(from, to).add(from.getLocation());
    }

    LinVec2 direction(PointOnLine from, PointOnLine to) {
        if (from instanceof Intersection)
            return direction((Intersection)from, to);
        if (from instanceof RimPoint)
            return direction((RimPoint)from, to);
        return new LinVec2(LinComb.constant(0), LinComb.constant(0));
    }

    abstract LinVec2 direction(Intersection from, PointOnLine to);

    abstract LinVec2 direction(RimPoint from, PointOnLine to);

    PseudoLinePath getPath(PseudoLine pl) {
        PseudoLinePath pth = new PseudoLinePath(arrangement.n);
        PointOnLine prev = null, cur = pl.start;
        while (cur != null) {
            PointOnLine next = cur.opposite(prev);
            LinVec2 loc = cur.getLocation();
            double x = loc.getXTerm().getValue();
            double y = loc.getYTerm().getValue();
            LinVec2 dir;
            if (next != null)
                dir = direction(cur, next);
            else
                dir = direction(cur, prev).scale(-1);
            double dx = dir.getXTerm().getValue();
            double dy = dir.getYTerm().getValue();
            pth.addSymmetric(x, y, dx, dy);
            prev = cur;
            cur = next;
        }
        return pth;
    }

    PseudoLinePath getEdge(PointOnLine from, PointOnLine to) {
        PseudoLinePath pth = new PseudoLinePath(2);
        LinVec2 loc, dir;
        loc = from.getLocation();
        dir = direction(from, to);
        pth.addSymmetric(loc.getXTerm().getValue(),
                         loc.getYTerm().getValue(),
                         dir.getXTerm().getValue(),
                         dir.getYTerm().getValue());
        loc = to.getLocation();
        dir = direction(to, from);
        pth.addSymmetric(loc.getXTerm().getValue(),
                         loc.getYTerm().getValue(),
                         -dir.getXTerm().getValue(),
                         -dir.getYTerm().getValue());
        return pth;
    }

}
