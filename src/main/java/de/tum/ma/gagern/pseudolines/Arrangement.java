package de.tum.ma.gagern.pseudolines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Arrangement {

    int n;

    List<PseudoLine> pls;

    List<PointOnLine> pols;

    Arrangement(Chirotope chi, int circleLine) {
        n = chi.numElements();
        this.pols = new ArrayList<PointOnLine>(n*n);
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
        PointOnLine[] pols = new PointOnLine[n];
        for (int i = 1; i < n; ++i) {
            PseudoLine plsi = pls[i];
            double angle = Math.PI*(i - 1)/(n - 1);
            double x = Math.cos(angle), y = Math.sin(angle);
            RimPoint start = new RimPoint(x, y);
            RimPoint end = new RimPoint(-x, -y);
            this.pols.add(start);
            this.pols.add(end);
            pls[i].setEndPoints(start, end);
            lseq = LineSequenceElement.getLineSequence(chi, i);
            assert lseq[0].length == 1 && lseq[0][0].line == 0;
            pols[0] = start;
            pols[lseq.length] = end;
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
                    this.pols.add(intersection);
                }
                pols[j] = intersection;
            }
            for (int j = 1; j < lseq.length; ++j) {
                pols[j].addCrossing(pols[j - 1], plsi, pols[j + 1]);
            }
            pols[0].addCrossing(pols[1], plsi, null);
            pols[lseq.length].addCrossing(pols[lseq.length - 1], plsi, null);
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
        return snapshot;
    }

}
