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
