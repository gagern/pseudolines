/*
 * pseudolines - Display pyseudoline arrangements
 * Copyright (C) 2011 Martin von Gagern
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
