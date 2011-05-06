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

    PseudoLinePath getPath(PseudoLine pl) {
        PseudoLinePath pth = new PseudoLinePath(arrangement.n);
        HalfEdge out = pl.start, in;
        do {
            pth.addSymmetric(out.center.xPos, out.center.yPos,
                             out.xCtrl - out.center.xPos,
                             out.yCtrl - out.center.yPos);
            in = out.connection;
            out = in.opposite;
        } while (in != pl.end);
        pth.addSymmetric(in.center.xPos, in.center.yPos,
                         in.center.xPos - in.xCtrl,
                         in.center.yPos - in.yCtrl);
        return pth;
    }

    PseudoLinePath getEdge(HalfEdge edge) {
        PseudoLinePath pth = new PseudoLinePath(2);
        pth.addSymmetric(edge.center.xPos, edge.center.yPos,
                         edge.xCtrl - edge.center.xPos,
                         edge.yCtrl - edge.center.yPos);
        edge = edge.connection;
        pth.addSymmetric(edge.center.xPos, edge.center.yPos,
                         edge.center.xPos - edge.xCtrl,
                         edge.center.yPos - edge.yCtrl);
        return pth;
    }

    CellShape getShape(Cell cell) {
        int n = cell.size();
        CellShape cs = new CellShape(cell);
        int i = 0;
        for (HalfEdge e: cell.edges())
            cs.edges[i++] = getEdge(e);
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
        // ignore this for now.
    }

    abstract void layoutInner(PseudoLine pl);

    Point2D rimDirection(RimPoint from, RimPoint to) {
	double dir = Math.signum(from.xPos*to.yPos - from.yPos*to.xPos);
        double mx = (from.xPos + to.xPos)/2., my = (from.yPos + to.yPos)/2.;
        double dx = (from.xPos - to.xPos)/2., dy = (from.yPos - to.yPos)/2.;
        double d = Math.hypot(dx, dy), h = Math.hypot(mx, my);
        double k = (4.*d)/(3.*(1.+h));
        return new Point2D.Double(-k*dir*from.yPos, k*dir*from.xPos);
    }

    PseudoLinePath rimArc(RimPoint from, RimPoint to) {
        double mx = (from.xPos + to.xPos)/2., my = (from.yPos + to.yPos)/2.;
        double dx = (from.xPos - to.xPos)/2., dy = (from.yPos - to.yPos)/2.;
        double d = Math.hypot(dx, dy), h = Math.hypot(mx, my);
        double k = (4.*d)/(3.*(1.+h));
        PseudoLinePath pth = new PseudoLinePath(2);
        pth.addSymmetric(from.xPos, from.yPos, -k*from.yPos, k*from.xPos);
        pth.addSymmetric(to.xPos, to.yPos, -k*to.yPos, k*to.xPos);
        return pth;
    }

}
