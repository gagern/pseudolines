package de.tum.ma.gagern.pseudolines;

class Arrangement {

    int n;

    PseudoLine[] pls;

    Arrangement(Chirotope chi, int circleLine) {
        n = chi.numElements();
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
        pls = new PseudoLine[n];
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
            pls[i].setEndPoints(start, end);
            lseq = LineSequenceElement.getLineSequence(chi, i);
            assert lseq[0].length == 1 && lseq[0][0].line == 0;
            pols[0] = start;
            pols[lseq.length] = end;
            for (int j = 1; j < lseq.length; ++j) {
                int min = i, max = i;
                for (int k = 0; k < lseq[j].length; ++k) {
                    min = Math.min(min, lseq[j][k].line);
                    max = Math.max(min, lseq[j][k].line);
                }
                // Now (min, max) is a unique key identifying the
                // given point of intersection. We turn it into an
                // index.
                int index = max*(max - 1)/2 + min;
                Intersection intersection = intersections[index];
                if (intersections[index] == null) {
                    intersections[index] = intersection =
                        new Intersection(lseq[j].length + 1);
                }
                pols[j] = intersection;
            }
            for (int j = 1; j < lseq.length; ++j) {
                pols[j].addCrossing(pols[j - 1], plsi, pols[j + 1]);
            }
            pols[0].addCrossing(null, plsi, pols[1]);
            pols[lseq.length].addCrossing(pols[lseq.length - 1], plsi, null);
        }
    }

}
