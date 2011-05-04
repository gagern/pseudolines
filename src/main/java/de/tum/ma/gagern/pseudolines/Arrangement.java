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
import java.util.List;

class Arrangement {

    int n;

    List<PseudoLine> pls;

    private int nextPolID;

    List<PointOnLine> pols;

    List<Cell> triangles;

    Arrangement(Chirotope chi, int circleLine) {
        n = chi.numElements();
        pols = new ArrayList<PointOnLine>(n*n);
        triangles = new ArrayList<Cell>(2*n);
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
        PseudoLine[] pls = new PseudoLine[n];
        for (int i = 0; i < n; ++i) {
            pls[i] = new PseudoLine();
        }
        Intersection[] intersections = new Intersection[n*(n-1)/2];
        PointOnLine[] ptsOnCurLine = new PointOnLine[n];
        for (int i = 1; i < n; ++i) {
            PseudoLine plsi = pls[i];
            double angle = Math.PI*(i - 1)/(n - 1);
            double x = Math.cos(angle), y = Math.sin(angle);
            RimPoint start = new RimPoint(x, y);
            RimPoint end = new RimPoint(-x, -y);
            addPOL(start);
            addPOL(end);
            pls[i].setEndPoints(start, end);
            lseq = LineSequenceElement.getLineSequence(chi, i);
            assert lseq[0].length == 1 && lseq[0][0].line == 0;
            ptsOnCurLine[0] = start;
            ptsOnCurLine[lseq.length] = end;
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
                if (intersections[index] == null) {
                    intersections[index] = intersection =
                        new Intersection(lseq[j].length + 1);
                    addPOL(intersection);
                }
                ptsOnCurLine[j] = intersection;
            }
            for (int j = 1; j < lseq.length; ++j) {
                ptsOnCurLine[j].addCrossing
                    (ptsOnCurLine[j - 1], plsi, ptsOnCurLine[j + 1]);
            }
            ptsOnCurLine[0].addCrossing
                (ptsOnCurLine[1], plsi, null);
            ptsOnCurLine[lseq.length].addCrossing
                (ptsOnCurLine[lseq.length - 1], plsi, null);
        }
        PseudoLine prevLine = pls[(2*n - 2)%n], curLine = pls[n - 1];
        EndPoint prevStart = prevLine.end, prevEnd = prevLine.start;
        EndPoint curStart = curLine.end, curEnd = curLine.start;
        for (int i = 1; i < n; ++i) {
            PseudoLine nextLine = pls[i];
            EndPoint nextStart = nextLine.start, nextEnd = nextLine.end;
            curStart.addCrossing(prevStart, pls[0], nextStart);
            curEnd.addCrossing(prevEnd, pls[0], nextEnd);
            prevStart = curStart;
            curStart = nextStart;
            prevEnd = curEnd;
            curEnd = nextEnd;
        }
        this.pls = new ArrayList<PseudoLine>(Arrays.asList(pls));
        findTriangles();
    }

    void addPOL(PointOnLine pol) {
        pol.id = nextPolID++;
        pols.add(pol);
    }

    void findTriangles() {
        triangles.clear();
        for (PointOnLine a: pols) {
            int na = a.numNeighbours();
            PointOnLine b = a.neighbour(na - 1);
            for (int i = 0; i < na; ++i) {
                PointOnLine c = a.neighbour(i);
                if (b != null && c != null && a.id < b.id && a.id < c.id
                    && b.hasNeighbour(c)) {
                    triangles.add(new Cell(a, b, c));
                }
                b = c;
            }
        }
    }

    Snapshot snapshot(Layout layout) throws LinearSystemException {
        layout.arrangement = this;
        layout.performLayout(this);
        Snapshot snapshot = new Snapshot(this);
        snapshot.circleLine = pls.get(0);
        for (int i = 1; i < n; ++i) {
            PseudoLine pl = pls.get(i);
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

}
