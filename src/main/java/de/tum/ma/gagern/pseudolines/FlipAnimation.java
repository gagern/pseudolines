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

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class FlipAnimation extends Animation {

    Cell oldTriangle;

    Cell newTriangle;

    IntersectionCell oldCell;

    IntersectionCell newCell;

    Collection<IntersectionCell> removedTriangles;

    Collection<AnimationCell> commonTriangles;

    Collection<IntersectionCell> addedTriangles;

    CubicPath cp;

    FlipAnimation(Cell triangle) {
        if (triangle.size() != 3)
            throw new IllegalArgumentException("Not a triangle, cannot flip");
        oldTriangle = triangle;
    }

    String getOperationName() {
        return "Flip";
    }

    void performChange() {
        // We must save the list of edges, as we will modify the
        // structure which would cause a concurrent modification.
        HalfEdge[] edges = oldTriangle.edges().toArray(new HalfEdge[3]);
        for (HalfEdge he: edges) {
            PointOnLine corner = he.center;
            if (corner.numLines != 2 ||
                corner instanceof Intersection == false) {
                removedTriangles = Collections.emptyList();
                addedTriangles = Collections.emptyList();
                commonTriangles =
                    new ArrayList<AnimationCell>(arr.triangles.size());
                for (Cell triangle: arr.triangles) {
                    AnimationCell ac = new AnimationCell(triangle);
                    ac.before = before.getTriangle(triangle);
                    assert ac.before != null;
                    ac.after = ac.before;
                    commonTriangles.add(ac);
                }
                return; // cannot flip that yet, simply ignore it
            }
        }
        oldCell = new IntersectionCell(oldTriangle, before);
        removedTriangles = new ArrayList<IntersectionCell>(3);
        arr.triangles.remove(oldTriangle);
        for (HalfEdge he: edges) {
            if (Cell.isTriangle(he.opposite)) {
                Cell cell = new Cell(he.opposite);
                if (arr.triangles.remove(cell)) {
                    removedTriangles.add(new IntersectionCell(cell, before));
                }
            }
        }
        commonTriangles = new ArrayList<AnimationCell>(arr.triangles.size());
        for (Cell triangle: arr.triangles) {
            AnimationCell ac = new AnimationCell(triangle);
            ac.before = before.getTriangle(triangle);
            assert ac.before != null;
            commonTriangles.add(ac);
        }
        HalfEdge newStart = edges[0].opposite;

        // Now do the actual flip in the arrangement
        for (HalfEdge c: edges) {
            /* We have half edges ab, cd and ef with points (bc) and
             * (de) and want to reconnect them as ad, eb, cf
             * preserving the same two intersection points. Remember
             * that a and f might be null as well. One of the b edges
             * will be the start for the new triangle.
             */
            HalfEdge b = c.opposite, a = b.connection;
            HalfEdge d = c.connection, e = d.opposite, f = e.connection;
            b.connect(e, undo);
            c.connect(f, undo);
            d.connect(a, undo);
        }

        newTriangle = new Cell(newStart);
        assert newTriangle.size() == 3: newTriangle.size() + " should be 3";
        arr.triangles.add(newTriangle);
        newCell = new IntersectionCell(newTriangle, before);
        addedTriangles = new ArrayList<IntersectionCell>(3);
        for (HalfEdge he: newTriangle.edges()) {
            if (Cell.isTriangle(he.opposite)) {
                Cell cell = new Cell(he.opposite);
                boolean added = arr.triangles.add(cell);
                assert added;
                addedTriangles.add(new IntersectionCell(cell, before));
            }
        }
    }

    void postProcess() {
        cp = new CubicPath(arr.n);
        for (AnimationCell ac: commonTriangles) {
            ac.after = after.getTriangle(ac.cell);
            assert ac.after != null;
        }
        for (IntersectionCell ic: removedTriangles)
            ic.setAfter(after);
        for (IntersectionCell ic: addedTriangles)
            ic.setAfter(after);
        oldCell.setAfter(after);
        newCell.setAfter(after);
    }

    void renderAnimated() {
        Path2D p2d = new Path2D.Double();
        Area area = new Area();
        renderer.setAlpha(p0);
        for (IntersectionCell ic: removedTriangles) {
            renderer.renderCell(ic.cell,
                                ic.interpolate(p0, p1, cp, p2d, area));
        }
        renderer.setAlpha(p1);
        for (IntersectionCell ic: addedTriangles) {
            renderer.renderCell(ic.cell,
                                ic.interpolate(p0, p1, cp, p2d, area));
        }
        renderer.setAlpha(1.);
        renderer.renderCell(oldCell.cell,
                            oldCell.interpolate(p0, p1, cp, p2d, area));
        renderer.renderCell(newCell.cell,
                            newCell.interpolate(p0, p1, cp, p2d, area));
        for (AnimationCell ac: commonTriangles) {
            cp.interpolate(p0, ac.before.path, p1, ac.after.path);
            renderer.renderCell(ac.cell, cp);
        }
        for (int i = 0; i < before.size(); ++i) {
            cp.interpolate(p0, before.getLine(i), p1, after.getLine(i));
            renderer.renderLine(before.getLine(i).pseudoLine, cp);
        }
    }

}
