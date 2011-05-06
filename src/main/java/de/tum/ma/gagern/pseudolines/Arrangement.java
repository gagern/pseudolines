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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Arrangement {

    int n;

    List<PseudoLine> lines;

    private int nextPointID;

    List<PointOnLine> points;

    Set<Cell> triangles;

    Arrangement(Chirotope chi, int circleLine) {
        n = chi.numElements();
        if (n < 3)
            throw new IllegalArgumentException("Chirotope too small");
        points = new ArrayList<PointOnLine>(n*n);
        triangles = new HashSet<Cell>(2*n);

        // reorient so we have the rim line as index 0, and other
        // lines numbered sequentially along the rim line.
        LineSequenceElement[][] lseq =
            LineSequenceElement.getLineSequence(chi, circleLine);
        int[] relabel = new int[n], reorient = new int[n];
        relabel[0] = circleLine;
        reorient[0] = 1;
        for (int i = 1; i < n; ++i) {
            if (lseq[i - 1].length != 1)
                throw new UnsupportedOperationException
                    ("Non-uniform circle line not supported (yet)");
            LineSequenceElement elt = lseq[i - 1][0];
            relabel[i] = elt.line;
            reorient[i] = elt.sign;
        }
        chi = new ReorientedChirotope(chi, relabel, reorient);

        // create our list of pseudo lines
        lines = new ArrayList<PseudoLine>(n);
        for (int i = 0; i < n; ++i) {
            PseudoLine line = new PseudoLine();
            lines.add(line);
        }

        PseudoLine rimLine = lines.get(0);
        RimPoint rimDummy = new RimPoint(0, 0);
        HalfEdge rimDummyStart = rimDummy.add(rimLine);
        HalfEdge rimDummyEnd = rimDummyStart.opposite;
        HalfEdge rimStartOut = rimDummyStart;
        HalfEdge rimEndOut = rimDummyEnd;
        Intersection[] intersections = new Intersection[n*(n-1)/2];

        // Process each non-rim line and connect its intersections
        for (int i = 1; i < n; ++i) {
            PseudoLine line = lines.get(i);

            // choose location of rim point
            double angle = Math.PI*(i - 1)/(n - 1);
            double x = Math.cos(angle), y = Math.sin(angle);
            RimPoint startPoint = new RimPoint(x, y);
            RimPoint endPoint = new RimPoint(-x, -y);
            addPoint(startPoint);
            addPoint(endPoint);

            // connect rim line
            HalfEdge rimStartIn = startPoint.add(rimLine);
            HalfEdge rimEndIn = endPoint.add(rimLine);
            rimStartIn.connect(rimStartOut);
            rimEndIn.connect(rimEndOut);
            rimStartOut = rimStartIn.opposite;
            rimEndOut = rimEndIn.opposite;

            // connect interiour pseudo line
            HalfEdge startEdge = startPoint.add(line).opposite;
            HalfEdge endEdge = endPoint.add(line).opposite;
            lseq = LineSequenceElement.getLineSequence(chi, i);
            assert lseq[0].length == 1 && lseq[0][0].line == 0;
            HalfEdge out = startEdge, in;
            for (int j = 1; j < lseq.length; ++j) {
                int min = i, max = i;
                assert lseq[j].length > 0;
                for (int k = 0; k < lseq[j].length; ++k) {
                    min = Math.min(min, lseq[j][k].line);
                    max = Math.max(max, lseq[j][k].line);
                }
                // Now (min, max) is a unique key identifying the
                // given point of intersection. We turn it into an
                // index.
                int index = max*(max - 1)/2 + min;
                Intersection intersection = intersections[index];
                if (intersection == null) {
                    intersection = new Intersection();
                    addPoint(intersection);
                    intersections[index] = intersection;
                }
                in = intersection.add(line);
                in.connect(out);
                out = in.opposite;
            }
            out.connect(endEdge);
            line.setEnds(startEdge, endEdge);
        }

        // now close the rim by connecting the two halves with one
        // another
        rimDummyStart.connection.connect(rimEndOut);
        rimDummyEnd.connection.connect(rimStartOut);
        HalfEdge rimEnd = rimEndOut.connection;
        HalfEdge rimStart = rimEnd.opposite;
        rimLine.setEnds(rimStart, rimEnd);

        // Done constructing the point graph, let's do other stuff
        assert assertInvariants();
        findTriangles();
    }

    boolean assertInvariants() {
        for (PointOnLine p: points)
            for (HalfEdge he: p)
                he.assertInvariants();
        return true;
    }

    void addPoint(PointOnLine pt) {
        pt.id = nextPointID++;
        points.add(pt);
    }

    PseudoLine rimLine() {
        return lines.get(0);
    }

    List<PseudoLine> innerLines() {
        return lines.subList(1, lines.size());
    }

    void findTriangles() {
        triangles.clear();
        for (PointOnLine p: points) {
            for (HalfEdge a: p) {
                // Edges of the triangle are ab, cd and ef, in ccw order
                HalfEdge b = a.connection;
                if (b == null)
                    continue;
                // HalfEdge c = b.prev;
                HalfEdge d = b.prev.connection; // = c.connection
                // HalfEdge f = a.next;
                HalfEdge e = a.next.connection; // = f.connection
                if (e == null || e.next != d)
                    continue;
                assert d.center == e.center;
                if (p.id > b.center.id || p.id > d.center.id)
                    continue;
                triangles.add(new Cell(a));
            }
        }
    }

    boolean isTriangle(HalfEdge a) {
        // Edges of the triangle are ab, cd and ef, in ccw order
        HalfEdge b = a.connection;
        if (b == null)
            return false;
        // HalfEdge c = b.prev;
        HalfEdge d = b.prev.connection; // = c.connection
        // HalfEdge f = a.next;
        HalfEdge e = a.next.connection; // = f.connection
        if (e == null || e.next != d)
            return false;
        assert d.center == e.center;
        return true;
    }

    Snapshot snapshot(Layout layout) throws LinearSystemException {
        layout.arrangement = this;
        layout.performLayout(this);
        Snapshot snapshot = new Snapshot(this);
        snapshot.circleLine = rimLine();
        for (int i = 1; i < n; ++i) {
            PseudoLine pl = lines.get(i);
            PseudoLinePath pth = layout.getPath(pl);
            pth.pseudoLine = pl;
            snapshot.paths.add(pth);
        }
        for (Cell triangle: triangles) {
            CellShape cs = layout.getShape(triangle);
            snapshot.triangles.add(cs);
        }
        return snapshot;
    }

    Cell flip(Cell triangle) {
        return flip(triangle, null, null);
    }

    Cell flip(Cell triangle,
              Collection<? super Cell> removedTriangles,
              Collection<? super Cell> addedTriangles) {
        if (triangle.size() != 3)
            throw new IllegalArgumentException("Not a triangle, cannot flip");
        // We must save the list of edges, as we will modify the
        // structure which would cause a concurrent modification.
        HalfEdge[] edges = triangle.edges().toArray(new HalfEdge[3]);
        for (HalfEdge he: edges) {
            PointOnLine corner = he.center;
            if (corner.numLines != 2 ||
                corner instanceof Intersection == false) {
                return triangle; // cannot flip that yet, simply ignore it
            }
        }
        triangles.remove(triangle);
        for (HalfEdge he: edges) {
            if (isTriangle(he.opposite)) {
                Cell cell = new Cell(he.opposite);
                if (triangles.remove(cell)) {
                    if (removedTriangles != null)
                        removedTriangles.add(cell);
                }
            }
        }
        HalfEdge newStart = edges[0].connection.opposite;
        for (HalfEdge c: edges) {
            /* We have half edges ab, cd and ef with points (bc) and
             * (de) and want to reconnect them as ad, eb, cf
             * preserving the same two intersection points. Remember
             * that a and f might be null as well. One of the e points
             * will be the start for the new triangle.
             */
            HalfEdge b = c.opposite, a = b.connection;
            HalfEdge d = c.connection, e = d.opposite, f = e.connection;
            b.connect(e);
            c.connect(f);
            d.connect(a);
        }
        if (true) { // update of modifications only doesn't work yet.
            findTriangles();
            return null;
        }
        triangle = new Cell(newStart);
        triangles.add(triangle);
        for (HalfEdge he: triangle.edges()) {
            if (isTriangle(he.opposite)) {
                Cell cell = new Cell(he.opposite);
                boolean added = triangles.add(cell);
                if (addedTriangles != null)
                    addedTriangles.add(cell);
                assert added;
            }
        }
        return triangle;
    }

}
