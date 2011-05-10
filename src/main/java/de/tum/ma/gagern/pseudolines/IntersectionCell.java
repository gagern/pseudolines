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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

class IntersectionCell {

    Cell cell;

    List<AnimationHalfPlane> parts;

    IntersectionCell(Cell cell, Snapshot before) {
        this.cell = cell;
        parts = new ArrayList<AnimationHalfPlane>(cell.size() + 1);
        for (HalfEdge he: cell.edges())
            intersect(he, before);
    }

    void setAfter(Snapshot after) {
        for (AnimationHalfPlane hp: parts) {
            hp.after = after.getLine(hp.before.pseudoLine);
            assert hp.after != null;
        }
    }

    void intersect(HalfEdge he, Snapshot before) {
        AnimationHalfPlane hp = new AnimationHalfPlane();
        hp.before = before.getLine(he.pseudoLine);
        assert hp.before != null;
        hp.dir = he.directionSign;
        parts.add(hp);
    }

    Shape interpolate(double p0, double p1, CubicPath cp, Path2D p2d, Area a) {
        a.reset();
        a.add(new Area(parts.get(0).interpolate(p0, p1, cp, p2d)));
        for (int i = 1; i < parts.size(); ++i)
            a.intersect(new Area(parts.get(i).interpolate(p0, p1, cp, p2d)));
        return a;
    }

}
