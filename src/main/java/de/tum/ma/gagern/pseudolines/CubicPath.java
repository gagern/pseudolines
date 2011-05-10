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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class CubicPath implements Shape {

    private static final int FIRST_POINT = 0;

    double[] coords;

    int length;

    boolean closed;

    CubicPath(int maxPoints) {
        coords = new double[maxPoints*6 + (FIRST_POINT - 4)];
        length = FIRST_POINT;
        closed = false;
    }

    void addPoint(PointOnLine point) {
        if (length%6 != FIRST_POINT)
            throw new IllegalStateException("Control expected not point");
        if (length == coords.length)
            setCapacity(2*length);
        assert !Double.isNaN(point.xPos);
        assert !Double.isNaN(point.yPos);
        assert !Double.isInfinite(point.xPos);
        assert !Double.isInfinite(point.yPos);
        coords[length] = point.xPos;
        coords[length + 1] = point.yPos;
        length += 2;
    }

    void addControl(HalfEdge he) {
        if (length%6 == FIRST_POINT)
            throw new IllegalStateException("Point expected not control");
        if (length == coords.length)
            setCapacity(2*length);
        assert !Double.isNaN(he.xCtrl);
        assert !Double.isNaN(he.yCtrl);
        assert !Double.isInfinite(he.xCtrl);
        assert !Double.isInfinite(he.yCtrl);
        coords[length] = he.xCtrl;
        coords[length + 1] = he.yCtrl;
        length += 2;
    }

    void setCapacity(int capacity) {
        double[] c = new double[capacity];
        System.arraycopy(coords, 0, c, 0, length);
        coords = c;
    }

    void compctify() {
        if (length != coords.length)
            setCapacity(length);
    }

    void close() {
        if (length%6 == FIRST_POINT) {
            if (length == FIRST_POINT)
                throw new IllegalStateException("Empty path may not be closed");
            coords[length] = coords[FIRST_POINT];
            coords[length + 1] = coords[FIRST_POINT + 1];
            length += 2;
        }
        if (length%6 != FIRST_POINT + 2) {
            throw new IllegalStateException("Path does not end on a point");
        }
        closed = true;
    }
 
    void interpolate(double fa, CubicPath a, double fb, CubicPath b) {
        if (a.length != b.length)
            throw new IllegalArgumentException("Paths have different length");
        if (a.closed != b.closed)
            throw new IllegalArgumentException("Paths differ in closedness");
        length = a.length;
        if (coords.length < length)
            coords = new double[length];
        for (int i = 0; i < length; ++i)
            coords[i] = fa*a.coords[i] + fb*b.coords[i];
        closed = a.closed;
    }

    double firstX() {
        return coords[FIRST_POINT];
    }

    double firstY() {
        return coords[FIRST_POINT + 1];
    }

    double lastX() {
        return coords[length - 2];
    }

    double lastY() {
        return coords[length - 2];
    }

    // Implementation of Shape

    public boolean contains(double x, double y) {
        return false;
    }

    public boolean contains(Point2D point) {
        return false;
    }

    public boolean contains(double x, double y, double w, double h) {
        return false;
    }

    public boolean contains(Rectangle2D rect) {
        return false;
    }

    public boolean intersects(double left, double top, double w, double h) {
        double right = left+w, bottom = top+h;
        boolean allLeft = true, allRight = true;
        boolean allTop = true, allBottom = true;
        for (int i = FIRST_POINT; i < length; i += 2) {
            double x = coords[i], y = coords[i + 1];
            allLeft &= (x < left);
            allRight &= (x > right);
            allTop &= (y < top);
            allBottom &= (y > bottom);
        }
        return !(allLeft || allRight || allTop || allBottom);
    }

    public boolean intersects(Rectangle2D rect) {
        return intersects(rect.getX(), rect.getY(),
                          rect.getWidth(), rect.getHeight());
    }

    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    public Rectangle2D getBounds2D() {
        if (length == FIRST_POINT)
            return new Rectangle();
        Rectangle2D.Double rect = new Rectangle2D.Double
            (coords[FIRST_POINT], coords[FIRST_POINT + 1], 0., 0.);
        for (int i = FIRST_POINT + 2; i < length; i += 2)
            rect.add(coords[i], coords[i + 1]);
        return rect;
    }

    public PathIterator getPathIterator(AffineTransform at) {
        if (length%6 != FIRST_POINT + 2)
            throw new IllegalStateException("Path not closed on a point.");
        return new CubicIterator(at);
    }

    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new FlatteningPathIterator(getPathIterator(at), flatness);
    }

    class CubicIterator extends AbstractPathIterator {

        private static final int FIRST_POS = FIRST_POINT - 4;

        int pos;

        CubicIterator(AffineTransform at) {
            super(at);
            pos = FIRST_POS;
        }

        protected int rawSegment(double[] c) {
            if (pos == FIRST_POS) {
                c[0] = coords[FIRST_POINT];
                c[1] = coords[FIRST_POINT + 1];
                return SEG_MOVETO;
            }
            if (pos == length) {
                return SEG_CLOSE;
            }
            System.arraycopy(coords, pos, c, 0, 6);
            return SEG_CUBICTO;
        }

        public boolean isDone() {
            if (closed)
                return pos > length;
            return pos >= length;
        }

        public void next() {
            pos += 6;
        }

    }

}
