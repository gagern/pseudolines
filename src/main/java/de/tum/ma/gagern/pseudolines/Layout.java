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
import java.util.Iterator;

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

    void addEquations(Intersection pt) {
        int n = pt.size();
        assert n > 0;
        LinVec2 eqPos = pt.pos.scale(-n);
        for (HalfEdge he: pt) {
            eqPos = eqPos.add(he.connection.center.getLocation());
        }
        ls.eqZero(eqPos);
    }

    PseudoLinePath getPath(PseudoLine pl) {
        PseudoLinePath pth = new PseudoLinePath(arrangement.n);
        pth.pseudoLine = pl;
        pth.addPoint(pl.start.center);
        Iterator<HalfEdge> iter = pl.allHalfEdges().iterator();
        while (iter.hasNext()) {
            HalfEdge he = iter.next();
            pth.addControl(he);
            he = iter.next();
            pth.addControl(he);
            pth.addPoint(he.center);
        }
        if (pl.start.center == pl.end.center)
            pth.close();
        return pth;
    }

    CellShape getShape(Cell cell) {
        CubicPath pth = new CubicPath(cell.size() + 1);
        for (HalfEdge he: cell.edges()) {
            pth.addPoint(he.center);
            pth.addControl(he);
            pth.addControl(he.connection);
        }
        pth.close();
        CellShape cs = new CellShape(cell);
        cs.path = pth;
        cell.shape = cs.getShape();
        return cs;
    }

    void performLayout(Arrangement arr) throws LinearSystemException {
        arrangement = arr;
        ls.clear();
        for (PointOnLine point: arr.points)
            addEquations(point);
        ls.solve();
        for (PointOnLine point: arr.points) {
            LinVec2 loc = point.getLocation();
            point.xPos = loc.getXTerm().getValue();
            point.yPos = loc.getYTerm().getValue();
            assert !Double.isNaN(point.xPos);
            assert !Double.isNaN(point.yPos);
            assert !Double.isInfinite(point.xPos);
            assert !Double.isInfinite(point.yPos);
        }
        for (PseudoLine pl: arr.lines)
            layoutLine(pl);
    }

    void layoutLine(PseudoLine pl) {
        if (pl == arrangement.rimLine())
            layoutRim(pl);
        else
            layoutInner(pl);
    }

    void layoutRim(PseudoLine pl) {
        for (HalfEdge he: pl.allHalfEdges()) {
            Point2D dir = rimDirection(he.center, he.connection.center);
            he.xCtrl = he.center.xPos + dir.getX();
            he.yCtrl = he.center.yPos + dir.getY();
        }
    }

    abstract void layoutInner(PseudoLine pl);

    Point2D rimDirection(PointOnLine from, PointOnLine to) {
	double dir = Math.signum(from.xPos*to.yPos - from.yPos*to.xPos);
        double mx = (from.xPos + to.xPos)/2., my = (from.yPos + to.yPos)/2.;
        double dx = (from.xPos - to.xPos)/2., dy = (from.yPos - to.yPos)/2.;
        double d = Math.hypot(dx, dy), h = Math.hypot(mx, my);
        double k = (4.*d)/(3.*(1.+h));
        return new Point2D.Double(-k*dir*from.yPos, k*dir*from.xPos);
    }

}
