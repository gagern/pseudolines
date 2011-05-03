package de.tum.ma.gagern.pseudolines;

import java.util.Arrays;
import java.util.Comparator;

class LineSequenceElement {

    int sign;

    int line;

    public LineSequenceElement(boolean negative, int line) {
        this.sign = negative ? (byte)-1 : (byte)1;
        this.line = line;
    }

    @Override public String toString() {
        return (new StringBuilder(3))
            .append(sign < 0 ? '-' : '+')
            .append(line)
            .toString();
    }

    static LineSequenceElement[][] getLineSequence(Chirotope chi, int line) {
        int n = chi.numElements();
        LineSequenceElement[] linear = new LineSequenceElement[n - 1];
        int i = 0;
        if (line == 0)
            i = 1;
        linear[0] = new LineSequenceElement(false, i);
        for (int pos = 1, j = i; pos < n - 1; ++pos) {
            if (++j == line)
                ++j;
            linear[pos] = new LineSequenceElement(chi.chi(line, i, j) < 0, j);
        }
        Arrays.sort(linear, 1, n - 1, new Sorter(chi, line));
        int nDistinct = n - 1;
        boolean[] same = new boolean[n - 2];
        for (i = 0; i < n - 2; ++i) {
            if (chi.chi(line, linear[i].line, linear[i + 1].line) == 0) {
                same[i] = true;
                --nDistinct;
            }
        }
        LineSequenceElement[][] result = new LineSequenceElement[nDistinct][];
        i = 0;
        for (int pos = 0; pos < nDistinct; ++pos) {
            int nSame = 1;
            for (int j = i; j < n - 2 && same[j]; ++j)
                ++nSame;
            LineSequenceElement[] merged = new LineSequenceElement[nSame];
            for (int j = 0; j < nSame; ++j)
                merged[j] = linear[i++];
            result[pos] = merged;
        }
        assert i == n - 1;
        return result;
    }

    private static class Sorter implements Comparator<LineSequenceElement> {

        private final Chirotope chi;

        private final int a;

        public Sorter(Chirotope chi, int a) {
            this.chi = chi;
            this.a = a;
        }

        public int compare(LineSequenceElement eb, LineSequenceElement ec) {
            int sgn = chi.chi(a, eb.line, ec.line)*eb.sign*ec.sign;
            if (sgn != 0)
                return -sgn;
            return eb.line - ec.line;
        }

    }

}
