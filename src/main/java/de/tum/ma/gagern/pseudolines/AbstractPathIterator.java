package de.tum.ma.gagern.pseudolines;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

abstract class AbstractPathIterator implements PathIterator {

    private final AffineTransform at;

    private final double[] buf;

    AbstractPathIterator(AffineTransform at) {
        this.at = (at == null || at.isIdentity()) ? null : at;
        buf = new double[6];
    }

    protected abstract int rawSegment(double[] coords);

    public int currentSegment(double[] coords) {
        int kind = rawSegment(coords);
        if (at != null)
            at.transform(coords, 0, coords, 0, numPoints(kind));
        return kind;
    }

    public int currentSegment(float[] coords) {
        int kind = currentSegment(buf);
        int n = 2*numPoints(kind);
        for (int i = 0; i < n; ++i)
            coords[i] = (float)buf[i];
        return kind;
    }

    private static int numPoints(int kind) {
        switch (kind) {
        case SEG_CLOSE:
            return 0;
        case SEG_MOVETO:
        case SEG_LINETO:
            return 1;
        case SEG_QUADTO:
            return 2;
        case SEG_CUBICTO:
            return 3;
        default:
            throw new IllegalArgumentException("Unknown segment type");
        }
    }

    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

}
