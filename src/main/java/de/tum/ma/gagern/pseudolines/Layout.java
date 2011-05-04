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

import java.awt.geom.Point2D;

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

    abstract PseudoLinePath getEdge(PointOnLine from, PointOnLine to);

    CellShape getShape(Cell cell) {
        int n = cell.corners.size();
        CellShape cs = new CellShape(cell);
        PointOnLine prev = cell.corners.get(n - 1);
        for (int i = 0; i < n; ++i) {
            PointOnLine next = cell.corners.get(i);
            cs.edges[i] = getEdge(prev, next);
            prev = next;
        }
        cell.shape = cs.getShape();
        return cs;
    }

    void performLayout(Arrangement arr) throws LinearSystemException {
        arrangement = arr;
        ls.clear();
        for (PointOnLine pol: arr.pols)
            addEquations(pol);
        ls.solve();
    }

    Point2D rimDirection(RimPoint from, RimPoint to) {
	double dir = Math.signum(from.x*to.y - from.y*to.x);
        double mx = (from.x + to.x)/2., my = (from.y + to.y)/2.;
        double dx = (from.x - to.x)/2., dy = (from.y - to.y)/2.;
        double d = Math.hypot(dx, dy), h = Math.hypot(mx, my);
        double k = (4.*d)/(3.*(1.+h));
        return new Point2D.Double(-k*dir*from.y, k*dir*from.x);
    }

    PseudoLinePath rimArc(RimPoint from, RimPoint to) {
        double mx = (from.x + to.x)/2., my = (from.y + to.y)/2.;
        double dx = (from.x - to.x)/2., dy = (from.y - to.y)/2.;
        double d = Math.hypot(dx, dy), h = Math.hypot(mx, my);
        double k = (4.*d)/(3.*(1.+h));
        PseudoLinePath pth = new PseudoLinePath(2);
        pth.addSymmetric(from.x, from.y, -k*from.y, k*from.x);
        pth.addSymmetric(to.x, to.y, -k*to.y, k*to.x);
        return pth;
    }

}
